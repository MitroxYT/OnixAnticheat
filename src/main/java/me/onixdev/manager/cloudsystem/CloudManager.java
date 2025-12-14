package me.onixdev.manager.cloudsystem;

import dev.onixac.api.check.ICheck;
import libs.json.JSONObject;
import me.onixdev.OnixAnticheat;
import me.onixdev.onixcloud.base.BasePacket;
import me.onixdev.onixcloud.base.IClientBoundPacketHandler;
import me.onixdev.onixcloud.impl.s2.CloudDataS2Packet;
import me.onixdev.onixcloud.impl.s2.PlayerDisconnectS2Packet;
import me.onixdev.user.OnixUser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.UUID;

public class CloudManager extends WebSocketClient implements IClientBoundPacketHandler {
    private boolean enabled = true;
    private boolean connected = false;
    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the specified URI. The
     * channel does not attampt to connect automatically. The connection will be established once you
     * call <var>connect</var>.
     *
     * @param serverUri the server URI to connect to
     */
    public CloudManager(URI serverUri) {
        super(serverUri);
    }
    public void handleTick(boolean onLoad) {
        OnixAnticheat.INSTANCE.getCloudCheckExecuter().run(() -> {
            if (enabled) {
                if (onLoad) connect();
                if (OnixAnticheat.INSTANCE.getTicksFromStart() % 30 ==0) {
                    if (isOpen()) {
                        connected = true;
                    } else {
                        reconnect();
                        System.out.println("Reconnect");
                    }
                }
            }
        });
    }
    public void sendPacket(String packet) {
        OnixAnticheat.INSTANCE.getCloudCheckExecuter().run(() -> {
            if (connected) send(packet);
        });
    }

    /**
     * Called after an opening handshake has been performed and the given websocket is ready to be
     * written on.
     *
     * @param handshakedata The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        connected = true;
    }

    public boolean isConnected() {
        return connected && enabled;
    }

    /**
     * Callback for string messages received from the remote host
     *
     * @param message The UTF-8 decoded message that was received.
     * @see #onMessage(ByteBuffer)
     **/
    @Override
    public void onMessage(String message) {
        if (!connected) {
            OnixAnticheat.INSTANCE.getPlugin().getLogger().warning("Как это возможно?");
            return;
        }
        OnixAnticheat.INSTANCE.getCloudCheckExecuter().run(() ->{
            JSONObject json = new JSONObject(message);
            if (!json.has("id") || !json.has("sender") || !json.has("bound")) {
                System.out.println("Invalid packet: no id");
                close();
                connected = false;
                return;
            }
            String bound = json.getString("bound");
            if (bound != null && !bound.isEmpty() && !bound.equals("SERVER")) {
                close();
                return;
            }
            int id = json.getInt("id");
            BasePacket packet;
            switch (id) {
                case 1 -> {
                    packet = new PlayerDisconnectS2Packet(json);
                    handle((PlayerDisconnectS2Packet) packet);
                }
                case 2-> {
                    packet = new CloudDataS2Packet(json);
                    handle((CloudDataS2Packet) packet);
                }
            }
        });
        System.out.println(message);
    }

    /**
     * Called after the websocket connection has been closed.
     *
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     **/
    @Override
    public void onClose(int code, String reason, boolean remote) {
        connected = false;
        System.out.println(reason + " code: " + code);
    }

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail {@link
     * #onClose(int, String, boolean)} will be called additionally.<br> This method will be called
     * primarily because of IO or protocol errors.<br> If the given exception is an RuntimeException
     * that probably means that you encountered a bug.<br>
     *
     * @param ex The exception causing this error
     **/
    @Override
    public void onError(Exception ex) {
        connected = false;
        System.out.println(ex);
    }

    @Override
    public void handle(PlayerDisconnectS2Packet playerDisconnectS2Packet) {

    }

    @Override
    public void handle(CloudDataS2Packet cloudDataS2Packet) {
     UUID uuid  = cloudDataS2Packet.getSender();
     OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(uuid);
     if (user != null) {
         String checkname = cloudDataS2Packet.getCheckName();
         String checktype = cloudDataS2Packet.getCheckType();
         System.out.println(checkname + " " + checktype);
         ICheck check = user.getCheck(checkname, checktype);
         if (check != null) {
             check.fail(cloudDataS2Packet.getVerbose());
         }
     }
    }
}
