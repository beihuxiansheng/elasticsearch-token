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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An indexing listener for indexing, delete, events.  */
end_comment

begin_interface
DECL|interface|IndexingOperationListener
specifier|public
interface|interface
name|IndexingOperationListener
block|{
comment|/**      * Called before the indexing occurs.      */
DECL|method|preIndex
specifier|default
name|Engine
operator|.
name|Index
name|preIndex
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Index
name|operation
parameter_list|)
block|{
return|return
name|operation
return|;
block|}
comment|/**      * Called after the indexing operation occurred. Note that this is      * also called when indexing a document did not succeed due to document      * related failures. See {@link #postIndex(ShardId, Engine.Index, Exception)}      * for engine level failures      */
DECL|method|postIndex
specifier|default
name|void
name|postIndex
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Engine
operator|.
name|IndexResult
name|result
parameter_list|)
block|{}
comment|/**      * Called after the indexing operation occurred with engine level exception.      * See {@link #postIndex(ShardId, Engine.Index, Engine.IndexResult)} for document      * related failures      */
DECL|method|postIndex
specifier|default
name|void
name|postIndex
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{}
comment|/**      * Called before the delete occurs.      */
DECL|method|preDelete
specifier|default
name|Engine
operator|.
name|Delete
name|preDelete
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
return|return
name|delete
return|;
block|}
comment|/**      * Called after the delete operation occurred. Note that this is      * also called when deleting a document did not succeed due to document      * related failures. See {@link #postDelete(ShardId, Engine.Delete, Exception)}      * for engine level failures      */
DECL|method|postDelete
specifier|default
name|void
name|postDelete
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Engine
operator|.
name|DeleteResult
name|result
parameter_list|)
block|{}
comment|/**      * Called after the delete operation occurred with engine level exception.      * See {@link #postDelete(ShardId, Engine.Delete, Engine.DeleteResult)} for document      * related failures      */
DECL|method|postDelete
specifier|default
name|void
name|postDelete
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{}
comment|/**      * A Composite listener that multiplexes calls to each of the listeners methods.      */
DECL|class|CompositeListener
specifier|final
class|class
name|CompositeListener
implements|implements
name|IndexingOperationListener
block|{
DECL|field|listeners
specifier|private
specifier|final
name|List
argument_list|<
name|IndexingOperationListener
argument_list|>
name|listeners
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|method|CompositeListener
specifier|public
name|CompositeListener
parameter_list|(
name|List
argument_list|<
name|IndexingOperationListener
argument_list|>
name|listeners
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|listeners
operator|=
name|listeners
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preIndex
specifier|public
name|Engine
operator|.
name|Index
name|preIndex
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Index
name|operation
parameter_list|)
block|{
assert|assert
name|operation
operator|!=
literal|null
assert|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|preIndex
argument_list|(
name|shardId
argument_list|,
name|operation
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
literal|"preIndex listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|operation
return|;
block|}
annotation|@
name|Override
DECL|method|postIndex
specifier|public
name|void
name|postIndex
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Engine
operator|.
name|IndexResult
name|result
parameter_list|)
block|{
assert|assert
name|index
operator|!=
literal|null
assert|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postIndex
argument_list|(
name|shardId
argument_list|,
name|index
argument_list|,
name|result
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
literal|"postIndex listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|postIndex
specifier|public
name|void
name|postIndex
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
assert|assert
name|index
operator|!=
literal|null
operator|&&
name|ex
operator|!=
literal|null
assert|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postIndex
argument_list|(
name|shardId
argument_list|,
name|index
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|inner
operator|.
name|addSuppressed
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|logger
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
literal|"postIndex listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|inner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|preDelete
specifier|public
name|Engine
operator|.
name|Delete
name|preDelete
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
assert|assert
name|delete
operator|!=
literal|null
assert|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|preDelete
argument_list|(
name|shardId
argument_list|,
name|delete
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
literal|"preDelete listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|delete
return|;
block|}
annotation|@
name|Override
DECL|method|postDelete
specifier|public
name|void
name|postDelete
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Engine
operator|.
name|DeleteResult
name|result
parameter_list|)
block|{
assert|assert
name|delete
operator|!=
literal|null
assert|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postDelete
argument_list|(
name|shardId
argument_list|,
name|delete
argument_list|,
name|result
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
literal|"postDelete listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|postDelete
specifier|public
name|void
name|postDelete
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
assert|assert
name|delete
operator|!=
literal|null
operator|&&
name|ex
operator|!=
literal|null
assert|;
for|for
control|(
name|IndexingOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|postDelete
argument_list|(
name|shardId
argument_list|,
name|delete
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|inner
operator|.
name|addSuppressed
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|logger
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
literal|"postDelete listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|inner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_interface

end_unit

