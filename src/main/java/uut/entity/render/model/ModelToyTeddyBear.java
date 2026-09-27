package uut.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.relauncher.*;
import uut.entity.EntityToyTeddyBear;

@SideOnly(Side.CLIENT)
public class ModelToyTeddyBear extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer head_r1;
    public ModelRenderer head_r2;
    public ModelRenderer eyeright;
    public ModelRenderer eyeright_r1;
    public ModelRenderer eyeright_r2;
    public ModelRenderer eyeleft;
    public ModelRenderer eyeleft_r1;
    public ModelRenderer eyeleft_r2;
    public ModelRenderer leftleg;
    public ModelRenderer rightleg;
    public ModelRenderer leftarm;
    public ModelRenderer body;
    public ModelRenderer collar;
    public ModelRenderer rightarm;

    public ModelToyTeddyBear() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 11, 35, -2.0F, -5.0F, -5.0F, 4, 4, 1, 0.0F, false));

        this.head_r1 = new ModelRenderer(this);
        this.head_r1.setRotationPoint(3.0F, -8.0F, 2.0F);
        this.head.addChild(head_r1);
        this.setRotationAngle(head_r1, 0.0F, -0.1745F, 0.2618F);
        this.head_r1.cubeList.add(new ModelBox(head_r1, 0, 35, -1.0F, -2.0F, -1.0F, 3, 3, 1, 0.0F, true));

        this.head_r2 = new ModelRenderer(this);
        this.head_r2.setRotationPoint(-3.0F, -8.0F, 2.0F);
        this.head.addChild(head_r2);
        this.setRotationAngle(head_r2, 0.0F, 0.1745F, -0.2618F);
        this.head_r2.cubeList.add(new ModelBox(head_r2, 0, 35, -2.0F, -2.0F, -1.0F, 3, 3, 1, 0.0F, false));

        this.eyeright = new ModelRenderer(this);
        this.eyeright.setRotationPoint(-2.0F, -5.0F, -4.5F);
        this.head.addChild(eyeright);
        this.setRotationAngle(eyeright, 0.0F, 0.1745F, 0.0F);
        this.eyeright.cubeList.add(new ModelBox(eyeright, 0, 18, -3.0F, -1.0F, 0.0F, 3, 3, 1, 0.0F, false));

        this.eyeright_r1 = new ModelRenderer(this);
        this.eyeright_r1.setRotationPoint(-1.5F, 0.5F, 0.5F);
        this.eyeright.addChild(eyeright_r1);
        this.setRotationAngle(eyeright_r1, 0.0F, 0.0F, 0.7854F);
        this.eyeright_r1.cubeList.add(new ModelBox(eyeright_r1, 0, 22, -0.5F, -1.5F, -0.6F, 1, 3, 1, 0.0F, false));

        this.eyeright_r2 = new ModelRenderer(this);
        this.eyeright_r2.setRotationPoint(-1.5F, 0.5F, 0.5F);
        this.eyeright.addChild(eyeright_r2);
        this.setRotationAngle(eyeright_r2, 0.0F, 0.0F, -0.7854F);
        this.eyeright_r2.cubeList.add(new ModelBox(eyeright_r2, 0, 22, -0.5F, -1.5F, -0.7F, 1, 3, 1, 0.0F, false));

        this.eyeleft = new ModelRenderer(this);
        this.eyeleft.setRotationPoint(2.0F, -5.0F, -4.5F);
        this.head.addChild(eyeleft);
        this.setRotationAngle(eyeleft, 0.0F, -0.1745F, 0.0F);
        this.eyeleft.cubeList.add(new ModelBox(eyeleft, 0, 18, 0.0F, -1.0F, 0.0F, 3, 3, 1, 0.0F, false));

        this.eyeleft_r1 = new ModelRenderer(this);
        this.eyeleft_r1.setRotationPoint(1.5F, 0.5F, 0.5F);
        this.eyeleft.addChild(eyeleft_r1);
        this.setRotationAngle(eyeleft_r1, 0.0F, 0.0F, -0.7854F);
        this.eyeleft_r1.cubeList.add(new ModelBox(eyeleft_r1, 0, 22, -0.5F, -1.5F, -0.6F, 1, 3, 1, 0.0F, false));

        this.eyeleft_r2 = new ModelRenderer(this);
        this.eyeleft_r2.setRotationPoint(1.5F, 0.5F, 0.5F);
        this.eyeleft.addChild(eyeleft_r2);
        this.setRotationAngle(eyeleft_r2, 0.0F, 0.0F, 0.7854F);
        this.eyeleft_r2.cubeList.add(new ModelBox(eyeleft_r2, 0, 22, -0.5F, -1.5F, -0.7F, 1, 3, 1, 0.0F, false));

        this.leftleg = new ModelRenderer(this);
        this.leftleg.setRotationPoint(2.0F, 16.0F, 0.0F);
        this.leftleg.cubeList.add(new ModelBox(leftleg, 24, 28, -2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F, true));

        this.rightleg = new ModelRenderer(this);
        this.rightleg.setRotationPoint(-2.0F, 16.0F, 0.0F);
        this.rightleg.cubeList.add(new ModelBox(rightleg, 24, 28, -2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F, false));

        this.leftarm = new ModelRenderer(this);
        this.leftarm.setRotationPoint(4.0F, 10.0F, 0.0F);
        this.leftarm.cubeList.add(new ModelBox(leftarm, 40, 16, 0.0F, -2.0F, -2.0F, 4, 9, 4, 0.0F, true));

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body.cubeList.add(new ModelBox(body, 16, 16, -4.0F, -16.0F, -2.0F, 8, 8, 4, 0.0F, false));
        this.body.cubeList.add(new ModelBox(body, 40, 29, -2.0F, -12.0F, 2.0F, 4, 4, 2, 0.0F, false));

        this.collar = new ModelRenderer(this);
        this.collar.setRotationPoint(3.0F, 16.0F, -1.0F);
        this.collar.cubeList.add(new ModelBox(collar, 0, 52, -7.0F, -8.0F, -1.0F, 8, 8, 4, 0.1F, false));

        this.rightarm = new ModelRenderer(this);
        this.rightarm.setRotationPoint(-4.0F, 10.0F, 0.0F);
        this.rightarm.cubeList.add(new ModelBox(rightarm, 40, 16, -4.0F, -2.0F, -2.0F, 4, 9, 4, 0.0F, false));
    }

    @Override
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        head.render(f5);
        leftleg.render(f5);
        rightleg.render(f5);
        leftarm.render(f5);
        body.render(f5);
        rightarm.render(f5);
        collar.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        this.head.rotationPointY = 8.0F;
        this.body.rotationPointY = 24.0F;
        this.collar.rotationPointY = 16.0F;
        this.rightarm.rotationPointY = 10.0F;
        this.leftarm.rotationPointY = 10.0F;

        this.leftleg.rotationPointY = 16.0F;
        this.leftleg.rotationPointZ = 0.0F;
        this.rightleg.rotationPointY = 16.0F;
        this.rightleg.rotationPointZ = 0.0F;

        this.leftleg.rotateAngleY = 0.0F;
        this.rightleg.rotateAngleY = 0.0F;
        this.leftleg.rotateAngleZ = 0.0F;
        this.rightleg.rotateAngleZ = 0.0F;

        this.rightarm.rotateAngleY = 0.0F;
        this.leftarm.rotateAngleY = 0.0F;
        this.rightarm.rotateAngleZ = 0.0F;
        this.leftarm.rotateAngleZ = 0.0F;

        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;

        boolean hasBow = false;
        boolean hasShield = false;
        boolean isBowAiming = false;
        boolean isBlocking = false;

        if (entityIn instanceof EntityToyTeddyBear) {
            EntityToyTeddyBear teddy = (EntityToyTeddyBear) entityIn;
            hasBow = teddy.getHeldItemMainhand().getItem() instanceof ItemBow;
            hasShield = teddy.getHeldItemOffhand().getItem() instanceof ItemShield;
            isBowAiming = teddy.isAiming();
            isBlocking = hasShield && teddy.isHandActive() && teddy.getActiveHand() == EnumHand.OFF_HAND;
        }

        this.leftleg.rotateAngleX = 1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;
        this.rightleg.rotateAngleX = -1.5f * this.triangleWave(limbSwing, 13.0f) * limbSwingAmount;

        if (hasBow && isBowAiming) {
            this.rightarm.rotateAngleX = -1.5707963F + this.head.rotateAngleX;
            this.rightarm.rotateAngleY = -0.34906585F + this.head.rotateAngleY;

            this.leftarm.rotateAngleX = -1.5707963F + this.head.rotateAngleX;
            this.leftarm.rotateAngleY = 0.9599311F + this.head.rotateAngleY;
        }

        if (isBlocking) {
            this.leftarm.rotateAngleX = -0.6981317F;
            this.leftarm.rotateAngleY = 0.5235988F;
            this.leftarm.rotateAngleZ = 0.0F;
        }
    }

    @Override
    public void setLivingAnimations(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTickTime) {
        final EntityToyTeddyBear entity = (EntityToyTeddyBear) entitylivingbaseIn;
        final int attackTimer = entity.getAttackTimer();
        boolean hasBow = entity.getHeldItemMainhand().getItem() instanceof ItemBow;
        boolean hasShield = entity.getHeldItemOffhand().getItem() instanceof ItemShield;
        boolean isBowAiming = entity.isAiming();
        boolean isBlocking = hasShield && entity.isHandActive() && entity.getActiveHand() == EnumHand.OFF_HAND;
        float idleTime = entity.ticksExisted + partialTickTime;
        float idleFade = MathHelper.clamp(1.0f - limbSwingAmount * 3.0f, 0.0f, 1.0f);
        float idleSway = MathHelper.sin(idleTime * 0.05f) * 0.05f * idleFade;
        float idleBob = (MathHelper.cos(idleTime * 0.045f) * 0.5f + 0.5f) * 0.03f * idleFade;

        if (!isBlocking) {
            this.leftarm.rotateAngleX = (-0.2f - 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
                    + idleSway + idleBob;
        }

        if (attackTimer > 0 && !hasBow) {
            this.rightarm.rotateAngleX = -2.0f + 1.5f * this.triangleWave(attackTimer - partialTickTime, 10.0f);
        } else if (!hasBow || !isBowAiming) {
            this.rightarm.rotateAngleX = (-0.2f + 1.5f * this.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
                    - idleSway + idleBob;
        }
    }

    private float triangleWave(final float p_78172_1_, final float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }

    protected EnumHandSide getMainHand(final Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            final EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
            final EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
            return (entitylivingbase.swingingHand == EnumHand.MAIN_HAND) ? enumhandside : enumhandside.opposite();
        }
        return EnumHandSide.RIGHT;
    }

    public void postRenderArm(final float scale, final EnumHandSide side) {
        this.getArmForSide(side).postRender(scale);
    }

    protected ModelRenderer getArmForSide(final EnumHandSide side) {
        return (side == EnumHandSide.LEFT) ? this.leftarm : this.rightarm;
    }
}