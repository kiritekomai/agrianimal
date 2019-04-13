package kiritekomai.agrianimal.init;

import java.util.Iterator;
import java.util.Set;

import kiritekomai.agrianimal.Reference;
import kiritekomai.agrianimal.entity.EntityAgriOcelot;
import kiritekomai.agrianimal.entity.EntityAgriWolf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MODID)
public class AgriAnimalEntity {
	public static final EntityType<EntityAgriWolf> AGRI_WOLF = EntityType.Builder
			.create(EntityAgriWolf.class, EntityAgriWolf::new).build(Reference.MODID+":agri_wolf");
	public static final EntityType<EntityAgriOcelot> AGRI_OCELOT = EntityType.Builder
			.create(EntityAgriOcelot.class, EntityAgriOcelot::new).build(Reference.MODID+":agri_ocelot");

	@SubscribeEvent
	public static void registerEntity(Register<EntityType<?>> event) {
		IRegistry.field_212629_r.put(new ResourceLocation(Reference.MODID+":agri_wolf"), AGRI_WOLF);
		event.getRegistry().register(AGRI_WOLF);

		IRegistry.field_212629_r.put(new ResourceLocation(Reference.MODID+":agri_ocelot"), AGRI_OCELOT);
		event.getRegistry().register(AGRI_OCELOT);
	}

	public static void spawnEntity() {
		Set<Biome> forestbiomes = BiomeDictionary.getBiomes(Type.FOREST);
		Iterator var1 = forestbiomes.iterator();
		while (var1.hasNext()) {
			Biome biome = (Biome) var1.next();
			biome.getSpawns(EnumCreatureType.CREATURE).add(new Biome.SpawnListEntry(AGRI_WOLF, 6, 2, 3));

		}
		

		Set<Biome> junblebiomes = BiomeDictionary.getBiomes(Type.JUNGLE);
		var1 = junblebiomes.iterator();
		while (var1.hasNext()) {
			Biome biome = (Biome) var1.next();
			biome.getSpawns(EnumCreatureType.CREATURE).add(new Biome.SpawnListEntry(AGRI_OCELOT, 6, 2, 3));

		}
	}

}
