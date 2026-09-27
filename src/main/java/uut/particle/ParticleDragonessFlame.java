package uut.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleDragonessFlame extends Particle {

    private static final ResourceLocation TEXTURE = new ResourceLocation("uut", "textures/particle/dragoness_flame.png");

    private float particleAngle;
    private float prevParticleAngle;
    private float rotationSpeed;

    public ParticleDragonessFlame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);

        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;

        this.particleScale = 1.2F + this.rand.nextFloat() * 0.5F;
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;

        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleAlpha = 1.0F;

        this.particleAngle = this.rand.nextFloat() * (float) Math.PI * 2.0F;
        this.prevParticleAngle = this.particleAngle;
        this.rotationSpeed = (this.rand.nextFloat() - 0.5F) * 0.2F;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += this.rotationSpeed;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.move(this.motionX, this.motionY, this.motionZ);

        this.motionX *= 0.96D;
        this.motionY *= 0.96D;
        this.motionZ *= 0.96D;

        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
        }

        float lifeFraction = 1.0F - ((float) this.particleAge / (float) this.particleMaxAge);

        this.particleAlpha = lifeFraction;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        double interpX = this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX;
        double interpY = this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY;
        double interpZ = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ;

        float currentAngle = this.prevParticleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;

        float agePartial = (float) this.particleAge + partialTicks;
        float alphaInterp = 1.0F - (agePartial / (float) this.particleMaxAge);
        if (alphaInterp < 0.0F) alphaInterp = 0.0F;
        if (alphaInterp > 1.0F) alphaInterp = 1.0F;

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(interpX, interpY, interpZ);

        GlStateManager.rotate(-entityIn.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entityIn.rotationPitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.rotate(currentAngle * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);

        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        float scale = 0.1F * this.particleScale;

        int skyLight = 240;
        int blockLight = 240;

        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.pos(-scale, -scale, 0.0D).tex(0.0D, 1.0D).color(this.particleRed, this.particleGreen, this.particleBlue, alphaInterp).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(-scale, scale, 0.0D).tex(0.0D, 0.0D).color(this.particleRed, this.particleGreen, this.particleBlue, alphaInterp).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(scale, scale, 0.0D).tex(1.0D, 0.0D).color(this.particleRed, this.particleGreen, this.particleBlue, alphaInterp).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(scale, -scale, 0.0D).tex(1.0D, 1.0D).color(this.particleRed, this.particleGreen, this.particleBlue, alphaInterp).lightmap(skyLight, blockLight).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}