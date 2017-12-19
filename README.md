# log4j appender for javabrake

[![Build Status](https://travis-ci.org/airbrake/log4javabrake.svg?branch=master)](https://travis-ci.org/airbrake/log4javabrake)

## Installation

Gradle:

```gradle
compile 'io.airbrake:log4javabrake:0.1.3'
```

Maven:

```xml
<dependency>
  <groupId>io.airbrake</groupId>
  <artifactId>log4javabrake</artifactId>
  <version>0.1.3</version>
</dependency>
```

Ivy:

```xml
<dependency org='io.airbrake' name='log4javabrake' rev='0.1.3'>
  <artifact name='log4javabrake' ext='pom'></artifact>
</dependency>
```

## Configuration

Using config:

```
log4j.rootLogger=INFO, stdout, airbrake

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[%d,%p] [%c{1}.%M:%L] %m%n

log4j.appender.airbrake=io.airbrake.log4javabrake.AirbrakeAppender
```

Using Java:

```java
import org.apache.log4j.Logger;
import io.airbrake.log4javabrake.AirbrakeAppender;

Logger.getRootLogger().addAppender(new AirbrakeAppender());
```
