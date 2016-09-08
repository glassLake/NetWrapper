package com.hss01248.net.wrapper;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hss01248.net.config.BaseNetBean;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2016/4/24.
 */
public class MyJson {

    public static String toJsonStr(Object obj){
        return new Gson().toJson(obj);
        // return JSON.parseObject(str,clazz);
    }


    public static <T> T  parseObject(String str,Class<T> clazz){
        return new Gson().fromJson(str,clazz);
        // return JSON.parseObject(str,clazz);
    }

    public static  <E> List<E> parseArray(String str,Class<E> clazz){
        return new Gson().fromJson(str,new TypeToken<List<E>>() {}.getType());
        // return JSON.parseArray(str,clazz);
    }


    /**
     * 使用时怎么传入T的类型?
     *
     * 对象才有类型,但这里不需要传入对象,
     * 或者无法传入对象,只能传入类型(被另一个框架层调用,只能传入那边的泛型F)
     *
     *
     * @param json
     * @param <T>
     * @return
     */
    public static <T> BaseNetBean<T> parseBaseBean(String json, Class<T> clazz) {
        Gson gson = new Gson();
        Type objectType = new TypeToken<BaseNetBean<T>>() {}.getType();
        return gson.fromJson(json, objectType);
    }


}
