package com.github.fbascheper.kafka.connect.telegram

import java.nio.ByteBuffer

import org.apache.kafka.connect.sink.SinkRecord
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto

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

  test("testSendPhoto") {
    val filename = "trained_airplane_1.jpg"
    val attachment = new TgAttachment()
    val inputStream = getClass.getClassLoader.getResourceAsStream(filename)
    val bArray = Stream.continually(inputStream.read).takeWhile(p => p != -1).map(_.toByte).toArray
    val buffer = ByteBuffer.wrap(bArray)
    attachment.setName(filename)
    attachment.setContents(buffer)

    val photoMessage = new TgPhotoMessage()
    photoMessage.setCaption("Hello world")
    photoMessage.setPhoto(attachment)

    val tgMessage = new TgMessage()
    tgMessage.setMessageType(TgMessageType.PHOTO)
    tgMessage.setPhotoMessage(photoMessage)

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

}
