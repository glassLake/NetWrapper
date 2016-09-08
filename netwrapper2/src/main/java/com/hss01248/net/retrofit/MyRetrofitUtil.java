package com.hss01248.net.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hss01248.net.wrapper.MyNetCallback;

import java.util.Map;

import retrofit2.Call;


/**
 * Created by Administrator on 2016/8/30.
 */
public class MyRetrofitUtil  {

    public static Context context;


    /**
     * 初始化
     * @param context
     */
    public static void init(Context context){
        MyRetrofitUtil.context = context;

    }


    public Call getString(@NonNull String url, @NonNull Map map,  MyNetCallback listener) {
        return RetrofitAdapter.getInstance().getString(url,map,"",listener);
    }


    public Call postStandardJsonResonse(@NonNull String url, @NonNull Map map,  MyNetCallback listener) {
        return RetrofitAdapter.getInstance().postStandardJsonResonse(url,map,"",listener);
    }


    public Call getStandardJsonResonse(@NonNull String url, @NonNull Map map,  MyNetCallback listener) {
        return RetrofitAdapter.getInstance().getStandardJsonResonse(url,map,"",listener);
    }


    public Call postCommonJsonResonse(@NonNull String url, @NonNull Map map,  MyNetCallback listener) {
        return RetrofitAdapter.getInstance().postCommonJsonResonse(url,map,"",listener);
    }


    public Call getCommonJsonResonse(@NonNull String url, @NonNull Map map,  MyNetCallback listener) {
        return RetrofitAdapter.getInstance().getCommonJsonResonse(url,map,"",listener);
    }


    public Call autoLogin() {
        return null;
    }


    public static Call autoLogin(MyNetCallback myNetListener) {
        return null;
    }


    public void cancleRequest(Object tag) {
         RetrofitAdapter.getInstance().cancleRequest(tag);
    }


    public Call download(String url, String savedpath, MyNetCallback callback) {
        return RetrofitAdapter.getInstance().download(url,savedpath,callback);
    }
}
