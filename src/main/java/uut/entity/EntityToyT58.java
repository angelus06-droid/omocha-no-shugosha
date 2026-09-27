package uut.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
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
import uut.entity.ai.EntityAIT58FollowOwner;
import uut.entity.projectiles.EntityT58Bullet;
import uut.item.ModItems;
import uut.util.ModSounds;

public class EntityToyT58 extends BaseDefensiveMob implements IRangedAttackMob {

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyT58.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyT58.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> MELEE_MODE = EntityDataManager.createKey(EntityToyT58.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyT58.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityToyT58.class, DataSerializers.BOOLEAN);

    private int healTimer;
    private int attackTimer;
    private int phaseTimer = 300;
    private int meleeComboCount = 0;
    private int dodgeCooldown = 0;
    private static final int DODGE_COOLDOWN_TICKS = 40;
    private static final float DODGE_CHANCE = 0.25F;

    public EntityToyT58(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 0.55f);
        this.isImmuneToFire = true;
        this.moveHelper = new T58MoveHelper(this);
        this.phaseTimer = 300 + this.rand.nextInt(101);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(MELEE_MODE, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.GRAY.getDyeDamage());
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

        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() { return isSitting(); }
        });

        this.tasks.addTask(4, new EntityAIT58FollowOwner(this, 1.2D, 5.0F, 2.0F) {
            @Override
            public boolean shouldExecute() { return isFollowing() && !isSitting() && super.shouldExecute(); }

            @Override
            public boolean shouldContinueExecuting() { return isFollowing() && !isSitting() && super.shouldContinueExecuting(); }
        });

        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false) {
            @Override
            public boolean shouldExecute() {
                return isMeleeMode() && !isSitting() && super.shouldExecute();
            }
            @Override
            public boolean shouldContinueExecuting() {
                return isMeleeMode() && !isSitting() && super.shouldContinueExecuting();
            }
        });

        this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 10, 10.0F) {
            @Override
            public boolean shouldExecute() {
                return !isMeleeMode() && !isSitting() && super.shouldExecute();
            }
            @Override
            public boolean shouldContinueExecuting() {
                return !isMeleeMode() && !isSitting() && super.shouldContinueExecuting();
            }
        });

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() { return !isFollowing() && !isSitting() && super.shouldExecute(); }
        });

        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false,
                entity -> !isSitting() && entity instanceof IMob));
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

        EntityLivingBase target = this.getAttackTarget();
        if (target != null && !this.isMeleeMode() && !this.isSitting()) {
            double dx = target.posX - this.posX;
            double dz = target.posZ - this.posZ;
            float targetYaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            this.rotationYaw = this.turnTowards(this.rotationYaw, targetYaw, 18.0F);
            this.renderYawOffset = this.rotationYaw;
        }

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

            if (this.phaseTimer > 0) {
                this.phaseTimer--;
            } else {
                boolean newMode = !this.isMeleeMode();
                this.setMeleeMode(newMode);

                this.phaseTimer = 300 + this.rand.nextInt(101);

                this.world.setEntityState(this, (byte) 10);
                this.getNavigator().clearPath();
            }

            if (this.isFollowing() && this.getAttackTarget() == null && !this.isSitting()) {
                EntityLivingBase owner = this.getOwner();
                if (owner != null && this.getDistanceSq(owner) > 144.0D) {
                    this.teleportToOwner(owner);
                }
            }
        } else {
            if (this.ticksExisted % 2 == 0) {
                double red = 109 / 255.0D;
                double green = 209 / 255.0D;
                double blue = 255 / 255.0D;

                this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY + 0.05, this.posZ, red, green, blue);
            }
        }
    }

    private float turnTowards(float currentYaw, float targetYaw, float maxStep) {
        float diff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        if (diff > maxStep) diff = maxStep;
        if (diff < -maxStep) diff = -maxStep;
        return currentYaw + diff;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 10) {
            this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5F, 2.0F);
            for (int i = 0; i < 10; ++i) {
                this.world.spawnParticle(EnumParticleTypes.END_ROD,
                        this.posX + (this.rand.nextDouble() - 0.5D) * this.width,
                        this.posY + this.rand.nextDouble() * this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * this.width,
                        0.0D, 0.0D, 0.0D);
            }
        } else if (id == 11) {
            double r = 109 / 255.0D;
            double g = 209 / 255.0D;
            double b = 255 / 255.0D;

            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < 20; ++i) {
                double vx = (this.rand.nextDouble() - 0.5D) * 0.5D;
                double vy = (this.rand.nextDouble() - 0.5D) * 0.5D;
                double vz = (this.rand.nextDouble() - 0.5D) * 0.5D;
                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, vx, vy, vz);
                this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX, this.posY, this.posZ, r, g, b);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (!this.getEntitySenses().canSee(target)) return;

        EntityT58Bullet bullet = new EntityT58Bullet(this.world, this);
        double spawnY = this.posY + 0.1D;
        bullet.setLocationAndAngles(this.posX, spawnY, this.posZ, this.rotationYaw, 0.0F);

        double d0 = target.posX - this.posX;
        double d1 = (target.getEntityBoundingBox().minY + (double)(target.height / 2.0F)) - spawnY;
        double d2 = target.posZ - this.posZ;
        bullet.shoot(d0, d1, d2, 1.0F, 0.0F);

        float baseDamage = 2.5F;
        bullet.setDamage(baseDamage + this.getRangedAttackBonus());

        this.playSound(ModSounds.t58_shoot, 0.4F, 1.0F);

        this.world.spawnEntity(bullet);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int knockback = 0;

        if (entityIn instanceof EntityLivingBase) {
            damage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
            knockback += EnchantmentHelper.getKnockbackModifier(this);
        }

        this.meleeComboCount++;
        boolean isChargedHit = this.meleeComboCount >= 3;
        if (isChargedHit) {
            damage *= 1.0F;
            this.meleeComboCount = 0;
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        if (flag) {
            if (knockback > 0) {
                entityIn.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            if (isChargedHit) {
                this.world.setEntityState(this, (byte) 12);
            }

            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.65D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        if (!this.world.isRemote) {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 0.0F, false);

            for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(2.0D))) {
                if (entity != this) {
                    entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), 2.0F);
                }
            }

            this.world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.t58_death, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            this.world.setEntityState(this, (byte) 11);
        }
    }

    public boolean isMeleeMode() { return this.dataManager.get(MELEE_MODE); }
    public void setMeleeMode(boolean mode) { this.dataManager.set(MELEE_MODE, mode); }
    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }
    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }
    public boolean isFlying() { return this.dataManager.get(FLYING); }
    public void setFlying(boolean flying) { this.dataManager.set(FLYING, flying); }

    @Override
    public void setSwingingArms(boolean swingingArms) {}

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

                    this.playSound(SoundEvents.ENTITY_IRONGOLEM_HURT, 0.9F, 2.0F);
                } else {
                    if (this.isSitting()) {
                        this.spawnArcadeBurstParticle(0.0F, 0.6F, 1.0F);
                    } else if (this.isFollowing()) {
                        this.spawnArcadeBurstParticle(1.0F, 0.1F, 0.1F);
                    } else {
                        this.spawnArcadeBurstParticle(0.1F, 1.0F, 0.2F);
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

    private void spawnArcadeBurstParticle(float red, float green, float blue) {
        int particlesCount = 24;

        this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY + (this.height / 2), this.posZ, 0.0D, 0.0D, 0.0D);

        for (int i = 0; i < particlesCount; ++i) {
            double angle = (i * Math.PI * 2) / particlesCount;
            double speed = 0.35D;

            double vx = Math.cos(angle) * speed;
            double vy = (this.rand.nextDouble() - 0.5D) * 0.1D;
            double vz = Math.sin(angle) * speed;

            this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                    this.posX, this.posY + (this.height / 2), this.posZ,
                    vx, vy, vz);

            this.world.spawnParticle(EnumParticleTypes.SPELL_MOB,
                    this.posX, this.posY + (this.height / 2), this.posZ,
                    (double)red, (double)green, (double)blue);

        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect pot) {
        return pot.getPotion() != MobEffects.POISON && super.isPotionApplicable(pot);
    }
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL || source == DamageSource.FALL || "poison".equals(source.damageType)) {
            return false;
        }

        if (!source.isUnblockable() && !this.isSitting() && this.dodgeCooldown <= 0 && this.isEntityAlive()) {
            if (this.rand.nextFloat() < DODGE_CHANCE) {
                this.dodgeCooldown = DODGE_COOLDOWN_TICKS;

                if (!this.world.isRemote) {
                    this.world.setEntityState(this, (byte) 12);
                }

                return false;
            }
        }

        return super.attackEntityFrom(source, amount);
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
        compound.setBoolean("meleeMode", this.isMeleeMode());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        this.setMeleeMode(compound.getBoolean("meleeMode"));
        if (compound.hasKey("flying")) {
            this.setFlying(compound.getBoolean("flying"));
        }
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
    }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    @Override public void fall(float d, float m) {}
    @Override protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {}
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.BLOCK_METAL_HIT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_METAL_BREAK; }

    static class T58MoveHelper extends EntityMoveHelper {
        private final EntityToyT58 entity;
        private int stuckTicks = 0;
        private double lastX, lastY, lastZ;

        public T58MoveHelper(EntityToyT58 entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        public void onUpdateMoveHelper() {
            if (this.action == Action.MOVE_TO
                    && this.entity.isFlying() && !this.entity.isSitting()) {
                double dx = this.posX - this.entity.posX;
                double dy = this.posY - this.entity.posY;
                double dz = this.posZ - this.entity.posZ;
                double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

                double movedDistSq = (this.entity.posX - this.lastX) * (this.entity.posX - this.lastX) +
                        (this.entity.posY - this.lastY) * (this.entity.posY - this.lastY) +
                        (this.entity.posZ - this.lastZ) * (this.entity.posZ - this.lastZ);

                if (movedDistSq < 0.0005D) {
                    this.stuckTicks++;
                } else {
                    this.stuckTicks = 0;
                }

                this.lastX = this.entity.posX;
                this.lastY = this.entity.posY;
                this.lastZ = this.entity.posZ;

                if (this.stuckTicks > 15) {
                    this.action = Action.WAIT;
                    this.entity.getNavigator().clearPath();
                    this.stuckTicks = 0;

                    this.entity.motionY += 0.15D;
                    this.entity.motionX += (this.entity.rand.nextDouble() - 0.5D) * 0.2D;
                    this.entity.motionZ += (this.entity.rand.nextDouble() - 0.5D) * 0.2D;
                    return;
                }

                if (dist < this.entity.getEntityBoundingBox().getAverageEdgeLength()) {
                    this.action = Action.WAIT;
                } else {
                    double flyingSpeed = this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
                    double currentSpeed = flyingSpeed * this.speed * 0.5D;

                    this.entity.motionX += (dx / dist * currentSpeed - this.entity.motionX) * 0.25D;
                    this.entity.motionY += (dy / dist * currentSpeed - this.entity.motionY) * 0.25D;
                    this.entity.motionZ += (dz / dist * currentSpeed - this.entity.motionZ) * 0.25D;

                    float targetYaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                    this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, targetYaw, 18.0F);
                    this.entity.renderYawOffset = this.entity.rotationYaw;
                }
            } else {
                super.onUpdateMoveHelper();
                this.stuckTicks = 0;

                if (this.entity.isFlying() && !this.entity.isSitting()) {
                    this.entity.motionX *= 0.8D;
                    this.entity.motionY *= 0.8D;
                    this.entity.motionZ *= 0.8D;
                }
            }
        }
    }
}