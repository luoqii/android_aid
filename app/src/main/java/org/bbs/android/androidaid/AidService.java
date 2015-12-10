package org.bbs.android.androidaid;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by bysong on 15-12-10.
 */
public class AidService extends AccessibilityService {
    private static final String TAG = AidService.class.getSimpleName();

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        MainActivity.AidManager.getInstance().bindService(service, conn, flags);
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MainActivity.AidManager.getInstance().onUnbind(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        MainActivity.AidManager.getInstance().onAccessibilityEvent(this, event);
    }

    @Override
    public void onInterrupt() {
        MainActivity.AidManager.getInstance().onInterrupt();
    }
}
