package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyPrincessRecruit;
import uut.entity.render.model.ModelToyPrincessRecruit;

@SideOnly(Side.CLIENT)
public class RenderToyPrincessRecruit extends RenderLiving<EntityToyPrincessRecruit> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/high_princess/toy_recruit_minion.png");

    public RenderToyPrincessRecruit(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyPrincessRecruit(), 0.2f);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyPrincessRecruit entity) {
        return TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyPrincessRecruit entity, final float partialTickTime) {
        GlStateManager.scale(0.6f, 0.6f, 0.6f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyPrincessRecruit entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}