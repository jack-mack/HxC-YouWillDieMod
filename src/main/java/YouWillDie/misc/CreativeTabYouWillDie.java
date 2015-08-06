package YouWillDie.misc;

import YouWillDie.ModRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabYouWillDie {
    public static CreativeTabs tabYouWillDie = new CreativeTabs("ywd"){
        public Item getTabIconItem() {
            return ModRegistry.sceptre;
        }
    };
}