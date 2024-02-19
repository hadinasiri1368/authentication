package org.authentication.common;

import java.util.HashMap;
import java.util.Map;

public class TokenManager {
    private static TokenManager instance;
    private static Map<Long, String> tokenMap;

    private TokenManager() {
        tokenMap = new HashMap<>();
    }

    public static TokenManager getInstance() {
        if (CommonUtils.isNull(instance)) {
            instance = new TokenManager();
        }
        return instance;
    }

    public void setToken(Long userId, String token) throws Exception {
        if (tokenMap.containsKey(userId))
            throw new RuntimeException("this.user.has.token");
        tokenMap.put(userId, token);
    }

    public String getToken(Long userId) {
        if (!tokenMap.containsKey(userId))
            return null;
        return tokenMap.get(userId);
    }

    public boolean HasToken(String token) {
        for (Long key : tokenMap.keySet()) {
            if (tokenMap.get(key).equals(token))
                return true;
        }
        return false;
    }

    public void removeTokenByUserId(Long userId) {
        if (tokenMap.containsKey(userId))
            tokenMap.remove(userId);
    }

    public void removeTokenByToken(String token) {
        for (Long key : tokenMap.keySet()) {
            if (tokenMap.get(key).equals(token))
                tokenMap.remove(key);
        }
    }
}
