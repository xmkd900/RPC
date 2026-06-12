package com.rpc.Common.serializer.mySerializer;

public interface Serializer {

    //对象序列化成字节数组
    byte[]serialize(Object object);
    //字节数组反序列化成对象，需要哪种序列化方式通过type自取
    Object deserialize(byte[]bytes,int messgeType);

    int getType();

    static Serializer getSerializerByCode(int code){
        switch (code){
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
                default:
                    return null;
        }


    }
}
