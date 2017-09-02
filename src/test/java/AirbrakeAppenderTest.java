package io.airbrake.log4javabrake;

import static org.junit.Assert.*;
import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.Airbrake;
import org.apache.log4j.Logger;
import io.airbrake.javabrake.Notifier;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import io.airbrake.javabrake.NoticeStackRecord;

public class AirbrakeAppenderTest {
  Notifier notifier = new Notifier(0, "");
  Throwable exc = new IOException("hello from Java");
  TestAsyncSender sender = new TestAsyncSender();

  @BeforeClass
  public static void beforeClass() {
    Logger.getRootLogger().addAppender(new AirbrakeAppender());
  }

  @Before
  public void before() {
    notifier.setAsyncSender(sender);
    Airbrake.setNotifier(notifier);
  }

  @Test
  public void testLogException() {
    Logger logger = Logger.getLogger(AirbrakeAppenderTest.class);
    logger.error(exc);

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("java.io.IOException", err.type);
    assertEquals("hello from Java", err.message);
  }

  @Test
  public void testLogMessage() {
    Logger logger = Logger.getLogger(AirbrakeAppenderTest.class);
    logger.error("hello from Java");

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("io.airbrake.log4javabrake.AirbrakeAppenderTest", err.type);
    assertEquals("hello from Java", err.message);

    NoticeStackRecord record = err.backtrace[0];
    assertEquals("testLogMessage", record.function);
    assertEquals("test/io/airbrake/log4javabrake/AirbrakeAppenderTest.class", record.file);
    assertEquals(43, record.line);
  }
}
