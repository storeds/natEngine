package codec;

import enumeration.NatError;
import exception.NatException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import message.NatMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 20:19
 * @description: 通用的解码器
 **/
public class CommonDecoder extends MessageToMessageDecoder<ByteBuf> {

    /** 定义日志 **/
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);

    /** 定义魔数 **/
    private static final int MAGIC_NUMBER = 0xCAFEBABE;


    /**
     * 解码方法
     * @param ctx  channelHandler上下文
     * @param in   传输数据的bytebuf
     * @param out  添加下一个处理的队列
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 获取魔数头部
        int magic = in.readInt();

        // 做预先判断处理，当这个包不属于这个协议
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包:{}" , magic);
            throw new NatException(NatError.UNKNOWN_PROTOCOL);
        }

        // 获取消息的请求号
        int requestId = in.readInt();

        // 获取消息的type
        int type = in.readInt();

        // 获取消息元数据
        int metaDataLength = in.readInt();
        CharSequence metaDataString = in.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
        JSONObject jsonObject = new JSONObject(metaDataString.toString());
        Map<String,Object> metaData = jsonObject.toMap();

        // 获取消息的数据
        byte[] data = null;
        if (in.isReadable()){
            data = ByteBufUtil.getBytes(in);
        }

        // 创建消息
        NatMessage natMessage = new NatMessage();
        natMessage.setRequestId(requestId);
        natMessage.setType(type);
        natMessage.setMetaData(metaData);
        natMessage.setData(data);

        // 将消息添加后交给下一个处理
        out.add(natMessage);
    }
}
