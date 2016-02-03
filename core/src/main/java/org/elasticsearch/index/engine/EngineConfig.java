begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|codecs
operator|.
name|Codec
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
name|index
operator|.
name|MergePolicy
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
name|index
operator|.
name|SnapshotDeletionPolicy
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
name|QueryCache
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
name|QueryCachingPolicy
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
name|similarities
operator|.
name|Similarity
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
name|Setting
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
name|ByteSizeUnit
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|IndexSettings
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
name|codec
operator|.
name|CodecService
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|TranslogRecoveryPerformer
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
name|store
operator|.
name|Store
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
name|TranslogConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndexingMemoryController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/*  * Holds all the configuration that is used to create an {@link Engine}.  * Once {@link Engine} has been created with this object, changes to this  * object will affect the {@link Engine} instance.  */
end_comment

begin_class
DECL|class|EngineConfig
specifier|public
specifier|final
class|class
name|EngineConfig
block|{
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|translogRecoveryPerformer
specifier|private
specifier|final
name|TranslogRecoveryPerformer
name|translogRecoveryPerformer
decl_stmt|;
DECL|field|indexSettings
specifier|private
specifier|final
name|IndexSettings
name|indexSettings
decl_stmt|;
DECL|field|indexingBufferSize
specifier|private
specifier|final
name|ByteSizeValue
name|indexingBufferSize
decl_stmt|;
DECL|field|enableGcDeletes
specifier|private
specifier|volatile
name|boolean
name|enableGcDeletes
init|=
literal|true
decl_stmt|;
DECL|field|flushMergesAfter
specifier|private
specifier|final
name|TimeValue
name|flushMergesAfter
decl_stmt|;
DECL|field|codecName
specifier|private
specifier|final
name|String
name|codecName
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|warmer
specifier|private
specifier|final
name|Engine
operator|.
name|Warmer
name|warmer
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|deletionPolicy
specifier|private
specifier|final
name|SnapshotDeletionPolicy
name|deletionPolicy
decl_stmt|;
DECL|field|mergePolicy
specifier|private
specifier|final
name|MergePolicy
name|mergePolicy
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|codecService
specifier|private
specifier|final
name|CodecService
name|codecService
decl_stmt|;
DECL|field|eventListener
specifier|private
specifier|final
name|Engine
operator|.
name|EventListener
name|eventListener
decl_stmt|;
DECL|field|forceNewTranslog
specifier|private
specifier|final
name|boolean
name|forceNewTranslog
decl_stmt|;
DECL|field|queryCache
specifier|private
specifier|final
name|QueryCache
name|queryCache
decl_stmt|;
DECL|field|queryCachingPolicy
specifier|private
specifier|final
name|QueryCachingPolicy
name|queryCachingPolicy
decl_stmt|;
comment|/**      * Index setting to change the low level lucene codec used for writing new segments.      * This setting is<b>not</b> realtime updateable.      */
DECL|field|INDEX_CODEC_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|INDEX_CODEC_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"index.codec"
argument_list|,
literal|"default"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{
switch|switch
condition|(
name|s
condition|)
block|{
case|case
literal|"default"
case|:
case|case
literal|"best_compression"
case|:
case|case
literal|"lucene_default"
case|:
return|return
name|s
return|;
default|default:
if|if
condition|(
name|Codec
operator|.
name|availableCodecs
argument_list|()
operator|.
name|contains
argument_list|(
name|s
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// we don't error message the not officially supported ones
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown value for [index.codec] must be one of [default, best_compression] but was: "
operator|+
name|s
argument_list|)
throw|;
block|}
return|return
name|s
return|;
block|}
block|}
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|INDEX
argument_list|)
decl_stmt|;
comment|/** if set to true the engine will start even if the translog id in the commit point can not be found */
DECL|field|INDEX_FORCE_NEW_TRANSLOG
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_FORCE_NEW_TRANSLOG
init|=
literal|"index.engine.force_new_translog"
decl_stmt|;
DECL|field|translogConfig
specifier|private
name|TranslogConfig
name|translogConfig
decl_stmt|;
DECL|field|create
specifier|private
name|boolean
name|create
init|=
literal|false
decl_stmt|;
comment|/**      * Creates a new {@link org.elasticsearch.index.engine.EngineConfig}      */
DECL|method|EngineConfig
specifier|public
name|EngineConfig
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|IndexSettings
name|indexSettings
parameter_list|,
name|Engine
operator|.
name|Warmer
name|warmer
parameter_list|,
name|Store
name|store
parameter_list|,
name|SnapshotDeletionPolicy
name|deletionPolicy
parameter_list|,
name|MergePolicy
name|mergePolicy
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|CodecService
name|codecService
parameter_list|,
name|Engine
operator|.
name|EventListener
name|eventListener
parameter_list|,
name|TranslogRecoveryPerformer
name|translogRecoveryPerformer
parameter_list|,
name|QueryCache
name|queryCache
parameter_list|,
name|QueryCachingPolicy
name|queryCachingPolicy
parameter_list|,
name|TranslogConfig
name|translogConfig
parameter_list|,
name|TimeValue
name|flushMergesAfter
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
specifier|final
name|Settings
name|settings
init|=
name|indexSettings
operator|.
name|getSettings
argument_list|()
decl_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|warmer
operator|=
name|warmer
operator|==
literal|null
condition|?
parameter_list|(
name|a
parameter_list|,
name|b
parameter_list|)
lambda|->
block|{}
else|:
name|warmer
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|deletionPolicy
operator|=
name|deletionPolicy
expr_stmt|;
name|this
operator|.
name|mergePolicy
operator|=
name|mergePolicy
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
name|this
operator|.
name|codecService
operator|=
name|codecService
expr_stmt|;
name|this
operator|.
name|eventListener
operator|=
name|eventListener
expr_stmt|;
name|codecName
operator|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_CODEC_SETTING
argument_list|)
expr_stmt|;
comment|// We give IndexWriter a "huge" (256 MB) buffer, so it won't flush on its own unless the ES indexing buffer is also huge and/or
comment|// there are not too many shards allocated to this node.  Instead, IndexingMemoryController periodically checks
comment|// and refreshes the most heap-consuming shards when total indexing heap usage across all shards is too high:
name|indexingBufferSize
operator|=
operator|new
name|ByteSizeValue
argument_list|(
literal|256
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
expr_stmt|;
name|this
operator|.
name|translogRecoveryPerformer
operator|=
name|translogRecoveryPerformer
expr_stmt|;
name|this
operator|.
name|forceNewTranslog
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|INDEX_FORCE_NEW_TRANSLOG
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryCache
operator|=
name|queryCache
expr_stmt|;
name|this
operator|.
name|queryCachingPolicy
operator|=
name|queryCachingPolicy
expr_stmt|;
name|this
operator|.
name|translogConfig
operator|=
name|translogConfig
expr_stmt|;
name|this
operator|.
name|flushMergesAfter
operator|=
name|flushMergesAfter
expr_stmt|;
block|}
comment|/** if true the engine will start even if the translog id in the commit point can not be found */
DECL|method|forceNewTranslog
specifier|public
name|boolean
name|forceNewTranslog
parameter_list|()
block|{
return|return
name|forceNewTranslog
return|;
block|}
comment|/**      * Enables / disables gc deletes      *      * @see #isEnableGcDeletes()      */
DECL|method|setEnableGcDeletes
specifier|public
name|void
name|setEnableGcDeletes
parameter_list|(
name|boolean
name|enableGcDeletes
parameter_list|)
block|{
name|this
operator|.
name|enableGcDeletes
operator|=
name|enableGcDeletes
expr_stmt|;
block|}
comment|/**      * Returns the initial index buffer size. This setting is only read on startup and otherwise controlled by {@link IndexingMemoryController}      */
DECL|method|getIndexingBufferSize
specifier|public
name|ByteSizeValue
name|getIndexingBufferSize
parameter_list|()
block|{
return|return
name|indexingBufferSize
return|;
block|}
comment|/**      * Returns<code>true</code> iff delete garbage collection in the engine should be enabled. This setting is updateable      * in realtime and forces a volatile read. Consumers can safely read this value directly go fetch it's latest value. The default is<code>true</code>      *<p>      *     Engine GC deletion if enabled collects deleted documents from in-memory realtime data structures after a certain amount of      *     time ({@link IndexSettings#getGcDeletesInMillis()} if enabled. Before deletes are GCed they will cause re-adding the document that was deleted      *     to fail.      *</p>      */
DECL|method|isEnableGcDeletes
specifier|public
name|boolean
name|isEnableGcDeletes
parameter_list|()
block|{
return|return
name|enableGcDeletes
return|;
block|}
comment|/**      * Returns the {@link Codec} used in the engines {@link org.apache.lucene.index.IndexWriter}      *<p>      *     Note: this settings is only read on startup.      *</p>      */
DECL|method|getCodec
specifier|public
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codecService
operator|.
name|codec
argument_list|(
name|codecName
argument_list|)
return|;
block|}
comment|/**      * Returns a thread-pool mainly used to get estimated time stamps from {@link org.elasticsearch.threadpool.ThreadPool#estimatedTimeInMillis()} and to schedule      * async force merge calls on the {@link org.elasticsearch.threadpool.ThreadPool.Names#FORCE_MERGE} thread-pool      */
DECL|method|getThreadPool
specifier|public
name|ThreadPool
name|getThreadPool
parameter_list|()
block|{
return|return
name|threadPool
return|;
block|}
comment|/**      * Returns an {@link org.elasticsearch.index.engine.Engine.Warmer} used to warm new searchers before they are used for searching.      */
DECL|method|getWarmer
specifier|public
name|Engine
operator|.
name|Warmer
name|getWarmer
parameter_list|()
block|{
return|return
name|warmer
return|;
block|}
comment|/**      * Returns the {@link org.elasticsearch.index.store.Store} instance that provides access to the {@link org.apache.lucene.store.Directory}      * used for the engines {@link org.apache.lucene.index.IndexWriter} to write it's index files to.      *<p>      * Note: In order to use this instance the consumer needs to increment the stores reference before it's used the first time and hold      * it's reference until it's not needed anymore.      *</p>      */
DECL|method|getStore
specifier|public
name|Store
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
comment|/**      * Returns a {@link SnapshotDeletionPolicy} used in the engines      * {@link org.apache.lucene.index.IndexWriter}.      */
DECL|method|getDeletionPolicy
specifier|public
name|SnapshotDeletionPolicy
name|getDeletionPolicy
parameter_list|()
block|{
return|return
name|deletionPolicy
return|;
block|}
comment|/**      * Returns the {@link org.apache.lucene.index.MergePolicy} for the engines {@link org.apache.lucene.index.IndexWriter}      */
DECL|method|getMergePolicy
specifier|public
name|MergePolicy
name|getMergePolicy
parameter_list|()
block|{
return|return
name|mergePolicy
return|;
block|}
comment|/**      * Returns a listener that should be called on engine failure      */
DECL|method|getEventListener
specifier|public
name|Engine
operator|.
name|EventListener
name|getEventListener
parameter_list|()
block|{
return|return
name|eventListener
return|;
block|}
comment|/**      * Returns the index settings for this index.      */
DECL|method|getIndexSettings
specifier|public
name|IndexSettings
name|getIndexSettings
parameter_list|()
block|{
return|return
name|indexSettings
return|;
block|}
comment|/**      * Returns the engines shard ID      */
DECL|method|getShardId
specifier|public
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
comment|/**      * Returns the analyzer as the default analyzer in the engines {@link org.apache.lucene.index.IndexWriter}      */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/**      * Returns the {@link org.apache.lucene.search.similarities.Similarity} used for indexing and searching.      */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|similarity
return|;
block|}
comment|/**      * Returns the {@link org.elasticsearch.index.shard.TranslogRecoveryPerformer} for this engine. This class is used      * to apply transaction log operations to the engine. It encapsulates all the logic to transfer the translog entry into      * an indexing operation.      */
DECL|method|getTranslogRecoveryPerformer
specifier|public
name|TranslogRecoveryPerformer
name|getTranslogRecoveryPerformer
parameter_list|()
block|{
return|return
name|translogRecoveryPerformer
return|;
block|}
comment|/**      * Return the cache to use for queries.      */
DECL|method|getQueryCache
specifier|public
name|QueryCache
name|getQueryCache
parameter_list|()
block|{
return|return
name|queryCache
return|;
block|}
comment|/**      * Return the policy to use when caching queries.      */
DECL|method|getQueryCachingPolicy
specifier|public
name|QueryCachingPolicy
name|getQueryCachingPolicy
parameter_list|()
block|{
return|return
name|queryCachingPolicy
return|;
block|}
comment|/**      * Returns the translog config for this engine      */
DECL|method|getTranslogConfig
specifier|public
name|TranslogConfig
name|getTranslogConfig
parameter_list|()
block|{
return|return
name|translogConfig
return|;
block|}
comment|/**      * Iff set to<code>true</code> the engine will create a new lucene index when opening the engine.      * Otherwise the lucene index writer is opened in append mode. The default is<code>false</code>      */
DECL|method|setCreate
specifier|public
name|void
name|setCreate
parameter_list|(
name|boolean
name|create
parameter_list|)
block|{
name|this
operator|.
name|create
operator|=
name|create
expr_stmt|;
block|}
comment|/**      * Iff<code>true</code> the engine should create a new lucene index when opening the engine.      * Otherwise the lucene index writer should be opened in append mode. The default is<code>false</code>      */
DECL|method|isCreate
specifier|public
name|boolean
name|isCreate
parameter_list|()
block|{
return|return
name|create
return|;
block|}
comment|/**      * Returns a {@link TimeValue} at what time interval after the last write modification to the engine finished merges      * should be automatically flushed. This is used to free up transient disk usage of potentially large segments that      * are written after the engine became inactive from an indexing perspective.      */
DECL|method|getFlushMergesAfter
specifier|public
name|TimeValue
name|getFlushMergesAfter
parameter_list|()
block|{
return|return
name|flushMergesAfter
return|;
block|}
block|}
end_class

end_unit

