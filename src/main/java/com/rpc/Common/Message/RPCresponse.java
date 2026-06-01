package com.rpc.Common.Message;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RPCresponse {
    private int code;
    private String message;
    private Object data;

public static RPCresponse success(Object data){
    return RPCresponse.builder().code(200).data(data).build();
}

public static RPCresponse fail(int code,String message){
    return RPCresponse.builder().code(code).message(message).build();
}
}
