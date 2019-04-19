package kiritekomai.agrianimal.entity.ai;

import javax.annotation.Nullable;

import kiritekomai.agrianimal.entity.EntityAgriAnimal;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class EntityAIPickUpItem extends EntityAIBase {
	protected int runDelay;
	protected int timeoutCounter;
	private int maxStayTicks;
	/** Villager that is harvesting */
	private final EntityAgriAnimal agriAnimal;
	public double movementSpeed;
	/** 0 => harvest, 1 => replant, -1 => none */
	private int currentTask;

	static final int maxSearchDist = 16;
	EntityItem targetEntityItem;

	public EntityAIPickUpItem(EntityAgriAnimal farmerIn, double speedIn) {
		this.agriAnimal = farmerIn;
		movementSpeed = speedIn;
		this.setMutexBits(5);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.runDelay > 0) {
			--this.runDelay;
			return false;
		}
		this.runDelay = 120 + this.agriAnimal.getRNG().nextInt(180);

		if (!this.agriAnimal.isHarvesting()
				|| isInventoryFull((IInventory) this.agriAnimal.getInventory())) {
			return false;
		}

		if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.agriAnimal.world,
				this.agriAnimal)) {
			return false;
		}
		this.targetEntityItem = getPickupTargetItem();
		if (this.targetEntityItem != null) {
			this.currentTask = -1;
			return true;

		}
		return false;
	}

	public void startExecuting() {
		this.agriAnimal.getNavigator().tryMoveToEntityLiving(getPickupTargetItem(), this.movementSpeed);
		this.timeoutCounter = 0;
		this.maxStayTicks = this.agriAnimal.getRNG().nextInt(this.agriAnimal.getRNG().nextInt(1200) + 1200) + 1200;

		this.currentTask = 1;
	}

	public boolean shouldContinueExecuting() {
		return this.currentTask >= 0 && this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200
				&& (!this.targetEntityItem.removed || getPickupTargetItem() != null)
				&& !isInventoryFull((IInventory) this.agriAnimal.getInventory());
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		if (this.targetEntityItem.removed) {
			this.agriAnimal.getNavigator().clearPath();
			this.targetEntityItem = getPickupTargetItem();
			if (this.targetEntityItem == null) {
				this.currentTask = -1;
				this.runDelay = 10;
				return;
			}
			this.timeoutCounter = 0;
		}
		this.agriAnimal.getLookHelper().setLookPosition(this.targetEntityItem.posX,
				this.targetEntityItem.posY, this.targetEntityItem.posZ, 10.0F,
				(float) this.agriAnimal.getVerticalFaceSpeed());

		if (this.agriAnimal.getDistance(this.targetEntityItem) > 1.0D) {
			//on going
			++this.timeoutCounter;
			this.agriAnimal.getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.movementSpeed);
			if (this.agriAnimal.getNavigator().noPath()) {
				//lost path
				this.currentTask = -1;
				this.runDelay = 10;
			}
		} else {
			//arrive
			--this.timeoutCounter;
			this.agriAnimal.getNavigator().clearPath();
			this.runDelay = 10;
		}
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

	@Nullable
	private EntityItem getPickupTargetItem() {
		if (!this.agriAnimal.world.isRemote && this.agriAnimal.canPickUpLoot()) {
			for (EntityItem entityitem : this.agriAnimal.world.getEntitiesWithinAABB(EntityItem.class,
					this.agriAnimal.getBoundingBox().grow(maxSearchDist, 1.0D, maxSearchDist))) {
				if (!entityitem.removed && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup()) {
					if (this.agriAnimal.isPickupItem(entityitem.getItem().getItem())) {
						if (this.agriAnimal.getNavigator().getPathToEntityLiving(entityitem) != null) {
							return entityitem;
						}
					}
				}
			}
		}
		return null;
	}
}
