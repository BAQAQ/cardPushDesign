package com.example.carddesign.service;

import com.example.carddesign.mapper.PushConfigMapper;
import com.example.carddesign.mapper.PushTimeMapper;
import com.example.carddesign.vo.FavoriteCard;
import com.example.carddesign.vo.PushConfig;
import com.example.carddesign.vo.PushTime;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.LocalDateTime;
import java.util.List;

public class PushConfigService extends BaseService {

    public PushConfigService(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    /**
     * Creates or updates a push configuration. If a configuration for the given user, project, and favoriteId already exists (enabled or disabled), it will be updated and activated. Otherwise, a new record is inserted.
     * @param userId User ID.
     * @param projectCode Project code.
     * @param favoriteId Favorite card ID.
     * @param pushFrequency Push frequency.
     * @param content Card content (can be obtained from FavoriteCard).
     * @param businessType Business type (can be obtained from FavoriteCard).
     * @param pushTimes List of push times.
     * @return PushConfig ID, or null if failed.
     */
    public Long createOrUpdatePushConfig(Long userId, String projectCode, Long favoriteId,
                                         String pushFrequency, String content, Integer businessType,
                                         List<PushTime> pushTimes) {
        try (SqlSession session = getTransactionalSession()) {
            PushConfigMapper configMapper = session.getMapper(PushConfigMapper.class);
            PushTimeMapper timeMapper = session.getMapper(PushTimeMapper.class);

            // Check if a push configuration record already exists for   this user, project, and favoriteId (regardless of enable_flag)
            PushConfig existingConfig = configMapper.selectConfigByUserIdAndProjectCodeAndFavoriteId(
                    userId, projectCode, favoriteId);

            if (existingConfig != null) {
                // If exists, update it
                System.out.println("Updating existing push configuration (ID: " + existingConfig.getId() + ")...");
                existingConfig.setPushFrequency(pushFrequency);
                existingConfig.setContent(content);
                existingConfig.setBusinessType(businessType);
                existingConfig.setEnableFlag(1); // Set to enabled when updating
                existingConfig.setUpdateTime(LocalDateTime.now());
                existingConfig.setUpdateUser(-1L);
                configMapper.updatePushConfig(existingConfig);

                // Physically delete old push times (because the time rules might change completely)
                timeMapper.deletePushTimesByConfigId(existingConfig.getId());

                // Insert new push times
                for (PushTime pt : pushTimes) {
                    pt.setPushConfigId(existingConfig.getId());
                    pt.setCreateTime(LocalDateTime.now());
                    pt.setUpdateTime(LocalDateTime.now());
                    timeMapper.insertPushTime(pt);
                }
                session.commit();
                System.out.println("Push configuration updated successfully, ID: " + existingConfig.getId());
                return existingConfig.getId();

            } else {
                // If not exists, create a new one
                System.out.println("Creating new push configuration...");
                PushConfig newConfig = new PushConfig(userId, projectCode, pushFrequency, content, businessType, favoriteId);
                configMapper.insertPushConfig(newConfig);
                Long newConfigId = newConfig.getId();

                for (PushTime pt : pushTimes) {
                    pt.setPushConfigId(newConfigId);
                    pt.setCreateTime(LocalDateTime.now());
                    pt.setUpdateTime(LocalDateTime.now());
                    timeMapper.insertPushTime(pt);
                }
                session.commit();
                System.out.println("Push configuration created successfully, ID: " + newConfigId);
                return newConfigId;
            }
        } catch (Exception e) {
            System.err.println("Failed to create or update push configuration: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Logically deletes a push configuration (sets enable_flag to 0).
     * @param configId Push configuration ID.
     * @return true if successful, false otherwise.
     */
    public boolean deletePushConfig(Long configId) {
        try (SqlSession session = getTransactionalSession()) {
            PushConfigMapper configMapper = session.getMapper(PushConfigMapper.class);
            PushTimeMapper timeMapper = session.getMapper(PushTimeMapper.class);

            PushConfig config = configMapper.selectPushConfigById(configId);
            if (config == null || config.getEnableFlag() == 0) {
                System.out.println("Push configuration (ID: " + configId + ") does not exist or is already disabled. No action needed.");
                return false;
            }

            config.setEnableFlag(0); // Logical deletion
            config.setUpdateTime(LocalDateTime.now());
            configMapper.updatePushConfig(config);

            // Logically delete associated push times
            timeMapper.logicDeletePushTimesByConfigId(configId);
            session.commit();
            System.out.println("Logical deletion of push configuration (ID: " + configId + ") successful.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all enabled push configurations (for the scheduler).
     * @return A list of enabled PushConfig entities.
     */
    public List<PushConfig> getAllEnabledPushConfigs() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PushConfigMapper mapper = session.getMapper(PushConfigMapper.class);
            return mapper.selectAllEnabledPushConfigs();
        }
    }

    /**
     * Retrieves push configuration by its ID.
     * @param configId Push configuration ID.
     * @return PushConfig entity.
     */
    public PushConfig getPushConfigById(Long configId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            PushConfigMapper mapper = session.getMapper(PushConfigMapper.class);
            return mapper.selectPushConfigById(configId);
        }
    }

}