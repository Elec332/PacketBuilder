package nl.elec332.lib.packetbuilder.internal;

import nl.elec332.lib.packetbuilder.api.util.ClosableIterator;
import nl.elec332.lib.packetbuilder.util.ValueReference;

import java.io.*;
import java.nio.ByteOrder;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Elec332 on 3/1/2021
 */
public class PCAPReader {

    private static final long PCAP_BYTE_ORDER_MAGIC = 0xa1b2c3d4L;
    private static final long PCAPNG_BYTE_ORDER_MAGIC = 0x1a2b3c4dL;

    private static final int PCAPNG_SHB = 0x0a0d0d0a;
    private static final int PCAPNG_IDB = 0x00000001;
    private static final int PCAPNG_EPB = 0x00000006;

    public static ClosableIterator<byte[]> readPcapPackets(File file) throws IOException {
        if (ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN) {
            throw new RuntimeException();
        }
        FileInputStream fis = new FileInputStream(file);
        long magic = readLE32(fis);
        if (magic == PCAP_BYTE_ORDER_MAGIC) {
            return readPcap(fis);
        } else if (magic == 0xd4c3b2a1L) {
            throw new RuntimeException("Byte order " + magic);
        }
        if (magic == PCAPNG_SHB) {
            return readPcapNg(fis);
        }
        throw new UnsupportedEncodingException("" + magic);
    }

    private static long readLE32(FileInputStream fis) throws IOException {
        return fis.read() | fis.read() << 8 | fis.read() << 16 | (long) fis.read() << 24;
    }

    private static int readLE16(FileInputStream fis) throws IOException {
        return fis.read() | fis.read() << 8;
    }

    private static void checkNetwork(FileInputStream fis) throws IOException {
        int network = readLE16(fis);
        if (network != 1) {
            throw new UnsupportedEncodingException("Only ethernet is supported at this moment. Type detected was: " + network);
        }
    }

    private static ClosableIterator<byte[]> readPcap(FileInputStream fis) throws IOException {
        int skip = 18;
        if (fis.skip(skip) != skip) {
            throw new EOFException();
        }
        checkNetwork(fis);
        AtomicBoolean closed = new AtomicBoolean(false);

        return new ClosableIterator<>() {

            @Override
            public boolean hasNext() {
                try {
                    return !closed.get() && fis.available() > 16;
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
                    long len = readLE32(fis);
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

    private static ClosableIterator<byte[]> readPcapNg(FileInputStream fis) throws IOException {
        AtomicBoolean closed = new AtomicBoolean(false);
        byte[] next = readNextBlock(fis, PCAPNG_SHB);
        ValueReference<byte[]> ref = new ValueReference<>(next);


        return new ClosableIterator<>() {

            @Override
            public boolean hasNext() {
                return ref.get() != null;
            }

            @Override
            public byte[] next() {
                byte[] ret = ref.get();
                if (ret == null) {
                    throw new NoSuchElementException();
                }
                try {
                    byte[] newRet = null;
                    if (!closed.get() && fis.available() >= 20) {
                        newRet = readNextBlock(fis, (int) readLE32(fis));
                    }
                    ref.accept(newRet);
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

    private static byte[] readNextBlock(FileInputStream fis, int identifier) throws IOException {
        switch (identifier) {
            case PCAPNG_SHB:
                readSHB(fis);
                return readNextBlock(fis, (int) readLE32(fis));
            case PCAPNG_IDB:
                readIDB(fis);
                if (fis.available() < 20) {
                    return null;
                }
                return readNextBlock(fis, (int) readLE32(fis));
            case PCAPNG_EPB:
                return readEPB(fis);
            default:
                throw new UnsupportedEncodingException("Identifier: " + identifier + "  " + fis.available());
        }
    }

    private static void readSHB(FileInputStream fis) throws IOException {
        long length = readLE32(fis);
        if (length > 256) {
            throw new UnsupportedEncodingException();
        }
        long magic = readLE32(fis);
        if (magic != PCAPNG_BYTE_ORDER_MAGIC) {
            throw new UnsupportedEncodingException();
        }
        int skip = (int) (length - 12);
        if (fis.skip(skip) != skip) {
            throw new EOFException();
        }
    }

    private static void readIDB(FileInputStream fis) throws IOException {
        long length = readLE32(fis);
        if (length > 256) {
            throw new UnsupportedEncodingException();
        }
        checkNetwork(fis);
        int skip = (int) (length - 10);
        if (fis.skip(skip) != skip) {
            throw new EOFException();
        }
    }

    private static byte[] readEPB(FileInputStream fis) throws IOException {
        long length = readLE32(fis);
        int skip = 12;
        if (fis.skip(skip) != skip) {
            throw new EOFException();
        }
        long pLen = readLE32(fis);
        long pLen2 = readLE32(fis);
        if (pLen != pLen2) {
            throw new UnsupportedOperationException();
        }
        byte[] ret = new byte[(int) pLen];
        int r = fis.read(ret);
        if (r != pLen) {
            throw new EOFException();
        }
        skip = 4 - (int) pLen % 4;
        if (skip < 4 && fis.skip(skip) != skip) {
            throw new EOFException();
        }
        long length2 = readLE32(fis);
        if (length != length2) {
            throw new RuntimeException("Length check failed: " + length + " vs " + length2);
        }
        return ret;
    }

}
