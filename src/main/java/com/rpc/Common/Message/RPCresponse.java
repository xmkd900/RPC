package com.rpc.Common.Message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCresponse implements Serializable {
    private int code;
    private String message;
    private Object data;
    //更新：加入传输数据的类型，以便在自定义序列化器中解析
    private Class<?> dataType;

public static RPCresponse success(Object data){
    return RPCresponse.builder().code(200).data(data).dataType(data.getClass()).build();
}

public static RPCresponse fail(int code,String message){
    return RPCresponse.builder().code(code).message(message).build();
}
}
