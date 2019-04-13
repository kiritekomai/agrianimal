package kiritekomai.agrianimal.client.render.entity;

import kiritekomai.agrianimal.Reference;
import kiritekomai.agrianimal.client.render.entity.model.ModelAgriOcelot;
import kiritekomai.agrianimal.entity.EntityAgriOcelot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderAgriOcelot extends RenderLiving<EntityAgriOcelot> {
	private static final ResourceLocation OCELOT_TEXTURES = new ResourceLocation(Reference.MODID,
			"textures/entities/agri_ocelot/agri_ocelot.png");
	private static final ResourceLocation HARVESTING_OCELOT_TEXTURES = new ResourceLocation(Reference.MODID,
			"textures/entities/agri_ocelot/agri_ocelot_harvest.png");

	public RenderAgriOcelot(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelAgriOcelot(), 0.4F);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityAgriOcelot entity) {
		if(entity.isHarvesting()) {
			return HARVESTING_OCELOT_TEXTURES;
		}
		else {
			return OCELOT_TEXTURES;
		}
	}

	/**
	 * Allows the render to do state modifications necessary before the model is rendered.
	 */
	protected void preRenderCallback(EntityAgriOcelot entitylivingbaseIn, float partialTickTime) {
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
		if (entitylivingbaseIn.isTamed()) {
			GlStateManager.scalef(0.8F, 0.8F, 0.8F);
		}

	}
}