package com.github.fbascheper.kafka.connect.telegram

import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

/**
  * Base test trait for telegram sink.
  *
  * @author Erik-Berndt Scheper
  * @since 16-09-2018
  *
  */
trait TestTelegramBase extends FunSuite with Matchers with BeforeAndAfter {
  def getSinkConfig: Map[String, String] = {
    Map(TelegramSinkConfig.TELEGRAM_BOT_NAME -> "test-name",
      TelegramSinkConfig.TELEGRAM_BOT_USERNAME -> "test-user-name",
      TelegramSinkConfig.TELEGRAM_BOT_DESTINATION_CHAT_ID -> "-12345",
      TelegramSinkConfig.TELEGRAM_BOT_API_KEY -> "99999999:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
      TelegramSinkConfig.TOPICS -> "test-sink-topic"
    )
  }
}
