package com.stream.flink;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * https://zhuanlan.zhihu.com/p/347246715
 */
public class CaffeineTest {

    private static AsyncLoadingCache<String, String> cache = null;

    static {
        cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 构建一个异步缓存元素操作并返回一个future
                .buildAsync((key, executor) -> createExpensiveGraphAsync(key, executor));
    }

    private static CompletableFuture<String> createExpensiveGraphAsync(String key, Executor executor) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("===================");
                return "小明";
            }
        }, executor);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //查找缓存元素，如果其不存在，将会异步进行生成
        cache.get("name").thenAccept(name->{
            System.out.println("name:" + name);
        });

        cache.get("name1").thenAccept(name->{
            System.out.println("name1:" + name);
        });

        cache.get("name").thenAccept(name->{
            System.out.println("name:" + name);
        });
    }
}