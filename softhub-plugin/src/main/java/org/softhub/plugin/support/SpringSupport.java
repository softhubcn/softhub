package org.softhub.plugin.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;


public class SpringSupport implements ApplicationContextAware {
	private ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void fillBeanField(Object instance){
		if (instance==null) {
			return;
		}
		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			Object fieldBean = null;
			// with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired
			if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
				try {
					fieldBean = applicationContext.getBean(field.getName());
				} catch (Exception e) {
				}
				if (fieldBean==null ) {
					fieldBean = applicationContext.getBean(field.getType());
				}
			} else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
				fieldBean = applicationContext.getBean(field.getType());		
			}
			
			if (fieldBean!=null) {
				field.setAccessible(true);
				try {
					field.set(instance, fieldBean);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
