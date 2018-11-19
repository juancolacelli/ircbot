# IRC Bot

## Dependencies
* **GNU IRC Library**: https://gitlab.com/jic/irclib
* **json-simple**: https://code.google.com/archive/p/json-simple/
* **apache-commons**: http://commons.apache.org/proper/commons-lang/

**Maven**
```
com.googlecode.json-simple:json-simple:1.1.12
org.apache.commons:commons-text:1.62
```

## Showcase
* **GNU Librebot**: https://gitlab.com/jic/librebot

## Basic usage
```java
IRCBot bot = new IRCBot();

User.Builder userBuilder = new User.Builder();
userBuilder
        .setNick("ircbot")
        .setLogin("ircbot")
        .setRealName("ircbot");

Server.Builder serverBuilder = new Server.Builder();
serverBuilder
        .setHostname("irc.freenode.net")
        .setPort(6697)
        .setSecure(true);

bot.connect(serverBuilder.build(), userBuilder.build());
```

## Basic plugin definition
```java
public class BasicPlugin implements Plugin {
    @Override
    public void setup(IRCBot bot) {
        bot.addListener((OnConnectListener) (connection, server, user) -> {
            // TODO: Do something...
        });
    }
}
```

```java
IRCBot bot = new IRCBot();
bot.addPlugin(new BasicPlugin());
```

## Plugins
* **access**: Grant/Revoke bot access
* **apertiumtranslator**: Translate text using [Apertium](https://apertium.org)
* **autojoin**: Auto-join channels on connect
* **autoreconnect**: Auto-reconnect on disconnection
* **ctcpversion**: Customize your CTCP VERSION response
* **help**: Bot help
* **ircop**: IRCop authentication
* **nickserv**: NickServ authentication
* **operator**: Basic operator commands (i.e, !op, !voice, etc.)
* **rejoinonkick**: Re-join channels on kick
* **rssfeed**: Get rss feed notices and send it to all joined channels
* **uptime**: Shows bot uptime
* **websitetitle**: Get website title when an url is detected

