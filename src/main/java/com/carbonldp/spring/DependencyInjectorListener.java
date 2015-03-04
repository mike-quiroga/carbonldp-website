package com.carbonldp.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.carbonldp.exceptions.StupidityException;

public class DependencyInjectorListener implements BeanPostProcessor, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private ApplicationContext applicationContext;

	private final List<Object> beansNeedingInjection = new ArrayList<Object>();

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Method[] methods = bean.getClass().getMethods();
		for (Method method : methods) {
			Inject injectAnnotation = method.getAnnotation(Inject.class);
			if ( injectAnnotation != null ) {
				beansNeedingInjection.add(bean);
				break;
			}
		}
		return bean;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		injectBeans();
	}

	private void injectBeans() {
		for (Object bean : beansNeedingInjection) {
			Method[] methods = bean.getClass().getMethods();
			for (Method method : methods) {
				Inject injectAnnotation = method.getAnnotation(Inject.class);
				if ( injectAnnotation == null ) continue;

				Object dependency = null;

				String beanID = injectAnnotation.id();
				if ( wasAssigned(beanID) ) dependency = getDependency(beanID);
				else dependency = getDependencyFromParameterClass(method);

				try {
					method.invoke(bean, dependency);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new StupidityException(e);
				}
			}
		}
	}

	private Object getDependency(String beanID) {
		return this.applicationContext.getBean(beanID);
	}

	private Object getDependencyFromParameterClass(Method method) {
		Class<?> dependencyClass = getDependencyClassFromParameter(method);
		return this.applicationContext.getBean(dependencyClass);
	}

	private Class<?> getDependencyClassFromParameter(Method method) {
		Parameter[] parameters = method.getParameters();
		if ( parameters.length > 1 ) throw new StupidityException("The method annotated isn't a setter.");
		for (Parameter parameter : method.getParameters()) {
			return parameter.getType();
		}
		throw new StupidityException("The method annotated doesn't have a parameter");
	}

	private boolean wasAssigned(String id) {
		return ! "[unassigned]".equals(id);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
