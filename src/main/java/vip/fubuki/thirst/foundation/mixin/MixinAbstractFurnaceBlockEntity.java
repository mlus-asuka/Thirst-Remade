package vip.fubuki.thirst.foundation.mixin;

import net.minecraft.core.RegistryAccess;
import vip.fubuki.thirst.content.purity.WaterPurity;
import vip.fubuki.thirst.foundation.config.CommonConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity
{
    @Shadow
    protected abstract boolean canBurn(RegistryAccess registryAccess,@Nullable Recipe<?> p_155006_, NonNullList<ItemStack> p_155007_, int p_155008_);

    @Inject(method = "canBurn", at = @At("HEAD"), cancellable = true)
    private void blockPotions(RegistryAccess p_266924_, Recipe<?> p_155006_, NonNullList<ItemStack> item, int p_155008_, CallbackInfoReturnable<Boolean> cir)
    {
        if(WaterPurity.isWaterFilledContainer(item.get(0)))
        {
            if(WaterPurity.getPurity(item.get(0)) == WaterPurity.MAX_PURITY)
                cir.setReturnValue(false);
        }
        else if(item.get(0).is(Items.POTION))
            cir.setReturnValue(false);
    }

    @Inject(method = "burn", at = @At("HEAD"), cancellable = true)
    private void burnPurityContainers(RegistryAccess registryAccess, Recipe<?> recipe, NonNullList<ItemStack> item, int p_267157_, CallbackInfoReturnable<Boolean> cir)
    {
        if (recipe != null && this.canBurn(registryAccess,recipe, item, p_267157_))
        {
            ItemStack itemstack = item.get(0);
            ItemStack itemstack2 = item.get(2);

            if(WaterPurity.isWaterFilledContainer(itemstack))
            {
                ItemStack itemstack1 = WaterPurity.getFilledContainer(itemstack, true);
                int purity = WaterPurity.getPurity(itemstack);
                WaterPurity.addPurity(itemstack1, Math.min(purity + CommonConfig.FURNACE_PURIFICATION_LEVELS.get().intValue(), 3));

                if (itemstack2.isEmpty())
                {
                    item.set(2, itemstack1.copy());
                }
                else if (itemstack2.is(itemstack1.getItem()))
                {
                    itemstack2.grow(itemstack1.getCount());
                }

                itemstack.shrink(1);

                cir.setReturnValue(true);
            }
        }
    }
}
