package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyTyrannosaur;
import uut.entity.render.model.ModelToyTyrannosaur;
import uut.entity.render.model.layer.LayerToyTyrannosaurCollar;

@SideOnly(Side.CLIENT)
public class RenderToyTyrannosaur extends RenderLiving<EntityToyTyrannosaur> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/tyrannosaur/toy_tyrannosaur.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/tyrannosaur/toy_tyrannosaur_tamed.png");

    public RenderToyTyrannosaur(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyTyrannosaur(), 0.35f);
        this.addLayer(new LayerToyTyrannosaurCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyTyrannosaur entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyTyrannosaur entity, final float partialTickTime) {
        GlStateManager.scale(0.6f, 0.6f, 0.6f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyTyrannosaur entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}