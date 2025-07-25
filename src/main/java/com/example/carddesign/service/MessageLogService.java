package com.example.carddesign.service;

import com.example.carddesign.mapper.MessageLogMapper;
import com.example.carddesign.vo.MessageLog;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class MessageLogService extends BaseService {

    public MessageLogService(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    /**
     * Inserts a message log record.
     * @param favoriteId Favorite card ID.
     * @param messageContent Message content.
     * @return The number of affected rows.
     */
    public int addMessage(Long favoriteId, String messageContent) {
        try (SqlSession session = getTransactionalSession()) {
            MessageLogMapper mapper = session.getMapper(MessageLogMapper.class);
            MessageLog message = new MessageLog(favoriteId, messageContent);
            int count = mapper.insertMessageLog(message);
            session.commit();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Retrieves the latest enabled message for a specific favorite card ID.
     * @param favoriteId Favorite card ID.
     * @return The latest MessageLog entity, or null if not found.
     */
    public MessageLog getLatestMessageByFavoriteId(Long favoriteId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            MessageLogMapper mapper = session.getMapper(MessageLogMapper.class);
            List<MessageLog> messages = mapper.selectEnabledMessageLogsByFavoriteId(favoriteId);
            if (!messages.isEmpty()) {
                return messages.get(0); // Assumes ordered by create_time DESC, so the first is the latest
            }
            return null;
        }
    }

    /**
     * Logically deletes a message record.
     * @param messageId Message ID.
     * @return true if successful, false otherwise.
     */
    public boolean deleteMessage(Long messageId) {
        try (SqlSession session = getTransactionalSession()) {
            MessageLogMapper mapper = session.getMapper(MessageLogMapper.class);
            int count = mapper.logicDeleteMessageLog(messageId);
            session.commit();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}