package me.modmuss50.retroexchange;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.impl.ConfigRegistry;
import reborncore.common.util.RebornCraftingHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RebornRegistry(modID = "retroexchange")
@Mod(modid = "retroexchange", name = "Retro Exchange")
public class RetroExchange {

	public static final Logger LOGGER = LogManager.getLogger("retroexchange");

	public static CreativeTabs CREATIVE_TAB = new CreativeTabs("retroexchange") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(transmutationShard);
		}
	};

	public static Item transmutationShard;
	public static Item transmutationStone;

	public static Map<Block, Block> blockConversionMap = new HashMap<>();

	private static Random random = new Random(System.nanoTime());

	@ConfigRegistry(key = "shard_drop_chance", comment = "The chance for a shard to drop from an entity")
	public static int dropChance = 25;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);

		blockConversionMap.put(Blocks.SAND, Blocks.DIRT);
		blockConversionMap.put(Blocks.DIRT, Blocks.COBBLESTONE);
		blockConversionMap.put(Blocks.COBBLESTONE, Blocks.GRASS);
		blockConversionMap.put(Blocks.GRASS, Blocks.SAND);

		blockConversionMap.put(Blocks.STONE, Blocks.COBBLESTONE);
		blockConversionMap.put(Blocks.GLASS, Blocks.SAND);
	}

	@SubscribeEvent
	public void itemInit(RegistryEvent.Register<Item> event){
		transmutationShard = new ItemTransmutationShard();
		transmutationStone = new ItemTransmutationStone();

		event.getRegistry().register(transmutationShard);
		event.getRegistry().register(transmutationStone);
	}

	@SubscribeEvent
	public void entityDeath(LivingDeathEvent event){
		if(event.getEntity() instanceof EntityMob){
			if(event.getSource().getTrueSource() instanceof EntityPlayer){
				if(random.nextInt(dropChance) == 0){
					event.getEntity().dropItem(transmutationShard, 1);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		addTransmuteRecipe(Items.FLINT, Blocks.COBBLESTONE, 4);
		addTransmuteRecipe(Blocks.GRAVEL, Blocks.DIRT, 4);
		addTransmuteRecipe(Blocks.SANDSTONE, Blocks.SAND, 4);
		addTransmuteRecipe(Blocks.PLANKS, Items.STICK, 2);
		addTransmuteRecipe(Blocks.LOG, Blocks.PLANKS, 4);
		addTransmuteRecipe(Items.CLAY_BALL, Blocks.GRAVEL, 4);
		addTransmuteRecipe(Items.CLAY_BALL, Items.FLINT, 4);
		addTransmuteRecipe(Blocks.CLAY, Items.CLAY_BALL, 4);
		addTransmuteRecipe(Blocks.OBSIDIAN, Blocks.PLANKS, 2);
		addTransmuteRecipe(Items.IRON_INGOT, Blocks.OBSIDIAN, 4);
		addTransmuteRecipe(Items.IRON_INGOT, Blocks.CLAY, 4);
		addTransmuteRecipe(Items.GOLD_INGOT, Items.IRON_INGOT, 8);
		addTransmuteRecipe(Items.DIAMOND, Items.GOLD_INGOT, 4);
		addTransmuteRecipe(Blocks.GOLD_BLOCK, Blocks.IRON_BLOCK, 8);
		addTransmuteRecipe(Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK, 4);
		addTransmuteRecipe(Items.ENDER_EYE, Items.IRON_INGOT, 4);
		addTransmuteRecipe(Items.REEDS, Items.PAPER, 3);
		addTransmuteRecipe(Items.BONE, new ItemStack(Items.DYE, 1, 15), 3);
		addTransmuteRecipe(Items.BLAZE_ROD, Items.BLAZE_POWDER, 3);


		addOneWayTransmuteRecipe(Blocks.DIRT, Blocks.SAND, 1);
		addOneWayTransmuteRecipe(Blocks.COBBLESTONE, Blocks.DIRT, 1);
		addOneWayTransmuteRecipe(Blocks.GRASS, Blocks.COBBLESTONE, 1);
		addOneWayTransmuteRecipe(Blocks.SAND, Blocks.GRASS, 1);
		addOneWayTransmuteRecipe(Blocks.COBBLESTONE, Blocks.STONE, 1);
		addOneWayTransmuteRecipe(Blocks.SAND, Blocks.GLASS, 1);

		addOreDictRR("record");
		addOreDictRR("plankWood");
		addOreDictRR("logWood");
		addOreDictRR("slabWood");
		addOreDictRR("treeSapling");
		addOreDictRR("treeLeaves");
		addOreDictRR("treeLeaves");
		addOreDictRR("fenceWood");
		addOreDictRR("fenceGateWood");
		addOreDictRR("doorWood");
		addOreDictRR("stickWood");
		addOreDictRR("chestWood");
		addOreDictRR("paneGlass");
		addOreDictRR("blockGlass");

		addAllSmelting();

		RebornCraftingHelper.addShapedRecipe(new ItemStack(transmutationStone),
			"SSS",
				"SDS",
				"SSS",
			'S', new ItemStack(transmutationShard), 'D', new ItemStack(Items.DIAMOND)
		);
	}

	public void addAllSmelting(){
		FurnaceRecipes.instance().getSmeltingList().forEach((input, output) -> {
			ItemStack copy = output.copy();
			copy.setCount(7);
			RebornCraftingHelper.addShapelessRecipe(copy, getStoneStack(), new ItemStack(Items.COAL),
				input, input, input, input, input, input, input
			);
		});


	}

	public static void addTransmuteRecipe(Object object, Object input, int size){
		ItemStack output = getStack(object);

		Object[] inputs = new Object[size + 1];
		for (int i = 1; i < size + 1; i++) {
			inputs[i] = input;
		}
		inputs[0] = getStoneStack();
		RebornCraftingHelper.addShapelessRecipe(output, inputs);

		RebornCraftingHelper.addShapelessRecipe(getStack(input, size), output, getStoneStack());

	}


	public static void addOneWayTransmuteRecipe(Object object, Object input, int size){
		ItemStack output = getStack(object);

		Object[] inputs = new Object[size + 1];
		for (int i = 1; i < size + 1; i++) {
			inputs[i] = input;
		}
		inputs[0] = getStoneStack();
		RebornCraftingHelper.addShapelessRecipe(output, inputs);
	}


	public static void addOreDictRR(String oreDict){
		List<ItemStack> stackList = getAllOres(oreDict);
		if(stackList.isEmpty() || stackList.size() == 1){
			return;
		}
		for (int i = 0; i < stackList.size() -1; i++) {
			RebornCraftingHelper.addShapelessRecipe(stackList.get(i + 1).copy(), getStoneStack(), stackList.get(i).copy());
		}
		RebornCraftingHelper.addShapelessRecipe(stackList.get(0).copy(), getStoneStack(), stackList.get(stackList.size() -1).copy());
	}
	
	public static List<ItemStack> getAllOres(String oreDict){
		NonNullList<ItemStack> ores = OreDictionary.getOres(oreDict);
		NonNullList<ItemStack> stacks = NonNullList.create();
		for(ItemStack stack : ores){
			stacks.addAll(ItemUtils.getSubtypes(stack));
		}
		return stacks;
	}

	public static ItemStack getStoneStack(){
		return new ItemStack(transmutationStone, 1, OreDictionary.WILDCARD_VALUE);
	}


	public static ItemStack getStack(Object object){
		return getStack(object, 1);
	}

	public static ItemStack getStack(Object object, int size){
		ItemStack stack = ItemStack.EMPTY;
		if(object instanceof ItemStack){
			stack = ((ItemStack) object).copy();
		} if (object instanceof Block){
			stack = new ItemStack((Block) object);
		} if (object instanceof Item){
			stack = new ItemStack((Item) object);
		}
		stack.setCount(size);
		return stack;
	}



}
