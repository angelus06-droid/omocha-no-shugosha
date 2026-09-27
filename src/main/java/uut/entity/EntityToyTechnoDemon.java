package uut.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityShulker;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.item.ModItems;

public class EntityToyTechnoDemon extends BaseDefensiveMob implements IRangedAttackMob {

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyTechnoDemon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyTechnoDemon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyTechnoDemon.class, DataSerializers.VARINT);

    private int healTimer;
    private int attackTimer;

    public EntityToyTechnoDemon(World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 0.8f);
        this.isImmuneToFire = true;
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
        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
            }
        });

        this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.0D, 40, 16.0f) {
            @Override
            public boolean shouldExecute() {
                return !isSitting() && super.shouldExecute();
            }

            @Override
            public boolean shouldContinueExecuting() {
                return !isSitting() && super.shouldContinueExecuting();
            }
        });

        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.1D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && !isSitting() && super.shouldExecute();
            }
        });

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !isFollowing() && !isSitting() && super.shouldExecute();
            }
        });

        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this) {
            @Override
            public boolean shouldExecute() {
                return !isSitting() && super.shouldExecute();
            }
        });

        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this) {
            @Override
            public boolean shouldExecute() {
                return !isSitting() && super.shouldExecute();
            }
        });

        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false) {
            @Override
            public boolean shouldExecute() {
                return !isSitting() && super.shouldExecute();
            }
        });

        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false,
                entity -> !isSitting() && entity instanceof IMob));
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

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.attackTimer > 0) {
            --this.attackTimer;
        }

        if (!this.world.isRemote && this.isFollowing() && !this.isSitting() && this.getAttackTarget() == null) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null && this.getDistanceSq(owner) > 144.0D) {
                this.teleportToOwner(owner);
            }
        }

        if (this.world.isRemote && (Math.abs(this.motionX) > 0.05D || Math.abs(this.motionZ) > 0.05D)) {
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0, 0.1D, 0);
        }
        if (this.world.isRemote && this.ticksExisted % 40 == 0 && !this.isSitting()) {
            this.playSound(SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, 0.1F, 0.5F);
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

        if (this.isPotionActive(MobEffects.POISON)) this.removePotionEffect(MobEffects.POISON);
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

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }
    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (target == null || !target.isEntityAlive() || !this.getEntitySenses().canSee(target)) {
            return;
        }
        this.world.setEntityState(this, (byte)4);

        double startX = this.posX;
        double startY = this.posY + 0.55D;
        double startZ = this.posZ;

        double endX = target.posX;
        double endY = target.posY + target.height * 0.45D;
        double endZ = target.posZ;

        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 0.0D) {
            dx /= distance;
            dy /= distance;
            dz /= distance;
        }

        if (this.world instanceof WorldServer) {
            WorldServer ws = (WorldServer) this.world;

            int particleCount = 20;
            double speed = 0.1D;

            for (int i = 0; i < particleCount; i++) {
                double motionX = dx * speed + (this.rand.nextDouble() - 0.5D) * 0.25D;
                double motionY = dy * speed + (this.rand.nextDouble() - 0.5D) * 0.25D;
                double motionZ = dz * speed + (this.rand.nextDouble() - 0.5D) * 0.25D;

                ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, startX, startY, startZ, 0, motionX, motionY, motionZ, 1.0D);
                ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, startX, startY, startZ, 0, motionX, motionY, motionZ, 1.0D);
            }

            ws.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, endX, endY, endZ, 1, 0.25D, 0.25D, 0.25D, 0.02D);
        }

        target.hurtResistantTime = 0;
        DamageSource source = DamageSource.causeMobDamage(this);

        float totalDamage = 4.0F + this.getRangedAttackBonus();
        target.attackEntityFrom(source, totalDamage);

        this.playSound(SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, 0.8F, 0.7F + this.rand.nextFloat() * 0.2F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    public boolean isPotionApplicable(PotionEffect pot) {
        return pot.getPotion() != MobEffects.POISON && super.isPotionApplicable(pot);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }

    @Override
    protected float getSoundPitch() {
        return 2.0F;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.BLOCK_ANVIL_STEP, 0.15F, 1.5F + this.rand.nextFloat() * 0.5F);
        this.playSound(SoundEvents.BLOCK_METAL_PLACE, 0.2F, 0.5F + this.rand.nextFloat() * 0.2F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
        } else {
            super.handleStatusUpdate(id);
        }
    }
}