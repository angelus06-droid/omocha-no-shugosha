package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.projectiles.EntityTechnoDemonRocket;
import uut.entity.render.model.ModelTankMissle;

@SideOnly(Side.CLIENT)
public class RenderTechnoDemonRocket extends Render<EntityTechnoDemonRocket> {
    private final ModelTankMissle model = new ModelTankMissle();
    private static final ResourceLocation TEXTURE = new ResourceLocation("uut:textures/model/technodemon_rocket.png");

    public RenderTechnoDemonRocket(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityTechnoDemonRocket entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();

        GlStateManager.translate((float)x, (float)y - 1.25F, (float)z);

        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(yaw + 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.enableRescaleNormal();

        this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTechnoDemonRocket entity) {
        return TEXTURE;
    }
}