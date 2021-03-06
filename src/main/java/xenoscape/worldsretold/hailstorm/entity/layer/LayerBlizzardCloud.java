package xenoscape.worldsretold.hailstorm.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xenoscape.worldsretold.hailstorm.entity.hostile.blizzard.EntityBlizzard;
import xenoscape.worldsretold.hailstorm.entity.hostile.blizzard.ModelBlizzard;
import xenoscape.worldsretold.hailstorm.entity.hostile.blizzard.RenderBlizzard;


@SideOnly(Side.CLIENT)
public class LayerBlizzardCloud implements LayerRenderer<EntityBlizzard> {

	private ResourceLocation CLOUD_TEXTURE = new ResourceLocation("worldsretold:textures/entity/blizzard_cloud.png");
	private final RenderBlizzard blizzardRenderer;
	private final ModelBlizzard blizzardModel = new ModelBlizzard();

	public LayerBlizzardCloud(RenderBlizzard blizzardRendererIn) {
		this.blizzardRenderer = blizzardRendererIn;
	}

	public void doRenderLayer(EntityBlizzard entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!(entitylivingbaseIn.deathTicks > 0)) {
		boolean flag = entitylivingbaseIn.isInvisible();
		GlStateManager.depthMask(!flag);
		this.blizzardRenderer.bindTexture(CLOUD_TEXTURE);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		float f = (float) entitylivingbaseIn.ticksExisted + partialTicks;
		GlStateManager.translate(f * 0.0075F, f * 0.0075F, 0.0F);
		GlStateManager.matrixMode(5888);
		this.blizzardModel.Head.showModel = false;
		this.blizzardModel.setModelAttributes(this.blizzardRenderer.getMainModel());
		Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
		this.blizzardModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
				scale);
		Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.depthMask(flag);
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}
}
