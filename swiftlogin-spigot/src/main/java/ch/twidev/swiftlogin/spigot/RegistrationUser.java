/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot;

import java.util.UUID;

public class RegistrationUser {

    private final UUID uuid;

    private final String name;

    private RegistrationState registrationState = RegistrationState.PENDING;

    private String captcha = "Unknown";

    private boolean logged = false;

    public RegistrationUser(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public RegistrationState getRegistrationState() {
        return registrationState;
    }

    public void setRegistrationState(RegistrationState registrationState) {
        this.registrationState = registrationState;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public String toString() {
        return "RegistrationUser{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", registrationState=" + registrationState +
                ", captcha='" + captcha + '\'' +
                '}';
    }
}
