package com.ares.transport.decoder;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class AresBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    public AresBasedFrameDecoder() {
        this(1024 * 1024 * 4, 0, 4);
    }

    public AresBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0);
    }
}
