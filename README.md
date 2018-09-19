# Kafka Connect source for Telegram

### Getting started

The Telegram connect plugin needs a bot to communicate with.
Currently you must use and create your own bot, e.g. the ``KafkaConnectBot`` using the BotFather

- Start Telegram app and [Open the BotFather](https://telegram.me/botfather).
- Use the ``/newbot`` command to create a new bot. The BotFather will ask you for a name and username, then generate an authorization token for your new bot.
  - The ``name`` of your bot is displ§ayed in contact details and elsewhere.
  - The ``username`` is a short name, to be used in mentions and telegram.me links. Usernames are 5-32 characters long and are case insensitive, but may only include Latin characters, numbers, and underscores. Your bot's username must end in ‘bot’, e.g. ``KafkaConnectBot``.
  - The ``token`` is a string along the lines of ``110201543:AAHdqTcvCH1vGWJxfSeofSAs0K5PALDsaw`` that is required to authorize the bot and send requests to the Bot API.
- (Optional) Set the picture of the bot, using the [Logo](https://nl.wikipedia.org/wiki/Bestand:Knex-Connect-Red.svg)
  - Type ``/setuserpic`` in the ``BotFather`` followed by the name of the bot, e.g. ``@KafkaConnectBot`` and the logo.
- Enable adding the bot to a group
  - Type ``/setjoingroups`` in the ``BotFather`` followed by the name of the bot, e.g. ``@KafkaConnectBot`` and the logo and ``enable`` joining.
- Create a new group, i.e. ``Burglar-alerts``
- Add the bot to the group
  - Select the ``@KafkaConnectBot`` and choose ``Add to group``, select the ``Burglar-alerts`` group.
  - Confirm to add the bot to the group.
- Send the message ``/test`` to the group; this should be sent to the bot as well, even in privacy mode

### Determine and verify the (group) chat ID
- The group chat id can be retrieved from the Telegram API, using the output fom the  following command, replacing ``XXtokenXX`` by the actual token
  - ``curl https://api.telegram.org/botXXtokenXX/getUpdates``
  - In the JSON response, e.g ``chat":{"id":-999991234,"title":"Burglar-alerts","type":"group"`` you can find the chat id, here -999991234
- Your bot should be able to send using a simple curl command:
- ``curl -X POST -H "Content-Type: application/json" -d '{"chat_id":"-999991234", "text":"Hello world!"}' https://api.telegram.org/botXXtokenXX/sendMessage``
