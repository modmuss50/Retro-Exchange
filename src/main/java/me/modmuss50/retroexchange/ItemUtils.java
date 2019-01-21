package me.modmuss50.retroexchange;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


//Thanks mezz, I stole this from JEI
public class ItemUtils {

	public static List<ItemStack> getSubtypes(@Nullable
		                                   ItemStack itemStack) {
		if (itemStack == null || itemStack.isEmpty()) {
			return Collections.emptyList();
		}

		if (itemStack.getItemDamage() != OreDictionary.WILDCARD_VALUE || !itemStack.getHasSubtypes()) {
			return Collections.singletonList(itemStack.copy());
		}

		NonNullList<ItemStack> subtypes = NonNullList.create();
		addSubtypesToList(subtypes, itemStack.copy());
		return subtypes;
	}

	private static void addSubtypesToList(final List<ItemStack> subtypeList, ItemStack itemStack) {
		final Item item = itemStack.getItem();
		final int stackSize = itemStack.getCount();
		for (CreativeTabs itemTab : item.getCreativeTabs()) {
			if (itemTab == null) {
				subtypeList.add(itemStack.copy());
			} else {
				addSubtypesFromCreativeTabToList(subtypeList, item, stackSize, itemTab);
			}
		}
	}

	private static void addSubtypesFromCreativeTabToList(List<ItemStack> subtypeList, Item item, final int stackSize, CreativeTabs itemTab) {
		NonNullList<ItemStack> subItems = NonNullList.create();
		try {
			item.getSubItems(itemTab, subItems);
		} catch (RuntimeException | LinkageError e) {
			RetroExchange.LOGGER.warn("Caught a crash while getting sub-items of {}", item, e);
		}

		for (ItemStack subItem : subItems) {
			if (subItem.isEmpty()) {
				RetroExchange.LOGGER.warn("Found an empty subItem of {}", item);
			} else if (subItem.getMetadata() == OreDictionary.WILDCARD_VALUE) {
				RetroExchange.LOGGER.error("Found an subItem of {} with wildcard metadata", item);
			} else {
				if (subItem.getCount() != stackSize) {
					ItemStack subItemCopy = subItem.copy();
					subItemCopy.setCount(stackSize);
					subtypeList.add(subItemCopy);
				} else {
					subtypeList.add(subItem.copy());
				}
			}
		}
	}

}
