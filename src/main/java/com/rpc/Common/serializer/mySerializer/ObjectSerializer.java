package com.rpc.Common.serializer.mySerializer;

import java.io.*;

public class ObjectSerializer implements Serializer{
    @Override
    public byte[] serialize(Object object) {
       byte[]bytes=null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);       // ← Java 原生序列化核心 API
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messgeType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();     // ← Java 原生反序列化核心 API
            // 注意：不需要 messageType！字节流里自带完整类信息
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;

    }

    @Override
    public int getType() {
        return 0;
    }
}
