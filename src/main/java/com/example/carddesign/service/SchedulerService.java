package com.example.carddesign.service;

import com.example.carddesign.vo.FavoriteCard;
import com.example.carddesign.vo.PushConfig;
import com.example.carddesign.vo.PushTime;
import com.example.carddesign.mock.ThirdPartyApiMock;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SchedulerService extends BaseService {

    private final FavoriteCardService favoriteCardService;
    private final PushConfigService pushConfigService;
    private final MessageLogService messageLogService;

    public SchedulerService(SqlSessionFactory sqlSessionFactory,
                            FavoriteCardService favoriteCardService,
                            PushConfigService pushConfigService,
                            MessageLogService messageLogService) {
        super(sqlSessionFactory);
        this.favoriteCardService = favoriteCardService;
        this.pushConfigService = pushConfigService;
        this.messageLogService = messageLogService;
    }

    /**
     * Simulates the entry point for the scheduled task.
     * This method would be called by a scheduling framework (e.g., Spring's @Scheduled or Quartz).
     */
    public void runPushTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1 (Monday) to 7 (Sunday)
        int currentMonthDay = now.getDayOfMonth();
        // Format to HH:mm for matching push times
        String currentHourMinute = String.format("%02d:%02d", currentTime.getHour(), currentTime.getMinute());

        System.out.println("\n--- Scheduled Task Started @ " + now + " ---");

        // 1. Process user's enabled collected cards: Only query data, assemble messages, and store in message_log. No external group push.
        List<PushConfig> enabledPushConfigs = pushConfigService.getAllEnabledPushConfigs();
        for (PushConfig config : enabledPushConfigs) {
            // Ensure it's a push config for an enabled favorite card belonging to the user and project
            FavoriteCard associatedCard = favoriteCardService.getFavoriteCardById(config.getFavoriteId());
            if (associatedCard != null && associatedCard.getEnableFlag() == 1 &&
                    associatedCard.getUserId().equals(config.getUserId()) && associatedCard.getProjectCode().equals(config.getProjectCode())) {

                if (shouldPush(config, currentDayOfWeek, currentMonthDay, currentHourMinute)) {
                    System.out.println("  -> Found enabled collected card config to process (DB storage ONLY): " + config.getContent() + " (User: " + config.getUserId() + ", Project: " + config.getProjectCode() + ")");
                    String messageContent = ThirdPartyApiMock.getThirdPartyCardData(config.getContent());
                    messageLogService.addMessage(config.getFavoriteId(), messageContent);
                    System.out.println("     Message stored in message_log: " + messageContent);
                }
            }
        }

        // 2. Process cards not collected by user or whose collection is disabled: Daily 8 AM push to groups and store in message_log.
        // Assuming we only care about exact hour pushes, if current time is 8:00, execute uncollected card push.
        if (currentTime.getHour() == 8 && currentTime.getMinute() == 0) {
            System.out.println("\n--- Daily 8 AM Uncollected/Disabled Card Push Started ---");
            // Simulate getting all project and user combinations (from group API)
            List<String> allProjectCodes = Arrays.asList("PROJ001", "PROJ002", "PROJ003"); // Simulate all projects
            for (String projectCode : allProjectCodes) {
                List<String> groupIds = ThirdPartyApiMock.getGroupIdsByProjectCode(projectCode);
                for (String groupId : groupIds) {
                    Long userId = extractUserIdFromGroupId(groupId);
                    if (userId == null) {
                        System.err.println("Could not extract User ID from Group ID [" + groupId + "]. Skipping.");
                        continue;
                    }

                    System.out.println("  -> Processing uncollected/disabled cards for User " + userId + " in Project " + projectCode + "...");
                    // Get all cards for this user and project (including user's enabled/disabled collected cards and default cards)
                    List<FavoriteCard> allCardsForUserAndProject = favoriteCardService.getAllCardsForUserAndProject(userId, projectCode);

                    // Identify all default cards
                    List<FavoriteCard> defaultCards = allCardsForUserAndProject.stream()
                            .filter(card -> card.getUserId().equals(-1L) && card.getProjectCode().equals("DEFAULT") && card.getEnableFlag() == 1)
                            .collect(Collectors.toList());

                    for (FavoriteCard defaultCard : defaultCards) {
                        // Check if the user has an enabled collection of this default card
                        boolean isUserCollectedAndEnabled = allCardsForUserAndProject.stream()
                                .anyMatch(userCard -> userCard.getUserId().equals(userId) &&
                                        userCard.getProjectCode().equals(projectCode) &&
                                        userCard.getContent().equals(defaultCard.getContent()) &&
                                        userCard.getBusinessType().equals(defaultCard.getBusinessType()) &&
                                        userCard.getEnableFlag() == 1);

                        if (!isUserCollectedAndEnabled) {
                            System.out.println("     - Found uncollected or disabled default card for User " + userId + " that needs push: " + defaultCard.getContent());
                            String messageContent = ThirdPartyApiMock.getThirdPartyCardData(defaultCard.getContent());
                            // Store message in message_log using the default card's ID as favorite_id
                            messageLogService.addMessage(defaultCard.getId(), "【System Push】" + messageContent);
                            System.out.println("       Message stored in DB AND simulated push to group: " + groupId + ", Content: " + messageContent);
                        }
                    }
                }
            }
            System.out.println("--- Daily 8 AM Uncollected/Disabled Card Push Finished ---");
        }

        System.out.println("--- Scheduled Task Finished ---");
    }

    /**
     * Determines if a push should occur based on the configuration and current time.
     * @param config Push configuration.
     * @param currentDayOfWeek Current day of the week (1-7).
     * @param currentMonthDay Current day of the month (1-31).
     * @param currentHourMinute Current hour and minute (HH:mm).
     * @return true if a push should occur, false otherwise.
     */
    private boolean shouldPush(PushConfig config, int currentDayOfWeek, int currentMonthDay, String currentHourMinute) {
        if (config.getEnableFlag() == 0 || config.getPushTimes() == null || config.getPushTimes().isEmpty()) {
            return false;
        }

        for (PushTime pushTime : config.getPushTimes()) {
            if (pushTime.getEnableFlag() == 0) continue; // Ignore disabled push times

            // Check if the hour matches
            List<String> hours = Arrays.asList(pushTime.getHour().split(","));
            if (!hours.contains(currentHourMinute)) {
                continue; // Hour does not match
            }

            switch (config.getPushFrequency()) {
                case "DAY":
                    return true; // Push every day
                case "WEEK":
                    if (pushTime.getWeekday() != null) {
                        List<Integer> weekdays = Arrays.stream(pushTime.getWeekday().split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        if (weekdays.contains(currentDayOfWeek)) {
                            return true;
                        }
                    }
                    break;
                case "MONTH":
                    if (pushTime.getMonthDay() != null) {
                        List<Integer> monthDays = Arrays.stream(pushTime.getMonthDay().split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        if (monthDays.contains(currentMonthDay)) {
                            return true;
                        }
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * Extracts the user ID from a group ID string.
     * @param groupId Group ID in the format: projectCode_userId.
     * @return User ID, or null if extraction fails.
     */
    private Long extractUserIdFromGroupId(String groupId) {
        if (groupId == null || !groupId.contains("_")) {
            return null;
        }
        String[] parts = groupId.split("_");
        if (parts.length < 2) {
            return null;
        }
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            System.err.println("Could not extract User ID from Group ID [" + groupId + "]: " + e.getMessage());
            return null;
        }
    }
}