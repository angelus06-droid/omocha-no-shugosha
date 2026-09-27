package uut.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import uut.entity.ai.EntityAIBalloonFollowOwner;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import uut.item.ModItems;

import javax.annotation.Nullable;
import java.util.List;

public class EntityToyAirBalloon extends BaseDefensiveMob {

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyAirBalloon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyAirBalloon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyAirBalloon.class, DataSerializers.VARINT);
    private int healTimer;

    public EntityToyAirBalloon(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 1.05f);
        this.moveHelper = new EntityFlyHelper(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getDyeDamage());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(2, new EntityAISit(this));

        this.tasks.addTask(3, new EntityAIBalloonFollowOwner(this, 1.2D, 5.0F, 2.0F) {
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

        this.tasks.addTask(6, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.6D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0);
    }

    @Override
    protected net.minecraft.pathfinding.PathNavigate createNavigator(World worldIn) {
        return new PathNavigateFlying(this, worldIn);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.setNoGravity(true);

        if (!this.world.isRemote) {
            if (this.isPotionActive(MobEffects.POISON)) {
                this.removePotionEffect(MobEffects.POISON);
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

            EntityLivingBase owner = this.getOwner();
            if (owner != null && this.isFollowing() && !this.isSitting()) {
                double targetY = owner.posY + owner.getEyeHeight() - 0.5D;
                if (this.posY > targetY) {
                    double distanceY = this.posY - targetY;
                    this.motionY -= Math.min(distanceY * 0.01D, 0.05D);
                }

                if (this.getDistanceSq(owner) > 144.0D) {
                    this.teleportToOwner(owner);
                }
            }

            this.motionY += Math.sin(this.ticksExisted * 0.1) * 0.005;
        }

        this.AbsorbWithParticles();
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isServerWorld() || this.canPassengerSteer()) {
            this.moveRelative(strafe, vertical, forward, (float)this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue() * 0.1F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8F;
            this.motionY *= 0.8F;
            this.motionZ *= 0.8F;
        } else {
            super.travel(strafe, vertical, forward);
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
                this.isSafeToStandAt(pos) && this.isSafeToStandAt(pos.up());
    }

    private boolean isSafeToStandAt(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return !state.getMaterial().isSolid() && !state.getMaterial().isLiquid();
    }

    private void AbsorbWithParticles() {
        AxisAlignedBB boundingBox = this.getEntityBoundingBox().grow(8.0, 4.0, 8.0);
        List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, boundingBox);
        List<EntityXPOrb> orbs = this.world.getEntitiesWithinAABB(EntityXPOrb.class, boundingBox);

        double speed = 0.07D;
        for (EntityItem item : items) {
            pullEntity(item, speed);
            spawnMagnetParticles(item);
        }
        for (EntityXPOrb orb : orbs) {
            pullEntity(orb, speed);
        }
    }

    private void pullEntity(Entity target, double speed) {
        double dX = this.posX - target.posX;
        double dY = (this.posY + 0.4) - target.posY;
        double dZ = this.posZ - target.posZ;
        double dist = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);

        if (dist > 0.5D) {
            target.motionX += (dX / dist) * speed;
            target.motionY += (dY / dist) * speed;
            target.motionZ += (dZ / dist) * speed;
            target.motionX *= 0.9D;
            target.motionY *= 0.9D;
            target.motionZ *= 0.9D;
        }
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

    private void spawnMagnetParticles(Entity target) {
        if (this.world.isRemote && this.rand.nextInt(3) == 0) {
            this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, target.posX, target.posY + 0.5, target.posZ, 0, 0, 0);
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect pot) {
        return pot.getPotion() != MobEffects.POISON && super.isPotionApplicable(pot);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL || source == DamageSource.FALL) return false;
        return super.attackEntityFrom(source, amount);
    }

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) {
        this.dataManager.set(SITTING, sitting);
        if (this.aiSit != null) this.aiSit.setSitting(sitting);
    }

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

    @Override public void fall(float distance, float damageMultiplier) {}
    @Nullable protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.BLOCK_GLASS_HIT; }
    @Nullable protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_SNOW_BREAK; }
}