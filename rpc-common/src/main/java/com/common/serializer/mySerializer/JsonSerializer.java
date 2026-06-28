package com.common.serializer.mySerializer;

import com.alibaba.fastjson.JSONObject;
import com.common.Message.RPCrequest;
import com.common.Message.RPCresponse;


public class JsonSerializer implements Serializer{
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = JSONObject.toJSONBytes(obj);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messgeType) {
        Object obj=null;
        switch (messgeType){
            case 0:
                RPCrequest request = JSONObject.parseObject(bytes,RPCrequest.class);
                Object[] objects = request.getParameters();
                for(int i = 0; i < objects.length; i++){
                    Class<?> paramsType = request.getParameterTypes()[i];  // 期望的类型（比如 User.class）

                    // 检查 JSON 反序列化出的实际类型和期望类型是否一致
                    if (!paramsType.isAssignableFrom(objects[i].getClass())){
                        // 比如：期望 User.class，实际是 JSONObject.class → 不一致！
                        // 需要强制转换：JSONObject → User
                        objects[i] = JSONObject.toJavaObject(
                                (JSONObject) request.getParameters()[i],   // 当前的 JSONObject
                                request.getParameterTypes()[i]             // 目标类型 User.class
                        );
                    }else{
                        // 类型一致（比如期望 String.class，实际也是 String.class）
                        // 或者期望 int.class，实际也是 Integer.class
                        objects[i] = request.getParameters()[i];
                    }
                }
                request.setParameters(objects);
                obj=request;
                break;
            case 1:
                RPCresponse rpcResponse= JSONObject.parseObject(bytes,RPCresponse.class);
                if(rpcResponse.getDataType()==null){
                    obj = rpcResponse.fail(500,"结果为空");
                    break;
                }
                Class<?>dataType=rpcResponse.getDataType();
                if(!dataType.isAssignableFrom(rpcResponse.getData().getClass())){
rpcResponse.setData(JSONObject.toJavaObject((JSONObject) rpcResponse.getData(),dataType));
                }
                obj=rpcResponse;
                break;
            default:
                throw new RuntimeException("不支持的序列化类型");
        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }
}
