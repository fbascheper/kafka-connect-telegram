package com.github.fbascheper.kafka.connect.telegram

import scala.collection.JavaConverters._

/**
  * Test class for {@link TelegramSinkConfig}.
  *
  * @author Erik-Berndt Scheper
  * @since 16-09-2018
  *
  */
class TestTelegramSinkConfig extends TestTelegramBase {
  test("A test TelegramSinkConfig should be correctly configured") {
    val config = getSinkConfig
    val taskConfig = new TelegramSinkConfig(config.asJava)
    taskConfig.getString(TelegramSinkConfig.TELEGRAM_BOT_NAME) shouldBe "test-name"
    taskConfig.getString(TelegramSinkConfig.TELEGRAM_BOT_USERNAME) shouldBe "test-user-name"
    taskConfig.getPassword(TelegramSinkConfig.TELEGRAM_BOT_DESTINATION_CHAT_ID).value shouldBe "-12345"
    taskConfig.getPassword(TelegramSinkConfig.TELEGRAM_BOT_API_KEY).value shouldBe "99999999:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    taskConfig.getList(TelegramSinkConfig.TOPICS) shouldBe Seq("test-sink-topic").asJava
  }
}
