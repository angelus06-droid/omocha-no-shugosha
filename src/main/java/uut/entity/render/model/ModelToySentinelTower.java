package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelToySentinelTower extends ModelBase {
    public ModelRenderer root;
    public ModelRenderer leg;
    public ModelRenderer head;
    public ModelRenderer rightEar;
    public ModelRenderer rightEar_r1;
    public ModelRenderer leftEar;
    public ModelRenderer leftEar_r1;

    public ModelToySentinelTower() {
        textureWidth = 64;
        textureHeight = 64;

        this.root = new ModelRenderer(this);
        this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.root.cubeList.add(new ModelBox(root, 21, 37, 4.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F, false));
        this.root.cubeList.add(new ModelBox(root, 21, 37, -1.0F, -2.0F, -6.0F, 2, 2, 2, 0.0F, false));
        this.root.cubeList.add(new ModelBox(root, 21, 37, -1.0F, -2.0F, 4.0F, 2, 2, 2, 0.0F, true));
        this.root.cubeList.add(new ModelBox(root, 21, 37, -6.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F, false));
        this.root.cubeList.add(new ModelBox(root, 0, 52, -4.0F, -4.0F, -4.0F, 8, 4, 8, 0.0F, false));
        this.root.cubeList.add(new ModelBox(root, 32, 52, -4.0F, -4.0F, -4.0F, 8, 4, 8, 0.2F, false));

        this.leg = new ModelRenderer(this);
        this.leg.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.root.addChild(leg);
        this.leg.cubeList.add(new ModelBox(leg, 24, 52, -1.0F, -7.0F, -1.0F, 2, 6, 2, 0.0F, false));

        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.head.cubeList.add(new ModelBox(head, 0, 20, -4.0F, -3.0F, -4.0F, 8, 3, 8, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 0, 41, -4.0F, -3.0F, -4.0F, 8, 3, 8, 0.1F, false));
        this.head.cubeList.add(new ModelBox(head, 40, 37, -3.0F, 0.0F, -3.0F, 6, 3, 6, 0.1F, false));
        this.head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 3, 8, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 0, 11, -3.5F, -5.0F, -3.5F, 7, 2, 7, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 24, 21, -1.0F, -12.0F, -1.0F, 2, 2, 2, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 26, 25, -0.5F, -10.0F, -0.5F, 1, 2, 1, 0.0F, false));

        this.rightEar = new ModelRenderer(this);
        this.rightEar.setRotationPoint(-4.0F, -4.0F, 0.0F);
        this.head.addChild(rightEar);


        this.rightEar_r1 = new ModelRenderer(this);
        this.rightEar_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rightEar.addChild(rightEar_r1);
        this.setRotationAngle(rightEar_r1, 0.48F, -0.3491F, -0.1309F);
        this.rightEar_r1.cubeList.add(new ModelBox(rightEar_r1, 50, 18, 0.0F, -1.5F, -1.0F, 1, 3, 6, 0.0F, false));

        this.leftEar = new ModelRenderer(this);
        this.leftEar.setRotationPoint(4.0F, -4.0F, 0.0F);
        this.head.addChild(leftEar);


        this.leftEar_r1 = new ModelRenderer(this);
        this.leftEar_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leftEar.addChild(leftEar_r1);
        this.setRotationAngle(leftEar_r1, 0.48F, 0.3491F, 0.1309F);
        this.leftEar_r1.cubeList.add(new ModelBox(leftEar_r1, 50, 18, -1.0F, -1.5F, -1.0F, 1, 3, 6, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        root.render(f5);
        head.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        float earWobble = MathHelper.sin(ageInTicks * 0.2F) * 0.05F;
        this.rightEar.rotateAngleZ = earWobble;
        this.leftEar.rotateAngleZ = -earWobble;
    }
}