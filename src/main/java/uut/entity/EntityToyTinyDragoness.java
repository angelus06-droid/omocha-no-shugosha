package uut.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.item.ModItems;
import uut.util.ModSounds;

import javax.annotation.Nullable;
import java.util.List;

public class EntityToyTinyDragoness extends BaseDefensiveMob {

    private int attackTimer;
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyTinyDragoness.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyTinyDragoness.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyTinyDragoness.class, DataSerializers.VARINT);

    private static final DataParameter<Boolean> BREATHING_FIRE = EntityDataManager.createKey(EntityToyTinyDragoness.class, DataSerializers.BOOLEAN);
    private int flameCooldown = 0;

    private int healTimer;

    public EntityToyTinyDragoness(World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 0.8f);
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.65F;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.YELLOW.getDyeDamage());
        this.dataManager.register(BREATHING_FIRE, false);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIFlameBreath(this, 1.0D, 4.0F));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.1D, false));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.1D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && super.shouldExecute();
            }
        });

        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !isFollowing() && super.shouldExecute();
            }
        });

        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
            }
        });

        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> {
            if (entity == null || isSitting() || !entity.isEntityAlive()) return false;
            return entity instanceof IMob || entity instanceof net.minecraft.entity.monster.EntityShulker;
        }));
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (this.isOwner(player) && itemstack.getItem() == ModItems.toy_command_staff) {
            if (!this.world.isRemote) {
                if (isFollowing()) {
                    setFollowing(false);
                    setSitting(true);
                    player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.sit"), true);
                } else if (isSitting()) {
                    setFollowing(false);
                    setSitting(false);
                    player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.free"), true);
                } else {
                    setFollowing(true);
                    setSitting(false);
                    player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.follow"), true);
                }

                this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }

            if (this.world.isRemote) {
                if (this.isSitting()) {
                    this.spawnColoredParticle(EnumParticleTypes.SPELL_MOB, 0.0F, 0.0F, 1.0F);
                } else if (this.isFollowing()) {
                    this.spawnColoredParticle(EnumParticleTypes.SPELL_MOB, 1.0F, 0.0F, 0.0F);
                } else {
                    this.spawnColoredParticle(EnumParticleTypes.SPELL_MOB, 0.0F, 1.0F, 0.0F);
                }
            }

            return true;
        }
        if (this.isOwner(player)) {
            if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemDye) {
                EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(itemstack.getMetadata());
                if (enumdyecolor != this.getCollarColor()) {
                    this.setCollarColor(enumdyecolor);
                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1.0F, 1.25F);
                    return true;
                }
            }
        }
        return super.processInteract(player, hand);
    }

    private void spawnColoredParticle(EnumParticleTypes type, float red, float green, float blue) {
        for (int i = 0; i < 10; ++i) {
            double px = this.posX + (this.rand.nextFloat() - 0.5D) * this.width;
            double py = this.posY + this.rand.nextFloat() * this.height;
            double pz = this.posZ + (this.rand.nextFloat() - 0.5D) * this.width;

            this.world.spawnParticle(type, px, py, pz, (double)red, (double)green, (double)blue);
        }
    }

    public float wingRotation;
    public float prevWingRotation;
    public float destPos;
    public float prevDestPos;
    public float wingRotDelta = 1.0F;

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.prevWingRotation = this.wingRotation;
        this.prevDestPos = this.destPos;

        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.15D);
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);

        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);

        if (!this.onGround && this.motionY < 0.0D) {
            this.motionY *= 0.6D;
        }

        this.wingRotation += this.wingRotDelta * 0.45F;

        if (this.attackTimer > 0) --this.attackTimer;

        if (!this.world.isRemote && this.flameCooldown > 0) {
            --this.flameCooldown;
        }

        if (!this.world.isRemote && this.isFollowing() && this.getAttackTarget() == null) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null) {
                double distSq = this.getDistanceSq(owner);
                if (distSq > 144.0D) {
                    this.teleportToOwner(owner);
                }
            }
        }

        if (!this.world.isRemote) {
            if (this.isEntityAlive() && this.getHealth() < this.getMaxHealth()) {
                this.healTimer++;
                if (this.healTimer >= 80) {
                    this.heal(1.0F);
                    this.healTimer = 0;
                }
            } else {
                this.healTimer = 0;
            }
        }
    }

    private void teleportToOwner(EntityLivingBase owner) {
        for (int i = 0; i < 10; ++i) {
            int offsetX = this.rand.nextInt(7) - 3;
            int offsetZ = this.rand.nextInt(7) - 3;

            BlockPos targetPos = new BlockPos(owner.posX + offsetX, owner.getEntityBoundingBox().minY, owner.posZ + offsetZ);

            if (this.isValidTeleportPos(targetPos)) {
                this.setLocationAndAngles((double)targetPos.getX() + 0.5D, (double)targetPos.getY(), (double)targetPos.getZ() + 0.5D, this.rotationYaw, this.rotationPitch);
                this.getNavigator().clearPath();
                return;
            }
        }
    }

    private boolean isValidTeleportPos(BlockPos pos) {
        return this.world.getBlockState(pos.down()).isSideSolid(this.world, pos.down(), EnumFacing.UP) &&
                this.isSafeToStandAt(pos) &&
                this.isSafeToStandAt(pos.up());
    }

    private boolean isSafeToStandAt(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return !state.getMaterial().isSolid() && !state.getMaterial().isLiquid();
    }

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }

    public boolean isBreathingFire() { return this.dataManager.get(BREATHING_FIRE); }
    public void setBreathingFire(boolean breathing) { this.dataManager.set(BREATHING_FIRE, breathing); }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    public int getFlameCooldown() { return this.flameCooldown; }
    public void setFlameCooldown(int cooldown) { this.flameCooldown = cooldown; }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("following", this.isFollowing());
        compound.setBoolean("sitting", this.isSitting());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
        compound.setInteger("FlameCooldown", this.flameCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
        if (compound.hasKey("FlameCooldown")) {
            this.flameCooldown = compound.getInteger("FlameCooldown");
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) this.attackTimer = 10;
        else super.handleStatusUpdate(id);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() { return this.attackTimer; }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() { return ModSounds.tiny_dragoness_say; }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) { this.playSound(SoundEvents.ENTITY_COW_STEP, 0.25f, 5.0f); }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) { return ModSounds.tiny_dragoness_hurt; }

    @Override
    protected SoundEvent getDeathSound() { return ModSounds.tiny_dragoness_death; }

    static class EntityAIFlameBreath extends EntityAIBase {
        private final EntityToyTinyDragoness entity;
        private final double entityMoveSpeed;
        private final float maxAttackDistance;
        private final float maxAttackDistanceSq;

        private int durationTicks;
        private int seeTime;

        public EntityAIFlameBreath(EntityToyTinyDragoness dragon, double moveSpeed, float maxAttackDistance) {
            this.entity = dragon;
            this.entityMoveSpeed = moveSpeed;
            this.maxAttackDistance = maxAttackDistance;
            this.maxAttackDistanceSq = maxAttackDistance * maxAttackDistance;
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = this.entity.getAttackTarget();
            if (target == null || !target.isEntityAlive() || this.entity.isSitting()) {
                return false;
            }
            return this.entity.getFlameCooldown() <= 0;
        }

        @Override
        public boolean shouldContinueExecuting() {
            EntityLivingBase target = this.entity.getAttackTarget();
            return (this.shouldExecute() || this.durationTicks > 0) && target != null && target.isEntityAlive() && !this.entity.isSitting();
        }

        @Override
        public void startExecuting() {
            this.seeTime = 0;
            this.durationTicks = 0;
        }

        @Override
        public void resetTask() {
            this.seeTime = 0;
            if (this.entity.isBreathingFire()) {
                this.entity.setBreathingFire(false);
                this.entity.setFlameCooldown(200);
            }
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = this.entity.getAttackTarget();
            if (target == null) return;

            double distSq = this.entity.getDistanceSq(target);
            boolean canSee = this.entity.getEntitySenses().canSee(target);

            if (canSee) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }

            if (distSq <= (double)this.maxAttackDistanceSq && this.seeTime >= 5) {
                this.entity.getNavigator().clearPath();
            } else {
                this.entity.getNavigator().tryMoveToEntityLiving(target, this.entityMoveSpeed);
            }

            AxisAlignedBB targetBox = target.getEntityBoundingBox();
            double targetCenterX = (targetBox.minX + targetBox.maxX) / 2.0D;
            double targetCenterY = (targetBox.minY + targetBox.maxY) / 2.0D;
            double targetCenterZ = (targetBox.minZ + targetBox.maxZ) / 2.0D;

            this.entity.getLookHelper().setLookPosition(targetCenterX, targetCenterY, targetCenterZ, 30.0F, 30.0F);

            if (this.durationTicks == 0 && distSq <= (double)this.maxAttackDistanceSq && canSee) {
                this.durationTicks = 100 + this.entity.getRNG().nextInt(41);
                this.entity.setBreathingFire(true);
            }

            if (this.durationTicks > 0) {
                --this.durationTicks;

                if (this.durationTicks % 5 == 0) {
                    performAoEMagicDamage();
                }

                if (this.durationTicks <= 0) {
                    this.entity.setBreathingFire(false);
                    this.entity.setFlameCooldown(200);
                }
            }
        }

        private void performAoEMagicDamage() {
            Vec3d look = this.entity.getLookVec();

            double eyeY = this.entity.posY + (double)this.entity.getEyeHeight();

            double areaX = this.entity.posX + look.x * 2.0D;
            double areaY = eyeY + look.y * 2.0D;
            double areaZ = this.entity.posZ + look.z * 2.0D;

            AxisAlignedBB aoeBox = new AxisAlignedBB(
                    areaX - 1.5D, areaY - 1.5D, areaZ - 1.5D,
                    areaX + 1.5D, areaY + 1.5D, areaZ + 1.5D
            );

            List<EntityLivingBase> targets = this.entity.world.getEntitiesWithinAABB(EntityLivingBase.class, aoeBox);

            float bonusDamage = 0.0F;
            if (this.entity instanceof BaseDefensiveMob) {
                bonusDamage = ((BaseDefensiveMob) this.entity).getRangedAttackBonus();
            }

            float totalDamage = 1.5F + bonusDamage;

            for (EntityLivingBase victim : targets) {
                if (victim != this.entity && isFacingTarget(victim)) {

                    if (this.entity.getOwner() != null && victim.equals(this.entity.getOwner())) {
                        continue;
                    }

                    if (!this.entity.canAttackClass(victim.getClass())) {
                        continue;
                    }

                    boolean isHostile = victim instanceof IMob || victim instanceof EntityShulker;
                    boolean isCurrentTarget = victim.equals(this.entity.getAttackTarget());
                    boolean isProvokedBy = victim.equals(this.entity.getRevengeTarget());

                    if (isHostile || isCurrentTarget || isProvokedBy) {
                        victim.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.entity, this.entity), totalDamage);
                    }
                }
            }
        }

        private boolean isFacingTarget(EntityLivingBase target) {
            Vec3d lookVec = this.entity.getLookVec();

            Vec3d eyePos = new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.getEyeHeight(), this.entity.posZ);

            AxisAlignedBB box = target.getEntityBoundingBox();
            Vec3d targetCenter = new Vec3d(
                    (box.minX + box.maxX) / 2.0D,
                    (box.minY + box.maxY) / 2.0D,
                    (box.minZ + box.maxZ) / 2.0D
            );

            Vec3d targetDir = targetCenter.subtract(eyePos).normalize();

            double dot = lookVec.dotProduct(targetDir);

            return dot > 0.3D;
        }
    }
}