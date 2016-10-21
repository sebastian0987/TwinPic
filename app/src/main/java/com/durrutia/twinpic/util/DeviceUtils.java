package com.durrutia.twinpic.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by durrutia on 20-Oct-16.
 */
@Slf4j
public class DeviceUtils {

    /**
     *
     * @param context
     * @return the DeviceID
     */
    public static String getDeviceId(final Context context) {

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if ("9774d56d682e549c".equals(androidId)) {
            log.warn("AndroidId Broken!");
        }

        return androidId;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getDeviceName(final Context context) {
        String manufacturer = Build.MANUFACTURER;
        String undecodedModel = Build.MODEL;
        String model = null;

        try {
            Properties prop = new Properties();
            InputStream fileStream;
            // Read the device name from a precomplied list:
            // see http://making.meetup.com/post/29648976176/human-readble-android-device-names
            fileStream = context.getAssets().open("android_models.properties");
            prop.load(fileStream);
            fileStream.close();
            String decodedModel = prop.getProperty(undecodedModel.replaceAll(" ", "_"));
            if (decodedModel != null && !decodedModel.trim().equals("")) {
                model = decodedModel;
            }
        } catch (IOException e) {
            log.error("Error", e);
        }

        if (model == null) {  //Device model not found in the list
            if (undecodedModel.startsWith(manufacturer)) {
                model = capitalize(undecodedModel);
            } else {
                model = capitalize(manufacturer) + " " + undecodedModel;
            }
        }
        return model;
    }

    /**
     *
     * @param s
     * @return
     */
    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
