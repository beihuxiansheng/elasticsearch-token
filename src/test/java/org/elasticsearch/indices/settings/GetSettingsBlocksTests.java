begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|settings
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|get
operator|.
name|GetSettingsResponse
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
name|ImmutableSettings
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
name|ElasticsearchIntegrationTest
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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|*
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
name|assertAcked
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
name|assertBlocked
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
name|greaterThanOrEqualTo
import|;
end_import

begin_class
DECL|class|GetSettingsBlocksTests
specifier|public
class|class
name|GetSettingsBlocksTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testGetSettingsWithBlocks
specifier|public
name|void
name|testGetSettingsWithBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.merge.policy.expunge_deletes_allowed"
argument_list|,
literal|"30"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.mapper.dynamic"
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|block
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|SETTING_BLOCKS_READ
argument_list|,
name|SETTING_BLOCKS_WRITE
argument_list|,
name|SETTING_READ_ONLY
argument_list|)
control|)
block|{
try|try
block|{
name|enableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|GetSettingsResponse
name|response
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
name|prepareGetSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndexToSettings
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.refresh_interval"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.merge.policy.expunge_deletes_allowed"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"30"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.mapper.dynamic"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|disableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|block
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|enableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|SETTING_BLOCKS_METADATA
argument_list|)
expr_stmt|;
name|assertBlocked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetSettings
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|disableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|SETTING_BLOCKS_METADATA
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

