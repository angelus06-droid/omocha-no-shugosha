package uut.entity.render.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.math.Rotations;

public class ModelPrincessCrown extends ModelBiped {
    private final ModelRenderer crown;

    public ModelPrincessCrown() {
        textureWidth = 64;
        textureHeight = 32;

        this.bipedHead.cubeList.clear();

        crown = new ModelRenderer(this);
        crown.setRotationPoint(0.0F, -8.0F, -3.0F);
        setRotationAngle(crown, -0.1745F, 0.0F, 0.0F);
        crown.cubeList.add(new ModelBox(crown, 0, 0, -2.5F, -4.0F, 0.0F, 5, 4, 5, 0.0F, false));

        this.bipedHead.addChild(crown);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        if (entityIn instanceof EntityArmorStand) {
            EntityArmorStand stand = (EntityArmorStand) entityIn;
            Rotations headRot = stand.getHeadRotation();

            this.bipedHead.rotateAngleX = (float) Math.toRadians(headRot.getX());
            this.bipedHead.rotateAngleY = (float) Math.toRadians(headRot.getY());
            this.bipedHead.rotateAngleZ = (float) Math.toRadians(headRot.getZ());
        }
    }
}