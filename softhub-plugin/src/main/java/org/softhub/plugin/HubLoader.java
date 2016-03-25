package org.softhub.plugin;


public interface HubLoader {

	/**
	 * load code source by name, ensure every load is the latest.
	 * @param name
	 * @return
	 */
	public String load(String name);
	
}
