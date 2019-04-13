package kiritekomai.agrianimal.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public abstract class EntityAgriAnimal extends EntityTameable {

	private static final DataParameter<Boolean> HAEVESTING = EntityDataManager.createKey(EntityAgriAnimal.class,
			DataSerializers.BOOLEAN);

	private final InventoryBasic myInventory = new InventoryBasic(new TextComponentString("Items"), 8);

	public EntityAgriAnimal(EntityType<?> type, World worldIn) {
		super(type, worldIn);
		this.setCanPickUpLoot(true);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(HAEVESTING, false);
	}

	/**
	* Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
	* use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
	*/
	public void writeAdditional(NBTTagCompound compound) {
		super.writeAdditional(compound);
		compound.setBoolean("Harvesting", this.isHarvesting());

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < this.myInventory.getSizeInventory(); ++i) {
			ItemStack itemstack = this.myInventory.getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				nbttaglist.add((INBTBase) itemstack.write(new NBTTagCompound()));
			}
		}
		compound.setTag("Inventory", nbttaglist);
	}

	/**
	* (abstract) Protected helper method to read subclass entity data from NBT.
	*/
	public void readAdditional(NBTTagCompound compound) {
		super.readAdditional(compound);
		this.setHarvesting(compound.getBoolean("Harvesting"));

		NBTTagList nbttaglist = compound.getList("Inventory", 10);

		for (int i = 0; i < nbttaglist.size(); ++i) {
			ItemStack itemstack = ItemStack.read(nbttaglist.getCompound(i));
			if (!itemstack.isEmpty()) {
				this.myInventory.addItem(itemstack);
			}
		}

	}

	public InventoryBasic getInventory() {
		return this.myInventory;
	}

	public void dropInventoryItems() {

		float f = 0.3F;
		float f1 = this.rotationYawHead;
		float f2 = this.rotationPitch;
		double mx = (double) (-MathHelper.sin(f1 * ((float) Math.PI / 180F))
				* MathHelper.cos(f2 * ((float) Math.PI / 180F)) * 0.3F);
		double my = (double) (MathHelper.cos(f1 * ((float) Math.PI / 180F))
				* MathHelper.cos(f2 * ((float) Math.PI / 180F)) * 0.3F);
		double mz = (double) (-MathHelper.sin(f2 * ((float) Math.PI / 180F)) * 0.3F + 0.1F);

		for (int i = 0; i < this.myInventory.getSizeInventory(); ++i) {
			ItemStack itemstack = this.myInventory.getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY + 0.5F, this.posZ,
						itemstack.copy());
				entityitem.motionX = mx;
				entityitem.motionY = my;
				entityitem.motionZ = mz;
				entityitem.setDefaultPickupDelay();
				if (this.world.spawnEntity(entityitem)) {
					itemstack.setCount(0);
				}
			}
			this.myInventory.setInventorySlotContents(i, itemstack);
			this.myInventory.markDirty();
		}

	}

	public boolean isFarmItemInInventory() {
		for (int i = 0; i < this.myInventory.getSizeInventory(); ++i) {
			Item item = this.myInventory.getStackInSlot(i).getItem();
			if (this.isFarmableItem(item)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
		ItemStack itemstack = itemEntity.getItem();
		Item item = itemstack.getItem();
		if (this.isFarmableItem(item) || this.isPickupItem(item)) {
			ItemStack itemstack1 = this.myInventory.addItem(itemstack);
			if (itemstack1.isEmpty()) {
				itemEntity.remove();
			} else {
				itemstack.setCount(itemstack1.getCount());
			}
		}

	}

	public boolean isFarmableItem(Item itemIn) {
		return itemIn instanceof ItemSeeds || itemIn instanceof ItemSeedFood;
	}

	public boolean isPickupItem(Item itemIn) {
		return true;
	}

	public void setHarvesting(boolean harvesting) {
		this.dataManager.set(HAEVESTING, harvesting);
	}

	public boolean isHarvesting() {
		return this.dataManager.get(HAEVESTING);
	}
}
