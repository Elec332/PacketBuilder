/**
 * Created by Elec332 on 2/28/2021
 */
module packetbuilder.test {
    uses nl.elec332.lib.packetbuilder.api.IPacketBuilder;
    //uses nl.elec332.lib.packetbuilder.api.IPacketBuilder;

    exports test.test2 to nl.elec332.lib.packetbuilder;

    requires nl.elec332.lib.packetbuilder;
    requires io.netty.common;

}