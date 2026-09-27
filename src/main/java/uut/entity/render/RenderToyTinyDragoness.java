package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyTinyDragoness;
import uut.entity.render.model.ModelToyTinyDragoness;
import uut.entity.render.model.layer.LayerToyTinyDragonessCollar;

@SideOnly(Side.CLIENT)
public class RenderToyTinyDragoness extends RenderLiving<EntityToyTinyDragoness> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/tiny_dragoness/toy_tinydragoness.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/tiny_dragoness/toy_tinydragoness_tamed.png");

    public RenderToyTinyDragoness(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyTinyDragoness(), 0.35f);
        this.addLayer(new LayerToyTinyDragonessCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyTinyDragoness entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyTinyDragoness entity, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyTinyDragoness entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}