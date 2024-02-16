/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot;

import ch.twidev.swiftlogin.spigot.map.CaptchaMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum RegistrationState {

    LOGIN,
    REGISTER,
    AUTHORIZED,
    PENDING;

    public static void checkState(RegistrationUser registrationUser, RegistrationState registrationState, Player player) {
        if(registrationState == RegistrationState.PENDING) return;

        if(registrationState == RegistrationState.AUTHORIZED) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);

            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 1, false, false));

            player.setWalkSpeed(0.0F);
            player.setFlySpeed(0.0F);

            if(registrationUser.getCaptcha() != null) {
                new CaptchaMap(SwiftLoginSpigot.getInstance(),registrationUser.getCaptcha()).get(player);
            }
        }
    }

}
