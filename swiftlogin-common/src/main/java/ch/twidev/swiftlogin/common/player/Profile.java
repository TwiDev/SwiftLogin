/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.player;

import ch.twidev.swiftlogin.api.crypto.HashedPassword;
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.common.crypto.CryptoPassword;

import java.util.UUID;

public class Profile implements SwiftPlayer {

    private final UUID profileUUID;
    private final ProfileTemplate profileTemplate;

    public Profile(ProfileTemplate template) {
        this.profileTemplate = template;
        this.profileUUID = template.getUniqueId();

        ProfileManager.getCachedProfiles().put(profileUUID, this);
    }

    public Profile(ProfileTemplate profileTemplate, UUID profileUUID) {
        this.profileUUID = profileUUID;
        this.profileTemplate = profileTemplate;
    }

    public ProfileTemplate getProfileTemplate() {
        return profileTemplate;
    }

    @Override
    public UUID getUniqueId() {
        return profileTemplate.getUniqueId();
    }

    @Override
    public UUID getPremiumId() {
        return profileTemplate.getPremiumId();
    }

    @Override
    public String getRecentName() {
        return profileTemplate.getRecentName();
    }

    @Override
    public HashedPassword getHashedPassword() {
        final String raw = profileTemplate.getHashedPassword();
        if(raw == null) return null;

        return CryptoPassword.fromString(raw);
    }

    @Override
    public String getRecentServer() {
        return profileTemplate.getRecentServer();
    }

    @Override
    public String getLastAddress() {
        return profileTemplate.getRecentAddress();
    }

    @Override
    public long getRecentSeen() {
        return profileTemplate.getRecentSeen();
    }

    @Override
    public String getFirstAddress() {
        return profileTemplate.getFirstAddress();
    }

    @Override
    public long getFirstSeen() {
        return profileTemplate.getFirstSeen();
    }

    @Override
    public boolean isPremium() {
        return profileTemplate.getPremiumId() != null;
    }

    @Override
    public boolean isRegistered() {
        return isPremium() || getHashedPassword() != null;
    }

    @Override
    public boolean isLogged() {
        return profileTemplate.isLogged();
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean exists() {
        return profileTemplate.isExists();
    }

    @Override
    public long getCurrentSession() {
        return profileTemplate.getCurrentSession();
    }

    @Override
    public boolean hasCurrentSession() {
        return getCurrentSession() != 0L;
    }

    @Override
    public void setCurrentName(String name) {
        profileTemplate.set("recentName", name);
    }

    @Override
    public void setCurrentAddress(String name) {
        profileTemplate.set("recentAddress", name);
    }

    @Override
    public void setCurrentServer(String name) {
        profileTemplate.set("recentServer", name);
    }

    @Override
    public void setHashedPassword(HashedPassword name) {
        profileTemplate.set("hashedPassword", name.toPasswordString());
    }

    @Override
    public boolean hasRecentName() {
        return this.getRecentName() != null;
    }

    @Override
    public boolean hasRecentServer() {
        return profileTemplate.getRecentServer() != null;
    }

    @Override
    public void save() {

    }

    @Override
    public void setLogged(boolean v) {
        profileTemplate.set("isLogged", v);
    }

    @Override
    public String toRawString() {
        return profileTemplate.toString();
    }

    @Override
    public String getCachedCaptcha() {
        return profileTemplate.getCachedCaptcha();
    }

    @Override
    public void setCachedCaptcha(String captcha) {
        profileTemplate.set("cachedCaptcha", captcha);
    }

}
