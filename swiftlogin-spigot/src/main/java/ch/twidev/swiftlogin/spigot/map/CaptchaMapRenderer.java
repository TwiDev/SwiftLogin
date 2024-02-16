/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.map;

import ch.twidev.swiftlogin.spigot.SwiftLoginSpigot;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CaptchaMapRenderer extends MapRenderer {

    private final String captchaCode;
    private boolean isRendered = false;
    private BufferedImage bufferedImage;

    public CaptchaMapRenderer(String captchaCode) {
        this.captchaCode = captchaCode;

        try {
            this.bufferedImage = ImageIO.read(new File(
                    SwiftLoginSpigot.getInstance().getDataFolder(), "logo.png"
            ));

            if(bufferedImage.getWidth() > 128 || bufferedImage.getHeight() > 96) {
                this.bufferedImage = null;
                SwiftLoginSpigot.getInstance().getLogger().severe("Cannot load captcha image because maximum supported resolution is 128x96");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (isRendered) return;

        if (player != null && player.isOnline()) {
            String captchaVerification = "Your captcha code is";

            int centeredText = (128 - MinecraftFont.Font.getWidth(captchaVerification)) / 2;
            int centeredTextCaptcha = (128 - MinecraftFont.Font.getWidth(captchaCode)) / 2;

            if(bufferedImage != null) {
                mapCanvas.drawImage((128 - bufferedImage.getWidth()) / 2, ((96 - bufferedImage.getHeight()) / 2) + 32, bufferedImage);
            }
            mapCanvas.drawText(centeredText, 10, MinecraftFont.Font, captchaVerification);
            mapCanvas.drawText(centeredTextCaptcha, 20, MinecraftFont.Font, captchaCode);
        }

        this.isRendered = true;
    }
}
