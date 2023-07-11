package vip.fubuki.thirst.foundation.mixin.farmersdelight;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.item.SkilletItem;
import vip.fubuki.thirst.content.purity.WaterPurity;
import vip.fubuki.thirst.foundation.config.CommonConfig;

import java.util.Optional;

@Mixin(value = SkilletItem.class)
public abstract class MixinSkilletItem {
    @Inject(method = "finishUsingItem",at = @At("HEAD"), cancellable = true)
    private void finishUsingItem(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir){
        if (entity instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("Cooking")) {
                ItemStack cookingStack = ItemStack.of(tag.getCompound("Cooking"));
                Optional<CampfireCookingRecipe> cookingRecipe = SkilletItem.getCookingRecipe(cookingStack, level);
                cookingRecipe.ifPresent((recipe) -> {
                    ItemStack resultStack = recipe.assemble(new SimpleContainer());
                    if(WaterPurity.isWaterFilledContainer(cookingStack)) {
                       resultStack = WaterPurity.addPurity(cookingStack.copy(), Math.min(WaterPurity.getPurity(cookingStack) + CommonConfig.CAMPFIRE_PURIFICATION_LEVELS.get().intValue() , WaterPurity.MAX_PURITY));
                    }
                    if (!player.getInventory().add(resultStack)) {
                        player.drop(resultStack, false);
                    }

                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
                    }

                });
                tag.remove("Cooking");
                tag.remove("CookTimeHandheld");
            }
        }

        cir.setReturnValue(stack);
        cir.cancel();
    }
}
