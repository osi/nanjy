package org.fotap.nanjy.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.GroovyClassLoader;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class GroovyScriptMonitorFactories implements MonitorFactories {
    private static final Logger logger = LoggerFactory.getLogger( GroovyScriptMonitorFactories.class );

    private final Map<String, MonitorFactories> factoriesMap = new ConcurrentHashMap<String, MonitorFactories>();
    private final GroovyClassLoader loader;

    public GroovyScriptMonitorFactories( URL source ) {
        logger.debug( "Will load scripts from {}", source );

        loader = new GroovyClassLoader();
        loader.addURL( source );
    }

    @Override
    public MonitorFactory factoryFor( ObjectName mbean ) {
        MonitorFactories factories = factoryForPackage( mbean.getDomain() );
        return null == factories ? null : factories.factoryFor( mbean );
    }

    private MonitorFactories factoryForPackage( String pkg ) {
        MonitorFactories factories = factoriesMap.get( pkg );

        if ( null != factories ) {
            return factories;
        }

        Class<?> clazz = findFactoryClass( pkg );

        if ( null == clazz ) {
            return factoryForSuperPackage( pkg );
        } else if ( !MonitorFactories.class.isAssignableFrom( clazz ) ) {
            logger.warn( "Found MonitorFactories {}, but it isn't a {}",
                         clazz.getName(),
                         MonitorFactories.class.getName() );

            return null;
        }

        try {
            factories = clazz.asSubclass( MonitorFactories.class ).newInstance();

            factoriesMap.put( pkg, factories );

            return factories;
        } catch( Exception e ) {
            logger.error( "Unable to create new instance of " + clazz.getName(), e );

            return null;
        }
    }

    private Class<?> findFactoryClass( String pkg ) {
        Class<?> clazz = findClassInPackage( pkg );

        if ( null != clazz ) {
            return clazz;
        }

        return findClassByResource( pkg );
    }

    private Class<?> findClassByResource( String pkg ) {
        String resource = "META-INF/nanjy/" + pkg + "/MonitorFactories.properties";
        InputStream stream = loader.getResourceAsStream( resource );

        if ( null == stream ) {
            logger.trace( "No resource declaration for package: {} - {}", pkg, resource );
            return null;
        }

        LineNumberReader reader = null;
        String className;

        try {
            reader = new LineNumberReader( new InputStreamReader( stream, "UTF-8" ) );
            className = reader.readLine();
        } catch( IOException e ) {
            logger.warn( "Unable to read resource " + resource, e );
            return null;
        } finally {
            if ( null != reader ) {
                try {
                    reader.close();
                } catch( IOException e ) {
                    logger.trace( "Unable to close resource reader", e );
                }
            }
        }

        if ( null == className ) {
            logger.warn( "Found empty resource declaration {}", resource );
            return null;
        }

        try {
            return loader.loadClass( className );
        } catch( ClassNotFoundException e ) {
            logger.warn( "Resource declaration " + resource + " specifies unknown class " + className, e );
            return null;
        }
    }

    private MonitorFactories factoryForSuperPackage( String pkg ) {
        int i = pkg.lastIndexOf( '.' );

        if ( i > 0 ) {
            return factoryForPackage( pkg.substring( 0, i ) );
        }

        return null;
    }

    private Class<?> findClassInPackage( String pkg ) {
        try {
            return loader.loadClass( pkg + ".MonitorFactories", true, false );
        } catch( ClassNotFoundException e ) {
            logger.trace( "No monitor class in package: " + pkg, e );

            return null;
        }
    }
}
