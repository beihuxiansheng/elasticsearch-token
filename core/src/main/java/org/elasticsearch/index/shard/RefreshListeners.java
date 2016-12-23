begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ReferenceManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Tuple
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|Translog
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|IntSupplier
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Allows for the registration of listeners that are called when a change becomes visible for search. This functionality is exposed from  * {@link IndexShard} but kept here so it can be tested without standing up the entire thing.  *  * When {@link Closeable#close()}d it will no longer accept listeners and flush any existing listeners.  */
end_comment

begin_class
DECL|class|RefreshListeners
specifier|public
specifier|final
class|class
name|RefreshListeners
implements|implements
name|ReferenceManager
operator|.
name|RefreshListener
implements|,
name|Closeable
block|{
DECL|field|getMaxRefreshListeners
specifier|private
specifier|final
name|IntSupplier
name|getMaxRefreshListeners
decl_stmt|;
DECL|field|forceRefresh
specifier|private
specifier|final
name|Runnable
name|forceRefresh
decl_stmt|;
DECL|field|listenerExecutor
specifier|private
specifier|final
name|Executor
name|listenerExecutor
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
comment|/**      * Is this closed? If true then we won't add more listeners and have flushed all pending listeners.      */
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
comment|/**      * List of refresh listeners. Defaults to null and built on demand because most refresh cycles won't need it. Entries are never removed      * from it, rather, it is nulled and rebuilt when needed again. The (hopefully) rare entries that didn't make the current refresh cycle      * are just added back to the new list. Both the reference and the contents are always modified while synchronized on {@code this}.      *      * We never set this to non-null while closed it {@code true}.      */
DECL|field|refreshListeners
specifier|private
specifier|volatile
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|refreshListeners
init|=
literal|null
decl_stmt|;
comment|/**      * The translog location that was last made visible by a refresh.      */
DECL|field|lastRefreshedLocation
specifier|private
specifier|volatile
name|Translog
operator|.
name|Location
name|lastRefreshedLocation
decl_stmt|;
DECL|method|RefreshListeners
specifier|public
name|RefreshListeners
parameter_list|(
name|IntSupplier
name|getMaxRefreshListeners
parameter_list|,
name|Runnable
name|forceRefresh
parameter_list|,
name|Executor
name|listenerExecutor
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|getMaxRefreshListeners
operator|=
name|getMaxRefreshListeners
expr_stmt|;
name|this
operator|.
name|forceRefresh
operator|=
name|forceRefresh
expr_stmt|;
name|this
operator|.
name|listenerExecutor
operator|=
name|listenerExecutor
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
comment|/**      * Add a listener for refreshes, calling it immediately if the location is already visible. If this runs out of listener slots then it      * forces a refresh and calls the listener immediately as well.      *      * @param location the location to listen for      * @param listener for the refresh. Called with true if registering the listener ran it out of slots and forced a refresh. Called with      *        false otherwise.      * @return did we call the listener (true) or register the listener to call later (false)?      */
DECL|method|addOrNotify
specifier|public
name|boolean
name|addOrNotify
parameter_list|(
name|Translog
operator|.
name|Location
name|location
parameter_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
name|listener
parameter_list|)
block|{
name|requireNonNull
argument_list|(
name|listener
argument_list|,
literal|"listener cannot be null"
argument_list|)
expr_stmt|;
name|requireNonNull
argument_list|(
name|location
argument_list|,
literal|"location cannot be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastRefreshedLocation
operator|!=
literal|null
operator|&&
name|lastRefreshedLocation
operator|.
name|compareTo
argument_list|(
name|location
argument_list|)
operator|>=
literal|0
condition|)
block|{
comment|// Location already visible, just call the listener
name|listener
operator|.
name|accept
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|listeners
init|=
name|refreshListeners
decl_stmt|;
if|if
condition|(
name|listeners
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't wait for refresh on a closed index"
argument_list|)
throw|;
block|}
name|listeners
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|refreshListeners
operator|=
name|listeners
expr_stmt|;
block|}
if|if
condition|(
name|listeners
operator|.
name|size
argument_list|()
operator|<
name|getMaxRefreshListeners
operator|.
name|getAsInt
argument_list|()
condition|)
block|{
comment|// We have a free slot so register the listener
name|listeners
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|location
argument_list|,
name|listener
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|// No free slot so force a refresh and call the listener in this thread
name|forceRefresh
operator|.
name|run
argument_list|()
expr_stmt|;
name|listener
operator|.
name|accept
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|oldListeners
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|oldListeners
operator|=
name|refreshListeners
expr_stmt|;
name|refreshListeners
operator|=
literal|null
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
comment|// Fire any listeners we might have had
name|fireListeners
argument_list|(
name|oldListeners
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns true if there are pending listeners.      */
DECL|method|refreshNeeded
specifier|public
name|boolean
name|refreshNeeded
parameter_list|()
block|{
comment|// A null list doesn't need a refresh. If we're closed we don't need a refresh either.
return|return
name|refreshListeners
operator|!=
literal|null
operator|&&
literal|false
operator|==
name|closed
return|;
block|}
comment|/**      * The number of pending listeners.      */
DECL|method|pendingCount
specifier|public
name|int
name|pendingCount
parameter_list|()
block|{
comment|// No need to synchronize here because we're doing a single volatile read
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|listeners
init|=
name|refreshListeners
decl_stmt|;
comment|// A null list means we haven't accumulated any listeners. Otherwise we need the size.
return|return
name|listeners
operator|==
literal|null
condition|?
literal|0
else|:
name|listeners
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Setup the translog used to find the last refreshed location.      */
DECL|method|setTranslog
specifier|public
name|void
name|setTranslog
parameter_list|(
name|Translog
name|translog
parameter_list|)
block|{
name|this
operator|.
name|translog
operator|=
name|translog
expr_stmt|;
block|}
comment|// Implementation of ReferenceManager.RefreshListener that adapts Lucene's RefreshListener into Elasticsearch's refresh listeners.
DECL|field|translog
specifier|private
name|Translog
name|translog
decl_stmt|;
comment|/**      * Snapshot of the translog location before the current refresh if there is a refresh going on or null. Doesn't have to be volatile      * because when it is used by the refreshing thread.      */
DECL|field|currentRefreshLocation
specifier|private
name|Translog
operator|.
name|Location
name|currentRefreshLocation
decl_stmt|;
annotation|@
name|Override
DECL|method|beforeRefresh
specifier|public
name|void
name|beforeRefresh
parameter_list|()
throws|throws
name|IOException
block|{
name|currentRefreshLocation
operator|=
name|translog
operator|.
name|getLastWriteLocation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterRefresh
specifier|public
name|void
name|afterRefresh
parameter_list|(
name|boolean
name|didRefresh
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* We intentionally ignore didRefresh here because our timing is a little off. It'd be a useful flag if we knew everything that made          * it into the refresh, but the way we snapshot the translog position before the refresh, things can sneak into the refresh that we          * don't know about. */
if|if
condition|(
literal|null
operator|==
name|currentRefreshLocation
condition|)
block|{
comment|/* The translog had an empty last write location at the start of the refresh so we can't alert anyone to anything. This              * usually happens during recovery. The next refresh cycle out to pick up this refresh. */
return|return;
block|}
comment|/* Set the lastRefreshedLocation so listeners that come in for locations before that will just execute inline without messing          * around with refreshListeners or synchronizing at all. Note that it is not safe for us to abort early if we haven't advanced the          * position here because we set and read lastRefreshedLocation outside of a synchronized block. We do that so that waiting for a          * refresh that has already passed is just a volatile read but the cost is that any check whether or not we've advanced the          * position will introduce a race between adding the listener and the position check. We could work around this by moving this          * assignment into the synchronized block below and double checking lastRefreshedLocation in addOrNotify's synchronized block but          * that doesn't seem worth it given that we already skip this process early if there aren't any listeners to iterate. */
name|lastRefreshedLocation
operator|=
name|currentRefreshLocation
expr_stmt|;
comment|/* Grab the current refresh listeners and replace them with null while synchronized. Any listeners that come in after this won't be          * in the list we iterate over and very likely won't be candidates for refresh anyway because we've already moved the          * lastRefreshedLocation. */
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|candidates
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|candidates
operator|=
name|refreshListeners
expr_stmt|;
comment|// No listeners to check so just bail early
if|if
condition|(
name|candidates
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|refreshListeners
operator|=
literal|null
expr_stmt|;
block|}
comment|// Iterate the list of listeners, copying the listeners to fire to one list and those to preserve to another list.
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|listenersToFire
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|preservedListeners
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|tuple
range|:
name|candidates
control|)
block|{
name|Translog
operator|.
name|Location
name|location
init|=
name|tuple
operator|.
name|v1
argument_list|()
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|compareTo
argument_list|(
name|currentRefreshLocation
argument_list|)
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|listenersToFire
operator|==
literal|null
condition|)
block|{
name|listenersToFire
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|listenersToFire
operator|.
name|add
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|preservedListeners
operator|==
literal|null
condition|)
block|{
name|preservedListeners
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|preservedListeners
operator|.
name|add
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* Now deal with the listeners that it isn't time yet to fire. We need to do this under lock so we don't miss a concurrent close or          * newly registered listener. If we're not closed we just add the listeners to the list of listeners we check next time. If we are          * closed we fire the listeners even though it isn't time for them. */
if|if
condition|(
name|preservedListeners
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|refreshListeners
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
name|listenersToFire
operator|.
name|addAll
argument_list|(
name|preservedListeners
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refreshListeners
operator|=
name|preservedListeners
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|closed
operator|==
literal|false
operator|:
literal|"Can't be closed and have non-null refreshListeners"
assert|;
name|refreshListeners
operator|.
name|addAll
argument_list|(
name|preservedListeners
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Lastly, fire the listeners that are ready on the listener thread pool
name|fireListeners
argument_list|(
name|listenersToFire
argument_list|)
expr_stmt|;
block|}
comment|/**      * Fire some listeners. Does nothing if the list of listeners is null.      */
DECL|method|fireListeners
specifier|private
name|void
name|fireListeners
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|>
name|listenersToFire
parameter_list|)
block|{
if|if
condition|(
name|listenersToFire
operator|!=
literal|null
condition|)
block|{
name|listenerExecutor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
for|for
control|(
name|Tuple
argument_list|<
name|Translog
operator|.
name|Location
argument_list|,
name|Consumer
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|listener
range|:
name|listenersToFire
control|)
block|{
try|try
block|{
name|listener
operator|.
name|v2
argument_list|()
operator|.
name|accept
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Error firing refresh listener"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

