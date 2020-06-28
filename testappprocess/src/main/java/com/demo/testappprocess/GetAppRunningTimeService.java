package com.demo.testappprocess;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class GetAppRunningTimeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }
    private Handler handler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            String foregroundActivityName = getForegroundActivityName(getApplicationContext());
            Log.e("前台进程-",foregroundActivityName);
            handler.postDelayed(r, 5000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(r, 5000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("AppService","onDestroy");
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static final long END_TIME = System.currentTimeMillis();
    private static final long TIME_INTERVAL = 7 * 24 * 60 * 60 * 1000L;
    private static final long START_TIME = END_TIME - TIME_INTERVAL;
    public static String getForegroundActivityName(Context context) {
        String currentClassName = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            currentClassName = manager.getRunningTasks(1).get(0).topActivity.getPackageName();
        } else {
            UsageStats initStat = getForegroundUsageStats(context, START_TIME, END_TIME);
            if (initStat != null) {
                currentClassName = initStat.getPackageName();
            }
        }
        return currentClassName;
    }

    private static UsageStats getForegroundUsageStats(Context context, long startTime, long endTime) {
        UsageStats usageStatsResult = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats> usageStatses = getUsageStatsList(context, startTime, endTime);
            if (usageStatses == null || usageStatses.isEmpty()) return null;
            for (UsageStats usageStats : usageStatses) {
                if (usageStatsResult == null || usageStatsResult.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    usageStatsResult = usageStats;
                }
            }
        }
        return usageStatsResult;
    }

    public static List<UsageStats> getUsageStatsList(Context context, long startTime, long endTime) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager manager = (UsageStatsManager) context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
            //UsageStatsManager.INTERVAL_WEEKLY，UsageStatsManager的参数定义了5个，具体查阅源码
            List<UsageStats> usageStatses = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
            if (usageStatses == null || usageStatses.size() == 0) {// 没有权限，获取不到数据
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
                return null;
            }
            return usageStatses;
        }
        return null;
    }
}
