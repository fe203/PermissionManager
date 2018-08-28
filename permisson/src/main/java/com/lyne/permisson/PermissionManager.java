package com.lyne.permisson;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * Created by LJ on 2016/6/24.
 */
public class PermissionManager {

    public static final int PERMISSION_CAMERA = 100;
    public static final int PERMISSION_RECORD_AUDIO = 101;
    public static final int PERMISSION_FINE_LOCATION = 102;
    public static final int PERMISSION_READ_CONTACTS = 103;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 104;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 105;
    public static final int PERMISSION_SYSTEM_ALERT_WINDOW = 106;
    public static final int PERMISSION_SEND_SMS = 107;
    public static final int PERMISSION_CALL_PHONE = 108;

    private static PermissionManager inst;
    private OnPermissionRequestListener onPermissionRequestListener;

    private long lastReqPermissionTime;

    public static PermissionManager getInstance() {
        if (inst == null){
            inst = new PermissionManager();
        }
        return inst;
    }

    public void setOnPermissionRequestListener(OnPermissionRequestListener onPermissionRequestListener){
        this.onPermissionRequestListener = onPermissionRequestListener;
    }

    public OnPermissionRequestListener getOnPermissionRequestListener() {
        return onPermissionRequestListener;
    }

    public void requestPermission(Activity activity, int permissionType){
        String[] permissions = getPermissionStringByType(permissionType);
        if (permissions == null){
            return;
        }

        lastReqPermissionTime = System.currentTimeMillis();
        ActivityCompat.requestPermissions(activity, permissions, permissionType);
    }

    public boolean isPermissionGranted(Context context, int permissionType){
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
            return true;
        }
        String[] permissions = getPermissionStringByType(permissionType);
        if (permissions == null){
            return false;
        }
        for (String permission : permissions){
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        OnPermissionRequestListener listener = getOnPermissionRequestListener();
        if (listener != null && grantResults.length > 0) {

            for (int result : grantResults){
                if (result == PackageManager.PERMISSION_DENIED){
                    listener.onPermissionRequestFailed(requestCode, isRejectByUser());
                    return;
                }
            }
            listener.onPermissionRequestSucceed(requestCode);
        }
    }


    /**
     * 禁止外部乱用
     * @return
     */
    private boolean isRejectByUser(){
        return System.currentTimeMillis() - lastReqPermissionTime > 1000;
    }

    private String[] getPermissionStringByType(int permission){
        switch (permission){
            case PERMISSION_CAMERA:
                return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            case PERMISSION_RECORD_AUDIO:
                return new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            case PERMISSION_FINE_LOCATION:
                return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            case PERMISSION_CALL_PHONE:
                return new String[]{Manifest.permission.CALL_PHONE};
            case PERMISSION_READ_CONTACTS:
                return new String[]{Manifest.permission.READ_CONTACTS};
            case PERMISSION_READ_EXTERNAL_STORAGE:
                return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            case PERMISSION_SYSTEM_ALERT_WINDOW:
                return new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};
            case PERMISSION_SEND_SMS:
                return new String[]{Manifest.permission.SEND_SMS};
            default:
                return null;
        }
    }

    public String getPermissionName(int permissionType){
        switch (permissionType){
            case PERMISSION_CAMERA:
                return "相机或存储";
            case PERMISSION_RECORD_AUDIO:
                return "麦克风或存储";
            case PERMISSION_FINE_LOCATION:
                return "定位";
            case PERMISSION_CALL_PHONE:
                return "拨打电话";
            case PERMISSION_READ_CONTACTS:
                return "通讯录";
            case PERMISSION_READ_EXTERNAL_STORAGE:
                return "访问文件";
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                return "存储";
            case PERMISSION_SYSTEM_ALERT_WINDOW:
                return "啊啊";
            case PERMISSION_SEND_SMS:
                return "发送信息";
            default:
                return "";
        }
    }

    private PermissionManager(){}

    public interface OnPermissionRequestListener {

        void onPermissionRequestSucceed(int permissionType);

        /**
         *
         * @param permissionType
         * @param rejectByUser 是否用户此次手动拒绝权限弹框
         */
        void onPermissionRequestFailed(int permissionType, boolean rejectByUser);
    }

}
