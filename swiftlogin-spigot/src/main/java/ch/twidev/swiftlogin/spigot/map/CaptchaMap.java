/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.map;

import ch.twidev.swiftlogin.common.SwiftLoginPlugin;
import ch.twidev.swiftlogin.common.configuration.schema.BackendConfiguration;
import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

public class CaptchaMap {

    private final SwiftLoginPlugin<?,?> swiftLoginPlugin;
    private final String captchaCode;
    private final boolean check;

    public CaptchaMap(SwiftLoginPlugin<?,?> swiftLoginPlugin, String captchaCode) {
        this.swiftLoginPlugin = swiftLoginPlugin;
        this.captchaCode = captchaCode;
        this.check = this.isVersionable();
    }

    public void get(Player player) {
        MapView mapView = Bukkit.createMap(player.getWorld());
        mapView.getRenderers().clear();

        CaptchaMapRenderer captchaMapRenderer = new CaptchaMapRenderer(captchaCode);
        mapView.getRenderers().forEach(mapView::removeRenderer);
        mapView.addRenderer(captchaMapRenderer);

        ItemStack itemStack = new ItemStack(!check ? Material.MAP : Material.valueOf("MAP"));

        try {
            if (check) {
                ItemStack.class.getMethod("setDurability", Short.TYPE).invoke(itemStack, this.getMapId(mapView));
            } else {
                MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();

                MapMeta.class.getMethod("setMapId", MapView.class).invoke(mapMeta, mapView);

                itemStack.setItemMeta(mapMeta);
            }

            player.getInventory().setItem(BackendConfiguration.CAPTCHA_MAP_SLOT.getValue(), itemStack);
            player.getInventory().setHeldItemSlot(
                    BackendConfiguration.CAPTCHA_MAP_SLOT.getValue()
            );
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException exception) {
            swiftLoginPlugin.getSwiftLogger().severe("Cannot create a captcha map because of");

            throw new RuntimeException(exception);
        }
    }

    private short getMapId(@Nonnull MapView mapView) {
        try {
            return (short) mapView.getId();
        } catch (NoSuchMethodError ex) {
            try {
                return (short) Class.forName("org.bukkit.map.MapView").getMethod("getId", (Class<?>[]) new Class[0])
                        .invoke(mapView, new Object[0]);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                     | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    private boolean isVersionable() {
        return Integer.parseInt(Bukkit.getServer().getBukkitVersion().split("\\.")[1].split("\\.")[0]) < 13;
    }
}
