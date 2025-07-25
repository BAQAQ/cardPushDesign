package com.example.carddesign.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Mocks third-party APIs.
 */
public class ThirdPartyApiMock {

    private static final Map<String, List<String>> MOCK_GROUPS = new HashMap<>();
    private static final Map<String, String> MOCK_CARD_DATA = new HashMap<>();
    private static final Map<String, String> MOCK_CARD_STYLE = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // Mock group data: projectCode -> List<groupId>
        MOCK_GROUPS.put("PROJ001", Arrays.asList("PROJ001_1001", "PROJ001_1002", "PROJ001_1003"));
        MOCK_GROUPS.put("PROJ002", Arrays.asList("PROJ002_2001", "PROJ002_2002"));
        MOCK_GROUPS.put("PROJ003", Arrays.asList("PROJ003_3001"));

        // Mock card data: cardContent -> data
        MOCK_CARD_DATA.put("学习报告卡片", "您的本周学习报告已生成：完成度85%，进步显著！");
        MOCK_CARD_DATA.put("成绩评估卡片", "期中考试成绩评估：数学A，语文B+，请再接再厉。");
        MOCK_CARD_DATA.put("计划进展卡片", "本月计划进展：已完成70%，剩余任务请及时跟进。");
        MOCK_CARD_DATA.put("每日关注卡片", "今日关注：最新教育政策解读。");
        MOCK_CARD_DATA.put("教辅采购进度卡片", "教辅采购订单S20240725已发货，预计3天内送达。");

        // Mock card style: cardContent -> styleJson (simplified as string)
        MOCK_CARD_STYLE.put("学习报告卡片", "{'color': 'blue', 'font': 'bold'}");
        MOCK_CARD_STYLE.put("成绩评估卡片", "{'color': 'red', 'font': 'italic'}");
        MOCK_CARD_STYLE.put("计划进展卡片", "{'color': 'green', 'font': 'normal'}");
        MOCK_CARD_STYLE.put("每日关注卡片", "{'color': 'purple', 'font': 'underline'}");
        MOCK_CARD_STYLE.put("教辅采购进度卡片", "{'color': 'orange', 'font': 'small'}");
    }

    /**
     * Simulates querying group interface, getting group IDs based on project code.
     * Group ID is formed by projectCode_userId.
     * @param projectCode The project code.
     * @return A list of mock group IDs.
     */
    public static List<String> getGroupIdsByProjectCode(String projectCode) {
        System.out.println("Simulating call to third-party group API: Getting group IDs for project [" + projectCode + "]...");
        return MOCK_GROUPS.getOrDefault(projectCode, Arrays.asList());
    }

    /**
     * Simulates getting third-party card data.
     * @param cardContent The content of the card (used to identify the card type).
     * @return Simulated card data.
     */
    public static String getThirdPartyCardData(String cardContent) {
        System.out.println("Simulating call to third-party data API: Getting data for card [" + cardContent + "]...");
        String data = MOCK_CARD_DATA.getOrDefault(cardContent, "No data for this card.");
        // Simulate data dynamism
        if (cardContent.equals("学习报告卡片")) {
            data = "您的本周学习报告已生成：完成度" + (80 + RANDOM.nextInt(20)) + "%，进步显著！";
        }
        return data;
    }

    /**
     * Simulates getting third-party card style.
     * @param cardContent The content of the card.
     * @return Simulated card style (JSON string).
     */
    public static String getThirdPartyCardStyle(String cardContent) {
        System.out.println("Simulating call to third-party style API: Getting style for card [" + cardContent + "]...");
        return MOCK_CARD_STYLE.getOrDefault(cardContent, "{'color': 'black', 'font': 'normal'}");
    }
}