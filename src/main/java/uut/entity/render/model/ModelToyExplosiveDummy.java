package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;

public class ModelToyExplosiveDummy extends ModelBase
{
	ModelRenderer head;
	ModelRenderer tnt;
	ModelRenderer body;
	ModelRenderer rightarm;
	ModelRenderer rightarm_r1;
	ModelRenderer leftarm;
	ModelRenderer leftarm_r1;
	ModelRenderer stick;
    
    public ModelToyExplosiveDummy() {
		this.textureWidth = 64;
		this.textureHeight = 64;

		this.head = new ModelRenderer(this);
		this.head.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.setRotationAngle(head, 0.1309F, 0.0F, -0.0873F);
		this.head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		this.tnt = new ModelRenderer(this);
		this.tnt.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head.addChild(tnt);
		this.tnt.cubeList.add(new ModelBox(tnt, 0, 52, -3.0F, -14.0F, -3.0F, 6, 6, 6, 0.0F, false));

		this.body = new ModelRenderer(this);
		this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.body.cubeList.add(new ModelBox(body, 16, 16, -4.0F, -25.0F, -2.0F, 8, 12, 4, 0.0F, false));
		this.body.cubeList.add(new ModelBox(body, 16, 32, -4.0F, -13.0F, -2.0F, 8, 2, 4, 0.0F, false));
		this.body.cubeList.add(new ModelBox(body, 36, 52, -4.5F, -22.0F, -2.5F, 9, 7, 5, 0.0F, false));

		this.rightarm = new ModelRenderer(this);
		this.rightarm.setRotationPoint(-6.0F, 1.0F, 0.0F);
		this.setRotationAngle(rightarm, 0.0F, 0.0F, 1.5708F);
		

		this.rightarm_r1 = new ModelRenderer(this);
		this.rightarm_r1.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.rightarm.addChild(rightarm_r1);
		this.setRotationAngle(rightarm_r1, 0.0F, 0.0436F, -0.2182F);
		this.rightarm_r1.cubeList.add(new ModelBox(rightarm_r1, 40, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		this.leftarm = new ModelRenderer(this);
		this.leftarm.setRotationPoint(6.0F, 1.0F, 0.0F);
		this.setRotationAngle(leftarm, 0.0F, 0.0F, -1.5708F);
		

		this.leftarm_r1 = new ModelRenderer(this);
		this.leftarm_r1.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.leftarm.addChild(leftarm_r1);
		this.setRotationAngle(leftarm_r1, 0.0F, 0.0873F, 0.2182F);
		this.leftarm_r1.cubeList.add(new ModelBox(leftarm_r1, 40, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));

		this.stick = new ModelRenderer(this);
		this.stick.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.stick.cubeList.add(new ModelBox(stick, 0, 16, -1.0F, -13.0F, -1.0F, 2, 13, 2, 0.0F, false));
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5);
        this.head.render(f5);
        this.body.render(f5);
        this.rightarm.render(f5);
        this.leftarm.render(f5);
        this.stick.render(f5);
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
