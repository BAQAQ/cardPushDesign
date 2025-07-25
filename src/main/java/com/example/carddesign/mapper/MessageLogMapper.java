package com.example.carddesign.mapper;

import com.example.carddesign.vo.MessageLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageLogMapper {

    /**
     * Inserts a new message log record.
     * @param messageLog The message log entity.
     * @return The number of affected rows.
     */
    int insertMessageLog(MessageLog messageLog);

    /**
     * Selects a message log record by its ID.
     * @param id The ID of the message log.
     * @return The MessageLog entity.
     */
    MessageLog selectMessageLogById(@Param("id") Long id);

    /**
     * Selects all enabled message log records for a specific favorite card ID, ordered by creation time descending.
     * @param favoriteId The ID of the associated favorite card.
     * @return A list of MessageLog entities.
     */
    List<MessageLog> selectEnabledMessageLogsByFavoriteId(@Param("favoriteId") Long favoriteId);

    /**
     * Selects all enabled message log records, ordered by creation time descending.
     * @return A list of MessageLog entities.
     */
    List<MessageLog> selectAllEnabledMessageLogs();

    /**
     * Updates an existing message log record.
     * @param messageLog The MessageLog entity with updated information.
     * @return The number of affected rows.
     */
    int updateMessageLog(MessageLog messageLog);

    /**
     * Logically deletes a message log record by setting enable_flag to 0 for a specific ID.
     * @param id The ID of the message log.
     * @return The number of affected rows.
     */
    int logicDeleteMessageLog(@Param("id") Long id);

    /**
     * Logically deletes all associated message log records for a specific favorite_id.
     * @param favoriteId The ID of the associated favorite card.
     * @return The number of affected rows.
     */
    int logicDeleteMessageLogsByFavoriteId(@Param("favoriteId") Long favoriteId);
}