package com.github.fbascheper.kafka.connect.telegram

import org.slf4j.{Logger, LoggerFactory}

/**
  * Logging trait using slf4j.
  *
  * @author Erik-Berndt Scheper
  * @since 16-09-2018
  *
  */
trait Logging {
  @transient lazy val log: Logger = LoggerFactory.getLogger(loggerName)
  private val loggerName = this.getClass.getName
}
