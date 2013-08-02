package assets.fyresmodjam;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class UnmarkedPotionData extends WorldSavedData {

	public static String key = "FyresWorldData";
	
	public static int[] potionValues = null;
	public static int[] potionDurations = null;

	public UnmarkedPotionData() {
		super(key);
	}

	public UnmarkedPotionData(String key) {
		super(key);
	}

	public static UnmarkedPotionData forWorld(World world) {
		MapStorage storage = world.perWorldStorage;
		UnmarkedPotionData result = (UnmarkedPotionData) storage.loadData(UnmarkedPotionData.class, key);
		if (result == null) {result = new UnmarkedPotionData(); storage.setData(key, result); }
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if(nbttagcompound.hasKey("values")) {potionValues = nbttagcompound.getIntArray("values");}
		checkPotionValues();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		checkPotionValues();
		nbttagcompound.setIntArray("values", potionValues);
	}
	
	public void checkPotionValues() {
		if(potionValues == null) {
			potionValues = new int[12];
			
			for(int i = 0; i < 12; i++) {
				int i2 = ModjamMod.r.nextInt(Potion.potionTypes.length);
				while(Potion.potionTypes[i2] == null) {i2 = ModjamMod.r.nextInt(Potion.potionTypes.length);}
				
				//boolean skip = false;
				//for(int i3 = 0; i3 < potionValues.length; i3++) {if(potionValues[i] == i3) {skip = true; break;}}
				//if(skip) {continue;}
						
				potionValues[i] = i2;// break;
				potionDurations[i] = 5 + ModjamMod.r.nextInt(26);
			}
		} else {
			for(int i = 0; i < 12; i++) {
				if(Potion.potionTypes[potionValues[i]] == null) {
					int i2 = ModjamMod.r.nextInt(Potion.potionTypes.length);
					while(Potion.potionTypes[i2] == null) {i2 = ModjamMod.r.nextInt(Potion.potionTypes.length);}
					potionValues[i] = i2;
				}
			}
		}
	}
}
