package uut.entity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyT58;
import uut.entity.render.model.ModelToyT58;
import uut.entity.render.model.layer.LayerToyT58Collar;

@SideOnly(Side.CLIENT)
public class RenderToyT58 extends RenderLiving<EntityToyT58>
{
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/t58/toy_t58.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/t58/toy_t58_tamed.png");

    public RenderToyT58(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToyT58(), 0.3f);
        this.addLayer(new LayerToyT58Collar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyT58 entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyT58 entity, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyT58 entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}
