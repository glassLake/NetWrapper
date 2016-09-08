package com.hss01248.net.retrofit;

import android.util.Log;

import com.hss01248.net.config.ConfigInfo;
import com.hss01248.net.config.NetDefaultConfig;
import com.hss01248.net.retrofit.progress.ProgressInterceptor;
import com.hss01248.net.wrapper.CommonHelper;
import com.hss01248.net.wrapper.MyNetCallback;
import com.hss01248.net.wrapper.NetAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class RetrofitAdapter extends NetAdapter<Call> {

    private static final String TAG = "RetrofitAdapter";
    Retrofit retrofit;
    ApiService service;


    ApiService serviceDownload;
    //需要单独为下载的call设置Retrofit: 主要是超时时间设置为0
    Retrofit retrofitDownload;



    private void init() {
        //默认情况下，Retrofit只能够反序列化Http体为OkHttp的ResponseBody类型
        //并且只能够接受ResponseBody类型的参数作为@body

        OkHttpClient.Builder httpBuilder=new OkHttpClient.Builder();
        OkHttpClient client=httpBuilder.readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS) //设置超时
                .retryOnConnectionFailure(true)//重试
                //.addInterceptor(new ProgressInterceptor())//下载时更新进度
                .build();

        retrofit = new Retrofit
                .Builder()
                .baseUrl(NetDefaultConfig.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // 使用Gson作为数据转换器
                .build();

        service = retrofit.create(ApiService.class);
    }

    private static RetrofitAdapter instance;

    private RetrofitAdapter(){
       init();
       // initDownload();
    }

    private void initDownload() {
        OkHttpClient.Builder httpBuilder=new OkHttpClient.Builder();
        OkHttpClient client=httpBuilder.readTimeout(0, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS).writeTimeout(0, TimeUnit.SECONDS) //设置超时
                .retryOnConnectionFailure(true)//重试
                .addInterceptor(new ProgressInterceptor())//下载时更新进度
                .build();

        retrofitDownload = new Retrofit
                .Builder()
                .baseUrl(NetDefaultConfig.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // 使用Gson作为数据转换器
                .build();

        serviceDownload = retrofitDownload.create(ApiService.class);
    }

    public static  RetrofitAdapter getInstance(){
        if (instance == null){
            synchronized (RetrofitAdapter.class){
                if (instance ==  null){
                    instance = new RetrofitAdapter();
                }
            }
        }
        return  instance;
    }


    @Override
    protected boolean isAppend() {
        return false;
    }

    @Override
    protected void addToQunue(Call request) {
        //空实现即可

    }

    @Override
    protected void cacheControl(ConfigInfo configInfo, Call request) {
        //todo

    }

    @Override
    protected Call newSingleUploadRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener) {
        return null;
    }

    @Override
    protected Call newDownloadRequest(int method, String url, Map map, final ConfigInfo configInfo, final MyNetCallback myListener) {

        if (serviceDownload == null){
            initDownload();
        }
        Call<ResponseBody> call = serviceDownload.download(url);
        myListener.registEventBus();

        //todo 改成在子线程中执行

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("download","onResponse finished");
                //开子线程将文件写到指定路径中
                writeResponseBodyToDisk(response.body(),configInfo.filePath,myListener);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                myListener.onError(t.toString());
            }
        });
        return call;
    }

    @Override
    protected Call newStringRequest(final int method, final String url, final Map map, final ConfigInfo configInfo, final MyNetCallback myListener) {

        Log.e("url","newStringRequest:"+url);
        //todo 分方法:
        Call<ResponseBody> call;

        if (method == NetDefaultConfig.Method.GET){
            call = service.executGet(url,map);
        }else if (method == NetDefaultConfig.Method.POST){
            call = service.executePost(url,map);
        }else {
            call = null;
        }


        final long time = System.currentTimeMillis();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String string = "";
                try {
                    string =  response.body().string();
                    CommonHelper.parseStringResponseInTime(time,string,method,url,map,configInfo,myListener);
                } catch (IOException e) {
                    e.printStackTrace();
                    CommonHelper.parseErrorInTime(time,e.toString(),configInfo,myListener);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonHelper.parseErrorInTime(time,t.toString(),configInfo,myListener);
            }
        });
        return call;
    }

    @Override
    protected void setInfoToRequest(ConfigInfo configInfo, Call request) {




    }

    @Override
    public void cancleRequest(Object tag) {

        if (tag instanceof  Call){
            Call call = (Call) tag;
            if (!call.isCanceled()){
                call.cancel();
            }

        }


    }


    private boolean writeResponseBodyToDisk(ResponseBody body,String path,MyNetCallback callback) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                callback.onSuccess(path,path);

                return true;
            } catch (IOException e) {
                callback.onError(e.toString());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}
