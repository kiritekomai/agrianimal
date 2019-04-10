package kiritekomai.agrianimal.client.render.entity;

import kiritekomai.agrianimal.Reference;
import kiritekomai.agrianimal.client.render.entity.layers.LayerAgriWolf;
import kiritekomai.agrianimal.client.render.entity.model.ModelAgriWolf;
import kiritekomai.agrianimal.entity.EntityAgriWolf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderAgriWolf extends RenderLiving<EntityAgriWolf> {
	private static final ResourceLocation FARMER_TEXTURES = new ResourceLocation(Reference.MODID,
			"textures/entities/agri_wolf/agri_wolf.png");
	private static final ResourceLocation TAMED_FARMER_TEXTURES = new ResourceLocation(Reference.MODID,
			"textures/entities/agri_wolf/agri_wolf_tame.png");
	private static final ResourceLocation TAMED_HARVESTING_FARMER_TEXTURES = new ResourceLocation(Reference.MODID,
			"textures/entities/agri_wolf/agri_wolf_harvest.png");
	private static final ResourceLocation ANRGY_FARMER_TEXTURES = new ResourceLocation(Reference.MODID,
			"textures/entities/agri_wolf/agri_wolf_angry.png");

	public RenderAgriWolf(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelAgriWolf(), 0.5F);
		this.addLayer(new LayerAgriWolf(this));
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(EntityAgriWolf livingBase, float partialTicks) {
		return livingBase.getTailRotation();
	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	public void doRender(EntityAgriWolf entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (entity.isWolfWet()) {
			float f = entity.getBrightness() * entity.getShadingWhileWet(partialTicks);
			GlStateManager.color3f(f, f, f);
		}

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityAgriWolf entity) {
		if (entity.isTamed()) {
			if (entity.isHarvesting()) {
				return TAMED_HARVESTING_FARMER_TEXTURES;

			} else {
				return TAMED_FARMER_TEXTURES;

			}
		} else {
			return entity.isAngry() ? ANRGY_FARMER_TEXTURES : FARMER_TEXTURES;
		}
	}

}
