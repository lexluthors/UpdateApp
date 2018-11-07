package com.jeywei.update;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeywei.updateapplibrary.R;

import java.io.File;

/**
 * Description:
 * Data：2018/11/5-18:55
 * Author: Allen
 */
public class UpdateManager {
    Context context;
    //apk下载地址
    String apkUrl;
    //apk存储路径
    String apkPath;
    //apk名称
    String apkName;
    //更新日志
    String updateLog = "";
    //标题
    String title = "";
    //副标题
    String subTitle = "";
    //通知icon
    int notifyIcon = R.drawable.ic_android_black_24dp;
    //是否强制更新
    boolean isForce;
    //是否显示进度条
    boolean isProgress;
    //是否显示通知
    boolean isNotification = true;

    public UpdateManager(Context context) {
        this.context = context;
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        UpdateManager mUpdateManager;

        public Builder(Context context) {
            mUpdateManager = new UpdateManager(context);
        }

        /**
         * @param apkUrl 设置apk下载路径
         */
        public Builder setApkUrl(String apkUrl) {
            mUpdateManager.apkUrl = apkUrl;
            return this;
        }

        /**
         * 设置apk下载路径，不设置就使用默认路径
         *
         * @param apkPath
         * @return
         */
        public Builder setApkPath(String apkPath) {
            mUpdateManager.apkPath = apkPath;
            return this;
        }

        /**
         * 设置apk名称，不设置默认截取最后一个/之后的名称
         *
         * @param apkName
         * @return
         */
        public Builder setApkName(String apkName) {
            mUpdateManager.apkName = apkName;
            return this;
        }

        /**
         * 设置更新日志
         *
         * @param updateLog
         * @return
         */
        public Builder setUpdateLog(String updateLog) {
            mUpdateManager.updateLog = updateLog;
            return this;
        }

        /**
         * 设置标题
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            mUpdateManager.title = title;
            return this;
        }

        /**
         * 副标题设置
         *
         * @param subTitle
         * @return
         */
        public Builder setSubTitle(String subTitle) {
            mUpdateManager.subTitle = subTitle;
            return this;
        }

        /**
         * 是否强制更新，isForce默认是false
         *
         * @param isForce
         * @return
         */
        public Builder setForceUpdate(boolean isForce) {
            mUpdateManager.isForce = isForce;
            return this;
        }

        /**
         * 点击确定按钮之后，是否显示进度条，默认是显示的
         *
         * @param isProgress
         * @return
         */
        public Builder setShowProgress(boolean isProgress) {
            mUpdateManager.isProgress = isProgress;
            return this;
        }

        /**
         * 是否显示通知
         *
         * @param isNotification
         * @return
         */
        public Builder setShowNotification(boolean isNotification) {
            mUpdateManager.isNotification = isNotification;
            return this;
        }

        /**
         * 设置通知图标
         * @param notifyIcon
         * @return
         */
        public Builder setNotificationIcon(int notifyIcon) {
            mUpdateManager.notifyIcon = notifyIcon;
            return this;
        }

        public UpdateManager build() {
            return mUpdateManager;
        }
    }

    String apkFilePath = null;
    //是否已经下载没有安装
    boolean isYetDownloadNoInstall = false;

