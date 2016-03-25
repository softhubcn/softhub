package com.xxl.groovy.core.cache;

public interface ICache {
	
	public boolean set(String key, Object value);
	
	public boolean set(String key, Object value, long timeout);
	
	public Object get(String key);
	
	public boolean remove(String key);
	
}
