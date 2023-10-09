package common.sync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;
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
        logger.info("SyncMessageEncoder encode called");
        try{
            byte[] workerId = parseByteBuf2Dto(msg.getWorkerId(), 16);
            out.writeBytes(workerId);

            byte[] syncElement = parseByteBuf2Dto(msg.getSyncElement().toString(), 16);
            out.writeBytes(syncElement);

            byte[] action = parseByteBuf2Dto(msg.getAction().toString(), 10);
            out.writeBytes(action);

            byte[] actionKey = parseByteBuf2Dto(msg.getActionKey(), 0);
            out.writeBytes(actionKey);

            logger.info("SyncMessageEncoder encode finished");
            logger.info(out.readableBytes());
        }catch (Exception e){
            logger.error("Exception occurred while encoding SyncMessageDto ");
        }

    }

    private byte[] parseByteBuf2Dto(String str, int size) {
        byte[] bytes = null;
        if(!StringUtils.isNotEmpty(str)) {
            bytes = new byte[size];
        }else if(size == 0){
            bytes = str.getBytes();
        }else {
            bytes = new byte[size];
            for (int i = 0; i < size; i++) {
                bytes[i] = 0;
            }
            byte[] strBytes = StringUtils.isNotEmpty(str) ? str.getBytes() : new byte[size];
            if (strBytes.length < size) {
                System.arraycopy(strBytes, 0, bytes, 0, strBytes.length);
            } else {
                System.arraycopy(strBytes, 0, bytes, 0, size);
            }
        }
        return bytes;
    }




}
