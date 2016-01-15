begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|reindex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|PlainActionFuture
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
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_class
DECL|class|AbstractAsyncBulkIndexByScrollActionTestCase
specifier|public
specifier|abstract
class|class
name|AbstractAsyncBulkIndexByScrollActionTestCase
parameter_list|<
name|Request
extends|extends
name|AbstractBulkIndexByScrollRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|Response
extends|extends
name|BulkIndexByScrollResponse
parameter_list|>
extends|extends
name|ESTestCase
block|{
DECL|field|threadPool
specifier|protected
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|task
specifier|protected
name|BulkByScrollTask
name|task
decl_stmt|;
annotation|@
name|Before
DECL|method|setupForTest
specifier|public
name|void
name|setupForTest
parameter_list|()
block|{
name|threadPool
operator|=
operator|new
name|ThreadPool
argument_list|(
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|task
operator|=
operator|new
name|BulkByScrollTask
argument_list|(
literal|1
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|,
parameter_list|()
lambda|->
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|action
specifier|protected
specifier|abstract
name|AbstractAsyncBulkIndexByScrollAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
parameter_list|()
function_decl|;
DECL|method|request
specifier|protected
specifier|abstract
name|Request
name|request
parameter_list|()
function_decl|;
DECL|method|listener
specifier|protected
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|()
block|{
return|return
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
return|;
block|}
block|}
end_class

end_unit

