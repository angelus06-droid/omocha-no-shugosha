package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;

public class ModelToyBomby extends ModelBase
{
    ModelRenderer body;
    ModelRenderer leg1;
    ModelRenderer leg2;
    ModelRenderer top;
    ModelRenderer rope;
    ModelRenderer rope_r1;
    
    public ModelToyBomby() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, -12.0F, -4.0F, 8, 8, 8, 0.0F, false));

        this.leg1 = new ModelRenderer(this);
        this.leg1.setRotationPoint(-2.0F, 20.0F, 0.0F);
        this.leg1.cubeList.add(new ModelBox(leg1, 0, 19, -1.5F, 1.0F, -3.5F, 3, 3, 5, 0.0F, false));
        this.leg1.cubeList.add(new ModelBox(leg1, 0, 16, -1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F, false));

        this.leg2 = new ModelRenderer(this);
        this.leg2.setRotationPoint(2.0F, 20.0F, 0.0F);
        this.leg2.cubeList.add(new ModelBox(leg2, 0, 19, -1.5F, 1.0F, -3.5F, 3, 3, 5, 0.0F, true));
        this.leg2.cubeList.add(new ModelBox(leg2, 0, 16, -1.0F, 0.0F, -1.0F, 2, 1, 2, 0.0F, true));

        this.top = new ModelRenderer(this);
        this.top.setRotationPoint(2.0F, 13.0F, -2.0F);
        this.top.cubeList.add(new ModelBox(top, 32, 0, -5.0F, -2.0F, -1.0F, 6, 2, 6, 0.0F, false));

        this.rope = new ModelRenderer(this);
        this.rope.setRotationPoint(0.0F, 11.0F, 0.0F);


        this.rope_r1 = new ModelRenderer(this);
        this.rope_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rope.addChild(rope_r1);
        this.setRotationAngle(rope_r1, -0.3491F, -0.2618F, -0.0873F);
        this.rope_r1.cubeList.add(new ModelBox(rope_r1, 56, 0, -0.5F, -3.0F, -0.5F, 1, 4, 1, 0.0F, false));
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
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 1.4442f) * 1.0f * limbSwingAmount / f;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 1.4442f + 3.1415927f) * 1.0f * limbSwingAmount / f;
        this.leg1.rotateAngleY = 0.0f;
        this.leg2.rotateAngleY = 0.0f;
        this.leg1.rotateAngleZ = 0.0f;
        this.leg2.rotateAngleZ = 0.0f;
    }
}
