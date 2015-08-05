package YouWillDie.items;

import YouWillDie.ModRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class ItemObsidianSceptre extends Item {

	public IIcon icon, icon2;

	public ItemObsidianSceptre() {
		super();
		hasSubtypes = true;
        setUnlocalizedName("sceptre");
        setCreativeTab(ModRegistry.tabYouWillDie);
        setFull3D();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		icon = par1IconRegister.registerIcon("YouWillDie:unenchantedSceptre");
		icon2 = par1IconRegister.registerIcon("YouWillDie:enchantedSceptre");
		itemIcon = icon;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(int i = 0; i < 2; i++) {par3List.add(new ItemStack(par1, 1, i));}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack par1ItemStack) {
		return (par1ItemStack.getItemDamage() == 0 ? "" : "Infused ") + super.getItemStackDisplayName(par1ItemStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return par1 == 0 ? icon : icon2;
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return par1ItemStack.getItemDamage() > 0;
	}
}
