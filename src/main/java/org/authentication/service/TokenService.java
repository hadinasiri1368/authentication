package org.authentication.service;

public interface TokenService<K, V> {
    void setToken(K id, V value) throws Exception;

    String getToken(K id);

    boolean exists(V value);

    void removeTokenById(K id);

    void removeTokenByToken(V value);
}