package com.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class LockScreenActivity extends Activity {

    private static final int PICTURE_WIDTH = 48;
    /**
     * 滑动到解锁图标周围的有效距离
     */
    private static final int EFFECTIVE_DISTANCE = 48;
    private int mIntWidth;
    private int mIntHeight;
    private ImageView mImvScreenKey;
    private ImageView mImvUnlockToHome;
    /**
     * 解锁按钮的位置：固定
     */
    private int mIntHomeX, mIntHomeY;
    /**
     * 滑动钥匙的位置：可变
     */
    private int[] mIntKeyPosition;
    private LayoutParams mLpKeyParams;
    public static final int FLAG_HOME_KEY_DISPATCHED = 0x80000000;
    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onAttachedToWindow();
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /**
         * 屏蔽HOME键
         */
        getWindow().setFlags(FLAG_HOME_KEY_DISPATCHED, FLAG_HOME_KEY_DISPATCHED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        if (getIntent() != null && getIntent().hasExtra("kill") && getIntent().getExtras().getInt("kill") == 1) {
            finish();
        }
        try {
            // initialize receiver
            startService(new Intent(this, ScreenListenerService.class));
            initPhoneListener();
            initImvUnlockKey();
            /**
             * 滑动图标操作
             */
            mImvScreenKey.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mLpKeyParams = (LayoutParams) v.getLayoutParams();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            int[] hom_pos = new int[2];
                            mImvUnlockToHome.getLocationOnScreen(hom_pos);
                            mIntHomeX = hom_pos[0];
                            mIntHomeY = hom_pos[1];
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int x_cord = (int) event.getRawX();
                            int y_cord = (int) event.getRawY();
                            mLpKeyParams.leftMargin = x_cord;
                            mLpKeyParams.topMargin = y_cord;
                            mImvScreenKey.getLocationOnScreen(mIntKeyPosition);
                            v.setLayoutParams(mLpKeyParams);

                            if (Math.abs(x_cord - mIntHomeX) <= EFFECTIVE_DISTANCE
                                    && Math.abs(mIntHomeY - y_cord) <= EFFECTIVE_DISTANCE) {
                                v.setVisibility(View.GONE);
                                finish();
                            } else {
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            int x_cord1 = (int) event.getRawX();
                            int y_cord2 = (int) event.getRawY();

                            if (Math.abs(x_cord1 - mIntHomeX) <= EFFECTIVE_DISTANCE
                                    && Math.abs(mIntHomeY - y_cord2) <= EFFECTIVE_DISTANCE) {
                                v.setVisibility(View.GONE);
                                finish();
                            } else {
                                v.setLayoutParams(getKeyInitialLayoutParam(v));
                            }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }

    /**
     * 设置电话监听
     */
    private void initPhoneListener(){
        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 设置可滑动图标的初始位置
     */
    private void initImvUnlockKey(){
        mIntWidth = getWindowManager().getDefaultDisplay().getWidth();
        mIntHeight = getWindowManager().getDefaultDisplay().getHeight();

        mImvScreenKey = (ImageView) findViewById(R.id.imv_key);
        mImvScreenKey.setLayoutParams(getKeyInitialLayoutParam(mImvScreenKey));
        mIntKeyPosition = new int[2];

        /////////////////////////////////////////////////////////////
        mImvUnlockToHome = (ImageView) findViewById(R.id.unlock);
        MarginLayoutParams lockParam = new MarginLayoutParams(mImvUnlockToHome.getLayoutParams());
        lockParam.setMargins((mIntWidth / 30) * 25 - PICTURE_WIDTH, (mIntHeight / 32) * 24, 0, 0);
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(lockParam);
        mImvUnlockToHome.setLayoutParams(layout);
    }

    private RelativeLayout.LayoutParams getKeyInitialLayoutParam(View view){
        MarginLayoutParams keyParams = new MarginLayoutParams(view.getLayoutParams());
        keyParams.setMargins((mIntWidth / 30) * 5,(mIntHeight / 32) * 24,0,0);
        return new RelativeLayout.LayoutParams(keyParams);
    }

    class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    finish();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_POWER)
                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP) || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            return true;
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) {
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
            return true;
        }
        return false;
    }

    public void onDestroy() {
        super.onDestroy();
    }
}