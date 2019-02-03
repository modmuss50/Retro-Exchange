package me.modmuss50.retroexchange.mixin;

import me.modmuss50.retroexchange.RetroExchange;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> entityType_1, World world_1) {
		super(entityType_1, world_1);
	}

	@Inject(method = "onDeath", at = @At("RETURN"))
	private void onDeath(DamageSource damageSource, CallbackInfo info) {
		if (((LivingEntity)(Object)this) instanceof HostileEntity) {
			if (damageSource.getAttacker() instanceof PlayerEntity) {
				if (random.nextInt(RetroExchange.dropChance) == 0) {
					dropItem(RetroExchange.transmutationShard, 1);
				}
			}
		}
	}

}
