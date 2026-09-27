package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelToyAirBalloon extends ModelBase
{
    private final ModelRenderer balloon;
    private final ModelRenderer base;
    private final ModelRenderer rope;
    private final ModelRenderer rope_r1;
    private final ModelRenderer rope2;
    private final ModelRenderer rope2_r1;
    private final ModelRenderer rope3;
    private final ModelRenderer rope3_r1;
    private final ModelRenderer rope4;
    private final ModelRenderer rope4_r1;

    public ModelToyAirBalloon() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.balloon = new ModelRenderer(this);
        this.balloon.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.balloon.cubeList.add(new ModelBox(balloon, 0, 0, -5.0F, -10.0F, -5.0F, 10, 10, 10, 0.0F, false));
        this.balloon.cubeList.add(new ModelBox(balloon, 0, 20, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));

        this.base = new ModelRenderer(this);
        this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.base.cubeList.add(new ModelBox(base, 0, 37, -4.0F, -3.0F, -4.0F, 8, 3, 8, 0.0F, false));
        this.base.cubeList.add(new ModelBox(base, 32, 37, -4.0F, -4.0F, -4.0F, 8, 1, 8, 0.0F, false));

        this.rope = new ModelRenderer(this);
        this.rope.setRotationPoint(-2.0F, -3.0F, -2.0F);
        this.base.addChild(rope);


        this.rope_r1 = new ModelRenderer(this);
        this.rope_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rope.addChild(rope_r1);
        this.setRotationAngle(rope_r1, 0.0873F, 0.0F, -0.0873F);
        this.rope_r1.cubeList.add(new ModelBox(rope_r1, 0, 50, -1.0F, -7.0F, -1.0F, 1, 7, 1, 0.0F, false));

        this.rope2 = new ModelRenderer(this);
        this.rope2.setRotationPoint(2.0F, -3.0F, -2.0F);
        this.base.addChild(rope2);


        this.rope2_r1 = new ModelRenderer(this);
        this.rope2_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rope2.addChild(rope2_r1);
        this.setRotationAngle(rope2_r1, 0.0873F, 0.0F, 0.0873F);
        this.rope2_r1.cubeList.add(new ModelBox(rope2_r1, 0, 50, 0.0F, -7.0F, -1.0F, 1, 7, 1, 0.0F, false));

        this.rope3 = new ModelRenderer(this);
        this.rope3.setRotationPoint(-2.0F, -3.0F, 2.0F);
        this.base.addChild(rope3);


        this.rope3_r1 = new ModelRenderer(this);
        this.rope3_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rope3.addChild(rope3_r1);
        this.setRotationAngle(rope3_r1, -0.0873F, 0.0F, -0.0873F);
        this.rope3_r1.cubeList.add(new ModelBox(rope3_r1, 0, 50, -1.0F, -7.0F, 0.0F, 1, 7, 1, 0.0F, false));

        this.rope4 = new ModelRenderer(this);
        this.rope4.setRotationPoint(2.0F, -3.0F, 2.0F);
        this.base.addChild(rope4);


        this.rope4_r1 = new ModelRenderer(this);
        this.rope4_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rope4.addChild(rope4_r1);
        this.setRotationAngle(rope4_r1, -0.0873F, 0.0F, 0.0873F);
        this.rope4_r1.cubeList.add(new ModelBox(rope4_r1, 0, 50, 0.0F, -7.0F, 0.0F, 1, 7, 1, 0.0F, false));
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
        this.balloon.render(f5);
        this.base.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, (Entity)null);
    }
}
