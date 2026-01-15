package io.github.milesreimann.packetsystem.core.pipeline;

import io.github.milesreimann.packetsystem.core.connection.AbstractConnection;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Miles
 * @since 28.08.25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Pipeline {
    private static final String FRAME_DECODER = "frameDecoder";
    private static final String PACKET_DECODER = "packetDecoder";
    private static final String FRAME_PREPENDER = "framePrepender";
    private static final String PACKET_ENCODER = "packetEncoder";
    private static final String CONNECTION_HANDLER = "connectionHandler";

    public static void init(
        AbstractConnection connection,
        ChannelPipeline pipeline,
        ConnectionHandler connectionHandler
    ) {
        pipeline.addLast(FRAME_DECODER, new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(PACKET_DECODER, new ByteToPacketDecoder(connection));
        pipeline.addLast(FRAME_PREPENDER, new LengthFieldPrepender(4));
        pipeline.addLast(PACKET_ENCODER, new PacketToByteEncoder(connection));
        pipeline.addLast(CONNECTION_HANDLER, connectionHandler);
    }
}
