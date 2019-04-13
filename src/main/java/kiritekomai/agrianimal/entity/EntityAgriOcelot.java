package kiritekomai.agrianimal.entity;

import javax.annotation.Nullable;

import kiritekomai.agrianimal.Reference;
import kiritekomai.agrianimal.entity.ai.EntityAIAgriOcelotSit;
import kiritekomai.agrianimal.entity.ai.EntityAIMyFollowOwner;
import kiritekomai.agrianimal.entity.ai.EntityAIMyHarvestFarmland;
import kiritekomai.agrianimal.entity.ai.EntityAIPutItemInChest;
import kiritekomai.agrianimal.init.AgriAnimalEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityAgriOcelot extends EntityAgriAnimal {
	private static final Ingredient field_195402_bB = Ingredient.fromItems(Items.COD, Items.SALMON, Items.TROPICAL_FISH,
			Items.PUFFERFISH);
	private static final ResourceLocation field_200608_bC = new ResourceLocation(Reference.MODID, "agri_ocelot");
	private EntityAIAvoidEntity<EntityPlayer> avoidEntity;
	/** The tempt AI task for this mob, used to prevent taming while it is fleeing. */
	private EntityAITempt aiTempt;

	public EntityAgriOcelot(World worldIn) {
		super(AgriAnimalEntity.AGRI_OCELOT, worldIn);
		this.setSize(0.6F, 0.7F);
	}

	protected void initEntityAI() {
		this.aiSit = new EntityAISit(this);
		this.aiTempt = new EntityAITempt(this, 0.6D, field_195402_bB, true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIPutItemInChest(this, 1.0D));
		this.tasks.addTask(3, new EntityAIMyHarvestFarmland(this, 1.0D));
		this.tasks.addTask(4, this.aiSit);
		this.tasks.addTask(5, this.aiTempt);
		this.tasks.addTask(7, new EntityAIMyFollowOwner(this, 1.0D, 10.0F, 5.0F));
		this.tasks.addTask(8, new EntityAIAgriOcelotSit(this, 0.8D));
		this.tasks.addTask(9, new EntityAILeapAtTarget(this, 0.3F));
		this.tasks.addTask(10, new EntityAIOcelotAttack(this));
		this.tasks.addTask(11, new EntityAIMate(this, 0.8D));
		this.tasks.addTask(12, new EntityAIWanderAvoidWater(this, 0.8D, 1.0000001E-5F));
		this.tasks.addTask(13, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
	}

	protected void registerData() {
		super.registerData();
	}

	public void updateAITasks() {
		if (this.getMoveHelper().isUpdating()) {
			double d0 = this.getMoveHelper().getSpeed();
			if (d0 == 0.6D) {
				this.setSneaking(true);
				this.setSprinting(false);
			} else if (d0 == 1.33D) {
				this.setSneaking(false);
				this.setSprinting(true);
			} else {
				this.setSneaking(false);
				this.setSprinting(false);
			}
		} else {
			this.setSneaking(false);
			this.setSprinting(false);
		}

	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	public boolean canDespawn() {
		return !this.isTamed() && this.ticksExisted > 2400;
	}

	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.3F);
	}

	public void fall(float distance, float damageMultiplier) {
	}

	/**
	 * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
	 * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
	 */
	public void writeAdditional(NBTTagCompound compound) {
		super.writeAdditional(compound);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(NBTTagCompound compound) {
		super.readAdditional(compound);
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		if (this.isTamed()) {
			if (this.isInLove()) {
				return SoundEvents.ENTITY_CAT_PURR;
			} else {
				return this.rand.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
			}
		} else {
			return null;
		}
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_CAT_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CAT_DEATH;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.4F;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (this.aiSit != null) {
				this.aiSit.setSitting(false);
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_OCELOT;
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		if (this.isTamed()) {
			Item item = itemstack.getItem();
			if (!itemstack.isEmpty()) {
				if (item == Items.ROTTEN_FLESH) {
					if (!player.abilities.isCreativeMode) {
						itemstack.shrink(1);
					}
					this.dropInventoryItems();
					return true;
				}
			}
			if (this.isOwner(player) && !this.world.isRemote && !this.isBreedingItem(itemstack)) {
				if (hand == EnumHand.MAIN_HAND) {
					if (this.isHarvesting()) {
						if (item == Items.WHEAT) {
							this.aiSit.setSitting(false);
							this.setHarvesting(false);
						} else {
							this.aiSit.setSitting(true);
							this.setHarvesting(false);
						}
					} else {
						if (item == Items.WHEAT) {
							this.aiSit.setSitting(false);
							this.setHarvesting(true);
						} else {
							this.aiSit.setSitting(!this.isSitting());
							this.setHarvesting(false);
						}

					}
					this.isJumping = false;
					this.navigator.clearPath();
					this.setAttackTarget((EntityLivingBase) null);
				}
			}
		} else if ((this.aiTempt == null || this.aiTempt.isRunning() || player.abilities.isCreativeMode) && field_195402_bB.test(itemstack)
				&& player.getDistanceSq(this) < 9.0D) {
			if (!player.abilities.isCreativeMode) {
				itemstack.shrink(1);
			}

			if (!this.world.isRemote) {
				if (this.rand.nextInt(3) == 0
						&& !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
					this.setTamedBy(player);
					this.playTameEffect(true);
					this.aiSit.setSitting(true);
					this.world.setEntityState(this, (byte) 7);
				} else {
					this.playTameEffect(false);
					this.world.setEntityState(this, (byte) 6);
				}
			}

			return true;
		}

		return super.processInteract(player, hand);
	}

	public EntityAgriOcelot createChild(EntityAgeable ageable) {
		EntityAgriOcelot entityocelot = new EntityAgriOcelot(this.world);
		if (this.isTamed()) {
			entityocelot.setOwnerId(this.getOwnerId());
			entityocelot.setTamed(true);
		}

		return entityocelot;
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	 * the animal type)
	 */
	public boolean isBreedingItem(ItemStack stack) {
		return field_195402_bB.test(stack);
	}

	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	public boolean canMateWith(EntityAnimal otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(otherAnimal instanceof EntityAgriOcelot)) {
			return false;
		} else {
			EntityAgriOcelot entityocelot = (EntityAgriOcelot) otherAnimal;
			if (!entityocelot.isTamed()) {
				return false;
			} else {
				return this.isInLove() && entityocelot.isInLove();
			}
		}
	}

	public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
		return this.rand.nextInt(3) != 0;
	}

	public boolean isNotColliding(IWorldReaderBase worldIn) {
		if (worldIn.checkNoEntityCollision(this, this.getBoundingBox())
				&& worldIn.isCollisionBoxesEmpty(this, this.getBoundingBox())
				&& !worldIn.containsAnyLiquid(this.getBoundingBox())) {
			BlockPos blockpos = new BlockPos(this.posX, this.getBoundingBox().minY, this.posZ);
			if (blockpos.getY() < worldIn.getSeaLevel()) {
				return false;
			}

			IBlockState iblockstate = worldIn.getBlockState(blockpos.down());
			Block block = iblockstate.getBlock();
			if (block == Blocks.GRASS_BLOCK || iblockstate.isIn(BlockTags.LEAVES)) {
				return true;
			}
		}

		return false;
	}

	/*public ITextComponent getName() {
		ITextComponent itextcomponent = this.getCustomName();
		if (itextcomponent != null) {
			return itextcomponent;
		} else {
			return (ITextComponent) (this.isTamed()
					? new TextComponentTranslation(Util.makeTranslationKey("entity", field_200608_bC))
					: super.getName());
		}
	}*/

	protected void setupTamedAI() {
		if (this.avoidEntity == null) {
			this.avoidEntity = new EntityAIAvoidEntity<>(this, EntityPlayer.class, 16.0F, 0.8D, 1.33D);
		}

		this.tasks.removeTask(this.avoidEntity);
		if (!this.isTamed()) {
			this.tasks.addTask(4, this.avoidEntity);
		}

	}

	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData,
			@Nullable NBTTagCompound itemNbt) {
		entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
		if (this.world.rand.nextInt(7) == 0) {
			for (int i = 0; i < 2; ++i) {
				EntityAgriOcelot entityocelot = new EntityAgriOcelot(this.world);
				entityocelot.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
				entityocelot.setGrowingAge(-24000);
				this.world.spawnEntity(entityocelot);
			}
		}

		return entityLivingData;
	}
}