package com.xlh.study.simpleretrofit;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author: Watler Xu
 * time:2020/4/1
 * description:
 * version:0.0.1
 */
public class ProxyTest {

    interface HOST {
        @GET("/ip/ipNew")
        Call<ResponseBody> get(@Query("ip") String ip, @Query("key") String key);

        @POST("/ip/ipNew")
        @FormUrlEncoded
        Call<ResponseBody> post(@Field("ip") String ip, @Field("key") String key);
    }

    @Test
    public void proxy() {
        HOST host = (HOST) Proxy.newProxyInstance(HOST.class.getClassLoader(), new Class[]{HOST.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                // 获取方法名称
                System.out.println("获取方法名称 >>> " + method.getName());
                // 获取方法的注解
                GET get = method.getAnnotation(GET.class);
                // 获取方法的注解值
                System.out.println("获取方法的注解值 >>> " + get.value());
                // 获取方法的参数的注解
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                for (Annotation[] annotation : parameterAnnotations) {
                    System.out.println("获取方法的参数的注解 >>> " + Arrays.toString(annotation));
                }
                // 获取方法的参数值
                System.out.println("获取方法的参数值 >>> " + Arrays.toString(args));
                return null;
            }
        });

        // $Proxy4,运行时创建的动态类，存在于内存中
        host.get("11.22.33.44","appKey");

        /*获取方法名称 >>> get
        获取方法的注解值 >>> /ip/ipNew
        获取方法的参数的注解 >>> [@retrofit2.http.Query(encoded=false, value=ip)]
        获取方法的参数的注解 >>> [@retrofit2.http.Query(encoded=false, value=key)]
        获取方法的参数值 >>> [11.22.33.44, appKey]*/
    }
}
