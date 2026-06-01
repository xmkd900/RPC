package com.rpc.Common.Message;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RPCrequest {
    private String interfaceName;
    private String methodName;
    private Object[]parameters;
    private Class<?>[]parameterTypes;
}
