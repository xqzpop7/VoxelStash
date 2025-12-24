package dev.fruitypop;

import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Utils {
     public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        try (InputStream source = GLFW.class.getResourceAsStream(resource)) {
            if (source == null) {
                throw new IOException("Resource not found: " + resource);
            }
            try (ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = org.lwjgl.system.MemoryUtil.memAlloc(bufferSize);
                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = org.lwjgl.system.MemoryUtil.memRealloc(buffer, buffer.capacity() * 3 / 2);
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }
}
