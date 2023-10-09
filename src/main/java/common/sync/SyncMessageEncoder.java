package common.sync;

import common.core.worker.WorkerGlobal;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SyncMessageEncoder extends MessageToByteEncoder<SyncMessageDto> {

    private static Logger logger = LogManager.getLogger(SyncMessageEncoder.class.getName());
    @Override
    protected void encode(ChannelHandlerContext ctx, SyncMessageDto msg, ByteBuf out) throws Exception {

        /*
        generate byteBuffer with fixed size
            workerID : 16byte
            workerIP : 16byte (TODO)
            workerPort : 4byte (TODO)
            workerName : 16byte (TODO)
            syncElement : 16byte
            action : 10byte
            actionKey : unfixed.
         */

        ByteBuf buf = Unpooled.buffer();

        try{
            byte[] workerId = getBytes(msg.getWorkerId(), 16);
            buf.writeBytes(workerId);

            byte[] action = getBytes(msg.getAction().toString(), 10);
            buf.writeBytes(action);

            byte[] actionKey = getBytes(msg.getActionKey(), 0);
            buf.writeBytes(actionKey);

            ctx.writeAndFlush(buf);
        }catch (Exception e){
            logger.error("Exception occurred while encoding SyncMessageDto : ", e.getMessage());
        }

    }

    private byte[] getBytes(String str, int size) {
        byte[] bytes = null;

        if(size == 0){
            bytes = str.getBytes();
        }else {
            bytes = new byte[size];
            for (int i = 0; i < size; i++) {
                bytes[i] = 0;
            }
            byte[] strBytes = str.getBytes();
            if (strBytes.length < size) {
                System.arraycopy(strBytes, 0, bytes, 0, strBytes.length);
            } else {
                System.arraycopy(strBytes, 0, bytes, 0, size);
            }
        }
        return bytes;
    }




}