    public void showUpdate() {
        if (TextUtils.isEmpty(apkPath)) {
            //直接使用默认路径和名称
            if (TextUtils.isEmpty(apkName)) {
                apkFilePath = UpdateUtils.FILEPATH + UpdateUtils.getNameFromUrl(apkUrl);
            } else {
                apkFilePath = UpdateUtils.FILEPATH + apkName;
            }
            File file = new File(apkFilePath);
            if (file.exists()) {
                //已经存在，直接安装
                isYetDownloadNoInstall = true;
            }
        } else {
            if (TextUtils.isEmpty(apkName)) {
                apkFilePath = apkPath + UpdateUtils.getNameFromUrl(apkUrl);
            } else {
                apkFilePath = apkPath + apkName;
            }
            File file = new File(apkFilePath);
            if (file.exists()) {
                //已经存在，直接安装
                isYetDownloadNoInstall = true;
            }
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //context需定义
        View view = layoutInflater.inflate(R.layout.def_update_dialog, null);
        final Dialog dialog = UpdateUtils.getCenterDialog(context, view);

        TextView title_tv = view.findViewById(R.id.title);
        TextView subtitle_tv = view.findViewById(R.id.subtitle);
        TextView confirm = view.findViewById(R.id.confirm);
        TextView cancle = view.findViewById(R.id.cancle);
        TextView content = view.findViewById(R.id.content);
        final LinearLayout button_layout = view.findViewById(R.id.button_layout);
        final ProgressBar progressBar = view.findViewById(R.id.progress);
        final TextView install = view.findViewById(R.id.install);
        if (isForce) {
            //如果是强制更新，就设置对话框不可取消
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        if (!TextUtils.isEmpty(title)) {
            title_tv.setText(title);
        }
        if (!TextUtils.isEmpty(subTitle)) {
            subtitle_tv.setText(subTitle);
        } else {
            subtitle_tv.setVisibility(View.GONE);
        }
        progressBar.setMax(100);
        content.setText(updateLog);

        if (isYetDownloadNoInstall) {
            button_layout.setVisibility(View.GONE);
            install.setVisibility(View.VISIBLE);
        }
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //直接去安装
                installApk(context, apkFilePath);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProgress) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                if (isForce) {
                    progressBar.setVisibility(View.VISIBLE);
                    button_layout.setVisibility(View.GONE);
                } else {
                    if (isProgress) {
                        progressBar.setVisibility(View.VISIBLE);
                        button_layout.setVisibility(View.GONE);
                    } else {
                        dialog.dismiss();
                    }
                }
                //立即更新
                if (TextUtils.isEmpty(apkPath)) {
                    apkPath = UpdateUtils.FILEPATH;
                }
                if (TextUtils.isEmpty(apkName)) {
                    apkName = UpdateUtils.getNameFromUrl(apkUrl);
                }

                if (isNotification) {
                    //显示下载通知
                    showNotification();
                }
                DownloadUtil.getInstance().download(apkUrl, apkPath, apkName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(String path) {
                        //去安装
                        installApk(context, path);
                        install.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        clear();
                    }

                    @Override
                    public void onDownloading(int progress) {
                        progressBar.setProgress(progress);
                        if (isNotification) {
                            cBuilder.setProgress(100, progress, false);
                            sent();
                        }
                    }

                    @Override
                    public void onDownloadFailed() {
                    }
                });
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //暂不更新
                if (!isForce) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    /**
     * 安装apk文件
     */
    public static void installApk(Context context, String apkPath) {
        File file = new File(apkPath);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, UpdateUtils.getPackageName(context) + ".fileprovider", file);
                //应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }


    private int NOTIFICATION_ID = 7;
    private NotificationManager nm;
    private NotificationCompat.Builder cBuilder;
    private Notification notification;

    private void showNotification() {
        // 获取系统服务来初始化对象
        nm = (NotificationManager) context
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        cBuilder = new NotificationCompat.Builder(context,"default");
        setCompatBuilder(notifyIcon, "新版本更新", "正在下载中");
    }

    /**
     * 设置在顶部通知栏中的各种信息
     * @param smallIcon
     */
    private void setCompatBuilder(int smallIcon, String title, String content) {
        NotificationChannel a;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            a = new NotificationChannel("10000","default", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(a);
            cBuilder.setChannelId("10000");
        }

        Intent it = new Intent(Intent.ACTION_VIEW);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
        cBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        cBuilder.setSmallIcon(smallIcon);// 设置顶部状态栏的小图标
//        cBuilder.setTicker(ticker);// 在顶部状态栏中的提示信息
        cBuilder.setContentTitle(title);// 设置通知中心的标题
        cBuilder.setContentText(content);// 设置通知中心中的内容
        cBuilder.setWhen(System.currentTimeMillis());
        cBuilder.setAutoCancel(true);
        cBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        int defaults = 0;
        cBuilder.setDefaults(defaults);
    }

    /**
     * 发送通知
     */
    private void sent() {
        notification = cBuilder.build();
        // 发送该通知
        nm.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 根据id清除通知
     */
    public void clear() {
        // 取消通知
        nm.cancelAll();
    }
}
