package com.colacelli.ircbot.listeners;

import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

public interface OnChannelCommandListener {
    String channelCommand();

    void onChannelCommand(Connection connection, ChannelMessage message, String command, String... args);
}
