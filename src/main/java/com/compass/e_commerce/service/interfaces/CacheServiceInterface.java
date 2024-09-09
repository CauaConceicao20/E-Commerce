package com.compass.e_commerce.service.interfaces;

public interface CacheServiceInterface {
    void evictAllCacheValues(String cacheName);
}
