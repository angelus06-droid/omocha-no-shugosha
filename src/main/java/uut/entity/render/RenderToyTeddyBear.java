package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyTeddyBear;
import uut.entity.render.model.ModelToyTeddyBear;
import uut.entity.render.model.layer.LayerToyTeddyBearHeldItem;
import uut.entity.render.model.layer.LayerFireArmor;
import uut.entity.render.model.layer.LayerToyTeddyBearCollar;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderToyTeddyBear extends RenderLiving<EntityToyTeddyBear> {

    private static final ResourceLocation[] BEAR_TEXTURES = new ResourceLocation[16];

    static {
        for (EnumDyeColor color : EnumDyeColor.values()) {
            int metadata = color.getMetadata();
            BEAR_TEXTURES[metadata] = new ResourceLocation(
                    "uut",
                    "textures/model/teddy_bear/toy_teddybear_" + color.getName() + ".png"
            );
        }
    }

    public RenderToyTeddyBear(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyTeddyBear(), 0.33F);

        this.addLayer(new LayerToyTeddyBearHeldItem(this));
        this.addLayer(new LayerFireArmor(this));
        this.addLayer(new LayerToyTeddyBearCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull final EntityToyTeddyBear entity) {
        int type = entity.getBearType();
        if (type < 0 || type >= BEAR_TEXTURES.length) {
            type = 0;
        }
        return BEAR_TEXTURES[type];
    }

    @Override
    protected void preRenderCallback(final EntityToyTeddyBear entity, final float partialTickTime) {
        GlStateManager.scale(0.65f, 0.65f, 0.65f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyTeddyBear entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }

    @Override
    protected void applyRotations(EntityToyTeddyBear entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
        float limbSwingAmount = entityLiving.limbSwingAmount;
        if (limbSwingAmount >= 0.01F) {
            float swingProgress = entityLiving.limbSwing - limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float wobble = (Math.abs(swingProgress % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * wobble, 0.0F, 0.0F, 1.0F);
        }
    }
}