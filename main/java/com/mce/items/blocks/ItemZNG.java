package com.mce.items.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemZNG extends ItemBlock {
	public ItemZNG(Block block) {
		super(block);
	}

	public void addInformation(ItemStack stack, EntityPlayer player, List list,
			boolean show) {
		list.add("Zimphnode Generator");
	}
}
