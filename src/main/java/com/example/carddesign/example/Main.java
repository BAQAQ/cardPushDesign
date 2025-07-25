package com.example.carddesign;

import com.example.carddesign.controller.CardController;
import com.example.carddesign.service.FavoriteCardService;
import com.example.carddesign.service.MessageLogService;
import com.example.carddesign.service.PushConfigService;
import com.example.carddesign.service.SchedulerService;
import com.example.carddesign.vo.FavoriteCard;
import com.example.carddesign.vo.MessageLog;
import com.example.carddesign.vo.PushTime;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static SqlSessionFactory sqlSessionFactory;
    private static FavoriteCardService favoriteCardService;
    private static PushConfigService pushConfigService;
    private static MessageLogService messageLogService;
    private static SchedulerService schedulerService;
    private static CardController cardController; // New: Controller instance

    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            // Initialize services
            favoriteCardService = new FavoriteCardService(sqlSessionFactory);
            pushConfigService = new PushConfigService(sqlSessionFactory);
            messageLogService = new MessageLogService(sqlSessionFactory);
            schedulerService = new SchedulerService(sqlSessionFactory, favoriteCardService, pushConfigService, messageLogService);

            // Initialize controller
            cardController = new CardController(sqlSessionFactory);

            // Initialize default cards
            favoriteCardService.initializeDefaultCards();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing application", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Application Started ---");

        // Simulate users and projects
        Long userId1 = 1001L;
        String projectCode1 = "PROJ001";
        Long userId2 = 1002L;
        String projectCode2 = "PROJ001"; // Different user in the same project

        // 1. Demonstrate card collection operations via Controller
        System.out.println("\n--- Demonstrating Card Collection Operations via Controller ---");
        demoCardCollectionOperations(userId1, projectCode1);

        // 2. Demonstrate push configuration operations via Controller
        System.out.println("\n--- Demonstrating Push Configuration Operations via Controller ---");
        demoPushConfigOperations(userId1, projectCode1);

        // 3. Simulate scheduled task execution
        System.out.println("\n--- Simulating Scheduled Task (runs every 5 seconds for 3 times) ---");
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            // For demonstration, you can manually set current time here to trigger specific logic
            // E.g., to simulate 8 AM:
            // LocalTime mockTime = LocalTime.of(8, 0);
            // System.out.println("Simulating current time as: " + mockTime);
            schedulerService.runPushTask();
        }, 0, 5, TimeUnit.SECONDS); // Run every 5 seconds

        // Run for a period then shut down the scheduler
        try {
            TimeUnit.SECONDS.sleep(15); // Run for 15 seconds (3 executions)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
            System.out.println("\n--- Simulated Scheduled Task Finished ---");
        }

        // 4. Demonstrate frontend message query API
        System.out.println("\n--- Demonstrating Frontend Message Query API ---");
        demoFrontendQueryMessage(userId1, projectCode1);

        System.out.println("\n--- Application Finished ---");
    }

    private static void demoCardCollectionOperations(Long userId, String projectCode) {
        // Collect "学习报告卡片"
        System.out.println("\n--- Collecting '学习报告卡片' ---");
        cardController.collectCard(userId, projectCode, "学习报告卡片", 1);
        // Attempt to collect again (should indicate already collected, no new record)
        cardController.collectCard(userId, projectCode, "学习报告卡片", 1);

        // Collect "每日关注卡片" (default non-cancellable)
        System.out.println("\n--- Collecting '每日关注卡片' (Default Non-Cancellable) ---");
        cardController.collectCard(userId, projectCode, FavoriteCardService.DAILY_FOCUS_CARD, 1);
        // Attempt to uncollect "每日关注卡片" (should fail)
        FavoriteCard dailyFocusCard = favoriteCardService.getAllCardsForUserAndProject(userId, projectCode).stream()
                .filter(card -> FavoriteCardService.DAILY_FOCUS_CARD.equals(card.getContent()) && card.getUserId().equals(userId))
                .findFirst().orElse(null);
        if (dailyFocusCard != null) {
            cardController.uncollectCard(userId, projectCode, dailyFocusCard.getId());
        }

        // Collect "教辅采购进度卡片" (default cancellable)
        System.out.println("\n--- Collecting '教辅采购进度卡片' (Default Cancellable) ---");
        cardController.collectCard(userId, projectCode, FavoriteCardService.TEACHING_AID_CARD, 1);
        // Attempt to uncollect "教辅采购进度卡片" (should succeed)
        FavoriteCard teachingAidCard = favoriteCardService.getAllCardsForUserAndProject(userId, projectCode).stream()
                .filter(card -> FavoriteCardService.TEACHING_AID_CARD.equals(card.getContent()) && card.getUserId().equals(userId))
                .findFirst().orElse(null);
        if (teachingAidCard != null) {
            cardController.uncollectCard(userId, projectCode, teachingAidCard.getId());
            // Collect again, simulating re-activation (should update existing record)
            System.out.println("\n--- Re-collecting '教辅采购进度卡片' (Should activate old record) ---");
            cardController.collectCard(userId, projectCode, FavoriteCardService.TEACHING_AID_CARD, 1);
        }

        System.out.println("\nUser " + userId + " in project " + projectCode + "'s enabled collected cards:");
        cardController.getMyFavoriteCards(userId, projectCode).forEach((key, value) -> {
            if ("data".equals(key) && value instanceof List) {
                ((List<?>) value).forEach(System.out::println);
            } else {
                System.out.println(key + ": " + value);
            }
        });

        System.out.println("\nUser " + userId + " in project " + projectCode + "'s all cards (including default and disabled):");
        favoriteCardService.getAllCardsForUserAndProject(userId, projectCode).forEach(System.out::println);
    }

    private static void demoPushConfigOperations(Long userId, String projectCode) {
        // Get the ID of "学习报告卡片" collected by the user
        FavoriteCard learningReportCard = favoriteCardService.getEnabledFavoriteCards(userId, projectCode).stream()
                .filter(card -> "学习报告卡片".equals(card.getContent()))
                .findFirst().orElse(null);

        if (learningReportCard != null) {
            // Set daily push for "学习报告卡片", 9 AM, 3 PM
            System.out.println("\n--- Setting Daily Push for '学习报告卡片' ---");
            cardController.createOrUpdatePushConfig(
                    userId, projectCode, learningReportCard.getId(),
                    "DAY", learningReportCard.getContent(), learningReportCard.getBusinessType(),
                    null, null, "09:00,15:00"
            );

            // Attempt to create another push setting for the same favoriteId (should update existing record)
            System.out.println("\n--- Attempting to create another push setting for the same favoriteId (Should update) ---");
            cardController.createOrUpdatePushConfig(
                    userId, projectCode, learningReportCard.getId(),
                    "DAY", learningReportCard.getContent(), learningReportCard.getBusinessType(),
                    null, null, "10:00,16:00"
            );
            System.out.println("Push configuration for '学习报告卡片' updated.");
            System.out.println("Query after update: " + pushConfigService.getPushConfigById((Long) ((Map) cardController.getMyFavoriteCards(userId, projectCode).get("data")).get("configId"))); // This is a bit hacky for demo, in real app you'd get configId properly

            // Get the config ID to demonstrate deletion
            Long configIdToDelete = pushConfigService.selectConfigByUserIdAndProjectCodeAndFavoriteId(userId, projectCode, learningReportCard.getId()).getId();

            // Logically delete this push setting
            System.out.println("\n--- Logically deleting push setting for '学习报告卡片' ---");
            if (configIdToDelete != null) {
                cardController.deletePushConfig(configIdToDelete);
            }
            System.out.println("Query after deletion: " + pushConfigService.getPushConfigById(configIdToDelete));

            // Create again (should reactivate old record)
            System.out.println("\n--- Creating push setting for '学习报告卡片' again (Should activate old record) ---");
            cardController.createOrUpdatePushConfig(
                    userId, projectCode, learningReportCard.getId(),
                    "WEEK", learningReportCard.getContent(), learningReportCard.getBusinessType(),
                    "1,3,5", null, "10:00"
            );
            System.out.println("Push configuration for '学习报告卡片' reactivated and updated to weekly push.");
            System.out.println("Query after re-activation: " + pushConfigService.getPushConfigById(configIdToDelete));


        } else {
            System.out.println("No '学习报告卡片' found for the user, skipping push configuration demo.");
        }
    }

    private static void demoFrontendQueryMessage(Long userId, String projectCode) {
        // Get the ID of "学习报告卡片" collected by the user
        FavoriteCard learningReportCard = favoriteCardService.getEnabledFavoriteCards(userId, projectCode).stream()
                .filter(card -> "学习报告卡片".equals(card.getContent()))
                .findFirst().orElse(null);

        if (learningReportCard != null) {
            System.out.println("\n--- Simulating frontend query for latest message of card ID " + learningReportCard.getId() + " ---");
            Map<String, Object> response = cardController.getLatestCardMessageAndStyle(learningReportCard.getId());
            System.out.println("Response: " + response);
        } else {
            System.out.println("No '