package uut.entity.render;

import uut.entity.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import uut.entity.render.model.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;
import uut.entity.render.model.layer.LayerInfiniteAura;
import uut.entity.render.model.layer.LayerToyArrowTowerCollar;
import uut.entity.render.model.layer.LayerToyGunnerCollar;

@SideOnly(Side.CLIENT)
public class RenderToyArrowTower extends RenderLiving<EntityToyArrowTower>
{
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/arrow_tower/toy_arrowtower.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/arrow_tower/toy_arrowtower_tamed.png");
    
    public RenderToyArrowTower(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToyArrowTower(), 0.35f);
        this.addLayer(new LayerInfiniteAura(this));
        this.addLayer(new LayerToyArrowTowerCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyArrowTower entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyArrowTower entity, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyArrowTower entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}
