package com.colacelli.ircbot;

import com.colacelli.irclib.actors.IrcChannel;
import com.colacelli.irclib.actors.IrcUser;
import com.colacelli.irclib.connection.IrcConnection;
import com.colacelli.irclib.connection.IrcServer;
import com.colacelli.irclib.connection.listeners.*;
import com.colacelli.irclib.messages.IrcChannelMessage;
import com.colacelli.irclib.messages.IrcPrivateMessage;

import java.io.IOException;
import java.util.Arrays;

public class IrcBot {
    static IrcConnection ircConnection = new IrcConnection();

    public static void main(String[] args) throws Exception {

        IrcServer.Builder ircServerBuilder = new IrcServer.Builder();
        ircServerBuilder
                .setHostname(Configurable.SERVER)
                .setPort(Configurable.PORT)
                .setSecure(Configurable.SECURE)
                .setPassword(Configurable.PASSWORD);

        IrcUser.Builder ircUserBuilder = new IrcUser.Builder();
        ircUserBuilder
                .setNick(Configurable.NICK)
                .setLogin(Configurable.LOGIN);

        addListeners();

        ircConnection.connect(
                ircServerBuilder.build(),
                ircUserBuilder.build()
        );
    }

    private static void addListeners() {
        ircConnection.addListener((OnConnectListener) (ircConnection, server, user) -> {
            System.out.println("Connected to " + server.getHostname() + ":" + server.getPort() + " as: " + user.getNick() + ":" + user.getLogin());

            try {
                ircConnection.join(new IrcChannel(Configurable.CHANNEL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ircConnection.addListener((OnDisconnectListener) (ircConnection, server) -> System.out.println("Disconnected from " + server.getHostname() + ":" + server.getPort()));

        ircConnection.addListener(ircConnection -> System.out.println("PING!"));

        ircConnection.addListener((OnJoinListener) (ircConnection, user, channel) -> {
            System.out.println(user.getNick() + " joined " + channel.getName());

            IrcChannelMessage.Builder ircChannelMessageBuilder = new IrcChannelMessage.Builder();
            ircChannelMessageBuilder
                    .setSender(ircConnection.getCurrentUser())
                    .setChannel(channel)
                    .setText("Hello " + user.getNick() + " welcome to " + channel.getName());

            if (!user.getNick().equals(ircConnection.getCurrentUser().getNick())) {
                try {
                    ircConnection.send(ircChannelMessageBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ircConnection.addListener((OnPartListener) (ircConnection, user, channel) -> System.out.println(user.getNick() + " parted from " + channel.getName()));

        ircConnection.addListener((OnKickListener) (ircConnection, user, channel) -> {
            System.out.println(user.getNick() + " has been kicked from " + channel.getName());

            try {
                ircConnection.join(new IrcChannel(Configurable.CHANNEL));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ircConnection.addListener((OnChannelModeListener) (ircConnection, channel, mode) -> System.out.println("Mode changed to " + mode + " in " + channel.getName()));

        ircConnection.addListener((OnChannelMessageListener) (ircConnection, message) -> {
            String sender = message.getSender().getNick();
            String text = message.getText();
            IrcChannel channel = message.getChannel();

            System.out.println("Message received from " + sender + ": " + text + " in " + channel.getName());

            String[] splittedMessage = text.split(" ");
            String command = splittedMessage[0];
            String[] parameters = null;

            if (splittedMessage.length > 1)
                parameters = Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length);

            IrcPrivateMessage.Builder ircPrivateMessageBuilder = new IrcPrivateMessage.Builder();
            ircPrivateMessageBuilder
                    .setSender(ircConnection.getCurrentUser())
                    .setReceiver(message.getSender());

            switch (command) {
                case "!join":
                    if (parameters != null) {
                        ircPrivateMessageBuilder.setText("Joining " + parameters[0]);
                        try {
                            ircConnection.send(ircPrivateMessageBuilder.build());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            ircConnection.join(new IrcChannel(parameters[0]));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case "!part":
                    String partChannel = channel.getName();

                    if (parameters != null)
                        partChannel = parameters[0];

                    if (!partChannel.equals("")) {
                        ircPrivateMessageBuilder.setText("Parting from " + partChannel);
                        try {
                            ircConnection.send(ircPrivateMessageBuilder.build());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            ircConnection.part(new IrcChannel(partChannel));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case "!op":
                    String opNick = sender;
                    if (parameters != null)
                        opNick = parameters[0];

                    ircPrivateMessageBuilder.setText("Giving OP to " + opNick + " in " + channel.getName());
                    try {
                        ircConnection.send(ircPrivateMessageBuilder.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        ircConnection.mode(channel, "+o " + opNick);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "!deop":
                    String deopNick = sender;
                    if (parameters != null)
                        deopNick = parameters[0];

                    ircPrivateMessageBuilder.setText("Removing OP to " + deopNick + " in " + channel.getName());
                    try {
                        ircConnection.send(ircPrivateMessageBuilder.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        ircConnection.mode(channel, "-o " + deopNick);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "!voice":
                    String voiceNick = sender;
                    if (parameters != null)
                        voiceNick = parameters[0];

                    ircPrivateMessageBuilder.setText("Giving VOICE to " + voiceNick + " in " + channel.getName());
                    try {
                        ircConnection.send(ircPrivateMessageBuilder.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        ircConnection.mode(channel, "+v " + voiceNick);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "!devoice":
                    String devoiceNick = sender;
                    if (parameters != null)
                        devoiceNick = parameters[0];

                    ircPrivateMessageBuilder.setText("Removing VOICE to " + devoiceNick + " in " + channel.getName());
                    try {
                        ircConnection.send(ircPrivateMessageBuilder.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        ircConnection.mode(channel, "-v " + devoiceNick);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        });

        ircConnection.addListener((OnPrivateMessageListener) (ircConnection, message) -> System.out.println("Private message received from " + message.getSender().getNick() + ": " + message.getText()));

        ircConnection.addListener((OnNickChangeListener) (ircConnection, user) -> System.out.println(user.getOldNick() + " changed nickname to " + user.getNick()));
    }
}