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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.item.ModItems;
import uut.util.ModSounds;

public class EntityToyKitsune extends BaseDefensiveMob {

    private int attackTimer;
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyKitsune.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyKitsune.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(EntityToyKitsune.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyKitsune.class, DataSerializers.VARINT);

    private int healTimer;

    public EntityToyKitsune(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 0.85f);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(TAMED, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getDyeDamage());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4f));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.1D, false));

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
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> {if (entity == null || isSitting() || !entity.isEntityAlive()) return false;return entity instanceof IMob || entity instanceof net.minecraft.entity.monster.EntityShulker;}
        ));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source != DamageSource.OUT_OF_WORLD && !this.world.isRemote) {
            if (this.rand.nextFloat() < 0.40F) {
                this.teleportRandomly();
                this.heal(2.0F);
                return false;
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if (potioneffectIn.getPotion() == MobEffects.LEVITATION || potioneffectIn.getPotion() == MobEffects.WITHER) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
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
                    this.heal(2.0F);
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

    private void teleportRandomly() {
        double oldX = this.posX;
        double oldY = this.posY;
        double oldZ = this.posZ;

        for (int i = 0; i < 32; ++i) {
            int offsetX = this.rand.nextInt(17) - 8;
            int offsetY = this.rand.nextInt(7) - 3;
            int offsetZ = this.rand.nextInt(17) - 8;

            double targetX = this.posX + offsetX;
            double targetY = this.posY + offsetY;
            double targetZ = this.posZ + offsetZ;

            Vec3d startVec = new Vec3d(this.posX, this.posY + (this.height / 2.0F), this.posZ);
            Vec3d endVec = new Vec3d(targetX, targetY + (this.height / 2.0F), targetZ);

            RayTraceResult rayTrace = this.world.rayTraceBlocks(startVec, endVec, false, true, false);

            if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos hitPos = rayTrace.getBlockPos();
                EnumFacing sideHit = rayTrace.sideHit;

                targetX = rayTrace.hitVec.x + sideHit.getXOffset() * 0.5D;
                targetY = rayTrace.hitVec.y + sideHit.getYOffset() * 0.5D;
                targetZ = rayTrace.hitVec.z + sideHit.getZOffset() * 0.5D;
            }

            BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);

            IBlockState stateCurrent = this.world.getBlockState(targetPos);
            if (stateCurrent.getMaterial().isSolid()) {
                int maxCheckUp = 3;
                while (maxCheckUp > 0 && this.world.getBlockState(targetPos).getMaterial().isSolid()) {
                    targetPos = targetPos.up();
                    maxCheckUp--;
                }
            }

            if (!this.world.getBlockState(targetPos.down()).isSideSolid(this.world, targetPos.down(), EnumFacing.UP)) {
                int maxCheckDown = 4;
                while (maxCheckDown > 0 && !this.world.getBlockState(targetPos.down()).isSideSolid(this.world, targetPos.down(), EnumFacing.UP)) {
                    targetPos = targetPos.down();
                    maxCheckDown--;
                }
            }

            if (this.isValidTeleportPos(targetPos)) {
                double newX = targetPos.getX() + 0.5D;
                double newY = targetPos.getY();
                double newZ = targetPos.getZ() + 0.5D;

                spawnSmokeExplosion(oldX, oldY, oldZ);
                spawnTeleportLine(oldX, oldY + this.height * 0.5D, oldZ, newX, newY + this.height * 0.5D, newZ);

                this.setLocationAndAngles(newX, newY, newZ, this.rotationYaw, this.rotationPitch);

                spawnSmokeExplosion(newX, newY, newZ);
                this.world.playSound(null, newX, newY, newZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1F, 1F);
                break;
            }
        }
    }

    private void spawnSmokeExplosion(double x, double y, double z) {
        if (!(world instanceof WorldServer)) return;
        WorldServer ws = (WorldServer) world;

        ws.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y + 0.4D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        ws.spawnParticle(EnumParticleTypes.CLOUD, x, y + 0.4D, z, 4, 0.3D, 0.2D, 0.3D, 0.02D);
    }

    private void spawnTeleportLine(double x1, double y1, double z1, double x2, double y2, double z2) {
        if (!(world instanceof WorldServer)) return;
        WorldServer ws = (WorldServer) world;

        Vec3d start = new Vec3d(x1, y1, z1);
        Vec3d end = new Vec3d(x2, y2, z2);

        double distance = start.distanceTo(end);
        int totalParticles = (int)(distance * 4);

        boolean isBlue = this.isBlueVariant();

        EnumParticleTypes particleType = isBlue ? EnumParticleTypes.SPELL_MOB : EnumParticleTypes.REDSTONE;

        double red = isBlue ? 0.0D : 1.0D;
        double green = 0.0D;
        double blue = isBlue ? 1.0D : 0.0D;

        for (int i = 0; i < totalParticles; i++) {
            double progress = (double)i / totalParticles;

            double x = start.x + (end.x - start.x) * progress;
            double y = start.y + (end.y - start.y) * progress;
            double z = start.z + (end.z - start.z) * progress;

            ws.spawnParticle(particleType, x, y, z, 0, red, green, blue, 1.0D);
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

    private void renderWalkParticles() {
        BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY - 0.2D), MathHelper.floor(this.posZ));
        IBlockState state = this.world.getBlockState(pos);
        if (state.getMaterial() != Material.AIR) {
            this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (this.rand.nextFloat() - 0.5D) * this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, 4.0D * (this.rand.nextFloat() - 0.5D), 0.5D, (this.rand.nextFloat() - 0.5D) * 4.0D, Block.getStateId(state));
        }
    }

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }

    public boolean isTamed() {
        return this.dataManager.get(TAMED);
    }
    public void setTamed(boolean tamed) { this.dataManager.set(TAMED, tamed); }

    public boolean isBlueVariant() {
        String name = this.hasCustomName() ? this.getCustomNameTag() : "";
        return "blue".equalsIgnoreCase(name) || "blue kitsune".equalsIgnoreCase(name);
    }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("following", this.isFollowing());
        compound.setBoolean("sitting", this.isSitting());
        compound.setBoolean("tamed", this.isTamed());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        this.setTamed(compound.getBoolean("tamed"));
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.5D);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);

        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        if(flag){
            if(this.rand.nextFloat() < 0.4F){
                teleportRandomly();
                this.heal(2.0F);
            }
        }

        return flag;
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
    protected void playStepSound(BlockPos pos, Block blockIn) { this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.35f, 2.0f); }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.kitsune_say;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) { return ModSounds.kitsune_hurt; }

    @Override
    protected SoundEvent getDeathSound() { return ModSounds.kitsune_death; }
}