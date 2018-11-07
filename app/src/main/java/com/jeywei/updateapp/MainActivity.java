package com.jeywei.updateapp;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jeywei.update.UpdateManager;
import com.jeywei.update.UpdateUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    String log = "12月17日晚更新说明：\n" +
            "1.修复之前反馈若干bug，优化系统，提升稳定性\n" +
            "2.右上角设置中增加“找回账号密码”功能\n" +
            "3.已派单和缺货页面增加“单量降序”筛选项\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "4.app增加未派单筛选项，可以实现档口货物部分派单\n" +
            "5.增加仓库拼包使用的“拼包货架”和“暂存货架”功能";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxPermissions rxPermission = new RxPermissions(MainActivity.this);
        rxPermission.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            switch (permission.name) {
                                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                    //同意写卡
                                    break;
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                        }
                    }
                });
        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateManager.newBuilder(MainActivity.this)
                        .setUpdateLog(log)
                        .setApkUrl("https://video-1253691097.cos.ap-guangzhou.myqcloud.com/apollo_2.5.4release_201810231751.apk")
                        .setApkName("apollo.apk")
                        .setApkPath(UpdateUtils.FILEPATH)
                        .setTitle("发现更新")
                        .setSubTitle("是否更新新版本？")
                        .setForceUpdate(false)
                        .setShowProgress(true)
                        .setShowNotification(true)
                        .build()
                        .showUpdate();
            }
        });
    }
}
