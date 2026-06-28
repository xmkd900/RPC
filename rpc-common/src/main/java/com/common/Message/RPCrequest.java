package com.common.Message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCrequest implements Serializable {
    private RequestType type=RequestType.NORMAL;
    private String interfaceName;
    private String methodName;
    private Object[]parameters;
    private Class<?>[]parameterTypes;
    public static RPCrequest heartBeat() {
        return RPCrequest.builder().type(RequestType.HEARTBEAT).build();
    }
}
