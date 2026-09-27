package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelToyArrowTowerInfinity extends ModelBase {
    ModelRenderer platform;
    ModelRenderer leg1;
    ModelRenderer leg2;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer topplatform;
    ModelRenderer stick;

    public ModelToyArrowTowerInfinity() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.platform = new ModelRenderer(this);
        this.platform.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.platform.cubeList.add(new ModelBox(platform, 16, 0, -5.0F, -2.0F, -5.0F, 10, 3, 10, -0.1F, false));

        this.leg1 = new ModelRenderer(this);
        this.leg1.setRotationPoint(-2.0F, 23.0F, -2.0F);
        this.leg1.cubeList.add(new ModelBox(leg1, 0, 0, -2.0F, -9.5F, -2.0F, 4, 10, 4, -0.1F, false));

        this.leg2 = new ModelRenderer(this);
        this.leg2.setRotationPoint(2.0F, 23.0F, -2.0F);
        this.leg2.cubeList.add(new ModelBox(leg2, 0, 0, -2.0F, -9.5F, -2.0F, 4, 10, 4, -0.1F, false));

        this.leg3 = new ModelRenderer(this);
        this.leg3.setRotationPoint(-2.0F, 23.0F, 2.0F);
        this.leg3.cubeList.add(new ModelBox(leg3, 0, 0, -2.0F, -9.5F, -2.0F, 4, 10, 4, -0.1F, false));

        this.leg4 = new ModelRenderer(this);
        this.leg4.setRotationPoint(2.0F, 23.0F, 2.0F);
        this.leg4.cubeList.add(new ModelBox(leg4, 0, 0, -2.0F, -9.5F, -2.0F, 4, 10, 4, -0.1F, false));

        this.topplatform = new ModelRenderer(this);
        this.topplatform.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.topplatform.cubeList.add(new ModelBox(topplatform, 0, 14, -5.0F, -19.0F, -5.0F, 10, 10, 10, -0.1F, false));

        this.stick = new ModelRenderer(this);
        this.stick.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.stick.cubeList.add(new ModelBox(stick, 0, 34, -1.5F, -23.5F, -1.5F, 3, 6, 3, -0.1F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.platform.render(f5);
        this.leg1.render(f5);
        this.leg2.render(f5);
        this.leg3.render(f5);
        this.leg4.render(f5);
        this.topplatform.render(f5);
        this.stick.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}