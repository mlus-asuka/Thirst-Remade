package vip.fubuki.thirst.foundation.common.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities
{
    public static final Capability<IThirstCap> PLAYER_THIRST = CapabilityManager.get(new CapabilityToken<>() {});
}
