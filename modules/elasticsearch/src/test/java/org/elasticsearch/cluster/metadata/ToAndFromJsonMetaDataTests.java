begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|AliasMetaData
operator|.
name|newAliasMetaDataBuilder
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
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|*
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
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|ToAndFromJsonMetaDataTests
specifier|public
class|class
name|ToAndFromJsonMetaDataTests
block|{
annotation|@
name|Test
DECL|method|testSimpleJsonFromAndTo
specifier|public
name|void
name|testSimpleJsonFromAndTo
parameter_list|()
throws|throws
name|IOException
block|{
name|MetaData
name|metaData
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"setting1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"setting2"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|2
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test3"
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
operator|.
name|putMapping
argument_list|(
literal|"mapping1"
argument_list|,
name|MAPPING_SOURCE1
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test4"
argument_list|)
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"setting1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"setting2"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
operator|.
name|putMapping
argument_list|(
literal|"mapping1"
argument_list|,
name|MAPPING_SOURCE1
argument_list|)
operator|.
name|putMapping
argument_list|(
literal|"mapping2"
argument_list|,
name|MAPPING_SOURCE2
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test5"
argument_list|)
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"setting1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"setting2"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
operator|.
name|putMapping
argument_list|(
literal|"mapping1"
argument_list|,
name|MAPPING_SOURCE1
argument_list|)
operator|.
name|putMapping
argument_list|(
literal|"mapping2"
argument_list|,
name|MAPPING_SOURCE2
argument_list|)
operator|.
name|putAlias
argument_list|(
name|newAliasMetaDataBuilder
argument_list|(
literal|"alias1"
argument_list|)
argument_list|)
operator|.
name|putAlias
argument_list|(
name|newAliasMetaDataBuilder
argument_list|(
literal|"alias2"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test6"
argument_list|)
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"setting1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"setting2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.aliases.0"
argument_list|,
literal|"alias3"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.aliases.1"
argument_list|,
literal|"alias1"
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
operator|.
name|putMapping
argument_list|(
literal|"mapping1"
argument_list|,
name|MAPPING_SOURCE1
argument_list|)
operator|.
name|putMapping
argument_list|(
literal|"mapping2"
argument_list|,
name|MAPPING_SOURCE2
argument_list|)
operator|.
name|putAlias
argument_list|(
name|newAliasMetaDataBuilder
argument_list|(
literal|"alias1"
argument_list|)
argument_list|)
operator|.
name|putAlias
argument_list|(
name|newAliasMetaDataBuilder
argument_list|(
literal|"alias2"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|metaDataSource
init|=
name|MetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|metaData
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ToJson: "
operator|+
name|metaDataSource
argument_list|)
expr_stmt|;
name|MetaData
name|parsedMetaData
init|=
name|MetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|metaDataSource
argument_list|)
argument_list|)
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|parsedMetaData
operator|.
name|index
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|indexMetaData
operator|=
name|parsedMetaData
operator|.
name|index
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|indexMetaData
operator|=
name|parsedMetaData
operator|.
name|index
argument_list|(
literal|"test3"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
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
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapping1"
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|MAPPING_SOURCE1
argument_list|)
argument_list|)
expr_stmt|;
name|indexMetaData
operator|=
name|parsedMetaData
operator|.
name|index
argument_list|(
literal|"test4"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapping1"
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|MAPPING_SOURCE1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapping2"
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|MAPPING_SOURCE2
argument_list|)
argument_list|)
expr_stmt|;
name|indexMetaData
operator|=
name|parsedMetaData
operator|.
name|index
argument_list|(
literal|"test5"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapping1"
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|MAPPING_SOURCE1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapping2"
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|MAPPING_SOURCE2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|get
argument_list|(
literal|"alias1"
argument_list|)
operator|.
name|alias
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"alias1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|get
argument_list|(
literal|"alias2"
argument_list|)
operator|.
name|alias
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"alias2"
argument_list|)
argument_list|)
expr_stmt|;
name|indexMetaData
operator|=
name|parsedMetaData
operator|.
name|index
argument_list|(
literal|"test6"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"setting2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapping1"
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|MAPPING_SOURCE1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapping2"
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|MAPPING_SOURCE2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|get
argument_list|(
literal|"alias1"
argument_list|)
operator|.
name|alias
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"alias1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|get
argument_list|(
literal|"alias2"
argument_list|)
operator|.
name|alias
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"alias2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|get
argument_list|(
literal|"alias3"
argument_list|)
operator|.
name|alias
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"alias3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|MAPPING_SOURCE1
specifier|private
specifier|static
specifier|final
name|String
name|MAPPING_SOURCE1
init|=
literal|"{\"mapping1\":{\"text1\":{\"type\":\"string\"}}}"
decl_stmt|;
DECL|field|MAPPING_SOURCE2
specifier|private
specifier|static
specifier|final
name|String
name|MAPPING_SOURCE2
init|=
literal|"{\"mapping2\":{\"text2\":{\"type\":\"string\"}}}"
decl_stmt|;
block|}
end_class

end_unit

