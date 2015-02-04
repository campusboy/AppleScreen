package com.screen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import com.receiver.ScreenBroadcastReceiver;

/**
 * 禁止系统锁屏界面，为广播接收添加两个可接收的广播：屏幕亮/屏幕暗
 */
public class ScreenListenerService extends Service {
    BroadcastReceiver mReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        /**
         * 禁止系统锁屏界面
         */
        ((KeyguardManager) getSystemService(KEYGUARD_SERVICE)).newKeyguardLock("IN").disableKeyguard();
        /**
         * 添加新的广播接收设置
         */
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new ScreenBroadcastReceiver();
        registerReceiver(mReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
