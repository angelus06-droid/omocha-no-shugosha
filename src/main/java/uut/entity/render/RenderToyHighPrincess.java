package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyHighPrincess;
import uut.entity.render.model.ModelToyHighPrincess;
import uut.entity.render.model.layer.LayerToyHighPrincessCollar;

@SideOnly(Side.CLIENT)
public class RenderToyHighPrincess extends RenderLiving<EntityToyHighPrincess> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/high_princess/toy_high_princess.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/high_princess/toy_high_princess_tamed.png");

    public RenderToyHighPrincess(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyHighPrincess(), 0.25f);
        this.addLayer(new LayerToyHighPrincessCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyHighPrincess entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyHighPrincess entity, final float partialTickTime) {
        GlStateManager.scale(0.6f, 0.6f, 0.6f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyHighPrincess entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}