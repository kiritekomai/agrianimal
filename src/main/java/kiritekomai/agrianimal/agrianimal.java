package kiritekomai.agrianimal;

import kiritekomai.agrianimal.client.AgriAnimalRender;
import kiritekomai.agrianimal.init.AgriAnimalEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MODID)
public class agrianimal {

	public agrianimal() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void init(FMLClientSetupEvent event) {
		//associate entity and render class
		AgriAnimalRender.entityRender();
		//add spawn biomes
		AgriAnimalEntity.spawnEntity();
	}
}
