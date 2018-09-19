package com.github.fbascheper.kafka.connect.telegram

import java.util

import com.github.fbascheper.kafka.connect.telegram.TelegramSinkConfig.TELEGRAM_BOT_DESTINATION_CHAT_ID
import org.apache.kafka.common.config.ConfigDef.{Importance, Type}
import org.apache.kafka.common.config.types.Password
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}
import org.apache.kafka.connect.sink.SinkTask

/**
  * Configuration definition for a telegram sink.
  *
  * @author Erik-Berndt Scheper
  * @since 16-09-2018
  *
  */
object TelegramSinkConfig {
  val TELEGRAM_BOT_NAME = "telegram.bot.name"
  val TELEGRAM_BOT_NAME_DOC = "The name of your Telegram bot."
  val TELEGRAM_BOT_USERNAME = "telegram.bot.username"
  val TELEGRAM_BOT_USERNAME_DOC = "The username for your Telegram bot."
  val TELEGRAM_BOT_API_KEY = "telegram.bot.api.key"
  val TELEGRAM_BOT_API_KEY_DOC = "The token to access the Telegram HTTP API"
  val TELEGRAM_BOT_DESTINATION_CHAT_ID = "telegram.bot.destination.chat.id"
  val TELEGRAM_BOT_DESTINATION_CHAT_ID_DOC = "The chat-id of the group to send the Telegram messages to"
  val TOPICS: String = SinkTask.TOPICS_CONFIG
  val TOPICS_DOC = "The Kafka topic to read from."

  val nonEmptyStringValidator = new ConfigDef.NonEmptyString
  val config: ConfigDef = new ConfigDef()
    .define(TELEGRAM_BOT_NAME, Type.STRING, ConfigDef.NO_DEFAULT_VALUE, nonEmptyStringValidator, Importance.HIGH, TELEGRAM_BOT_NAME_DOC)
    .define(TELEGRAM_BOT_USERNAME, Type.STRING, ConfigDef.NO_DEFAULT_VALUE, nonEmptyStringValidator, Importance.HIGH, TELEGRAM_BOT_USERNAME_DOC)
    .define(TELEGRAM_BOT_API_KEY, Type.PASSWORD, Importance.HIGH, TELEGRAM_BOT_API_KEY_DOC)
    .define(TELEGRAM_BOT_DESTINATION_CHAT_ID, Type.PASSWORD, Importance.HIGH, TELEGRAM_BOT_DESTINATION_CHAT_ID_DOC)
    .define(TOPICS, Type.LIST, Importance.HIGH, TOPICS_DOC)
}

class TelegramSinkConfig(props: util.Map[String, String])
  extends AbstractConfig(TelegramSinkConfig.config, props) {

  val chatId: Password = getPassword(TELEGRAM_BOT_DESTINATION_CHAT_ID)

  try {
    chatId.value().toLong
  } catch {
    case e: NumberFormatException => throw new RuntimeException("You should use numeric chat IDs in "
      + TELEGRAM_BOT_DESTINATION_CHAT_ID)
  }
}
