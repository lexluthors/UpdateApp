package com.jeywei.update;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jeywei.updateapplibrary.R;

import java.io.File;

/**
 * Description:
 * Data：2018/11/2-17:13
 * Author: Allen
 */
public class UpdateUtils {

    public static final String PATH = "apk_path";
    public static final String FILEPATH = Environment.getExternalStorageDirectory() + File.separator + "download" + File.separator + "update_app" + File.separator;

    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static Dialog getCenterDialog(Context context, View view) {
        final Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int screenW = getScreenWidth(context)-getScreenWidth(context)*20/100;
        lp.width = screenW;
        return dialog;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取应用程序的版本号
     */
    public static String getPackageName(Context context)
    {
        try
        {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.packageName;
            return version;
        }
        catch (Exception exception)
        {
        }
        return "";
    }



}
