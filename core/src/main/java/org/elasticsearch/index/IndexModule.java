begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|util
operator|.
name|SetOnce
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|service
operator|.
name|ClusterService
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
name|Strings
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
name|Setting
operator|.
name|Property
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
name|util
operator|.
name|BigArrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|NodeEnvironment
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
name|analysis
operator|.
name|AnalysisRegistry
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
name|cache
operator|.
name|query
operator|.
name|DisabledQueryCache
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
name|cache
operator|.
name|query
operator|.
name|IndexQueryCache
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
name|cache
operator|.
name|query
operator|.
name|QueryCache
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
name|engine
operator|.
name|EngineFactory
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
name|mapper
operator|.
name|MapperService
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
name|IndexEventListener
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
name|IndexSearcherWrapper
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
name|IndexingOperationListener
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
name|SearchOperationListener
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
name|similarity
operator|.
name|BM25SimilarityProvider
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
name|similarity
operator|.
name|SimilarityProvider
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
name|similarity
operator|.
name|SimilarityService
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
name|IndexStore
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
name|IndexStoreConfig
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
name|IndicesQueryCache
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
name|breaker
operator|.
name|CircuitBreakerService
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
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCache
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
name|mapper
operator|.
name|MapperRegistry
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
name|query
operator|.
name|IndicesQueriesRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
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
name|Function
import|;
end_import

begin_comment
comment|/**  * IndexModule represents the central extension point for index level custom implementations like:  *<ul>  *<li>{@link SimilarityProvider} - New {@link SimilarityProvider} implementations can be registered through  *     {@link #addSimilarity(String, BiFunction)}while existing Providers can be referenced through Settings under the  *     {@link IndexModule#SIMILARITY_SETTINGS_PREFIX} prefix along with the "type" value.  For example, to reference the  *     {@link BM25SimilarityProvider}, the configuration<tt>"index.similarity.my_similarity.type : "BM25"</tt> can be used.</li>  *<li>{@link IndexStore} - Custom {@link IndexStore} instances can be registered via {@link #addIndexStore(String, BiFunction)}</li>  *<li>{@link IndexEventListener} - Custom {@link IndexEventListener} instances can be registered via  *      {@link #addIndexEventListener(IndexEventListener)}</li>  *<li>Settings update listener - Custom settings update listener can be registered via  *      {@link #addSettingsUpdateConsumer(Setting, Consumer)}</li>  *</ul>  */
end_comment

