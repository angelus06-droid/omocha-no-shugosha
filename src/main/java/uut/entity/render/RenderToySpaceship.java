package uut.entity.render;

import uut.entity.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import uut.entity.render.model.*;
import net.minecraft.client.renderer.*;
import uut.entity.render.model.layer.LayerToySpaceshipCollar;
import uut.entity.render.model.layer.LayerToySpaceshipGlow;

@SideOnly(Side.CLIENT)
public class RenderToySpaceship extends RenderLiving<EntityToySpaceship> {
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/spaceship/toy_spaceship.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/spaceship/toy_spaceship_tamed.png");

    public RenderToySpaceship(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToySpaceship(), 0.35f);
        this.addLayer(new LayerToySpaceshipGlow(this));
        this.addLayer(new LayerToySpaceshipCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToySpaceship entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToySpaceship entity, final float partialTickTime) {
        GlStateManager.scale(0.75f, 0.75f, 0.75f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToySpaceship entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}