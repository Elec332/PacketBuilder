import nl.elec332.lib.packetbuilder.api.IPacketBuilder;
import nl.elec332.lib.packetbuilder.internal.PacketBuilder;

/**
 * Created by Elec332 on 2/28/2021
 */
module nl.elec332.lib.packetbuilder {

    exports nl.elec332.lib.packetbuilder;

    exports nl.elec332.lib.packetbuilder.api;
    exports nl.elec332.lib.packetbuilder.api.field;
    exports nl.elec332.lib.packetbuilder.api.util;

    exports nl.elec332.lib.packetbuilder.fields;
    exports nl.elec332.lib.packetbuilder.fields.generic;

    //exports nl.elec332.lib.packetbuilder.impl;
    exports nl.elec332.lib.packetbuilder.impl.fields;
    exports nl.elec332.lib.packetbuilder.impl.fields.base;
    exports nl.elec332.lib.packetbuilder.impl.fields.optional;
    exports nl.elec332.lib.packetbuilder.impl.fields.numbers;
    exports nl.elec332.lib.packetbuilder.impl.packet;
    exports nl.elec332.lib.packetbuilder.impl.protocol;

    provides IPacketBuilder with PacketBuilder;

    requires io.netty.buffer;
    requires io.netty.common;

    //Netty requires this for the cleaner
    requires jdk.unsupported;

}