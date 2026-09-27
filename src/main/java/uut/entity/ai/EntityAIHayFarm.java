package uut.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemShears;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import uut.entity.EntityToyHayFarmer;

public class EntityAIHayFarm extends EntityAIMoveToBlock {
    private final EntityToyHayFarmer farmer;
    private boolean chesttimer = false;

    public EntityAIHayFarm(EntityToyHayFarmer gnomeIn, double speedIn) {
        super(gnomeIn, speedIn, 16);
        this.farmer = gnomeIn;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.farmer.isTamed()) return false;

        ItemStack held = this.farmer.getHeldItemMainhand();
        boolean hasTools = held.getItem() instanceof ItemShears || isBoneMeal(held);

        if (!hasTools) return false;
        return super.shouldExecute();
    }

    private boolean isBoneMeal(ItemStack stack) {
        return stack.getItem() == Items.DYE && stack.getMetadata() == EnumDyeColor.WHITE.getDyeDamage();
    }

    @Override
    public void updateTask() {
        super.updateTask();

        this.farmer.getLookHelper().setLookPosition(this.destinationBlock.getX() + 0.5D,
                this.destinationBlock.getY() + 1, this.destinationBlock.getZ() + 0.5D, 10.0F,
                this.farmer.getVerticalFaceSpeed());

        if (this.getIsAboveDestination()) {
            World world = this.farmer.world;
            BlockPos cropPos = this.destinationBlock.up();
            IBlockState state = world.getBlockState(cropPos);
            ItemStack held = this.farmer.getHeldItemMainhand();

            if (state.getBlock() instanceof IGrowable) {
                IGrowable growable = (IGrowable) state.getBlock();

                BlockPos topPos = cropPos.up();
                IBlockState topState = world.getBlockState(topPos);
                boolean isDoubleTall = topState.getBlock() instanceof IGrowable;

                if (isDoubleTall) {
                    IGrowable topGrowable = (IGrowable) topState.getBlock();
                    boolean bottomMax = !growable.canGrow(world, cropPos, state, world.isRemote);
                    boolean topMax = !topGrowable.canGrow(world, topPos, topState, world.isRemote);

                    if (bottomMax && topMax) {
                        if (held.getItem() instanceof ItemShears) {
                            harvestDoubleCrop(world, topPos, topState);
                        } else if (isBoneMeal(held)) {
                            applyBoneMeal(world, topPos, topState, topGrowable, held);
                        }
                    } else if (isBoneMeal(held)) {
                        if (!bottomMax) {
                            applyBoneMeal(world, cropPos, state, growable, held);
                        } else if (!topMax) {
                            applyBoneMeal(world, topPos, topState, topGrowable, held);
                        }
                    }
                } else {
                    boolean isMaxAge = !growable.canGrow(world, cropPos, state, world.isRemote);

                    if (isMaxAge && held.getItem() instanceof ItemShears) {
                        harvestSingleCrop(world, cropPos, state);
                    } else if (!isMaxAge && isBoneMeal(held)) {
                        applyBoneMeal(world, cropPos, state, growable, held);
                    }
                }
            } else if (world.getTileEntity(this.destinationBlock) != null) {
                this.farmer.swingArm(EnumHand.MAIN_HAND);
                this.emptychests();
                this.chesttimer = false;
                this.runDelay = 2;
            }
        }
    }

    private void harvestSingleCrop(World world, BlockPos pos, IBlockState state) {
        this.farmer.swingArm(EnumHand.MAIN_HAND);
        world.playEvent(2001, pos, Block.getStateId(state));

        if (state.getBlock() instanceof BlockCrops) {
            BlockCrops crop = (BlockCrops) state.getBlock();
            world.destroyBlock(pos, true);
            world.setBlockState(pos, crop.withAge(0));
        } else {
            world.destroyBlock(pos, true);
        }

        this.farmer.getHeldItemMainhand().damageItem(1, this.farmer);
        this.chesttimer = this.farmer.getRNG().nextFloat() < 0.30F;
        this.runDelay = 1;
    }

    private void harvestDoubleCrop(World world, BlockPos topPos, IBlockState topState) {
        this.farmer.swingArm(EnumHand.MAIN_HAND);
        world.playEvent(2001, topPos, Block.getStateId(topState));

        world.destroyBlock(topPos, true);

        this.farmer.getHeldItemMainhand().damageItem(1, this.farmer);
        this.chesttimer = this.farmer.getRNG().nextFloat() < 0.30F;
        this.runDelay = 1;
    }

    private void applyBoneMeal(World world, BlockPos pos, IBlockState state, IGrowable growable, ItemStack stack) {
        if (growable.canGrow(world, pos, state, world.isRemote)) {
            this.farmer.swingArm(EnumHand.MAIN_HAND);
            if (!world.isRemote) {
                growable.grow(world, world.rand, pos, state);
                stack.shrink(1);
                world.playEvent(2005, pos, 0);
            }
            this.runDelay = 30;
        }
    }

    private boolean isFarmland(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (block.isFertile(world, pos)) {
            return true;
        }
        if (block == Blocks.FARMLAND) {
            return true;
        }
        ResourceLocation regName = block.getRegistryName();
        if (regName != null && "biomesoplenty".equals(regName.getNamespace())) {
            return regName.getPath().contains("farmland");
        }
        return false;
    }

    @Override
    protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
        if (this.farmer.getDistanceSqToCenter(pos) > 256.0D) {
            return false;
        }

        if ((this.chesttimer || farmer.isInventoryFull()) && farmer.hasItemsInInventory()) {
            BlockPos boundChest = farmer.getBoundChestPos();
            if (boundChest != null) {
                return pos.equals(boundChest) && worldIn.getTileEntity(pos) != null;
            }
            return false;
        }

        IBlockState soilState = worldIn.getBlockState(pos);

        if (isFarmland(worldIn, pos, soilState)) {
            BlockPos cropPos = pos.up();
            IBlockState stateAbove = worldIn.getBlockState(cropPos);

            if (stateAbove.getBlock() instanceof IGrowable) {
                IGrowable growable = (IGrowable) stateAbove.getBlock();
                ItemStack held = farmer.getHeldItemMainhand();

                BlockPos topPos = cropPos.up();
                IBlockState topState = worldIn.getBlockState(topPos);
                boolean isDoubleTall = topState.getBlock() instanceof IGrowable;

                if (isDoubleTall) {
                    IGrowable topGrowable = (IGrowable) topState.getBlock();
                    boolean bottomMax = !growable.canGrow(worldIn, cropPos, stateAbove, worldIn.isRemote);
                    boolean topMax = !topGrowable.canGrow(worldIn, topPos, topState, worldIn.isRemote);

                    if (held.getItem() instanceof ItemShears && bottomMax && topMax) {
                        return true;
                    }
                    if (isBoneMeal(held) && (!bottomMax || !topMax)) {
                        return true;
                    }
                } else {
                    boolean isMaxAge = !growable.canGrow(worldIn, cropPos, stateAbove, worldIn.isRemote);

                    if (held.getItem() instanceof ItemShears && isMaxAge) {
                        return true;
                    }
                    if (isBoneMeal(held) && !isMaxAge) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void emptychests() {
        net.minecraft.tileentity.TileEntity te = this.farmer.world.getTileEntity(this.destinationBlock);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler destInv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

            for (int i = 0; i < farmer.farmerInventory.getSizeInventory(); i++) {
                ItemStack stack = farmer.farmerInventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(destInv, stack, false);
                    farmer.farmerInventory.setInventorySlotContents(i, remainder);
                }
            }
            this.farmer.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, 1.0F);
        }
    }
}