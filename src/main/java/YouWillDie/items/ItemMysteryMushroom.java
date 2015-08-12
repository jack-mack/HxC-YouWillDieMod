package YouWillDie.items;

import YouWillDie.ModRegistry;
import YouWillDie.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemMysteryMushroom extends Item {

	public IIcon overlay, icon;

	public ItemMysteryMushroom() {
		super();
		setHasSubtypes(true);
        setUnlocalizedName("mysteryMushroom");
        setCreativeTab(ModRegistry.tabYouWillDie);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		icon = par1IconRegister.registerIcon("YouWillDie:mushroom");
		overlay = par1IconRegister.registerIcon("YouWillDie:mushroom_overlay");
		itemIcon = icon;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(int i = 0; i < 13; i++) {par3List.add(new ItemStack(par1, 1, i));}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return PacketHandler.mushroomColors[par1ItemStack.getItemDamage() % 13][par2 % 2];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
		return par2 > 0 ? overlay : icon;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		Block i1 = par3World.getBlock(par4, par5, par6);

		if (i1 == Blocks.snow && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
		{
			par7 = 1;
		}
		else if (i1 != Blocks.vine && i1 != Blocks.tallgrass && i1 != Blocks.deadbush
				&& (i1 == null || !i1.isReplaceable(par3World, par4, par5, par6)))
		{
			if (par7 == 0)
			{
				--par5;
			}

			if (par7 == 1)
			{
				++par5;
			}

			if (par7 == 2)
			{
				--par6;
			}

			if (par7 == 3)
			{
				++par6;
			}

			if (par7 == 4)
			{
				--par4;
			}

			if (par7 == 5)
			{
				++par4;
			}
		}

		if (par1ItemStack.stackSize == 0)
		{
			return false;
		}
		else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
		{
			return false;
		}
		else if (par5 == 255 && ModRegistry.mysteryMushroomBlock.getMaterial().isSolid())
		{
			return false;
		}
		else if (par3World.canPlaceEntityOnSide(ModRegistry.mysteryMushroomBlock, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack))
		{

			Block block = ModRegistry.mysteryMushroomBlock;
			int j1 = par1ItemStack.getItemDamage();
			int k1 = ModRegistry.mysteryMushroomBlock.onBlockPlaced(par3World, par4, par5, par6, par7, par8, par9, par10, j1);

			if(onItemUseFirst(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10, k1)) {
				par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
				--par1ItemStack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (!world.setBlock(x, y, z, ModRegistry.mysteryMushroomBlock, metadata, 3))
		{
			return false;
		}

		if (world.getBlock(x, y, z) == ModRegistry.mysteryMushroomBlock)
		{
            ModRegistry.mysteryMushroomBlock.onBlockPlacedBy(world, x, y, z, player, stack);
            ModRegistry.mysteryMushroomBlock.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}
}
