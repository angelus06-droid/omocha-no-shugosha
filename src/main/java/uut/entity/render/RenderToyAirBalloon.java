package uut.entity.render;

import uut.entity.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import uut.entity.render.model.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import uut.entity.render.model.layer.LayerToyAirBalloonCollar;

@SideOnly(Side.CLIENT)
public class RenderToyAirBalloon extends RenderLiving<EntityToyAirBalloon> {
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/air_balloon/toy_air_balloon.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/air_balloon/toy_air_balloon_tamed.png");
    
    public RenderToyAirBalloon(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToyAirBalloon(), 0.4f);
        this.addLayer(new LayerToyAirBalloonCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyAirBalloon entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyAirBalloon entity, final float partialTickTime) {
        GlStateManager.scale(0.75f, 0.75f, 0.75f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyAirBalloon entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}
