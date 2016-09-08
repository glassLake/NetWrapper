package com.hss01248.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ParseError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hss01248.net.config.BaseNetBean;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class MyBaseJsonRequest<T> extends Request<BaseNetBean<T>> {

    protected static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    public void setmListener(Response.Listener<BaseNetBean<T>> mListener) {
        this.mListener = mListener;
    }

    private  Response.Listener<BaseNetBean<T>> mListener;//注意,这里指定的泛型不是T,而是BaseNetBean<T>



    public MyBaseJsonRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    public MyBaseJsonRequest(int method, String url, Response.ErrorListener listener, RetryPolicy retryPolicy) {
        super(method, url, listener, retryPolicy);
    }

    public MyBaseJsonRequest(int method, String url, Priority priority, Response.ErrorListener listener, RetryPolicy retryPolicy) {
        super(method, url, priority, listener, retryPolicy);
    }



    @Override
    protected Response<BaseNetBean<T>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            //gson 解析
            Gson gson = new Gson();
            Type objectType = new TypeToken<BaseNetBean<T>>() {}.getType();
            BaseNetBean<T> bean = gson.fromJson(jsonString,objectType);
            if (bean == null){
                throw new JSONException("json parse error");
            }

            //拦截响应,重置header里缓存相关字段,实现完全的缓存控制, 注意只缓存真正成功的请求
            reSetCacheControl(response,bean);

            return Response.success(bean,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(BaseNetBean<T> response) {
        if (mListener != null){
            // todo 将code解析加上
            mListener.onResponse(response);// 直接返回类型BaseNetBean<T>的数据,让使用者去根据code解析
        }

    }


    //下方的为实现完全的缓存控制的代码


    long cacheTime;//毫秒

    public boolean isFromCache = false;
    public int cacheHitCount = 0;

    @Override  //怎么判断是从缓存中取的还是从网络上取的?
    public void addMarker(String tag) {
        super.addMarker(tag);
        if ("cache-hit".equals(tag)){
            cacheHitCount++;
        }else if ("cache-hit-parsed".equals(tag)){
            cacheHitCount++;
        }

        if (cacheHitCount == 2){
            isFromCache = true;
        }
    }

    private void reSetCacheControl(NetworkResponse response, BaseNetBean<T> bean) {
        this.setShouldCache(true);//重置cache开关
        if (!isFromCache && bean != null && bean.code == BaseNetBean.CODE_SUCCESS){//todo 只缓存 真正拿到数据的响应,需要让用户去写
            Map<String, String> headers = response.headers;
            headers.put("Cache-Control","max-age="+cacheTime);
        }

    }

    /**
     * 缓存key的生成规则:url+body
     * @return
     */
    @Override
    public String getCacheKey() {
        String bodyStr = "";
        try {
            byte[]   body = getBody();
            bodyStr = new String(body,"UTF-8");
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (bodyStr != null){
            return super.getCacheKey()+bodyStr;
        }else {
            return super.getCacheKey();
        }


    }
}
