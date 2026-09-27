package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyCrimsonBunny;
import uut.entity.render.model.ModelToyCrimsonBunny;
import uut.entity.render.model.layer.LayerToyCrimsonBunnyCollar;

@SideOnly(Side.CLIENT)
public class RenderToyCrimsonBunny extends RenderLiving<EntityToyCrimsonBunny> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/crimson_bunny/toy_crimsonbunny.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/crimson_bunny/toy_crimsonbunny_tamed.png");

    public RenderToyCrimsonBunny(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyCrimsonBunny(), 0.35f);
        this.addLayer(new LayerToyCrimsonBunnyCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyCrimsonBunny entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyCrimsonBunny entity, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyCrimsonBunny entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}