package xenoscape.worldsretold.hellfire.entity.hostile.livingflame;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xenoscape.worldsretold.hailstorm.entity.passive.caribou.EntityCaribou;
import xenoscape.worldsretold.hailstorm.entity.passive.caribou.RenderCaribou;
import xenoscape.worldsretold.hailstorm.entity.passive.nix.EntityNix;
import xenoscape.worldsretold.hailstorm.entity.passive.nix.ModelNix;

@SideOnly(Side.CLIENT)
public class RenderLivingFlame extends RenderLiving<EntityLivingFlame>
{
	public static final RenderLivingFlame.Factory FACTORY = new RenderLivingFlame.Factory();
	
	protected RenderLivingFlame(RenderManager renderManagerIn) 
	{
		super(renderManagerIn, new ModelNix(), 0F);
	}

	protected ResourceLocation getEntityTexture(EntityLivingFlame entity) 
	{
		return null;
	}

    public static class Factory implements IRenderFactory<EntityLivingFlame>
    {
        @Override
        public Render<? super EntityLivingFlame> createRenderFor(RenderManager manager) 
        {
            return new RenderLivingFlame(manager);
        }
    }
}
