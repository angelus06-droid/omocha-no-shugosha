package uut.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
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
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.item.ModItems;
import uut.util.ModSounds;

public class EntityToyDestroyerPity extends BaseDefensiveMob {

    private int attackTimer;
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyDestroyerPity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyDestroyerPity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyDestroyerPity.class, DataSerializers.VARINT);

    private int healTimer;
    private double waterTargetX;
    private double waterTargetY;
    private double waterTargetZ;
    private int waterWanderTicks;
    private int waterIdleTicks;

    public EntityToyDestroyerPity(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 0.85f);
        this.setNavigatorPaths();
    }

    private void setNavigatorPaths() {
        if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround) this.getNavigator()).setCanSwim(true);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.GRAY.getDyeDamage());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.1D, false) {
            @Override
            public boolean shouldExecute() {
                return !EntityToyDestroyerPity.this.isInWater() && !EntityToyDestroyerPity.this.isSitting() && super.shouldExecute();
            }
        });

        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.1D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return EntityToyDestroyerPity.this.isFollowing() && !EntityToyDestroyerPity.this.isSitting() && !EntityToyDestroyerPity.this.isInWater() && super.shouldExecute();
            }
        });

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !EntityToyDestroyerPity.this.isFollowing() && !EntityToyDestroyerPity.this.isSitting() && !EntityToyDestroyerPity.this.isInWater() && super.shouldExecute();
            }
        });

        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return EntityToyDestroyerPity.this.isSitting();
            }
        });

        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> {
            if (entity == null || EntityToyDestroyerPity.this.isSitting() || !entity.isEntityAlive()) return false;
            return entity instanceof IMob || entity instanceof net.minecraft.entity.monster.EntityShulker;
        }));
    }

    @Override
    public void travel(float strafe, float up, float forward) {
        if (this.isSitting()) {
            super.travel(0F, 0F, 0F);
            return;
        }

        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(strafe, up, forward, 0.1F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

            this.motionX *= 0.88D;
            this.motionY *= 0.88D;
            this.motionZ *= 0.88D;

            if (this.collidedHorizontally) {
                BlockPos headPos = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
                if (this.world.getBlockState(headPos.up()).getMaterial() == Material.AIR) {
                    this.motionY = 0.35D;
                }
            }

            if (this.getAttackTarget() == null && this.isFollowing() && this.getOwner() != null) {
                EntityLivingBase owner = this.getOwner();

                double dx = owner.posX - this.posX;
                double dy = owner.posY - this.posY;
                double dz = owner.posZ - this.posZ;
                double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

                if (dist > 10.0D || (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D && dist > 2.0D)) {
                    double targetSpeed = 0.035D;

                    this.motionX += (dx / dist) * targetSpeed;
                    this.motionY += (dy / dist) * targetSpeed;
                    this.motionZ += (dz / dist) * targetSpeed;

                    float targetYaw = (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                    this.rotationYaw = this.rotationYawHead = this.renderYawOffset = this.updateRotation(this.rotationYaw, targetYaw, 10.0F);
                } else if (dist <= 2.0D) {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }
            }
            else if (this.getAttackTarget() == null && !this.isFollowing()) {
                if (this.waterWanderTicks > 0) {
                    --this.waterWanderTicks;
                }
                if (this.waterIdleTicks > 0) {
                    --this.waterIdleTicks;
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                if (this.waterIdleTicks <= 0 && (this.waterWanderTicks <= 0 || this.getDistanceSq(this.waterTargetX, this.waterTargetY, this.waterTargetZ) < 2.0D)) {

                    if (this.rand.nextBoolean()) {
                        this.waterIdleTicks = 40 + this.rand.nextInt(60);
                    } else {
                        this.waterTargetX = this.posX + (double)(this.rand.nextFloat() * 12.0F - 6.0F);
                        this.waterTargetY = this.posY + (double)(this.rand.nextFloat() * 4.0F - 2.0F);
                        this.waterTargetZ = this.posZ + (double)(this.rand.nextFloat() * 12.0F - 6.0F);
                        this.waterWanderTicks = 80 + this.rand.nextInt(80);
                    }
                }

                if (this.waterIdleTicks <= 0) {
                    BlockPos targetPos = new BlockPos(this.waterTargetX, this.waterTargetY, this.waterTargetZ);
                    if (this.world.getBlockState(targetPos).getMaterial() == Material.WATER) {
                        double dx = this.waterTargetX - this.posX;
                        double dy = this.waterTargetY - this.posY;
                        double dz = this.waterTargetZ - this.posZ;
                        double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

                        if (dist > 0.5D) {
                            this.motionX += (dx / dist) * 0.015D;
                            this.motionY += (dy / dist) * 0.015D;
                            this.motionZ += (dz / dist) * 0.015D;

                            float targetYaw = (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                            this.rotationYaw = this.rotationYawHead = this.renderYawOffset = this.updateRotation(this.rotationYaw, targetYaw, 5.0F);
                        }
                    } else {
                        this.waterWanderTicks = 0;
                    }
                }
            }
        } else {
            super.travel(strafe, up, forward);
        }
    }

    private float updateRotation(float current, float target, float maxChange) {
        float f = MathHelper.wrapDegrees(target - current);
        if (f > maxChange) f = maxChange;
        if (f < -maxChange) f = -maxChange;
        return current + f;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.attackTimer > 0) --this.attackTimer;

        if (!this.world.isRemote) {
            if (this.isInWater() && this.getAttackTarget() != null && !this.isSitting()) {
                EntityLivingBase target = this.getAttackTarget();

                double dx = target.posX - this.posX;
                double dy = (target.posY + (double)target.getEyeHeight() * 0.5D) - this.posY;
                double dz = target.posZ - this.posZ;
                double distanceSq = dx * dx + dy * dy + dz * dz;
                double distance = MathHelper.sqrt(distanceSq);

                if (distance > 0.5D) {
                    double speedModifier = 0.045D;
                    this.motionX += (dx / distance) * speedModifier;
                    this.motionY += (dy / distance) * speedModifier;
                    this.motionZ += (dz / distance) * speedModifier;

                    float targetYaw = (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                    this.rotationYaw = this.rotationYawHead = this.renderYawOffset = this.updateRotation(this.rotationYaw, targetYaw, 30.0F);
                }

                if (distanceSq < 2.5D && this.attackTimer <= 0) {
                    this.attackEntityAsMob(target);
                }
            }

            if (this.isFollowing() && !this.isSitting() && this.getAttackTarget() == null) {
                EntityLivingBase owner = this.getOwner();
                if (owner != null && this.getDistanceSq(owner) > 144.0D) {
                    this.teleportToOwner(owner);
                }
            }

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

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.5E-7D && this.rand.nextInt(5) == 0) {
            renderWalkParticles();
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && itemstack.getItem() == net.minecraft.init.Items.COOKIE) {
            if (this.getHealth() < this.getMaxHealth()) {
                if (!player.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                }

                this.heal(5.0F);

                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F, 1.6F);
                if (this.world.isRemote) {
                    this.spawnColoredParticle(EnumParticleTypes.HEART, 0, 0, 0);
                }
                return true;
            }
        }

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

                this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1.0F, 1.4F);
            }

            if (this.world.isRemote) {
                if (this.isSitting()) {
                    this.spawnColoredParticle(EnumParticleTypes.REDSTONE, 0.0F, 0.4F, 1.0F);
                } else if (this.isFollowing()) {
                    this.spawnColoredParticle(EnumParticleTypes.REDSTONE, 1.0F, 0.1F, 0.1F);
                } else {
                    this.spawnColoredParticle(EnumParticleTypes.REDSTONE, 0.1F, 1.0F, 0.2F);
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
        for (int i = 0; i < 7; ++i) {
            double px = this.posX + (this.rand.nextFloat() - 0.5D) * 0.3D;
            double py = this.posY + this.height * 0.5D + (this.rand.nextFloat() - 0.5D) * 0.3D;
            double pz = this.posZ + (this.rand.nextFloat() - 0.5D) * 0.3D;

            if (type == EnumParticleTypes.REDSTONE) {
                double r = (red == 0F) ? 0.001D : red;
                double g = (green == 0F) ? 0.001D : green;
                double b = (blue == 0F) ? 0.001D : blue;
                this.world.spawnParticle(type, px, py, pz, r, g, b);
            } else {
                this.world.spawnParticle(type, px, py, pz, 0.0D, 0.1D, 0.0D);
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
        IBlockState downState = this.world.getBlockState(pos.down());
        return (downState.isSideSolid(this.world, pos.down(), EnumFacing.UP) || downState.getMaterial() == Material.WATER) &&
                this.isSafeToStandAt(pos) &&
                this.isSafeToStandAt(pos.up());
    }

    private boolean isSafeToStandAt(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return !state.getMaterial().isSolid();
    }

    private void renderWalkParticles() {
        if (this.isInWater() && !this.isSitting()) {
            double speedSq = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;

            if (speedSq > 0.002D) {
                if (this.rand.nextInt(3) == 0) {
                    float yawRadians = this.rotationYaw * 0.017453292F;
                    double backX = this.posX - (double)(MathHelper.sin(yawRadians) * 0.35F);
                    double backZ = this.posZ + (double)(MathHelper.cos(yawRadians) * 0.35F);
                    double backY = this.posY + this.height * 0.3D;

                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, backX, backY, backZ, -this.motionX * 0.2D, -this.motionY * 0.1D, -this.motionZ * 0.2D);
                }

                if (this.ticksExisted % 20 == 0) {
                    this.playSound(this.getSwimSound(), 0.35F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                }
            }
        }
    }

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
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.5D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15.0D);
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

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_STEP, 0.25f, 3.0f);
    }
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.destroyer_pity_say;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return ModSounds.destroyer_pity_hurt;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.destroyer_pity_death;
    }
}