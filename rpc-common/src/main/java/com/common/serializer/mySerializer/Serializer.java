package com.common.serializer.mySerializer;

import java.util.HashMap;
import java.util.Map;

public interface Serializer {

    //对象序列化成字节数组
    byte[]serialize(Object object);
    //字节数组反序列化成对象，需要哪种序列化方式通过type自取
    Object deserialize(byte[]bytes,int messgeType);

    int getType();

    static final Map<Integer, Serializer> serializerMap = new HashMap<>();

    static Serializer getSerializerByCode(int code){
        if(serializerMap.isEmpty()){
            serializerMap.put(0, new ObjectSerializer());
            serializerMap.put(1, new JsonSerializer());
            serializerMap.put(2, new KryoSerializer());
            serializerMap.put(3, new HessianSerializer());
            serializerMap.put(4, new ProtostuffSerializer());
        }
        return serializerMap.get(code);
    }
}
