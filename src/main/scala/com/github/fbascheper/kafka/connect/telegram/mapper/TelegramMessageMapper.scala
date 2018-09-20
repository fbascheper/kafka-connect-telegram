package com.github.fbascheper.kafka.connect.telegram.mapper

import java.io.ByteArrayInputStream
import java.nio.ByteBuffer

import com.github.fbascheper.kafka.connect.telegram._
import com.github.fbascheper.kafka.connect.telegram.bot.{BotMessage, KafkaConnectPhotoMessage, KafkaConnectTextMessage, KafkaConnectVideoMessage}
import org.apache.kafka.connect.data.Struct
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

    def mapAvroStructMessage(struct: Struct) = {
      def byteBuffer(option: Option[AnyRef]): ByteBuffer = option match {
        case Some(value) if value.isInstanceOf[Array[Byte]]  => ByteBuffer.wrap(value.asInstanceOf[Array[Byte]])
        case _ => null
      }

      def tgMessageType(option: Option[AnyRef]): TgMessageType = option match {
        case Some(value) if value.isInstanceOf[String] => TgMessageType.valueOf(value.asInstanceOf[String])
        case _ => null
      }

      def tgParseMode(option: Option[AnyRef]): TgParseMode = option match {
        case Some(value) if value.isInstanceOf[String] => TgParseMode.valueOf(value.asInstanceOf[String])
        case _ => null
      }

      def tgReplyKeyboard(option: Option[AnyRef]): TgReplyKeyboard = option match {
        case Some(value) if value.isInstanceOf[Struct] => {
          val replyKeyboard = new TgReplyKeyboard()
          replyKeyboard.setContentsTBD("TBD")
          replyKeyboard
        }
        case _ => null
      }

      def tgAttachment(extractor: StructFieldsExtractor, option: Option[AnyRef]): TgAttachment = option match {
        case Some(value) if value.isInstanceOf[Struct] => {
          val attachmentFields: Map[String, AnyRef] = extractor.get(value.asInstanceOf[Struct]).toMap
          val attachment: TgAttachment = new TgAttachment()
          attachment.setName(attachmentFields.getOrElse("name", null).asInstanceOf[java.lang.String])
          attachment.setContents(byteBuffer(attachmentFields.get("contents")))
          attachment
        }
        case _ => null
      }

      def tgTextMessage(extractor: StructFieldsExtractor, option: Option[AnyRef]): TgTextMessage = option match {
        case Some(value) if value.isInstanceOf[Struct] => {
          val tgTextMsgFields: Map[String, AnyRef] = extractor.get(value.asInstanceOf[Struct]).toMap
          val tgTextMessage = new TgTextMessage()
          tgTextMessage.setChatId(tgTextMsgFields.getOrElse("chatId", null).asInstanceOf[java.lang.Long])
          tgTextMessage.setDisableNotification(tgTextMsgFields.getOrElse("disableNotification", null).asInstanceOf[java.lang.Boolean])
          tgTextMessage.setDisableWebPagePreview(tgTextMsgFields.getOrElse("disableWebPagePreview", null).asInstanceOf[java.lang.Boolean])
          tgTextMessage.setParseMode(tgParseMode(tgTextMsgFields.get("parseMode")))
          tgTextMessage.setReplyMarkup(tgReplyKeyboard(tgTextMsgFields.get("replyMarkup")))
          tgTextMessage.setReplyToMessageId(tgTextMsgFields.getOrElse("replyToMessageId", null).asInstanceOf[java.lang.Integer])
          tgTextMessage.setText(tgTextMsgFields.getOrElse("text", null).asInstanceOf[java.lang.String])
          tgTextMessage
        }
        case _ => null
      }

      def tgPhotoMessage(extractor: StructFieldsExtractor, option: Option[AnyRef]): TgPhotoMessage = option match {
        case Some(value) if value.isInstanceOf[Struct] => {
          val tgPhotoMsgFields: Map[String, AnyRef] = extractor.get(value.asInstanceOf[Struct]).toMap
          val tgPhotoMessage = new TgPhotoMessage()
          tgPhotoMessage.setCaption(tgPhotoMsgFields.getOrElse("caption", null).asInstanceOf[java.lang.String])
          tgPhotoMessage.setChatId(tgPhotoMsgFields.getOrElse("chatId", null).asInstanceOf[java.lang.Long])
          tgPhotoMessage.setDisableNotification(tgPhotoMsgFields.getOrElse("disableNotification", null).asInstanceOf[java.lang.Boolean])
          tgPhotoMessage.setParseMode(tgParseMode(tgPhotoMsgFields.get("parseMode")))
          tgPhotoMessage.setPhoto(tgAttachment(extractor, tgPhotoMsgFields.get("photo")))
          tgPhotoMessage.setReplyMarkup(tgReplyKeyboard(tgPhotoMsgFields.get("replyMarkup")))
          tgPhotoMessage.setReplyToMessageId(tgPhotoMsgFields.getOrElse("replyToMessageId", null).asInstanceOf[java.lang.Integer])
          tgPhotoMessage
        }
        case _ => null
      }

      def tgVideoMessage(extractor: StructFieldsExtractor, option: Option[AnyRef]): TgVideoMessage = option match {
        case Some(value) if value.isInstanceOf[Struct] => {
          val tgVideoMsgFields: Map[String, AnyRef] = extractor.get(value.asInstanceOf[Struct]).toMap
          val tgVideoMessage = new TgVideoMessage()
          tgVideoMessage.setCaption(tgVideoMsgFields.getOrElse("caption", null).asInstanceOf[java.lang.String])
          tgVideoMessage.setChatId(tgVideoMsgFields.getOrElse("chatId", null).asInstanceOf[java.lang.Long])
          tgVideoMessage.setDisableNotification(tgVideoMsgFields.getOrElse("disableNotification", null).asInstanceOf[java.lang.Boolean])
          tgVideoMessage.setDuration(tgVideoMsgFields.getOrElse("duration", null).asInstanceOf[java.lang.Integer])
          tgVideoMessage.setHeight(tgVideoMsgFields.getOrElse("height", null).asInstanceOf[java.lang.Integer])
          tgVideoMessage.setParseMode(tgParseMode(tgVideoMsgFields.get("parseMode")))
          tgVideoMessage.setReplyMarkup(tgReplyKeyboard(tgVideoMsgFields.get("replyMarkup")))
          tgVideoMessage.setReplyToMessageId(tgVideoMsgFields.getOrElse("replyToMessageId", null).asInstanceOf[java.lang.Integer])
          tgVideoMessage.setSupportsStreaming(tgVideoMsgFields.getOrElse("supportsStreaming", null).asInstanceOf[java.lang.Boolean])
          tgVideoMessage.setVideo(tgAttachment(extractor, tgVideoMsgFields.get("video")))
          tgVideoMessage.setWidth(tgVideoMsgFields.getOrElse("width", null).asInstanceOf[java.lang.Integer])
          tgVideoMessage
        }
        case _ => null
      }

      log.info("Converting struct back to model")

      val extractor = StructFieldsExtractor(includeAllFields = true, Map())
      val tgMessageFields: Map[String, AnyRef] = extractor.get(struct).toMap

      val messageType = tgMessageType(tgMessageFields.get("messageType"))
      val textMessage = tgTextMessage(extractor, tgMessageFields.get("textMessage"))
      val photoMessage = tgPhotoMessage(extractor, tgMessageFields.get("photoMessage"))
      val videoMessage = tgVideoMessage(extractor, tgMessageFields.get("videoMessage"))

      val msg = new TgMessage()
      msg.setMessageType(messageType)
      msg.setTextMessage(textMessage)
      msg.setPhotoMessage(photoMessage)
      msg.setVideoMessage(videoMessage)

      mapTelegramMessage(msg)
    }

    def mapTelegramMessage(msg: TgMessage) = {
      val msgType = msg.getMessageType
      log.info("Found TgMessage with type {}", msgType)

      msgType match {
        case TgMessageType.TEXT => Try(sendMessage(msg.getTextMessage, chatId).asInstanceOf[BotMessage[T, R]])
        case TgMessageType.PHOTO => Try(sendPhoto(msg.getPhotoMessage, chatId).asInstanceOf[BotMessage[T, R]])
        case TgMessageType.VIDEO => Try(SendVideo(msg.getVideoMessage, chatId).asInstanceOf[BotMessage[T, R]])
      }
    }

    def mapTextMessage = {
      val text = sinkRecord.value.toString
      log.info("Using text to send telegram text-message with contents = {}", text)
      val message = new SendMessage()
      message.setChatId(chatId)
      message.setText(text)

      Try(KafkaConnectTextMessage(message).asInstanceOf[BotMessage[T, R]])
    }

    val result: Try[BotMessage[T, R]] = x match {
      case struct: Struct => mapAvroStructMessage(struct)
      case msg: TgMessage => mapTelegramMessage(msg)
      case _ => mapTextMessage
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
    if (txtMsg.getDisableWebPagePreview != null && !txtMsg.getDisableWebPagePreview)
      message.enableWebPagePreview()
    else
      message.disableWebPagePreview()

    if (txtMsg.getDisableNotification != null && !txtMsg.getDisableNotification)
      message.enableNotification()
    else
      message.disableNotification()

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
