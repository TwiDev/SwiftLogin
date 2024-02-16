/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api;

import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.event.SwiftEventProvider;
import ch.twidev.swiftlogin.api.players.SwiftPlayerManager;
import ch.twidev.swiftlogin.api.servers.SwiftServerManager;
import ch.twidev.swiftlogin.api.utils.Nullable;

import java.util.logging.Logger;

public abstract class SwiftLogin<P> {

    public abstract Logger getLogger();

    public abstract SwiftPlayerManager getProfileManager();

    public abstract SwiftServerManager getServerManager();

    public abstract @Nullable SwiftEventProvider<P> getEventProvider();

    public abstract CryptoProvider getCurrentCryptoProvider();

    public abstract CryptoProvider getBCryptCryptoProvider();

    public abstract CryptoProvider getMessageDigest256Provider();

    public abstract CryptoProvider getMessageDigest512Provider();

    public abstract String translateAlternateColorCodes(char altColorChar, String textToTranslate);
}
