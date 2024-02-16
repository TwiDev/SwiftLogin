/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.crypto;

public enum CryptoType {

    BCRYPT("BCRYPT"),
    SHA_256("SHA-256"),
    SHA_512("SHA-512");

    private final String algorithmName;

    CryptoType(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }
}
