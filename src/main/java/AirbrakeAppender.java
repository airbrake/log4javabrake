package io.airbrake.log4javabrake;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import java.util.ArrayList;
import java.util.List;
import io.airbrake.javabrake.Notifier;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.NoticeStackRecord;

public class AirbrakeAppender extends AppenderSkeleton {
  public AirbrakeAppender() {
    setThreshold(Level.ERROR);
  }

  @Override
  protected void append(LoggingEvent event) {
    Throwable throwable = this.throwable(event);
    if (throwable != null) {
      Airbrake.report(throwable);
      return;
    }

    String type = event.getLoggerName();
    String message = event.getRenderedMessage();
    List<NoticeStackRecord> backtrace = null;

    LocationInfo loc = event.getLocationInformation();
    if (loc != null) {
      String function = loc.getMethodName();
      String file = loc.getFileName();
      int line = Integer.parseInt(loc.getLineNumber());
      NoticeStackRecord rec = new NoticeStackRecord(function, file, line);

      backtrace = new ArrayList<>();
      backtrace.add(rec);
    }

    NoticeError err = new NoticeError(type, message, backtrace);

    List<NoticeError> errors = new ArrayList<>();
    errors.add(err);

    Notice notice = new Notice(errors);
    Airbrake.send(notice);
  }

  @Override
  public void close() {}

  @Override
  public boolean requiresLayout() {
    return false;
  }

  Throwable throwable(LoggingEvent event) {
    ThrowableInformation info = event.getThrowableInformation();
    if (info != null) {
      return info.getThrowable();
    }

    Object msg = event.getMessage();
    if (msg instanceof Throwable) {
      return (Throwable) msg;
    }

    return null;
  }
}
