/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

import ch.twidev.swiftlogin.api.SwiftLogin;
import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.event.SwiftEventProvider;
import ch.twidev.swiftlogin.api.players.SwiftPlayer;
import ch.twidev.swiftlogin.api.players.SwiftPlayerManager;
import ch.twidev.swiftlogin.api.servers.SwiftServerManager;
import ch.twidev.swiftlogin.common.SwiftLogger;
import ch.twidev.swiftlogin.common.SwiftLoginPlugin;
import ch.twidev.swiftlogin.common.configuration.ConfigurationFiles;
import ch.twidev.swiftlogin.common.events.AbstractEventsProvider;
import ch.twidev.swiftlogin.common.exception.PluginConfigurationException;
import ch.twidev.swiftlogin.common.player.Profile;
import ch.twidev.swiftlogin.common.player.ProfileTemplate;
import ch.twidev.swiftlogin.common.servers.ServerType;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

public class EventsBridgeTests {

    @Test
    public void encodeEvent() {
        Player player = new Player("test", UUID.randomUUID());
        SwiftPlayer swiftPlayer = new Swift(null, player.getPlayerUuid());

        AbstractEventsProvider<Player> eventsProvider = new AbstractEventsProvider<Player>(Player.class) {
            @Override
            public String getUniqueIdentifier(Player player) {
                return player.getPlayerUuid().toString();
            }


            @Override
            public Player getFromUniqueIdentifier(String s) {
                return new Player(s, UUID.randomUUID());
            }
        };


    /*    PlayerAuthenticatedEvent<Player> playerPlayerAuthenticatedEvent = new PlayerAuthenticatedEvent<>(
                swiftPlayer,
                player,
                AuthenticatedReason.SESSION
        );

        String jsonEvent = eventsProvider.getEventsGson().toJson(playerPlayerAuthenticatedEvent);

        Assert.assertEquals(
                jsonEvent,
                String.format("{\"authenticatedReason\":\"SESSION\",\"cancelledReason\":\"Your action has been cancelled by an external agent.\",\"isCancelled\":false,\"swiftPlayer\":\"%s\",\"player\":\"%s\",\"eventName\":\"ch.twidev.swiftlogin.api.event.events.PlayerAuthenticatedEvent\",\"async\":false}",
                        player.getPlayerUuid().toString(), player.getPlayerUuid().toString()));

        try {
            System.out.println(
                    eventsProvider.getEventsGson().fromJson(jsonEvent, (Type) eventsProvider.getRegisteredEvents().get("ch.twidev.swiftlogin.api.event.events.PlayerAuthenticatedEvent").getConstructor(Class.class).newInstance(Player.class)).toString()
            );
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
*/
        // PlayerAuthenticatedEvent{authenticatedReason=SESSION, cancelledReason='Your action has been cancelled by an external agent.', isCancelled=false, player=Player{playerName='a71f7185-4308-49ae-87d3-3baf124fba79', playerUuid=7262ea11-bae5-463d-a216-68b0872f9758}, swiftPlayer=null}
    }

    @Test
    public void generatedConfiguration() throws Exception {
        SwiftLoginPlugin<Object, Object> swiftLoginPlugin = new SwiftLoginPlugin<Object, Object>() {
            @Override
            public SwiftLogger getSwiftLogger() {
                return new SwiftLogger(Logger.getLogger("[TestLogger]"));
            }

            @Override
            public ServerType getServerType() {
                return null;
            }

            @Override
            public InputStream getResourceFile(String filename) {
                return null;
            }

            @Override
            public File getDataFolder() {
                return null;
            }

            @Override
            public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
                return null;
            }

            @Override
            public void disable() {

            }

            @Override
            public void reloadConfiguration() throws PluginConfigurationException {

            }

            @Override
            public SwiftLogin<Object> getImplementation() {
                return new SwiftLogin<Object>() {
                    @Override
                    public Logger getLogger() {
                        return new SwiftLogger(Logger.getLogger("[TestLogger]"));
                    }

                    @Override
                    public SwiftPlayerManager getProfileManager() {
                        return null;
                    }

                    @Override
                    public SwiftServerManager getServerManager() {
                        return null;
                    }

                    @Override
                    public SwiftEventProvider<Object> getEventProvider() {
                        return null;
                    }

                    @Override
                    public CryptoProvider getCurrentCryptoProvider() {
                        return null;
                    }

                    @Override
                    public CryptoProvider getBCryptCryptoProvider() {
                        return null;
                    }

                    @Override
                    public CryptoProvider getMessageDigest256Provider() {
                        return null;
                    }

                    @Override
                    public CryptoProvider getMessageDigest512Provider() {
                        return null;
                    }

                    @Override
                    public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
                        return null;
                    }
                };
            }
        };

        Instant instant = Instant.now();

        ConfigurationFiles.CONFIG.generatedExampleConfiguration(swiftLoginPlugin, instant);
        ConfigurationFiles.BACKEND_CONFIG.generatedExampleConfiguration(swiftLoginPlugin, instant);
        ConfigurationFiles.LANG.generatedExampleConfiguration(swiftLoginPlugin, instant);

    }

    public static class Swift extends Profile{

        private final UUID profileUUID;

        public Swift(ProfileTemplate profileTemplate, UUID profileUUID) {
            super(profileTemplate, profileUUID);

            this.profileUUID = profileUUID;
        }

        @Override
        public UUID getUniqueId() {
            return profileUUID;
        }


    }

    public static class Player {
        
        private final String playerName;
        private final UUID playerUuid;

        public Player(String playerName, UUID playerUuid) {
            this.playerName = playerName;
            this.playerUuid = playerUuid;
        }

        public String getPlayerName() {
            return playerName;
        }

        public UUID getPlayerUuid() {
            return playerUuid;
        }

        @Override
        public String toString() {
            return "Player{" +
                    "playerName='" + playerName + '\'' +
                    ", playerUuid=" + playerUuid +
                    '}';
        }
    }

}
