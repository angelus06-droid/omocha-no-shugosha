package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyPrincessGoblin;
import uut.entity.render.model.ModelToyPrincessGoblin;

@SideOnly(Side.CLIENT)
public class RenderToyPrincessGoblin extends RenderLiving<EntityToyPrincessGoblin> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/high_princess/toy_goblin_minion.png");

    public RenderToyPrincessGoblin(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyPrincessGoblin(), 0.2f);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyPrincessGoblin entity) {
        return TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyPrincessGoblin entity, final float partialTickTime) {
        GlStateManager.scale(0.5f, 0.5f, 0.5f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyPrincessGoblin entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}