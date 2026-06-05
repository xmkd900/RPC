package com.rpc.Common.Message;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RPCrequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[]parameters;
    private Class<?>[]parameterTypes;
}
