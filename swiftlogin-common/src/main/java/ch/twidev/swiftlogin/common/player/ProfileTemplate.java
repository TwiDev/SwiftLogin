/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.player;

import ch.twidev.swiftlogin.common.database.sql.SQLTableTemplate;

import java.util.UUID;

public class ProfileTemplate extends SQLTableTemplate<UUID> {

    @Column(columnName = "uniqueId", columnType = ColumnType.VARCHAR, isNull = false)
    private UUID uniqueId;

    @Column(columnName = "premiumId", columnType = ColumnType.VARCHAR)
    private UUID premiumId = null;

    @Column(columnName = "recentName", columnType = ColumnType.VARCHAR)
    private String recentName = null;

    @Column(columnName = "recentServer", columnType = ColumnType.VARCHAR)
    private String recentServer = null;

    @Column(columnName = "recentAddress", columnType = ColumnType.VARCHAR)
    private String recentAddress = null;

    @Column(columnName = "recentSeen", columnType = ColumnType.BIGINT)
    private long recentSeen = 0L;

    @Column(columnName = "currentSession", columnType = ColumnType.BIGINT)
    private long currentSession = 0L;

    @Column(columnName = "firstAddress", columnType = ColumnType.VARCHAR)
    private String firstAddress = null;

    @Column(columnName = "firstSeen", columnType = ColumnType.BIGINT)
    private long firstSeen = 0L;

    @Column(columnName = "hashedPassword", columnType = ColumnType.TEXT)
    private String hashedPassword = null;

    @Column(columnName = "cachedCaptcha", columnType = ColumnType.VARCHAR)
    private String cachedCaptcha = null;

    @Column(columnName = "isLogged", columnType = ColumnType.BOOLEAN)
    private boolean isLogged = false;

    public ProfileTemplate() {
        super("swiftlogin_profiles", "uniqueId");
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getPremiumId() {
        return premiumId;
    }

    public String getRecentName() {
        return recentName;
    }

    public String getRecentServer() {
        return recentServer;
    }

    public String getRecentAddress() {
        return recentAddress;
    }

    public long getRecentSeen() {
        return recentSeen;
    }

    public String getFirstAddress() {
        return firstAddress;
    }

    public long getFirstSeen() {
        return firstSeen;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public long getCurrentSession() {
        return currentSession;
    }

    public String getCachedCaptcha() {
        return cachedCaptcha;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setRecentName(String recentName) {
        this.recentName = recentName;
    }

    public void setRecentServer(String recentServer) {
        this.recentServer = recentServer;
    }

    public void setRecentAddress(String recentAddress) {
        this.recentAddress = recentAddress;
    }

    public void setRecentSeen(long recentSeen) {
        this.recentSeen = recentSeen;
    }

    public void setCurrentSession(long currentSession) {
        this.currentSession = currentSession;
    }

    public void setFirstAddress(String firstAddress) {
        this.firstAddress = firstAddress;
    }

    public void setFirstSeen(long firstSeen) {
        this.firstSeen = firstSeen;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setCachedCaptcha(String cachedCaptcha) {
        this.cachedCaptcha = cachedCaptcha;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    @Override
    public String toString() {
        return "ProfileTemplate{" +
                "uniqueId=" + uniqueId +
                ", premiumId=" + premiumId +
                ", recentName='" + recentName + '\'' +
                ", recentServer='" + recentServer + '\'' +
                ", recentAddress='" + recentAddress + '\'' +
                ", recentSeen=" + recentSeen +
                ", currentSession=" + currentSession +
                ", firstAddress='" + firstAddress + '\'' +
                ", firstSeen=" + firstSeen +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", cachedCaptcha='" + cachedCaptcha + '\'' +
                ", isLogged=" + isLogged +
                '}';
    }
}
