package kiritekomai.agrianimal.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import kiritekomai.agrianimal.entity.EntityAgriAnimal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class EntityAIPutItemInChest extends EntityAIMoveToBlock {
	/** Villager that is harvesting */
	private final EntityAgriAnimal agriAnimal;
	private boolean isEntityInventoryFull;
	/** 0 => harvest, 1 => replant, -1 => none */
	private int currentTask;

	static final int findPathMaxLength = 16;

	public EntityAIPutItemInChest(EntityAgriAnimal farmerIn, double speedIn) {
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
			this.isEntityInventoryFull = isInventoryFull((IInventory) this.agriAnimal.getInventory());
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
				(double) (this.destinationBlock.getY() + 0.5D), (double) this.destinationBlock.getZ() + 0.5D, 10.0F,
				(float) this.agriAnimal.getVerticalFaceSpeed());
		if (this.getIsAboveDestination()) {
			World world = this.agriAnimal.world;
			IInventory iinventory = getInventoryAtPosition(world, this.destinationBlock);

			//iinventory.openInventory(null);
			this.transferItems((IInventory) this.agriAnimal.getInventory(), iinventory);
			//iinventory.closeInventory(null);

			this.currentTask = -1;
			this.runDelay = 10;
		}

	}

	/**
	 * Return true to set given position as destination
	 */
	protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
		if (!isEntityInventoryFull) {
			//Don't move while putting items or has inventry space
			return false;
		}
		PathFinder path_finder = new PathFinder(new WalkNodeProcessor());
		if (path_finder.findPath(this.agriAnimal.world, this.agriAnimal, pos, findPathMaxLength) == null) {
			//no path to the destination block
			return false;
		}
		IInventory iinventory = getInventoryAtPosition((World) worldIn, pos);
		if (iinventory == null) {
			//Don't move if block isn't chest
			return false;
		}

		if (!isInventoryFull(iinventory) && isInventoryFull((IInventory) this.agriAnimal.getInventory())) {
			this.currentTask = 1;
			return true;
		}
		return false;
	}

	@Nullable
	public static IInventory getInventoryAtPosition(World worldIn, BlockPos pos) {
		IInventory iinventory = null;
		BlockPos[] chkPosArray = { pos, pos.up().north(), pos.up().east(), pos.up().south(), pos.up().west() };

		for (BlockPos chkPos : chkPosArray) {

			IBlockState iblockstate = worldIn.getBlockState(chkPos);
			Block block = iblockstate.getBlock();
			if (iblockstate.hasTileEntity()) {
				TileEntity tileentity = worldIn.getTileEntity(chkPos);
				if (tileentity instanceof IInventory) {
					iinventory = (IInventory) tileentity;
					if (iinventory instanceof TileEntityChest && block instanceof BlockChest) {
						iinventory = ((BlockChest) block).getContainer(iblockstate, worldIn, chkPos, true);
					}
				}
			}

			if (iinventory == null) {
				List<Entity> list = ((World) worldIn).getEntitiesInAABBexcluding((Entity) null,
						new AxisAlignedBB(chkPos),
						EntitySelectors.HAS_INVENTORY);
				if (!list.isEmpty()) {
					iinventory = (IInventory) list.get(worldIn.rand.nextInt(list.size()));
				}
			}
			if (iinventory != null) {
				break;
			}
		}

		return iinventory;
	}

	/**
	 * Returns false if the inventory has any room to place items in
	 */
	private boolean isInventoryFull(IInventory inventoryIn) {
		int i = inventoryIn.getSizeInventory();

		for (int j = 0; j < i; ++j) {
			ItemStack itemstack = inventoryIn.getStackInSlot(j);
			if (itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private void transferItems(IInventory iinventoryFrom, IInventory iinventoryTo) {
		//if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(this)) return;
		if (iinventoryFrom == null || iinventoryTo == null) {
			return;
		}
		if (this.isInventoryFull(iinventoryTo)) {
			return;
		}
		for (int i = 0; i < iinventoryFrom.getSizeInventory(); ++i) {
			if (!iinventoryFrom.getStackInSlot(i).isEmpty()) {
				ItemStack itemstack = iinventoryFrom.getStackInSlot(i).copy();
				Item item = itemstack.getItem();
				boolean needPut = false;
				if (!this.agriAnimal.isFarmableItem(item)) {
					needPut = true;
				} else {
					for (int j = 0; j < i; j++) {
						if (item == iinventoryFrom.getStackInSlot(j).getItem()) {
							needPut = true;
							break;
						}
					}
				}
				if (needPut) {
					ItemStack itemstack1 = putStackInInventoryAllSlots(iinventoryFrom, iinventoryTo, itemstack);
					iinventoryFrom.setInventorySlotContents(i, itemstack1);
					if (itemstack1.isEmpty()) {
						iinventoryFrom.markDirty();
					}
				}
			}
		}

	}

	/**
	 * Attempts to place the passed stack in the inventory, using as many slots as required. Returns leftover items
	 */
	public static ItemStack putStackInInventoryAllSlots(@Nullable IInventory source, IInventory destination,
			ItemStack stack) {
		int i = destination.getSizeInventory();

		for (int j = 0; j < i && !stack.isEmpty(); ++j) {
			stack = insertStack(source, destination, stack, j);
		}

		return stack;
	}

	/**
	 * Insert the specified stack to the specified inventory and return any leftover items
	 */
	private static ItemStack insertStack(@Nullable IInventory source, IInventory destination, ItemStack stack,
			int index) {
		ItemStack itemstack = destination.getStackInSlot(index);
		if (canInsertItemInSlot(destination, stack, index)) {
			boolean flag = false;
			if (itemstack.isEmpty()) {
				destination.setInventorySlotContents(index, stack);
				stack = ItemStack.EMPTY;
				flag = true;
			} else if (canCombine(itemstack, stack)) {
				int i = stack.getMaxStackSize() - itemstack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.shrink(j);
				itemstack.grow(j);
				flag = j > 0;
			}

			if (flag) {
				destination.markDirty();
			}
		}

		return stack;
	}

	/**
	 * Can this hopper insert the specified item from the specified slot on the specified side?
	 */
	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index) {
		if (!inventoryIn.isItemValidForSlot(index, stack)) {
			return false;
		}
		return true;
	}

	private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
		if (stack1.getItem() != stack2.getItem()) {
			return false;
		} else if (stack1.getDamage() != stack2.getDamage()) {
			return false;
		} else if (stack1.getCount() > stack1.getMaxStackSize()) {
			return false;
		} else {
			return ItemStack.areItemStackTagsEqual(stack1, stack2);
		}
	}
}
