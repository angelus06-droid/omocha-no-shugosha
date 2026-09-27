package uut.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import uut.entity.EntityToyPrincessShooter;

public class EntityAISummonRangeAttack extends EntityAIBase {
    private final EntityToyPrincessShooter entity;
    private final double moveSpeedAmp;
    private final int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public EntityAISummonRangeAttack(EntityToyPrincessShooter summon, double speedAmplifier, int delay, float maxDistance) {
        this.entity = summon;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null;
    }

    public boolean continueExecuting() {
        return this.shouldExecute() || !this.entity.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.attackTime = this.attackCooldown;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.entity.getAttackTarget();
        if (target != null) {
            double distSq = this.entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
            boolean canSee = this.entity.getEntitySenses().canSee(target);
            boolean wasSeeing = this.seeTime > 0;

            if (canSee != wasSeeing) this.seeTime = 0;
            if (canSee) ++this.seeTime;
            else --this.seeTime;

            if (distSq <= (double)this.maxAttackDistance && this.seeTime >= 20) {
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
                if (distSq > (double)(this.maxAttackDistance * 0.75F)) this.strafingBackwards = false;
                else if (distSq < (double)(this.maxAttackDistance * 0.25F)) this.strafingBackwards = true;

                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -1.0F : 1.0F, this.strafingClockwise ? 1.0F : -1.0F);
                this.entity.faceEntity(target, 30.0F, 30.0F);

                if (this.entity.onGround && this.entity.getRNG().nextFloat() < 0.15F) {
                    this.entity.getJumpHelper().setJumping();
                }
            } else {
                this.entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            }

            if (this.attackTime > 0) {
                this.attackTime--;
            }

            if (this.attackTime <= 0 && this.seeTime >= 20 && canSee) {
                float f = MathHelper.sqrt(distSq) / MathHelper.sqrt(this.maxAttackDistance);
                float distanceFactor = MathHelper.clamp(f, 0.1F, 1.0F);
                this.entity.attackEntityWithRangedAttack(target, distanceFactor);
                this.attackTime = this.attackCooldown;
            }
        }
    }
}