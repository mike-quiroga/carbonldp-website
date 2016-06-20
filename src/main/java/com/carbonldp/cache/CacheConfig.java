package com.carbonldp.cache;

import com.carbonldp.authorization.acl.ACLPermissionEvaluator;
import com.carbonldp.authorization.acl.CachedPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

/**
 * Configuration in charge of registering all the cache system
 *
 * @author MiguelAraCo
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.cache.annotation.CacheConfig
 * @see CachingConfigurer
 * @see <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html">Cache Abstraction</a>
 * @since 0.37.1
 */
@Configuration
@EnableAspectJAutoProxy
@EnableCaching
public class CacheConfig implements CachingConfigurer {

	@Autowired
	private ACLPermissionEvaluator permissionEvaluator;

	@Bean
	@Override
	public CacheManager cacheManager() {
		return new CaffeineCacheManager();
	}

	@Bean
	@Override
	public CacheResolver cacheResolver() {
		return new SimpleCacheResolver( cacheManager() );
	}

	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return new SimpleKeyGenerator();
	}

	@Bean
	@Override
	public CacheErrorHandler errorHandler() {
		return new SimpleCacheErrorHandler();
	}

	@Bean
	public CachedPermissionEvaluator cachedPermissionEvaluator() {
		CachedPermissionEvaluator cachedPermissionEvaluator = new CachedPermissionEvaluator();
		cachedPermissionEvaluator.setPermissionEvaluator( permissionEvaluator );
		permissionEvaluator.setCachedPermissionEvaluator( cachedPermissionEvaluator );
		return new CachedPermissionEvaluator();
	}

	/**
	 * Method needed to wire cachedPermissionEvaluator with ACLPermissionEvaluator. This is needed
	 * because SecurityConfig is being initialized without Proxies. Calls to any bean define there
	 * cannot be intercepted with aspects.
	 *
	 * @see <a href="https://jira.base22.com/browse/LDP-705">LDP-705</a>
	 */
	@PostConstruct
	public void wirePermissionEvaluator() {
		CachedPermissionEvaluator cachedPermissionEvaluator = cachedPermissionEvaluator();
		cachedPermissionEvaluator.setPermissionEvaluator( permissionEvaluator );
		permissionEvaluator.setCachedPermissionEvaluator( cachedPermissionEvaluator );
	}
}
