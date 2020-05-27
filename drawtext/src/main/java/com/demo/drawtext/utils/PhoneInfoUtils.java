package com.demo.drawtext.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhoneInfoUtils {

    /**
     * 系统4.0的时候
     * 获取手机IMEI 或者MEID
     *
     * @return 手机IMEI
     */
    @SuppressLint("MissingPermission")
    public static String getImeiOrMeid(Context ctx) {
        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (manager != null) {
            return manager.getDeviceId();
        }

        return null;
    }


    /**
     * 5.0统一使用这个获取IMEI IMEI2 MEID
     *
     * @param ctx
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    public static Map getImeiAndMeid(Context ctx) {
        Map<String, String> map = new HashMap<>();
        TelephonyManager mTelephonyManager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        Class<?> clazz = null;
        Method method = null;//(int slotId)

        try {
            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getMethod("get", String.class, String.class);
            String gsm = (String) method.invoke(null, "ril.gsm.imei", "");
            String meid = (String) method.invoke(null, "ril.cdma.meid", "");
            map.put("meid", meid);
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                String imeiArray[] = gsm.split(",");
                if (imeiArray != null && imeiArray.length > 0) {
                    map.put("imei1", imeiArray[0]);

                    if (imeiArray.length > 1) {
                        map.put("imei2", imeiArray[1]);
                    } else {
                        map.put("imei2", mTelephonyManager.getDeviceId(1));
                    }
                } else {
                    map.put("imei1", mTelephonyManager.getDeviceId(0));
                    map.put("imei2", mTelephonyManager.getDeviceId(1));
                }
            } else {
                map.put("imei1", mTelephonyManager.getDeviceId(0));
                map.put("imei2", mTelephonyManager.getDeviceId(1));

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return map;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    public static Map getIMEIforO(Context context) {
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager tm = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        String imei1 = tm.getImei(0);
        String imei2 = tm.getImei(1);
        if (TextUtils.isEmpty(imei1) && TextUtils.isEmpty(imei2)) {

            map.put("imei1", tm.getMeid()); //如果CDMA制式手机返回MEID
        } else {
            map.put("imei1", imei1);

            map.put("imei2", imei2);
        }
        return map;
    }


    public static String getIMEI(Context ctx) {
        String imei = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {  //M以下 直接获取
            imei = getImeiOrMeid(ctx);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //O系统以下
            Map imeiMaps = getImeiAndMeid(ctx);
            imei = getTransform(imeiMaps);
        } else {
            Map imeiMaps = getIMEIforO(ctx);

            imei = getTransform(imeiMaps);
        }

        return imei;
    }


    private static String getTransform(Map imeiMaps) {
        String imei = "";
        if (imeiMaps != null) {
            String imei1 = (String) imeiMaps.get("imei1");
            if (TextUtils.isEmpty(imei1)) {
                return imei;
            }
            String imei2 = (String) imeiMaps.get("imei2");
            if (imei2 != null) {
                if (imei1.trim().length() == 15 && imei2.trim().length() == 15) {
                    //如果两个位数都是15。说明都是有效IMEI。根据从大到小排列
                    long i1 = Long.parseLong(imei1.trim());
                    long i2 = Long.parseLong(imei2.trim());
                    if (i1 > i2) {
                        imei = imei2 + ";" + imei1;
                    } else {
                        imei = imei1 + ";" + imei2;
                    }

                } else {  //
                    if (imei1.trim().length() == 15) {
                        //如果只有imei1是有效的
                        imei = imei1;
                    } else if (imei2.trim().length() == 15) {
                        //如果只有imei2是有效的
                        imei = imei2;
                    } else {
                        //如果都无效那么都为meid。只取一个就可以
                        imei = imei1;
                    }

                }
            } else {
                imei = imei1;
            }
        }
        return imei;
    }

    public static String getAndroidID(Context ctx) {
        return Settings.System.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID );
    }


    public static String getIMEIReflect(Context ctx) {
        String imei = "";
        try {
            TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            imei = (String) method.invoke(manager, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(imei)) {
            imei = getAYLDeviceId(); //13 digits // 新建相同型号模拟器相同
            String serial;
            long imeiAndroidIDHash = (imei + getAndroidID(ctx)).hashCode();
            try {
                serial = Build.class.getField("SERIAL").get(null).toString();
                return new UUID(imeiAndroidIDHash, serial.hashCode()).toString();
            } catch (Exception exception) {
                //serial需要一个初始化
                serial = "imoneyhw"; // 随便一个初始化
            }
            imei = new UUID(imeiAndroidIDHash, serial.hashCode()).toString();

        }

        return imei;
    }

    @NotNull
    private static String getAYLDeviceId() {
        return "35" +
                //DeviceUtil.Companion.getAndroidID() +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
    }
}
