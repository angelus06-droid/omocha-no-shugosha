package uut.entity.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import uut.MainClass;
import uut.entity.EntityToyTinyDragoness;
import uut.particle.ModParticles;

public class ModelToyTinyDragoness extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer tail2;
    private final ModelRenderer rightWing;
    private final ModelRenderer rightWingEnd;
    private final ModelRenderer leftWing;
    private final ModelRenderer leftWingEnd;
    private final ModelRenderer rightLegFront;
    private final ModelRenderer rightLegBack;
    private final ModelRenderer leftLegFront;
    private final ModelRenderer leftLegBack;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer mouthUpper;
    private final ModelRenderer mouthLower;
    private final ModelRenderer rightHorn;
    private final ModelRenderer leftHorn;
    private final ModelRenderer flame;

    public ModelToyTinyDragoness() {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 14.0F, -6.0F);
        body.cubeList.add(new ModelBox(body, 0, 33, -3.0F, -2.0F, 0.0F, 6, 6, 12, 0.0F, false));
        body.cubeList.add(new ModelBox(body, 24, 30, 0.0F, -5.0F, 0.0F, 0, 3, 12, 0.0F, false));

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, -2.0F, 12.0F);
        body.addChild(tail);
        setRotationAngle(tail, 0.1745F, 0.0F, 0.0F);
        tail.cubeList.add(new ModelBox(tail, 46, 0, -2.0F, 0.0F, -1.0F, 4, 4, 7, 0.0F, false));
        tail.cubeList.add(new ModelBox(tail, 61, -1, 0.0F, -2.0F, 0.0F, 0, 2, 6, 0.0F, false));

        tail2 = new ModelRenderer(this);
        tail2.setRotationPoint(0.0F, 0.0F, 6.0F);
        tail.addChild(tail2);
        setRotationAngle(tail2, 0.1745F, 0.0F, 0.0F);
        tail2.cubeList.add(new ModelBox(tail2, 45, 11, -1.0F, 0.0F, -1.0F, 2, 3, 8, 0.0F, false));
        tail2.cubeList.add(new ModelBox(tail2, 57, 10, 0.0F, -2.0F, 0.0F, 0, 2, 7, 0.0F, false));

        rightWing = new ModelRenderer(this);
        rightWing.setRotationPoint(-3.0F, -2.0F, 2.0F);
        body.addChild(rightWing);
        setRotationAngle(rightWing, 0.0F, 0.0F, 0.1745F);
        rightWing.cubeList.add(new ModelBox(rightWing, -10, 65, -4.0F, 0.0F, -2.0F, 4, 0, 10, 0.0F, false));

        rightWingEnd = new ModelRenderer(this);
        rightWingEnd.setRotationPoint(-4.0F, 0.0F, -1.0F);
        rightWing.addChild(rightWingEnd);
        rightWingEnd.cubeList.add(new ModelBox(rightWingEnd, -14, 51, -11.0F, 0.0F, -2.0F, 11, 0, 14, 0.0F, false));

        leftWing = new ModelRenderer(this);
        leftWing.setRotationPoint(3.0F, -2.0F, 2.0F);
        body.addChild(leftWing);
        setRotationAngle(leftWing, 0.0F, 0.0F, -0.1745F);
        leftWing.cubeList.add(new ModelBox(leftWing, -10, 65, 0.0F, 0.0F, -2.0F, 4, 0, 10, 0.0F, true));

        leftWingEnd = new ModelRenderer(this);
        leftWingEnd.setRotationPoint(4.0F, 0.0F, -1.0F);
        leftWing.addChild(leftWingEnd);
        leftWingEnd.cubeList.add(new ModelBox(leftWingEnd, -14, 51, 0.0F, 0.0F, -2.0F, 11, 0, 14, 0.0F, true));

        rightLegFront = new ModelRenderer(this);
        rightLegFront.setRotationPoint(-2.5F, 18.0F, -3.5F);
        rightLegFront.cubeList.add(new ModelBox(rightLegFront, 40, 53, -1.5F, -1.0F, -1.5F, 3, 7, 3, 0.0F, false));
        rightLegFront.cubeList.add(new ModelBox(rightLegFront, 28, 53, -1.5F, -1.0F, -1.5F, 3, 7, 3, 0.1F, false));

        rightLegBack = new ModelRenderer(this);
        rightLegBack.setRotationPoint(-2.5F, 17.0F, 3.5F);
        rightLegBack.cubeList.add(new ModelBox(rightLegBack, 52, 53, -1.5F, -1.0F, -1.5F, 3, 8, 3, 0.0F, false));

        leftLegFront = new ModelRenderer(this);
        leftLegFront.setRotationPoint(2.5F, 18.0F, -3.5F);
        leftLegFront.cubeList.add(new ModelBox(leftLegFront, 40, 53, -1.5F, -1.0F, -1.5F, 3, 7, 3, 0.0F, true));
        leftLegFront.cubeList.add(new ModelBox(leftLegFront, 28, 53, -1.5F, -1.0F, -1.5F, 3, 7, 3, 0.1F, true));

        leftLegBack = new ModelRenderer(this);
        leftLegBack.setRotationPoint(2.5F, 17.0F, 3.5F);
        leftLegBack.cubeList.add(new ModelBox(leftLegBack, 52, 53, -1.5F, -1.0F, -1.5F, 3, 8, 3, 0.0F, true));

        neck = new ModelRenderer(this);
        neck.setRotationPoint(0.0F, 14.0F, -6.0F);
        setRotationAngle(neck, -0.3491F, 0.0F, 0.0F);
        neck.cubeList.add(new ModelBox(neck, 0, 13, -2.0F, -2.0F, -3.0F, 4, 4, 4, 0.0F, false));
        neck.cubeList.add(new ModelBox(neck, 16, 13, 0.0F, -4.0F, -3.0F, 0, 2, 4, 0.0F, false));

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 0.0F, -3.0F);
        neck.addChild(head);
        setRotationAngle(head, 0.3491F, 0.0F, 0.0F);
        head.cubeList.add(new ModelBox(head, 0, 0, -3.0F, -3.0F, -4.0F, 6, 6, 5, 0.0F, false));
        head.cubeList.add(new ModelBox(head, 0, 2, 0.0F, -5.0F, -8.0F, 0, 2, 9, 0.0F, false));

        mouthUpper = new ModelRenderer(this);
        mouthUpper.setRotationPoint(0.0F, 1.0F, -4.0F);
        head.addChild(mouthUpper);
        mouthUpper.cubeList.add(new ModelBox(mouthUpper, 22, 0, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, false));
        mouthUpper.cubeList.add(new ModelBox(mouthUpper, 22, 4, -2.0F, 0.0F, -4.0F, 0, 1, 4, 0.0F, false));
        mouthUpper.cubeList.add(new ModelBox(mouthUpper, 22, 4, 2.0F, 0.0F, -4.0F, 0, 1, 4, 0.0F, false));
        mouthUpper.cubeList.add(new ModelBox(mouthUpper, 31, 8, 1.5F, -1.0F, -4.5F, 1, 3, 1, 0.0F, false));
        mouthUpper.cubeList.add(new ModelBox(mouthUpper, 31, 8, -2.5F, -1.0F, -4.5F, 1, 3, 1, 0.0F, false));
        mouthUpper.cubeList.add(new ModelBox(mouthUpper, 22, 9, -2.0F, 0.0F, -4.0F, 4, 1, 0, 0.0F, false));
        mouthUpper.cubeList.add(new ModelBox(mouthUpper, 34, -2, 0.0F, -6.0F, -4.0F, 0, 2, 4, 0.0F, false));

        mouthLower = new ModelRenderer(this);
        mouthLower.setRotationPoint(0.0F, 1.0F, -4.0F);
        head.addChild(mouthLower);
        mouthLower.cubeList.add(new ModelBox(mouthLower, 22, 11, -1.5F, 0.0F, -3.0F, 3, 2, 3, 0.0F, false));

        rightHorn = new ModelRenderer(this);
        rightHorn.setRotationPoint(-2.0F, -2.0F, 0.0F);
        head.addChild(rightHorn);
        setRotationAngle(rightHorn, 0.6545F, -0.48F, 0.0F);
        rightHorn.cubeList.add(new ModelBox(rightHorn, 0, 21, -1.0F, -1.0F, -1.0F, 2, 2, 6, 0.0F, false));
        rightHorn.cubeList.add(new ModelBox(rightHorn, 4, 29, -1.0F, 1.0F, 3.0F, 2, 2, 2, 0.0F, false));

        leftHorn = new ModelRenderer(this);
        leftHorn.setRotationPoint(2.0F, -2.0F, 0.0F);
        head.addChild(leftHorn);
        setRotationAngle(leftHorn, 0.6545F, 0.48F, 0.0F);
        leftHorn.cubeList.add(new ModelBox(leftHorn, 0, 21, -1.0F, -1.0F, -1.0F, 2, 2, 6, 0.0F, false));
        leftHorn.cubeList.add(new ModelBox(leftHorn, 4, 29, -1.0F, 1.0F, 3.0F, 2, 2, 2, 0.0F, false));

        flame = new ModelRenderer(this);
        flame.setRotationPoint(0.0F, -1.0F, -4.0F);
        head.addChild(flame);
        flame.cubeList.add(new ModelBox(flame, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 0, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        body.render(f5);
        rightLegFront.render(f5);
        rightLegBack.render(f5);
        leftLegFront.render(f5);
        leftLegBack.render(f5);
        neck.render(f5);

        if (entity instanceof EntityToyTinyDragoness) {
            EntityToyTinyDragoness dragon = (EntityToyTinyDragoness) entity;

            if (dragon.isBreathingFire() && !Minecraft.getMinecraft().isGamePaused()) {
                if (dragon.ticksExisted % 2 == 0) {
                    spawnFlameParticles(dragon);
                }
            }
        }
    }

    private void spawnFlameParticles(EntityToyTinyDragoness dragon) {
        World world = dragon.world;

        float yaw = dragon.rotationYawHead;
        float pitch = dragon.rotationPitch;

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        double dirX = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        double dirY = -MathHelper.sin(pitchRad);
        double dirZ = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);

        Vec3d dir = new Vec3d(dirX, dirY, dirZ).normalize();

        double forwardOffset = 0.75D;
        double heightOffset = dragon.isSitting() ? 0.35D : dragon.getEyeHeight();

        double px = dragon.posX + dir.x * forwardOffset;
        double py = dragon.posY + heightOffset + dir.y * forwardOffset;
        double pz = dragon.posZ + dir.z * forwardOffset;

        double speed = 0.35D + world.rand.nextDouble() * 0.05D;
        double vx = dir.x * speed + (world.rand.nextDouble() - 0.5D) * 0.04D;
        double vy = dir.y * speed + (world.rand.nextDouble() - 0.5D) * 0.04D;
        double vz = dir.z * speed + (world.rand.nextDouble() - 0.5D) * 0.04D;

        ModParticles.spawnDragonessFlame(world, px, py, pz, vx, vy, vz);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        // 1. RESTABLECER PUNTOS DE ROTACIÓN BASE
        this.body.setRotationPoint(0.0F, 14.0F, -6.0F);
        setRotationAngle(this.body, 0.0F, 0.0F, 0.0F);

        this.neck.setRotationPoint(0.0F, 14.0F, -6.0F);
        setRotationAngle(this.neck, -0.3491F + headPitch * 0.017453292F * 0.5F, netHeadYaw * 0.017453292F * 0.5F, 0.0F);
        setRotationAngle(this.head, 0.3491F + headPitch * 0.017453292F * 0.5F, netHeadYaw * 0.017453292F * 0.5F, 0.0F);

        this.rightLegFront.setRotationPoint(-2.5F, 18.0F, -3.5F);
        this.leftLegFront.setRotationPoint(2.5F, 18.0F, -3.5F);
        this.rightLegBack.setRotationPoint(-2.5F, 17.0F, 3.5F);
        this.leftLegBack.setRotationPoint(2.5F, 17.0F, 3.5F);

        this.rightLegBack.rotateAngleY = 0.0F;
        this.leftLegBack.rotateAngleY = 0.0F;

        setRotationAngle(this.mouthUpper, 0.0F, 0.0F, 0.0F);
        setRotationAngle(this.mouthLower, 0.0F, 0.0F, 0.0F);
        setRotationAngle(this.tail, 0.1745F, 0.0F, 0.0F);

        if (entityIn instanceof EntityToyTinyDragoness) {
            EntityToyTinyDragoness dragon = (EntityToyTinyDragoness) entityIn;

            float partialTicks = ageInTicks - (float)dragon.ticksExisted;

            float interpolatedRot = dragon.prevWingRotation + (dragon.wingRotation - dragon.prevWingRotation) * partialTicks;
            float interpolatedDest = dragon.prevDestPos + (dragon.destPos - dragon.prevDestPos) * partialTicks;

            float flap = MathHelper.cos(interpolatedRot) * interpolatedDest * 0.7F;

            float groundFactor = 1.0F - interpolatedDest;

            float breathCycle = ageInTicks * 0.06F;
            float idleZ = MathHelper.sin(breathCycle) * 0.05F * groundFactor;
            float idleY = MathHelper.cos(breathCycle * 0.5F) * 0.04F * groundFactor;
            float idleX = MathHelper.sin(breathCycle * 0.5F) * 0.03F * groundFactor;

            float foldedBaseZ = 0.45F * groundFactor;


            this.rightWing.rotateAngleZ = 0.2F + foldedBaseZ + flap + idleZ;
            this.leftWing.rotateAngleZ = -0.2F - foldedBaseZ - flap - idleZ;

            this.rightWing.rotateAngleY = 0.2F * groundFactor + idleY;
            this.leftWing.rotateAngleY = -0.2F * groundFactor - idleY;

            this.rightWing.rotateAngleX = idleX;
            this.leftWing.rotateAngleX = idleX;

            float wingEndIdle = MathHelper.sin(breathCycle - 0.8F) * 0.04F * groundFactor;
            this.rightWingEnd.rotateAngleZ = (flap * 0.3F) + wingEndIdle;
            this.leftWingEnd.rotateAngleZ = (-flap * 0.3F) - wingEndIdle;
        }

        this.tail.rotateAngleY = MathHelper.cos(limbSwing * 0.8882F) * 0.2F * limbSwingAmount + MathHelper.sin(ageInTicks * 0.1F) * 0.1F;
        this.tail2.rotateAngleY = MathHelper.cos(limbSwing * 0.8882F) * 0.2F * limbSwingAmount + MathHelper.sin(ageInTicks * 0.1F + 0.5F) * 0.15F;

        this.rightLegFront.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F) * 1.4F * limbSwingAmount;
        this.leftLegFront.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F + (float) Math.PI) * 1.2F * limbSwingAmount;
        this.rightLegBack.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F + (float) Math.PI) * 1.2F * limbSwingAmount;
        this.leftLegBack.rotateAngleX = MathHelper.cos(limbSwing * 0.8882F) * 1.2F * limbSwingAmount;

        // 4. ESTADOS ESPECIALES
        if (entityIn instanceof EntityToyTinyDragoness) {
            EntityToyTinyDragoness dragon = (EntityToyTinyDragoness) entityIn;

            if (dragon.isBreathingFire()) {
                this.head.rotateAngleX += -0.0872665F;
                this.mouthLower.rotateAngleX = 0.35F;
                this.mouthUpper.rotateAngleX = -0.25F;
            }

            if (dragon.isSitting()) {
                this.body.rotateAngleX = -0.4363323F;

                this.rightLegFront.rotateAngleX = 0.0F;
                this.leftLegFront.rotateAngleX = 0.0F;

                this.rightLegBack.rotateAngleX = -1.5707963F;
                this.rightLegBack.rotateAngleY = 0.5235988F;
                this.rightLegBack.setRotationPoint(-2.5F, 22.5F, 3.5F);

                this.leftLegBack.rotateAngleX = -1.5707963F;
                this.leftLegBack.rotateAngleY = -0.5235988F;
                this.leftLegBack.setRotationPoint(2.5F, 22.5F, 3.5F);

                this.tail.rotateAngleX = 0.25F;
            }
        }
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}