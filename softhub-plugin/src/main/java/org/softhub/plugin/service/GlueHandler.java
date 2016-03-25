package org.softhub.plugin.service;

import java.util.Map;


public interface GlueHandler {
	
	/**
	 * defaule method
	 * @param params
	 * @return
	 */
	public Object handle(Map<String, Object> params);
	
}
