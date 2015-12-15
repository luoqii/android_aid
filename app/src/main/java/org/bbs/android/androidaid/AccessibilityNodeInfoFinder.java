package org.bbs.android.androidaid;

import android.view.accessibility.AccessibilityNodeInfo;


import ext.com.android.uiautomator.core.QueryController;
import ext.com.android.uiautomator.core.UiAutomatorBridge;
import ext.com.android.uiautomator.core.UiSelector;

/**
 * Created by bysong on 15-12-14.
 */
public class AccessibilityNodeInfoFinder {

    public static AccessibilityNodeInfo find(AccessibilityNodeInfo topNode, UiSelector uiSelector){
        return new QueryController(new UiAutomatorBridge(topNode)).findAccessibilityNodeInfo(uiSelector);
    }

}
