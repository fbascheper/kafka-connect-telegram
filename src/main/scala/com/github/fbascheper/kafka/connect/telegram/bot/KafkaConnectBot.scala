package com.github.fbascheper.kafka.connect.telegram.bot

import com.github.fbascheper.kafka.connect.telegram.{Logging, TelegramMessageSender}
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod

import scala.collection.JavaConverters._
import scala.util.Try


/**
  * Implementation of the Kafka Connect bot.
  *
  * @param botUsername username of the Telegram bot
  * @param apiKey      api key of the Telegram bot
  * @author Erik-Berndt Scheper
  * @since 17-09-2018
  *
  */
class KafkaConnectBot(botUsername: String, apiKey: String) extends TelegramMessageSender with Logging {

  val pollingBot = new KafkaConnectLongPollingBot(botUsername, apiKey)

  override def sendMessage[T <: PartialBotApiMethod[R], R <: java.io.Serializable](botMessage: BotMessage[T, R]): Try[Seq[Int]] = {
    Try(pollingBot.sendBotMessage(botMessage.getMessage()).asScala.map(_.toInt))
  }
}

