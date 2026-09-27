package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyPrincessGuard;
import uut.entity.render.model.ModelToyPrincessGuard;

@SideOnly(Side.CLIENT)
public class RenderToyPrincessGuard extends RenderLiving<EntityToyPrincessGuard> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/high_princess/toy_guard_minion.png");

    public RenderToyPrincessGuard(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyPrincessGuard(), 0.2f);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyPrincessGuard entity) {
        return TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyPrincessGuard entity, final float partialTickTime) {
        GlStateManager.scale(0.6f, 0.6f, 0.6f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyPrincessGuard entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}