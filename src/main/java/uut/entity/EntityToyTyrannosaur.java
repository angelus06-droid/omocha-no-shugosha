package uut.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.entity.monster.IMob;
import uut.item.ModItems;
import uut.util.ModSounds;

import javax.annotation.Nullable;

public class EntityToyTyrannosaur extends BaseDefensiveMob {

    private int attackTimer;
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyTyrannosaur.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyTyrannosaur.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyTyrannosaur.class, DataSerializers.VARINT);

    private int healTimer;

    public EntityToyTyrannosaur(World worldIn) {
        super(worldIn);
        this.setSize(0.7f, 0.7f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.GREEN.getDyeDamage());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4f));
        this.tasks.addTask(3, new AIAttackFast(this, 1.2D, false));

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

        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
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

        if (this.attackTimer > 0) --this.attackTimer;

        if (!this.world.isRemote && this.isFollowing() && this.getAttackTarget() == null) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null && this.getDistanceSq(owner) > 144.0D) {
                this.teleportToOwner(owner);
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
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
    }

    class AIAttackFast extends EntityAIAttackMelee {
        public AIAttackFast(EntityCreature creature, double speedIn, boolean useLongMemory) {
            super(creature, speedIn, useLongMemory);
        }

        @Override
        protected double getAttackReachSqr(EntityLivingBase attackTarget) {
            return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
        }

        @Override
        protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.attackTick <= 0) {
                this.attackTick = 5;
                this.attacker.swingArm(EnumHand.MAIN_HAND);
                this.attacker.attackEntityAsMob(enemy);
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 5;
        this.world.setEntityState(this, (byte) 4);

        float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        if (flag) {
            if (entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 2));

                if (this.rand.nextFloat() <= 0.50F) {
                    float healAmount = 5.0f;
                    this.heal(healAmount);

                    if (!this.world.isRemote) {
                        ((net.minecraft.world.WorldServer)this.world).spawnParticle(
                                EnumParticleTypes.HEART,
                                this.posX, this.posY + this.height + 0.5D, this.posZ,
                                5, 0.3D, 0.3D, 0.3D, 0.02D
                        );
                    }
                    this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5f, 1.5f);
                }
            }

            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0f, 1.2f);
        }
        return flag;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) this.attackTimer = 5;
        else super.handleStatusUpdate(id);
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

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() { return ModSounds.tyrannosaur_say; }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.25F, 2.0F);
    }

    @Nullable @Override
    protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.tyrannosaur_hurt; }

    @Nullable @Override
    protected SoundEvent getDeathSound() { return ModSounds.tyrannosaur_death; }
}