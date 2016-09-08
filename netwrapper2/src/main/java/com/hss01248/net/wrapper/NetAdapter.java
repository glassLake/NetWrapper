package com.hss01248.net.wrapper;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.hss01248.net.config.ConfigInfo;
import com.hss01248.net.config.NetDefaultConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public abstract class NetAdapter<T> implements Netable{

    private static final String TAG = "NetAdapter";
    public  Context context;

    public void initInApp(Context context){
        this.context = context;

    }







    public T sendRequest(final int method, final String urlTail, final Map map, final ConfigInfo configInfo,
                            final MyNetCallback myListener){
        String url = CommonHelper.appendUrl(urlTail,isAppend());

        myListener.url = url;

        CommonHelper.addToken(map);

        T request = generateNewRequest(method,url,map,configInfo,myListener);

        setInfoToRequest(configInfo,request);

        //cachecontrol

        cacheControl(configInfo,request);


        addToQunue(request);

        return request;
    }

    protected abstract boolean isAppend();

    protected abstract void addToQunue(T request);

    protected abstract void cacheControl(ConfigInfo configInfo, T request);

    protected T generateNewRequest(int method, String url, Map map,
                                       ConfigInfo configInfo, MyNetCallback myListener) {
        int requestType = configInfo.resonseType;
        switch (requestType){
            case ConfigInfo.TYPE_STRING:
            case ConfigInfo.TYPE_JSON:
            case ConfigInfo.TYPE_JSON_FORMATTED:
                return newStringRequest(method,url,map,configInfo,myListener);
            case ConfigInfo.TYPE_DOWNLOAD:
                return newDownloadRequest(method,url,map,configInfo,myListener);
            case ConfigInfo.TYPE_UPLOAD:
                return newSingleUploadRequest(method,url,map,configInfo,myListener);
        }
        return null;
    }

    protected abstract T newSingleUploadRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener);

    protected abstract T newDownloadRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener);

    protected abstract T newStringRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener);




    protected abstract void setInfoToRequest(ConfigInfo configInfo,T request);

    protected RetryPolicy generateRetryPolicy(ConfigInfo configInfo) {
        return new DefaultRetryPolicy(configInfo.timeout,configInfo.retryCount,1.0f);
    }







    protected Request.Priority getPriority(int priority) {
        switch (priority){
            case ConfigInfo.Priority_NORMAL:
                return Request.Priority.NORMAL;
            case ConfigInfo.Priority_IMMEDIATE:
                return Request.Priority.IMMEDIATE;
            case ConfigInfo.Priority_LOW:
                return Request.Priority.LOW;
            case ConfigInfo.Priority_HIGH:
                return Request.Priority.HIGH;
        }
        return Request.Priority.NORMAL;

    }


    public abstract void cancleRequest(Object tag);









    public T getString(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener){

        ConfigInfo info = new ConfigInfo();
        info.tag = tag;

       return sendRequest(NetDefaultConfig.Method.GET,url,map,info,listener);
    }


    public T postStandardJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener){

        ConfigInfo info = new ConfigInfo();
        info.tag = tag;
        info.resonseType = ConfigInfo.TYPE_JSON_FORMATTED;

        return sendRequest(NetDefaultConfig.Method.POST,url,map,info,listener);
    }

    public T getStandardJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener){
        ConfigInfo info = new ConfigInfo();
        info.tag = tag;
        info.resonseType = ConfigInfo.TYPE_JSON_FORMATTED;
        return sendRequest(NetDefaultConfig.Method.GET,url,map,info,listener);
    }



    public T postCommonJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener){

        ConfigInfo info = new ConfigInfo();
        info.tag = tag;
        info.resonseType = ConfigInfo.TYPE_JSON;

        return sendRequest(NetDefaultConfig.Method.POST,url,map,info,listener);
    }

    public T getCommonJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener){
        ConfigInfo info = new ConfigInfo();
        info.tag = tag;
        info.resonseType = ConfigInfo.TYPE_JSON;
        return sendRequest(NetDefaultConfig.Method.GET,url,map,info,listener);
    }



    public T download(String url,String savedpath,MyNetCallback callback){
        ConfigInfo info = new ConfigInfo();
        info.tag = url;
        info.resonseType = ConfigInfo.TYPE_DOWNLOAD;
        info.filePath = savedpath;
        info.timeout = 0;
        return  sendRequest(NetDefaultConfig.Method.GET,url,new HashMap(),info,callback);

    }

    @Override
    public Object autoLogin() {
        return null;
    }

    @Override
    public Object autoLogin(MyNetCallback myNetListener) {
        return null;
    }
}
