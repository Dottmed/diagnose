package com.dingbei.diagnose.http;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Dayo
 * @desc 网络请求的参数类
 */

public class HttpParams {

    private ArrayList<KeyValue> params = new ArrayList<>();

    public void put(String key, Object value){
        boolean flag = true;
        Iterator<KeyValue> iterator = params.iterator();
        while (iterator.hasNext()){
            KeyValue entry = iterator.next();
            if(key.equals(entry.getKey())) {
                entry.setValue(value);
                flag = false;
            }
        }

        //没有发现重复的数据,添加进来
        if(flag) {
            params.add(new KeyValue(key,value));
        }
    }

    /**
     * 用于传入key相同,但value不同的参数
     * @param key
     * @param value
     */
    public void putMulti(String key, Object value){
        params.add(new KeyValue(key,value));
    }

    public void clear(){
        params.clear();
    }

    public void remove(String key){
        Iterator<KeyValue> iterator = params.iterator();
        while (iterator.hasNext()){
            KeyValue keyValue = iterator.next();
            if(key.equals(keyValue.getKey())) {
                iterator.remove();
            }
        }
    }

    public ArrayList<KeyValue> getParams(){
        return params;
    }

    public int size(){
        return params.size();
    }

    //自己封装的key-value,用于传入key相同但value不同的参数
    static public class KeyValue{
        private final String key;
        private Object value;

        public KeyValue(String key, Object value){
            this.key = key;
            this.value = value;
        }

        public String getKey(){
            return key;
        }

        public Object getValue(){
            return value;
        }

        public void setValue(Object value){
            this.value = value;
        }
    }
}
