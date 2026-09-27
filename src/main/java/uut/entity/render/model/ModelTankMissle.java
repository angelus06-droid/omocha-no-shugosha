package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelTankMissle extends ModelBase {
    public ModelRenderer main;
    
    public ModelTankMissle() {
        textureWidth = 64;
        textureHeight = 64;

        this.main = new ModelRenderer(this);
        this.main.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.main.cubeList.add(new ModelBox(main, 0, 0, -2.0F, -4.0F, -2.0F, 4, 4, 5, 0.0F, false));
        this.main.cubeList.add(new ModelBox(main, 0, 14, -1.0F, -3.0F, -5.0F, 2, 2, 1, 0.0F, false));
        this.main.cubeList.add(new ModelBox(main, 0, 9, -1.5F, -3.5F, -4.0F, 3, 3, 2, 0.0F, false));
        this.main.cubeList.add(new ModelBox(main, 13, 0, -1.5F, -3.5F, 3.0F, 3, 3, 1, 0.0F, false));
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
        this.main.render(f5);
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
