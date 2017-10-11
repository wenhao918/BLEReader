package com.myhand.BLE;

import java.util.HashMap;

/**
 * Created by vincent on 2017/10/11.
 */

public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    public static String HEART_RATE_MEASUREMENT = "00002902-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String MYCJ_BLE = "服务UUID";
    public static String MYCJ_BLE_READ = "读属性UUID";
    public static String MYCJ_BLE_WRITE = "写属性UUID";
    static {
        attributes.put("0000fff00000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String getUUID(String uuidKey) {
        return attributes.get(uuidKey);
    }

}
