package com.github.fbascheper.kafka.connect.telegram

import org.apache.kafka.connect.errors.ConnectException

import scala.collection.JavaConverters._

/**
  * Tests for the {@link TelegramSinkConnector}.
  *
  * @author Erik-Berndt Scheper
  * @since 17-09-2018
  *
  */
class TestTelegramSinkConnector extends TestTelegramBase {
  val goodProps: Map[String, String] = getSinkConfig
  val badProps: Map[String, String] = goodProps + (TelegramSinkConfig.TELEGRAM_BOT_DESTINATION_CHAT_ID -> "this is no integer")

  test("A TelegramSinkConnector should start with valid properties") {
    val t = new TelegramSinkConnector()
    t.start(goodProps.asJava)
  }

  test("A TelegramSinkConnector shouldn't start with invalid properties") {
    val t = new TelegramSinkConnector()
    a[ConnectException] should be thrownBy {
      t.start(badProps.asJava)
    }
  }

  test("A TelegramSinkConnector should provide the correct taskClass") {
    val t = new TelegramSinkConnector()
    t.taskClass() should be(classOf[TelegramSinkTask])
  }

  test("A TelegramSinkConnector should return a taskConfig for each task") {
    val t = new TelegramSinkConnector()
    t.taskConfigs(42).size() should be(42)
  }
}
