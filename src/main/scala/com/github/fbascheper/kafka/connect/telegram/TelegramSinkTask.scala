package com.github.fbascheper.kafka.connect.telegram

import java.util

import com.github.fbascheper.kafka.connect.telegram.bot.KafkaConnectBot
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.connect.sink.{SinkRecord, SinkTask}
import org.telegram.telegrambots.meta.generics.BotSession

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

/**
  * Sink used to send messages to a Telegram chat.
  *
  * @author Erik-Berndt Scheper
  * @since 17-09-2018
  *
  */
class TelegramSinkTask extends SinkTask with Logging {
  var botSession: Option[BotSession] = None
  var messageSender: Option[TelegramMessageSender] = None
  var chatId: Option[Long] = None
  var messageMapper: Option[TelegramMessageMapper] = None

  override def start(props: util.Map[String, String]): Unit = {
    import org.telegram.telegrambots.ApiContextInitializer
    import org.telegram.telegrambots.meta.TelegramBotsApi

    ApiContextInitializer.init()
    val botsApi = new TelegramBotsApi
    val kafkaConnectBot = initTask(props)

    botSession = Some(botsApi.registerBot(kafkaConnectBot.pollingBot))
  }

  def initTask(props: util.Map[String, String]): KafkaConnectBot = {
    val sinkConfig = new TelegramSinkConfig(props)

    val username = sinkConfig.getString(TelegramSinkConfig.TELEGRAM_BOT_USERNAME)
    val apiKey = sinkConfig.getPassword(TelegramSinkConfig.TELEGRAM_BOT_API_KEY).value
    val kafkaConnectBot = new KafkaConnectBot(username, apiKey)

    chatId = Some(sinkConfig.getPassword(TelegramSinkConfig.TELEGRAM_BOT_DESTINATION_CHAT_ID).value.toLong)
    messageSender = Some(kafkaConnectBot)
    messageMapper = Some(new TelegramMessageMapper(chatId.get))

    kafkaConnectBot
  }

  override def put(records: util.Collection[SinkRecord]): Unit =
    messageMapper match {
      case Some(mapper) =>
        records.asScala
          .map(record => mapper.map(record))
          .map(message => (message, messageSender match {
            case Some(sender) =>
              message match {
                case Success(msg) => sender.sendMessage(message.get)
                case Failure(err) =>
                  log.error("Could not map message", err)
                  Failure(new IllegalStateException("Could not map message due to previous errors", err))
              }
            case None => Failure(new IllegalStateException("Telegram message sender is not set"))
          }))
          .foreach {
            case (message, result) => result match {
              case Success(id) => log.info(s"Successfully sent message with assigned message-id $id and ${message.get.getText()} ")
              case Failure(err) => log.error(s"Could not send message due to error '${err.getMessage}'")
            }
          }
      case None =>
        log.error(s"Could not send message because message mapper is not set")
    }


  override def stop(): Unit = {
    botSession match {
      case Some(session) =>
        session.stop()
        log.info(s"Successfully stopped bot session")
      case None =>
        log.info(s"No bot sessions running")

    }
  }

  override def flush(map: util.Map[TopicPartition, OffsetAndMetadata]) = {
  }

  override def version(): String = ""
}
