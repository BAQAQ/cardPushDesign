package com.example.carddesign.mapper;

import com.example.carddesign.vo.FavoriteCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FavoriteCardMapper {

    /**
     * Inserts a new favorite card.
     * @param card The favorite card entity.
     * @return The number of affected rows.
     */
    int insertFavoriteCard(FavoriteCard card);

    /**
     * Selects a favorite card by its ID.
     * @param id The ID of the favorite card.
     * @return The FavoriteCard entity.
     */
    FavoriteCard selectFavoriteCardById(@Param("id") Long id);

    /**
     * Selects all enabled favorite cards for a specific user and project.
     * @param userId The user ID.
     * @param projectCode The project code.
     * @return A list of enabled FavoriteCard entities.
     */
    List<FavoriteCard> selectAllEnabledFavoriteCardsByUserIdAndProjectCode(@Param("userId") Long userId, @Param("projectCode") String projectCode);

    /**
     * Selects a favorite card for a specific user, project, content, and business type, regardless of its enable_flag.
     * Used to check if a card already exists.
     * @param userId The user ID.
     * @param projectCode The project code.
     * @param content The content of the card.
     * @param businessType The business type of the card.
     * @return The FavoriteCard entity if found, otherwise null.
     */
    FavoriteCard selectCardByUserIdAndProjectCodeAndContentAndType(
            @Param("userId") Long userId,
            @Param("projectCode") String projectCode,
            @Param("content") String content,
            @Param("businessType") Integer businessType);

    /**
     * Selects all default favorite cards.
     * @return A list of default FavoriteCard entities.
     */
    List<FavoriteCard> selectDefaultFavoriteCards();

    /**
     * Updates an existing favorite card.
     * @param card The FavoriteCard entity with updated information.
     * @return The number of affected rows.
     */
    int updateFavoriteCard(FavoriteCard card);

    /**
     * Selects all cards for a specific user and project, including user's own cards (enabled/disabled) and default cards.
     * Used by the scheduler to determine which cards need to be pushed.
     * @param userId The user ID.
     * @param projectCode The project code.
     * @return A list of FavoriteCard entities.
     */
    List<FavoriteCard> selectAllCardsForUserAndProject(
            @Param("userId") Long userId,
            @Param("projectCode") String projectCode);
}