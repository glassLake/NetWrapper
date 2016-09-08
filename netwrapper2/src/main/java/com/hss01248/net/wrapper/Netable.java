package com.hss01248.net.wrapper;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/5.
 */
public interface Netable<T> {
    public T getString(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener);

    public T postStandardJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener);

    public T getStandardJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener);



    public T postCommonJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener);

    public T getCommonJsonResonse(@NonNull String url, @NonNull Map map, String tag, final MyNetCallback listener);



    public T download(String url, String savedpath, MyNetCallback<String> callback);

    public  T autoLogin();

    public  T autoLogin(MyNetCallback myNetListener);

    public  void cancleRequest(Object tag);
}
