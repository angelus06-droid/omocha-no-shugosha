package uut.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
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
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import uut.entity.ai.EntityAISpaceshipFollowOwner;
import uut.item.ModItems;

public class EntityToySpaceship extends BaseDefensiveMob {
    private int healTimer;
    private int attackTimer;
    private int dodgeCooldown = 0;

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToySpaceship.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToySpaceship.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToySpaceship.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityToySpaceship.class, DataSerializers.BOOLEAN);

    public EntityToySpaceship(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 0.65f);
        this.isImmuneToFire = true;
        this.moveHelper = new SpaceshipMoveHelper(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.WHITE.getDyeDamage());
        this.dataManager.register(FLYING, true);
    }

    @Override
    protected PathNavigate createNavigator(World worldIn) {
        PathNavigateFlying flyingNavigator = new PathNavigateFlying(this, worldIn);
        flyingNavigator.setCanOpenDoors(false);
        flyingNavigator.setCanFloat(true);
        flyingNavigator.setCanEnterDoors(true);
        return flyingNavigator;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(2, new EntityAISpaceshipFollowOwner(this, 1.2D, 5.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && !isSitting() && super.shouldExecute();
            }

            @Override
            public boolean shouldContinueExecuting() {
                return isFollowing() && !isSitting() && super.shouldContinueExecuting();
            }
        });

        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
            }
        });

        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.2D, false));

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !isFollowing() && !isSitting() && super.shouldExecute();
            }
        });

        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> !isSitting() && entity instanceof IMob));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.isFlying()) {
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }

        if (this.attackTimer > 0) --this.attackTimer;
        if (this.dodgeCooldown > 0) --this.dodgeCooldown;

        if (this.isSitting()) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
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

            if (this.isFollowing() && this.getAttackTarget() == null && !this.isSitting()) {
                EntityLivingBase owner = this.getOwner();
                if (owner != null && this.getDistanceSq(owner) > 144.0D) {
                    this.teleportToOwner(owner);
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource s, float amount) {
        if (s == DamageSource.IN_WALL || s == DamageSource.FALL || "poison".equals(s.damageType)) {
            return false;
        }

        if (!this.isSitting() && this.dodgeCooldown <= 0 && s.getTrueSource() != null) {
            if (this.rand.nextFloat() < 0.60F) {
                this.hurtResistantTime = 20;
                this.dodgeCooldown = 40;
                return false;
            }
        }

        return super.attackEntityFrom(s, amount);
    }

    private void teleportToOwner(EntityLivingBase owner) {
        for (int i = 0; i < 10; ++i) {
            int offsetX = this.rand.nextInt(7) - 3;
            int offsetZ = this.rand.nextInt(7) - 3;

            BlockPos targetPos = new BlockPos(owner.posX + offsetX, owner.getEntityBoundingBox().minY + 1, owner.posZ + offsetZ);

            if (this.isValidTeleportPos(targetPos)) {
                this.setLocationAndAngles((double)targetPos.getX() + 0.5D, (double)targetPos.getY(), (double)targetPos.getZ() + 0.5D, this.rotationYaw, this.rotationPitch);
                this.getNavigator().clearPath();
                return;
            }
        }
    }

    private boolean isValidTeleportPos(BlockPos pos) {
        return this.isSafeToStandAt(pos) && this.isSafeToStandAt(pos.up());
    }

    private boolean isSafeToStandAt(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return !state.getMaterial().isSolid() && !state.getMaterial().isLiquid();
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (this.isOwner(player)) {
            if (itemstack.getItem() == ModItems.toy_command_staff) {
                if (!this.world.isRemote) {
                    if (isFollowing()) {
                        setFollowing(false);
                        setSitting(true);
                        setFlying(false);
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.sit"), true);
                    } else if (isSitting()) {
                        setFollowing(false);
                        setSitting(false);
                        setFlying(true);
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.free"), true);
                    } else {
                        setFollowing(true);
                        setSitting(false);
                        setFlying(true);
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.follow"), true);
                    }
                    this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                } else {
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

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte)4);
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }

    @Override public void fall(float distance, float damageMultiplier) {}
    @Override protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {}

    @Override
    public boolean isPotionApplicable(PotionEffect pot) {
        return pot.getPotion() != MobEffects.POISON && super.isPotionApplicable(pot);
    }

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }

    public boolean isFlying() { return this.dataManager.get(FLYING); }
    public void setFlying(boolean flying) { this.dataManager.set(FLYING, flying); }

    public EnumDyeColor getCollarColor() {
        return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR));
    }

    public void setCollarColor(EnumDyeColor color) {
        this.dataManager.set(COLLAR_COLOR, color.getDyeDamage());
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("following", this.isFollowing());
        compound.setBoolean("sitting", this.isSitting());
        compound.setBoolean("flying", this.isFlying());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        if (compound.hasKey("flying")) {
            this.setFlying(compound.getBoolean("flying"));
        }
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
    }

    @Override protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.BLOCK_METAL_HIT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_METAL_BREAK; }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    static class SpaceshipMoveHelper extends EntityMoveHelper {
        private final EntityToySpaceship ship;
        private int stuckTicks = 0;
        private double lastX, lastY, lastZ;

        public SpaceshipMoveHelper(EntityToySpaceship ship) {
            super(ship);
            this.ship = ship;
        }

        @Override
        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO
                    && this.ship.isFlying() && !this.ship.isSitting()) {
                double dx = this.posX - this.ship.posX;
                double dy = this.posY - this.ship.posY;
                double dz = this.posZ - this.ship.posZ;
                double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

                double movedDistSq = (this.ship.posX - this.lastX) * (this.ship.posX - this.lastX) +
                        (this.ship.posY - this.lastY) * (this.ship.posY - this.lastY) +
                        (this.ship.posZ - this.lastZ) * (this.ship.posZ - this.lastZ);

                if (movedDistSq < 0.0005D) {
                    this.stuckTicks++;
                } else {
                    this.stuckTicks = 0;
                }

                this.lastX = this.ship.posX;
                this.lastY = this.ship.posY;
                this.lastZ = this.ship.posZ;

                if (this.stuckTicks > 15) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    this.ship.getNavigator().clearPath();
                    this.stuckTicks = 0;

                    this.ship.motionY += 0.15D;
                    this.ship.motionX += (this.ship.rand.nextDouble() - 0.5D) * 0.2D;
                    this.ship.motionZ += (this.ship.rand.nextDouble() - 0.5D) * 0.2D;
                    return;
                }

                if (dist < this.ship.getEntityBoundingBox().getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                } else {
                    double flyingSpeed = this.ship.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
                    double currentSpeed = flyingSpeed * this.speed * 0.5D;

                    this.ship.motionX += (dx / dist * currentSpeed - this.ship.motionX) * 0.25D;
                    this.ship.motionY += (dy / dist * currentSpeed - this.ship.motionY) * 0.25D;
                    this.ship.motionZ += (dz / dist * currentSpeed - this.ship.motionZ) * 0.25D;

                    float targetYaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                    this.ship.rotationYaw = this.limitAngle(this.ship.rotationYaw, targetYaw, 18.0F);
                    this.ship.renderYawOffset = this.ship.rotationYaw;
                }
            } else {
                super.onUpdateMoveHelper();
                this.stuckTicks = 0;

                if (this.ship.isFlying() && !this.ship.isSitting()) {
                    this.ship.motionX *= 0.8D;
                    this.ship.motionY *= 0.8D;
                    this.ship.motionZ *= 0.8D;
                }
            }
        }
    }
}