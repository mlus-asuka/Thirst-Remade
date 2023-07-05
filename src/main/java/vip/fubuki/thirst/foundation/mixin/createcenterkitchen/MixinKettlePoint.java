package vip.fubuki.thirst.foundation.mixin.createcenterkitchen;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import vip.fubuki.thirst.content.purity.WaterPurity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createcentralkitchen.content.logistics.block.mechanicalArm.KettlePoint;
import plus.dragons.createcentralkitchen.content.logistics.item.guide.brewing.BrewingGuide;
import umpaz.farmersrespite.common.block.KettleBlock;

@Mixin(KettlePoint.class)
public abstract class MixinKettlePoint extends ArmInteractionPoint {

    public MixinKettlePoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Inject(method = "insertWater", at = @At("HEAD"),remap = false)
    private void insertWater(BrewingGuide guide, IItemHandler inventory, ItemStack stack, boolean simulate, CallbackInfoReturnable<ItemStack> cir){
        BlockState state = this.level.getBlockState(pos);
        int waterLevel = state.getValue(KettleBlock.WATER_LEVEL);
        int waterIncrement = 0;

        if (stack.is(Items.WATER_BUCKET)) {
            waterIncrement = 3 - waterLevel;
        } else if (stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.WATER) {
            waterIncrement = Math.min(stack.getCount(), 3 - waterLevel);
        }

        if (waterIncrement > 0 && !simulate) {
            int purity = WaterPurity.getPurity(stack);
            int blockPurity = !state.hasProperty(WaterPurity.BLOCK_PURITY) ? WaterPurity.MAX_PURITY : (state.getValue(WaterPurity.BLOCK_PURITY) - 1 < 0 ? WaterPurity.MAX_PURITY : state.getValue(WaterPurity.BLOCK_PURITY) - 1);
            level.setBlockAndUpdate(pos, state.setValue(WaterPurity.BLOCK_PURITY, Math.max(purity, blockPurity)+1));
        }
    }
}
