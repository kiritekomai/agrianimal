package kiritekomai.agrianimal.client;

import kiritekomai.agrianimal.client.render.entity.RenderAgriWolf;
import kiritekomai.agrianimal.entity.EntityAgriWolf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class AgriAnimalRender {
	public static void entityRender() {
		RenderingRegistry.registerEntityRenderingHandler(EntityAgriWolf.class, RenderAgriWolf::new);
	}

}
