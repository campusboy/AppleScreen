package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.screen.LockScreenActivity;

/**
 * 广播接收者，初始化接收开机完成广播<xml>
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {
    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            wasScreenOn = false;
            Intent intent11 = new Intent(context, LockScreenActivity.class);
            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent11);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            wasScreenOn = true;
            Intent intent11 = new Intent(context, LockScreenActivity.class);
            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent11);
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent11 = new Intent(context, LockScreenActivity.class);
            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent11);
        }
    }
}
