package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import uut.entity.EntityToyPrincessTroll;


public class ModelToyPrincessTroll extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer rightArm;
	private final ModelRenderer leftArm;
	private final ModelRenderer head;
	private final ModelRenderer rightLeg;
	private final ModelRenderer leftLeg;

	public ModelToyPrincessTroll() {
		this.textureWidth = 64;
		this.textureHeight = 32;

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 19.0F, 4.0F);


		ModelRenderer bodyMain = new ModelRenderer(this);
		bodyMain.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body.addChild(bodyMain);
		this.setRotationAngle(bodyMain, 0.7418F, 0.0F, 0.0F);
		bodyMain.cubeList.add(new ModelBox(bodyMain, 16, 13, -3.0F, -9.0F, -2.0F, 6, 9, 4, 0.0F, false));
		bodyMain.cubeList.add(new ModelBox(bodyMain, 53, 15, 0.0F, -8.0F, 2.0F, 0, 8, 2, 0.0F, false));

		this.rightArm = new ModelRenderer(this);
		this.rightArm.setRotationPoint(-5.0F, -4.0F, -5.0F);
		this.body.addChild(rightArm);
		this.rightArm.cubeList.add(new ModelBox(rightArm, 0, 12, -2.0F, -1.0F, -2.0F, 4, 10, 4, 0.0F, false));

		this.leftArm = new ModelRenderer(this);
		this.leftArm.setRotationPoint(5.0F, -4.0F, -5.0F);
		this.body.addChild(leftArm);
		this.leftArm.cubeList.add(new ModelBox(leftArm, 0, 12, -2.0F, -1.0F, -2.0F, 4, 10, 4, 0.0F, true));

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, -6.0F, -5.0F);
		this.body.addChild(head);
		this.head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -5.0F, -4.0F, 8, 6, 6, 0.0F, false));

		this.rightLeg = new ModelRenderer(this);
		this.rightLeg.setRotationPoint(-1.5F, 18.0F, 3.5F);
		this.rightLeg.cubeList.add(new ModelBox(rightLeg, 36, 17, -2.0F, 0.0F, -1.5F, 3, 6, 3, 0.0F, false));

		this.leftLeg = new ModelRenderer(this);
		this.leftLeg.setRotationPoint(1.5F, 18.0F, 3.5F);
		this.leftLeg.cubeList.add(new ModelBox(leftLeg, 36, 17, -1.0F, 0.0F, -1.5F, 3, 6, 3, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
		rightLeg.render(f5);
		leftLeg.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        final float f = 1.0f;
		this.leftLeg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
		this.rightLeg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
        this.leftLeg.rotateAngleY = 0.0f;
        this.rightLeg.rotateAngleY = 0.0f;
        this.leftLeg.rotateAngleZ = 0.0f;
        this.rightLeg.rotateAngleZ = 0.0f;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.head.rotateAngleX = headPitch * 0.017453292F;
    }

    public void setLivingAnimations(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTickTime) {
        final EntityToyPrincessTroll entityirongolem = (EntityToyPrincessTroll) entitylivingbaseIn;
        final int i = entityirongolem.getAttackTimer();
        if (i > 0) {
            this.rightArm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
            this.leftArm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(i - partialTickTime, 10.0f);
        }
        else {
            this.rightArm.rotateAngleX = (-0.2f + 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
            this.leftArm.rotateAngleX = (-0.2f - 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount;
        }
    }

    private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }
}