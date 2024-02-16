/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.crypto;

import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoType;
import ch.twidev.swiftlogin.api.crypto.HashedPassword;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;

public class CryptoPassword extends HashedPassword {

    private final String raw;
    private final CryptoType algorithmType;

    public CryptoPassword(String raw, CryptoProvider algorithmType) {
        super(algorithmType);

        this.raw = raw;
        this.algorithmType = algorithmType.getCryptoType();
    }

    @Override
    public String toPasswordString() {
        return algorithmType.getAlgorithmName() + "_" + raw;
    }
    @Override
    public String getRaw() {
        return raw;
    }

    public CryptoType getAlgorithmType() {
        return algorithmType;
    }

    public static HashedPassword fromString(String s) {
        String[] raw = s.split("_");

        try {
            CryptoType cryptoType = CryptoType.valueOf(raw[0].replaceAll("-","_"));
            CryptoProvider cryptoProvider = SwiftLoginImplementation.getInstance().getLoadedCryptoProvider().getOrDefault(cryptoType, null);

            if(cryptoProvider == null) throw new IllegalArgumentException();

            return new CryptoPassword(raw[1], cryptoProvider);
        } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "CryptoPassword{" +
                "raw='" + raw + '\'' +
                ", algorithmType=" + algorithmType +
                '}';
    }
}
