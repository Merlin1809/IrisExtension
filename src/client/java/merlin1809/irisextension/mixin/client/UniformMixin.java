package merlin1809.irisextension.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import merlin1809.irisextension.SeasonHelperReflect;
import merlin1809.irisextension.Variables;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.irisshaders.iris.uniforms.IrisExclusiveUniforms;
import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import sereneseasons.api.season.SeasonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.effect.MobEffects;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.util.Optional;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

@Mixin(IrisExclusiveUniforms.class)
public class UniformMixin {
	private static boolean isSereneSeasonsLoaded = merlin1809.irisextension.Variables.isSereneSeasonsLoaded;
   	private static boolean isBeltborneLanternsLoaded = merlin1809.irisextension.Variables.isBeltborneLanternsLoaded;

	private static Method GET_LAMP_METHOD = null;

	private static Item getLampSafe(Player player) {
		if (GET_LAMP_METHOD == null && Variables.isBeltborneLanternsLoaded) {
			try {
				Class<?> clazz = Class.forName("net.oxcodsnet.beltborne_lanterns.common.BeltState");
				GET_LAMP_METHOD = clazz.getMethod("getLamp", Player.class);
			} catch (Exception e) {
				Variables.isBeltborneLanternsLoaded = false;
				return null;
			}
		}
		if (GET_LAMP_METHOD != null) {
			try {
				return (Item) GET_LAMP_METHOD.invoke(null, player);
			} catch (Exception e) {
			}
		}
		return null;
	}

