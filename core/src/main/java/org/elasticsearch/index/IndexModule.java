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
name|cache
operator|.
name|query
operator|.
name|index
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
name|none
operator|.
name|NoneQueryCache
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
name|cache
operator|.
name|query
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
name|mapper
operator|.
name|MapperRegistry
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

begin_comment
comment|/**  * IndexModule represents the central extension point for index level custom implementations like:  *<ul>  *<li>{@link SimilarityProvider} - New {@link SimilarityProvider} implementations can be registered through {@link #addSimilarity(String, BiFunction)}  *         while existing Providers can be referenced through Settings under the {@link IndexModule#SIMILARITY_SETTINGS_PREFIX} prefix  *         along with the "type" value.  For example, to reference the {@link BM25SimilarityProvider}, the configuration  *<tt>"index.similarity.my_similarity.type : "BM25"</tt> can be used.</li>  *<li>{@link IndexStore} - Custom {@link IndexStore} instances can be registered via {@link #addIndexStore(String, BiFunction)}</li>  *<li>{@link IndexEventListener} - Custom {@link IndexEventListener} instances can be registered via {@link #addIndexEventListener(IndexEventListener)}</li>  *<li>Settings update listener - Custom settings update listener can be registered via {@link #addSettingsUpdateConsumer(Setting, Consumer)}</li>  *</ul>  */
end_comment

begin_class
DECL|class|IndexModule
specifier|public
specifier|final
class|class
name|IndexModule
block|{
DECL|field|STORE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|STORE_TYPE
init|=
literal|"index.store.type"
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
DECL|field|INDEX_QUERY_CACHE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_QUERY_CACHE
init|=
literal|"index"
decl_stmt|;
DECL|field|NONE_QUERY_CACHE
specifier|public
specifier|static
specifier|final
name|String
name|NONE_QUERY_CACHE
init|=
literal|"none"
decl_stmt|;
DECL|field|QUERY_CACHE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_CACHE_TYPE
init|=
literal|"index.queries.cache.type"
decl_stmt|;
comment|// for test purposes only
DECL|field|QUERY_CACHE_EVERYTHING
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_CACHE_EVERYTHING
init|=
literal|"index.queries.cache.everything"
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
DECL|field|listener
specifier|private
name|IndexEventListener
name|listener
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
DECL|field|queryCaches
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
name|IndicesQueryCache
argument_list|,
name|QueryCache
argument_list|>
argument_list|>
name|queryCaches
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|registerQueryCache
argument_list|(
name|INDEX_QUERY_CACHE
argument_list|,
name|IndexQueryCache
operator|::
operator|new
argument_list|)
expr_stmt|;
name|registerQueryCache
argument_list|(
name|NONE_QUERY_CACHE
argument_list|,
parameter_list|(
name|a
parameter_list|,
name|b
parameter_list|)
lambda|->
operator|new
name|NoneQueryCache
argument_list|(
name|a
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
if|if
condition|(
name|this
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't add listener after listeners are frozen"
argument_list|)
throw|;
block|}
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
comment|/**      * Adds an {@link IndexStore} type to this index module. Typically stores are registered with a refrence to      * it's constructor:      *<pre>      *     indexModule.addIndexStore("my_store_type", MyStore::new);      *</pre>      *      * @param type the type to register      * @param provider the instance provider / factory method      */
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
literal|"] already registerd"
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
comment|/**      * Registers a {@link QueryCache} provider for a given name      * @param name the providers / caches name      * @param provider the provider instance      */
DECL|method|registerQueryCache
specifier|public
name|void
name|registerQueryCache
parameter_list|(
name|String
name|name
parameter_list|,
name|BiFunction
argument_list|<
name|IndexSettings
argument_list|,
name|IndicesQueryCache
argument_list|,
name|QueryCache
argument_list|>
name|provider
parameter_list|)
block|{
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"provider must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|queryCaches
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
literal|"Can't register the same [query_cache] more than once for ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|queryCaches
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets a {@link org.elasticsearch.index.IndexModule.IndexSearcherWrapperFactory} that is called once the IndexService is fully constructed.      * Note: this method can only be called once per index. Multiple wrappers are not supported.      */
DECL|method|setSearcherWrapper
specifier|public
name|void
name|setSearcherWrapper
parameter_list|(
name|IndexSearcherWrapperFactory
name|indexSearcherWrapperFactory
parameter_list|)
block|{
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
specifier|public
name|IndexEventListener
name|freeze
parameter_list|()
block|{
comment|// TODO somehow we need to make this pkg private...
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
name|listener
operator|=
operator|new
name|CompositeIndexEventListener
argument_list|(
name|indexSettings
argument_list|,
name|indexEventListeners
argument_list|)
expr_stmt|;
block|}
return|return
name|listener
return|;
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
DECL|enum constant|DEFAULT
name|DEFAULT
block|;
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
name|NodeServicesProvider
name|servicesProvider
parameter_list|,
name|MapperRegistry
name|mapperRegistry
parameter_list|,
name|IndexingOperationListener
modifier|...
name|listeners
parameter_list|)
throws|throws
name|IOException
block|{
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
name|IndexEventListener
name|eventListener
init|=
name|freeze
argument_list|()
decl_stmt|;
specifier|final
name|String
name|storeType
init|=
name|indexSettings
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|STORE_TYPE
argument_list|)
decl_stmt|;
specifier|final
name|IndexStore
name|store
decl_stmt|;
if|if
condition|(
name|storeType
operator|==
literal|null
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
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC_SETTING
argument_list|,
name|store
operator|::
name|setMaxRate
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
name|INDEX_STORE_THROTTLE_TYPE_SETTING
argument_list|,
name|store
operator|::
name|setType
argument_list|)
expr_stmt|;
specifier|final
name|String
name|queryCacheType
init|=
name|indexSettings
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|IndexModule
operator|.
name|QUERY_CACHE_TYPE
argument_list|,
name|IndexModule
operator|.
name|INDEX_QUERY_CACHE
argument_list|)
decl_stmt|;
specifier|final
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
name|queryCaches
operator|.
name|get
argument_list|(
name|queryCacheType
argument_list|)
decl_stmt|;
specifier|final
name|QueryCache
name|queryCache
init|=
name|queryCacheProvider
operator|.
name|apply
argument_list|(
name|indexSettings
argument_list|,
name|servicesProvider
operator|.
name|getIndicesQueryCache
argument_list|()
argument_list|)
decl_stmt|;
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
name|servicesProvider
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
name|listeners
argument_list|)
return|;
block|}
block|}
end_class

end_unit

