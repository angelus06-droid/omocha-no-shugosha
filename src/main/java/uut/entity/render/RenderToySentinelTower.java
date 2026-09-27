package uut.entity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToySentinelTower;
import uut.entity.render.model.ModelToySentinelTower;
import uut.entity.render.model.layer.LayerToySentinelTowerCollar;

@SideOnly(Side.CLIENT)
public class RenderToySentinelTower extends RenderLiving<EntityToySentinelTower> {
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/sentinel_tower/toy_sentineltower.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/sentinel_tower/toy_sentineltower_tamed.png");

    public RenderToySentinelTower(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToySentinelTower(), 0.32f);
        this.addLayer(new LayerToySentinelTowerCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToySentinelTower entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToySentinelTower entity, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToySentinelTower entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}
