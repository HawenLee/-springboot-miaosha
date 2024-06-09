package com.hawen.miaosha.redis;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
public class AbstractPrefix implements KeyPrefix {
    private final int expireSeconds;
    private final String prefix;

    public AbstractPrefix(String prefix) {
        this(-1, prefix);
    }

    public AbstractPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }


}
