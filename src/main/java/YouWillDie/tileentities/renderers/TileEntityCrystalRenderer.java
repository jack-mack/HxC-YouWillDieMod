package YouWillDie.tileentities.renderers;

import YouWillDie.ModRegistry;
import YouWillDie.items.ItemCrystal;
import YouWillDie.models.ModelCrystal;
import YouWillDie.tileentities.TileEntityCrystal;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TileEntityCrystalRenderer extends TileEntitySpecialRenderer {

	private ModelCrystal modelCrystal = new ModelCrystal();

	public static ResourceLocation texture = new ResourceLocation("youwilldie", "textures/blocks/crystal.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d0, (float) d1, (float) d2);

		TileEntityCrystal crystal = (TileEntityCrystal) tileentity;
		Block block = ModRegistry.crystal;
		World world = crystal.getWorldObj();

		Tessellator tessellator = Tessellator.instance;
		float f2 = block.getMixedBrightnessForBlock(world, crystal.xCoord, crystal.yCoord, crystal.zCoord);
		int l = world.getLightBrightnessForSkyBlocks(crystal.xCoord, crystal.yCoord, crystal.zCoord, 0);
		int l1 = l % 65536;
		int l2 = l / 65536;
		tessellator.setColorOpaque_F(f2, f2, f2);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

		float f3 = world.getWorldInfo().getWorldTime() % 20 / 20.0F;
		GL11.glTranslatef(0.5F, -0.4F + (f3 > 0.5F ? 0.25F - 0.25F * f3 : 0.25F * f3), 0.5F);

		GL11.glRotatef((float) world.getWorldInfo().getWorldTime() % 360, 0.0F, 1.0F, 0.0F);

		Color color = ItemCrystal.colors[crystal.getBlockMetadata() % ItemCrystal.colors.length];
		GL11.glColor3f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);

		bindTexture(texture);
		modelCrystal.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
	}

}
