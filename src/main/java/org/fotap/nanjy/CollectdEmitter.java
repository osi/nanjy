package org.fotap.nanjy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.fotap.nanjy.monitor.Sample;
import org.jetlang.core.Callback;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr" } )
class CollectdEmitter implements Callback<Sample> {
    private final String hostname;

    public CollectdEmitter() {
        hostname = hostname();
    }

    private static String hostname() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String name = address.getHostName();
            int dot = name.indexOf( '.' );

            return dot > 0 ? name.substring( 0, dot ) : name;
        } catch( UnknownHostException e ) {
            throw new RuntimeException( "Unable to determine host name", e );
        }
    }

    @Override
    public void onMessage( Sample sample ) {
        StringBuilder sb = new StringBuilder( 256 );
        sb.append( "PUTVAL " )
            .append( hostname )
            .append( "/jvm-" )
            .append( sample.getDescriptor().getName() )
            .append( " " )
            .append( TimeUnit.MILLISECONDS.toSeconds( sample.takenAt() ) );

        for ( Number number : sample.getValues() ) {
            sb.append( ":" ).append( number );
        }

        System.out.println( sb );
    }
}
