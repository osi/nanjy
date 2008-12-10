package org.fotap.nanjy;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
class Fibers {
    private final ThreadFiber core;
    private final Fiber sampler;

    Fibers() {
        final PoolFiberFactory samplers = new PoolFiberFactory( Executors.newFixedThreadPool( 1, new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger( 0 );

            @Override
            public Thread newThread( Runnable r ) {
                return new Thread( r, "sampler-" + counter.getAndIncrement() );
            }
        } ) );

        core = new ThreadFiber( new RunnableExecutorImpl(), "core", false );

        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                samplers.dispose();

                core.dispose();
                core.join();
            }
        } );

        sampler = samplers.create();
        sampler.start();
        core.start();
    }

    Fiber core() {
        return core;
    }

    Fiber sampler() {
        return sampler;
    }
}
