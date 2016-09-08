package com.hss01248.net.volley;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.hss01248.net.wrapper.MyNetCallback;
import com.hss01248.net.wrapper.Netable;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/5.
 */
public class MyVolleyUtil implements Netable<Request> {

    public static Context context;


    /**
     * 初始化
     * @param context
     */
    public static void init(Context context){
        MyVolleyUtil.context = context;
    }


    @Override
    public Request getString(@NonNull String url, @NonNull Map map, String tag, MyNetCallback listener) {
        return VolleyAdapter.getInstance(context).getString(url,map,tag,listener);
    }

    @Override
    public Request postStandardJsonResonse(@NonNull String url, @NonNull Map map, String tag, MyNetCallback listener) {
        return VolleyAdapter.getInstance(context).postStandardJsonResonse(url,map,tag,listener);
    }

    @Override
    public Request getStandardJsonResonse(@NonNull String url, @NonNull Map map, String tag, MyNetCallback listener) {
        return VolleyAdapter.getInstance(context).getStandardJsonResonse(url,map,tag,listener);
    }

    @Override
    public Request postCommonJsonResonse(@NonNull String url, @NonNull Map map, String tag, MyNetCallback listener) {
        return VolleyAdapter.getInstance(context).postCommonJsonResonse(url,map,tag,listener);
    }

    @Override
    public Request getCommonJsonResonse(@NonNull String url, @NonNull Map map, String tag, MyNetCallback listener) {
        return VolleyAdapter.getInstance(context).getCommonJsonResonse(url,map,tag,listener);
    }

    @Override
    public Request download(String url, String savedpath, MyNetCallback<String> callback) {
        return VolleyAdapter.getInstance(context).download(url,savedpath,callback);
    }

    @Override
    public Request autoLogin() {
        return null;
    }

    @Override
    public Request autoLogin(MyNetCallback myNetListener) {
        return null;
    }

    @Override
    public void cancleRequest(Object tag) {
        VolleyAdapter.getInstance(context).cancleRequest(tag);
    }
}
