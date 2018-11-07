package com.jeywei.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Description:
 * Data：2018/11/2-16:46
 * Author: Allen
 */
public class DownloadUtil {
    public static final int DOWNLOAD_FAIL = 0;
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_SUCCESS = 2;
    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil getInstance() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url      下载地址
     * @param saveDir  保存文件目录
     * @param pathName 保存的文件名称
     * @param listener 监听器
     */
    public void download(final String url, final String saveDir, final String pathName, final OnDownloadListener listener) {
        this.listener = listener;
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Message message = Message.obtain();
                message.what = DOWNLOAD_FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(okhttp3.Call call, final Response response) throws IOException {
                Log.e("这里是什么线程>>>>", Thread.currentThread().getName());
                runOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        //储存下载文件的目录
                        String savePath = null;
                        try {
                            savePath = isExistDir(saveDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            File file = null;
                            if (TextUtils.isEmpty(pathName)) {
                                file = new File(savePath, UpdateUtils.getNameFromUrl(url));
                            } else {
                                file = new File(savePath, pathName);
                            }
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                //下载中
                                Message message = Message.obtain();
                                message.what = DOWNLOAD_PROGRESS;
                                message.obj = progress;
                                mHandler.sendMessage(message);
                                Log.e("哈哈哈", "这里走了吗");
                            }
                            fos.flush();
                            //下载完成
                            Message message = Message.obtain();
                            message.what = DOWNLOAD_SUCCESS;
                            message.obj = file.getAbsolutePath();
                            mHandler.sendMessage(message);
                        } catch (Exception e) {
                            Message message = Message.obtain();
                            message.what = DOWNLOAD_FAIL;
                            mHandler.sendMessage(message);
                        } finally {
                            try {
                                if (is != null)
                                    is.close();
                            } catch (IOException e) {

                            }
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                            } catch (IOException e) {

                            }
                        }
                    }
                });
            }

        });
    }

    /**
     * description: 不传入保存路径，默认使用FILEPATH
     * author: Allen
     * date: 2018/11/2 17:05
     */
    public void download(final String url, String apkName, final OnDownloadListener listener) {
        download(url, UpdateUtils.FILEPATH, apkName, listener);
    }

    public void download(final String url, final OnDownloadListener listener) {
        download(url, UpdateUtils.FILEPATH, UpdateUtils.getNameFromUrl(url), listener);
    }

    private String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_PROGRESS:
                    listener.onDownloading((Integer) msg.obj);
                    break;
                case DOWNLOAD_FAIL:
                    listener.onDownloadFailed();
                    break;
                case DOWNLOAD_SUCCESS:
                    listener.onDownloadSuccess((String) msg.obj);
                    break;
            }
        }
    };


    OnDownloadListener listener;

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(String path);

        /**
         * 下载进度
         *
         * @param progress
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    //Runnable：任务，必须依附于某一个线程
    //Thread:线程，线程用来执行任务
    //Process：进程
    //保证r这个任务一定是在主线程中执行
    public static void runOnUiThread(Runnable r) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            //主线程
            //new Thread(r).start(); 一旦new了Thread就一定是子线程
            r.run();
        } else {
            //new Thread(r).start()
            sHandler.post(r);
        }

    }

    //保证r一定在子线程中得到执行
    public static void runOnSubThread(Runnable r) {
//        new Thread(r).start();
        //线程池的概念，线程池里面装的是线程，使用线程池可以达到线程的复用，提高性能
        sExecutorService.submit(r);//将r丢到线程池中，线程池中的线程就会来执行r这个任务
    }
}
