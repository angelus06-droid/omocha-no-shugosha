package uut.entity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyElfArcher;
import uut.entity.render.model.ModelToyElfArcher;
import uut.entity.render.model.layer.LayerToyElfArcherCollar;
import uut.entity.render.model.layer.LayerToyElfArcherHeldItem;

@SideOnly(Side.CLIENT)
public class RenderToyElfArcher extends RenderLiving<EntityToyElfArcher>
{
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/elf_archer/toy_elf_archer.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/elf_archer/toy_elf_archer_tamed.png");

    private static final ResourceLocation TEXTURE_HIGH_WILD = new ResourceLocation("uut:textures/model/elf_archer/toy_elf_archer_high.png");
    private static final ResourceLocation TEXTURE_HIGH_TAMED = new ResourceLocation("uut:textures/model/elf_archer/toy_elf_archer_high_tamed.png");

    public RenderToyElfArcher(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToyElfArcher(), 0.35f);
        this.addLayer(new LayerToyElfArcherCollar(this));
        this.addLayer(new LayerToyElfArcherHeldItem(this));
    }

    public static boolean isHighName(String customName) {
        if (customName == null) return false;
        String cleanName = StringUtils.stripControlCodes(customName).trim();
        return "high".equalsIgnoreCase(cleanName) ||
                "high elf".equalsIgnoreCase(cleanName) ||
                "high_elf".equalsIgnoreCase(cleanName);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyElfArcher entity) {
        boolean isHigh = isHighName(entity.getName());

        if (isHigh) {
            return entity.isTamed() ? TEXTURE_HIGH_TAMED : TEXTURE_HIGH_WILD;
        }

        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyElfArcher entity, final float partialTickTime) {
        GlStateManager.scale(0.55f, 0.55f, 0.55f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyElfArcher entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}