package io.airbrake.log4javabrake;

import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.AsyncSender;
import java.util.concurrent.CompletableFuture;

class TestAsyncSender implements AsyncSender {
  public Notice notice;

  @Override
  public CompletableFuture<Notice> send(Notice notice) {
    this.notice = notice;
    return new CompletableFuture<Notice>();
  }

  @Override
  public void setHost(String host) {}
}
