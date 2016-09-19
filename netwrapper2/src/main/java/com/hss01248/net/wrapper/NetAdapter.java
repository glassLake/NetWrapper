package com.hss01248.net.wrapper;

import android.content.Context;

import com.hss01248.net.config.ConfigInfo;
import com.hss01248.net.config.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public abstract class NetAdapter<T> implements Netable<T>{

    private static final String TAG = "NetAdapter";
    public  Context context;

    public void initInApp(Context context){
        this.context = context;

    }


    /**
     * 将configinfo组装成请求
     * @param configInfo
     * @return
     */
    public <E> T assembleRequest(final ConfigInfo<E> configInfo){
        String url = CommonHelper.appendUrl(configInfo.url,isAppend());

        configInfo.listener.url = url;

        if (configInfo.isAppendToken){
            CommonHelper.addToken(configInfo.params);
        }


        T request = generateNewRequest(configInfo);


        setInfoToRequest(configInfo,request);

        //cachecontrol

        cacheControl(configInfo,request);


        addToQunue(request);

        return request;
    }

    protected abstract boolean isAppend();

    protected abstract void addToQunue(T request);

    protected abstract void cacheControl(ConfigInfo configInfo, T request);

    protected <E> T generateNewRequest(ConfigInfo<E> configInfo) {
        int requestType = configInfo.type;
        switch (requestType){
            case ConfigInfo.TYPE_STRING:
                return  newCommonStringRequest(configInfo);
            case ConfigInfo.TYPE_JSON:
                return newCommonJsonRequest(configInfo);
            case ConfigInfo.TYPE_JSON_FORMATTED:
                return newStandardJsonRequest(configInfo);
            case ConfigInfo.TYPE_DOWNLOAD:
                return newDownloadRequest(configInfo);
            case ConfigInfo.TYPE_UPLOAD_SINGLE:
                return newSingleUploadRequest(configInfo);
            case ConfigInfo.TYPE_UPLOAD_MULTIPLE:
                return newMultiUploadRequest(configInfo);
        }
        return null;
    }

    protected abstract <E> T newStandardJsonRequest(ConfigInfo<E> configInfo);

    protected abstract <E> T newCommonJsonRequest(ConfigInfo<E> configInfo);

    protected abstract T newMultiUploadRequest(ConfigInfo configInfo);

    protected abstract T newSingleUploadRequest(ConfigInfo configInfo);

    protected abstract T newDownloadRequest(ConfigInfo configInfo);

    protected abstract T newCommonStringRequest(ConfigInfo configInfo);




    protected abstract void setInfoToRequest(ConfigInfo configInfo,T request);

   /* protected RetryPolicy generateRetryPolicy(ConfigInfo configInfo) {
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

    }*/


    public abstract void cancleRequest(Object tag);

   /* @Override
    public <E> T getString(String url, Map map, String tag, MyNetListener<E> listener) {
        ConfigInfo<E> info = new ConfigInfo();
        setKeyInfo(info,url,map,tag,listener);
        info.tag = tag;

        return assembleRequest(info);
    }*/
   @Override
   public <E> T getString( String url, Map map, String tag, final MyNetListener<E> listener){

        ConfigInfo<E> info = new ConfigInfo();
        setKeyInfo(info,url,map,tag,listener);
        info.tag = tag;

       return assembleRequest(info);
    }

    @Override
    public <E> T postString(String url, Map map, String tag, MyNetListener<E> listener) {
        ConfigInfo<E> info = new ConfigInfo();
        setKeyInfo(info,url,map,tag,listener);
        info.tag = tag;
        info.method = HttpMethod.POST;

        return assembleRequest(info);
    }

    @Override
    public <E> T postStandardJsonResonse( String url,  Map map, String tag, final MyNetListener<E> listener){

        ConfigInfo<E> info = new ConfigInfo();
        setKeyInfo(info,url,map,tag,listener);
        info.type = ConfigInfo.TYPE_JSON_FORMATTED;
        info.method = HttpMethod.POST;
        return assembleRequest(info);
    }
    @Override
    public <E> T getStandardJsonResonse(String url, Map map, String tag, final MyNetListener<E> listener){
        ConfigInfo<E> info = new ConfigInfo<E>();
        setKeyInfo(info,url,map,tag,listener);
        info.type = ConfigInfo.TYPE_JSON_FORMATTED;
        return assembleRequest(info);
    }


    @Override
    public <E> T postCommonJsonResonse( String url,  Map map, String tag, final MyNetListener<E> listener){

        ConfigInfo<E> info = new ConfigInfo();
        setKeyInfo(info,url,map,tag,listener);
        info.method = HttpMethod.POST;
        info.tag = tag;
        info.type = ConfigInfo.TYPE_JSON;

        return assembleRequest(info);
    }

    protected  <E> void setKeyInfo(ConfigInfo<E> info, String url, Map map, String tag, MyNetListener<E> listener){
        info.url = url;
        info.params = map;
        info.tag = tag;
        info.listener = listener;
    }

    @Override
    public <E> T getCommonJsonResonse( String url,  Map map, String tag, final MyNetListener<E> listener){
        ConfigInfo<E> info = new ConfigInfo<E>();
        setKeyInfo(info,url,map,tag,listener);
        info.type = ConfigInfo.TYPE_JSON;
        return assembleRequest(info);
    }


    @Override
    public <E> T download(String url,String savedpath,MyNetListener<E> listener){
        ConfigInfo<E> info = new ConfigInfo();
        setKeyInfo(info,url,new HashMap(),"",listener);
        info.isAppendToken = false;
        info.type = ConfigInfo.TYPE_DOWNLOAD;
        info.filePath = savedpath;
        info.timeout = 0;
        return  assembleRequest(info);

    }


}
