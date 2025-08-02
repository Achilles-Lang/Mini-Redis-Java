package cn.tedu.Achilles;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Achilles
 */
public class Store {


    static class ValueWithExpiry{
        String value;
        long expiryTime; // System.currentTimeMillis() + duration

        ValueWithExpiry(String value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            if (expiryTime == -1) {
                return false; // -1 表示永不过期
            }
            return System.currentTimeMillis() > expiryTime;
        }
    }
    //线程安全的键值存储
    public static final ConcurrentHashMap<String,ValueWithExpiry> DATA = new ConcurrentHashMap<>();

}
