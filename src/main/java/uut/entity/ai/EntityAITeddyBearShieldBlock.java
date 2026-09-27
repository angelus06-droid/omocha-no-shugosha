package uut.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import uut.entity.EntityToyTeddyBear;

public class EntityAITeddyBearShieldBlock extends EntityAIBase {
    private final EntityToyTeddyBear bear;
    private int blockTimer = 0;

    public EntityAITeddyBearShieldBlock(EntityToyTeddyBear bear) {
        this.bear = bear;
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        if (this.bear.isSitting()) return false;

        ItemStack offhand = this.bear.getHeldItemOffhand();
        if (offhand.isEmpty() || !(offhand.getItem() instanceof ItemShield)) {
            return false;
        }

        // Si acaba de recibir daño, activa o reinicia la duración del bloqueo (30 ticks = 1.5s)
        if (this.bear.hurtTime > 0) {
            this.blockTimer = 30;
        }

        return this.blockTimer > 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        ItemStack offhand = this.bear.getHeldItemOffhand();
        if (offhand.isEmpty() || !(offhand.getItem() instanceof ItemShield)) {
            return false;
        }

        // Si recibe otro golpe mientras ya está bloqueando, renueva el temporizador
        if (this.bear.hurtTime > 0) {
            this.blockTimer = 30;
        }

        return this.blockTimer > 0 && !this.bear.isSitting();
    }

    @Override
    public void startExecuting() {
        if (!this.bear.isHandActive()) {
            this.bear.setActiveHand(EnumHand.OFF_HAND);
        }
    }

    @Override
    public void resetTask() {
        this.blockTimer = 0;
        if (this.bear.isHandActive() && this.bear.getActiveHand() == EnumHand.OFF_HAND) {
            this.bear.stopActiveHand();
        }
    }

    @Override
    public void updateTask() {
        if (this.blockTimer > 0) {
            this.blockTimer--;
        }

        if (!this.bear.isHandActive()) {
            this.bear.setActiveHand(EnumHand.OFF_HAND);
        }
    }
}