package com.liuwei1995.red.service.util.ofo.presenter;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.MainActivity;
import com.liuwei1995.red.service.OFOAccessibilityService;
import com.liuwei1995.red.service.OFOEntitySaveIntentService;

import java.util.List;

/**
 * Created by liuwei on 2017/5/26 14:44
 */

public class OFOOtherPresenter extends OFOPresenter {

    private static final String TAG = "OFOOtherPresenter";

    private AccessibilityService accessibilityService;

    public OFOOtherPresenter(AccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
    }
    private static final int RED_RECEIVER_CODE = 5;

    private NotificationManager notificationManager;

    private RedReceiver redReceiver;

    private int id = 15;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RED_RECEIVER_CODE:
                    if (notification == null) {
                        setSelfNotification();
                    }
                    if (notificationManager == null) {
                        notificationManager = (NotificationManager) accessibilityService.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    }
                    notificationManager.notify(id, notification);
                    accessibilityService.startForeground(id, notification);
                    break;
                default:
                    break;
            }
        }
    };
    private synchronized void sendEmptyMessageDelayed(int what){
        handler.removeMessages(what);
        handler.sendEmptyMessageDelayed(what,500);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!isOpen)return;
        switch (event.getEventType()){//
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                if(rootInActiveWindow != null){
                    List<AccessibilityNodeInfo> accessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId("so.ofo.labofo:id/carno");
                    if(accessibilityNodeInfosByViewId != null && !accessibilityNodeInfosByViewId.isEmpty()){
                        for (int i = 0; i < accessibilityNodeInfosByViewId.size(); i++) {
                            AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByViewId.get(i);
                            CharSequence text = accessibilityNodeInfo.getText();
                            if(text != null && !TextUtils.isEmpty(text.toString())){
                                if(text.toString().equals("请输入车牌号")){

                                }//android.app.Dialog
                            }
                            AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
                            if(parent != null){
                                List<AccessibilityNodeInfo> btn_sent_list = parent.findAccessibilityNodeInfosByViewId("so.ofo.labofo:id/btn_sent");
                                if(btn_sent_list != null && !btn_sent_list.isEmpty()){
                                    AccessibilityNodeInfo btn_sent = btn_sent_list.get(btn_sent_list.size() - 1);
                                    if (btn_sent.isClickable()){
                                        Log.e(TAG, "onAccessibilityEvent: 点击了提交按钮");
                                    }
                                }
                            }

                        }
                    }else {
                        List<AccessibilityNodeInfo> unlocking_bike_code_left_车牌号_list = rootInActiveWindow.findAccessibilityNodeInfosByViewId("so.ofo.labofo:id/unlocking_bike_code_left");
                        if(unlocking_bike_code_left_车牌号_list != null && !unlocking_bike_code_left_车牌号_list.isEmpty()){
                            AccessibilityNodeInfo unlocking_bike_code_left_车牌号 = unlocking_bike_code_left_车牌号_list.get(unlocking_bike_code_left_车牌号_list.size() - 1);
                            AccessibilityNodeInfo parent = unlocking_bike_code_left_车牌号.getParent();
                            if(parent != null){
                                List<AccessibilityNodeInfo> unlocking_bike_code_list = parent.findAccessibilityNodeInfosByViewId("so.ofo.labofo:id/unlocking_bike_code");
                                List<AccessibilityNodeInfo> unlock_code_list = parent.findAccessibilityNodeInfosByText("解锁码");
                                if(unlocking_bike_code_list != null && !unlocking_bike_code_list.isEmpty()
                                        &&unlock_code_list != null && !unlock_code_list.isEmpty()){
                                    AccessibilityNodeInfo parentParent = parent.getParent();
                                    if (parentParent != null){
                                        List<AccessibilityNodeInfo> ll_unlock_pwd_list = parentParent.findAccessibilityNodeInfosByViewId("so.ofo.labofo:id/ll_unlock_pwd");
                                        if(ll_unlock_pwd_list != null && !ll_unlock_pwd_list.isEmpty()){
                                            AccessibilityNodeInfo ll_unlock_pwd = ll_unlock_pwd_list.get(ll_unlock_pwd_list.size() - 1);
                                            int childCount = ll_unlock_pwd.getChildCount();
                                            if(childCount > 0){
                                                String pwd = "";
                                                for (int i = 0; i < childCount; i++) {
                                                    AccessibilityNodeInfo child = ll_unlock_pwd.getChild(i);
                                                    CharSequence text = child.getText();
                                                    if(text != null && !TextUtils.isEmpty(text)){
                                                        try {
                                                            Integer integer = Integer.valueOf(text.toString());
                                                            pwd += integer;
                                                        } catch (NumberFormatException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                if(pwd.length() == 4){
                                                    AccessibilityNodeInfo unlocking_bike_code = unlocking_bike_code_list.get(unlocking_bike_code_list.size() - 1);
                                                    CharSequence text = unlocking_bike_code.getText();
                                                    if(text != null && !TextUtils.isEmpty(text.toString())){
                                                        try {
                                                            Long aLong = Long.valueOf(text.toString());
                                                            OFOEntitySaveIntentService.startActionBaz(getApplicationContext(),aLong+"",pwd);
                                                        } catch (NumberFormatException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Log.e(TAG, "onAccessibilityEvent: 类型窗口状态改变" );
                break;//so.ofo.labofo.activities.journey.CaptureActivity
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.e(TAG, "onAccessibilityEvent: 类型窗口内容改变了" );
                break;//so.ofo.labofo.activities.journey.MainActivity
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Log.e(TAG, "onAccessibilityEvent: 手势检测结束");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                Log.e(TAG, "onAccessibilityEvent: 开始触摸交互类型");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.e(TAG, "onAccessibilityEvent: TYPE_VIEW_SCROLLED  类型视图滚动");
                break;
            default:
                break;
        }
    }

    public AccessibilityNodeInfo getRootInActiveWindow(){
        return accessibilityService.getRootInActiveWindow();
    }
    private void registerReceiver(RedReceiver redReceiver, IntentFilter filter) {
        accessibilityService.registerReceiver(redReceiver,filter);
    }
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        IntentFilter filter = new IntentFilter();
        filter.addAction(open);
        filter.addAction(close);
        redReceiver = new RedReceiver();
        registerReceiver(redReceiver, filter);
        Log.e(TAG, "onServiceConnected: " + TAG);
        isOpen = true;
        handler.sendEmptyMessage(RED_RECEIVER_CODE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (redReceiver != null) {
            unregisterReceiver(redReceiver);
        }
        if (notification != null && notificationManager != null) {
            notificationManager.cancel(id);
        }
    }
    private void unregisterReceiver(RedReceiver redReceiver) {
        accessibilityService.unregisterReceiver(redReceiver);
    }
    public Context getApplicationContext(){
        return accessibilityService.getApplicationContext();
    }
    public String getPackageName(){
        return getApplicationContext().getPackageName();
    }
    @Override
    public void onInterrupt() {
    }
    public static String open = OFOAccessibilityService.class.getSimpleName()+".open";
    public static String close = OFOAccessibilityService.class.getSimpleName()+".close";
    private Notification notification;
    RemoteViews remoteViews;
    //自定义通知
    protected void setSelfNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews = new RemoteViews(getPackageName(), R.layout.remote_view);
        remoteViews.setImageViewResource(R.id.icon, R.mipmap.ic_launcher_round);
        remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "OFO监听内容辅助功能已开启");
        remoteViews.setOnClickPendingIntent(R.id.icon, pendingIntent);
        PendingIntent broadcast_open = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(open), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tv_open, broadcast_open);
        PendingIntent broadcast_close = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(close), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tv_close, broadcast_close);
        notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContent(remoteViews)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setTicker("来了一条消息")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
//                .setContentIntent(pendingIntent)
                .setAutoCancel(false)//false 自己维护通知的消失  true  点击后消失
                .build();
//        notification.flags |= Notification.FLAG_INSISTENT; // 一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
    }
    private boolean isOpen = false;
    public class RedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (remoteViews != null)
                if (intent.getAction().equals(open)) {//开
                    isOpen = true;
                    remoteViews.setViewVisibility(R.id.tv_close, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.tv_open, View.GONE);
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "OFO监听内容辅助功能已开启");
                } else if (intent.getAction().equals(close)) {//关
                    isOpen = false;
                    remoteViews.setViewVisibility(R.id.tv_close, View.GONE);
                    remoteViews.setViewVisibility(R.id.tv_open, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.tv_Auxiliary_function, "OFO监听内容辅助功能已关闭");
                }
            handler.sendEmptyMessage(RED_RECEIVER_CODE);
        }

    }


}
