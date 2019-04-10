package kiritekomai.agrianimal.client.render.entity.layers;

import kiritekomai.agrianimal.Reference;
import kiritekomai.agrianimal.client.render.entity.RenderAgriWolf;
import kiritekomai.agrianimal.entity.EntityAgriWolf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerAgriWolf implements LayerRenderer<EntityAgriWolf> {
	private static final ResourceLocation FARMER_COLLAR = new ResourceLocation(Reference.MODID,
			"textures/entities/agri_wolf/agri_wolf_collar.png");
	private final RenderAgriWolf farmerRenderer;

	public LayerAgriWolf(RenderAgriWolf farmerRendererIn) {
		this.farmerRenderer = farmerRendererIn;
	}

	public void render(EntityAgriWolf entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
			this.farmerRenderer.bindTexture(FARMER_COLLAR);
			float[] afloat = entitylivingbaseIn.getCollarColor().getColorComponentValues();
			GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
			this.farmerRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks,
					netHeadYaw, headPitch, scale);
		}
	}

	public boolean shouldCombineTextures() {
		return true;
	}

}
