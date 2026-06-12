package com.rpc.Common.serializer.myCode;

import com.rpc.Common.Message.MessageType;
import com.rpc.Common.Message.RPCrequest;
import com.rpc.Common.Message.RPCresponse;
import com.rpc.Common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        //写入消息类型
        if(o instanceof RPCrequest){
            byteBuf.writeShort(MessageType.REQUEST.getCode());
        }else if(o instanceof RPCresponse){
            byteBuf.writeShort(MessageType.QESPONSE.getCode());
        }
        //写入序列化类型
        byteBuf.writeShort(serializer.getType());
        //序列化
        byte[] bytes = serializer.serialize(o);
        //序列化长度写入
        byteBuf.writeInt(bytes.length);
        //序列化内容写入
        byteBuf.writeBytes(bytes);

    }
}
