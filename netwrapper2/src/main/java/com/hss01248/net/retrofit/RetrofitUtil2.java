package com.hss01248.net.retrofit;


import com.hss01248.net.config.BaseNetBean;
import com.hss01248.net.config.NetDefaultConfig;
import com.hss01248.net.wrapper.CommonHelper;
import com.hss01248.net.wrapper.MyNetCallback;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class RetrofitUtil2 {

    Retrofit retrofit;
    ApiService service;






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


    public <E> void postStandard(final String urlTail, final Map<String,String> params, final MyNetCallback<E> myListener){
        CommonHelper.addToken(params);
       Call<BaseNetBean<E>> call = service.postStandradJson(urlTail,params);
        call.enqueue(new Callback<BaseNetBean<E>>() {
            @Override
            public void onResponse(Call<BaseNetBean<E>> call, Response<BaseNetBean<E>> response) {
                BaseNetBean<E> baseBean = response.body();

                CommonHelper.parseStandardJsonObj(baseBean,urlTail,params,myListener,RetrofitUtil2.this);

            }

            @Override
            public void onFailure(Call<BaseNetBean<E>> call, Throwable t) {
                myListener.onError(t.toString());

            }
        });
    }

    public void autoLogin(MyNetCallback callback) {

    }

    public void audoLogin(){}
}
