package kiritekomai.agrianimal.client.render.entity.model;

import kiritekomai.agrianimal.entity.EntityAgriWolf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelAgriWolf extends ModelBase {

	/** main box for the farmer head */
	private final ModelRenderer farmerHeadMain;
	/** The farmer's body */
	private final ModelRenderer farmerBody;
	/** Wolf'se first leg */
	private final ModelRenderer farmerLeg1;
	/** Wolf's second leg */
	private final ModelRenderer farmerLeg2;
	/** Wolf's third leg */
	private final ModelRenderer farmerLeg3;
	/** Wolf's fourth leg */
	private final ModelRenderer farmerLeg4;
	/** The farmer's tail */
	private final ModelRenderer farmerTail;
	/** The farmer's mane */
	private final ModelRenderer farmerMane;

	public ModelAgriWolf() {
		this.farmerHeadMain = new ModelRenderer(this, 0, 0);
		this.farmerHeadMain.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
		this.farmerHeadMain.setRotationPoint(-1.0F, 13.5F, -7.0F);
		this.farmerBody = new ModelRenderer(this, 18, 14);
		this.farmerBody.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
		this.farmerBody.setRotationPoint(0.0F, 14.0F, 2.0F);
		this.farmerMane = new ModelRenderer(this, 21, 0);
		this.farmerMane.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
		this.farmerMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
		this.farmerLeg1 = new ModelRenderer(this, 0, 18);
		this.farmerLeg1.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.farmerLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
		this.farmerLeg2 = new ModelRenderer(this, 0, 18);
		this.farmerLeg2.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.farmerLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
		this.farmerLeg3 = new ModelRenderer(this, 0, 18);
		this.farmerLeg3.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.farmerLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
		this.farmerLeg4 = new ModelRenderer(this, 0, 18);
		this.farmerLeg4.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.farmerLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
		this.farmerTail = new ModelRenderer(this, 9, 18);
		this.farmerTail.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.farmerTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
		this.farmerHeadMain.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.farmerHeadMain.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.farmerHeadMain.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		if (this.isChild) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 5.0F * scale, 2.0F * scale);
			this.farmerHeadMain.renderWithRotation(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
			this.farmerBody.render(scale);
			this.farmerLeg1.render(scale);
			this.farmerLeg2.render(scale);
			this.farmerLeg3.render(scale);
			this.farmerLeg4.render(scale);
			this.farmerTail.renderWithRotation(scale);
			this.farmerMane.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.farmerHeadMain.renderWithRotation(scale);
			this.farmerBody.render(scale);
			this.farmerLeg1.render(scale);
			this.farmerLeg2.render(scale);
			this.farmerLeg3.render(scale);
			this.farmerLeg4.render(scale);
			this.farmerTail.renderWithRotation(scale);
			this.farmerMane.render(scale);
		}

	}

	/**
	 * Used for easily adding entity-dependent animations. The second and third float params here are the same second and
	 * third as in the setRotationAngles method.
	 */
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTickTime) {
		EntityAgriWolf entityfarmer = (EntityAgriWolf) entitylivingbaseIn;
		if (entityfarmer.isAngry()) {
			this.farmerTail.rotateAngleY = 0.0F;
		} else {
			this.farmerTail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}

		if (entityfarmer.isSitting() && !entityfarmer.isHarvesting()) {
			this.farmerMane.setRotationPoint(-1.0F, 16.0F, -3.0F);
			this.farmerMane.rotateAngleX = 1.2566371F;
			this.farmerMane.rotateAngleY = 0.0F;
			this.farmerBody.setRotationPoint(0.0F, 18.0F, 0.0F);
			this.farmerBody.rotateAngleX = ((float) Math.PI / 4F);
			this.farmerTail.setRotationPoint(-1.0F, 21.0F, 6.0F);
			this.farmerLeg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
			this.farmerLeg1.rotateAngleX = ((float) Math.PI * 1.5F);
			this.farmerLeg2.setRotationPoint(0.5F, 22.0F, 2.0F);
			this.farmerLeg2.rotateAngleX = ((float) Math.PI * 1.5F);
			this.farmerLeg3.rotateAngleX = 5.811947F;
			this.farmerLeg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
			this.farmerLeg4.rotateAngleX = 5.811947F;
			this.farmerLeg4.setRotationPoint(0.51F, 17.0F, -4.0F);
		} else {
			this.farmerBody.setRotationPoint(0.0F, 14.0F, 2.0F);
			this.farmerBody.rotateAngleX = ((float) Math.PI / 2F);
			this.farmerMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
			this.farmerMane.rotateAngleX = this.farmerBody.rotateAngleX;
			this.farmerTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
			this.farmerLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
			this.farmerLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
			this.farmerLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
			this.farmerLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
			this.farmerLeg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
			this.farmerLeg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F
					* limbSwingAmount;
			this.farmerLeg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F
					* limbSwingAmount;
			this.farmerLeg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}

		this.farmerHeadMain.rotateAngleZ = entityfarmer.getInterestedAngle(partialTickTime)
				+ entityfarmer.getShakeAngle(partialTickTime, 0.0F);
		this.farmerMane.rotateAngleZ = entityfarmer.getShakeAngle(partialTickTime, -0.08F);
		this.farmerBody.rotateAngleZ = entityfarmer.getShakeAngle(partialTickTime, -0.16F);
		this.farmerTail.rotateAngleZ = entityfarmer.getShakeAngle(partialTickTime, -0.2F);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
	 * arms and legs can swing at most.
	 */
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		this.farmerHeadMain.rotateAngleX = headPitch * ((float) Math.PI / 180F);
		this.farmerHeadMain.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
		this.farmerTail.rotateAngleX = ageInTicks;
	}
}
