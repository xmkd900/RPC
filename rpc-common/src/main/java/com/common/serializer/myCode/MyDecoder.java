package com.common.serializer.myCode;
import com.common.Message.MessageType;
import com.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        short messageType = byteBuf.readShort();
        if (messageType != MessageType.QESPONSE.getCode() &&
                messageType != MessageType.REQUEST.getCode()) {
            System.out.println("不支持此种类型");
            return;
        }
        short serializerType = byteBuf.readShort();
        Object deserializer = Serializer.getSerializerByCode(serializerType);
        if(deserializer == null){
            System.out.println("没有对应的序列化器");
            return;
        }
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object obj=((Serializer)deserializer).deserialize(bytes,messageType);
        list.add(obj);
    }
}
