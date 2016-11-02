package com.durrutia.twinpic;

import com.google.common.base.Stopwatch;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by durrutia on 02-Nov-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
@Slf4j
public abstract class BaseTest {

    /**
     * Inicializacion
     */
    @SuppressWarnings("deprecation")
    @BeforeClass
    public static void initialize() {

        final Stopwatch stopwatch = Stopwatch.createStarted();

        log.debug("Initializing ...");

        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusManager sm = lc.getStatusManager();
        if (sm != null) {
            sm.add(new InfoStatus("Setting up default configuration.", lc));
        }

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%date %level [%file:%line] %msg%n");
        //ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();

        /*
        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setName("logcat");
        logcatAppender.setEncoder(ple);
        logcatAppender.setContext(lc);
        logcatAppender.start();
        */

        //*
        ch.qos.logback.core.ConsoleAppender<ILoggingEvent> consoleAppender = new ch.qos.logback.core.ConsoleAppender<>();
        consoleAppender.setName("Console");
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(lc);
        consoleAppender.start();
        //*/

        ch.qos.logback.classic.Logger rootLogger = lc.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(consoleAppender);
        rootLogger.setLevel(Level.ALL);

        rootLogger.debug("Logger configurated: {}", log);

        log.debug("Logger initialized in {}.", stopwatch);

        // StatusPrinter.print(lc);
    }


}
