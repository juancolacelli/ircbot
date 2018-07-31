package com.colacelli.irclib.connection.listeners;

import com.colacelli.irclib.connection.Connection;
import com.colacelli.irclib.messages.ChannelMessage;

public interface OnChannelCommandListener {
    void onChannelCommand(Connection connection, ChannelMessage message, String command, String... params);
}
