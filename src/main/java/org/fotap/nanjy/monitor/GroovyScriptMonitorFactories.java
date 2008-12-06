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
        loader = new GroovyClassLoader();
        loader.addURL( source );
    }

    @Override
    public MonitorFactory factoryFor( ObjectName mbean ) {
        return factoryForPackage( mbean.getDomain() );
    }

    private MonitorFactory factoryForPackage( String pkg ) {
        Class<?> clazz;

        try {
            clazz = loader.loadClass( pkg + ".MonitorFactory", true, false );
        } catch( ClassNotFoundException e ) {
            logger.debug( "No monitor for package: " + pkg, e );

            int i = pkg.lastIndexOf( '.' );

            if ( i > 0 ) {
                return factoryForPackage( pkg.substring( 0, i ) );
            }

            return null;
        }

        if ( MonitorFactory.class.isAssignableFrom( clazz ) ) {
            try {
                return clazz.asSubclass( MonitorFactory.class ).newInstance();
            } catch( Exception e ) {
                logger.error( "Unable to create new instance of " + clazz.getName(), e );

                return null;
            }
        }

        logger.warn( "Found MonitorFactory {}, but it isn't a {}", clazz.getName(), MonitorFactory.class.getName() );

        return null;
    }
}
