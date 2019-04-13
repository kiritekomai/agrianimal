package kiritekomai.agrianimal.entity.ai;

import kiritekomai.agrianimal.entity.EntityAgriAnimal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAttachedStem;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockStemGrown;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIMyHarvestFarmland extends EntityAIMoveToBlock {
	/** Villager that is harvesting */
	private final EntityAgriAnimal agriAnimal;
	private boolean hasFarmItem;
	/** 0 => harvest, 1 => replant, -1 => none */
	private int currentTask;

	static final int findPathMaxLength = 16;

	public EntityAIMyHarvestFarmland(EntityAgriAnimal farmerIn, double speedIn) {
		super(farmerIn, speedIn, findPathMaxLength);
		this.agriAnimal = farmerIn;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (!this.agriAnimal.isHarvesting()) {
			return false;
		}
		if (this.runDelay <= 0) {
			if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.agriAnimal.world,
					this.agriAnimal)) {
				return false;
			}

			this.currentTask = -1;
			this.hasFarmItem = this.agriAnimal.isFarmItemInInventory();
		}

		return super.shouldExecute();
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return this.currentTask >= 0 && super.shouldContinueExecuting();
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		super.tick();
		this.agriAnimal.getLookHelper().setLookPosition((double) this.destinationBlock.getX() + 0.5D,
				(double) (this.destinationBlock.getY() + 1), (double) this.destinationBlock.getZ() + 0.5D, 10.0F,
				(float) this.agriAnimal.getVerticalFaceSpeed());
		if (this.getIsAboveDestination()) {
			IWorld iworld = this.agriAnimal.world;
			BlockPos blockpos = this.destinationBlock.up();
			IBlockState iblockstate = iworld.getBlockState(blockpos);
			Block block = iblockstate.getBlock();
			if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(iblockstate)) {
				iworld.destroyBlock(blockpos, true);
			} else if (this.currentTask == 0 && getStemGlownBlockPos(iworld, blockpos) != null) {
				iworld.destroyBlock(getStemGlownBlockPos(iworld, blockpos), true);
			} else if (this.currentTask == 0 && getBlockReedPos(iworld, blockpos) != null) {
				iworld.destroyBlock(getBlockReedPos(iworld, blockpos), true);
			} else if (this.currentTask == 1 && iblockstate.isAir()) {
				InventoryBasic inventorybasic = this.agriAnimal.getInventory();

				for (int i = 0; i < inventorybasic.getSizeInventory(); ++i) {
					ItemStack itemstack = inventorybasic.getStackInSlot(i);
					boolean flag = false;
					if (!itemstack.isEmpty()) {
						if (itemstack.getItem() == Items.WHEAT_SEEDS) {
							iworld.setBlockState(blockpos, Blocks.WHEAT.getDefaultState(), 3);
							flag = true;
						} else if (itemstack.getItem() == Items.POTATO) {
							iworld.setBlockState(blockpos, Blocks.POTATOES.getDefaultState(), 3);
							flag = true;
						} else if (itemstack.getItem() == Items.CARROT) {
							iworld.setBlockState(blockpos, Blocks.CARROTS.getDefaultState(), 3);
							flag = true;
						} else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
							iworld.setBlockState(blockpos, Blocks.BEETROOTS.getDefaultState(), 3);
							flag = true;
						} else if (itemstack.getItem() instanceof net.minecraftforge.common.IPlantable) {
							if (((net.minecraftforge.common.IPlantable) itemstack.getItem()).getPlantType(iworld,
									blockpos) == net.minecraftforge.common.EnumPlantType.Crop) {
								iworld.setBlockState(blockpos,
										((net.minecraftforge.common.IPlantable) itemstack.getItem()).getPlant(iworld,
												blockpos),
										3);
								flag = true;
							}
						}
					}

					if (flag) {
						itemstack.shrink(1);
						if (itemstack.isEmpty()) {
							inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
						}
						break;
					}
				}
			}

			this.currentTask = -1;
			this.runDelay = 10;
		}

	}

	/**
	 * Return true to set given position as destination
	 */
	protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		pos = pos.up();

		int task = -1;
		boolean shouldGo = false;

		if (block == Blocks.FARMLAND) {
			IBlockState iblockstate = worldIn.getBlockState(pos);
			block = iblockstate.getBlock();
			if (block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(iblockstate)
					&& (this.currentTask == 0 || this.currentTask < 0)) {
				task = 0;
				shouldGo = true;
			}

			if (!shouldGo && iblockstate.isAir() && this.hasFarmItem
					&& (this.currentTask == 1 || this.currentTask < 0)) {
				task = 1;
				shouldGo = true;
			}
		}
		if (!shouldGo && (getStemGlownBlockPos(worldIn, pos) != null)
				&& (this.currentTask == 0 || this.currentTask < 0)) {
			task = 0;
			shouldGo = true;

		}
		if (!shouldGo && (getBlockReedPos(worldIn, pos) != null)
				&& (this.currentTask == 0 || this.currentTask < 0)) {
			task = 0;
			shouldGo = true;

		}
		PathFinder path_finder = new PathFinder(new WalkNodeProcessor());

		if (shouldGo) {
			if (path_finder.findPath(this.agriAnimal.world, this.agriAnimal, pos, findPathMaxLength) != null) {
				//exits path to the destination block
				this.currentTask = task;
				return true;
			}
		}

		return false;
	}

	protected BlockPos getStemGlownBlockPos(IWorldReaderBase worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (block instanceof BlockAttachedStem) {
			BlockPos facing_pos = pos.offset(iblockstate.get(BlockHorizontal.HORIZONTAL_FACING));
			Block facing_block = worldIn.getBlockState(facing_pos).getBlock();
			if (facing_block instanceof BlockStemGrown) {
				return facing_pos;
			}
		}
		return null;
	}

	protected BlockPos getBlockReedPos(IWorldReaderBase worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();

		if (block instanceof BlockReed) {
			BlockPos up_pos = pos.up();
			Block up_block = worldIn.getBlockState(up_pos).getBlock();
			if (up_block instanceof BlockReed) {
				return up_pos;
			}
		}
		return null;
	}

}