package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelToySpaceship extends ModelBase {
    public ModelRenderer all;
    public ModelRenderer base;
    public ModelRenderer glass;
    public ModelRenderer orbit;
    public ModelRenderer orbit_r1;

    public ModelToySpaceship() {
        textureWidth = 64;
        textureHeight = 64;

        this.all = new ModelRenderer(this);
        all.setRotationPoint(0.0F, 21.0F, 0.0F);


        this.base = new ModelRenderer(this);
        this.base.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.all.addChild(base);
        this.base.cubeList.add(new ModelBox(base, 0, 11, -3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F, false));
        this.base.cubeList.add(new ModelBox(base, 20, 0, -3.0F, -4.0F, -3.0F, 6, 1, 6, 0.0F, false));
        this.base.cubeList.add(new ModelBox(base, 24, 7, -2.0F, 0.5F, -2.0F, 4, 1, 4, 0.0F, false));

        this.glass = new ModelRenderer(this);
        this.glass.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.all.addChild(glass);
        this.glass.cubeList.add(new ModelBox(glass, 0, 0, -2.5F, -5.0F, -2.5F, 5, 2, 5, 0.0F, false));
        this.glass.cubeList.add(new ModelBox(glass, 0, 7, -1.5F, -6.0F, -1.5F, 3, 1, 3, 0.0F, false));

        this.orbit = new ModelRenderer(this);
        this.orbit.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.all.addChild(orbit);
        this.orbit.cubeList.add(new ModelBox(orbit, 0, 34, -6.5F, 0.0F, -2.5F, 13, 3, 5, 0.0F, false));
        this.orbit.cubeList.add(new ModelBox(orbit, 7, 18, -2.5F, 0.0F, -6.5F, 5, 3, 13, 0.0F, false));
        this.orbit.cubeList.add(new ModelBox(orbit, 45, 0, -1.0F, 2.5F, -5.0F, 2, 1, 1, 0.0F, false));
        this.orbit.cubeList.add(new ModelBox(orbit, 45, 0, -1.0F, 2.5F, 4.0F, 2, 1, 1, 0.0F, false));
        this.orbit.cubeList.add(new ModelBox(orbit, 51, 0, -5.0F, 2.5F, -1.0F, 1, 1, 2, 0.0F, false));
        this.orbit.cubeList.add(new ModelBox(orbit, 51, 0, 4.0F, 2.5F, -1.0F, 1, 1, 2, 0.0F, false));

        this.orbit_r1 = new ModelRenderer(this);
        this.orbit_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.orbit.addChild(orbit_r1);
        this.setRotationAngle(orbit_r1, 0.0F, 0.7854F, 0.0F);
        this.orbit_r1.cubeList.add(new ModelBox(orbit_r1, 0, 23, -2.5F, 0.0F, -6.5F, 5, 3, 5, -0.05F, false));
        this.orbit_r1.cubeList.add(new ModelBox(orbit_r1, 0, 23, -2.5F, 0.0F, 1.5F, 5, 3, 5, -0.05F, false));
        this.orbit_r1.cubeList.add(new ModelBox(orbit_r1, 36, 34, 1.5F, 0.0F, -2.5F, 5, 3, 5, -0.05F, false));
        this.orbit_r1.cubeList.add(new ModelBox(orbit_r1, 36, 34, -6.5F, 0.0F, -2.5F, 5, 3, 5, -0.05F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        all.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        this.orbit.rotateAngleY = f2 * 0.15F;
        this.all.rotateAngleX = (float) Math.sin(f2 * 0.05F) * 0.05F;
        this.all.rotateAngleZ = (float) Math.cos(f2 * 0.05F) * 0.05F;
    }
}
