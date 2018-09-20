package com.github.fbascheper.kafka.connect.telegram.mapper

import com.github.fbascheper.kafka.connect.telegram._
import io.confluent.connect.avro.AvroData
import org.apache.kafka.connect.sink.SinkRecord
import org.telegram.telegrambots.meta.api.methods.send.{SendMessage, SendPhoto}
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard

/**
  * Test class for `TelegramMessageMapper`.
  *
  * @author Erik-Berndt Scheper
  * @since 19-09-2018
  *
  */
class TelegramMessageMapperTest extends TestTelegramBase {

  val chatId = 23
  val mapper = new TelegramMessageMapper(chatId)
  val avroData = new AvroData(100)

  test("test mapping of telegram photo message") {
    val filename = "trained_airplane_1.jpg"
    val tgMessage: TgMessage = TelegramTestMessages.tgPhotoMessage(filename)
    val photoMessage = tgMessage.getPhotoMessage

    val sinkRecord = new SinkRecord("topic", 5, null, null, null, tgMessage, 123)

    val message = mapper.map(sinkRecord)

    message.isSuccess shouldBe true
    message.get.getText() shouldBe s"caption '${photoMessage.getCaption}'"
    val sendPhoto: SendPhoto = message.get.getMessage().asInstanceOf[SendPhoto]

    sendPhoto.getCaption shouldBe photoMessage.getCaption
    sendPhoto.getChatId shouldBe chatId.toString
    sendPhoto.getParseMode shouldBe null
    sendPhoto.getPhoto.getAttachName shouldBe "attach://" + filename
  }

  test("test mapping of struct photo message") {
    val filename = "trained_airplane_1.jpg"
    val tgMessage: TgMessage = TelegramTestMessages.tgPhotoMessage(filename)
    val photoMessage = tgMessage.getPhotoMessage

    val schemaAndValue = avroData.toConnectData(TgMessage.getClassSchema, tgMessage)

    val sinkRecord = new SinkRecord("topic", 5, null, null, schemaAndValue.schema(), schemaAndValue.value(), 123)

    val message = mapper.map(sinkRecord)

    message.isSuccess shouldBe true
    message.get.getText() shouldBe s"caption '${photoMessage.getCaption}'"
    val sendPhoto: SendPhoto = message.get.getMessage().asInstanceOf[SendPhoto]

    sendPhoto.getCaption shouldBe photoMessage.getCaption
    sendPhoto.getChatId shouldBe chatId.toString
    sendPhoto.getParseMode shouldBe null
    sendPhoto.getPhoto.getAttachName shouldBe "attach://" + filename
  }


  test("test mapping of struct text message") {
    val tgMessage: TgMessage = TelegramTestMessages.tgTextMessage()
    val textMessage = tgMessage.getTextMessage

    val schemaAndValue = avroData.toConnectData(TgMessage.getClassSchema, tgMessage)

    val sinkRecord = new SinkRecord("topic", 5, null, null, schemaAndValue.schema(), schemaAndValue.value(), 123)

    val tryMessage = mapper.map(sinkRecord)

    tryMessage.isSuccess shouldBe true

    val sendMessage: SendMessage = tryMessage.get.getMessage().asInstanceOf[SendMessage]

    sendMessage.getText shouldBe textMessage.getText
    sendMessage.getChatId shouldBe chatId.toString
    sendMessage.getDisableNotification shouldBe true
    sendMessage.getDisableWebPagePreview shouldBe true
    sendMessage.getReplyToMessageId shouldBe null

    val replyKeyboard = sendMessage.getReplyMarkup
    replyKeyboard.isInstanceOf[ForceReplyKeyboard] shouldBe false
  }
}
