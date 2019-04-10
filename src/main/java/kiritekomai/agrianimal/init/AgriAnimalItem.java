package kiritekomai.agrianimal.init;

import kiritekomai.agrianimal.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MODID)
public class AgriAnimalItem {

	public static final Item spawn_egg_agri_wolf = null;

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new ItemSpawnEgg(AgriAnimalEntity.AGRI_WOLF, 0xFFFFFF, 0x000000,
						new Item.Properties().maxStackSize(64).group(ItemGroup.MISC)).setRegistryName(Reference.MODID,
								"spawn_egg_agri_wolf"));
	}
}
