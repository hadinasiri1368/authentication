package org.authentication.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CaptchaTokenManager implements TokenService<String, String> {
    private static CaptchaTokenManager instance;
    private static Map<String, String> tokenMap;

    private CaptchaTokenManager() {
        tokenMap = new ConcurrentHashMap<>();
    }

    public static CaptchaTokenManager getInstance() {
        if (instance == null) {
            synchronized (TokenManager.class) {
                if (instance == null) {
                    instance = new CaptchaTokenManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void setToken(String uuid, String token) throws Exception {
        tokenMap.put(uuid, token);
    }

    @Override
    public String getToken(String uuid) {
        return tokenMap.get(uuid);
    }

    @Override
    public boolean exists(String token) {
        return tokenMap.containsValue(token);
    }

    @Override
    public void removeTokenById(String uuid) {
        tokenMap.remove(uuid);
    }

    @Override
    public void removeTokenByToken(String token) {
        tokenMap.entrySet().removeIf(entry -> entry.getValue().equals(token));
    }
}
