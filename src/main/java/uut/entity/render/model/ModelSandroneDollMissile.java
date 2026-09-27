package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSandroneDollMissile extends ModelBase {
    private final ModelRenderer glow;

    public ModelSandroneDollMissile() {
        textureWidth = 64;
        textureHeight = 64;

        glow = new ModelRenderer(this);
        glow.setRotationPoint(0.0F, 2.0F, 0.0F);
        glow.cubeList.add(new ModelBox(glow, 0, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float headYaw, float headPitch, float f5) {
        glow.render(f5);
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
