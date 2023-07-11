package vip.fubuki.thirst.foundation.mixin.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vip.fubuki.thirst.content.purity.WaterPurity;
import vip.fubuki.thirst.foundation.config.CommonConfig;

import java.util.Optional;

@Mixin(value = StoveBlockEntity.class,remap = false)
public abstract class MixinStoveBlockEntity extends SyncedBlockEntity {

    @Final
    @Shadow
    private final ItemStackHandler inventory = this.createHandler();

    @Final
    @Shadow
    private final int[] cookingTimes = new int[6];

    @Final
    @Shadow
    private final int[] cookingTimesTotal = new int[6];

    public MixinStoveBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }


    @Inject(method = "cookAndOutputItems", at = @At(value = "HEAD"), cancellable = true)
    private void cookAndOutputItems(CallbackInfo ci){
        if (this.level != null) {
            boolean didInventoryChange = false;

            for(int i = 0; i < this.inventory.getSlots(); ++i) {
                ItemStack stoveStack = this.inventory.getStackInSlot(i);
                if (!stoveStack.isEmpty()) {
                    this.cookingTimes[i]++;
                    if (this.cookingTimes[i] >= this.cookingTimesTotal[i]) {
                        Container inventoryWrapper = new SimpleContainer(stoveStack);
                        Optional<CampfireCookingRecipe> recipe = this.getMatchingRecipe(inventoryWrapper, i);
                        if (recipe.isPresent()) {
                            ItemStack resultStack = recipe.get().getResultItem();

                            if(WaterPurity.isWaterFilledContainer(stoveStack)) {
                                resultStack= WaterPurity.addPurity(stoveStack, Math.min(WaterPurity.getPurity(stoveStack) + CommonConfig.CAMPFIRE_PURIFICATION_LEVELS.get().intValue() , WaterPurity.MAX_PURITY));
                            }

                            if (!resultStack.isEmpty()) {
                                ItemUtils.spawnItemEntity(this.level, resultStack.copy(), (double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 1.0, (double)this.worldPosition.getZ() + 0.5, this.level.random.nextGaussian() * 0.009999999776482582, 0.10000000149011612, this.level.random.nextGaussian() * 0.009999999776482582);
                            }
                        }

                        this.inventory.setStackInSlot(i, ItemStack.EMPTY);
                        didInventoryChange = true;
                    }
                }
            }

            if (didInventoryChange) {
                this.inventoryChanged();
            }

        }
        ci.cancel();
    }

    @Shadow
    protected abstract ItemStackHandler createHandler();

    @Shadow
    public abstract Optional<CampfireCookingRecipe> getMatchingRecipe(Container recipeWrapper, int slot);

}
