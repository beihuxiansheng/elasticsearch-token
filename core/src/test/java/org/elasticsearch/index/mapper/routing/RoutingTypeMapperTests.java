begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|routing
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
name|IndexOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|index
operator|.
name|IndexRequest
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
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|xcontent
operator|.
name|ToXContent
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
name|XContentBuilder
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
name|XContentParser
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
name|json
operator|.
name|JsonXContent
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
name|ParsedDocument
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
name|SourceToParse
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
name|ElasticsearchSingleNodeTest
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

begin_class
DECL|class|RoutingTypeMapperTests
specifier|public
class|class
name|RoutingTypeMapperTests
extends|extends
name|ElasticsearchSingleNodeTest
block|{
DECL|method|testRoutingMapper
specifier|public
name|void
name|testRoutingMapper
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
literal|"type"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
operator|.
name|routing
argument_list|(
literal|"routing_value"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|get
argument_list|(
literal|"_routing"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"routing_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|get
argument_list|(
literal|"field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTypeSettingsBackcompat
specifier|public
name|void
name|testFieldTypeSettingsBackcompat
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_routing"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|Settings
name|indexSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|V_1_4_2
operator|.
name|id
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|indexSettings
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|routingFieldMapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|NONE
argument_list|,
name|docMapper
operator|.
name|routingFieldMapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldTypeSettingsSerializationBackcompat
specifier|public
name|void
name|testFieldTypeSettingsSerializationBackcompat
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|enabledMapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_routing"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|Settings
name|indexSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|V_1_4_2
operator|.
name|id
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DocumentMapper
name|enabledMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|indexSettings
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
name|enabledMapping
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
name|enabledMapper
operator|.
name|routingFieldMapper
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|serializedMap
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
init|)
block|{
name|serializedMap
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|serializedMap
argument_list|,
name|hasKey
argument_list|(
literal|"_routing"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|serializedMap
operator|.
name|get
argument_list|(
literal|"_routing"
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|routingConfiguration
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|serializedMap
operator|.
name|get
argument_list|(
literal|"_routing"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|routingConfiguration
argument_list|,
name|hasKey
argument_list|(
literal|"store"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingConfiguration
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingConfiguration
argument_list|,
name|hasKey
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingConfiguration
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"no"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPathBackcompat
specifier|public
name|void
name|testPathBackcompat
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_routing"
argument_list|)
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
literal|"custom_routing"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|V_1_4_2
operator|.
name|id
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|settings
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|XContentBuilder
name|doc
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"custom_routing"
argument_list|,
literal|"routing_value"
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|MappingMetaData
name|mappingMetaData
init|=
operator|new
name|MappingMetaData
argument_list|(
name|docMapper
argument_list|)
decl_stmt|;
name|IndexRequest
name|request
init|=
operator|new
name|IndexRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|source
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|request
operator|.
name|process
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
name|mappingMetaData
argument_list|,
literal|true
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|routing
argument_list|()
argument_list|,
literal|"routing_value"
argument_list|)
expr_stmt|;
block|}
DECL|method|testIncludeInObjectBackcompat
specifier|public
name|void
name|testIncludeInObjectBackcompat
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|V_1_4_2
operator|.
name|id
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|,
name|settings
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|XContentBuilder
name|doc
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"_timestamp"
argument_list|,
literal|2000000
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|MappingMetaData
name|mappingMetaData
init|=
operator|new
name|MappingMetaData
argument_list|(
name|docMapper
argument_list|)
decl_stmt|;
name|IndexRequest
name|request
init|=
operator|new
name|IndexRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|source
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|request
operator|.
name|process
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
name|mappingMetaData
argument_list|,
literal|true
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
comment|// _routing in a document never worked, so backcompat is ignoring the field
name|assertNull
argument_list|(
name|request
operator|.
name|routing
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|docMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|doc
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
operator|.
name|get
argument_list|(
literal|"_routing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