begin_class
DECL|class|IndexModule
specifier|public
specifier|final
class|class
name|IndexModule
block|{
DECL|field|INDEX_STORE_TYPE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|INDEX_STORE_TYPE_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"index.store.type"
argument_list|,
literal|""
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
comment|/** On which extensions to load data into the file-system cache upon opening of files.      *  This only works with the mmap directory, and even in that case is still      *  best-effort only. */
DECL|field|INDEX_STORE_PRE_LOAD_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|INDEX_STORE_PRE_LOAD_SETTING
init|=
name|Setting
operator|.
name|listSetting
argument_list|(
literal|"index.store.preload"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|SIMILARITY_SETTINGS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SIMILARITY_SETTINGS_PREFIX
init|=
literal|"index.similarity"
decl_stmt|;
comment|// whether to use the query cache
DECL|field|INDEX_QUERY_CACHE_ENABLED_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|INDEX_QUERY_CACHE_ENABLED_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.queries.cache.enabled"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
comment|// for test purposes only
DECL|field|INDEX_QUERY_CACHE_EVERYTHING_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|INDEX_QUERY_CACHE_EVERYTHING_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.queries.cache.everything"
argument_list|,
literal|false
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|indexSettings
specifier|private
specifier|final
name|IndexSettings
name|indexSettings
decl_stmt|;
DECL|field|indexStoreConfig
specifier|private
specifier|final
name|IndexStoreConfig
name|indexStoreConfig
decl_stmt|;
DECL|field|analysisRegistry
specifier|private
specifier|final
name|AnalysisRegistry
name|analysisRegistry
decl_stmt|;
comment|// pkg private so tests can mock
DECL|field|engineFactory
specifier|final
name|SetOnce
argument_list|<
name|EngineFactory
argument_list|>
name|engineFactory
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|indexSearcherWrapper
specifier|private
name|SetOnce
argument_list|<
name|IndexSearcherWrapperFactory
argument_list|>
name|indexSearcherWrapper
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|indexEventListeners
specifier|private
specifier|final
name|Set
argument_list|<
name|IndexEventListener
argument_list|>
name|indexEventListeners
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|similarities
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BiFunction
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|,
name|SimilarityProvider
argument_list|>
argument_list|>
name|similarities
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|storeTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BiFunction
argument_list|<
name|IndexSettings
argument_list|,
name|IndexStoreConfig
argument_list|,
name|IndexStore
argument_list|>
argument_list|>
name|storeTypes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|forceQueryCacheProvider
specifier|private
specifier|final
name|SetOnce
argument_list|<
name|BiFunction
argument_list|<
name|IndexSettings
argument_list|,
name|IndicesQueryCache
argument_list|,
name|QueryCache
argument_list|>
argument_list|>
name|forceQueryCacheProvider
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|searchOperationListeners
specifier|private
specifier|final
name|List
argument_list|<
name|SearchOperationListener
argument_list|>
name|searchOperationListeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|indexOperationListeners
specifier|private
specifier|final
name|List
argument_list|<
name|IndexingOperationListener
argument_list|>
name|indexOperationListeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|frozen
specifier|private
specifier|final
name|AtomicBoolean
name|frozen
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|IndexModule
specifier|public
name|IndexModule
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|IndexStoreConfig
name|indexStoreConfig
parameter_list|,
name|AnalysisRegistry
name|analysisRegistry
parameter_list|)
block|{
name|this
operator|.
name|indexStoreConfig
operator|=
name|indexStoreConfig
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|analysisRegistry
operator|=
name|analysisRegistry
expr_stmt|;
name|this
operator|.
name|searchOperationListeners
operator|.
name|add
argument_list|(
operator|new
name|SearchSlowLog
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexOperationListeners
operator|.
name|add
argument_list|(
operator|new
name|IndexingSlowLog
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a Setting and it's consumer for this index.      */
DECL|method|addSettingsUpdateConsumer
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|addSettingsUpdateConsumer
parameter_list|(
name|Setting
argument_list|<
name|T
argument_list|>
name|setting
parameter_list|,
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|setting
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"setting must not be null"
argument_list|)
throw|;
block|}
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|setting
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a Setting, it's consumer and validator for this index.      */
DECL|method|addSettingsUpdateConsumer
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|addSettingsUpdateConsumer
parameter_list|(
name|Setting
argument_list|<
name|T
argument_list|>
name|setting
parameter_list|,
name|Consumer
argument_list|<
name|T
argument_list|>
name|consumer
parameter_list|,
name|Consumer
argument_list|<
name|T
argument_list|>
name|validator
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|setting
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"setting must not be null"
argument_list|)
throw|;
block|}
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|setting
argument_list|,
name|consumer
argument_list|,
name|validator
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the index {@link Settings} for this index      */
DECL|method|getSettings
specifier|public
name|Settings
name|getSettings
parameter_list|()
block|{
return|return
name|indexSettings
operator|.
name|getSettings
argument_list|()
return|;
block|}
comment|/**      * Returns the index this module is associated with      */
DECL|method|getIndex
specifier|public
name|Index
name|getIndex
parameter_list|()
block|{
return|return
name|indexSettings
operator|.
name|getIndex
argument_list|()
return|;
block|}
comment|/**      * Adds an {@link IndexEventListener} for this index. All listeners added here      * are maintained for the entire index lifecycle on this node. Once an index is closed or deleted these      * listeners go out of scope.      *<p>      * Note: an index might be created on a node multiple times. For instance if the last shard from an index is      * relocated to another node the internal representation will be destroyed which includes the registered listeners.      * Once the node holds at least one shard of an index all modules are reloaded and listeners are registered again.      * Listeners can't be unregistered they will stay alive for the entire time the index is allocated on a node.      *</p>      */
DECL|method|addIndexEventListener
specifier|public
name|void
name|addIndexEventListener
parameter_list|(
name|IndexEventListener
name|listener
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|indexEventListeners
operator|.
name|contains
argument_list|(
name|listener
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener already added"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexEventListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds an {@link SearchOperationListener} for this index. All listeners added here      * are maintained for the entire index lifecycle on this node. Once an index is closed or deleted these      * listeners go out of scope.      *<p>      * Note: an index might be created on a node multiple times. For instance if the last shard from an index is      * relocated to another node the internal representation will be destroyed which includes the registered listeners.      * Once the node holds at least one shard of an index all modules are reloaded and listeners are registered again.      * Listeners can't be unregistered they will stay alive for the entire time the index is allocated on a node.      *</p>      */
DECL|method|addSearchOperationListener
specifier|public
name|void
name|addSearchOperationListener
parameter_list|(
name|SearchOperationListener
name|listener
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|searchOperationListeners
operator|.
name|contains
argument_list|(
name|listener
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener already added"
argument_list|)
throw|;
block|}
name|this
operator|.
name|searchOperationListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds an {@link IndexingOperationListener} for this index. All listeners added here      * are maintained for the entire index lifecycle on this node. Once an index is closed or deleted these      * listeners go out of scope.      *<p>      * Note: an index might be created on a node multiple times. For instance if the last shard from an index is      * relocated to another node the internal representation will be destroyed which includes the registered listeners.      * Once the node holds at least one shard of an index all modules are reloaded and listeners are registered again.      * Listeners can't be unregistered they will stay alive for the entire time the index is allocated on a node.      *</p>      */
DECL|method|addIndexOperationListener
specifier|public
name|void
name|addIndexOperationListener
parameter_list|(
name|IndexingOperationListener
name|listener
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|indexOperationListeners
operator|.
name|contains
argument_list|(
name|listener
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener already added"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexOperationListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds an {@link IndexStore} type to this index module. Typically stores are registered with a reference to      * it's constructor:      *<pre>      *     indexModule.addIndexStore("my_store_type", MyStore::new);      *</pre>      *      * @param type the type to register      * @param provider the instance provider / factory method      */
DECL|method|addIndexStore
specifier|public
name|void
name|addIndexStore
parameter_list|(
name|String
name|type
parameter_list|,
name|BiFunction
argument_list|<
name|IndexSettings
argument_list|,
name|IndexStoreConfig
argument_list|,
name|IndexStore
argument_list|>
name|provider
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|storeTypes
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key ["
operator|+
name|type
operator|+
literal|"] already registered"
argument_list|)
throw|;
block|}
name|storeTypes
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
comment|/**      * Registers the given {@link SimilarityProvider} with the given name      *      * @param name Name of the SimilarityProvider      * @param similarity SimilarityProvider to register      */
DECL|method|addSimilarity
specifier|public
name|void
name|addSimilarity
parameter_list|(
name|String
name|name
parameter_list|,
name|BiFunction
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|,
name|SimilarityProvider
argument_list|>
name|similarity
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|similarities
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|||
name|SimilarityService
operator|.
name|BUILT_IN
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"similarity for name: ["
operator|+
name|name
operator|+
literal|" is already registered"
argument_list|)
throw|;
block|}
name|similarities
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|similarity
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets a {@link org.elasticsearch.index.IndexModule.IndexSearcherWrapperFactory} that is called once the IndexService      * is fully constructed.      * Note: this method can only be called once per index. Multiple wrappers are not supported.      */
DECL|method|setSearcherWrapper
specifier|public
name|void
name|setSearcherWrapper
parameter_list|(
name|IndexSearcherWrapperFactory
name|indexSearcherWrapperFactory
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexSearcherWrapper
operator|.
name|set
argument_list|(
name|indexSearcherWrapperFactory
argument_list|)
expr_stmt|;
block|}
DECL|method|freeze
name|IndexEventListener
name|freeze
parameter_list|()
block|{
comment|// pkg private for testing
if|if
condition|(
name|this
operator|.
name|frozen
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
operator|new
name|CompositeIndexEventListener
argument_list|(
name|indexSettings
argument_list|,
name|indexEventListeners
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already frozen"
argument_list|)
throw|;
block|}
block|}
DECL|method|isBuiltinType
specifier|private
specifier|static
name|boolean
name|isBuiltinType
parameter_list|(
name|String
name|storeType
parameter_list|)
block|{
for|for
control|(
name|Type
name|type
range|:
name|Type
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|match
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|enum constant|NIOFS
name|NIOFS
block|,
DECL|enum constant|MMAPFS
name|MMAPFS
block|,
DECL|enum constant|SIMPLEFS
name|SIMPLEFS
block|,
DECL|enum constant|FS
name|FS
block|,
DECL|enum constant|Deprecated
annotation|@
name|Deprecated
DECL|enum constant|DEFAULT
name|DEFAULT
decl_stmt|;
DECL|method|getSettingsKey
specifier|public
name|String
name|getSettingsKey
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
comment|/**          * Returns true iff this settings matches the type.          */
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|String
name|setting
parameter_list|)
block|{
return|return
name|getSettingsKey
argument_list|()
operator|.
name|equals
argument_list|(
name|setting
argument_list|)
return|;
block|}
block|}
comment|/**      * Factory for creating new {@link IndexSearcherWrapper} instances      */
DECL|interface|IndexSearcherWrapperFactory
specifier|public
interface|interface
name|IndexSearcherWrapperFactory
block|{
comment|/**          * Returns a new IndexSearcherWrapper. This method is called once per index per node          */
DECL|method|newWrapper
name|IndexSearcherWrapper
name|newWrapper
parameter_list|(
specifier|final
name|IndexService
name|indexService
parameter_list|)
function_decl|;
block|}
DECL|method|newIndexService
specifier|public
name|IndexService
name|newIndexService
parameter_list|(
name|NodeEnvironment
name|environment
parameter_list|,
name|IndexService
operator|.
name|ShardStoreDeleter
name|shardStoreDeleter
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|Client
name|client
parameter_list|,
name|IndicesQueryCache
name|indicesQueryCache
parameter_list|,
name|MapperRegistry
name|mapperRegistry
parameter_list|,
name|IndicesFieldDataCache
name|indicesFieldDataCache
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexEventListener
name|eventListener
init|=
name|freeze
argument_list|()
decl_stmt|;
name|IndexSearcherWrapperFactory
name|searcherWrapperFactory
init|=
name|indexSearcherWrapper
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|?
parameter_list|(
name|shard
parameter_list|)
lambda|->
literal|null
else|:
name|indexSearcherWrapper
operator|.
name|get
argument_list|()
decl_stmt|;
name|eventListener
operator|.
name|beforeIndexCreated
argument_list|(
name|indexSettings
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexSettings
operator|.
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|storeType
init|=
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_STORE_TYPE_SETTING
argument_list|)
decl_stmt|;
specifier|final
name|IndexStore
name|store
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|storeType
argument_list|)
operator|||
name|isBuiltinType
argument_list|(
name|storeType
argument_list|)
condition|)
block|{
name|store
operator|=
operator|new
name|IndexStore
argument_list|(
name|indexSettings
argument_list|,
name|indexStoreConfig
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BiFunction
argument_list|<
name|IndexSettings
argument_list|,
name|IndexStoreConfig
argument_list|,
name|IndexStore
argument_list|>
name|factory
init|=
name|storeTypes
operator|.
name|get
argument_list|(
name|storeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown store type ["
operator|+
name|storeType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|store
operator|=
name|factory
operator|.
name|apply
argument_list|(
name|indexSettings
argument_list|,
name|indexStoreConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"store must not be null"
argument_list|)
throw|;
block|}
block|}
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|IndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE_SETTING
argument_list|,
name|store
operator|::
name|setType
argument_list|)
expr_stmt|;
name|indexSettings
operator|.
name|getScopedSettings
argument_list|()
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|IndexStore
operator|.
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC_SETTING
argument_list|,
name|store
operator|::
name|setMaxRate
argument_list|)
expr_stmt|;
specifier|final
name|QueryCache
name|queryCache
decl_stmt|;
if|if
condition|(
name|indexSettings
operator|.
name|getValue
argument_list|(
name|INDEX_QUERY_CACHE_ENABLED_SETTING
argument_list|)
condition|)
block|{
name|BiFunction
argument_list|<
name|IndexSettings
argument_list|,
name|IndicesQueryCache
argument_list|,
name|QueryCache
argument_list|>
name|queryCacheProvider
init|=
name|forceQueryCacheProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryCacheProvider
operator|==
literal|null
condition|)
block|{
name|queryCache
operator|=
operator|new
name|IndexQueryCache
argument_list|(
name|indexSettings
argument_list|,
name|indicesQueryCache
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryCache
operator|=
name|queryCacheProvider
operator|.
name|apply
argument_list|(
name|indexSettings
argument_list|,
name|indicesQueryCache
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|queryCache
operator|=
operator|new
name|DisabledQueryCache
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|IndexService
argument_list|(
name|indexSettings
argument_list|,
name|environment
argument_list|,
operator|new
name|SimilarityService
argument_list|(
name|indexSettings
argument_list|,
name|similarities
argument_list|)
argument_list|,
name|shardStoreDeleter
argument_list|,
name|analysisRegistry
argument_list|,
name|engineFactory
operator|.
name|get
argument_list|()
argument_list|,
name|circuitBreakerService
argument_list|,
name|bigArrays
argument_list|,
name|threadPool
argument_list|,
name|scriptService
argument_list|,
name|indicesQueriesRegistry
argument_list|,
name|clusterService
argument_list|,
name|client
argument_list|,
name|queryCache
argument_list|,
name|store
argument_list|,
name|eventListener
argument_list|,
name|searcherWrapperFactory
argument_list|,
name|mapperRegistry
argument_list|,
name|indicesFieldDataCache
argument_list|,
name|searchOperationListeners
argument_list|,
name|indexOperationListeners
argument_list|)
return|;
block|}
comment|/**      * creates a new mapper service to do administrative work like mapping updates. This *should not* be used for document parsing.      * doing so will result in an exception.      */
DECL|method|newIndexMapperService
specifier|public
name|MapperService
name|newIndexMapperService
parameter_list|(
name|MapperRegistry
name|mapperRegistry
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MapperService
argument_list|(
name|indexSettings
argument_list|,
name|analysisRegistry
operator|.
name|build
argument_list|(
name|indexSettings
argument_list|)
argument_list|,
operator|new
name|SimilarityService
argument_list|(
name|indexSettings
argument_list|,
name|similarities
argument_list|)
argument_list|,
name|mapperRegistry
argument_list|,
parameter_list|()
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"no index query shard context available"
argument_list|)
throw|;
block|}
argument_list|)
return|;
block|}
comment|/**      * Forces a certain query cache to use instead of the default one. If this is set      * and query caching is not disabled with {@code index.queries.cache.enabled}, then      * the given provider will be used.      * NOTE: this can only be set once      *      * @see #INDEX_QUERY_CACHE_ENABLED_SETTING      */
DECL|method|forceQueryCacheProvider
specifier|public
name|void
name|forceQueryCacheProvider
parameter_list|(
name|BiFunction
argument_list|<
name|IndexSettings
argument_list|,
name|IndicesQueryCache
argument_list|,
name|QueryCache
argument_list|>
name|queryCacheProvider
parameter_list|)
block|{
name|ensureNotFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|forceQueryCacheProvider
operator|.
name|set
argument_list|(
name|queryCacheProvider
argument_list|)
expr_stmt|;
block|}
DECL|method|ensureNotFrozen
specifier|private
name|void
name|ensureNotFrozen
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|frozen
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't modify IndexModule once the index service has been created"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

