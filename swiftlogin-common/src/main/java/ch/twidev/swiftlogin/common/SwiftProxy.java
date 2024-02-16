/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common;

import ch.twidev.swiftlogin.api.SwiftLogin;
import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoType;
import ch.twidev.swiftlogin.common.configuration.Configuration;
import ch.twidev.swiftlogin.common.configuration.helpers.TranslationHandler;
import ch.twidev.swiftlogin.common.configuration.schema.MainConfiguration;
import ch.twidev.swiftlogin.common.exception.CryptoProviderNotSupportedException;
import ch.twidev.swiftlogin.common.exception.PluginIssues;
import co.aikar.commands.CommandManager;

import java.util.logging.Level;

public interface SwiftProxy<P, S, C> extends SwiftLoginPlugin<P, S> {


    Configuration getMainConfiguration();
    Configuration getTranslationConfiguration();

    CommandManager<?, ?, ?, ?, ?, ?> getCommandProvider();

    TranslationHandler<P, C> getTranslationHandler();

    CryptoProvider getDefaultCryptoProvider();

    @Override
    SwiftLoginImplementation<P,S> getImplementation();

    default CryptoProvider loadCryptoProvider(SwiftLoginImplementation<P,S> swiftLoginImplementation) {
        String hashingType = MainConfiguration.getPasswordHashingType();

        try {
            CryptoType cryptoType = CryptoType.valueOf(hashingType);

            if(!swiftLoginImplementation.getLoadedCryptoProvider().containsKey(cryptoType)) {
                throw new CryptoProviderNotSupportedException(cryptoType);
            }

            return swiftLoginImplementation.getLoadedCryptoProvider().get(cryptoType);
        } catch (IllegalArgumentException ignored) {
            this.getSwiftLogger().sendPluginError(
                    Level.SEVERE,
                    PluginIssues.CRYPTO_ALGORITHM_NOT_FOUND,
                    PluginIssues.CRYPTO_ALGORITHM_NOT_FOUND.getMessage(),
                    hashingType);
        } catch (CryptoProviderNotSupportedException e) {
            this.getSwiftLogger().sendPluginError(
                    Level.SEVERE,
                    PluginIssues.CRYPTO_ALGORITHM_NOT_SUPPORTED,
                    PluginIssues.CRYPTO_ALGORITHM_NOT_SUPPORTED.getMessage(),
                    e.getCryptoType().getAlgorithmName()
            );
        }

        return swiftLoginImplementation.getBCryptCryptoProvider();
    }


}
