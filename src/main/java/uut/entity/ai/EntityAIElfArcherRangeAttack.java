package uut.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import uut.entity.EntityToyElfArcher;

public class EntityAIElfArcherRangeAttack extends EntityAIBase {
    private final EntityToyElfArcher entity;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public EntityAIElfArcherRangeAttack(EntityToyElfArcher archer, double speedAmplifier, int delay, float maxDistance) {
        this.entity = archer;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexBits(3);
    }

    protected boolean isHoldingBow() {
        ItemStack mainHandStack = this.entity.getHeldItemMainhand();
        return !mainHandStack.isEmpty() && mainHandStack.getItem() instanceof ItemBow;
    }

    @Override
    public boolean shouldExecute() {
        return this.isHoldingBow()
                && this.entity.getAttackTarget() != null
                && this.entity.getAttackTarget().isEntityAlive();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.isHoldingBow() && (this.shouldExecute() || !this.entity.getNavigator().noPath());
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.entity.setSwingingArms(true);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.entity.setSwingingArms(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.entity.getAttackTarget();
        if (target == null || !this.isHoldingBow()) {
            return;
        }

        double distSq = this.entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        boolean canSee = this.entity.getEntitySenses().canSee(target);
        boolean wasSeeing = this.seeTime > 0;

        if (canSee != wasSeeing) this.seeTime = 0;
        if (canSee) ++this.seeTime;
        else --this.seeTime;

        boolean isInWater = this.entity.isInWater();

        if (distSq <= (double) this.maxAttackDistance && this.seeTime >= 20 && !isInWater) {
            this.entity.getNavigator().clearPath();
            ++this.strafingTime;
        } else {
            this.entity.getNavigator().tryMoveToEntityLiving(target, this.moveSpeedAmp);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= 20) {
            if (this.entity.getRNG().nextFloat() < 0.3D) this.strafingClockwise = !this.strafingClockwise;
            if (this.entity.getRNG().nextFloat() < 0.3D) this.strafingBackwards = !this.strafingBackwards;
            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (distSq > (double) (this.maxAttackDistance * 0.75F)) this.strafingBackwards = false;
            else if (distSq < (double) (this.maxAttackDistance * 0.25F)) this.strafingBackwards = true;

            this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            this.entity.faceEntity(target, 30.0F, 30.0F);
        } else {
            this.entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        }

        if (this.entity.isHandActive()) {
            if (!canSee && this.seeTime < -60) {
                this.entity.resetActiveHand();
            } else if (canSee) {
                int useDuration = this.entity.getItemInUseMaxCount();
                if (useDuration >= 20) {
                    this.entity.resetActiveHand();
                    this.entity.attackEntityWithRangedAttack(target, ItemBow.getArrowVelocity(useDuration));
                    this.attackTime = this.attackCooldown;
                }
            }

        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            this.entity.setActiveHand(EnumHand.MAIN_HAND);
        }
    }
}