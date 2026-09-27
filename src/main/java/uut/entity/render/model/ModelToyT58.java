package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelToyT58 extends ModelBase {
    public ModelRenderer base;
    public ModelRenderer base_r1;
    public ModelRenderer base_r2;
    public ModelRenderer base_r3;
    public ModelRenderer base_r4;
    public ModelRenderer base_r5;
    public ModelRenderer base_r6;
    public ModelRenderer glass;

    public ModelToyT58() {
        textureWidth = 64;
        textureHeight = 64;

        this.base = new ModelRenderer(this);
        this.base.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.base.cubeList.add(new ModelBox(base, 18, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));
        this.base.cubeList.add(new ModelBox(base, 34, 0, -1.0F, -3.0F, 0.0F, 2, 1, 5, 0.0F, false));
        this.base.cubeList.add(new ModelBox(base, 25, 10, -1.5F, -1.5F, 5.0F, 3, 3, 1, 0.0F, false));

        this.base_r1 = new ModelRenderer(this);
        this.base_r1.setRotationPoint(1.5F, 2.0F, 5.0F);
        this.base.addChild(base_r1);
        this.setRotationAngle(base_r1, 0.5672F, 0.0F, -0.3491F);
        this.base_r1.cubeList.add(new ModelBox(base_r1, 0, -2, 0.0F, -2.0F, -2.0F, 0, 3, 2, 0.0F, false));

        this.base_r2 = new ModelRenderer(this);
        this.base_r2.setRotationPoint(-1.5F, 2.0F, 5.0F);
        this.base.addChild(base_r2);
        this.setRotationAngle(base_r2, 0.5672F, 0.0F, 0.3491F);
        this.base_r2.cubeList.add(new ModelBox(base_r2, 0, -2, 0.0F, -2.0F, -2.0F, 0, 3, 2, 0.0F, false));

        this.base_r3 = new ModelRenderer(this);
        this.base_r3.setRotationPoint(-2.5F, -2.0F, 2.0F);
        this.base.addChild(base_r3);
        this.setRotationAngle(base_r3, -0.829F, 0.0F, -0.48F);
        this.base_r3.cubeList.add(new ModelBox(base_r3, 51, -2, 0.0F, -4.0F, 0.0F, 0, 4, 2, 0.0F, false));

        this.base_r4 = new ModelRenderer(this);
        this.base_r4.setRotationPoint(2.5F, -2.0F, 2.0F);
        this.base.addChild(base_r4);
        this.setRotationAngle(base_r4, -0.829F, 0.0F, 0.48F);
        this.base_r4.cubeList.add(new ModelBox(base_r4, 51, -2, 0.0F, -4.0F, 0.0F, 0, 4, 2, 0.0F, false));

        this.base_r5 = new ModelRenderer(this);
        this.base_r5.setRotationPoint(2.0F, 1.0F, 1.0F);
        this.base.addChild(base_r5);
        this.setRotationAngle(base_r5, 0.0F, 0.1309F, 0.0F);
        this.base_r5.cubeList.add(new ModelBox(base_r5, 0, 9, -0.5F, -2.0F, -1.0F, 2, 2, 6, 0.0F, false));

        this.base_r6 = new ModelRenderer(this);
        this.base_r6.setRotationPoint(-3.0F, 1.0F, 1.0F);
        this.base.addChild(base_r6);
        this.setRotationAngle(base_r6, 0.0F, -0.1309F, 0.0F);
        this.base_r6.cubeList.add(new ModelBox(base_r6, 0, 9, -0.5F, -2.0F, -1.0F, 2, 2, 6, 0.0F, false));

        this.glass = new ModelRenderer(this);
        this.glass.setRotationPoint(2.0F, -2.5F, -1.0F);
        this.base.addChild(glass);
        this.setRotationAngle(glass, 0.2618F, 0.0F, 0.0F);
        this.glass.cubeList.add(new ModelBox(glass, 0, 0, -3.5F, 1.0F, -5.0F, 3, 3, 6, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        base.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
