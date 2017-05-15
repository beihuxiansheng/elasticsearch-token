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
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

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
name|util
operator|.
name|Supplier
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
name|DirectoryReader
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
name|fielddata
operator|.
name|IndexFieldData
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|DocumentMapper
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
name|FieldMapper
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
name|MappedFieldType
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
name|IndexShard
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
name|IndexShardState
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
name|List
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
name|concurrent
operator|.
name|CountDownLatch
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
DECL|class|IndexWarmer
specifier|public
specifier|final
class|class
name|IndexWarmer
extends|extends
name|AbstractComponent
block|{
DECL|field|listeners
specifier|private
specifier|final
name|List
argument_list|<
name|Listener
argument_list|>
name|listeners
decl_stmt|;
DECL|method|IndexWarmer
name|IndexWarmer
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|Listener
modifier|...
name|listeners
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Listener
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Executor
name|executor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|WARMER
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|FieldDataWarmer
argument_list|(
name|executor
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|list
argument_list|,
name|listeners
argument_list|)
expr_stmt|;
name|this
operator|.
name|listeners
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
DECL|method|warm
name|void
name|warm
parameter_list|(
name|Engine
operator|.
name|Searcher
name|searcher
parameter_list|,
name|IndexShard
name|shard
parameter_list|,
name|IndexSettings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|shard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|CLOSED
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|settings
operator|.
name|isWarmerEnabled
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return;
block|}
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
literal|"{} top warming [{}]"
argument_list|,
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|searcher
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|shard
operator|.
name|warmerService
argument_list|()
operator|.
name|onPreWarm
argument_list|()
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|TerminationHandle
argument_list|>
name|terminationHandles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// get a handle on pending tasks
for|for
control|(
specifier|final
name|Listener
name|listener
range|:
name|listeners
control|)
block|{
name|terminationHandles
operator|.
name|add
argument_list|(
name|listener
operator|.
name|warmReader
argument_list|(
name|shard
argument_list|,
name|searcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// wait for termination
for|for
control|(
name|TerminationHandle
name|terminationHandle
range|:
name|terminationHandles
control|)
block|{
try|try
block|{
name|terminationHandle
operator|.
name|awaitTermination
argument_list|()
expr_stmt|;
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
name|logger
operator|.
name|warn
argument_list|(
literal|"top warming has been interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|long
name|took
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|time
decl_stmt|;
name|shard
operator|.
name|warmerService
argument_list|()
operator|.
name|onPostWarm
argument_list|(
name|took
argument_list|)
expr_stmt|;
if|if
condition|(
name|shard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|shard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|trace
argument_list|(
literal|"top warming took [{}]"
argument_list|,
operator|new
name|TimeValue
argument_list|(
name|took
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** A handle on the execution of  warm-up action. */
DECL|interface|TerminationHandle
specifier|public
interface|interface
name|TerminationHandle
block|{
DECL|field|NO_WAIT
name|TerminationHandle
name|NO_WAIT
init|=
parameter_list|()
lambda|->
block|{}
decl_stmt|;
comment|/** Wait until execution of the warm-up action completes. */
DECL|method|awaitTermination
name|void
name|awaitTermination
parameter_list|()
throws|throws
name|InterruptedException
function_decl|;
block|}
DECL|interface|Listener
specifier|public
interface|interface
name|Listener
block|{
comment|/** Queue tasks to warm-up the given segments and return handles that allow to wait for termination of the          *  execution of those tasks. */
DECL|method|warmReader
name|TerminationHandle
name|warmReader
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|,
name|Engine
operator|.
name|Searcher
name|searcher
parameter_list|)
function_decl|;
block|}
DECL|class|FieldDataWarmer
specifier|private
specifier|static
class|class
name|FieldDataWarmer
implements|implements
name|IndexWarmer
operator|.
name|Listener
block|{
DECL|field|executor
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
DECL|method|FieldDataWarmer
name|FieldDataWarmer
parameter_list|(
name|Executor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|warmReader
specifier|public
name|TerminationHandle
name|warmReader
parameter_list|(
specifier|final
name|IndexShard
name|indexShard
parameter_list|,
specifier|final
name|Engine
operator|.
name|Searcher
name|searcher
parameter_list|)
block|{
specifier|final
name|MapperService
name|mapperService
init|=
name|indexShard
operator|.
name|mapperService
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MappedFieldType
argument_list|>
name|warmUpGlobalOrdinals
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DocumentMapper
name|docMapper
range|:
name|mapperService
operator|.
name|docMappers
argument_list|(
literal|false
argument_list|)
control|)
block|{
for|for
control|(
name|FieldMapper
name|fieldMapper
range|:
name|docMapper
operator|.
name|mappers
argument_list|()
control|)
block|{
specifier|final
name|MappedFieldType
name|fieldType
init|=
name|fieldMapper
operator|.
name|fieldType
argument_list|()
decl_stmt|;
specifier|final
name|String
name|indexName
init|=
name|fieldType
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldType
operator|.
name|eagerGlobalOrdinals
argument_list|()
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
name|warmUpGlobalOrdinals
operator|.
name|put
argument_list|(
name|indexName
argument_list|,
name|fieldType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|IndexFieldDataService
name|indexFieldDataService
init|=
name|indexShard
operator|.
name|indexFieldDataService
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|warmUpGlobalOrdinals
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|MappedFieldType
name|fieldType
range|:
name|warmUpGlobalOrdinals
operator|.
name|values
argument_list|()
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|IndexFieldData
operator|.
name|Global
name|ifd
init|=
name|indexFieldDataService
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
name|DirectoryReader
name|reader
init|=
name|searcher
operator|.
name|getDirectoryReader
argument_list|()
decl_stmt|;
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|global
init|=
name|ifd
operator|.
name|loadGlobal
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|global
operator|.
name|load
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexShard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|indexShard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|trace
argument_list|(
literal|"warmed global ordinals for [{}], took [{}]"
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|indexShard
operator|.
name|warmerService
argument_list|()
operator|.
name|logger
argument_list|()
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"failed to warm-up global ordinals for [{}]"
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
parameter_list|()
lambda|->
name|latch
operator|.
name|await
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

