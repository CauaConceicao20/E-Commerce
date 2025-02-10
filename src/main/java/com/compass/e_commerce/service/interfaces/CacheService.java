package com.compass.e_commerce.service.interfaces;

public interface CacheService {
    void evictAllCacheValues(String cacheName);
}
