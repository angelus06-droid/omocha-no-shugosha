package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelToyTank extends ModelBase
{
    public ModelRenderer base;
    public ModelRenderer light;
    public ModelRenderer baseBack;
    public ModelRenderer roof;
    public ModelRenderer cannon;
    public ModelRenderer bone2;
    public ModelRenderer wheel;
    public ModelRenderer wheel2;
    
    public ModelToyTank() {
        textureWidth = 64;
        textureHeight = 64;

        this.base = new ModelRenderer(this);
        this.base.setRotationPoint(0.0F, 22.0F, -2.0F);
        this.base.cubeList.add(new ModelBox(base, 0, 0, -5.5F, -4.0F, -5.0F, 11, 4, 14, 0.0F, false));
        this.base.cubeList.add(new ModelBox(base, 36, 0, -2.0F, 0.0F, -3.0F, 4, 1, 10, 0.0F, false));

        this.light = new ModelRenderer(this);
        this.light.setRotationPoint(0.0F, -3.0F, -5.0F);
        this.base.addChild(light);
        this.setRotationAngle(light, -0.2182F, 0.0F, 0.0F);
        this.light.cubeList.add(new ModelBox(light, 1, 1, -5.0F, 0.0F, -0.5F, 2, 2, 1, 0.0F, false));
        this.light.cubeList.add(new ModelBox(light, 1, 1, 3.0F, 0.0F, -0.5F, 2, 2, 1, 0.0F, false));

        this.baseBack = new ModelRenderer(this);
        this.baseBack.setRotationPoint(0.0F, -4.0F, 9.0F);
        this.base.addChild(baseBack);
        this.setRotationAngle(baseBack, 0.5672F, 0.0F, 0.0F);
        this.baseBack.cubeList.add(new ModelBox(baseBack, 35, 36, -4.5F, 0.0F, -2.0F, 9, 3, 2, 0.0F, false));

        this.roof = new ModelRenderer(this);
        this.roof.setRotationPoint(0.0F, -4.0F, 2.5F);
        this.base.addChild(roof);
        this.roof.cubeList.add(new ModelBox(roof, 0, 23, -4.0F, -4.0F, -3.5F, 8, 4, 7, 0.0F, false));
        this.roof.cubeList.add(new ModelBox(roof, 0, 34, -3.0F, -4.0F, -4.5F, 6, 4, 1, 0.0F, false));
        this.roof.cubeList.add(new ModelBox(roof, 0, 18, -3.0F, -4.0F, 3.5F, 6, 4, 1, 0.0F, false));

        this.cannon = new ModelRenderer(this);
        this.cannon.setRotationPoint(0.0F, -2.0F, -4.5F);
        this.roof.addChild(cannon);
        this.cannon.cubeList.add(new ModelBox(cannon, 24, 20, -1.0F, -1.0F, -6.0F, 2, 2, 7, 0.0F, false));

        this.bone2 = new ModelRenderer(this);
        this.bone2.setRotationPoint(0.0F, -4.0F, 3.0F);
        this.roof.addChild(bone2);
        this.setRotationAngle(bone2, -0.0436F, 0.0F, 0.0F);
        this.bone2.cubeList.add(new ModelBox(bone2, 40, 26, -3.0F, -0.5F, -6.0F, 6, 1, 6, 0.0F, false));

        this.wheel = new ModelRenderer(this);
        this.wheel.setRotationPoint(-1.0F, 22.0F, -3.0F);
        this.wheel.cubeList.add(new ModelBox(wheel, 0, 46, -4.0F, -3.0F, -3.5F, 3, 5, 13, 0.0F, false));

        this.wheel2 = new ModelRenderer(this);
        this.wheel2.setRotationPoint(-1.0F, 24.0F, -3.0F);
        this.wheel2.cubeList.add(new ModelBox(wheel2, 0, 46, 3.0F, -5.0F, -3.5F, 3, 5, 13, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        base.render(f5);
        wheel.render(f5);
        wheel2.render(f5);
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.roof.rotateAngleY = f3 / (180F / (float)Math.PI);
        this.cannon.rotateAngleX = f4 / (180F / (float)Math.PI);

        float speed = 0.5F;
        float amplitude = 0.2F;
        this.base.rotationPointY = 22.0F + (float)Math.cos(f * speed) * f1 * amplitude;

    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
