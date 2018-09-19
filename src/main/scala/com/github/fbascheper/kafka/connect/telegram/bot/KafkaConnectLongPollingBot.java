package com.github.fbascheper.kafka.connect.telegram.bot;

import org.slf4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A Telegram bot which extends the {@link TelegramLongPollingBot}.
 *
 * @author Erik-Berndt Scheper
 * @since 17-09-2018
 */
public class KafkaConnectLongPollingBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = getLogger(KafkaConnectLongPollingBot.class);

    private final String botUsername;

    private final String botToken;

    public KafkaConnectLongPollingBot(String botUsername, String botToken) {
        super();
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            LOGGER.debug("Ignoring received update with text {}", update.getMessage().getText());
            // this.sendTextMessage(update.getMessage().getChatId(), "Echo: " + update.getMessage().getText());
        }
    }

    /**
     * Send a Telegram bot message.
     *
     * @param botMessage message to send
     * @return a list of message-ids from the sent messages (if applicable)
     */
    public List<Integer> sendBotMessage(PartialBotApiMethod<? extends java.io.Serializable> botMessage) {
        List<Integer> result;

        try {
            if (botMessage instanceof SendMessage) {
                result = Collections.singletonList(execute((SendMessage) botMessage).getMessageId());
            } else if (botMessage instanceof SendPhoto) {
                result = Collections.singletonList(execute((SendPhoto) botMessage).getMessageId());
            } else if (botMessage instanceof SendVideo) {
                result = Collections.singletonList(execute((SendVideo) botMessage).getMessageId());
            } else if (botMessage instanceof SendAnimation) {
                result = Collections.singletonList(execute((SendAnimation) botMessage).getMessageId());
            } else if (botMessage instanceof SendChatAction) {
                execute((SendChatAction) botMessage);
                result = Collections.emptyList();
            } else if (botMessage instanceof SendMediaGroup) {
                result = execute((SendMediaGroup) botMessage).stream().map(Message::getMessageId).collect(Collectors.toList());
            } else {
                throw new IllegalStateException("Unexpected message type created");
            }
            return result;

        } catch (TelegramApiException | IllegalStateException ex) {
            LOGGER.error("Could not send Telegram message, API exception was", ex);
            return null;
        }
    }

    @Override
    public final String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public final String getBotToken() {
        return this.botToken;
    }
}
