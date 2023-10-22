package common.sync;

import common.enums.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class SyncMessageDecoder extends ByteToMessageDecoder {

    private static Logger logger = LogManager.getLogger(SyncMessageDecoder.class.getName());
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        SyncMessageDto syncMessageDto = new SyncMessageDto();

        int readableByte = in.readableBytes();
        logger.info("syncMessage byteBuf readableBytes : {}", readableByte);

        //minimal byteBuf length = 42bytes (without actionkey)
        //TODO : use json or protobuf someday... remove fixed length
        if(readableByte < 42){
            logger.error("syncMessage byteBuf readableBytes should be longer than 42bytes");
            return;
        }else{
<<<<<<< Updated upstream
            String workerId = in.readCharSequence(16, StandardCharsets.UTF_8).toString().trim();
            String syncElement = in.readCharSequence(16, StandardCharsets.UTF_8).toString().trim();
            String action = in.readCharSequence(10, StandardCharsets.UTF_8).toString().trim();
            String actionKey = in.readCharSequence(readableByte - 42, StandardCharsets.UTF_8).toString().trim();
=======
            String workerId = ((String) in.readCharSequence(16, StandardCharsets.UTF_8)).trim();
            String syncElement = ((String) in.readCharSequence(16, StandardCharsets.UTF_8)).trim();
            String action = ((String) in.readCharSequence(10, StandardCharsets.UTF_8)).trim();
            String actionKey = ((String) in.readCharSequence(readableByte - 42, StandardCharsets.UTF_8)).trim();
>>>>>>> Stashed changes

            if(!StringUtils.isNoneEmpty(workerId, syncElement, action)){
                logger.error("syncMessage byteBuf should have workerId, syncElement, action");
                return;
            }

            syncMessageDto.setWorkerId(workerId);
            syncMessageDto.setSyncElement(Constants.SyncElement.valueOf(syncElement));
            syncMessageDto.setAction(Action.valueOf(action));
            syncMessageDto.setActionKey(StringUtils.isNoneEmpty(actionKey) ? actionKey : null);

            ctx.fireChannelRead(syncMessageDto);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("syncMessage decoder exception : {}", cause.getMessage());
    }
}
