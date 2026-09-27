package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyPrincessOgre;
import uut.entity.render.model.ModelToyPrincessOgre;

@SideOnly(Side.CLIENT)
public class RenderToyPrincessOgre extends RenderLiving<EntityToyPrincessOgre> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/high_princess/toy_ogre_minion.png");

    public RenderToyPrincessOgre(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyPrincessOgre(), 0.25f);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyPrincessOgre entity) {
        return TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyPrincessOgre entity, final float partialTickTime) {
        GlStateManager.scale(0.95f, 0.95f, 0.95f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyPrincessOgre entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}