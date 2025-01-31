package com.compass.e_commerce.service;

import com.compass.e_commerce.service.interfaces.CacheServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CacheService implements CacheServiceImp {

    private final CacheManager cacheManager;

    public void addTokenToCache(String userLogin, String token) {
        Cache cache = cacheManager.getCache("token");
        if (cache != null) {
            cache.put(userLogin, token);
        }
    }

    public void evictAllCacheValues(String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }
}