package org.authentication.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenManager implements TokenService<Long, String> {
    private static TokenManager instance;
    private static Map<Long, String> tokenMap;

    private TokenManager() {
        tokenMap = new ConcurrentHashMap<>();
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            synchronized (TokenManager.class) {
                if (instance == null) {
                    instance = new TokenManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void setToken(Long userId, String token) throws Exception {
        if (tokenMap.containsKey(userId))
            throw new RuntimeException("1024");
        tokenMap.put(userId, token);
    }

    @Override
    public String getToken(Long userId) {
        return tokenMap.get(userId);
    }

    @Override
    public boolean exists(String token) {
        return tokenMap.containsValue(token);
    }

    @Override
    public void removeTokenById(Long userId) {
        tokenMap.remove(userId);
    }

    @Override
    public void removeTokenByToken(String token) {
        tokenMap.entrySet().removeIf(entry -> entry.getValue().equals(token));
    }
}

