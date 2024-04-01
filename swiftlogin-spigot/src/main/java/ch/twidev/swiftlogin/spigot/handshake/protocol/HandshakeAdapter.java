/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.handshake.protocol;

import ch.twidev.swiftlogin.spigot.SwiftLoginSpigot;
import ch.twidev.swiftlogin.spigot.handshake.Handshaker;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class HandshakeAdapter extends PacketAdapter {

    private final SwiftLoginSpigot spigot;

    public HandshakeAdapter(SwiftLoginSpigot spigot) {
        super(spigot, ListenerPriority.HIGHEST, PacketType.Handshake.Client.SET_PROTOCOL);

        this.spigot = spigot;
    }

    public static void init(SwiftLoginSpigot plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new HandshakeAdapter(plugin));
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packetContainer = event.getPacket();

        try {
            if (!packetContainer.getProtocols().read(0).equals(PacketType.Protocol.LOGIN)) {
                return;
            }

            Handshaker handshaker = SwiftLoginSpigot.parseHandshake(
                    packetContainer.getStrings().read(0)
            );

            if (handshaker != null) {
                packetContainer.getStrings().write(0, '\u0000' + handshaker.getAddress() + '\u0000' + handshaker.getProfileUuid().toString() + '\u0000' + handshaker.getProperties());
            } else {
                packetContainer.getStrings().write(0, "null");
                PacketContainer kickPacket = new PacketContainer(PacketType.Login.Server.DISCONNECT);
                kickPacket.getModifier().writeDefaults();
                kickPacket.getChatComponents().write(0, WrappedChatComponent.fromText("Please join the server with correct address (2)"));
                ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), kickPacket);
            }
        } catch (NullPointerException e) {
            packetContainer.getStrings().write(0, "null");
            PacketContainer kickPacket = new PacketContainer(PacketType.Login.Server.DISCONNECT);
            kickPacket.getModifier().writeDefaults();
            kickPacket.getChatComponents().write(0, WrappedChatComponent.fromText("Please join the server with correct address (2)"));
            ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), kickPacket);
        }
    }
}
