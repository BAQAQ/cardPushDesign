package com.example.carddesign.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public abstract class BaseService {
    protected SqlSessionFactory sqlSessionFactory;

    public BaseService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * Gets a mapper instance from a new SqlSession. The session is closed immediately after getting the mapper.
     * This is suitable for single, non-transactional read operations.
     * For transactional operations, use getTransactionalSession() and manage the session manually.
     * @param mapperClass The class of the mapper interface.
     * @param <T> The type of the mapper.
     * @return An instance of the mapper.
     */
    protected <T> T getMapper(Class<T> mapperClass) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.getMapper(mapperClass);
        } finally {
            session.close();
        }
    }

    /**
     * Gets a SqlSession that supports manual transaction management.
     * The session must be committed or rolled back and then closed manually.
     * @return A transactional SqlSession.
     */
    protected SqlSession getTransactionalSession() {
        return sqlSessionFactory.openSession(false); // false means auto-commit is off
    }
}