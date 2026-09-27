package uut.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import uut.entity.EntityToySandroneDoll;

public class EntityAIRetreatToHeal extends EntityAIBase {
    private final EntityToySandroneDoll entity;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private final double movementSpeed;

    public EntityAIRetreatToHeal(EntityToySandroneDoll entityIn, double movementSpeedIn) {
        this.entity = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexBits(1); // Bloquea el movimiento de ataque/wander estándar
    }

    @Override
    public boolean shouldExecute() {
        // Ejecuta si la vida está por debajo del 50% y tiene comida en la mano izquierda
        if (this.entity.getHealth() < this.entity.getMaxHealth() * 0.5F) {
            ItemStack offhand = this.entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            if (!offhand.isEmpty() && offhand.getItem() instanceof ItemFood) {
                // Si tiene un objetivo cerca, busca una posición en dirección opuesta (retroceder)
                if (this.entity.getAttackTarget() != null) {
                    Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(
                            this.entity, 16, 7, this.entity.getAttackTarget().getPositionVector()
                    );
                    if (vec3d == null) {
                        return false;
                    }
                    this.shelterX = vec3d.x;
                    this.shelterY = vec3d.y;
                    this.shelterZ = vec3d.z;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        // Continúa retrocediendo mientras no haya llegado al destino y siga estando baja de vida con comida
        ItemStack offhand = this.entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        return !this.entity.getNavigator().noPath()
                && this.entity.getHealth() < this.entity.getMaxHealth()
                && !offhand.isEmpty()
                && offhand.getItem() instanceof ItemFood;
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }
}