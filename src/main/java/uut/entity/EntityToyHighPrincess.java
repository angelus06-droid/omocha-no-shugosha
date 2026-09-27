package uut.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.IRangedAttackMob;
import uut.item.ModItems;
import uut.util.ModSounds;

public class EntityToyHighPrincess extends BaseDefensiveMob implements IRangedAttackMob {

    private int attackTimer;
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyHighPrincess.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyHighPrincess.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SPELLCASTING = EntityDataManager.createKey(EntityToyHighPrincess.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyHighPrincess.class, DataSerializers.VARINT);

    private int healTimer;
    private int spellTicks;
    private int spellCooldown;

    public EntityToyHighPrincess(World worldIn) {
        super(worldIn);
        this.setSize(0.35f, 1.2f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(SPELLCASTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.GREEN.getDyeDamage());
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

        this.tasks.addTask(3, new EntityAIAttackRanged(this, 1.0D, 60, 16.0F));

        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.1D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && super.shouldExecute();
            }
        });

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !isFollowing() && super.shouldExecute();
            }
        });

        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));

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
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.attackTimer > 0) --this.attackTimer;

        if (!this.world.isRemote) {
            if (this.spellCooldown > 0) {
                this.spellCooldown--;
            }

            if (this.isSpellcasting()) {
                this.spellTicks++;

                if (this.rand.nextInt(2) == 0) {
                    this.world.setEntityState(this, (byte) 83);
                }

                if (this.spellTicks >= 40) {
                    this.summonMinions();
                    this.setSpellcasting(false);
                    this.spellCooldown = 60;
                }
            }
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

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (!this.world.isRemote && !this.isSitting() && !this.isSpellcasting() && this.spellCooldown <= 0) {
            this.setSpellcasting(true);
            this.spellTicks = 0;
            this.world.setEntityState(this, (byte) 82);
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    private void summonMinions() {
        int amount = 1 + this.rand.nextInt(3);

        for (int i = 0; i < amount; ++i) {
            double spawnX = this.posX + (this.rand.nextDouble() - 0.5D) * 3.0D;
            double spawnY = this.posY;
            double spawnZ = this.posZ + (this.rand.nextDouble() - 0.5D) * 3.0D;
            BlockPos spawnPos = new BlockPos(spawnX, spawnY, spawnZ);

            BaseSummonableMob minion;
            float chance = this.rand.nextFloat();

            if (chance < 0.05F) {
                if (this.rand.nextBoolean()) {
                    minion = new EntityToyPrincessTroll(this.world);
                } else {
                    minion = new EntityToyPrincessEnt(this.world);
                }
            }
            else if (chance < 0.20F) {
                minion = new EntityToyPrincessShooter(this.world);
            }
            else if (chance < 0.55F) {
                if (this.rand.nextBoolean()) {
                    minion = new EntityToyPrincessOgre(this.world);
                } else {
                    minion = new EntityToyPrincessGuard(this.world);
                }
            }
            else {
                if (this.rand.nextBoolean()) {
                    minion = new EntityToyPrincessGoblin(this.world);
                } else {
                    minion = new EntityToyPrincessRecruit(this.world);
                }
            }

            minion.setLocationAndAngles(spawnX + 0.5D, spawnY, spawnZ + 0.5D, this.rotationYaw, 0.0F);

            if (this.getOwnerId() != null) {
                minion.setOwnerId(this.getOwnerId());
                minion.setTamed(true);
            }

            if (minion instanceof EntityLiving) {
                ((EntityLiving) minion).onInitialSpawn(this.world.getDifficultyForLocation(spawnPos), null);
            }

            if (this.getAttackTarget() != null) {
                minion.setAttackTarget(this.getAttackTarget());
            }

            this.applyBoostsToMinion(minion);

            this.world.spawnEntity(minion);
        }

        this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ILLAGER_CAST_SPELL, this.getSoundCategory(), 1.0F, 1.5F);
    }

    private void applyBoostsToMinion(BaseSummonableMob minion) {
        IAttributeInstance minionHealth = minion.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (minionHealth != null) {
            double extraHealth = this.getHeartBoostCount() * 1.0D;
            minionHealth.setBaseValue(minionHealth.getBaseValue() + extraHealth);
            minion.heal((float) extraHealth);
        }

        IAttributeInstance minionArmor = minion.getEntityAttribute(SharedMonsterAttributes.ARMOR);
        if (minionArmor != null) {
            double extraArmor = this.getDefenseBoostCount() * 1.0D;
            minionArmor.setBaseValue(minionArmor.getBaseValue() + extraArmor);
        }

        IAttributeInstance minionAttack = minion.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        if (minionAttack != null) {
            double extraAttack = this.getAttackBoostCount() * 1.0D;
            minionAttack.setBaseValue(minionAttack.getBaseValue() + extraAttack);
        }
        IAttributeInstance minionSpeed = minion.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (minionSpeed != null) {
            double extraAttack = this.getSpeedBoostCount() * 0.01D;
            minionSpeed.setBaseValue(minionSpeed.getBaseValue() + extraAttack);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
        } else if (id == 82) {
            this.world.playSound(this.posX, this.posY, this.posZ, ModSounds.high_princess_prepare_summon, this.getSoundCategory(), 1.0F, 1.0F, false);
        } else if (id == 83) {

            float yawRadian = this.renderYawOffset * 0.017453292F;

            double motionXLeft = Math.cos(yawRadian) * 0.5D;
            double motionZLeft = Math.sin(yawRadian) * 0.5D;

            double motionXRight = -Math.cos(yawRadian) * 0.5D;
            double motionZRight = -Math.sin(yawRadian) * 0.5D;

            double spawnY = this.posY + (this.height * 0.8D);

            for (int i = 0; i < 3; ++i) {
                double px = this.posX + motionXLeft + (this.rand.nextDouble() - 0.5D) * 0.2D;
                double pz = this.posZ + motionZLeft + (this.rand.nextDouble() - 0.5D) * 0.2D;

                this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, px, spawnY + (this.rand.nextDouble() - 0.5D) * 0.3D, pz, 0.0D, 0.8D, 0.0D);
            }

            for (int i = 0; i < 3; ++i) {
                double px = this.posX + motionXRight + (this.rand.nextDouble() - 0.5D) * 0.2D;
                double pz = this.posZ + motionZRight + (this.rand.nextDouble() - 0.5D) * 0.2D;

                this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, px, spawnY + (this.rand.nextDouble() - 0.5D) * 0.3D, pz, 0.0D, 0.8D, 0.0D);
            }

        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return false;
    }

    public boolean isSpellcasting() { return this.dataManager.get(SPELLCASTING); }
    public void setSpellcasting(boolean casting) { this.dataManager.set(SPELLCASTING, casting); }

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
        return this.world.getBlockState(pos.down()).isSideSolid(this.world, pos.down(), EnumFacing.UP) && this.isSafeToStandAt(pos) && this.isSafeToStandAt(pos.up());
    }

    private boolean isSafeToStandAt(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return !state.getMaterial().isSolid() && !state.getMaterial().isLiquid();
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
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15.0D);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() { return this.attackTimer; }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) { this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.25f, 1.5f); }
    @Override
    protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.BLOCK_STONE_HIT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_STONE_BREAK; }
}