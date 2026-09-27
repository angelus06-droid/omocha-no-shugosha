package uut.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import uut.entity.BaseDefensiveMob;
import uut.entity.BaseTurretMob;
import uut.tabs.CreativeTabsOmochaToys;

import java.util.List;
import javax.annotation.Nullable;

public class ItemToyCapsule extends Item {

    public static final String REG_NAME = "toy_capsule";

    public ItemToyCapsule(final String unlocalizedName) {
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("MobData");
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("MobData");
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("MobData")) {
            NBTTagCompound mobData = stack.getTagCompound().getCompoundTag("MobData");
            float health = mobData.getFloat("Health");
            float maxHealth = mobData.getFloat("MaxHealth");
            if (maxHealth > 0) {
                return 1.0D - (double) (health / maxHealth);
            }
        }
        return 0.0D;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack ignoredStack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (playerIn.getCooldownTracker().hasCooldown(this)) {
            return false;
        }

        if (!playerIn.world.isRemote) {
            boolean isDefensive = target instanceof BaseDefensiveMob;
            boolean isTurret = target instanceof BaseTurretMob;

            if (isDefensive || isTurret) {
                EntityLiving mob = (EntityLiving) target;
                ItemStack stack = playerIn.getHeldItem(hand);

                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("MobData")) {
                    playerIn.sendMessage(new TextComponentTranslation("chat.toy_capsule.occupied"));
                    return true;
                }

                java.util.UUID ownerId = null;
                boolean isTamed = false;

                if (isDefensive) {
                    isTamed = ((BaseDefensiveMob) mob).isTamed();
                    ownerId = ((BaseDefensiveMob) mob).getOwnerId();
                } else {
                    isTamed = ((BaseTurretMob) mob).isTamed();
                    ownerId = ((BaseTurretMob) mob).getOwnerId();
                }

                if (isTamed && ownerId != null) {
                    if (!ownerId.equals(playerIn.getUniqueID())) {
                        return true;
                    }
                }

                ResourceLocation entityId = EntityList.getKey(mob);
                if (entityId == null) {
                    playerIn.sendMessage(new TextComponentTranslation("chat.toy_capsule.not_registered"));
                    return true;
                }

                NBTTagCompound itemTag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
                NBTTagCompound mobData = new NBTTagCompound();

                String translationKey = "entity." + EntityList.getTranslationName(entityId) + ".name";

                mobData.setString("ClassID", entityId.toString());
                mobData.setString("TranslationKey", translationKey);
                mobData.setString("CustomName", mob.getCustomNameTag());
                mobData.setString("OwnerUUID", playerIn.getUniqueID().toString());
                mobData.setString("OwnerName", playerIn.getName());
                mobData.setFloat("Health", mob.getHealth());
                mobData.setFloat("MaxHealth", mob.getMaxHealth());

                NBTTagCompound entityTag = new NBTTagCompound();
                mob.writeToNBT(entityTag);
                mobData.setTag("EntityTag", entityTag);

                itemTag.setTag("MobData", mobData);
                stack.setTagCompound(itemTag);

                if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP) {
                    ((net.minecraft.entity.player.EntityPlayerMP) playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                }

                World world = playerIn.world;
                world.playSound(null, mob.posX, mob.posY, mob.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.5F);
                world.playSound(null, mob.posX, mob.posY, mob.posZ, SoundEvents.BLOCK_NOTE_BELL, SoundCategory.PLAYERS, 0.5F, 1.2F);

                if (world instanceof net.minecraft.world.WorldServer) {
                    net.minecraft.world.WorldServer ws = (net.minecraft.world.WorldServer) world;
                    ws.spawnParticle(EnumParticleTypes.PORTAL, mob.posX, mob.posY + 1.0D, mob.posZ, 50, 0.3D, 0.5D, 0.3D, 0.5D);
                    ws.spawnParticle(EnumParticleTypes.SPELL_WITCH, mob.posX, mob.posY + 1.0D, mob.posZ, 30, 0.2D, 0.4D, 0.2D, 0.1D);
                }

                mob.setDead();

                playerIn.getCooldownTracker().setCooldown(this, 40);
                playerIn.swingArm(hand);

                return true;
            }
        }
        return false;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.getCooldownTracker().hasCooldown(this)) {
            return EnumActionResult.FAIL;
        }

        ItemStack stack = player.getHeldItem(hand);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("MobData")) {
            if (!worldIn.isRemote) {
                NBTTagCompound mobData = stack.getTagCompound().getCompoundTag("MobData");
                String ownerUUID = mobData.getString("OwnerUUID");

                if (!ownerUUID.equals(player.getUniqueID().toString())) {
                    return EnumActionResult.FAIL;
                }

                String classIdStr = mobData.getString("ClassID");
                ResourceLocation res = new ResourceLocation(classIdStr);
                net.minecraft.entity.Entity entity = EntityList.createEntityByIDFromName(res, worldIn);

                if (entity instanceof BaseDefensiveMob || entity instanceof BaseTurretMob) {
                    EntityLiving mob = (EntityLiving) entity;

                    if (mobData.hasKey("EntityTag")) {
                        mob.readFromNBT(mobData.getCompoundTag("EntityTag"));
                    }

                    mob.setCustomNameTag(mobData.getString("CustomName"));

                    if (mob instanceof BaseDefensiveMob) {
                        ((BaseDefensiveMob) mob).setTamed(true);
                        ((BaseDefensiveMob) mob).setOwnerId(player.getUniqueID());
                    } else if (mob instanceof BaseTurretMob) {
                        ((BaseTurretMob) mob).setTamed(true);
                        ((BaseTurretMob) mob).setOwnerId(player.getUniqueID());
                    }

                    mob.setHealth(mobData.getFloat("Health"));

                    BlockPos spawnPos = pos.offset(facing);
                    mob.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, player.rotationYaw, 0.0F);

                    worldIn.spawnEntity(mob);

                    worldIn.playSound(null, mob.posX, mob.posY, mob.posZ, SoundEvents.ENTITY_FIREWORK_TWINKLE, SoundCategory.PLAYERS, 1.0F, 1.2F);
                    worldIn.playSound(null, mob.posX, mob.posY, mob.posZ, SoundEvents.BLOCK_NOTE_XYLOPHONE, SoundCategory.PLAYERS, 0.8F, 1.2F);

                    if (worldIn instanceof net.minecraft.world.WorldServer) {
                        net.minecraft.world.WorldServer ws = (net.minecraft.world.WorldServer) worldIn;
                        ws.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, mob.posX, mob.posY + 0.5D, mob.posZ, 40, 0.4D, 0.5D, 0.4D, 0.1D);
                        ws.spawnParticle(EnumParticleTypes.TOTEM, mob.posX, mob.posY + 0.5D, mob.posZ, 20, 0.3D, 0.3D, 0.3D, 0.2D);
                    }

                    stack.getTagCompound().removeTag("MobData");
                    if (stack.getTagCompound().isEmpty()) {
                        stack.setTagCompound(null);
                    }
                } else {
                    player.sendMessage(new TextComponentTranslation("chat.toy_capsule.recreate_error"));
                    return EnumActionResult.FAIL;
                }
            }

            player.getCooldownTracker().setCooldown(this, 40);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("MobData")) {
            NBTTagCompound mobData = stack.getTagCompound().getCompoundTag("MobData");

            String translationKey = mobData.getString("TranslationKey");
            String typeName = net.minecraft.client.resources.I18n.format(translationKey);

            String customName = mobData.getString("CustomName");
            String ownerName = mobData.getString("OwnerName");
            float health = mobData.getFloat("Health");
            float maxHealth = mobData.getFloat("MaxHealth");

            tooltip.add(net.minecraft.client.resources.I18n.format("tooltip.toy_capsule.toy", typeName));
            if (customName != null && !customName.isEmpty()) {
                tooltip.add(net.minecraft.client.resources.I18n.format("tooltip.toy_capsule.nickname", customName));
            }
            tooltip.add(net.minecraft.client.resources.I18n.format("tooltip.toy_capsule.owner", ownerName));
            tooltip.add(net.minecraft.client.resources.I18n.format("tooltip.toy_capsule.health", health, maxHealth));
            tooltip.add(net.minecraft.client.resources.I18n.format("tooltip.toy_capsule.active"));
        } else {
            tooltip.add(net.minecraft.client.resources.I18n.format("tooltip.toy_capsule.empty"));
        }
    }
}