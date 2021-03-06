/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of ONgDB Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) as found
 * in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 */
package org.neo4j.metrics.output;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.neo4j.logging.RotatingFileOutputStreamSupplier;
import org.neo4j.test.rule.TestDirectory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RotatableCsvReporterTest
{
    @Rule
    public TestDirectory testDirectory = TestDirectory.testDirectory();
    private RotatingFileOutputStreamSupplier fileOutputStreamSupplier = mock( RotatingFileOutputStreamSupplier.class );

    @Test
    public void stopAllWritersOnStop() throws IOException
    {
        OutputStream outputStream = mock( OutputStream.class );
        when( fileOutputStreamSupplier.get() ).thenReturn( outputStream );
        RotatableCsvReporter reporter =
                new RotatableCsvReporter( mock( MetricRegistry.class ), Locale.US, TimeUnit.SECONDS, TimeUnit.SECONDS,
                        Clock.defaultClock(), testDirectory.directory(),
                        ( file, rotationListener ) -> fileOutputStreamSupplier );
        TreeMap<String,Gauge> gauges = new TreeMap<>();
        gauges.put( "a", () -> ThreadLocalRandom.current().nextLong() );
        gauges.put( "b", () -> ThreadLocalRandom.current().nextLong() );
        gauges.put( "c", () -> ThreadLocalRandom.current().nextLong() );
        reporter.report( gauges, new TreeMap<>(), new TreeMap<>(), new TreeMap<>(), new TreeMap<>() );

        gauges.put( "b", () -> ThreadLocalRandom.current().nextLong() );
        gauges.put( "c", () -> ThreadLocalRandom.current().nextLong() );
        gauges.put( "d", () -> ThreadLocalRandom.current().nextLong() );
        reporter.report( gauges, new TreeMap<>(), new TreeMap<>(), new TreeMap<>(), new TreeMap<>() );

        reporter.stop();
        verify( fileOutputStreamSupplier, times( 4 ) ).close();
    }
}
