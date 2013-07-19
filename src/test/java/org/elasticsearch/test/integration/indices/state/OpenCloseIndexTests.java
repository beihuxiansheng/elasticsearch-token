begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.indices.state
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|indices
operator|.
name|state
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
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|state
operator|.
name|ClusterStateResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|close
operator|.
name|CloseIndexResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|open
operator|.
name|OpenIndexResponse
import|;
end_import

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
name|IgnoreIndices
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
name|metadata
operator|.
name|IndexMetaData
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
name|IndexMissingException
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
name|integration
operator|.
name|AbstractSharedClusterTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
import|;
end_import

begin_class
DECL|class|OpenCloseIndexTests
specifier|public
class|class
name|OpenCloseIndexTests
extends|extends
name|AbstractSharedClusterTest
block|{
annotation|@
name|Test
DECL|method|testSimpleCloseOpen
specifier|public
name|void
name|testSimpleCloseOpen
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexMissingException
operator|.
name|class
argument_list|)
DECL|method|testSimpleCloseMissingIndex
specifier|public
name|void
name|testSimpleCloseMissingIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexMissingException
operator|.
name|class
argument_list|)
DECL|method|testSimpleOpenMissingIndex
specifier|public
name|void
name|testSimpleOpenMissingIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexMissingException
operator|.
name|class
argument_list|)
DECL|method|testCloseOneMissingIndex
specifier|public
name|void
name|testCloseOneMissingIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseOneMissingIndexIgnoreMissing
specifier|public
name|void
name|testCloseOneMissingIndexIgnoreMissing
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
operator|.
name|setIgnoreIndices
argument_list|(
name|IgnoreIndices
operator|.
name|MISSING
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IndexMissingException
operator|.
name|class
argument_list|)
DECL|method|testOpenOneMissingIndex
specifier|public
name|void
name|testOpenOneMissingIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenOneMissingIndexIgnoreMissing
specifier|public
name|void
name|testOpenOneMissingIndexIgnoreMissing
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
operator|.
name|setIgnoreIndices
argument_list|(
name|IgnoreIndices
operator|.
name|MISSING
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseOpenMultipleIndices
specifier|public
name|void
name|testCloseOpenMultipleIndices
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse1
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse1
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse2
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse2
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test3"
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse1
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse1
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse2
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse2
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseOpenWildcard
specifier|public
name|void
name|testCloseOpenWildcard
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test*"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test*"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseOpenAll
specifier|public
name|void
name|testCloseOpenAll
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseOpenAllWildcard
specifier|public
name|void
name|testCloseOpenAllWildcard
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"*"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"*"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ActionRequestValidationException
operator|.
name|class
argument_list|)
DECL|method|testCloseNoIndex
specifier|public
name|void
name|testCloseNoIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ActionRequestValidationException
operator|.
name|class
argument_list|)
DECL|method|testCloseNullIndex
specifier|public
name|void
name|testCloseNullIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|null
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ActionRequestValidationException
operator|.
name|class
argument_list|)
DECL|method|testOpenNoIndex
specifier|public
name|void
name|testOpenNoIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ActionRequestValidationException
operator|.
name|class
argument_list|)
DECL|method|testOpenNullIndex
specifier|public
name|void
name|testOpenNullIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|null
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenAlreadyOpenedIndex
specifier|public
name|void
name|testOpenAlreadyOpenedIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|//no problem if we try to open an index that's already in open state
name|OpenIndexResponse
name|openIndexResponse1
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse1
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseAlreadyClosedIndex
specifier|public
name|void
name|testCloseAlreadyClosedIndex
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|//closing the index
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
comment|//no problem if we try to close an index that's already in close state
name|OpenIndexResponse
name|openIndexResponse1
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse1
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleCloseOpenAlias
specifier|public
name|void
name|testSimpleCloseOpenAlias
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|IndicesAliasesResponse
name|aliasesResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareAliases
argument_list|()
operator|.
name|addAlias
argument_list|(
literal|"test1"
argument_list|,
literal|"test1-alias"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|aliasesResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test1-alias"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test1-alias"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseOpenAliasMultipleIndices
specifier|public
name|void
name|testCloseOpenAliasMultipleIndices
parameter_list|()
block|{
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|healthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|IndicesAliasesResponse
name|aliasesResponse1
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareAliases
argument_list|()
operator|.
name|addAlias
argument_list|(
literal|"test1"
argument_list|,
literal|"test-alias"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|aliasesResponse1
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|IndicesAliasesResponse
name|aliasesResponse2
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareAliases
argument_list|()
operator|.
name|addAlias
argument_list|(
literal|"test2"
argument_list|,
literal|"test-alias"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|aliasesResponse2
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|CloseIndexResponse
name|closeIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test-alias"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|closeIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsClosed
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|OpenIndexResponse
name|openIndexResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test-alias"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|openIndexResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertIndexIsOpened
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertIndexIsOpened
specifier|private
name|void
name|assertIndexIsOpened
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|checkIndexState
argument_list|(
name|IndexMetaData
operator|.
name|State
operator|.
name|OPEN
argument_list|,
name|indices
argument_list|)
expr_stmt|;
block|}
DECL|method|assertIndexIsClosed
specifier|private
name|void
name|assertIndexIsClosed
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|checkIndexState
argument_list|(
name|IndexMetaData
operator|.
name|State
operator|.
name|CLOSE
argument_list|,
name|indices
argument_list|)
expr_stmt|;
block|}
DECL|method|checkIndexState
specifier|private
name|void
name|checkIndexState
parameter_list|(
name|IndexMetaData
operator|.
name|State
name|state
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|ClusterStateResponse
name|clusterStateResponse
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|clusterStateResponse
operator|.
name|getState
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|getState
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

