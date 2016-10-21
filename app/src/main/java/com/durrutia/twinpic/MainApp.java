package com.durrutia.twinpic;

import android.app.Application;
import android.os.SystemClock;

import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.util.DeviceUtils;
import com.durrutia.twinpic.util.PackageUtils;
import com.squareup.leakcanary.LeakCanary;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by durrutia on 20-Oct-16.
 */
@Slf4j
public class MainApp extends Application {

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        // Startdate
        long startDate = SystemClock.elapsedRealtime();

        log.debug("Install LeakCanary ..");

        LeakCanary.install(this);
        // Normal app init code...

        final String deviceName = DeviceUtils.getDeviceName(this);
        log.debug("DeviceName: {}", deviceName);

        final String deviceId = DeviceUtils.getDeviceId(this);
        log.debug("DeviceId: {}", deviceId);
        log.debug("UserId: {}", getUserAgent());

        log.debug("Building ..");

        final Pic pic = Pic.builder()
                .id(-1L)
                .deviceId(deviceId)
                .date(System.currentTimeMillis())
                .build();

        log.debug("Pic: {}", pic);

        long endDate = SystemClock.elapsedRealtime() - startDate;
        log.info("Init in {}ms.", endDate);

    }

    public String getUserAgent() {
        return "tp-android/" + PackageUtils.getVersionName(this);
    }
}
