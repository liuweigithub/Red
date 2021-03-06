//package com.liuwei1995.red;
//
//import android.Manifest;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.text.Html;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.text.style.URLSpan;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.accessibility.AccessibilityManager;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.blankj.utilcode.util.AppUtils;
//import com.liuwei1995.red.entity.AppEntity;
//import com.liuwei1995.red.service.DingDingAccessibilityService;
//import com.liuwei1995.red.service.FloatWindowService;
//import com.liuwei1995.red.service.NotificationService;
//import com.liuwei1995.red.service.OFOAccessibilityService;
//import com.liuwei1995.red.service.QQAccessibilityService;
//import com.liuwei1995.red.service.WeChatAccessibilityService;
//import com.liuwei1995.red.service.sensor.SensorService;
//import com.liuwei1995.red.service.util.qq.presenter.QQPresenter;
//import com.liuwei1995.red.service.util.wechat.presenter.WechatPresenter;
//import com.liuwei1995.red.util.permission.AndPermission;
//import com.liuwei1995.red.util.permission.PermissionListener;
//import com.liuwei1995.red.util.permission.RationaleListener;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import static android.content.ContentValues.TAG;
//import static com.liuwei1995.red.BaseApplication.QQ_map;
//import static com.liuwei1995.red.BaseApplication.WeChat_map;
//
//public class MainActivity1 extends Activity {
//    private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//    private TextView text_WeChat_accessible;
//    private TextView text_QQ_accessible;
//    private TextView text_ofo_accessible;
//    private TextView text_dingding_accessible;
//    private TextView text_notification_listener;
//    private TextView text_start_SensorService;
//    TextView tv_content;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        startRequestPermissions();
//        receiver = new SensorServiceReceiver();//com.alibaba.android.rimet
//        registerReceiver(receiver,new IntentFilter(ACTION_SensorServiceReceiver_CHANGE));
//        text_WeChat_accessible = (TextView) findViewById(R.id.text_WeChat_accessible);
//        text_QQ_accessible = (TextView) findViewById(R.id.text_QQ_accessible);
//        text_ofo_accessible = (TextView) findViewById(R.id.text_ofo_accessible);
//        text_dingding_accessible = (TextView) findViewById(R.id.text_dingding_accessible);
//        text_notification_listener = (TextView) findViewById(R.id.text_notification_listener);
//        text_start_SensorService = (TextView) findViewById(R.id.text_start_SensorService);
//         tv_content = (TextView) findViewById(R.id.tv_content);
//        updateServiceStatus();
//    }
//
//    private void startRequestPermissions() {
//        AndPermission.with(this)
//                .setPermission(Manifest.permission.CAMERA,Manifest.permission.SEND_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .setCallback(new PermissionListener() {
//                    @Override
//                    protected void onSucceed(Context context, int requestCode, @NonNull List<String> grantPermissions) {
//                        Log.e(TAG, "onSucceed: "+grantPermissions.toString());
//                    }
//                    @Override
//                    protected void onFailed(@NonNull Context context,int requestCode, @NonNull List<String> deniedPermissions,@NonNull List<String> deniedDontRemindList,@NonNull RationaleListener rationale) {
//                        Log.e(TAG, "onFailed: " +deniedPermissions.toString()
//                        +"\n"+deniedDontRemindList.toString()
//                        );
//                        if(!deniedDontRemindList.isEmpty())
//                            rationale.showSettingDialog(context,rationale,deniedDontRemindList);
//                    }
//
//                    @Override
//                    protected void onCancel(Context context, int requestCode, @NonNull List<String> deniedPermissions, List<String> deniedDontRemindList) {
//                        Log.e(TAG, "onCancel: "+deniedPermissions.toString()+"\n"+deniedDontRemindList.toString());
//                    }
//                })
//                .start();
//    }
//
//    /**
//     * 提示内容
//     */
//    private void setHintContent() {
//        String wechatAppversionname = AppUtils.getAppVersionName(this, WechatPresenter.WECHAT_PACKAGENAME);
//        String qqAppVersionName = AppUtils.getAppVersionName(this, QQPresenter.QQ_PACKAGENAME);
//        String htmlLinkText = "<li>";
//                htmlLinkText += "<font color=\\\"#FF0000\\\"> 温馨提示：</font>";
//                htmlLinkText += "<p> 你当前的微信版本号为："+wechatAppversionname+"</p>";
//                if(WeChat_map.get(wechatAppversionname) != null){
//                    htmlLinkText += "<p>下面的微信版本也支持可点击下载</p>";
//                }else{
//                    htmlLinkText += "<p>现在只能支持下面的微信版本可点击下载</p>";
//                }
//                for (String key : WeChat_map.keySet()){
//                    AppEntity appEntity = WeChat_map.get(key);
//                    htmlLinkText += "<a href=\""+appEntity.getUrl()+"\" title=\"微信"+appEntity.getVersionName()+"下载\">微信"+appEntity.getVersionName()+"下载</a>";
//                }
//                    htmlLinkText += "<p> 你当前的QQ版本号为："+qqAppVersionName+"</p>";
//                if(QQ_map.get(qqAppVersionName) != null){
//                    htmlLinkText += "<p>下面的QQ版本也支持可点击下载</p>";
//                }else{
//                    htmlLinkText += "<p>现在只能支持下面的QQ版本可点击下载</p>";
//                }
//                for (String key : QQ_map.keySet()){
//                    AppEntity appEntity = QQ_map.get(key);
//                    htmlLinkText += "<a href=\""+appEntity.getUrl()+"\" title=\"QQ"+appEntity.getVersionName()+"下载\">QQ"+appEntity.getVersionName()+"下载</a>";
//                }
//                    htmlLinkText += "</li>";
//        tv_content.setText(Html.fromHtml(htmlLinkText));
////        //此行必须有
//        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
//        CharSequence text = tv_content.getText();
//        if(text instanceof Spannable){
//            int end = text.length();
//            Spannable sp = (Spannable)tv_content.getText();
//            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
//            SpannableStringBuilder style = new SpannableStringBuilder(text);
//            style.clearSpans();//should clear old spans
//            for(URLSpan url : urls){
//                ClickURLSpan myURLSpan = new ClickURLSpan(url.getURL());
//                style.setSpan(myURLSpan,sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//            tv_content.setText(style);
//        }
//    }
//
//    private  class ClickURLSpan extends ClickableSpan{
//
//        private String mUrl;
//        ClickURLSpan(String url) {
//            mUrl = url;
//        }
//        @Override
//        public void onClick(View widget) {
//            Uri uri = Uri.parse(mUrl);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
//        }
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        updateServiceStatus();
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//    }
//
//    private void updateServiceStatus() {//检测手机是否支持计歩
//        boolean WeChatServiceEnabled = false;
//        boolean QQServiceEnabled = false;
//        boolean OFOServiceEnabled = false;
//        boolean DingServiceEnabled = false;
//
//        AccessibilityManager accessibilityManager =(AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
//        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
//        if(accessibilityServices != null)
//        for (AccessibilityServiceInfo info : accessibilityServices) {
//            if(info.getId() == null)continue;
//            if(info.getId().contains(WeChatAccessibilityService.class.getSimpleName())){
//                WeChatServiceEnabled = true;
//            }else if(info.getId().contains(QQAccessibilityService.class.getSimpleName())){
//                QQServiceEnabled = true;
//            }
//            else if (info.getId().contains(OFOAccessibilityService.class.getSimpleName())){
//                OFOServiceEnabled = true;
//            }
//            else if (info.getId().contains(DingDingAccessibilityService.class.getSimpleName())){
//                DingServiceEnabled = true;
//            }
//        }
//        text_WeChat_accessible.setText(WeChatServiceEnabled ? "微信辅助服务已开启" : "微信辅助服务已关闭");
//        text_QQ_accessible.setText(QQServiceEnabled ? "QQ辅助服务已开启" : "QQ辅助服务已关闭");
//        text_ofo_accessible.setText(OFOServiceEnabled ? "OFO辅助服务已开启" : "OFO辅助服务已关闭");
//        text_dingding_accessible.setText(DingServiceEnabled ? "钉钉辅助服务已开启" : "钉钉辅助服务已关闭");
//        boolean serviceNotificationEnabled = false;
//        String string = Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
//        if(string != null)
//        if (string.contains(NotificationService.class.getName())) {
//            serviceNotificationEnabled = true;
//        }
//        text_notification_listener.setText(serviceNotificationEnabled ? "通知栏辅助服务已开启" : "通知栏辅助服务已关闭");
//        setHintContent();
//    }
//
//    public void onButtonClicked(View view) {
//        switch (view.getId()){
//            case R.id.button_WeChat_accessible:
//            case R.id.button_QQ_accessible:
//            case R.id.button_ofo_accessible:
//            case R.id.button_dingding_accessible:
//                startActivity(mAccessibleIntent);
//                break;
//            case R.id.button_notification_listener:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(MainActivity1.this, "手机的系统不支持此功能", Toast.LENGTH_SHORT)
//                            .show();
//                }
//                break;
////            case R.id.button_vpn:
//////
////                Toast.makeText(this, "小组件", Toast.LENGTH_SHORT).show();
////                sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
////
////                break;
//            case R.id.button_start_SensorService:
//                startService(new Intent(this, SensorService.class));
//                break;
//            case R.id.button_float:
//                askForPermission();
//                break;
//
//        }
//    }
//
//    private static final int OVERLAY_PERMISSION_REQ_CODE = 100;
//    /**
//     * 请求用户给予悬浮窗的权限
//     */
//    public void askForPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "当前无权限，请授权！", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
//            } else {
//                startService(new Intent(MainActivity1.this, FloatWindowService.class));
//            }
//        }else {
//            startService(new Intent(MainActivity1.this, FloatWindowService.class));
//        }
//    }
//
//    /**
//     * 用户返回
//     *
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK && requestCode == OVERLAY_PERMISSION_REQ_CODE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!Settings.canDrawOverlays(this)) {
//                    Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
//                    //启动FxService
//                    startService(new Intent(MainActivity1.this, FloatWindowService.class));
//                }
//            }
//
//        }
//    }
//
//    public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    class SensorServiceReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ACTION_SensorServiceReceiver_CHANGE.equals(intent.getAction())){
//                String data = intent.getStringExtra("data");
//                text_start_SensorService.setText(format.format(new Date())+"\t:"+data);
//            }
//        }
//    }
//
//    public static final String ACTION_SensorServiceReceiver_CHANGE = "ACTION_SensorServiceReceiver_CHANGE";
//
//    private SensorServiceReceiver receiver = null;
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(receiver != null){
//            unregisterReceiver(receiver);
//            receiver = null;
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //方式一：将此任务转向后台
////        if (keyCode == KeyEvent.KEYCODE_BACK) {
////            moveTaskToBack(true);
////        }
//        //方式二：返回手机的主屏幕
//    /*Intent intent = new Intent(Intent.ACTION_MAIN);
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    intent.addCategory(Intent.CATEGORY_HOME);
//    startActivity(intent);*/
//        return super.onKeyDown(keyCode,event);
//    }
//}
