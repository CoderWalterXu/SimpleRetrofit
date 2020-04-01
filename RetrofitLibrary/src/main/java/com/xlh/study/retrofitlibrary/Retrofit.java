package com.xlh.study.retrofitlibrary;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Retrofit {

    // 接口请求地址
    private HttpUrl baseUrl;
    // OkHttpClient唯一实现接口
    private Call.Factory callFactory;
    // 缓存请求的方法   key:请求方法（如：host.get()）   value:该方法的属性封装类（方法名、方法注解、参数注解、参数）
    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();

    private Retrofit(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
    }

    public HttpUrl baseUrl(){
        return baseUrl;
    }

    public Call.Factory callFactory(){
        return callFactory;
    }

    public static class Builder {
        // 接口请求地址
        private HttpUrl baseUrl;
        // OkHttpClient唯一实现接口
        private Call.Factory callFactory;

        // 对外提供api方法入口
        public Builder baseUrl(String baseUrl) {
            if (baseUrl.isEmpty()) {
                throw new NullPointerException("baseUrl == null");
            }
            this.baseUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Builder baseUrl(HttpUrl baseUrl) {
            if (baseUrl == null) {
                throw new NullPointerException("baseUrl == null");
            }
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder callFactory(Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        // 属性的校验，或者初始化
        public Retrofit build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }

            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }

            return new Retrofit(this);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        // 动态代理和反射的最底层原理是：$Proxy4
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 获取方法所有内容：方法名，方法的注解，方法参数的注解，方法的参数
                // 将method所有信息拦截之后，存储到ServiceMethod(JavaBean实体类)
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return new OkHttpCall(serviceMethod, args);
            }
        });
    }

    // 获取方法所有内容：方法名、方法注解、参数注解、参数
    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result != null) {
            return result;
        }

        // 线程安全同步锁
        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this,method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

}
