//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.AppOpsManagerCompat;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.R.string;

public class PermissionCheckUtil {
    private static final String TAG = PermissionCheckUtil.class.getSimpleName();

    public PermissionCheckUtil() {
    }

    public static boolean requestPermissions(Fragment fragment, String[] permissions) {
        return requestPermissions((Fragment) fragment, permissions, 0);
    }

    public static boolean requestPermissions(final Fragment fragment, String[] permissions, int requestCode) {
        if (permissions.length == 0) {
            return true;
        } else {
            List<String> permissionsNotGranted = new ArrayList();
            boolean result = false;

            for (int i = 0; i < permissions.length; ++i) {
                if ((isFlyme() || VERSION.SDK_INT < 23) && permissions[i].equals("android.permission.RECORD_AUDIO")) {
                    showPermissionAlert(fragment.getContext(), fragment.getString(string.rc_permission_grant_needed) + fragment.getString(string.rc_permission_microphone), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (-1 == which) {
                                fragment.startActivity(new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS"));
                            }

                        }
                    });
                    return false;
                }

                if (!hasPermission(fragment.getActivity(), permissions[i])) {
                    permissionsNotGranted.add(permissions[i]);
                }
            }

            if (permissionsNotGranted.size() > 0) {
                fragment.requestPermissions((String[]) permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), requestCode);
            } else {
                result = true;
            }

