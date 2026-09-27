package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.MathHelper;

public class ModelToyArrowTower extends ModelBase {
    public ModelRenderer platform;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;
    public ModelRenderer topplatform;
    public ModelRenderer stick;
    public ModelRenderer banner;
    public ModelRenderer bannermiddle;
    public ModelRenderer bannerend;

    public ModelToyArrowTower() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.platform = new ModelRenderer(this);
        this.platform.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.platform.cubeList.add(new ModelBox(platform, 12, 2, -4.0F, -1.0F, -4.0F, 8, 1, 8, 0.0F, false));

        this.leg1 = new ModelRenderer(this);
        this.leg1.setRotationPoint(-2.0F, 23.0F, -2.0F);
        this.leg1.cubeList.add(new ModelBox(leg1, 0, 0, -1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F, false));

        this.leg2 = new ModelRenderer(this);
        this.leg2.setRotationPoint(2.0F, 23.0F, -2.0F);
        this.leg2.cubeList.add(new ModelBox(leg2, 0, 0, -1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F, false));

        this.leg3 = new ModelRenderer(this);
        this.leg3.setRotationPoint(-2.0F, 23.0F, 2.0F);
        this.leg3.cubeList.add(new ModelBox(leg3, 0, 0, -1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F, false));

        this.leg4 = new ModelRenderer(this);
        this.leg4.setRotationPoint(2.0F, 23.0F, 2.0F);
        this.leg4.cubeList.add(new ModelBox(leg4, 0, 0, -1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F, false));

        this.topplatform = new ModelRenderer(this);
        this.topplatform.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.topplatform.cubeList.add(new ModelBox(topplatform, 0, 13, -4.0F, -18.0F, -4.0F, 8, 8, 8, 0.0F, false));

        this.stick = new ModelRenderer(this);
        this.stick.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.stick.cubeList.add(new ModelBox(stick, 0, 29, -0.5F, -23.0F, -0.5F, 1, 5, 1, 0.0F, false));

        this.banner = new ModelRenderer(this);
        this.banner.setRotationPoint(0.5F, 2.0F, 0.0F);
        this.banner.cubeList.add(new ModelBox(banner, 6, 30, 0.0F, -1.0F, 0.0F, 2, 4, 0, 0.0F, false));

        this.bannermiddle = new ModelRenderer(this);
        this.bannermiddle.setRotationPoint(2.0F, 1.0F, 0.0F);
        this.banner.addChild(bannermiddle);
        this.bannermiddle.cubeList.add(new ModelBox(bannermiddle, 10, 30, 0.0F, -2.0F, 0.0F, 2, 4, 0, 0.0F, false));

        this.bannerend = new ModelRenderer(this);
        this.bannerend.setRotationPoint(2.0F, -1.0F, 0.0F);
        this.bannermiddle.addChild(bannerend);
        this.bannerend.cubeList.add(new ModelBox(bannerend, 14, 30, 0.0F, -1.0F, 0.0F, 2, 4, 0, 0.0F, false));
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
        this.banner.render(f5);
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        this.banner.rotateAngleY = MathHelper.cos(f2 * 0.15F) * 0.12F;
        this.bannermiddle.rotateAngleY = MathHelper.cos((f2 * 0.15F) - 0.4F) * 0.18F;
        this.bannerend.rotateAngleY = MathHelper.cos((f2 * 0.15F) - 0.8F) * 0.22F;

        this.banner.rotateAngleZ = 0.0F;
        this.bannermiddle.rotateAngleZ = 0.0F;
        this.bannerend.rotateAngleZ = 0.0F;
    }
}