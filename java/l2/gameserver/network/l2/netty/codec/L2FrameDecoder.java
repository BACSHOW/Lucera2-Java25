package l2.gameserver.network.l2.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Divide a stream bruta de bytes TCP nos frames do protocolo Lineage II.
 * Formato: 2 bytes little-endian com o tamanho total do pacote (header + body).
 */
public final class L2FrameDecoder extends ByteToMessageDecoder {
    public static final int HEADER_SIZE = 2;
    public static final int DEFAULT_MAX_FRAME_SIZE = 65534;

    private final int maxFrameSize;

    public L2FrameDecoder() {
        this(DEFAULT_MAX_FRAME_SIZE);
    }

    public L2FrameDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < HEADER_SIZE) {
            return;
        }

        in.markReaderIndex();

        // Little-endian unsigned short = tamanho total do frame
        int low = in.readByte() & 0xFF;
        int high = in.readByte() & 0xFF;
        int totalLength = (high << 8) | low;

        if (totalLength < HEADER_SIZE || totalLength > this.maxFrameSize) {
            throw new IllegalArgumentException("Illegal L2 frame size: " + totalLength + " (max " + this.maxFrameSize + ")");
        }

        int bodyLength = totalLength - HEADER_SIZE;

        if (bodyLength == 0) {
            return;
        }

        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }

        out.add(in.readRetainedSlice(bodyLength));
    }
}
