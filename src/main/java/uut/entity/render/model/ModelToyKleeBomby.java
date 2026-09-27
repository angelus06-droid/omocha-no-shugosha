package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelToyKleeBomby extends ModelBase {
    public ModelRenderer body;
    public ModelRenderer rightear;
    public ModelRenderer leftear;
    public ModelRenderer back;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer top;
    public ModelRenderer rope;
    public ModelRenderer rope_r1;
    public ModelRenderer rope_r2;
    public ModelRenderer rope_r3;

    public ModelToyKleeBomby() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, -12.0F, -4.0F, 8, 7, 8, 0.0F, false));
        this.body.cubeList.add(new ModelBox(body, 0, 34, -4.0F, -12.0F, -4.0F, 8, 7, 8, 0.1F, false));

        this.rightear = new ModelRenderer(this);
        this.rightear.setRotationPoint(-3.0F, -12.0F, 0.0F);
        this.body.addChild(rightear);
        this.setRotationAngle(rightear, -0.2182F, 0.3054F, -1.0908F);
        this.rightear.cubeList.add(new ModelBox(rightear, 12, 17, -3.0F, -4.0F, -1.0F, 3, 5, 1, 0.0F, false));
        this.rightear.cubeList.add(new ModelBox(rightear, 12, 15, -2.0F, -5.0F, -1.0F, 2, 1, 1, 0.0F, false));

        this.leftear = new ModelRenderer(this);
        this.leftear.setRotationPoint(3.0F, -12.0F, 0.0F);
        this.body.addChild(leftear);
        this.setRotationAngle(leftear, -0.2182F, -0.3054F, 1.0908F);
        this.leftear.cubeList.add(new ModelBox(leftear, 12, 17, 0.0F, -4.0F, -1.0F, 3, 5, 1, 0.0F, true));
        this.leftear.cubeList.add(new ModelBox(leftear, 12, 15, 0.0F, -5.0F, -1.0F, 2, 1, 1, 0.0F, true));

        this.back = new ModelRenderer(this);
        this.back.setRotationPoint(0.0F, -7.0F, 4.0F);
        this.body.addChild(back);
        this.setRotationAngle(back, -1.2654F, 0.0F, 0.0F);
        this.back.cubeList.add(new ModelBox(back, 30, 47, -1.0F, 0.0F, 0.0F, 2, 0, 6, 0.0F, false));

        this.leg1 = new ModelRenderer(this);
        this.leg1.setRotationPoint(-3.0F, 19.0F, 0.0F);
        this.leg1.cubeList.add(new ModelBox(leg1, 0, 15, -0.5F, 0.0F, -1.5F, 3, 5, 3, 0.0F, false));

        this.leg2 = new ModelRenderer(this);
        this.leg2.setRotationPoint(3.0F, 19.0F, 0.0F);
        this.leg2.cubeList.add(new ModelBox(leg2, 0, 15, -2.5F, 0.0F, -1.5F, 3, 5, 3, 0.0F, true));

        this.top = new ModelRenderer(this);
        this.top.setRotationPoint(2.0F, 13.0F, -2.0F);
        this.top.cubeList.add(new ModelBox(top, 32, 0, -5.0F, -2.0F, -1.0F, 6, 1, 6, 0.0F, false));
        this.top.cubeList.add(new ModelBox(top, 31, 13, -5.0F, 5.5F, -1.0F, 6, 1, 6, 0.0F, false));

        this.rope = new ModelRenderer(this);
        this.rope.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.setRotationAngle(rope, -0.4363F, 0.0F, 0.0F);
        this.rope.cubeList.add(new ModelBox(rope, 56, 0, -0.5F, -3.0F, -0.5F, 1, 4, 1, 0.0F, false));

        this.rope_r1 = new ModelRenderer(this);
        this.rope_r1.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.rope.addChild(rope_r1);
        this.setRotationAngle(rope_r1, 0.0F, 0.2182F, 0.0F);
        this.rope_r1.cubeList.add(new ModelBox(rope_r1, 32, 28, 0.0F, 0.0F, 0.0F, 3, 0, 3, 0.0F, false));

        this.rope_r2 = new ModelRenderer(this);
        this.rope_r2.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.rope.addChild(rope_r2);
        this.setRotationAngle(rope_r2, 0.0F, -0.2182F, 0.0F);
        this.rope_r2.cubeList.add(new ModelBox(rope_r2, 26, 28, -3.0F, 0.0F, 0.0F, 3, 0, 3, 0.0F, false));

        this.rope_r3 = new ModelRenderer(this);
        this.rope_r3.setRotationPoint(0.0F, -2.5F, 0.0F);
        this.rope.addChild(rope_r3);
        this.setRotationAngle(rope_r3, 0.0F, -0.7854F, 0.0F);
        this.rope_r3.cubeList.add(new ModelBox(rope_r3, 26, 34, -3.0F, 0.0F, -3.0F, 3, 0, 3, 0.0F, false));
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.body.render(f5);
        this.leg1.render(f5);
        this.leg2.render(f5);
        this.top.render(f5);
        this.rope.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        final float f = 1.0f;

        this.leg2.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
        this.leg1.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
        this.leg1.rotateAngleY = 0.0f;
        this.leg2.rotateAngleY = 0.0f;
        this.leg1.rotateAngleZ = 0.0f;
        this.leg2.rotateAngleZ = 0.0f;

        float earMotion = MathHelper.cos(ageInTicks * 0.15F) * 0.08F;

        this.rightear.rotateAngleX = -0.2182F;
        this.rightear.rotateAngleY = 0.3054F;
        this.rightear.rotateAngleZ = -1.0908F + earMotion;

        this.leftear.rotateAngleX = -0.2182F;
        this.leftear.rotateAngleY = -0.3054F;
        this.leftear.rotateAngleZ = 1.0908F - earMotion;
    }

    private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }
}
