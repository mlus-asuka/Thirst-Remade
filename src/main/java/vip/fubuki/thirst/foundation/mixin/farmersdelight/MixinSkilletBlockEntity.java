package vip.fubuki.thirst.foundation.mixin.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
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
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vip.fubuki.thirst.content.purity.WaterPurity;
import vip.fubuki.thirst.foundation.config.CommonConfig;

import java.util.Optional;

@Mixin(value = SkilletBlockEntity.class,remap = false)
public class MixinSkilletBlockEntity extends SyncedBlockEntity {

    @Shadow
    private int cookingTime;

    @Shadow
    private int cookingTimeTotal;

    @Shadow
    private ResourceLocation lastRecipeID;

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
                    if(cookingStack.getItem()== Items.POTION && PotionUtils.getPotion(cookingStack)== Potions.WATER){
                        resultStack = WaterPurity.addPurity(cookingStack.copy(), Math.min(WaterPurity.getPurity(cookingStack) + CommonConfig.FURNACE_PURIFICATION_LEVELS.get().intValue() , WaterPurity.MAX_PURITY));
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

    private ItemStackHandler createHandler() {
        return new ItemStackHandler() {
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }
        };
    }

    private Optional<CampfireCookingRecipe> getMatchingRecipe(Container recipeWrapper) {
        if (this.level == null) {
            return Optional.empty();
        } else {
            if (this.lastRecipeID != null) {
                Recipe<Container> recipe = ((RecipeManagerAccessor)this.level.getRecipeManager()).getRecipeMap(RecipeType.CAMPFIRE_COOKING).get(this.lastRecipeID);
                if (recipe instanceof CampfireCookingRecipe && recipe.matches(recipeWrapper, this.level)) {
                    return Optional.of((CampfireCookingRecipe)recipe);
                }
            }

            Optional<CampfireCookingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, recipeWrapper, this.level);
            if (recipe.isPresent()) {
                this.lastRecipeID = recipe.get().getId();
                return recipe;
            } else {
                return Optional.empty();
            }
        }
    }
}

