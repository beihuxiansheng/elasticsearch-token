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
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|test
operator|.
name|ESTestCase
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
name|Arrays
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|IndexingOperationListenerTests
specifier|public
class|class
name|IndexingOperationListenerTests
extends|extends
name|ESTestCase
block|{
comment|// this test also tests if calls are correct if one or more listeners throw exceptions
DECL|method|testListenersAreExecuted
specifier|public
name|void
name|testListenersAreExecuted
parameter_list|()
block|{
name|AtomicInteger
name|preIndex
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|AtomicInteger
name|postIndex
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|AtomicInteger
name|postIndexException
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|AtomicInteger
name|preDelete
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|AtomicInteger
name|postDelete
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|AtomicInteger
name|postDeleteException
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|IndexingOperationListener
name|listener
init|=
operator|new
name|IndexingOperationListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Engine
operator|.
name|Index
name|preIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|operation
parameter_list|)
block|{
name|preIndex
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|operation
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postIndex
parameter_list|(
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
name|postIndex
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
name|postIndexException
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Engine
operator|.
name|Delete
name|preDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
name|preDelete
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|delete
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postDelete
parameter_list|(
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
name|postDelete
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
name|postDeleteException
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|IndexingOperationListener
name|throwingListener
init|=
operator|new
name|IndexingOperationListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Engine
operator|.
name|Index
name|preIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|operation
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postIndex
parameter_list|(
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
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postIndex
parameter_list|(
name|Engine
operator|.
name|Index
name|index
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Engine
operator|.
name|Delete
name|preDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postDelete
parameter_list|(
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
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postDelete
parameter_list|(
name|Engine
operator|.
name|Delete
name|delete
parameter_list|,
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|IndexingOperationListener
argument_list|>
name|indexingOperationListeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|listener
argument_list|,
name|listener
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|indexingOperationListeners
operator|.
name|add
argument_list|(
name|throwingListener
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|indexingOperationListeners
operator|.
name|add
argument_list|(
name|throwingListener
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|indexingOperationListeners
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|IndexingOperationListener
operator|.
name|CompositeListener
name|compositeListener
init|=
operator|new
name|IndexingOperationListener
operator|.
name|CompositeListener
argument_list|(
name|indexingOperationListeners
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Delete
name|delete
init|=
operator|new
name|Engine
operator|.
name|Delete
argument_list|(
literal|"test"
argument_list|,
literal|"1"
argument_list|,
operator|new
name|Term
argument_list|(
literal|"_uid"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Index
name|index
init|=
operator|new
name|Engine
operator|.
name|Index
argument_list|(
operator|new
name|Term
argument_list|(
literal|"_uid"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|compositeListener
operator|.
name|postDelete
argument_list|(
name|delete
argument_list|,
operator|new
name|Engine
operator|.
name|DeleteResult
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|preIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postIndexException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|preDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postDeleteException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|compositeListener
operator|.
name|postDelete
argument_list|(
name|delete
argument_list|,
operator|new
name|RuntimeException
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|preIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postIndexException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|preDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDeleteException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|compositeListener
operator|.
name|preDelete
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|preIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postIndexException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|preDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDeleteException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|compositeListener
operator|.
name|postIndex
argument_list|(
name|index
argument_list|,
operator|new
name|Engine
operator|.
name|IndexResult
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|preIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|postIndexException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|preDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDeleteException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|compositeListener
operator|.
name|postIndex
argument_list|(
name|index
argument_list|,
operator|new
name|RuntimeException
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|preIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postIndexException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|preDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDeleteException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|compositeListener
operator|.
name|preIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|preIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postIndex
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postIndexException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|preDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDelete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|postDeleteException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

