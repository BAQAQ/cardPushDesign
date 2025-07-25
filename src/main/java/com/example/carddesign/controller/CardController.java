package com.example.carddesign.controller;

import com.example.carddesign.mock.ThirdPartyApiMock;
import com.example.carddesign.service.FavoriteCardService;
import com.example.carddesign.service.MessageLogService;
import com.example.carddesign.service.PushConfigService;
import com.example.carddesign.vo.FavoriteCard;
import com.example.carddesign.vo.MessageLog;
import com.example.carddesign.vo.PushTime;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Controller class to handle card-related requests (simulated REST endpoints).
 */
public class CardController {

    private final FavoriteCardService favoriteCardService;
    private final PushConfigService pushConfigService;
    private final MessageLogService messageLogService;

    public CardController(SqlSessionFactory sqlSessionFactory) {
        this.favoriteCardService = new FavoriteCardService(sqlSessionFactory);
        this.pushConfigService = new PushConfigService(sqlSessionFactory);
        this.messageLogService = new MessageLogService(sqlSessionFactory);
    }

    /**
     * API: User collects a card.
     * @param userId User ID.
     * @param projectCode Project code.
     * @param content Card content.
     * @param businessType Business type (1 for card, 0 for question).
     * @return Response map with status and message/data.
     */
    public Map<String, Object> collectCard(Long userId, String projectCode, String content, Integer businessType) {
        Map<String, Object> response = new HashMap<>();
        Long cardId = favoriteCardService.collectCard(userId, projectCode, content, businessType);
        if (cardId != null) {
            response.put("status", "success");
            response.put("message", "Card collected successfully.");
            response.put("cardId", cardId);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to collect card or card already exists.");
        }
        return response;
    }

    /**
     * API: User uncollects a card.
     * @param userId User ID.
     * @param projectCode Project code.
     * @param cardId Card ID.
     * @return Response map with status and message.
     */
    public Map<String, Object> uncollectCard(Long userId, String projectCode, Long cardId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = favoriteCardService.uncollectCard(userId, projectCode, cardId);
        if (success) {
            response.put("status", "success");
            response.put("message", "Card uncollected successfully.");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to uncollect card or card is not cancellable/not found.");
        }
        return response;
    }

    /**
     * API: Get all enabled favorite cards for a user in a project.
     * @param userId User ID.
     * @param projectCode Project code.
     * @return Response map with status and list of cards.
     */
    public Map<String, Object> getMyFavoriteCards(Long userId, String projectCode) {
        Map<String, Object> response = new HashMap<>();
        List<FavoriteCard> cards = favoriteCardService.getEnabledFavoriteCards(userId, projectCode);
        response.put("status", "success");
        response.put("data", cards);
        return response;
    }

    /**
     * API: Create or update push settings for a favorite card.
     * @param userId User ID.
     * @param projectCode Project code.
     * @param favoriteId Favorite card ID.
     * @param pushFrequency Push frequency (DAY, WEEK, MONTH).
     * @param content Card content.
     * @param businessType Business type.
     * @param weekday Weekdays (comma-separated, e.g., "1,3,5").
     * @param monthDay Month days (comma-separated, e.g., "1,15").
     * @param hour Hours (comma-separated, e.g., "09:00,15:00").
     * @return Response map with status and message/configId.
     */
    public Map<String, Object> createOrUpdatePushConfig(Long userId, String projectCode, Long favoriteId,
                                                        String pushFrequency, String content, Integer businessType,
                                                        String weekday, String monthDay, String hour) {
        Map<String, Object> response = new HashMap<>();
        List<PushTime> pushTimes = Arrays.asList(new PushTime(null, weekday, monthDay, hour)); // Assuming one PushTime per config for simplicity

        Long configId = pushConfigService.createOrUpdatePushConfig(
                userId, projectCode, favoriteId, pushFrequency, content, businessType, pushTimes);

        if (configId != null) {
            response.put("status", "success");
            response.put("message", "Push configuration saved successfully.");
            response.put("configId", configId);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to save push configuration.");
        }
        return response;
    }

    /**
     * API: Delete (logically) a push setting.
     * @param configId Push configuration ID.
     * @return Response map with status and message.
     */
    public Map<String, Object> deletePushConfig(Long configId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = pushConfigService.deletePushConfig(configId);
        if (success) {
            response.put("status", "success");
            response.put("message", "Push configuration deleted successfully.");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to delete push configuration.");
        }
        return response;
    }

    /**
     * API: Frontend queries the latest message for a favorite card, and its style.
     * @param favoriteId Favorite card ID.
     * @return Response map with message content and card style.
     */
    public Map<String, Object> getLatestCardMessageAndStyle(Long favoriteId) {
        Map<String, Object> response = new HashMap<>();
        MessageLog latestMessage = messageLogService.getLatestMessageByFavoriteId(favoriteId);
        FavoriteCard card = favoriteCardService.getFavoriteCardById(favoriteId);

        if (latestMessage != null && card != null) {
            String cardStyle = ThirdPartyApiMock.getThirdPartyCardStyle(card.getContent());
            response.put("status", "success");
            response.put("messageContent", latestMessage.getMessageContent());
            response.put("cardStyle", cardStyle);
        } else {
            response.put("status", "error");
            response.put("message", "No message or card found for favorite ID: " + favoriteId);
        }
        return response;
    }
}