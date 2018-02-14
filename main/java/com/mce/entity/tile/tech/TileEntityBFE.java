package com.mce.entity.tile.tech;

import com.mce.blocks.tech.BioFuelExtractor;
import com.mce.handlers.custom_recipes.BFERecipes;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyStorage;
import cofh.api.energy.TileEnergyHandler;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityBFE extends TileEnergyHandler implements ISidedInventory {
	// Slot 0 = input; slot 1 = output
	private static final int[] input_slot = new int[] { 0 };
	private static final int[] output_slot = new int[] { 1 };

	private String isInvNameLocalized;
	private ItemStack[] slots = new ItemStack[2];
	protected EnergyStorage es = new EnergyStorage(0);

	// Cutter Specs
	// 300 speed is a little slower than a furnace
	public int speed = 300;
	public int extractingTime;
	public int burnTime;
	public int facing;
	public int damage;
	public int maxDamage;

	public int getSizeInv() {
		return this.slots.length;
	}

	private String ln;

	public void setGuiDisplayName(String dn) {
		this.ln = dn;
	}

	public boolean isInvNameLocalized() {
		return this.isInvNameLocalized != null && this.ln.length() > 0;
	}

	public String getInvName() {
		return this.isInvNameLocalized() ? this.ln : "container.bfe";
	}

	public int getSizeInventory() {
		return 0;
	}

	public ItemStack getStackInSlot(int i) {
		return this.slots[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.slots[i] != null) {
			ItemStack stack;

			if (this.slots[i].stackSize <= j) {
				stack = this.slots[i];
				this.slots[i] = null;
				return stack;
			} else {
				stack = this.slots[i].splitStack(j);

				if (this.slots[i].stackSize == 0) {
					this.slots[i] = null;
				}
				return stack;
			}
		}
		return null;
	}

	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.slots[i] != null) {
			ItemStack stack = this.slots[i];
			this.slots[i] = null;
			return stack;
		}
		return null;
	}

	public void setInventorySlotContents(int i, ItemStack stack) {
		this.slots[i] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setInteger("ExtractingTime", this.extractingTime);
		tag.setInteger("Facing", this.facing);
		if (getDamage() > getMaxDamage())
			setDamage(getMaxDamage());
		if (getDamage() < 0)
			setDamage(0);

		tag.setInteger("Damage", getDamage());
		tag.setInteger("MaxDamage", getMaxDamage());
		es.writeToNBT(tag);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < this.slots.length; i++) {
			if (this.slots[i] != null) {
				NBTTagCompound comp = new NBTTagCompound();
				comp.setByte("Slot", (byte) i);
				this.slots[i].writeToNBT(comp);
				list.appendTag(comp);
			}
		}

		tag.setTag("Items", list);

		if (this.isInvNameLocalized()) {
			tag.setString("CustomName", this.ln);
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		NBTTagList list = tag.getTagList("Items", 10);
		this.slots = new ItemStack[this.getSizeInv()];

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound comp = (NBTTagCompound) list.getCompoundTagAt(i);
			byte b = comp.getByte("Slot");

			if (b >= 0 && b < this.slots.length) {
				this.slots[b] = ItemStack.loadItemStackFromNBT(comp);
			}
		}

		this.extractingTime = tag.getInteger("ExtractingTime");
		this.facing = tag.getInteger("Facing");
		setDamage(tag.getInteger("Damage"));
		setMaxDamage(tag.getInteger("MaxDamage"));
		es.readFromNBT(tag);

		if (getDamage() > getMaxDamage())
			setDamage(getMaxDamage());
		if (getDamage() < 0)
			setDamage(0);

		if (tag.hasKey("CustomName")) {
			this.ln = tag.getString("CustomName");
		}
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false
				: player.getDistanceSq((double) this.xCoord + .5d, (double) this.yCoord + .5d,
						(double) this.zCoord + .5d) <= 64d;
	}

	public void setFacing(int side) {
		this.facing = side;
	}

	public int getFacing() {
		return this.facing;
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public boolean isExtracting() {
		return this.extractingTime > 0;
	}

	public int getEnergyNeeded() {
		return 50;
	}

	public void updateEntity() {
		boolean flag = this.isPowered();
		boolean flag1 = false;

		this.calcTier(); // Method is in Machine Casing Tile Entity class

		if (this.isExtracting() && flag) {
			this.burnTime--;
			this.damage--;
		}

		if (!this.worldObj.isRemote) {
			if (this.canExtract() && this.checkSlot() && (this.getDamage() > 0)) {
				if (flag) {
					es.extractEnergy(this.getEnergyNeeded(), false);
					this.extractingTime++;

					if (this.extractingTime == this.speed) {
						this.extractingTime = 0;
						this.extractItem();
						flag1 = true;
					}

					if (this.getDamage() <= 0) {
						this.extractingTime = 0;
					}
				}
			} else {
				this.extractingTime = 0;
			}

			if (flag != this.isPowered()) {
				flag1 = true;
				BioFuelExtractor.updateState(this.isPowered(), this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if (flag1) {
			this.onInventoryChanged();
		}
	}

	private void onInventoryChanged() {
	}

	public boolean checkSlot() {
		ItemStack stack = BFERecipes.extracting().getInput(this.slots[0]);
		if (this.slots[0] == stack) {
			return true;
		} else {
			return false;
		}
	}

	private boolean canExtract() {
		if (this.slots[0] == null) {
			return false;
		} else {
			ItemStack stack = BFERecipes.extracting().getExtractingResult(this.slots[0]);

			if (this.slots[0] == null) {
				return false;
			}

			if (this.slots[1] == null) {
				return true;
			}

			if (!this.slots[1].isItemEqual(stack)) {
				return false;
			}

			int result = this.slots[1].stackSize + stack.stackSize;

			return result <= getInventoryStackLimit() && result <= stack.getMaxStackSize();
		}
	}

	public void extractItem() {
		if (this.canExtract() == true) {
			ItemStack stack = BFERecipes.extracting().getExtractingResult(this.slots[0]);

			if (this.slots[1] == null) {
				this.slots[1] = stack.copy();
			}

			else if (this.slots[1].isItemEqual(stack)) {
				this.slots[1].stackSize += stack.stackSize;
			}

			this.slots[0].stackSize--;

			if (this.slots[0].stackSize <= 0) {
				this.slots[0] = null;
			}
		}
	}

	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return i == 1 ? false : true;
	}

	public int[] getAccessibleSlotsFromSide(int side) {
		return side == 0 ? input_slot : side == 1 ? output_slot : null;
	}

	public boolean canInsertItem(int i, ItemStack stack, int j) {
		return this.isItemValidForSlot(i, stack);
	}

	public boolean canExtractItem(int i, ItemStack stack, int j) {
		return j != 0 || i != 1;
	}

	public String getInventoryName() {
		return null;
	}

	public boolean hasCustomInventoryName() {
		return false;
	}

	public void openInventory() {
	}

	public void closeInventory() {
	}

	public int getCookProgressScaled(int i) {
		return this.extractingTime * i / this.speed;
	}

	// public int lvl;

	/*public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		setDamage(tag.getInteger("Damage"));
		setMaxDamage(tag.getInteger("MaxDamage"));
		es.readFromNBT(tag);

		if (getDamage() > getMaxDamage())
			setDamage(getMaxDamage());
		if (getDamage() < 0)
			setDamage(0);
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (getDamage() > getMaxDamage())
			setDamage(getMaxDamage());
		if (getDamage() < 0)
			setDamage(0);

		tag.setInteger("Damage", getDamage());
		tag.setInteger("MaxDamage", getMaxDamage());
		es.writeToNBT(tag);
	}*/

	public void setMaxEnergy(int max) {
		es.setCapacity(max);
	}

	public void setMaxDamage(int max) {
		maxDamage = max;
	}

	public void setDamage(int dam) {
		damage = dam;
	}

	public int getDamage() {
		return damage;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public boolean getMeta(int meta) {
		if (meta == this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord))
			return true;
		else
			return false;
	}

	public boolean isPowered() {
		return this.getEnergyStored(ForgeDirection.UNKNOWN) > 0;
	}

	public boolean hasEnergy(int energy) {
		return es.getEnergyStored() >= energy;
	}

	public boolean drainEnergy(int energy) {
		return hasEnergy(energy) && es.extractEnergy(energy, false) == energy;
	}

	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return es.receiveEnergy(maxReceive, simulate);
	}

	public int getEnergyStored(ForgeDirection from) {
		return es.getEnergyStored();
	}

	public int getMaxEnergyStored(ForgeDirection from) {
		return es.getMaxEnergyStored();
	}

	public boolean canConnectEnergy(ForgeDirection from) {
		return es.getMaxEnergyStored() > 0;
	}

	public final void setEnergyStored(int quantity) {
		es.setEnergyStored(quantity);
	}

	public IEnergyStorage getEnergyStored() {
		return es;
	}

	public int getScaledEnergyStorage(int scale) {
		return MathHelper.round((long) es.getEnergyStored() * scale / es.getMaxEnergyStored());
	}

	public void calcTier() {
		// Steel
		if (getMeta(0)) {
			setMaxDamage(32000);
			setMaxEnergy(32000);

			if (getDamage() > getMaxDamage())
				setDamage(getMaxDamage());
			if (getDamage() < 0)
				setDamage(0);
		}

		// Titanium
		if (getMeta(1)) {
			setMaxDamage(64000);
			setMaxEnergy(64000);

			if (getDamage() > getMaxDamage())
				setDamage(getMaxDamage());
			if (getDamage() < 0)
				setDamage(0);
		}

		// Tantalum
		if (getMeta(2)) {
			setMaxDamage(96000);
			setMaxEnergy(96000);

			if (getDamage() > getMaxDamage())
				setDamage(getMaxDamage());
			if (getDamage() < 0)
				setDamage(0);
		}

		// Vanadium
		if (getMeta(3)) {
			setMaxDamage(128000);
			setMaxEnergy(128000);

			if (getDamage() > getMaxDamage())
				setDamage(getMaxDamage());
			if (getDamage() < 0)
				setDamage(0);
		}

		// VC
		if (getMeta(4)) {
			setMaxDamage(160000);
			setMaxEnergy(160000);

			if (getDamage() > getMaxDamage())
				setDamage(getMaxDamage());
			if (getDamage() < 0)
				setDamage(0);
		}
	}

	public int getDamageScaled(int i) {
		return getDamage() * i / getMaxDamage();
	}
}