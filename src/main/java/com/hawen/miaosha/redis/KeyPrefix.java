package com.hawen.miaosha.redis;

public interface KeyPrefix
{
	int expireSeconds();

	String getPrefix();
}
