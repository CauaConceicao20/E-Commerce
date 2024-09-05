package com.compass.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CacheService {

    private final CacheManager cacheManager;
    private final GameService gameService;

    @Autowired
    public CacheService(CacheManager cacheManager, GameService gameService) {
        this.cacheManager = cacheManager;
        this.gameService = gameService;
    }

    public void evictAllCacheValues(String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }
}