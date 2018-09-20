package com.github.fbascheper.kafka.connect.telegram.mapper

import java.nio.ByteBuffer

import com.github.fbascheper.kafka.connect.telegram._

/**
  * Sample telegram test messages.
  *
  * @author Erik-Berndt Scheper
  * @since 20-09-2018
  *
  */
object TelegramTestMessages {
  /**
    * Create a Telegram text message for a given filename.
    *
    * @return the Telegram text message.
    */
  def tgTextMessage(): TgMessage = {
    val textMessage = new TgTextMessage()
    textMessage.setText("#Hello world")
    textMessage.setParseMode(TgParseMode.MARKDOWN)
    textMessage.setDisableNotification(true)

    val tgReplyKeyboard = new TgReplyKeyboard()
    tgReplyKeyboard.setContentsTBD("whatever")
    textMessage.setReplyMarkup(tgReplyKeyboard)

    val tgMessage = new TgMessage()
    tgMessage.setMessageType(TgMessageType.TEXT)
    tgMessage.setTextMessage(textMessage)

    tgMessage

  }


  /**
    * Create a Telegram photo message for a given filename.
    *
    * @param filename the filename with a picture
    * @return the Telegram photo message.
    */
  def tgPhotoMessage(filename: String): TgMessage = {
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

    tgMessage
  }

}
