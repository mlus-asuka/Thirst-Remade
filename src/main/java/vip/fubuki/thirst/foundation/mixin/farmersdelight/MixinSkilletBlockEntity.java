package vip.fubuki.thirst.foundation.mixin.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vip.fubuki.thirst.content.purity.WaterPurity;
import vip.fubuki.thirst.foundation.config.CommonConfig;

import java.util.Optional;

@Mixin(value = SkilletBlockEntity.class,remap = false)
public abstract class MixinSkilletBlockEntity extends SyncedBlockEntity {

    @Shadow
    private int cookingTime;

    @Shadow
    private int cookingTimeTotal;

    @Final
    @Shadow
    private final ItemStackHandler inventory = this.createHandler();

    public MixinSkilletBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Inject(method="cookAndOutputItems",at = @At("HEAD"), cancellable = true)
    private void cookAndOutputItems(ItemStack cookingStack, CallbackInfo ci) {
        if (this.level != null) {
            ++this.cookingTime;
            if (this.cookingTime >= this.cookingTimeTotal) {
                SimpleContainer wrapper = new SimpleContainer(cookingStack);
                Optional<CampfireCookingRecipe> recipe = this.getMatchingRecipe(wrapper);
                if (recipe.isPresent()) {
                    ItemStack resultStack = recipe.get().assemble(wrapper);

                    if(WaterPurity.isWaterFilledContainer(cookingStack)) {
                        resultStack = WaterPurity.addPurity(cookingStack.copy(), Math.min(WaterPurity.getPurity(cookingStack) + CommonConfig.CAMPFIRE_PURIFICATION_LEVELS.get().intValue() , WaterPurity.MAX_PURITY));
                        resultStack.setCount(1);
                    }

                    Direction direction = this.getBlockState().getValue(SkilletBlock.FACING).getClockWise();
                    ItemUtils.spawnItemEntity(this.level, resultStack.copy(), (double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.3, (double)this.worldPosition.getZ() + 0.5, (float)direction.getStepX() * 0.08F, 0.25, (float)direction.getStepZ() * 0.08F);
                    this.cookingTime = 0;
                    this.inventory.extractItem(0, 1, false);
                }
            }

        }
        ci.cancel();
    }

    @Shadow
    protected abstract ItemStackHandler createHandler();

    @Shadow
    protected abstract Optional<CampfireCookingRecipe> getMatchingRecipe(Container recipeWrapper);
}