            return result;
        }
    }

    public static boolean requestPermissions(Activity activity, @NonNull String[] permissions) {
        return requestPermissions((Activity) activity, permissions, 0);
    }

    @TargetApi(23)
    public static boolean requestPermissions(Activity activity, @NonNull String[] permissions, int requestCode) {
        if (VERSION.SDK_INT < 23) {
            return true;
        } else if (permissions.length == 0) {
            return true;
        } else {
            List<String> permissionsNotGranted = new ArrayList();
            boolean result = false;

            for (int i = 0; i < permissions.length; ++i) {
                if (!hasPermission(activity, permissions[i])) {
                    permissionsNotGranted.add(permissions[i]);
                }
            }

            if (permissionsNotGranted.size() > 0) {
                activity.requestPermissions((String[]) permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), requestCode);
            } else {
                result = true;
            }

            return result;
        }
    }

    public static boolean checkPermissions(Context context, @NonNull String[] permissions) {
        if (permissions != null && permissions.length != 0) {
            String[] var2 = permissions;
            int var3 = permissions.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String permission = var2[var4];
                if ((isFlyme() || VERSION.SDK_INT < 23) && permission.equals("android.permission.RECORD_AUDIO")) {
                    RLog.i(TAG, "Build.MODEL = " + Build.MODEL);
                    if (Build.BRAND.toLowerCase().equals("meizu") && Build.MODEL.equals("M1852")) {
                        if (!hasPermission(context, permission)) {
                            return false;
                        }

                        return true;
                    }

                    if (!hasRecordPermision(context)) {
                        return false;
                    }
                } else if (!hasPermission(context, permission)) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    private static boolean isFlyme() {
        String osString = "";

        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            osString = (String) get.invoke(clz, "ro.build.display.id", "");
        } catch (Exception var3) {
            RLog.e(TAG, "isFlyme", var3);
        }

        return osString.toLowerCase().contains("flyme");
    }

    private static boolean hasRecordPermision(Context context) {
        boolean hasPermission = false;
        int bufferSizeInBytes = AudioRecord.getMinBufferSize(44100, 12, 2);
        if (bufferSizeInBytes < 0) {
            RLog.e(TAG, "bufferSizeInBytes = " + bufferSizeInBytes);
            return false;
        } else {
            AudioRecord audioRecord = new AudioRecord(1, 44100, 12, 2, bufferSizeInBytes);

            try {
                audioRecord.startRecording();
            } catch (Exception var5) {
                RLog.e(TAG, "hasRecordPermision", var5);
                hasPermission = false;
            }

            if (audioRecord.getRecordingState() == 3) {
                audioRecord.stop();
                hasPermission = true;
            }

            audioRecord.release();
            return hasPermission;
        }
    }

    public static String getNotGrantedPermissionMsg(Context context, String[] permissions, int[] grantResults) {
        if (permissions != null && permissions.length != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(context.getResources().getString(string.rc_permission_grant_needed));
            sb.append("(");

            try {
                for (int i = 0; i < permissions.length; ++i) {
                    if (grantResults[i] == -1) {
                        String permissionName = context.getString(context.getResources().getIdentifier("rc_" + permissions[i], "string", context.getPackageName()), new Object[]{0});
                        sb.append(permissionName);
                        if (i != permissions.length - 1) {
                            sb.append(" ");
                        }
                    }
                }
            } catch (NotFoundException var6) {
                RLog.e(TAG, "One of the permissions is not recognized by SDK." + permissions.toString());
                return "";
            }

            sb.append(")");
            return sb.toString();
        } else {
            return "";
        }
    }

    private static String getNotGrantedPermissionMsg(Context context, List<String> permissions) {
        if (permissions != null && permissions.size() != 0) {
            HashSet permissionsValue = new HashSet();

            try {
                Iterator var4 = permissions.iterator();

                while (var4.hasNext()) {
                    String permission = (String) var4.next();
                    String permissionValue = context.getString(context.getResources().getIdentifier("rc_" + permission, "string", context.getPackageName()), new Object[]{0});
                    permissionsValue.add(permissionValue);
                }
            } catch (NotFoundException var7) {
                RLog.e(TAG, "one of the permissions is not recognized by SDK." + permissions.toString());
                return "";
            }

            String result = "(";

            String value;
            for (Iterator var9 = permissionsValue.iterator(); var9.hasNext(); result = result + value + " ") {
                value = (String) var9.next();
            }

            result = result.trim() + ")";
            return result;
        } else {
            return "";
        }
    }

    @TargetApi(11)
    private static void showPermissionAlert(Context context, String content, OnClickListener listener) {
        (new Builder(context)).setMessage(content).setPositiveButton(string.rc_confirm, listener).setNegativeButton(string.rc_cancel, listener).setCancelable(false).create().show();
    }

    @TargetApi(19)
    public static boolean canDrawOverlays(Context context) {
        return canDrawOverlays(context, true);
    }

    @TargetApi(19)
    public static boolean canDrawOverlays(final Context context, boolean needOpenPermissionSetting) {
        boolean result = true;
        if (VERSION.SDK_INT >= 23) {
            try {
                boolean booleanValue = (Boolean) Settings.class.getDeclaredMethod("canDrawOverlays", Context.class).invoke((Object) null, context);
                if (!booleanValue && needOpenPermissionSetting) {
                    ArrayList<String> permissionList = new ArrayList();
                    permissionList.add("android.settings.action.MANAGE_OVERLAY_PERMISSION");
                    showPermissionAlert(context, context.getString(string.rc_permission_grant_needed) + getNotGrantedPermissionMsg(context, permissionList), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (-1 == which) {
                                Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                            }

                        }
                    });
                }

                RLog.i(TAG, "isFloatWindowOpAllowed allowed: " + booleanValue);
                return booleanValue;
            } catch (Exception var7) {
                RLog.e(TAG, String.format("getDeclaredMethod:canDrawOverlays! Error:%s, etype:%s", var7.getMessage(), var7.getClass().getCanonicalName()));
                return true;
            }
        } else if (VERSION.SDK_INT < 19) {
            return true;
        } else {
            Object systemService = context.getSystemService("appops");

            Method method;
            try {
                method = Class.forName("android.app.AppOpsManager").getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
            } catch (NoSuchMethodException var9) {
                RLog.e(TAG, String.format("NoSuchMethodException method:checkOp! Error:%s", var9.getMessage()));
                method = null;
            } catch (ClassNotFoundException var10) {
                RLog.e(TAG, "canDrawOverlays", var10);
                method = null;
            }

            if (method != null) {
                try {
                    Integer tmp = (Integer) method.invoke(systemService, 24, context.getApplicationInfo().uid, context.getPackageName());
                    result = tmp == 0;
                } catch (Exception var8) {
                    RLog.e(TAG, String.format("call checkOp failed: %s etype:%s", var8.getMessage(), var8.getClass().getCanonicalName()));
                }
            }

            RLog.i(TAG, "isFloatWindowOpAllowed allowed: " + result);
            return result;
        }
    }

    private static boolean hasPermission(Context context, String permission) {
        String opStr = AppOpsManagerCompat.permissionToOp(permission);
        if (opStr == null) {
            return true;
        } else {
            return context.checkCallingOrSelfPermission(permission) == 0;
        }
    }

    public static void showRequestPermissionFailedAlter(final Context context, String content) {
        if (!TextUtils.isEmpty(content)) {
            OnClickListener listener = new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case -1:
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            Uri uri = Uri.fromParts("package", context.getPackageName(), (String) null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        case -2:
                        default:
                    }
                }
            };
            (new Builder(context, 16974394)).setMessage(content).setPositiveButton(string.rc_confirm, listener).setNegativeButton(string.rc_cancel, listener).setCancelable(false).create().show();
        }
    }
}