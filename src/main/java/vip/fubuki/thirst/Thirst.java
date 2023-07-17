package vip.fubuki.thirst;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import vip.fubuki.thirst.api.ThirstHelper;
import vip.fubuki.thirst.compat.create.CreateRegistry;
import vip.fubuki.thirst.compat.create.ponder.ThirstPonders;
import vip.fubuki.thirst.content.purity.WaterPurity;
import vip.fubuki.thirst.content.registry.ThirstItem;
import vip.fubuki.thirst.foundation.common.capability.IThirstCap;
import vip.fubuki.thirst.foundation.config.ClientConfig;
import vip.fubuki.thirst.foundation.config.CommonConfig;
import vip.fubuki.thirst.foundation.config.ItemSettingsConfig;
import vip.fubuki.thirst.foundation.config.KeyWordConfig;
import vip.fubuki.thirst.foundation.gui.ThirstBarRenderer;
import vip.fubuki.thirst.foundation.gui.appleskin.HUDOverlayHandler;
import vip.fubuki.thirst.foundation.gui.appleskin.TooltipOverlayHandler;
import vip.fubuki.thirst.foundation.network.ThirstModPacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Thirst.ID)
public class Thirst
{
    public static final String ID = "thirst";
    public static final NonNullSupplier<Registrate> REGISTRATE=NonNullSupplier.lazy(() ->Registrate.create(Thirst.ID));

    public Thirst()
    {

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::registerCapabilities);

        ThirstItem.register();

        if(ModList.get().isLoaded("create"))
        {
            CreateRegistry.register();
        }

        //configs
        ItemSettingsConfig.setup();
        CommonConfig.setup();
        ClientConfig.setup();
        KeyWordConfig.setup();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        WaterPurity.init();
        ThirstModPacketHandler.init();

        if(ModList.get().isLoaded("coldsweat"))
            ThirstHelper.shouldUseColdSweatCaps(true);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        if(ModList.get().isLoaded("create")){
            event.enqueueWork(ThirstPonders::register);
        }
        if(ModList.get().isLoaded("appleskin") && FMLEnvironment.dist.isClient())
        {
            HUDOverlayHandler.init();
            TooltipOverlayHandler.init();
        }
        ThirstBarRenderer.register();
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(IThirstCap.class);
    }

    public static ResourceLocation asResource(String path)
    {
        return new ResourceLocation(ID, path);
    }
}
