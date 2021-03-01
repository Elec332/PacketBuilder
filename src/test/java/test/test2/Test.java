package test.test2;

import io.netty.util.internal.StringUtil;
import nl.elec332.lib.packetbuilder.api.IPacketBuilder;
import nl.elec332.lib.packetbuilder.api.util.ClosableIterator;
import nl.elec332.lib.packetbuilder.impl.packet.RawPayloadPacket;
import nl.elec332.lib.packetbuilder.impl.protocol.Ethernet;
import nl.elec332.lib.packetbuilder.impl.protocol.IPv4;
import nl.elec332.lib.packetbuilder.impl.protocol.TCP;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Arrays;
import java.util.Collections;
import java.util.ServiceLoader;

/**
 * Created by Elec332 on 2/26/2021
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String fileN = Arrays.stream(args).filter(s -> s.contains("/")).findFirst().orElseThrow();
        System.out.println("Using file: " + fileN);
        File file = new File(fileN);
        IPacketBuilder packetBuilder = ServiceLoader.load(IPacketBuilder.class).findFirst().orElseThrow();
        ClosableIterator<byte[]> it = packetBuilder.readPcapPackets(file);
        it.next();
        it.next();
        System.out.println(Arrays.toString(it.next()));
        System.out.println(Arrays.toString(DATA));
        it.close();

        packetBuilder.getPayloadManager().bindLayers(Ethernet.class, IPv4.class, (p, c, c2) -> c2.accept("type", 2048));
        packetBuilder.getPayloadManager().bindLayers(IPv4.class, TCP.class, (p, c, c2) -> c2.accept("protocol", 0x06));
        packetBuilder.getPayloadManager().bindLayers(TCP.class, RawPayloadPacket.class);

        test(packetBuilder);

        System.out.println(Collections.singletonMap("Jow", 3));
        System.out.println(packetBuilder.decode(DATA, new Ethernet()).toString());
        System.out.println(Arrays.toString(DATA));
        System.out.println(Arrays.toString(packetBuilder.encode(new Ethernet(), e -> {
            e.destination = "00:1c:06:08:e7:db";
            e.source = "90:e6:ba:84:5e:41";
            e.addPayloadT(new IPv4(), ip -> {
                ip.source = (Inet4Address) Inet4Address.getByName("192.168.1.10");
                ip.destination = (Inet4Address) Inet4Address.getByName("192.168.1.191");
                ip.addPayload(new TCP(), tcp -> {
                    tcp.destinationPort = 102;
                    tcp.seq = 337108461;
                    tcp.ack = 195017;
                    tcp.flags = 0x010;
                    tcp.windowSize = 0xfaf0;
                    tcp.checksum = 0x8434;
                    tcp.options = Arrays.asList(new TCP.NOPOption(), new TCP.NOPOption(), new TCP.Timestamps());
                });
            });
        })));

        System.out.println("-------");
        byte[] t2 = {0, 28, 6, 8, -25, -37, -112, -26, -70, -124, 94, 65, 8, 0, 69, 0, 0, 52, 30, 25, 64, 0, -128, 6, 0, 0, -64, -88, 1, 10, -64, -88, 1, -65, -20, 95, 0, 102, 20, 23, -35, -19, 0, 2, -7, -55, -128, 16, -6, -16, -124, 52, 0, 0, 1, 1, 8, 10, 0, 0, 0, 123, 0, 0, -121, 5};

        Ethernet ethernet = packetBuilder.decode(t2, new Ethernet());
        System.out.println(ethernet.toString());
        System.out.println(Arrays.toString(packetBuilder.encode(ethernet)));

        System.out.println("------------");
        testPCAP(packetBuilder, file);
    }
    
    private static final byte[] DATA = {
            (byte) 0x00, (byte) 0x1c, (byte) 0x06, (byte) 0x08, (byte) 0xe7, (byte) 0xdb, (byte) 0x90, (byte) 0xe6,
            (byte) 0xba, (byte) 0x84, (byte) 0x5e, (byte) 0x41, (byte) 0x08, (byte) 0x00, (byte) 0x45, (byte) 0x00,
            (byte) 0x00, (byte) 0x28, (byte) 0x1e, (byte) 0x19, (byte) 0x40, (byte) 0x00, (byte) 0x80, (byte) 0x06,
            (byte) 0x00, (byte) 0x00, (byte) 0xc0, (byte) 0xa8, (byte) 0x01, (byte) 0x0a, (byte) 0xc0, (byte) 0xa8,
            (byte) 0x01, (byte) 0xbf, (byte) 0xec, (byte) 0x5f, (byte) 0x00, (byte) 0x66, (byte) 0x14, (byte) 0x17,
            (byte) 0xdd, (byte) 0xed, (byte) 0x00, (byte) 0x02, (byte) 0xf9, (byte) 0xc9, (byte) 0x50, (byte) 0x10,
            (byte) 0xfa, (byte) 0xf0, (byte) 0x84, (byte) 0x34, (byte) 0x00, (byte) 0x00
    };

    private static void test(IPacketBuilder packetBuilder) {
        byte[] DATA_TEST = {
                (byte) 0xa3, 0x7c
        };
        System.out.println("BIT_TEST");
        System.out.println(Arrays.toString(DATA_TEST));
        System.out.println(StringUtil.toHexStringPadded(DATA_TEST));
        System.out.println(packetBuilder.decode(DATA_TEST, new BitTestProt()).toString());
        System.out.println(Arrays.toString(packetBuilder.encode(new BitTestProt(), a -> {})));
        System.out.println(new BitTestProt().getPacketSize());
        System.out.println("BIT_TEST_END");
    }

    private static void testPCAP(IPacketBuilder packetBuilder, File file) throws IOException {
        ClosableIterator<byte[]> it = packetBuilder.readPcapPackets(file);
        while (it.hasNext()) {
            System.out.println(packetBuilder.decode(it.next(), new Ethernet()));
        }
        it.close();
    }

}
