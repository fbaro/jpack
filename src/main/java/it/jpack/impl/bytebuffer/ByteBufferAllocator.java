package it.jpack.impl.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Flavio
 */
public abstract class ByteBufferAllocator {

    private final static ByteBufferAllocator PLAIN_LE = new ByteBufferAllocator() {
        @Override
        public ByteBuffer allocate(int capacity) {
            ByteBuffer ret = ByteBuffer.allocate(capacity);
            ret.order(ByteOrder.LITTLE_ENDIAN);
            return ret;
        }
    };

    private final static ByteBufferAllocator PLAIN_BE = new ByteBufferAllocator() {
        @Override
        public ByteBuffer allocate(int capacity) {
            ByteBuffer ret = ByteBuffer.allocate(capacity);
            ret.order(ByteOrder.BIG_ENDIAN);
            return ret;
        }
    };
    
    private final static ByteBufferAllocator DIRECT_LE = new ByteBufferAllocator() {
        @Override
        public ByteBuffer allocate(int capacity) {
            ByteBuffer ret = ByteBuffer.allocateDirect(capacity);
            ret.order(ByteOrder.LITTLE_ENDIAN);
            return ret;
        }
    };

    private final static ByteBufferAllocator DIRECT_BE = new ByteBufferAllocator() {
        @Override
        public ByteBuffer allocate(int capacity) {
            ByteBuffer ret = ByteBuffer.allocateDirect(capacity);
            ret.order(ByteOrder.BIG_ENDIAN);
            return ret;
        }
    };
    
    public abstract ByteBuffer allocate(int size);

    private ByteBufferAllocator() { }

    public static ByteBufferAllocator plain(ByteOrder order) {
        if (ByteOrder.BIG_ENDIAN.equals(order)) {
            return PLAIN_BE;
        } else if (ByteOrder.LITTLE_ENDIAN.equals(order)) {
            return PLAIN_LE;
        } else {
            throw new IllegalArgumentException("Unknown byte order: " + order);
        }
    }

    public static ByteBufferAllocator direct(ByteOrder order) {
        if (ByteOrder.BIG_ENDIAN.equals(order)) {
            return DIRECT_BE;
        } else if (ByteOrder.LITTLE_ENDIAN.equals(order)) {
            return DIRECT_LE;
        } else {
            throw new IllegalArgumentException("Unknown byte order: " + order);
        }
    }
}
