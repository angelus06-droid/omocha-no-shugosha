package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyTeddyBear;

@SideOnly(Side.CLIENT)
public class ModelToyTeddyBearFireArmor extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer leftleg;
    public ModelRenderer rightleg;
    public ModelRenderer leftarm;
    public ModelRenderer body;
    public ModelRenderer rightarm;

    public ModelToyTeddyBearFireArmor() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.head.cubeList.add(new ModelBox(head, 0, 0, -5.0F, -9.0F, -5.0F, 10, 10, 10, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 39, 7, -7.0F, -11.0F, 0.0F, 6, 6, 4, 0.0F, false));
        this.head.cubeList.add(new ModelBox(head, 39, 7, 1.0F, -11.0F, 0.0F, 6, 6, 4, 0.0F, false));

        this.leftleg = new ModelRenderer(this);
        this.leftleg.setRotationPoint(2.0F, 16.0F, 0.0F);
        this.leftleg.cubeList.add(new ModelBox(leftleg, 0, 36, -3.0F, -1.0F, -3.0F, 6, 10, 6, 0.0F, false));

        this.rightleg = new ModelRenderer(this);
        this.rightleg.setRotationPoint(-2.0F, 16.0F, 0.0F);
        this.rightleg.cubeList.add(new ModelBox(rightleg, 0, 36, -3.0F, -1.0F, -3.0F, 6, 10, 6, 0.0F, false));

        this.leftarm = new ModelRenderer(this);
        this.leftarm.setRotationPoint(4.0F, 10.0F, 0.0F);
        this.leftarm.cubeList.add(new ModelBox(leftarm, 36, 18, -1.0F, -3.0F, -3.0F, 6, 11, 6, 0.0F, false));

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body.cubeList.add(new ModelBox(body, 24, 35, -5.0F, -17.0F, -3.5F, 10, 10, 7, 0.0F, false));
        this.body.cubeList.add(new ModelBox(body, 45, 51, -3.0F, -13.0F, 2.5F, 6, 6, 2, 0.0F, false));

        this.rightarm = new ModelRenderer(this);
        this.rightarm.setRotationPoint(-4.0F, 10.0F, 0.0F);
        this.rightarm.cubeList.add(new ModelBox(rightarm, 36, 18, -5.0F, -3.0F, -3.0F, 6, 11, 6, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        head.render(f5);
        leftleg.render(f5);
        rightleg.render(f5);
        leftarm.render(f5);
        body.render(f5);
        rightarm.render(f5);
    }

    @Override
    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        this.head.rotationPointY = 8.0F;
        this.body.rotationPointY = 24.0F;
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

    private float triangleWave(float p_78172_1_, float p_78172_2_) {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5f) - p_78172_2_ * 0.25f) / (p_78172_2_ * 0.25f);
    }
}