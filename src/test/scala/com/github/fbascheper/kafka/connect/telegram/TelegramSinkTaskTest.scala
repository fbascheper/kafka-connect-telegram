package com.github.fbascheper.kafka.connect.telegram

import com.github.fbascheper.kafka.connect.telegram.bot.{BotMessage, KafkaConnectPhotoMessage, KafkaConnectTextMessage}
import com.github.fbascheper.kafka.connect.telegram.mapper.{TelegramMessageMapper, TelegramTestMessages}
import org.apache.kafka.connect.sink.SinkRecord
import org.scalamock.scalatest.MockFactory
import org.telegram.telegrambots.meta.api.methods.send.{SendMessage, SendPhoto}

import scala.collection.JavaConverters._
import scala.util.Success

/**
  * Test class for `TelegramSinkTask`.
  *
  * @author Erik-Berndt Scheper
  * @since 17-09-2018
  *
  */
class TelegramSinkTaskTest extends TestTelegramBase with MockFactory {
  val goodProps: Map[String, String] = getSinkConfig
  val messageSenderMock = mock[TelegramMessageSender]
  val messageMapperMock = mock[TelegramMessageMapper]

  test("testPut text message") {
    val sinkTask = new TelegramSinkTask()

    sinkTask.initTask(goodProps.asJava)
    sinkTask.messageSender = Some(messageSenderMock)
    sinkTask.messageMapper = Some(messageMapperMock)

    // sinkTask.start(goodProps.asJava)

    val textMessage = "This is a text message"
    val sinkRecord = new SinkRecord("topic", 5, null, null, null, textMessage, 123)

    val sendMessage = new SendMessage()
    sendMessage.setText(textMessage)

    val botMessage = KafkaConnectTextMessage(sendMessage).asInstanceOf[BotMessage[Nothing, Nothing]]

    (messageMapperMock.map _).expects(sinkRecord).returning(Success(botMessage))
    (messageSenderMock.sendMessage _).expects(botMessage).returning(Success(Seq(1)))

    sinkTask.put(Seq(sinkRecord).asJava)

    // sinkTask.stop()
  }

  test("testPut photo message") {
    val sinkTask = new TelegramSinkTask()

    sinkTask.initTask(goodProps.asJava)
    sinkTask.messageSender = Some(messageSenderMock)
    sinkTask.messageMapper = Some(messageMapperMock)

    // sinkTask.start(goodProps.asJava)

    val filename = "trained_airplane_1.jpg"
    val tgMessage: TgMessage = TelegramTestMessages.tgPhotoMessage(filename)

    val sinkRecord = new SinkRecord("topic", 5, null, null, null, tgMessage, 123)

    val sendPhoto = new SendPhoto()

    val botMessage = KafkaConnectPhotoMessage(sendPhoto).asInstanceOf[BotMessage[Nothing, Nothing]]

    (messageMapperMock.map _).expects(sinkRecord).returning(Success(botMessage))
    (messageSenderMock.sendMessage _).expects(botMessage).returning(Success(Seq(1)))

    sinkTask.put(Seq(sinkRecord).asJava)

    // sinkTask.stop()
  }

}
