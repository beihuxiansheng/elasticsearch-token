begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bwcompat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bwcompat
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
name|alias
operator|.
name|Alias
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
name|create
operator|.
name|CreateIndexResponse
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
name|get
operator|.
name|GetIndexRequest
operator|.
name|Feature
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
name|get
operator|.
name|GetIndexResponse
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
name|AliasMetaData
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
name|MappingMetaData
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
name|collect
operator|.
name|ImmutableOpenMap
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
name|test
operator|.
name|ESBackcompatTestCase
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|anyOf
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
DECL|class|GetIndexBackwardsCompatibilityIT
specifier|public
class|class
name|GetIndexBackwardsCompatibilityIT
extends|extends
name|ESBackcompatTestCase
block|{
DECL|method|testGetAliases
specifier|public
name|void
name|testGetAliases
parameter_list|()
throws|throws
name|Exception
block|{
name|CreateIndexResponse
name|createIndexResponse
init|=
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addAlias
argument_list|(
operator|new
name|Alias
argument_list|(
literal|"testAlias"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
name|createIndexResponse
argument_list|)
expr_stmt|;
name|GetIndexResponse
name|getIndexResponse
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
name|prepareGetIndex
argument_list|()
operator|.
name|addIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addFeatures
argument_list|(
name|Feature
operator|.
name|ALIASES
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliasesMap
init|=
name|getIndexResponse
operator|.
name|aliases
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|aliasesMap
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aliasesMap
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AliasMetaData
argument_list|>
name|aliasesList
init|=
name|aliasesMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|aliasesList
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aliasesList
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|AliasMetaData
name|alias
init|=
name|aliasesList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|alias
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|alias
operator|.
name|alias
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"testAlias"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetMappings
specifier|public
name|void
name|testGetMappings
parameter_list|()
throws|throws
name|Exception
block|{
name|CreateIndexResponse
name|createIndexResponse
init|=
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
literal|"{\"type1\":{}}"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
name|createIndexResponse
argument_list|)
expr_stmt|;
name|GetIndexResponse
name|getIndexResponse
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
name|prepareGetIndex
argument_list|()
operator|.
name|addIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addFeatures
argument_list|(
name|Feature
operator|.
name|MAPPINGS
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|mappings
init|=
name|getIndexResponse
operator|.
name|mappings
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|mappings
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mappings
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|indexMappings
init|=
name|mappings
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|indexMappings
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMappings
operator|.
name|size
argument_list|()
argument_list|,
name|anyOf
argument_list|(
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexMappings
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
name|MappingMetaData
name|mapping
init|=
name|indexMappings
operator|.
name|get
argument_list|(
literal|"_default_"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|mapping
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|MappingMetaData
name|mapping
init|=
name|indexMappings
operator|.
name|get
argument_list|(
literal|"type1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|mapping
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mapping
operator|.
name|type
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"type1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetSettings
specifier|public
name|void
name|testGetSettings
parameter_list|()
throws|throws
name|Exception
block|{
name|CreateIndexResponse
name|createIndexResponse
init|=
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
name|createIndexResponse
argument_list|)
expr_stmt|;
name|GetIndexResponse
name|getIndexResponse
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
name|prepareGetIndex
argument_list|()
operator|.
name|addIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addFeatures
argument_list|(
name|Feature
operator|.
name|SETTINGS
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|settingsMap
init|=
name|getIndexResponse
operator|.
name|settings
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|settingsMap
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settingsMap
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|settingsMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|settings
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"index.number_of_shards"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

