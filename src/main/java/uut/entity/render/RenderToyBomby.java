package uut.entity.render;

import uut.entity.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import uut.entity.render.model.*;
import net.minecraft.client.model.*;
import net.minecraft.util.math.*;
import net.minecraft.client.renderer.*;
import uut.entity.render.model.layer.LayerToyBombyCollar;

@SideOnly(Side.CLIENT)
public class RenderToyBomby extends RenderLiving<EntityToyBomby>
{
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/bomby/toy_bomby.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/bomby/toy_bomby_tamed.png");
    
    public RenderToyBomby(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToyBomby(), 0.35f);
        this.addLayer(new LayerToyBombyCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyBomby entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }
    
    protected void preRenderCallback(final EntityToyBomby entitylivingbaseIn, final float partialTickTime) {
        float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
        final float f2 = 1.0f + MathHelper.sin(f * 100.0f) * f * 0.01f;
        f = MathHelper.clamp(f, 0.0f, 1.0f);
        f *= f;
        f *= f;
        final float f3 = (0.8f + f * 0.9f) * f2;
        final float f4 = (0.8f + f * 0.6f) / f2;
        GlStateManager.scale(f3, f4, f3);
    }
    
    protected int getColorMultiplier(final EntityToyBomby entitylivingbaseIn, final float lightBrightness, final float partialTickTime) {
        final float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
        if ((int)(f * 10.0f) % 2 == 0) {
            return 0;
        }
        int i = (int)(f * 0.2f * 255.0f);
        i = MathHelper.clamp(i, 0, 255);
        return i << 24 | 0x30FFFFFF;
    }
}
