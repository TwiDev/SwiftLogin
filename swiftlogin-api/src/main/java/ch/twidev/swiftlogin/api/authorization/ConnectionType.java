/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.authorization;

public enum ConnectionType {

    SUCCESS_REGISTER("authorizationSuccessRegister"),
    SUCCESS_LOGIN("authorizationSuccessLogin"),
    PREMIUM("authorizationPremium"),
    SESSION("authorizationSession"),
    REGISTER("requireRegister"),
    LOGIN("requireLogin"),
    @Deprecated
    BEDROCK(null);

    private final String translationPrefix;

    ConnectionType(String translationPrefix) {
        this.translationPrefix = translationPrefix;
    }

    public String getTranslationPrefix() {
        return translationPrefix;
    }

}
