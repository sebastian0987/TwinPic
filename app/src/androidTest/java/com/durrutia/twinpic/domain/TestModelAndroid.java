package com.durrutia.twinpic.domain;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.base.Stopwatch;
import android.support.test.runner.AndroidJUnit4;

import com.durrutia.twinpic.util.DeviceUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by durrutia on 02-Nov-16.
 */
@RunWith(AndroidJUnit4.class)
@Slf4j
public class TestModelAndroid {

    /**
     * Timming
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);


    /**
     * Testing de la base de datos
     */
    @Test
    public void testDatabase() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // Cronometro
        final Stopwatch stopwatch = Stopwatch.createStarted();

        log.debug("Testing database ..");

        // Inicializacion
        FlowManager.init(new FlowConfig.Builder(appContext)
                .openDatabasesOnInit(true)
                .build());

        log.debug("DB initialized in: {}.", stopwatch);

        stopwatch.reset().start();

        // Ciclo para insertar 100 objetos en la bd
        for (int i = 1; i <= 100; i++) {

            stopwatch.reset().start();

            final Pic pic = Pic.builder()
                    .deviceId(DeviceUtils.getDeviceId(appContext))
                    .latitude(RandomUtils.nextDouble())
                    .longitude(RandomUtils.nextDouble())
                    .date(new Date().getTime())
                    .positive(RandomUtils.nextInt(0, 100))
                    .negative(RandomUtils.nextInt(0, 100))
                    .warning(RandomUtils.nextInt(0, 2))
                    .build();

            // Commit
            pic.save();

            log.debug("Saved {} in {}.", i, stopwatch);
        }

        stopwatch.reset().start();

        List<Pic> pics = SQLite.select().from(Pic.class).queryList();
        log.debug("Result: {} in {}.", pics.size(), stopwatch);

        stopwatch.reset().start();

        for (final Pic p : pics) {
            log.debug("{}", p);
        }

        FlowManager.destroy();

        log.debug("Finished in {}.", stopwatch);

    }

}
