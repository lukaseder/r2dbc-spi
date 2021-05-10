/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.spi;

import org.reactivestreams.Publisher;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents the results of a query against a database.  Results can be consumed only once by either consuming {@link #getRowsUpdated()}, {@link #map(BiFunction)}, or {@link #map(Function)}.
 *
 * <p>A {@link Result} object maintains a consumption state that may be backed by a cursor pointing
 * to its current row of data or out parameters.  A {@link Result} allows read-only and forward-only consumption of statement results.
 * Thus, you can consume either {@link #getRowsUpdated()}, {@link #map(BiFunction) Rows}, or {@link #map(Function) Rows or out parameters} through it only once and only from the first to the last
 * row/parameter set.
 */
public interface Result {

    /**
     * Returns the number of rows updated by a query against a database.  May be empty if the query did not update any rows.
     *
     * @return the number of rows updated by a query against a database
     * @throws IllegalStateException if the result was consumed
     */
    Publisher<Integer> getRowsUpdated();

    /**
     * Returns a mapping of the rows that are the results of a query against a database.  May be empty if the query did not return any rows.  A {@link Row} can be only considered valid within a
     * {@link BiFunction mapping function} callback.
     *
     * @param mappingFunction the {@link BiFunction} that maps a {@link Row} and {@link RowMetadata} to a value
     * @param <T>             the type of the mapped value
     * @return a mapping of the rows that are the results of a query against a database
     * @throws IllegalArgumentException if {@code mappingFunction} is {@code null}
     * @throws IllegalStateException    if the result was consumed
     */
    <T> Publisher<T> map(BiFunction<? super Gettable, ? super ThingMetadata, ? extends T> mappingFunction);

    /**
     * Returns a mapping of the rows/out parameters that are the results of a query against a database.  May be empty if the query did not return any results.  A {@link Gettable} can be only
     * considered valid within a {@link Function mapping function} callback.
     *
     * @param mappingFunction the {@link Function} that maps a {@link Gettable} to a value
     * @param <T>             the type of the mapped value
     * @return a mapping of the rows that are the results of a query against a database
     * @throws IllegalArgumentException if {@code mappingFunction} is {@code null}
     * @throws IllegalStateException    if the result was consumed
     * @see Row
     * @see OutParameters
     * @since 0.9
     */
    default <T> Publisher<T> map(Function<Gettable, ? extends T> mappingFunction) {
        Assert.requireNonNull(mappingFunction, "mappingFunction must not be null");
        return map((row, metadata) -> mappingFunction.apply(row));
    }

}
