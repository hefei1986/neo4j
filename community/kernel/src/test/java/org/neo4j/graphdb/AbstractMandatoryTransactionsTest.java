/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.graphdb;

import org.junit.After;
import org.junit.Before;

import org.neo4j.test.TestGraphDatabaseFactory;

import static org.junit.Assert.fail;

public abstract class AbstractMandatoryTransactionsTest<T>
{
    private GraphDatabaseService graphDatabaseService;

    @Before
    public void before()
    {
        graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    @After
    public void after()
    {
        graphDatabaseService.shutdown();
    }

    public T obtainEntity()
    {
        Transaction tx = graphDatabaseService.beginTx();
        try
        {
            T result = obtainEntityInTransaction( graphDatabaseService );
            tx.success();

            return result;
        }
        finally
        {
            tx.finish();
        }
    }

    protected abstract T obtainEntityInTransaction( GraphDatabaseService graphDatabaseService );

    public static <T> void assertFacadeMethodsThrowNotInTransaction( T entity, Iterable<FacadeMethod<T>> methods )
    {
        for ( FacadeMethod<T> method : methods )
        {
            try
            {
                method.call( entity );

                fail( "Transactions are mandatory, also for reads: " + method );
            }
            catch ( NotInTransactionException e )
            {
                // awesome
            }
        }
    }
}