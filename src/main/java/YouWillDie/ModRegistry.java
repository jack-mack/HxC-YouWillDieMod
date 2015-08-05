package YouWillDie;

import YouWillDie.blocks.*;
import YouWillDie.commands.*;
import YouWillDie.entities.EntityMysteryPotion;
import YouWillDie.items.*;
import YouWillDie.misc.CreativeTabYouWillDie;
import YouWillDie.tileentities.TileEntityCrystal;
import YouWillDie.tileentities.TileEntityCrystalStand;
import YouWillDie.tileentities.TileEntityPillar;
import YouWillDie.tileentities.TileEntityTrap;
import YouWillDie.worldgen.PillarGen;
import YouWillDie.worldgen.WorldGenMoreDungeons;
import YouWillDie.worldgen.WorldGenTrapsTowersAndMore;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ChestGenHooks;

public class ModRegistry {
    public static CreativeTabs tabYouWillDie = CreativeTabYouWillDie.tabYouWillDie;

    public static Block blockPillar = new BlockPillar().setBlockUnbreakable().setResistance(-1);
    public static Block blockTrap = new BlockTrap().setBlockUnbreakable().setResistance(-1);
    public static Block mysteryMushroomBlock = new BlockMysteryMushroom();

    public static Item itemPillar = new ItemPillar();
    public static Item mysteryPotion = new ItemMysteryPotion();
    public static Item itemTrap = new ItemTrap();
    public static Item mysteryMushroom = new ItemMysteryMushroom();
    public static Item sceptre = new ItemObsidianSceptre();

    public static Item crystalItem = new ItemCrystal();
    public static Item scroll = new ItemScroll();

    public static Block crystal = new BlockCrystal();
    public static Block crystalStand = new BlockCrystalStand();

    public static Achievement startTheGame;
    public static Achievement losingIsFun;
    public static Achievement whoops;

    public static Achievement theHunt;
    public static Achievement jackOfAllTrades;

    public static AchievementPage page;

    public static void preInit(){
        registerBlocks();
        registerItems();
        registerTileEntities();
        registerCraftingRecipes();
    }

    public static void init(){
        registerWorldGen();
        registerAchievements();
        registerEntities();
    }

    private static void registerBlocks() {
        GameRegistry.registerBlock(blockPillar, "blockPillar");
        GameRegistry.registerBlock(blockTrap, "blockTrap");
        GameRegistry.registerBlock(mysteryMushroomBlock, "mysteryMushroomBlock");
        GameRegistry.registerBlock(crystal, "crystal");
        GameRegistry.registerBlock(crystalStand, "crystalStand");
    }

    private static void registerItems() {
        GameRegistry.registerItem(mysteryPotion, "mysteryPotion");
        GameRegistry.registerItem(itemPillar, "itemPillar");
        GameRegistry.registerItem(itemTrap, "itemTrap");
        GameRegistry.registerItem(mysteryMushroom, "mysteryMushroom");
        GameRegistry.registerItem(sceptre, "sceptre");
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityCrystal.class, "Crystal Tile Entity");
        GameRegistry.registerTileEntity(TileEntityCrystalStand.class, "Crystal Stand Tile Entity");
        GameRegistry.registerTileEntity(TileEntityPillar.class, "Pillar Tile Entity");
        GameRegistry.registerTileEntity(TileEntityTrap.class, "Trap Entity");
    }
    private static void registerAchievements() {
        startTheGame = getNewAchievement(Config.AchievementID, 0, 0, new ItemStack(Items.iron_sword, 1), "startTheGame", null, true);
        losingIsFun = getNewAchievement(Config.AchievementID + 1, -2, 0, new ItemStack(itemTrap, 1), "losingIsFun", startTheGame, false);
        whoops = getNewAchievement(Config.AchievementID + 2, 2, 0, new ItemStack(itemTrap, 1, 1), "whoops", startTheGame, false);
        theHunt = getNewAchievement(Config.AchievementID + 3, 0, -2, new ItemStack(Items.bow, 1), "theHunt", startTheGame, false);
        jackOfAllTrades = getNewAchievement(Config.AchievementID + 4, 0, 2, new ItemStack(Blocks.crafting_table, 1), "jackOfAllTrades", startTheGame, false);

        page = new AchievementPage("The \"You Will Die\" Mod HxC-Edition", startTheGame, losingIsFun, whoops, theHunt, jackOfAllTrades);

        AchievementPage.registerAchievementPage(page);
    }
    private static void registerCraftingRecipes() {
        GameRegistry.addShapelessRecipe(new ItemStack(itemTrap, 1, 0), Blocks.heavy_weighted_pressure_plate, Blocks.cactus);
        GameRegistry.addShapelessRecipe(new ItemStack(itemTrap, 1, 1), Blocks.heavy_weighted_pressure_plate, Blocks.torch);
        GameRegistry.addShapelessRecipe(new ItemStack(itemTrap, 1, 2), Blocks.heavy_weighted_pressure_plate, new ItemStack(Items.dye, 1, 0));

        for(int i = 0; i < 13; i++) {
            GameRegistry.addShapelessRecipe(new ItemStack(mysteryPotion, 1, i + 13), new ItemStack(mysteryPotion, 1, i), Items.gunpowder);
            GameRegistry.addShapelessRecipe(new ItemStack(mysteryPotion, 1, i), new ItemStack(Items.potionitem, 1, 0), Items.leather, new ItemStack(mysteryMushroom, 1, i));
        }

        GameRegistry.addRecipe(new ItemStack(sceptre, 1, 0), "X", "Y", "X", 'X', Blocks.obsidian, 'Y', Blocks.end_stone);
        GameRegistry.addShapelessRecipe(new ItemStack(sceptre, 1, 1), new ItemStack(sceptre, 1, 0), Items.ender_pearl, Items.book);

    }
    private static void registerEntities() {
        EntityRegistry.registerGlobalEntityID(EntityMysteryPotion.class, "MysteryPotion", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityMysteryPotion.class, "MysteryPotion", 0, YouWillDie.instance, 128, 1, true);
    }

    public static void initCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCurrentBlessing());
        event.registerServerCommand(new CommandCurrentDisadvantage());
        event.registerServerCommand(new CommandCurrentWorldTask());
        event.registerServerCommand(new CommandKillStats());
        event.registerServerCommand(new CommandWeaponStats());
        event.registerServerCommand(new CommandCraftingStats());
    }

    public static Achievement getNewAchievement(int id, int x, int y, ItemStack stack, String name, Achievement prereq, boolean independent) {
        Achievement achievement = new Achievement("YWD-" + id, name, x, y, stack, prereq);
        if(independent) {achievement = achievement.initIndependentStat();}
        achievement.registerStat();
        return achievement;
    }

    private static void registerWorldGen() {
        GameRegistry.registerWorldGenerator(new PillarGen(), 0);
        GameRegistry.registerWorldGenerator(new WorldGenTrapsTowersAndMore(), 0);

        for(int i = 0; i < 3; i++) {GameRegistry.registerWorldGenerator(new WorldGenMoreDungeons(), 0);}

        for(int i = 0; i < 13; i++) {
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(mysteryPotion, i, 1, 3, 2));
            WorldGenTrapsTowersAndMore.chestGenInfo.addItem(new WeightedRandomChestContent(mysteryPotion, i, 1, 3, 2));
        }
    }
}
