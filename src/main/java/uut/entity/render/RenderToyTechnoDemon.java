package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyTechnoDemon;
import uut.entity.render.model.ModelToyTechnoDemon;
import uut.entity.render.model.layer.LayerToyTechnoDemonCollar;
import uut.entity.render.model.layer.LayerToyTechnoDemonGlow;

@SideOnly(Side.CLIENT)
public class RenderToyTechnoDemon extends RenderLiving<EntityToyTechnoDemon> {
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut", "textures/model/techno_demon/toy_technodemon.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut", "textures/model/techno_demon/toy_technodemon_tamed.png");

    public RenderToyTechnoDemon(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyTechnoDemon(), 0.34f);
        this.addLayer(new LayerToyTechnoDemonGlow(this));
        this.addLayer(new LayerToyTechnoDemonCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyTechnoDemon entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyTechnoDemon entity, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyTechnoDemon entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}