	@Inject(at = @At("HEAD"), method = "addIrisExclusiveUniforms")
	private static void addUniformsNewIris(UniformHolder uniforms, CallbackInfo ci) {
   		for (int i = 0; i < 10; i++) {
			final int index = i;
			uniforms.uniform1b(UniformUpdateFrequency.PER_FRAME, "IEXT_KEY_" + index, () -> merlin1809.irisextension.Variables.keyPressed[index]);
		}

		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_season", () -> {
			ClientLevel level = Minecraft.getInstance().level;
			return level != null && isSereneSeasonsLoaded ? SeasonHelperReflect.getSeasonOrdinal(level) : 0;
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_sub_season", () -> {
			ClientLevel level = Minecraft.getInstance().level;
			return level != null && isSereneSeasonsLoaded ? SeasonHelperReflect.getSubSeasonOrdinal(level) : 0;
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_tropical_season", () -> {
			ClientLevel level = Minecraft.getInstance().level;
			return level != null && isSereneSeasonsLoaded ? SeasonHelperReflect.getTropicalSeasonOrdinal(level) : 0;
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_season_cycle_ticks", () -> {
			ClientLevel level = Minecraft.getInstance().level;
			return level != null && isSereneSeasonsLoaded ? SeasonHelperReflect.getSeasonCycleTicks(level) : 0;
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_season_day_duration", () -> {
			ClientLevel level = Minecraft.getInstance().level;
			return level != null && isSereneSeasonsLoaded ? SeasonHelperReflect.getDayDuration(level) : 0;
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_sub_season_duration", () -> {
			ClientLevel level = Minecraft.getInstance().level;
			return level != null && isSereneSeasonsLoaded ? SeasonHelperReflect.getSubSeasonDuration(level) : 0;
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_season_day", () -> {
			ClientLevel level = Minecraft.getInstance().level;
			return level != null && isSereneSeasonsLoaded ? SeasonHelperReflect.getDay(level) : 0;
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_poison_effect", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.hasEffect(MobEffects.POISON);
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_wither_effect", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.hasEffect(MobEffects.WITHER);
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_frozen", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.isFreezing();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_hunger_effect", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.hasEffect(MobEffects.HUNGER);
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_speed_effect", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.hasEffect(MobEffects.MOVEMENT_SPEED);
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_slowness_effect", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_regeneration_effect", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.hasEffect(MobEffects.REGENERATION);
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_sleeping", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				return player.isSleeping();
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_experience_level", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				return player.experienceLevel;
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_experience_total", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				return player.totalExperience;
			}
		});
		uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "IEXT_experience_progress", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0.0f;
			} else {
				return player.experienceProgress;
			}
		});
		uniforms.uniform3f(PER_FRAME, "IEXT_relative_death_position", () -> {
			Player player = Minecraft.getInstance().player;
			if (player != null) {
				Optional<GlobalPos> deathLocation = player.getLastDeathLocation();
				boolean sameDim = deathLocation.map(GlobalPos::dimension).map(dim -> dim.equals(player.level().dimension())).orElse(false);

				if(sameDim) {
					BlockPos pos = deathLocation.get().pos();
					return new Vec3(pos.getX(), pos.getY(), pos.getZ()).subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()).toVector3f();
				}
			}

			return new Vector3f(0.0f);
		});
		uniforms.uniform3f(PER_FRAME, "IEXT_relative_hook_position", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return new Vector3f(0.0f);
			} else {
				FishingHook hook = player.fishing;
				if(hook == null) {
					return new Vector3f(0.0f);
				} else {
					return hook.position().subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()).toVector3f();
				}
			}
		});
		uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "IEXT_attack_cooldown", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0.0f;
			} else {
				return player.getAttackStrengthScale(0.0f);
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_head_Id", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
				Object2IntFunction<NamespacedId> itemId = WorldRenderingSettings.INSTANCE.getItemIds();
				if (itemId == null) {
				return 0;
				} else {
				ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
				return itemId.applyAsInt(new NamespacedId(id.getNamespace(), id.getPath()));
				}
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_chest_Id", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
				Object2IntFunction<NamespacedId> itemId = WorldRenderingSettings.INSTANCE.getItemIds();
				if (itemId == null) {
				return 0;
				} else {
				ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
				return itemId.applyAsInt(new NamespacedId(id.getNamespace(), id.getPath()));
				}
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_legs_Id", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.LEGS);
				Object2IntFunction<NamespacedId> itemId = WorldRenderingSettings.INSTANCE.getItemIds();
				if (itemId == null) {
				return 0;
				} else {
				ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
				return itemId.applyAsInt(new NamespacedId(id.getNamespace(), id.getPath()));
				}
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_feet_Id", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
				Object2IntFunction<NamespacedId> itemId = WorldRenderingSettings.INSTANCE.getItemIds();
				if (itemId == null) {
				return 0;
				} else {
				ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
				return itemId.applyAsInt(new NamespacedId(id.getNamespace(), id.getPath()));
				}
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_head_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
				return stack.hasFoil();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_chest_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
				return stack.hasFoil();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_legs_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.LEGS);
				return stack.hasFoil();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_feet_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
				return stack.hasFoil();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_hand_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.MAINHAND);
				return stack.hasFoil();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_offhand_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.OFFHAND);
				return stack.hasFoil();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_vehicle_armor_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				Entity vehicle = player.getVehicle();
				if (vehicle == null) {
				return false;
				} else if (vehicle instanceof Mob mob) {
				return mob.getItemBySlot(EquipmentSlot.BODY).hasFoil();
				} else {
				return false;
				}
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_vehicle_saddle_enchanted", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				Entity vehicle = player.getVehicle();
				if (vehicle == null) {
					return false;
				} else if (vehicle instanceof AbstractHorse horse) {
					return horse.getSlot(400).get().hasFoil();
				} else {
					return false;
				}
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_beltborne_lanterns_Id", () -> {
			Player player = Minecraft.getInstance().player;
			if (player != null && isBeltborneLanternsLoaded) {
				Item lamp = getLampSafe(player);
				if (lamp == null) {
				return 0;
				} else {
				Object2IntFunction<NamespacedId> itemId = WorldRenderingSettings.INSTANCE.getItemIds();
				if (itemId == null) {
					return 0;
				} else {
					ResourceLocation id = BuiltInRegistries.ITEM.getKey(lamp);
					return itemId.applyAsInt(new NamespacedId(id.getNamespace(), id.getPath()));
				}
				}
			} else {
				return 0;
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_vehicle_armor_Id", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				Entity vehicle = player.getVehicle();
				if (vehicle instanceof Mob mob) {
					Object2IntFunction<NamespacedId> itemId = WorldRenderingSettings.INSTANCE.getItemIds();
					if (itemId == null) {
						return 0;
					} else {
						ResourceLocation id = BuiltInRegistries.ITEM.getKey(mob.getItemBySlot(EquipmentSlot.BODY).getItem());
						return itemId.applyAsInt(new NamespacedId(id.getNamespace(), id.getPath()));
					}
				} else {
					return 0;
				}
			}
		});
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_vehicle_saddle_Id", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return 0;
			} else {
				Entity vehicle = player.getVehicle();
				if (vehicle instanceof AbstractHorse horse) {
					Object2IntFunction<NamespacedId> itemId = WorldRenderingSettings.INSTANCE.getItemIds();
					if (itemId == null) {
						return 0;
					} else {
						ResourceLocation id = BuiltInRegistries.ITEM.getKey(horse.getSlot(400).get().getItem());
						return itemId.applyAsInt(new NamespacedId(id.getNamespace(), id.getPath()));
					}
				} else {
					return 0;
				}
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_vehicle_underwater", () -> {
			Player player = Minecraft.getInstance().player;
			if (player == null) {
				return false;
			} else {
				Entity vehicle = player.getVehicle();
				return vehicle == null ? false : vehicle.isUnderWater();
			}
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_underwater", () -> {
			Player player = Minecraft.getInstance().player;
			return player == null ? false : player.isUnderWater();
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_crouching", () -> {
			Player player = Minecraft.getInstance().player;
			return player == null ? false : player.isCrouching();
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_crawling", () -> {
			Player player = Minecraft.getInstance().player;
			return player == null ? false : player.isVisuallyCrawling();
		});
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_using_spyglass", () -> {
			Player player = Minecraft.getInstance().player;
			return player == null ? false : player.isScoping();
		});

		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_first_dragon_killed", () -> merlin1809.irisextension.Variables.firstdragonKilled);
		uniforms.uniform1b(UniformUpdateFrequency.PER_TICK, "IEXT_ender_dragon_fight", () -> merlin1809.irisextension.Variables.dragonAlive);
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_ender_dragon_crystals", () -> merlin1809.irisextension.Variables.crystalsAmount);
		uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "IEXT_ender_dragon_players", () -> merlin1809.irisextension.Variables.playersCountDragon);
		uniforms.uniform1f(UniformUpdateFrequency.PER_TICK, "IEXT_ender_dragon_progress", () -> merlin1809.irisextension.Variables.dragonProgress);
	}
}