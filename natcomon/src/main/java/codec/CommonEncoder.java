package codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import message.NatMessage;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 21:15
 * @description:  公共的编码器
 **/
public class CommonEncoder extends MessageToByteEncoder<NatMessage> {

    /** 定义魔数 **/
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    /**
     * 定义编码器方法
     * @param ctx            channelHandler上下文
     * @param natMessage     传输的消息
     * @param out            输出的natMessage
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, NatMessage natMessage, ByteBuf out) throws Exception {

        // 定义数据流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // 写入魔数
        dos.writeInt(MAGIC_NUMBER);

        // 写入请求号
        int requestId = natMessage.getRequestId();
        dos.writeInt(requestId);

        // 写入消息类型
        int type = natMessage.getType();
        dos.writeInt(type);

        // 写入元数据
        JSONObject metaDataJson = new JSONObject(natMessage.getMetaData());
        byte[] metaDataBytes = metaDataJson.toString().getBytes(CharsetUtil.UTF_8);
        dos.writeInt(metaDataBytes.length);
        dos.write(metaDataBytes);

        // 判断传递的信息为不为空
        if (natMessage.getData()!=null && natMessage.getData().length>0){
            dos.write(natMessage.getData());
        }

        // 将数据写入
        byte[] data = baos.toByteArray();
        out.writeInt(data.length);
        out.writeBytes(data);

    }
}
