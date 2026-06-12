package com.rpc.Common.Message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageType {
    REQUEST(0),QESPONSE(1);
    private int code;

    public int getCode() {
        return code;
    }
}
