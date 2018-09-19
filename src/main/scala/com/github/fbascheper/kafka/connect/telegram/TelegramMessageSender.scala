package com.github.fbascheper.kafka.connect.telegram

import com.github.fbascheper.kafka.connect.telegram.bot.BotMessage
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod

import scala.util.Try

/**
  * A message sender using Telegram.
  *
  * @author Erik-Berndt Scheper
  * @since 17-09-2018
  *
  */
trait TelegramMessageSender {
  /**
    * Send a bot message using Telegram.
    *
    * @param botMessage the message to send
    */
  def sendMessage[T <: PartialBotApiMethod[R], R <: java.io.Serializable](botMessage: BotMessage[T, R]): Try[Seq[Int]]
}
