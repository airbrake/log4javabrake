package io.airbrake.log4javabrake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;

import io.airbrake.javabrake.Notifier;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.NoticeStackRecord;

public class AirbrakeAppender extends AppenderSkeleton {
  Notifier notifier;

  public AirbrakeAppender() {
    setThreshold(Level.ERROR);
  }

  public AirbrakeAppender(int projectId, String projectKey) {
    this();
    this.notifier = new Notifier(projectId, projectKey);
  }

  @Override
  protected void append(LoggingEvent event) {
    Notice notice = newNotice(event);
    notice.setContext("severity", formatLevel(event.getLevel()));
    notice.setParam("threadName", event.getThreadName());
    if (event.getNDC() != null) {
      notice.setParam("ndc", event.getNDC());
    }
    Map props = event.getProperties();
    if (props.size() > 0) {
      notice.setParam("properties", props);
    }
    this.send(notice);
  }

  @Override
  public void close() {}

  @Override
  public boolean requiresLayout() {
    return false;
  }

  static Notice newNotice(LoggingEvent event) {
    ThrowableInformation info = event.getThrowableInformation();
    if (info != null) {
      return new Notice(info.getThrowable());
    }

    Object msg = event.getMessage();
    if (msg instanceof Throwable) {
      return new Notice((Throwable) msg);
    }

    String type = event.getLoggerName();
    String message = event.getRenderedMessage();
    StackTraceElement[] stackTrace = null;

    LocationInfo loc = event.getLocationInformation();
    if (loc != null) {
      String className = loc.getClassName();
      String method = loc.getMethodName();
      String file = loc.getFileName();
      int line = Integer.parseInt(loc.getLineNumber());
      stackTrace = new StackTraceElement[1];
      stackTrace[0] = new StackTraceElement(className, method, file, line);
    }

    NoticeError err = new NoticeError(type, message, stackTrace);

    List<NoticeError> errors = new ArrayList<>();
    errors.add(err);

    return new Notice(errors);
  }

  static String formatLevel(Level level) {
    if (level.isGreaterOrEqual(Level.FATAL)) {
      return "critical";
    }
    if (level.isGreaterOrEqual(Level.ERROR)) {
      return "error";
    }
    if (level.isGreaterOrEqual(Level.WARN)) {
      return "warn";
    }
    if (level.isGreaterOrEqual(Level.INFO)) {
      return "info";
    }
    if (level.isGreaterOrEqual(Level.DEBUG)) {
      return "debug";
    }
    return "trace";
  }

  Future<Notice> send(Notice notice) {
    if (this.notifier != null) {
      return this.notifier.send(notice);
    }
    return Airbrake.send(notice);
  }
}
