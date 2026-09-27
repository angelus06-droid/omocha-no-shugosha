package uut.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import uut.entity.EntityToyTeddyQilin;

public class EntityAITeddyQilinRangedBow extends EntityAIBase {
    private final EntityToyTeddyQilin qilin;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public EntityAITeddyQilinRangedBow(EntityToyTeddyQilin qilinIn, double speedAmplifier, int delay, float maxDistance) {
        this.qilin = qilinIn;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        return this.qilin.getAttackTarget() != null && this.isBowInMainhand();
    }

    protected boolean isBowInMainhand() {
        return !this.qilin.getHeldItemMainhand().isEmpty() &&
                this.qilin.getHeldItemMainhand().getItem() instanceof ItemBow;
    }

    private boolean isHoldingBow() {
        ItemStack heldItem = this.qilin.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        return !heldItem.isEmpty() && heldItem.getItem() instanceof ItemBow;
    }

    public void startExecuting() {
        super.startExecuting();
        this.qilin.setSwingingArms(true);
    }

    public void resetTask() {
        super.resetTask();
        this.qilin.setSwingingArms(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.qilin.resetActiveHand();
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.qilin.getAttackTarget();
        if (target == null) {
            return;
        }

        double distSq = this.qilin.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        boolean canSee = this.qilin.getEntitySenses().canSee(target);
        boolean wasSeeing = this.seeTime > 0;

        if (canSee != wasSeeing) this.seeTime = 0;
        if (canSee) ++this.seeTime;
        else --this.seeTime;

        boolean isInWater = this.qilin.isInWater();

        if (distSq <= (double) this.maxAttackDistance && this.seeTime >= 20 && !isInWater) {
            this.qilin.getNavigator().clearPath();
            ++this.strafingTime;
        } else {
            this.qilin.getNavigator().tryMoveToEntityLiving(target, this.moveSpeedAmp);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if (this.qilin.getRNG().nextFloat() < 0.3D) this.strafingClockwise = !this.strafingClockwise;
            if (this.qilin.getRNG().nextFloat() < 0.3D) this.strafingBackwards = !this.strafingBackwards;
            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (distSq > (double) (this.maxAttackDistance * 0.75F)) this.strafingBackwards = false;
            else if (distSq < (double) (this.maxAttackDistance * 0.25F)) this.strafingBackwards = true;

            this.qilin.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            this.qilin.faceEntity(target, 30.0F, 30.0F);
        } else {
            this.qilin.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        }

        if (this.qilin.isHandActive()) {
            if (!canSee && this.seeTime < -60) {
                this.qilin.resetActiveHand();
            } else if (canSee) {
                int useDuration = this.qilin.getItemInUseMaxCount();
                if (useDuration >= 20) {
                    this.qilin.resetActiveHand();
                    this.qilin.attackEntityWithRangedAttack(target, ItemBow.getArrowVelocity(useDuration));
                    this.attackTime = this.attackCooldown;
                }
            }

        } else if (this.isHoldingBow() && --this.attackTime <= 0 && this.seeTime >= -60) {
            this.qilin.setActiveHand(EnumHand.MAIN_HAND);
        }
    }
}