package com.github.fbascheper.kafka.connect.telegram.bot

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.{SendMessage, SendPhoto, SendVideo}
import org.telegram.telegrambots.meta.api.objects.Message

/**
  * A message suitable for sending by the `KafkaConnectLongPollingBot`.
  *
  * @author Erik-Berndt Scheper
  * @since 18-09-2018
  *
  */
trait BotMessage[T <: PartialBotApiMethod[R], R <: java.io.Serializable] {
  def getMessage(): T

  def getText(): String
}

case class KafkaConnectTextMessage(message: SendMessage) extends BotMessage[SendMessage, Message] {
  def getMessage(): SendMessage = message

  def getText(): String = s"text '${message.getText}'"
}

case class KafkaConnectPhotoMessage(message: SendPhoto) extends BotMessage[SendPhoto, Message] {
  def getMessage(): SendPhoto = message

  def getText(): String = s"caption '${message.getCaption}'"
}

case class KafkaConnectVideoMessage(message: SendVideo) extends BotMessage[SendVideo, Message] {
  def getMessage(): SendVideo = message

  def getText(): String = s"caption '${message.getCaption}'"
}
