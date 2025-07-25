package com.example.carddesign.mapper;

import com.example.carddesign.vo.PushConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PushConfigMapper {

    /**
     * Inserts a new push configuration.
     * @param config The push configuration entity.
     * @return The number of affected rows.
     */
    int insertPushConfig(PushConfig config);

    /**
     * Selects a push configuration by its ID, including associated PushTime records.
     * @param id The ID of the push configuration.
     * @return The PushConfig entity.
     */
    PushConfig selectPushConfigById(@Param("id") Long id);

    /**
     * Selects all enabled push configurations for a specific user and project, including associated PushTime records.
     * @param userId The user ID.
     * @param projectCode The project code.
     * @return A list of enabled PushConfig entities.
     */
    List<PushConfig> selectAllEnabledPushConfigsByUserIdAndProjectCode(
            @Param("userId") Long userId,
            @Param("projectCode") String projectCode);

    /**
     * Selects a push configuration for a specific user, project, and favorite_id, regardless of its enable_flag.
     * Used to check if a push configuration already exists for a favorite card.
     * @param userId The user ID.
     * @param projectCode The project code.
     * @param favoriteId The ID of the associated favorite card.
     * @return The PushConfig entity if found, otherwise null.
     */
    PushConfig selectConfigByUserIdAndProjectCodeAndFavoriteId(
            @Param("userId") Long userId,
            @Param("projectCode") String projectCode,
            @Param("favoriteId") Long favoriteId);

    /**
     * Updates an existing push configuration.
     * @param config The PushConfig entity with updated information.
     * @return The number of affected rows.
     */
    int updatePushConfig(PushConfig config);

    /**
     * Selects all enabled push configurations (for the scheduler).
     * @return A list of enabled PushConfig entities.
     */
    List<PushConfig> selectAllEnabledPushConfigs();
}