package com.github.fbascheper.kafka.connect.telegram

import java.util

import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.sink.SinkConnector

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

/**
  * Connector to send messages from Kafka to a Telegram chat.
  *
  * @author Erik-Berndt Scheper
  * @since 17-09-2018
  *
  */
class TelegramSinkConnector extends SinkConnector with Logging {
  private var configProps: util.Map[String, String] = null

  /**
    * Return config definition for sink connector
    */
  override def config() = TelegramSinkConfig.config

  /**
    * States which SinkTask class to use
    **/
  override def taskClass(): Class[_ <: Task] = classOf[TelegramSinkTask]

  /**
    * Set the configuration for each work and determine the split
    *
    * @param maxTasks The max number of task workers be can spawn
    * @return a List of configuration properties per worker
    **/
  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    log.info(s"Setting task configurations for $maxTasks workers.")
    (1 to maxTasks).map(c => configProps).toList.asJava
  }

  /**
    * Start the sink and set to configuration
    *
    * @param props A map of properties for the connector and worker
    **/
  override def start(props: util.Map[String, String]): Unit = {
    log.info(s"Starting Telegram sink task with ${props.toString}.")
    configProps = props
    Try(new TelegramSinkConfig(props)) match {
      case Failure(f) => throw new ConnectException("Couldn't start TelegramSinkConnector due to configuration error.", f)
      case _ =>
    }
  }

  override def stop(): Unit = {}

  override def version(): String = ""
}
