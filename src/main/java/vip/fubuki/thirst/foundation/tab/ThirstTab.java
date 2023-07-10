package vip.fubuki.thirst.foundation.tab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import vip.fubuki.thirst.Thirst;
import vip.fubuki.thirst.compat.create.CreateRegistry;
import vip.fubuki.thirst.content.purity.WaterPurity;
import vip.fubuki.thirst.content.registry.ThirstItem;

import java.util.ArrayList;
import java.util.Collection;

public class ThirstTab
{
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Thirst.ID);


    public static final RegistryObject<CreativeModeTab> THIRST_TAB = TAB_REGISTER.register("thirst",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.thirst"))
                    .icon(ThirstItem.TERRACOTTA_WATER_BOWL.get()::getDefaultInstance)
                    .displayItems((displayParameters, output) -> output.acceptAll(DisplayItems()))
                    .build());

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }

    public static Collection<ItemStack> DisplayItems() {
        Collection<ItemStack> list=new ArrayList<>();
        list.add(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 0));
        list.add(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 1));
        list.add(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 2));
        list.add(WaterPurity.addPurity(new ItemStack(Items.WATER_BUCKET), 3));
        list.add(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 0));
        list.add(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 1));
        list.add(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 2));
        list.add(WaterPurity.addPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), 3));
        list.add(ThirstItem.CLAY_BOWL.asStack());
        list.add(ThirstItem.TERRACOTTA_BOWL.asStack());
        list.add(ThirstItem.TERRACOTTA_WATER_BOWL.asStack());
        if(Thirst.CreateLoaded){
            list.add(CreateRegistry.SAND_FILTER_BLOCK.asStack());
        }
        return list;
    }
}
