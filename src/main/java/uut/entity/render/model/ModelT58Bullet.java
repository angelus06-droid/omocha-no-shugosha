package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelT58Bullet extends ModelBase {
    public ModelRenderer bullet;

    public ModelT58Bullet() {
        textureWidth = 16;
        textureHeight = 16;
        this.bullet = new ModelRenderer(this);
        this.bullet.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.bullet.cubeList.add(new ModelBox(bullet, 0, -6, 0.0F, -1.0F, -3.0F, 0, 3, 6, 0.0F, false));
        this.bullet.cubeList.add(new ModelBox(bullet, -6, 4, -1.5F, 0.5F, -3.0F, 3, 0, 6, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GlStateManager.disableCull();
        this.bullet.render(f5);
        GlStateManager.enableCull();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    public void setRotationAngles(final float limbSwing, final float limbSwingAngle, final float entityTickTime, final float rotationYaw, final float rotationPitch, final float unitPixel, final Entity entity) {
    }
}
