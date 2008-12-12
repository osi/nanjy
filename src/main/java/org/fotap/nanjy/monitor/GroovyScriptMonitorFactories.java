package org.fotap.nanjy.monitor;

import java.net.URL;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.GroovyClassLoader;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class GroovyScriptMonitorFactories implements MonitorFactories {
    private static final Logger logger = LoggerFactory.getLogger( GroovyScriptMonitorFactories.class );

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
        Class<?> clazz;

        try {
            clazz = loader.loadClass( pkg + ".MonitorFactories", true, false );
        } catch( ClassNotFoundException e ) {
            logger.trace( "No monitor for package: " + pkg, e );

            int i = pkg.lastIndexOf( '.' );

            if ( i > 0 ) {
                return factoryForPackage( pkg.substring( 0, i ) );
            }

            return null;
        }

        if ( MonitorFactories.class.isAssignableFrom( clazz ) ) {
            try {
                return clazz.asSubclass( MonitorFactories.class ).newInstance();
            } catch( Exception e ) {
                logger.error( "Unable to create new instance of " + clazz.getName(), e );

                return null;
            }
        }

        logger.warn( "Found MonitorFactories {}, but it isn't a {}",
                     clazz.getName(),
                     MonitorFactories.class.getName() );

        return null;
    }
}
