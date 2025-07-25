package com.example.carddesign.mapper;

import com.example.carddesign.vo.PushTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PushTimeMapper {

    /**
     * Inserts a new push time record.
     * @param pushTime The push time entity.
     * @return The number of affected rows.
     */
    int insertPushTime(PushTime pushTime);

    /**
     * Selects push time records by push_config_id.
     * @param pushConfigId The ID of the associated push configuration.
     * @return A list of PushTime entities.
     */
    List<PushTime> selectPushTimesByConfigId(@Param("pushConfigId") Long pushConfigId);

    /**
     * Updates an existing push time record.
     * @param pushTime The PushTime entity with updated information.
     * @return The number of affected rows.
     */
    int updatePushTime(PushTime pushTime);

    /**
     * Logically deletes push time records by setting enable_flag to 0 for a specific ID.
     * @param id The ID of the push time record.
     * @return The number of affected rows.
     */
    int logicDeletePushTime(@Param("id") Long id);

    /**
     * Logically deletes all associated push time records for a specific push_config_id.
     * @param pushConfigId The ID of the associated push configuration.
     * @return The number of affected rows.
     */
    int logicDeletePushTimesByConfigId(@Param("pushConfigId") Long pushConfigId);

    /**
     * Physically deletes all associated push time records for a specific push_config_id.
     * Used when the push frequency type changes.
     * @param pushConfigId The ID of the associated push configuration.
     * @return The number of affected rows.
     */
    int deletePushTimesByConfigId(@Param("pushConfigId") Long pushConfigId);
}