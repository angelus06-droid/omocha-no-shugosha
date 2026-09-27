package uut.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.item.ModItems;

public class EntityToyCrimsonBunny extends BaseDefensiveMob {

    private int attackTimer;
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyCrimsonBunny.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyCrimsonBunny.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyCrimsonBunny.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHARGED = EntityDataManager.createKey(EntityToyCrimsonBunny.class, DataSerializers.BOOLEAN);

    private int healTimer;
    private int chargeTimer = 0;
    private static final int MAX_CHARGE_TICKS = 140;

    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;

    public EntityToyCrimsonBunny(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 0.85f);
        this.jumpHelper = new EntityToyCrimsonBunny.RabbitJumpHelper(this);
        this.moveHelper = new EntityToyCrimsonBunny.RabbitMoveHelper(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getDyeDamage());
        this.dataManager.register(CHARGED, false);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
            }
        });

        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.5D, true));

        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.2D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && !isSitting() && getAttackTarget() == null && super.shouldExecute();
            }
        });

        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !isFollowing() && !isSitting() && getAttackTarget() == null && super.shouldExecute();
            }
        });

        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false,
                entity -> {
                    if (entity == null || isSitting() || !entity.isEntityAlive()) return false;
                    return entity instanceof IMob || entity instanceof net.minecraft.entity.monster.EntityShulker;
                }
        ));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.5D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(0.0F, damageMultiplier);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);

        double baseDamage = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

        if (this.isCharged()) {
            baseDamage *= 2.0D;

            if (entityIn instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase) entityIn;
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 255, false, false));
                target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60, 5, false, false));
            }

            if (!this.world.isRemote && this.world instanceof WorldServer) {
                spawnXParticles((WorldServer) this.world, entityIn);
            }

            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.5F, 1.5F);

            this.setCharged(false);
            this.chargeTimer = 0;
        }

        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) baseDamage);
    }

    @SideOnly(Side.CLIENT)
    public float getJumpCompletion(float partialTicks) {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + partialTicks) / (float) this.jumpDuration;
    }

    private void spawnXParticles(WorldServer worldServer, Entity target) {
        double cx = target.posX;
        double cy = target.posY + (target.height / 2.0D);
        double cz = target.posZ;
        double size = 0.8D;

        for (double i = -size; i <= size; i += 0.15D) {
            worldServer.spawnParticle(EnumParticleTypes.REDSTONE, cx + i, cy + i, cz, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            worldServer.spawnParticle(EnumParticleTypes.REDSTONE, cx + i, cy - i, cz, 0, 0.001D, 0.0D, 0.0D, 1.0D);
            worldServer.spawnParticle(EnumParticleTypes.REDSTONE, cx, cy + i, cz + i, 0, 0.001D, 0.0D, 0.0D, 1.0D);
        }
    }

    @Override
    protected float getJumpUpwardsMotion() {
        if (!this.collidedHorizontally && (!this.moveHelper.isUpdating() || this.moveHelper.getY() <= this.posY + 0.5D)) {
            Path path = this.navigator.getPath();
            if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
                Vec3d vec3d = path.getPosition(this);
                if (vec3d.y > this.posY + 0.5D) {
                    return 0.5F;
                }
            }
            return this.moveHelper.getSpeed() <= 0.6D ? 0.2F : 0.3F;
        } else {
            return 0.5F;
        }
    }

    @Override
    protected void jump() {
        super.jump();
        double d0 = this.moveHelper.getSpeed();
        if (d0 > 0.0D) {
            double d1 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d1 < 0.01D) {
                this.moveRelative(0.0F, 0.0F, 1.0F, 0.25F);
            }
        }
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte) 1);
        }
    }

    public void setMovementSpeed(double newSpeed) {
        this.getNavigator().setSpeed(newSpeed);
        this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    public void updateAITasks() {
        if (this.currentMoveTypeDuration > 0) {
            --this.currentMoveTypeDuration;
        }

        if (this.onGround) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            RabbitJumpHelper jumpHelper = (RabbitJumpHelper) this.jumpHelper;

            if (jumpHelper.canJump()) {
                this.startJumping();
                jumpHelper.setCanJump(false);
            }
        }

        this.wasOnGround = this.onGround;
    }

    private void checkLandingDelay() {
        if (this.moveHelper.getSpeed() < 2.2D) {
            this.currentMoveTypeDuration = 4;
        } else {
            this.currentMoveTypeDuration = 1;
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {
            if (!this.isCharged()) {
                this.chargeTimer++;
                if (this.chargeTimer >= MAX_CHARGE_TICKS) {
                    this.setCharged(true);
                }
            }
        } else {
            if (this.isCharged()) {
                for (int i = 0; i < 2; ++i) {
                    double px = this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width;
                    double py = this.posY + this.rand.nextDouble() * (double) this.height;
                    double pz = this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width;
                    this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        if (this.jumpTicks < this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }

        if (this.attackTimer > 0) --this.attackTimer;

        if (!this.world.isRemote && this.isFollowing() && this.getAttackTarget() == null && !this.isSitting()) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null && !owner.isDead) {
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

    public boolean isCharged() { return this.dataManager.get(CHARGED); }
    public void setCharged(boolean charged) { this.dataManager.set(CHARGED, charged); }

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("following", this.isFollowing());
        compound.setBoolean("sitting", this.isSitting());
        compound.setByte("CollarColor", (byte) this.getCollarColor().getDyeDamage());
        compound.setBoolean("charged", this.isCharged());
        compound.setInteger("chargeTimer", this.chargeTimer);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
        this.setCharged(compound.getBoolean("charged"));
        this.chargeTimer = compound.getInteger("chargeTimer");
    }

    public static class RabbitJumpHelper extends EntityJumpHelper {
        private final EntityToyCrimsonBunny rabbit;
        private boolean canJump;

        public RabbitJumpHelper(EntityToyCrimsonBunny rabbit) {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public boolean getIsJumping() { return this.isJumping; }
        public boolean canJump() { return this.canJump; }
        public void setCanJump(boolean canJumpIn) { this.canJump = canJumpIn; }

        @Override
        public void doJump() {
            if (this.isJumping) {
                this.rabbit.startJumping();
                this.isJumping = false;
            }
        }
    }

    public static class RabbitMoveHelper extends EntityMoveHelper {
        private final EntityToyCrimsonBunny rabbit;
        private double nextJumpSpeed;

        public RabbitMoveHelper(EntityToyCrimsonBunny rabbit) {
            super(rabbit);
            this.rabbit = rabbit;
        }

        @Override
        public void onUpdateMoveHelper() {
            if (this.rabbit.onGround && !this.rabbit.isJumping && !((RabbitJumpHelper) this.rabbit.jumpHelper).getIsJumping()) {
                this.rabbit.setMovementSpeed(0.0D);
            } else if (this.isUpdating()) {
                this.rabbit.setMovementSpeed(this.nextJumpSpeed);
            }

            super.onUpdateMoveHelper();
        }

        @Override
        public void setMoveTo(double x, double y, double z, double speedIn) {
            if (this.rabbit.isInWater()) {
                speedIn = 1.5D;
            }
            super.setMoveTo(x, y, z, speedIn);
            if (speedIn > 0.0D) {
                this.nextJumpSpeed = speedIn;
                ((RabbitJumpHelper) this.rabbit.jumpHelper).setCanJump(true);
            }
        }
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

            this.world.spawnParticle(type, px, py, pz, (double) red, (double) green, (double) blue);
        }
    }

    private void teleportToOwner(EntityLivingBase owner) {
        for (int i = 0; i < 10; ++i) {
            int offsetX = this.rand.nextInt(7) - 3;
            int offsetZ = this.rand.nextInt(7) - 3;

            BlockPos targetPos = new BlockPos(owner.posX + offsetX, owner.getEntityBoundingBox().minY, owner.posZ + offsetZ);

            if (this.isValidTeleportPos(targetPos)) {
                this.setLocationAndAngles((double) targetPos.getX() + 0.5D, (double) targetPos.getY(), (double) targetPos.getZ() + 0.5D, this.rotationYaw, this.rotationPitch);
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
        else if (id == 1) {
            this.createRunningParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else super.handleStatusUpdate(id);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() { return this.attackTimer; }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_RABBIT_JUMP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.ENTITY_RABBIT_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_RABBIT_DEATH; }
}