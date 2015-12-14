package ext.com.android.uiautomator.core;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by bysong on 15-12-14.
 */
public class UiAutomatorBridge {

    private final AccessibilityNodeInfo mTopNode;

    public UiAutomatorBridge(AccessibilityNodeInfo topNode){
        mTopNode = topNode;
    }

    public void waitForIdle() {
        ;; // TODO
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        return mTopNode;
    }
}