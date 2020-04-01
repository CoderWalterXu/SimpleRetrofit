package com.xlh.study.simpleretrofit;

import com.xlh.study.retrofitlibrary.Retrofit;
import com.xlh.study.retrofitlibrary.http.Field;
import com.xlh.study.retrofitlibrary.http.GET;
import com.xlh.study.retrofitlibrary.http.POST;
import com.xlh.study.retrofitlibrary.http.Query;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.Call;

/**
 * @author: Watler Xu
 * time:2020/4/1
 * description:
 * version:0.0.1
 */
public class WxRetrofitUnitTest {

    private final static String IP = "144.34.161.97";
    private final static String KEY = "aa205eeb45aa76c6afe3c52151b52160";
    private final static String BASE_URL = "http://apis.juhe.cn/";

    interface HOST {
        @GET("/ip/ipNew")
        Call getIpMethod(@Query("ip") String ip, @Query("key") String key);

        @POST("/ip/ipNew")
        Call postIpMethod(@Field("ip") String ip, @Field("key") String key);
    }

    @Test
    public void wxRetrofit() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        HOST host = retrofit.create(HOST.class);

        // Retrofit GET同步请求
        {
            Call call = host.getIpMethod(IP,KEY);

            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("Retrofit GET同步请求 >>> " + response.body().string());
            }
        }

        // Retrofit POST同步请求
        {
            Call call = host.postIpMethod(IP,KEY);

            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("Retrofit POST同步请求 >>> " + response.body().string());
            }
        }

    }


}
