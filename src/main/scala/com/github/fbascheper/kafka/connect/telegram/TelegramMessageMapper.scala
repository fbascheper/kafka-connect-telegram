package com.github.fbascheper.kafka.connect.telegram

import java.io.ByteArrayInputStream

import com.github.fbascheper.kafka.connect.telegram.bot.{BotMessage, KafkaConnectPhotoMessage, KafkaConnectTextMessage, KafkaConnectVideoMessage}
import org.apache.kafka.connect.sink.SinkRecord
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.{SendMessage, SendPhoto, SendVideo}
import org.telegram.telegrambots.meta.api.objects.Message

import scala.util.Try

/**
  * Mapper from sink records to a Telegram bot message.
  *
  * @author Erik-Berndt Scheper
  * @since 18-09-2018
  * @param chatId Default chat-id to use for Telegram messages.
  */
class TelegramMessageMapper(chatId: Long) extends Logging {

  def map[T <: PartialBotApiMethod[R], R <: java.io.Serializable](sinkRecord: SinkRecord): Try[BotMessage[T, R]] = {
    val x: AnyRef = sinkRecord.value

    val result: Try[BotMessage[T, R]] = x match {
      case msg: TgMessage =>
        val msgType = msg.getMessageType
        log.info("Found TgMessage with type {}", msgType)

        msgType match {
          case TgMessageType.TEXT => Try(sendMessage(msg.getTextMessage, chatId).asInstanceOf[BotMessage[T, R]])
          case TgMessageType.PHOTO => Try(sendPhoto(msg.getPhotoMessage, chatId).asInstanceOf[BotMessage[T, R]])
          case TgMessageType.VIDEO => Try(SendVideo(msg.getVideoMessage, chatId).asInstanceOf[BotMessage[T, R]])
        }

      case _ =>
        val text = sinkRecord.value.toString
        log.info("Using text to send telegram text-message with contents = {}", text)
        val message = new SendMessage()
        message.setChatId(chatId)
        message.setText(text)

        Try(KafkaConnectTextMessage(message).asInstanceOf[BotMessage[T, R]])
    }

    result
  }

  private def sendMessage(txtMsg: TgTextMessage, chatId: Long): BotMessage[SendMessage, Message] = {
    val message = new SendMessage()

    if (txtMsg.getChatId == null)
      message.setChatId(chatId)
    else
      message.setChatId(txtMsg.getChatId)

    if (txtMsg.getParseMode != null) {
      txtMsg.getParseMode match {
        case TgParseMode.MARKDOWN => message.setParseMode("Markdown")
        case TgParseMode.HTML => message.setParseMode("HTML")
        case _ => message.setParseMode(null)
      }
    }
    if (txtMsg.getDisableWebPagePreview)
      message.disableWebPagePreview()
    else
      message.enableWebPagePreview()

    if (txtMsg.getDisableNotification)
      message.disableNotification()
    else
      message.enableNotification()

    message.setText(txtMsg.getText)
    message.setReplyToMessageId(txtMsg.getReplyToMessageId)

    KafkaConnectTextMessage(message).asInstanceOf[BotMessage[SendMessage, Message]]
  }

  private def sendPhoto(photoMsg: TgPhotoMessage, chatId: Long): BotMessage[SendPhoto, Message] = {
    val message = new SendPhoto()

    if (photoMsg.getChatId == null)
      message.setChatId(chatId)
    else
      message.setChatId(photoMsg.getChatId)

    message.setCaption(photoMsg.getCaption)

    if (photoMsg.getParseMode != null) {
      photoMsg.getParseMode match {
        case TgParseMode.MARKDOWN => message.setParseMode("Markdown")
        case TgParseMode.HTML => message.setParseMode("HTML")
        case _ => message.setParseMode(null)
      }
    }

    val attachment = photoMsg.getPhoto
    if (attachment.getContents == null) {
      message.setPhoto(attachment.getName)
    } else {
      val contents = attachment.getContents.array()
      val inputStream = new ByteArrayInputStream(contents)
      message.setPhoto(attachment.getName, inputStream)
    }

    message.setReplyToMessageId(photoMsg.getReplyToMessageId)

    KafkaConnectPhotoMessage(message).asInstanceOf[BotMessage[SendPhoto, Message]]
  }

  private def SendVideo(videoMsg: TgVideoMessage, chatId: Long): BotMessage[SendVideo, Message] = {

    val message = new SendVideo()

    if (videoMsg.getChatId == null)
      message.setChatId(chatId)
    else
      message.setChatId(videoMsg.getChatId)

    message.setCaption(videoMsg.getCaption)

    if (videoMsg.getParseMode != null) {
      videoMsg.getParseMode match {
        case TgParseMode.MARKDOWN => message.setParseMode("Markdown")
        case TgParseMode.HTML => message.setParseMode("HTML")
        case _ => message.setParseMode(null)
      }
    }

    val attachment = videoMsg.getVideo
    if (attachment.getContents == null) {
      message.setVideo(attachment.getName)
    } else {
      val contents = attachment.getContents.array()
      val inputStream = new ByteArrayInputStream(contents)
      message.setVideo(attachment.getName, inputStream)
    }

    message.setDuration(videoMsg.getDuration)
    message.setHeight(videoMsg.getHeight)
    message.setWidth(videoMsg.getWidth)
    message.setReplyToMessageId(videoMsg.getReplyToMessageId)
    message.setSupportsStreaming(videoMsg.getSupportsStreaming)

    KafkaConnectVideoMessage(message).asInstanceOf[BotMessage[SendVideo, Message]]
  }

}
