package vip.fubuki.thirst.foundation.network;

import vip.fubuki.thirst.Thirst;
import vip.fubuki.thirst.foundation.network.message.DrinkByHandMessage;
import vip.fubuki.thirst.foundation.network.message.PlayerThirstSyncMessage;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ThirstModPacketHandler
{
    private static final String PROTOCOL_VERSION = "0.1.1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            Thirst.asResource("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init()
    {
        INSTANCE.registerMessage(0, PlayerThirstSyncMessage.class, PlayerThirstSyncMessage::encode, PlayerThirstSyncMessage::decode, PlayerThirstSyncMessage::handle);
        INSTANCE.registerMessage(1, DrinkByHandMessage.class, DrinkByHandMessage::encode, DrinkByHandMessage::decode, DrinkByHandMessage::handle);
    }
}
