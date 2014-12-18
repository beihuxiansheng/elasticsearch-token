begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.env
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|env
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
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
name|store
operator|.
name|*
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|Nullable
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
name|component
operator|.
name|AbstractComponent
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
name|inject
operator|.
name|Inject
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
name|settings
operator|.
name|Settings
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
name|unit
operator|.
name|ByteSizeValue
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
name|Index
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
name|shard
operator|.
name|ShardId
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
name|nio
operator|.
name|file
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|Semaphore
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * A component that holds all data paths for a single node.  */
end_comment

begin_class
DECL|class|NodeEnvironment
specifier|public
class|class
name|NodeEnvironment
extends|extends
name|AbstractComponent
implements|implements
name|Closeable
block|{
comment|/* ${data.paths}/nodes/{node.id} */
DECL|field|nodePaths
specifier|private
specifier|final
name|Path
index|[]
name|nodePaths
decl_stmt|;
comment|/* ${data.paths}/nodes/{node.id}/indices */
DECL|field|nodeIndicesPaths
specifier|private
specifier|final
name|Path
index|[]
name|nodeIndicesPaths
decl_stmt|;
DECL|field|locks
specifier|private
specifier|final
name|Lock
index|[]
name|locks
decl_stmt|;
DECL|field|localNodeId
specifier|private
specifier|final
name|int
name|localNodeId
decl_stmt|;
DECL|field|closed
specifier|private
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|shardLocks
specifier|private
specifier|final
name|Map
argument_list|<
name|ShardId
argument_list|,
name|InternalShardLock
argument_list|>
name|shardLocks
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodeEnvironment
specifier|public
name|NodeEnvironment
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|environment
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DiscoveryNode
operator|.
name|nodeRequiresLocalStorage
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|nodePaths
operator|=
literal|null
expr_stmt|;
name|nodeIndicesPaths
operator|=
literal|null
expr_stmt|;
name|locks
operator|=
literal|null
expr_stmt|;
name|localNodeId
operator|=
operator|-
literal|1
expr_stmt|;
return|return;
block|}
specifier|final
name|Path
index|[]
name|nodePaths
init|=
operator|new
name|Path
index|[
name|environment
operator|.
name|dataWithClusterFiles
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|Lock
index|[]
name|locks
init|=
operator|new
name|Lock
index|[
name|environment
operator|.
name|dataWithClusterFiles
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
name|int
name|localNodeId
init|=
operator|-
literal|1
decl_stmt|;
name|IOException
name|lastException
init|=
literal|null
decl_stmt|;
name|int
name|maxLocalStorageNodes
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"node.max_local_storage_nodes"
argument_list|,
literal|50
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|possibleLockId
init|=
literal|0
init|;
name|possibleLockId
operator|<
name|maxLocalStorageNodes
condition|;
name|possibleLockId
operator|++
control|)
block|{
for|for
control|(
name|int
name|dirIndex
init|=
literal|0
init|;
name|dirIndex
operator|<
name|environment
operator|.
name|dataWithClusterFiles
argument_list|()
operator|.
name|length
condition|;
name|dirIndex
operator|++
control|)
block|{
name|Path
name|dir
init|=
name|environment
operator|.
name|dataWithClusterFiles
argument_list|()
index|[
name|dirIndex
index|]
operator|.
name|resolve
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"nodes"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|possibleLockId
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
operator|==
literal|false
condition|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|Directory
name|luceneDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dir
argument_list|,
name|NativeFSLockFactory
operator|.
name|INSTANCE
argument_list|)
init|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"obtaining node lock on {} ..."
argument_list|,
name|dir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Lock
name|tmpLock
init|=
name|luceneDir
operator|.
name|makeLock
argument_list|(
literal|"node.lock"
argument_list|)
decl_stmt|;
name|boolean
name|obtained
init|=
name|tmpLock
operator|.
name|obtain
argument_list|()
decl_stmt|;
if|if
condition|(
name|obtained
condition|)
block|{
name|locks
index|[
name|dirIndex
index|]
operator|=
name|tmpLock
expr_stmt|;
name|nodePaths
index|[
name|dirIndex
index|]
operator|=
name|dir
expr_stmt|;
name|localNodeId
operator|=
name|possibleLockId
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"failed to obtain node lock on {}"
argument_list|,
name|dir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// release all the ones that were obtained up until now
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|locks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|locks
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|locks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|locks
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"failed to obtain node lock on {}"
argument_list|,
name|e
argument_list|,
name|dir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|lastException
operator|=
operator|new
name|IOException
argument_list|(
literal|"failed to obtain lock on "
operator|+
name|dir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// release all the ones that were obtained up until now
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|locks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|locks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|locks
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|locks
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
comment|// we found a lock, break
break|break;
block|}
block|}
if|if
condition|(
name|locks
index|[
literal|0
index|]
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Failed to obtain node lock, is the following location writable?: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|environment
operator|.
name|dataWithClusterFiles
argument_list|()
argument_list|)
argument_list|,
name|lastException
argument_list|)
throw|;
block|}
name|this
operator|.
name|localNodeId
operator|=
name|localNodeId
expr_stmt|;
name|this
operator|.
name|locks
operator|=
name|locks
expr_stmt|;
name|this
operator|.
name|nodePaths
operator|=
name|nodePaths
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"using node location [{}], local_node_id [{}]"
argument_list|,
name|nodePaths
argument_list|,
name|localNodeId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"node data locations details:\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|file
range|:
name|nodePaths
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
operator|.
name|append
argument_list|(
name|file
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", free_space ["
argument_list|)
operator|.
name|append
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|Files
operator|.
name|getFileStore
argument_list|(
name|file
argument_list|)
operator|.
name|getUnallocatedSpace
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], usable_space ["
argument_list|)
operator|.
name|append
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|Files
operator|.
name|getFileStore
argument_list|(
name|file
argument_list|)
operator|.
name|getUsableSpace
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|trace
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|nodeIndicesPaths
operator|=
operator|new
name|Path
index|[
name|nodePaths
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodePaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodeIndicesPaths
index|[
name|i
index|]
operator|=
name|nodePaths
index|[
name|i
index|]
operator|.
name|resolve
argument_list|(
literal|"indices"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Deletes a shard data directory iff the shards locks were successfully acquired.      *      * @param shardId the id of the shard to delete to delete      * @throws IOException if an IOException occurs      */
DECL|method|deleteShardDirectorySafe
specifier|public
name|void
name|deleteShardDirectorySafe
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
index|[]
name|paths
init|=
name|shardPaths
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
try|try
init|(
name|Closeable
name|lock
init|=
name|shardLock
argument_list|(
name|shardId
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Deletes an indexes data directory recursively iff all of the indexes      * shards locks were successfully acquired. If any of the indexes shard directories can't be locked      * non of the shards will be deleted      *      * @param index the index to delete      * @param lockTimeoutMS how long to wait for acquiring the indices shard locks      * @throws Exception if any of the shards data directories can't be locked or deleted      */
DECL|method|deleteIndexDirectorySafe
specifier|public
name|void
name|deleteIndexDirectorySafe
parameter_list|(
name|Index
name|index
parameter_list|,
name|long
name|lockTimeoutMS
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|ShardLock
argument_list|>
name|locks
init|=
name|lockAllForIndex
argument_list|(
name|index
argument_list|,
name|lockTimeoutMS
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Path
index|[]
name|indexPaths
init|=
operator|new
name|Path
index|[
name|nodeIndicesPaths
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexPaths
index|[
name|i
index|]
operator|=
name|nodeIndicesPaths
index|[
name|i
index|]
operator|.
name|resolve
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|rm
argument_list|(
name|indexPaths
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|locks
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tries to lock all local shards for the given index. If any of the shard locks can't be acquired      * an {@link LockObtainFailedException} is thrown and all previously acquired locks are released.      *      * @param index the index to lock shards for      * @param lockTimeoutMS how long to wait for acquiring the indices shard locks      * @return the {@link ShardLock} instances for this index.      * @throws IOException if an IOException occurs.      */
DECL|method|lockAllForIndex
specifier|public
name|List
argument_list|<
name|ShardLock
argument_list|>
name|lockAllForIndex
parameter_list|(
name|Index
name|index
parameter_list|,
name|long
name|lockTimeoutMS
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|ShardId
argument_list|>
name|allShardIds
init|=
name|findAllShardIds
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardLock
argument_list|>
name|allLocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|ShardId
name|shardId
range|:
name|allShardIds
control|)
block|{
name|long
name|timeoutLeft
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|lockTimeoutMS
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
decl_stmt|;
name|allLocks
operator|.
name|add
argument_list|(
name|shardLock
argument_list|(
name|shardId
argument_list|,
name|timeoutLeft
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|allLocks
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|allLocks
return|;
block|}
comment|/**      * Tries to lock the given shards ID. A shard lock is required to perform any kind of      * write operation on a shards data directory like deleting files, creating a new index writer      * or recover from a different shard instance into it. If the shard lock can not be acquired      * an {@link LockObtainFailedException} is thrown.      *      * Note: this method will return immediately if the lock can't be acquired.      *      * @param id the shard ID to lock      * @return the shard lock. Call {@link ShardLock#close()} to release the lock      * @throws IOException if an IOException occurs.      */
DECL|method|shardLock
specifier|public
name|ShardLock
name|shardLock
parameter_list|(
name|ShardId
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|shardLock
argument_list|(
name|id
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * Tries to lock the given shards ID. A shard lock is required to perform any kind of      * write operation on a shards data directory like deleting files, creating a new index writer      * or recover from a different shard instance into it. If the shard lock can not be acquired      * an {@link org.apache.lucene.store.LockObtainFailedException} is thrown      * @param id the shard ID to lock      * @param lockTimeoutMS the lock timeout in milliseconds      * @return the shard lock. Call {@link ShardLock#close()} to release the lock      * @throws IOException if an IOException occurs.      */
DECL|method|shardLock
specifier|public
name|ShardLock
name|shardLock
parameter_list|(
specifier|final
name|ShardId
name|id
parameter_list|,
name|long
name|lockTimeoutMS
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|InternalShardLock
name|shardLock
decl_stmt|;
specifier|final
name|boolean
name|acquired
decl_stmt|;
synchronized|synchronized
init|(
name|shardLocks
init|)
block|{
if|if
condition|(
name|shardLocks
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|shardLock
operator|=
name|shardLocks
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|shardLock
operator|.
name|incWaitCount
argument_list|()
expr_stmt|;
name|acquired
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|shardLock
operator|=
operator|new
name|InternalShardLock
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|shardLocks
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|shardLock
argument_list|)
expr_stmt|;
name|acquired
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|acquired
operator|==
literal|false
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|shardLock
operator|.
name|acquire
argument_list|(
name|lockTimeoutMS
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
name|shardLock
operator|.
name|decWaitCount
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|ShardLock
argument_list|(
name|id
argument_list|)
block|{
comment|// new instance prevents double closing
annotation|@
name|Override
specifier|protected
name|void
name|closeInternal
parameter_list|()
block|{
name|shardLock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**      * Returns all currently lock shards      */
DECL|method|lockedShards
specifier|public
name|Set
argument_list|<
name|ShardId
argument_list|>
name|lockedShards
parameter_list|()
block|{
synchronized|synchronized
init|(
name|shardLocks
init|)
block|{
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ShardId
argument_list|>
name|builder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
return|return
name|builder
operator|.
name|addAll
argument_list|(
name|shardLocks
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
DECL|class|InternalShardLock
specifier|private
specifier|final
class|class
name|InternalShardLock
block|{
comment|/*          * This class holds a mutex for exclusive access and timeout / wait semantics          * and a reference count to cleanup the shard lock instance form the internal data          * structure if nobody is waiting for it. the wait count is guarded by the same lock          * that is used to mutate the map holding the shard locks to ensure exclusive access          */
DECL|field|mutex
specifier|private
specifier|final
name|Semaphore
name|mutex
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|waitCount
specifier|private
name|int
name|waitCount
init|=
literal|1
decl_stmt|;
comment|// guarded by shardLocks
DECL|field|shardId
specifier|private
name|ShardId
name|shardId
decl_stmt|;
DECL|method|InternalShardLock
name|InternalShardLock
parameter_list|(
name|ShardId
name|id
parameter_list|)
block|{
name|shardId
operator|=
name|id
expr_stmt|;
name|mutex
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
block|}
DECL|method|release
specifier|protected
name|void
name|release
parameter_list|()
block|{
name|mutex
operator|.
name|release
argument_list|()
expr_stmt|;
name|decWaitCount
argument_list|()
expr_stmt|;
block|}
DECL|method|incWaitCount
name|void
name|incWaitCount
parameter_list|()
block|{
synchronized|synchronized
init|(
name|shardLocks
init|)
block|{
assert|assert
name|waitCount
operator|>
literal|0
operator|:
literal|"waitCount is "
operator|+
name|waitCount
operator|+
literal|" but should be> 0"
assert|;
name|waitCount
operator|++
expr_stmt|;
block|}
block|}
DECL|method|decWaitCount
specifier|private
name|void
name|decWaitCount
parameter_list|()
block|{
synchronized|synchronized
init|(
name|shardLocks
init|)
block|{
assert|assert
name|waitCount
operator|>
literal|0
operator|:
literal|"waitCount is "
operator|+
name|waitCount
operator|+
literal|" but should be> 0"
assert|;
if|if
condition|(
operator|--
name|waitCount
operator|==
literal|0
condition|)
block|{
name|InternalShardLock
name|remove
init|=
name|shardLocks
operator|.
name|remove
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
assert|assert
name|remove
operator|!=
literal|null
operator|:
literal|"Removed lock was null"
assert|;
block|}
block|}
block|}
DECL|method|acquire
name|void
name|acquire
parameter_list|(
name|long
name|timeoutInMillis
parameter_list|)
throws|throws
name|LockObtainFailedException
block|{
try|try
block|{
if|if
condition|(
name|mutex
operator|.
name|tryAcquire
argument_list|(
name|timeoutInMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|LockObtainFailedException
argument_list|(
literal|"Can't lock shard "
operator|+
name|shardId
operator|+
literal|", timed out after "
operator|+
name|timeoutInMillis
operator|+
literal|"ms"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|LockObtainFailedException
argument_list|(
literal|"Can't lock shard "
operator|+
name|shardId
operator|+
literal|", interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|localNodeId
specifier|public
name|int
name|localNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|localNodeId
return|;
block|}
DECL|method|hasNodeFile
specifier|public
name|boolean
name|hasNodeFile
parameter_list|()
block|{
return|return
name|nodePaths
operator|!=
literal|null
operator|&&
name|locks
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns an array of all of the nodes data locations.      * @throws org.elasticsearch.ElasticsearchIllegalStateException if the node is not configured to store local locations      */
DECL|method|nodeDataPaths
specifier|public
name|Path
index|[]
name|nodeDataPaths
parameter_list|()
block|{
assert|assert
name|assertEnvIsLocked
argument_list|()
assert|;
if|if
condition|(
name|nodePaths
operator|==
literal|null
operator|||
name|locks
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"node is not configured to store local location"
argument_list|)
throw|;
block|}
return|return
name|nodePaths
return|;
block|}
comment|/**      * Returns all data paths for the given index.      */
DECL|method|indexPaths
specifier|public
name|Path
index|[]
name|indexPaths
parameter_list|(
name|Index
name|index
parameter_list|)
block|{
assert|assert
name|assertEnvIsLocked
argument_list|()
assert|;
name|Path
index|[]
name|indexPaths
init|=
operator|new
name|Path
index|[
name|nodeIndicesPaths
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeIndicesPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexPaths
index|[
name|i
index|]
operator|=
name|nodeIndicesPaths
index|[
name|i
index|]
operator|.
name|resolve
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|indexPaths
return|;
block|}
comment|/**      * Returns all data paths for the given shards ID      */
DECL|method|shardPaths
specifier|public
name|Path
index|[]
name|shardPaths
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
assert|assert
name|assertEnvIsLocked
argument_list|()
assert|;
specifier|final
name|Path
index|[]
name|nodePaths
init|=
name|nodeDataPaths
argument_list|()
decl_stmt|;
specifier|final
name|Path
index|[]
name|shardLocations
init|=
operator|new
name|Path
index|[
name|nodePaths
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodePaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shardLocations
index|[
name|i
index|]
operator|=
name|nodePaths
index|[
name|i
index|]
operator|.
name|resolve
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"indices"
argument_list|,
name|shardId
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|shardLocations
return|;
block|}
DECL|method|findAllIndices
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|findAllIndices
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|nodePaths
operator|==
literal|null
operator|||
name|locks
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"node is not configured to store local location"
argument_list|)
throw|;
block|}
assert|assert
name|assertEnvIsLocked
argument_list|()
assert|;
name|Set
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|indicesLocation
range|:
name|nodeIndicesPaths
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|indicesLocation
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|indicesLocation
argument_list|)
condition|)
block|{
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|indicesLocation
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|index
range|:
name|stream
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|index
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|indices
return|;
block|}
comment|/**      * Tries to find all allocated shards for the given index or for all indices iff the given index is<code>null</code>      * on the current node. NOTE: This methods is prone to race-conditions on the filesystem layer since it might not      * see directories created concurrently or while it's traversing.      * @param index the index to filter shards for or<code>null</code> if all shards for all indices should be listed      * @return a set of shard IDs      * @throws IOException if an IOException occurs      */
DECL|method|findAllShardIds
specifier|public
name|Set
argument_list|<
name|ShardId
argument_list|>
name|findAllShardIds
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Index
name|index
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nodePaths
operator|==
literal|null
operator|||
name|locks
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"node is not configured to store local location"
argument_list|)
throw|;
block|}
assert|assert
name|assertEnvIsLocked
argument_list|()
assert|;
return|return
name|findAllShardIds
argument_list|(
name|index
operator|==
literal|null
condition|?
literal|null
else|:
name|index
operator|.
name|getName
argument_list|()
argument_list|,
name|nodeIndicesPaths
argument_list|)
return|;
block|}
DECL|method|findAllShardIds
specifier|private
specifier|static
name|Set
argument_list|<
name|ShardId
argument_list|>
name|findAllShardIds
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|String
name|index
parameter_list|,
name|Path
modifier|...
name|locations
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Set
argument_list|<
name|ShardId
argument_list|>
name|shardIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|location
range|:
name|locations
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|location
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|location
argument_list|)
condition|)
block|{
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|indexStream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|location
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|indexPath
range|:
name|indexStream
control|)
block|{
if|if
condition|(
name|index
operator|==
literal|null
operator|||
name|index
operator|.
name|equals
argument_list|(
name|indexPath
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|shardIds
operator|.
name|addAll
argument_list|(
name|findAllShardsForIndex
argument_list|(
name|indexPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|shardIds
return|;
block|}
DECL|method|findAllShardsForIndex
specifier|private
specifier|static
name|Set
argument_list|<
name|ShardId
argument_list|>
name|findAllShardsForIndex
parameter_list|(
name|Path
name|indexPath
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|ShardId
argument_list|>
name|shardIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|indexPath
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|indexPath
argument_list|)
condition|)
block|{
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|indexPath
argument_list|)
init|)
block|{
name|String
name|currentIndex
init|=
name|indexPath
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|shardPath
range|:
name|stream
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|shardPath
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|shardPath
argument_list|)
condition|)
block|{
name|Integer
name|shardId
init|=
name|Ints
operator|.
name|tryParse
argument_list|(
name|shardPath
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardId
operator|!=
literal|null
condition|)
block|{
name|shardIds
operator|.
name|add
argument_list|(
operator|new
name|ShardId
argument_list|(
name|currentIndex
argument_list|,
name|shardId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|shardIds
return|;
block|}
comment|/**      * Tries to find all allocated shards for all indices iff the given index on the current node. NOTE: This methods      * is prone to race-conditions on the filesystem layer since it might not see directories created concurrently or      * while it's traversing.      *      * @return a set of shard IDs      * @throws IOException if an IOException occurs      */
DECL|method|findAllShardIds
specifier|public
name|Set
argument_list|<
name|ShardId
argument_list|>
name|findAllShardIds
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|findAllShardIds
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
operator|&&
name|locks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Lock
name|lock
range|:
name|locks
control|)
block|{
try|try
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"releasing lock [{}]"
argument_list|,
name|lock
argument_list|)
expr_stmt|;
name|lock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"failed to release lock [{}]"
argument_list|,
name|e
argument_list|,
name|lock
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|assertEnvIsLocked
specifier|private
name|boolean
name|assertEnvIsLocked
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
operator|.
name|get
argument_list|()
operator|&&
name|locks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Lock
name|lock
range|:
name|locks
control|)
block|{
try|try
block|{
assert|assert
name|lock
operator|.
name|isLocked
argument_list|()
operator|:
literal|"Lock: "
operator|+
name|lock
operator|+
literal|"is not locked"
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"lock assertion failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * This method tries to write an empty file and moves it using an atomic move operation.      * This method throws an {@link ElasticsearchIllegalStateException} if this operation is      * not supported by the filesystem. This test is executed on each of the data directories.      * This method cleans up all files even in the case of an error.      */
DECL|method|ensureAtomicMoveSupported
specifier|public
name|void
name|ensureAtomicMoveSupported
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
index|[]
name|nodePaths
init|=
name|nodeDataPaths
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|directory
range|:
name|nodePaths
control|)
block|{
assert|assert
name|Files
operator|.
name|isDirectory
argument_list|(
name|directory
argument_list|)
operator|:
name|directory
operator|+
literal|" is not a directory"
assert|;
specifier|final
name|Path
name|src
init|=
name|directory
operator|.
name|resolve
argument_list|(
literal|"__es__.tmp"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|src
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|target
init|=
name|directory
operator|.
name|resolve
argument_list|(
literal|"__es__.final"
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|src
argument_list|,
name|target
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AtomicMoveNotSupportedException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"atomic_move is not supported by the filesystem on path ["
operator|+
name|directory
operator|+
literal|"] atomic_move is required for elasticsearch to work correctly."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSettings
name|Settings
name|getSettings
parameter_list|()
block|{
comment|// for testing
return|return
name|settings
return|;
block|}
block|}
end_class

end_unit

