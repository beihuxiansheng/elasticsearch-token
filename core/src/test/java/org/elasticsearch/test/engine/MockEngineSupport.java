begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
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
name|index
operator|.
name|DirectoryReader
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
name|FilterDirectoryReader
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
name|IndexReader
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
name|AssertingIndexSearcher
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
name|IndexSearcher
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
name|SearcherManager
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|index
operator|.
name|engine
operator|.
name|Engine
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
name|EngineConfig
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
name|EngineException
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
name|test
operator|.
name|ESIntegTestCase
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
comment|/**  * Support class to build MockEngines like {@link org.elasticsearch.test.engine.MockInternalEngine} or {@link org.elasticsearch.test.engine.MockShadowEngine}  * since they need to subclass the actual engine  */
end_comment

begin_class
DECL|class|MockEngineSupport
specifier|public
specifier|final
class|class
name|MockEngineSupport
block|{
DECL|field|WRAP_READER_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|WRAP_READER_RATIO
init|=
literal|"index.engine.mock.random.wrap_reader_ratio"
decl_stmt|;
DECL|field|READER_WRAPPER_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|READER_WRAPPER_TYPE
init|=
literal|"index.engine.mock.random.wrapper"
decl_stmt|;
DECL|field|FLUSH_ON_CLOSE_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|FLUSH_ON_CLOSE_RATIO
init|=
literal|"index.engine.mock.flush_on_close.ratio"
decl_stmt|;
DECL|field|closing
specifier|private
specifier|final
name|AtomicBoolean
name|closing
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Engine
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|filterCache
specifier|private
specifier|final
name|QueryCache
name|filterCache
decl_stmt|;
DECL|field|filterCachingPolicy
specifier|private
specifier|final
name|QueryCachingPolicy
name|filterCachingPolicy
decl_stmt|;
DECL|field|searcherCloseable
specifier|private
specifier|final
name|SearcherCloseable
name|searcherCloseable
decl_stmt|;
DECL|field|mockContext
specifier|private
specifier|final
name|MockContext
name|mockContext
decl_stmt|;
DECL|class|MockContext
specifier|public
specifier|static
class|class
name|MockContext
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|wrapReader
specifier|private
specifier|final
name|boolean
name|wrapReader
decl_stmt|;
DECL|field|wrapper
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|FilterDirectoryReader
argument_list|>
name|wrapper
decl_stmt|;
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|flushOnClose
specifier|private
specifier|final
name|double
name|flushOnClose
decl_stmt|;
DECL|method|MockContext
specifier|public
name|MockContext
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|wrapReader
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FilterDirectoryReader
argument_list|>
name|wrapper
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|wrapReader
operator|=
name|wrapReader
expr_stmt|;
name|this
operator|.
name|wrapper
operator|=
name|wrapper
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|flushOnClose
operator|=
name|indexSettings
operator|.
name|getAsDouble
argument_list|(
name|FLUSH_ON_CLOSE_RATIO
argument_list|,
literal|0.5d
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|MockEngineSupport
specifier|public
name|MockEngineSupport
parameter_list|(
name|EngineConfig
name|config
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FilterDirectoryReader
argument_list|>
name|wrapper
parameter_list|)
block|{
name|Settings
name|indexSettings
init|=
name|config
operator|.
name|getIndexSettings
argument_list|()
decl_stmt|;
name|shardId
operator|=
name|config
operator|.
name|getShardId
argument_list|()
expr_stmt|;
name|filterCache
operator|=
name|config
operator|.
name|getQueryCache
argument_list|()
expr_stmt|;
name|filterCachingPolicy
operator|=
name|config
operator|.
name|getQueryCachingPolicy
argument_list|()
expr_stmt|;
specifier|final
name|long
name|seed
init|=
name|indexSettings
operator|.
name|getAsLong
argument_list|(
name|ESIntegTestCase
operator|.
name|SETTING_INDEX_SEED
argument_list|,
literal|0l
argument_list|)
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
specifier|final
name|double
name|ratio
init|=
name|indexSettings
operator|.
name|getAsDouble
argument_list|(
name|WRAP_READER_RATIO
argument_list|,
literal|0.0d
argument_list|)
decl_stmt|;
comment|// DISABLED by default - AssertingDR is crazy slow
name|boolean
name|wrapReader
init|=
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
name|ratio
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Using [{}] for shard [{}] seed: [{}] wrapReader: [{}]"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|shardId
argument_list|,
name|seed
argument_list|,
name|wrapReader
argument_list|)
expr_stmt|;
block|}
name|mockContext
operator|=
operator|new
name|MockContext
argument_list|(
name|random
argument_list|,
name|wrapReader
argument_list|,
name|wrapper
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|searcherCloseable
operator|=
operator|new
name|SearcherCloseable
argument_list|()
expr_stmt|;
name|LuceneTestCase
operator|.
name|closeAfterSuite
argument_list|(
name|searcherCloseable
argument_list|)
expr_stmt|;
comment|// only one suite closeable per Engine
block|}
DECL|enum|CloseAction
enum|enum
name|CloseAction
block|{
DECL|enum constant|FLUSH_AND_CLOSE
name|FLUSH_AND_CLOSE
block|,
DECL|enum constant|CLOSE
name|CLOSE
block|;     }
comment|/**      * Returns the CloseAction to execute on the actual engine. Note this method changes the state on      * the first call and treats subsequent calls as if the engine passed is already closed.      */
DECL|method|flushOrClose
specifier|public
name|CloseAction
name|flushOrClose
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|CloseAction
name|originalAction
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|closing
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// only do the random thing if we are the first call to this since super.flushOnClose() calls #close() again and then we might end up with a stackoverflow.
if|if
condition|(
name|mockContext
operator|.
name|flushOnClose
operator|>
name|mockContext
operator|.
name|random
operator|.
name|nextDouble
argument_list|()
condition|)
block|{
return|return
name|CloseAction
operator|.
name|FLUSH_AND_CLOSE
return|;
block|}
else|else
block|{
return|return
name|CloseAction
operator|.
name|CLOSE
return|;
block|}
block|}
else|else
block|{
return|return
name|originalAction
return|;
block|}
block|}
DECL|method|newSearcher
specifier|public
name|AssertingIndexSearcher
name|newSearcher
parameter_list|(
name|String
name|source
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|SearcherManager
name|manager
parameter_list|)
throws|throws
name|EngineException
block|{
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|IndexReader
name|wrappedReader
init|=
name|reader
decl_stmt|;
assert|assert
name|reader
operator|!=
literal|null
assert|;
if|if
condition|(
name|reader
operator|instanceof
name|DirectoryReader
operator|&&
name|mockContext
operator|.
name|wrapReader
condition|)
block|{
name|wrappedReader
operator|=
name|wrapReader
argument_list|(
operator|(
name|DirectoryReader
operator|)
name|reader
argument_list|)
expr_stmt|;
block|}
comment|// this executes basic query checks and asserts that weights are normalized only once etc.
specifier|final
name|AssertingIndexSearcher
name|assertingIndexSearcher
init|=
operator|new
name|AssertingIndexSearcher
argument_list|(
name|mockContext
operator|.
name|random
argument_list|,
name|wrappedReader
argument_list|)
decl_stmt|;
name|assertingIndexSearcher
operator|.
name|setSimilarity
argument_list|(
name|searcher
operator|.
name|getSimilarity
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertingIndexSearcher
operator|.
name|setQueryCache
argument_list|(
name|filterCache
argument_list|)
expr_stmt|;
name|assertingIndexSearcher
operator|.
name|setQueryCachingPolicy
argument_list|(
name|filterCachingPolicy
argument_list|)
expr_stmt|;
return|return
name|assertingIndexSearcher
return|;
block|}
DECL|method|wrapReader
specifier|private
name|DirectoryReader
name|wrapReader
parameter_list|(
name|DirectoryReader
name|reader
parameter_list|)
block|{
try|try
block|{
name|Constructor
argument_list|<
name|?
argument_list|>
index|[]
name|constructors
init|=
name|mockContext
operator|.
name|wrapper
operator|.
name|getConstructors
argument_list|()
decl_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
name|nonRandom
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Constructor
argument_list|<
name|?
argument_list|>
name|constructor
range|:
name|constructors
control|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
init|=
name|constructor
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|>
literal|0
operator|&&
name|parameterTypes
index|[
literal|0
index|]
operator|==
name|DirectoryReader
operator|.
name|class
condition|)
block|{
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|nonRandom
operator|=
name|constructor
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|==
literal|2
operator|&&
name|parameterTypes
index|[
literal|1
index|]
operator|==
name|Settings
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|DirectoryReader
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
name|reader
argument_list|,
name|mockContext
operator|.
name|indexSettings
argument_list|)
return|;
block|}
block|}
block|}
if|if
condition|(
name|nonRandom
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|DirectoryReader
operator|)
name|nonRandom
operator|.
name|newInstance
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Can not wrap reader"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|reader
return|;
block|}
DECL|class|DirectoryReaderWrapper
specifier|public
specifier|static
specifier|abstract
class|class
name|DirectoryReaderWrapper
extends|extends
name|FilterDirectoryReader
block|{
DECL|field|subReaderWrapper
specifier|protected
specifier|final
name|SubReaderWrapper
name|subReaderWrapper
decl_stmt|;
DECL|method|DirectoryReaderWrapper
specifier|public
name|DirectoryReaderWrapper
parameter_list|(
name|DirectoryReader
name|in
parameter_list|,
name|SubReaderWrapper
name|subReaderWrapper
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|subReaderWrapper
argument_list|)
expr_stmt|;
name|this
operator|.
name|subReaderWrapper
operator|=
name|subReaderWrapper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCombinedCoreAndDeletesKey
argument_list|()
return|;
block|}
block|}
DECL|method|wrapSearcher
specifier|public
name|Engine
operator|.
name|Searcher
name|wrapSearcher
parameter_list|(
name|String
name|source
parameter_list|,
name|Engine
operator|.
name|Searcher
name|engineSearcher
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|SearcherManager
name|manager
parameter_list|)
block|{
specifier|final
name|AssertingIndexSearcher
name|assertingIndexSearcher
init|=
name|newSearcher
argument_list|(
name|source
argument_list|,
name|searcher
argument_list|,
name|manager
argument_list|)
decl_stmt|;
name|assertingIndexSearcher
operator|.
name|setSimilarity
argument_list|(
name|searcher
operator|.
name|getSimilarity
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// pass the original searcher to the super.newSearcher() method to make sure this is the searcher that will
comment|// be released later on. If we wrap an index reader here must not pass the wrapped version to the manager
comment|// on release otherwise the reader will be closed too early. - good news, stuff will fail all over the place if we don't get this right here
name|AssertingSearcher
name|assertingSearcher
init|=
operator|new
name|AssertingSearcher
argument_list|(
name|assertingIndexSearcher
argument_list|,
name|engineSearcher
argument_list|,
name|shardId
argument_list|,
name|logger
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|searcherCloseable
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|searcherCloseable
operator|.
name|add
argument_list|(
name|assertingSearcher
argument_list|,
name|engineSearcher
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|assertingSearcher
return|;
block|}
DECL|class|SearcherCloseable
specifier|private
specifier|static
specifier|final
class|class
name|SearcherCloseable
implements|implements
name|Closeable
block|{
DECL|field|openSearchers
specifier|private
specifier|final
name|IdentityHashMap
argument_list|<
name|AssertingSearcher
argument_list|,
name|RuntimeException
argument_list|>
name|openSearchers
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|openSearchers
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|AssertionError
name|error
init|=
operator|new
name|AssertionError
argument_list|(
literal|"Unreleased searchers found"
argument_list|)
decl_stmt|;
for|for
control|(
name|RuntimeException
name|ex
range|:
name|openSearchers
operator|.
name|values
argument_list|()
control|)
block|{
name|error
operator|.
name|addSuppressed
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|error
throw|;
block|}
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|AssertingSearcher
name|searcher
parameter_list|,
name|String
name|source
parameter_list|)
block|{
specifier|final
name|RuntimeException
name|ex
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"Unreleased Searcher, source ["
operator|+
name|source
operator|+
literal|"]"
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|openSearchers
operator|.
name|put
argument_list|(
name|searcher
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|remove
specifier|synchronized
name|void
name|remove
parameter_list|(
name|AssertingSearcher
name|searcher
parameter_list|)
block|{
name|openSearchers
operator|.
name|remove
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
