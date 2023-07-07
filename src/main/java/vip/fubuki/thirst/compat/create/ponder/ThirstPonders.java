package vip.fubuki.thirst.compat.create.ponder;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import plus.dragons.createcentralkitchen.foundation.ponder.PonderEntry;
import vip.fubuki.thirst.Thirst;
import vip.fubuki.thirst.compat.create.CreateRegistry;
import vip.fubuki.thirst.compat.create.ponder.scene.SandFilterScene;


public class ThirstPonders {
    public static final PonderTag PURIFICATION = new PonderTag(Thirst.asResource("purification")).item(CreateRegistry.SAND_FILTER_BLOCK.get().asItem(), true, false)
            .defaultLang("Purification", "Components which purifying water");

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Thirst.ID);

    public static void register(){
        HELPER.addStoryBoard(CreateRegistry.SAND_FILTER_BLOCK, "sand_filter", SandFilterScene::intro, AllPonderTags.FLUIDS, PURIFICATION);
    }
}
