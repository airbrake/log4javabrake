package io.airbrake.log4javabrake;

import java.util.concurrent.Future;
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.AsyncSender;

class TestAsyncSender implements AsyncSender {
  public Notice notice;

  @Override
  public Future<Notice> send(Notice notice) {
    this.notice = notice;
    return null;
  }
}
