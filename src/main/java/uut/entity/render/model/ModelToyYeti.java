package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import uut.entity.*;


public class ModelToyYeti extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer horn;
	private final ModelRenderer horn2;
	private final ModelRenderer body;
	private final ModelRenderer rightarm;
	private final ModelRenderer rightarm_r1;
	private final ModelRenderer leftarm;
	private final ModelRenderer leftarm_r1;
	private final ModelRenderer rightleg;
	private final ModelRenderer leftleg;

	public ModelToyYeti() {
		this.textureWidth = 64;
		this.textureHeight = 64;

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, 8.0F, 1.0F);
		this.head.cubeList.add(new ModelBox(head, 0, 0, -3.5F, -6.5F, -4.5F, 7, 7, 6, 0.0F, false));

		this.horn = new ModelRenderer(this);
		this.horn.setRotationPoint(-2.0F, -4.5F, -2.5F);
		this.head.addChild(horn);
		this.setRotationAngle(horn, 0.0F, 0.0F, -0.9599F);
		this.horn.cubeList.add(new ModelBox(horn, 35, 2, -0.4F, -3.0F, -1.0F, 2, 3, 2, 0.0F, true));

		this.horn2 = new ModelRenderer(this);
		this.horn2.setRotationPoint(2.0F, -4.5F, -2.5F);
		this.head.addChild(horn2);
		this.setRotationAngle(horn2, 0.0F, 0.0F, 0.9599F);
		this.horn2.cubeList.add(new ModelBox(horn2, 35, 2, -1.6F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 17.5F, 3.0F);
		this.setRotationAngle(body, 0.2793F, 0.0F, 0.0F);
		this.body.cubeList.add(new ModelBox(body, 14, 13, -4.0F, -10.5F, -3.0F, 8, 10, 6, 0.0F, false));

		this.rightarm = new ModelRenderer(this);
		this.rightarm.setRotationPoint(-4.0F, 10.0F, 1.5F);
		

		this.rightarm_r1 = new ModelRenderer(this);
		this.rightarm_r1.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.rightarm.addChild(rightarm_r1);
		this.setRotationAngle(rightarm_r1, -0.3491F, 0.0F, 0.0F);
		this.rightarm_r1.cubeList.add(new ModelBox(rightarm_r1, 42, 17, -4.0F, -2.0F, -2.0F, 4, 9, 4, 0.0F, false));

		this.leftarm = new ModelRenderer(this);
		this.leftarm.setRotationPoint(4.0F, 10.0F, 1.5F);
		

		this.leftarm_r1 = new ModelRenderer(this);
		this.leftarm_r1.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.leftarm.addChild(leftarm_r1);
		this.setRotationAngle(leftarm_r1, -0.2618F, 0.0F, 0.0F);
		this.leftarm_r1.cubeList.add(new ModelBox(leftarm_r1, 42, 17, 0.0F, -2.0F, -2.0F, 4, 9, 4, 0.0F, true));

		this.rightleg = new ModelRenderer(this);
		this.rightleg.setRotationPoint(-3.0F, 17.0F, 3.0F);
		this.rightleg.cubeList.add(new ModelBox(rightleg, 0, 13, -2.0F, -1.0F, -2.0F, 3, 8, 4, 0.0F, false));

		this.leftleg = new ModelRenderer(this);
		this.leftleg.setRotationPoint(3.0F, 17.0F, 3.0F);
		this.leftleg.cubeList.add(new ModelBox(leftleg, 0, 13, -1.0F, -1.0F, -2.0F, 3, 8, 4, 0.0F, true));
	}

	public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		head.render(f5);
		leftleg.render(f5);
		rightleg.render(f5);
		body.render(f5);
		leftarm.render(f5);
		rightarm.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
    
    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        final float f = 1.0f;
		this.leftleg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.rightleg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
        this.leftleg.rotateAngleY = 0.0f;
        this.rightleg.rotateAngleY = 0.0f;
        this.leftleg.rotateAngleZ = 0.0f;
        this.rightleg.rotateAngleZ = 0.0f;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;
    }

	public void setLivingAnimations(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTickTime) {
		final EntityToyYeti entityirongolem = (EntityToyYeti)entitylivingbaseIn;
		final int i = entityirongolem.getAttackTimer();
		if (i > 0) {
			this.rightarm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
			this.leftarm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
		}
		else {
			this.rightarm.rotateAngleX = (-0.2f + 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
			this.leftarm.rotateAngleX = (-0.2f - 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
		}
	}
    
    private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }
}