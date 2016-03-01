begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHitCount
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * Tests that you can actually cancel a reindex request and all the plumbing works. Doesn't test all of the different cancellation places -  * that is the responsibility of {@link AsyncBulkByScrollActionTests} which have more precise control to simulate failures but do not  * exercise important portion of the stack like transport and task management.  */
end_comment

begin_class
DECL|class|ReindexCancelTests
specifier|public
class|class
name|ReindexCancelTests
extends|extends
name|ReindexTestCase
block|{
DECL|method|testCancel
specifier|public
name|void
name|testCancel
parameter_list|()
throws|throws
name|Exception
block|{
name|ReindexResponse
name|response
init|=
name|CancelTestUtils
operator|.
name|testCancel
argument_list|(
name|this
argument_list|,
name|reindex
argument_list|()
operator|.
name|destination
argument_list|(
literal|"dest"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
name|ReindexAction
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
argument_list|,
name|responseMatcher
argument_list|()
operator|.
name|created
argument_list|(
literal|1
argument_list|)
operator|.
name|reasonCancelled
argument_list|(
name|equalTo
argument_list|(
literal|"by user request"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|refresh
argument_list|(
literal|"dest"
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"dest"
argument_list|)
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numberOfShards
specifier|protected
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
return|return
name|CancelTestUtils
operator|.
name|nodePlugins
argument_list|()
return|;
block|}
block|}
end_class

end_unit

