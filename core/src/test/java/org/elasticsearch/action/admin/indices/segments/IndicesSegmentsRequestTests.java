begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.segments
package|package
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
name|segments
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
name|IndicesOptions
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
name|Segment
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
name|MergePolicyConfig
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
name|IndexClosedException
import|;
end_import

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
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESSingleNodeTestCase
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
name|InternalSettingsPluging
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
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|is
import|;
end_import

begin_class
DECL|class|IndicesSegmentsRequestTests
specifier|public
class|class
name|IndicesSegmentsRequestTests
extends|extends
name|ESSingleNodeTestCase
block|{
annotation|@
name|Override
DECL|method|getPlugins
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
name|getPlugins
parameter_list|()
block|{
return|return
name|pluginList
argument_list|(
name|InternalSettingsPluging
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Before
DECL|method|setupIndex
specifier|public
name|void
name|setupIndex
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
comment|// don't allow any merges so that the num docs is the expected segments
operator|.
name|put
argument_list|(
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_ENABLED
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocs
condition|;
operator|++
name|j
control|)
block|{
name|String
name|id
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|id
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"text"
argument_list|,
literal|"sometext"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setWaitIfOngoing
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
block|{
name|IndicesSegmentResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareSegments
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Segment
argument_list|>
name|segments
init|=
name|rsp
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getShards
argument_list|()
index|[
literal|0
index|]
operator|.
name|getSegments
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|segments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|segments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|ramTree
argument_list|)
expr_stmt|;
block|}
DECL|method|testVerbose
specifier|public
name|void
name|testVerbose
parameter_list|()
block|{
name|IndicesSegmentResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareSegments
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Segment
argument_list|>
name|segments
init|=
name|rsp
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getShards
argument_list|()
index|[
literal|0
index|]
operator|.
name|getSegments
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|segments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|segments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|ramTree
argument_list|)
expr_stmt|;
block|}
comment|/**      * with the default IndicesOptions inherited from BroadcastOperationRequest this will raise an exception      */
DECL|method|testRequestOnClosedIndex
specifier|public
name|void
name|testRequestOnClosedIndex
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareSegments
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IndexClosedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexClosedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"closed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * setting the "ignoreUnavailable" option prevents IndexClosedException      */
DECL|method|testRequestOnClosedIndexIgnoreUnavailable
specifier|public
name|void
name|testRequestOnClosedIndexIgnoreUnavailable
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|IndicesOptions
name|defaultOptions
init|=
operator|new
name|IndicesSegmentsRequest
argument_list|()
operator|.
name|indicesOptions
argument_list|()
decl_stmt|;
name|IndicesOptions
name|testOptions
init|=
name|IndicesOptions
operator|.
name|fromOptions
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|defaultOptions
argument_list|)
decl_stmt|;
name|IndicesSegmentResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareSegments
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setIndicesOptions
argument_list|(
name|testOptions
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getIndices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * by default IndicesOptions setting IndicesSegmentsRequest should not throw exception when no index present      */
DECL|method|testAllowNoIndex
specifier|public
name|void
name|testAllowNoIndex
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|IndicesSegmentResponse
name|rsp
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareSegments
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getIndices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

