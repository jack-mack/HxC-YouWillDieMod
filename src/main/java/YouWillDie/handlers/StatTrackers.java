package YouWillDie.handlers;

import YouWillDie.misc.EntityStatHelper;
import YouWillDie.misc.ItemStatHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

import java.util.Random;

public class StatTrackers {
    public static void TrackStats () {
    //Entity Trackers

    EntityStatHelper.EntityStatTracker playerTracker = new EntityStatHelper.EntityStatTracker(EntityPlayer.class, true);

    playerTracker.addStat(new EntityStatHelper.EntityStat("BlessingCooldown", "" + 0));
    playerTracker.addStat(new EntityStatHelper.EntityStat("BlessingCounter", "" + 0));

    EntityStatHelper.EntityStatTracker mobTracker = new EntityStatHelper.EntityStatTracker(EntityMob.class, true);

    mobTracker.addStat(new EntityStatHelper.EntityStat("Level", "") {
        @Override
        public Object getNewValue(Random r) {
            int i = 1;
            for(; i < 5; i++) {if(YouWillDie.YouWillDie.r.nextInt(5) < 3) {break;}}
            return i;
        }

        @Override
        public String getAlteredEntityName(EntityLiving entity) {
            int level = 1;

            try {
                level = Integer.parseInt(entity.getEntityData().getString(name));
            } catch (Exception e) {e.printStackTrace();}

            return (level == 5 ? "\u00A7c" : "") + entity.getCommandSenderName() + ", Level " + level;
        }

        @Override
        public void modifyEntity(Entity entity) {
            int level = 1;

            try {
                level = Integer.parseInt(entity.getEntityData().getString(name));
            } catch (Exception e) {e.printStackTrace();}

            int healthGain = (int) ((level - 1) * (((EntityLivingBase) entity).getMaxHealth()/4) + (level == 5 ? ((EntityLivingBase) entity).getMaxHealth()/4 : 0));

            if(healthGain != 0) {
                ((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(((EntityLivingBase) entity).getMaxHealth() + healthGain);
                ((EntityLivingBase) entity).setHealth(((EntityLivingBase) entity).getMaxHealth() + healthGain);
            }

            if(level == 5) {
                switch(YouWillDie.YouWillDie.r.nextInt(4)) {

                    case 0:
                        if(entity instanceof IRangedAttackMob) {entity.getEntityData().setString("Blessing", "Hunter");}
                        else {entity.getEntityData().setString("Blessing", "Warrior");}
                        break;

                    case 1: entity.getEntityData().setString("Blessing", "Swamp"); break;
                    case 2: entity.getEntityData().setString("Blessing", "Guardian"); break;
                    case 3: entity.getEntityData().setString("Blessing", "Vampire"); break;

                    default: break;

                }

                if(entity instanceof EntityCreeper) {
                    entity.getDataWatcher().updateObject(17, (byte) 1);
                    entity.getEntityData().setBoolean("powered", true);
                }
            }
        }
    });

    EntityStatHelper.addStatTracker(mobTracker);

    //Item Trackers

    ItemStatHelper.ItemStatTracker weaponTracker = new ItemStatHelper.ItemStatTracker(new Class[] {ItemSword.class, ItemAxe.class, ItemBow.class}, true);

    weaponTracker.addStat(new ItemStatHelper.ItemStat("Rank", "") {

        public String[][] prefixesByRank = {
                {"Old", "Dull", "Broken", "Worn"},
                {"Average", "Decent", "Modest", "Ordinary"},
                {"Strong", "Sharp", "Polished", "Refined"},
                {"Powerful", "Ruthless", "Elite", "Astonishing"},
                {"Godly", "Divine", "Fabled", "Legendary"}
        };

        @Override
        public Object getNewValue(ItemStack stack, Random r) {
            int i = 1;
            for(; i < 5; i++) {if(YouWillDie.YouWillDie.r.nextInt(10) < 7) {break;}}
            return i;
        }

        @Override
        public void modifyStack(ItemStack stack, Random r) {
            int rank = Integer.parseInt(stack.getTagCompound().getString(name));
            float bonusDamage = ((float) rank - 1)/2 + (YouWillDie.YouWillDie.r.nextInt(rank + 1) * YouWillDie.YouWillDie.r.nextFloat());

            ItemStatHelper.giveStat(stack, "BonusDamage", String.format("%.2f", bonusDamage));
            ItemStatHelper.addLore(stack, !String.format("%.2f", bonusDamage).equals("0.00") ? "\u00A77\u00A7o  " + (bonusDamage > 0 ? "+" : "") + String.format("%.2f", bonusDamage) + " bonus damage" : null);

            ItemStatHelper.addLore(stack, "\u00A7eRank: "+ rank);
        }

        @Override
        public String getAlteredStackName(ItemStack stack, Random r) {
            String[] list = prefixesByRank[Integer.parseInt(stack.getTagCompound().getString(name)) - 1];
            String prefix = list[YouWillDie.YouWillDie.r.nextInt(list.length)];

            if(prefix.equals("Sharp") && stack.getItem() instanceof ItemBow) {prefix = "Long";}

            return "\u00A7f" + prefix + " " + stack.getDisplayName();
        }

    });

    ItemStatHelper.addStatTracker(weaponTracker);

    ItemStatHelper.ItemStatTracker armorTracker = new ItemStatHelper.ItemStatTracker(new Class[] {ItemArmor.class}, true);

    armorTracker.addStat(new ItemStatHelper.ItemStat("Rank", "") {

        public String[][] prefixesByRank = {
                {"Old", "Broken", "Worn", "Weak"},
                {"Average", "Decent", "Modest", "Ordinary"},
                {"Polished", "Tough", "Hardened", "Durable"},
                {"Elite", "Astonishing", "Reinforced", "Resilient"},
                {"Godly", "Divine", "Fabled", "Legendary"}
        };

        @Override
        public Object getNewValue(ItemStack stack, Random r) {
            int i = 1;
            for(; i < 5; i++) {if(YouWillDie.YouWillDie.r.nextInt(10) < 7) {break;}}
            return i;
        }

        @Override
        public void modifyStack(ItemStack stack, Random r) {
            int rank = Integer.parseInt(stack.getTagCompound().getString(name));
            float damageReduction = (rank - 1) + YouWillDie.YouWillDie.r.nextFloat() * 0.5F;

            ItemStatHelper.giveStat(stack, "DamageReduction", String.format("%.2f", damageReduction));
            ItemStatHelper.addLore(stack, !String.format("%.2f", damageReduction).equals("0.00") ? "\u00A77\u00A7o  " + (damageReduction > 0 ? "+" : "") + String.format("%.2f", damageReduction) + "% damage reduction" : null);

            ItemStatHelper.addLore(stack, "\u00A7eRank: "+ rank);
        }

        @Override
        public String getAlteredStackName(ItemStack stack, Random r) {
            String[] list = prefixesByRank[Integer.parseInt(stack.getTagCompound().getString(name)) - 1];
            String prefix = list[YouWillDie.YouWillDie.r.nextInt(list.length)];

            if(prefix.equals("Sharp") && stack.getItem() instanceof ItemBow) {prefix = "Long";}

            return "\u00A7f" + prefix + " " + stack.getDisplayName();
        }

    });

    ItemStatHelper.addStatTracker(armorTracker);
    }
}
