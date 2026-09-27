package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSentinelMissle extends ModelBase {
    public ModelRenderer bullet;

    public ModelSentinelMissle() {
        textureWidth = 32;
        textureHeight = 32;

        this.bullet = new ModelRenderer(this);
        this.bullet.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bullet.cubeList.add(new ModelBox(bullet, 0, 10, 0.0F, -2.0F, 0.5F, 0, 5, 3, 0.0F, false));
        this.bullet.cubeList.add(new ModelBox(bullet, -2, 10, -2.5F, 0.5F, 1.5F, 5, 0, 2, 0.0F, false));
        this.bullet.cubeList.add(new ModelBox(bullet, 0, 0, -1.5F, -1.0F, -2.5F, 3, 3, 5, 0.0F, false));
        this.bullet.cubeList.add(new ModelBox(bullet, 11, 0, -1.0F, -0.5F, -3.5F, 2, 2, 2, 0.0F, false));
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
        this.bullet.render(f5);
    }
    
    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, (Entity)null);
    }
}
