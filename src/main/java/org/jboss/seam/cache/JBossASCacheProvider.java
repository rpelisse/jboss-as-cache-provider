package org.jboss.seam.cache;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.infinispan.Cache;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * A simple cache provider looking up a cache entry defined within JBoss AS configuration.
 * 
 * @author Romain Pelisse - <belaran@redhat.com>
 *
 */
@Name("org.jboss.seam.cache.cacheProvider")
@Scope(APPLICATION)
@BypassInterceptors
@Install(value = false, precedence = BUILT_IN, classDependencies = { "org.infinispan.tree.TreeCache", "org.jgroups.MembershipListener" })
@AutoCreate
public class JBossASCacheProvider<T> extends CacheProvider<T> {

	public static final Object NO_SUCH_ENTRY = null;
    private static final LogProvider LOGGER = Logging.getLogProvider(JBossASCacheProvider.class);
    
    private String container ;
    private String name;
    
    private Cache<String, T> cache;
	
    public JBossASCacheProvider() {
     	super();
    	String config = super.getConfiguration();
    	if ( LOGGER.isDebugEnabled() ) LOGGER.debug("Provider constructed with configuration:" + config);
    }
    
	@Override
	public void clear() {
    	if ( LOGGER.isDebugEnabled() ) LOGGER.debug("clear() invoked");
		if ( cache == null )
			throw new IllegalStateException("Cache is 'null' but clear() was invoked !");
		cache.clear();
	}

	@Override
	public Object get(String regionName, String key) {
		if ( LOGGER.isDebugEnabled() ) LOGGER.debug("get() invoked with " + regionName + " and " + key);
		if ( cache == null )
			throw new IllegalStateException("Cache is 'null' but get() was invoked !");
		
		if ( cache.containsKey(regionName) ) {
	    	if ( LOGGER.isDebugEnabled() ) LOGGER.debug("Entry with key [" + regionName + "] found, returning value");
			return cache.get(key);
		}
		else {
	    	if ( LOGGER.isDebugEnabled() ) LOGGER.debug("No entry with key: " + regionName + " and " + key);
			return NO_SUCH_ENTRY;
		}
		
	}

	@Override
	public T getDelegate() {
		throw new UnsupportedOperationException("Unsupported Method for " + this.getClass().getName() + " ");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void put(String key, String region, Object value) {
    	if ( LOGGER.isDebugEnabled() ) LOGGER.debug("put() invoked with key [" + key + "] and region [" + region + "] for value [" + value + "]");
		if ( region != null && ! "".equals(region))
			throw new UnsupportedOperationException("This provider " + this.getClass().getCanonicalName() + " does not support region - this parameter should be set o 'null' or an empty string.");
						
		if ( cache == null )
			throw new IllegalStateException("Cache is 'null' but put() was invoked !");
		cache.put(key, (T) value);
	}

	@Override
	public void remove(String key, String region) {
    	if ( LOGGER.isDebugEnabled() ) LOGGER.debug("remove() invoked with key [" + key + "] and region [" + region + "]");
		if ( region != null && ! "".equals(region))
			throw new UnsupportedOperationException("This provider " + this.getClass().getCanonicalName() + " does not support region - this parameter should be set o 'null' or an empty string.");

		if ( cache == null )
			throw new IllegalStateException("Cache is 'null' but remove() was invoked !");
	
		cache.remove(key);
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Cache<String, T> getCache() {
		return cache;
	}

	public void setCache(Cache<String, T> cache) {
		this.cache = cache;
	}
}
