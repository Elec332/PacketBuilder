package nl.elec332.lib.packetbuilder.internal;

import nl.elec332.lib.packetbuilder.api.util.ClosableIterator;

import java.io.*;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Elec332 on 3/1/2021
 */
public class PCAPReader {

    public static ClosableIterator<byte[]> readPcapPackets(File file) throws IOException {
        if (ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN) {
            throw new RuntimeException();
        }
        FileInputStream fis = new FileInputStream(file);
        long magic = fis.read() | fis.read() << 8 | fis.read() << 16 | (long) fis.read() << 24;
        if (magic != 0xa1b2c3d4L) {
            throw new RuntimeException("Byte order " + magic);
        }
        int skip = 16;
        if (fis.skip(skip) != skip) {
            throw new EOFException();
        }
        long network = fis.read() | fis.read() << 8 | fis.read() << 16 | (long) fis.read() << 24;
        if (network != 1) {
            throw new UnsupportedEncodingException("Only ethernet is supported at this moment. Type detected was: " + network);
        }

        AtomicBoolean closed = new AtomicBoolean(false);

        return new ClosableIterator<>() {

            @Override
            public boolean hasNext() {
                try {
                    return closed.get() || fis.available() > 16;
                } catch (IOException e) {
                    close();
                    return false;
                }
            }

            @Override
            public byte[] next() {
                if (closed.get()) {
                    throw new RuntimeException("File is already closed!");
                }
                try {
                    int skip = 8;
                    if (fis.skip(skip) != skip) {
                        throw new EOFException();
                    }
                    long len = fis.read() | fis.read() << 8 | fis.read() << 16 | (long) fis.read() << 24;
                    skip = 4;
                    if (fis.skip(skip) != skip) {
                        throw new EOFException();
                    }
                    byte[] ret = new byte[(int) len];
                    if (fis.read(ret) != len) {
                        throw new EOFException();
                    }
                    return ret;
                } catch (Exception e) {
                    close();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() {
                if (!closed.get()) {
                    closed.set(true);
                    try {
                        fis.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        };

    }

}
