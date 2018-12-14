package com.colacelli.ircbot.plugins.autoresponse

import com.colacelli.ircbot.IRCBot
import com.colacelli.ircbot.Plugin
import com.colacelli.ircbot.listeners.OnChannelCommandListener
import com.colacelli.ircbot.plugins.access.IRCBotAccess
import com.colacelli.ircbot.plugins.help.PluginHelp
import com.colacelli.ircbot.plugins.help.PluginHelper
import com.colacelli.irclib.connection.Connection
import com.colacelli.irclib.connection.listeners.OnChannelMessageListener
import com.colacelli.irclib.messages.ChannelMessage
import com.colacelli.irclib.messages.PrivateNoticeMessage

class AutoResponsePlugin :Plugin {
    val listener = object : OnChannelMessageListener {
        override fun onChannelMessage(connection: Connection, message: ChannelMessage) {
            val text = AutoResponse.instance.get(message)

            if (text != null && text.isNotBlank()) {
                connection.send(ChannelMessage(message.channel, text, connection.user))
            }
        }
    }

    override fun getName(): String {
        return "auto_response"
    }

    override fun onLoad(bot: IRCBot) {
        IRCBotAccess.instance.addListener(bot, IRCBotAccess.Level.ADMIN, object : OnChannelCommandListener {
            override val commands: Array<String>
                get() = arrayOf(".autoresponse", ".ar")

            override fun onChannelCommand(connection: Connection, message: ChannelMessage, command: String, args: Array<String>) {
                if (args.size > 1) {
                    when (args[0]) {
                        "add" -> {
                            // FIXME: Dirty code...
                            val joinedArgs = args.drop(1).joinToString(" ")
                            val separatedArgs = joinedArgs.split(AutoResponsePluginHelp.SEPARATOR)
                            val trigger = separatedArgs[0]
                            val text = separatedArgs.drop(1).joinToString("|")

                            if (trigger.isNotBlank() && text.isNotBlank()) {
                                AutoResponse.instance.add(trigger, text)
                                connection.send(PrivateNoticeMessage("Auto-response added!", connection.user, message.sender))
                            }
                        }

                        "del" -> {
                            val trigger = args.drop(1).joinToString(" ")
                            AutoResponse.instance.del(trigger)
                            connection.send(PrivateNoticeMessage("Auto-response removed!", connection.user, message.sender))
                        }
                    }
                } else {
                    when (args[0]) {
                        "list" -> {
                            AutoResponse.instance.list().forEach { trigger, text ->
                                connection.send(PrivateNoticeMessage("$trigger: $text", connection.user, message.sender))
                            }
                        }
                    }
                }
            }
        })

        PluginHelper.instance.addHelp(AutoResponsePluginHelp(
                ".ar add",
                IRCBotAccess.Level.OPERATOR,
                "Adds an auto-response. Available replacements: regex (\$1, \$2, etc.), \$nick and \$channel, ie., .ar add hello" + AutoResponsePluginHelp.SEPARATOR + "hello \$nick, welcome to \$channel!",
                "trigger",
                "response"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".ar del",
                IRCBotAccess.Level.OPERATOR,
                "Removes an auto-response",
                "trigger"))

        PluginHelper.instance.addHelp(PluginHelp(
                ".ar list",
                IRCBotAccess.Level.OPERATOR,
                "List all auto-responses"))

        bot.addListener(listener)
    }

    override fun onUnload(bot: IRCBot) {
        bot.removeListener(".ar")
        bot.removeListener(listener)
        arrayOf("add", "del", "list").forEach {
            PluginHelper.instance.removeHelp(".ar $it")
        }
    }
}