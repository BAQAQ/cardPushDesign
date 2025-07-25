package com.example.carddesign.service;

import com.example.carddesign.mapper.FavoriteCardMapper;
import com.example.carddesign.vo.FavoriteCard;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class FavoriteCardService extends BaseService {

    // Predefined five cards
    public static final List<String> ALL_CARD_CONTENTS = Arrays.asList(
            "学习报告卡片", "成绩评估卡片", "计划进展卡片", "每日关注卡片", "教辅采购进度卡片"
    );
    public static final String DAILY_FOCUS_CARD = "每日关注卡片";
    public static final String TEACHING_AID_CARD = "教辅采购进度卡片";

    public FavoriteCardService(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    /**
     * Initializes default cards if they don't exist or ensures their correct state.
     * Default cards are associated with user_id = -1 and project_code = "DEFAULT".
     */
    public void initializeDefaultCards() {
        try (SqlSession session = getTransactionalSession()) {
            FavoriteCardMapper mapper = session.getMapper(FavoriteCardMapper.class);
            Long defaultUserId = -1L;
            String defaultProjectCode = "DEFAULT";
            Integer cardBusinessType = 1; // All are card types

            // "每日关注卡片": Default collected and non-cancellable
            FavoriteCard dailyFocusCard = mapper.selectCardByUserIdAndProjectCodeAndContentAndType(
                    defaultUserId, defaultProjectCode, DAILY_FOCUS_CARD, cardBusinessType);
            if (dailyFocusCard == null) {
                FavoriteCard newCard = new FavoriteCard(defaultUserId, defaultProjectCode, DAILY_FOCUS_CARD, cardBusinessType, 0); // 0: non-cancellable
                mapper.insertFavoriteCard(newCard);
                System.out.println("Initialized default card: " + DAILY_FOCUS_CARD);
            } else {
                // Ensure it's enabled and non-cancellable
                if (dailyFocusCard.getEnableFlag() == 0 || dailyFocusCard.getIsCancellable() != 0) {
                    dailyFocusCard.setEnableFlag(1);
                    dailyFocusCard.setIsCancellable(0);
                    dailyFocusCard.setUpdateTime(LocalDateTime.now());
                    mapper.updateFavoriteCard(dailyFocusCard);
                    System.out.println("Updated default card: " + DAILY_FOCUS_CARD + " to enabled and non-cancellable.");
                }
            }

            // "教辅采购进度卡片": Default collected and cancellable
            FavoriteCard teachingAidCard = mapper.selectCardByUserIdAndProjectCodeAndContentAndType(
                    defaultUserId, defaultProjectCode, TEACHING_AID_CARD, cardBusinessType);
            if (teachingAidCard == null) {
                FavoriteCard newCard = new FavoriteCard(defaultUserId, defaultProjectCode, TEACHING_AID_CARD, cardBusinessType, 1); // 1: cancellable
                mapper.insertFavoriteCard(newCard);
                System.out.println("Initialized default card: " + TEACHING_AID_CARD);
            } else {
                // Ensure it's enabled and cancellable
                if (teachingAidCard.getEnableFlag() == 0 || teachingAidCard.getIsCancellable() != 1) {
                    teachingAidCard.setEnableFlag(1);
                    teachingAidCard.setIsCancellable(1);
                    teachingAidCard.setUpdateTime(LocalDateTime.now());
                    mapper.updateFavoriteCard(teachingAidCard);
                    System.out.println("Updated default card: " + TEACHING_AID_CARD + " to enabled and cancellable.");
                }
            }
            session.commit();
        } catch (Exception e) {
            System.err.println("Failed to initialize default cards: " + e.getMessage());
            try (SqlSession session = getTransactionalSession()) {
                session.rollback();
            }
        }
    }

    /**
     * User collects a card. If the card already exists (enabled or disabled), it will be activated. Otherwise, a new record is inserted.
     * @param userId User ID.
     * @param projectCode Project code.
     * @param content Card content.
     * @param businessType Business type.
     * @return The ID of the collected card, or null if failed.
     */
    public Long collectCard(Long userId, String projectCode, String content, Integer businessType) {
        try (SqlSession session = getTransactionalSession()) {
            FavoriteCardMapper mapper = session.getMapper(FavoriteCardMapper.class);
            // Check if a card record already exists for this user, project, content, and businessType (regardless of enable_flag)
            FavoriteCard existingCard = mapper.selectCardByUserIdAndProjectCodeAndContentAndType(
                    userId, projectCode, content, businessType);

            if (existingCard != null) {
                // If exists, activate it (set enable_flag to 1)
                if (existingCard.getEnableFlag() == 1) {
                    System.out.println("User " + userId + " in project " + projectCode + " has already collected card: " + content + " (ID: " + existingCard.getId() + "). No change needed.");
                } else {
                    existingCard.setEnableFlag(1);
                    existingCard.setUpdateTime(LocalDateTime.now());
                    mapper.updateFavoriteCard(existingCard);
                    System.out.println("User " + userId + " in project " + projectCode + " re-collected card successfully: " + content + " (ID: " + existingCard.getId() + "). Old record activated.");
                }
                session.commit();
                return existingCard.getId();
            } else {
                // If not exists, insert a new record
                // Check if it's a default card, if so, copy its properties for the user's collection
                FavoriteCard defaultCard = mapper.selectCardByUserIdAndProjectCodeAndContentAndType(
                        -1L, "DEFAULT", content, businessType);

                FavoriteCard newCard;
                if (defaultCard != null) {
                    // Copy properties from the default card, set to user's own collection
                    newCard = new FavoriteCard(userId, projectCode, defaultCard.getContent(), defaultCard.getBusinessType(), defaultCard.getIsCancellable());
                } else {
                    // If not a default card, treat as a regular card, default to cancellable
                    newCard = new FavoriteCard(userId, projectCode, content, businessType, 1);
                }

                int insertCount = mapper.insertFavoriteCard(newCard);
                session.commit();
                System.out.println("User " + userId + " in project " + projectCode + " collected card successfully: " + content + ", ID: " + newCard.getId());
                return newCard.getId();
            }
        } catch (Exception e) {
            System.err.println("Failed to collect card: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * User uncollects a card (logical deletion by setting enable_flag to 0).
     * @param userId User ID.
     * @param projectCode Project code.
     * @param cardId Card ID.
     * @return true if successful, false otherwise.
     */
    public boolean uncollectCard(Long userId, String projectCode, Long cardId) {
        try (SqlSession session = getTransactionalSession()) {
            FavoriteCardMapper mapper = session.getMapper(FavoriteCardMapper.class);
            FavoriteCard card = mapper.selectFavoriteCardById(cardId);

            if (card == null || card.getEnableFlag() == 0) {
                System.out.println("Card (ID: " + cardId + ") does not exist or is already disabled. No action needed.");
                return false;
            }
            if (!card.getUserId().equals(userId) || !card.getProjectCode().equals(projectCode)) {
                System.out.println("Card (ID: " + cardId + ") does not belong to user " + userId + " or project " + projectCode + ". Cannot uncollect.");
                return false;
            }
            if (card.getIsCancellable() == 0) {
                System.out.println("Card '" + card.getContent() + "' (ID: " + cardId + ") is not cancellable.");
                return false;
            }

            card.setEnableFlag(0); // Logical deletion
            card.setUpdateTime(LocalDateTime.now());
            int updateCount = mapper.updateFavoriteCard(card);
            session.commit();
            System.out.println("User " + userId + " in project " + projectCode + " uncollected card successfully: " + card.getContent());
            return updateCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all enabled favorite cards for a specific user and project.
     * @param userId User ID.
     * @param projectCode Project code.
     * @return A list of enabled favorite cards.
     */
    public List<FavoriteCard> getEnabledFavoriteCards(Long userId, String projectCode) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            FavoriteCardMapper mapper = session.getMapper(FavoriteCardMapper.class);
            return mapper.selectAllEnabledFavoriteCardsByUserIdAndProjectCode(userId, projectCode);
        }
    }

    /**
     * Retrieves all cards for a specific user and project, including user's own cards (enabled/disabled) and default cards.
     * @param userId User ID.
     * @param projectCode Project code.
     * @return A list of all relevant cards.
     */
    public List<FavoriteCard> getAllCardsForUserAndProject(Long userId, String projectCode) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            FavoriteCardMapper mapper = session.getMapper(FavoriteCardMapper.class);
            return mapper.selectAllCardsForUserAndProject(userId, projectCode);
        }
    }

    /**
     * Retrieves favorite card information by its ID.
     * @param cardId Card ID.
     * @return FavoriteCard entity.
     */
    public FavoriteCard getFavoriteCardById(Long cardId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            FavoriteCardMapper mapper = session.getMapper(FavoriteCardMapper.class);
            return mapper.selectFavoriteCardById(cardId);
        }
    }
}