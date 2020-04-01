package com.xlh.study.retrofitlibrary;

/**
 * @author: Watler Xu
 * time:2020/4/1
 * description:
 * version:0.0.1
 */
abstract class ParameterHandler {
    abstract void apply(RequestBuilder requestBuilder, String toString);

    static final class Query extends ParameterHandler{
        private final String name;

        Query(String name){
            if(name.isEmpty()){
                throw new IllegalArgumentException("name = null");
            }
            this.name = name;
        }


        @Override
        void apply(RequestBuilder requestBuilder, String value) {
            if(value == null){
                return;
            }
            requestBuilder.addQueryParam(name,value);
        }
    }

    static final class Field extends ParameterHandler{
        private final String name;

        // 传过来的是注解的值，并非参数值
        Field(String name){
            if(name.isEmpty()){
                throw new IllegalArgumentException("name = null");
            }
            this.name = name;
        }


        @Override
        void apply(RequestBuilder requestBuilder, String value) {
            if(value == null){
                return;
            }
            // 拼接Field参数，此处name为参数注解的值，value为参数值
            requestBuilder.addFormField(name,value);
        }
    }


}
