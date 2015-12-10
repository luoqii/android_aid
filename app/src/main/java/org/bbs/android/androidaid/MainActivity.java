package org.bbs.android.androidaid;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            view.findViewById(R.id.open_adb).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.open_adb){
                AidManager.getInstance().setAid(new OpenAdbAid()).start();
                Intent i = new Intent(Settings.ACTION_SETTINGS);
                startActivity(i);
//                getActivity().finish();
            }
            if (v.getId() == R.id.enable){
                Intent acc = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(acc);
            }
        }
    }


    public static ComponentName getTopActivity(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        return taskInfo.get(0).topActivity;
    }

    public static interface IAid {
        public void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event);
    }

    public static class OpenAdbAid implements IAid {

        ComponentName mLastComponentName = null;

        @Override
        public void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {

            Log.d(TAG, "event: " + event);
//            Log.d(TAG, "event: " + toString(event));

            AccessibilityNodeInfo node = event.getSource();
            CharSequence packageName = event.getPackageName();
            int type = event.getEventType();
            int windowId = event.getWindowId();

            if (null == node){
                Log.w(TAG, "node is null, ^~^");
                return;
            }

            if ("com.android.settings".equals(packageName)){
                //http://stackoverflow.com/questions/3873659/android-how-can-i-get-the-current-foreground-activity-from-a-service
                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    mLastComponentName = new ComponentName(
                            event.getPackageName().toString(),
                            event.getClassName().toString()
                    );
                    Log.i(TAG, "last activity: " + mLastComponentName);

                    // from QueryController
                    if (event.getText() != null && event.getText().size() > 0) {
                        if(event.getText().get(0) != null) {
                            String lastActivityName = event.getText().get(0).toString();
//                            Log.i(TAG, "last activity name: " + lastActivityName);
                        }
                    }

                }

                if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == type){

                    AccessibilityNodeInfo rootNode = node;
                    rootNode = service.getRootInActiveWindow();

                    List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId("com.android.settings:id/dashboard");
//                    nodes = rootNode.findAccessibilityNodeInfosByText("设置");
                    boolean settingActivity = nodes != null && nodes.size() > 0;

                    nodes = rootNode.findAccessibilityNodeInfosByText("开发者选项");
                    boolean devActivity = nodes != null && nodes.size() == 1;

                    settingActivity = new ComponentName("com.android.settings", "com.android.settings.Settings").equals(mLastComponentName);
                    devActivity = new ComponentName("com.android.settings", "com.android.settings.SubSettings").equals(mLastComponentName);

                    Log.d(TAG, "settingActivity: " + settingActivity + " devActivity: " + devActivity);
                    if (settingActivity){
                        Log.d(TAG, "scroll to bottom");
                        nodes = rootNode.findAccessibilityNodeInfosByViewId("com.android.settings:id/dashboard");
                        nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

                        nodes = rootNode.findAccessibilityNodeInfosByText("开发者选项");
                        if (null != nodes && nodes.size() > 0) {
                            Log.d(TAG, "open developer's setting");
                            nodes.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    } else if (devActivity) {
                        nodes = rootNode.findAccessibilityNodeInfosByText("开启");
                        if (null != nodes && nodes.size() > 0) {
                            Log.d(TAG, nodes.size() + " node found.");
                            Log.d(TAG, "open developer's option");
                            nodes.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        } else {
                            return;
                        }
                    }
                }
            }

//        node.recycle();
        }

        private ActivityInfo tryGetActivity(AccessibilityService service, ComponentName componentName) {
            try {
                return service.getPackageManager().getActivityInfo(componentName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }

        String toString(AccessibilityEvent event){
            String str = "";
            str += "eventtype: " + AccessibilityEvent.eventTypeToString(event.getEventType());
            str += "\nsource: " + event.getSource();
            str += "\npackagename: " + event.getPackageName();
            str += "\nclassname: " + event.getClassName();

            return str;
        }
    }

    public static class AidManager implements Runnable {
        private static AidManager sInstance;

        private final Handler mHandler;
        private IAid mAid;
        private boolean mTimeOut;
        private long mSessionTime = 2 * 1000;

        public static AidManager getInstance(){
            if (null == sInstance){
                sInstance = new AidManager();
            }

            return sInstance;
        }

        private AidManager(){
            mHandler = new Handler(Looper.getMainLooper());
        }

        public AidManager setAid(IAid aid){
            mAid = aid;

            return this;
        }

        public void start() {
            mHandler.removeCallbacks(this);
            mTimeOut = false;
        }

        public void stop(){
            mTimeOut = true;
        }

        public void bindService(Intent service, ServiceConnection conn, int flags) {
            Log.d(TAG, "onUnbind() service: " + service + " conn: " + conn + " flags: " + flags);
        }

        public void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
            if (mTimeOut){
                return;
            }
            
            if (null != mAid){
                mAid.onAccessibilityEvent(service, event);
                mHandler.postDelayed(this, mSessionTime);
            }
        }

        @Override
        public void run() {
            stop();
        }


        public void onInterrupt() {
            Log.d(TAG, "onInterrupt()");
        }

        public void onUnbind(Intent intent) {
            Log.d(TAG, "onUnbind() intent: " + intent);
        }


    }
}
