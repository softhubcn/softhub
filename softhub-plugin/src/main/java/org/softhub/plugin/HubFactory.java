package org.softhub.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softhub.plugin.cache.LocalCache;
import org.softhub.plugin.service.GlueHandler;
import org.softhub.plugin.support.SpringSupport;
import groovy.lang.GroovyClassLoader;


public class HubFactory {
	private static Logger logger = LoggerFactory.getLogger(HubFactory.class);
	
	// groovy class loader
	private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
	
	// glue cache timeout / second
	private long cacheTimeout = 5000;
	public void setCacheTimeout(long cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}
	
	// code source loader
	private HubLoader hubLoader;
	public void setHubLoader(HubLoader hubLoader) {
		this.hubLoader = hubLoader;
	}
	
	// spring support
	private SpringSupport springSupport;


	public void setSpringSupport(SpringSupport springSupport) {
		this.springSupport = springSupport;
	}

	public void fillBeanField(Object instance){
		if (springSupport!=null) {
			springSupport.fillBeanField(instance);
		}
	}
	
	// load class, 
	public static String generateClassCacheKey(String name){
		return name+"_class";
	}


	public Class<?> loadClass(String name) throws Exception{
		if (name==null || name.trim().length()==0) {
			return null;
		}
		String cacheClassKey = generateClassCacheKey(name);
		Object cacheClass = LocalCache.getInstance().get(cacheClassKey);
		if (cacheClass != null) {
			return (Class<?>) cacheClass;
		}
		String codeSource = hubLoader.load(name);
		if (codeSource!=null && codeSource.trim().length()>0) {
			Class<?> clazz = groovyClassLoader.parseClass(codeSource);
			if (clazz!=null) {
				LocalCache.getInstance().set(cacheClassKey, clazz, cacheTimeout);
				logger.info(">>>>>>>>>>>> xxl-glue, fresh class, name:{}", name);
				return clazz;
			}
		}
		return null;
	}


	// load new instance, prototype
	public GlueHandler loadNewInstance(String name) throws Exception{
		if (name==null || name.trim().length()==0) {
			return null;
		}
		Class<?> clazz = loadClass(name);
		if (clazz!=null) {
			Object instance = clazz.newInstance();
			if (instance!=null) {
				if (!(instance instanceof GlueHandler)) {
					throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
							+ "cannot convert from instance["+ instance.getClass() +"] to GlueHandler");
				}
				
				this.fillBeanField(instance);
				return (GlueHandler) instance;
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
	}
	
	// // load instance, singleton
	public static String generateInstanceCacheKey(String name){
		return name+"_instance";
	}


	public GlueHandler loadInstance(String name) throws Exception{
		if (name==null || name.trim().length()==0) {
			return null;
		}
		String cacheInstanceKey = generateInstanceCacheKey(name);
		Object cacheInstance = LocalCache.getInstance().get(cacheInstanceKey);
		if (cacheInstance!=null) {
			if (!(cacheInstance instanceof GlueHandler)) {
				throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadInstance error, "
						+ "cannot convert from cacheClass["+ cacheInstance.getClass() +"] to GlueHandler");
			}
			return (GlueHandler) cacheInstance;
		}
		Object instance = loadNewInstance(name);
		if (instance!=null) {
			if (!(instance instanceof GlueHandler)) {
				throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadInstance error, "
						+ "cannot convert from instance["+ instance.getClass() +"] to GlueHandler");
			}
			
			LocalCache.getInstance().set(cacheInstanceKey, instance, cacheTimeout);
			logger.info(">>>>>>>>>>>> xxl-glue, fresh instance, name:{}", name);
			return (GlueHandler) instance;
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadInstance error, instance is null");
	}
	
}
