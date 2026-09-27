package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyKitsune;
import uut.entity.render.model.ModelToyKitsune;
import uut.entity.render.model.layer.LayerToyKitsuneCollar;

@SideOnly(Side.CLIENT)
public class RenderToyKitsune extends RenderLiving<EntityToyKitsune> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/kitsune/toy_kitsune.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/kitsune/toy_kitsune_tamed.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("uut:textures/model/kitsune/toy_kitsune_blue.png");
    private static final ResourceLocation TEXTURE_BLUE_TAMED = new ResourceLocation("uut:textures/model/kitsune/toy_kitsune_blue_tamed.png");

    public RenderToyKitsune(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyKitsune(), 0.35f);
        this.addLayer(new LayerToyKitsuneCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyKitsune entity) {
        if (entity.isBlueVariant()) {
            return entity.isTamed() ? TEXTURE_BLUE_TAMED : TEXTURE_BLUE;
        }
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyKitsune entity, final float partialTickTime) {
        GlStateManager.scale(0.85f, 0.85f, 0.85f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyKitsune entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}