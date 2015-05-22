package YouWillDie;

import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;

public class Config {
    public static boolean pillarGlow;
    public static int pillarGenChance;
    public static int maxPillarsPerChunk;
    public static int towerGenChance;
    public static int trapGenChance;
    public static int mushroomReplaceChance;
    public static boolean spawnTraps;
    public static boolean spawnTowers;
    public static boolean spawnRandomPillars;
    public static boolean Disadvantages;
    public static boolean enableMobKillStats;
    public static boolean enableWeaponKillStats;
    public static boolean enableCraftingStats;
    public static boolean trapsBelowGroundOnly;
    public static int ExamineKey;
    public static int BlessingKey;
    public static int AchievementID;
    public Config(Configuration config) {
        config.load();

        pillarGlow = config.getBoolean("PillarGlow", "Features", true, "Allow pillars to emit light?");

        pillarGenChance = config.getInt("PillarChance", "Features", 75, 0, 500, "Pillar generation chance.");
        maxPillarsPerChunk = config.getInt("MaxPillars", "Features", 3, 0, 500, "Max pillars per chunk.");
        towerGenChance = config.getInt("TowerChance", "Features", 225, 0, 500, "Tower generation chance.");
        trapGenChance = config.getInt("TrapChance", "Features", 300, 0, 500, "Hidden trap generation chance.");
        mushroomReplaceChance = config.getInt("ShroomChance", "Features", 15, 0, 500, "Mystery mushroom generation chance.");

        spawnTraps = config.getBoolean("Traps", "Features", true, "Spawn hidden traps during world generation?");
        spawnTowers = config.getBoolean("Towers", "Features", true, "Spawn towers of death during world generation?");
        spawnRandomPillars = config.getBoolean("RandomPillars", "Features", true, "Spawn random pillars during world generation?");
        Disadvantages = config.getBoolean("Disadvantages", "Features", true, "Disadvantages to world until task is completed?");

        enableMobKillStats = config.getBoolean("MobKillStats", "Features", true, "Enable mob kill stats?");
        enableWeaponKillStats = config.getBoolean("WeaponKillStats", "Features", true, "Enable weapon kill stats?");
        enableCraftingStats = config.getBoolean("CraftingStats", "Features", true, "Enable crafting stats?");
        trapsBelowGroundOnly = config.getBoolean("TrapsBelowGroundOnly", "Features", true, "Spawn hidden traps below ground only?");

        ExamineKey = config.getInt("ExamineKey", "Keybindings", Keyboard.KEY_X, 0, 0, "");
        BlessingKey = config.getInt("BlessingKey", "Keybindings", Keyboard.KEY_K, 0, 0, "");

        AchievementID = config.getInt("AchievementsID", "IDs", 2500, 1, 10000, "ID for achivements");

        if(config.hasChanged()){
            config.save();
        }
    }
}
