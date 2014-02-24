begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.mapping
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|mapping
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
name|mapping
operator|.
name|get
operator|.
name|GetFieldMappingsResponse
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
name|XContentBuilder
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
name|hamcrest
operator|.
name|Matchers
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
name|io
operator|.
name|IOException
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
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
comment|/**  *  */
end_comment

begin_class
DECL|class|SimpleGetFieldMappingsTests
specifier|public
class|class
name|SimpleGetFieldMappingsTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|getMappingsWhereThereAreNone
specifier|public
name|void
name|getMappingsWhereThereAreNone
parameter_list|()
block|{
name|createIndex
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|GetFieldMappingsResponse
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
name|prepareGetFieldMappings
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
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
name|response
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
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
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"field"
argument_list|)
argument_list|,
name|Matchers
operator|.
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getMappingForType
specifier|private
name|XContentBuilder
name|getMappingForType
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|type
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"obj"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"subfield"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
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
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|simpleGetFieldMappings
specifier|public
name|void
name|simpleGetFieldMappings
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
operator|.
name|Builder
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_shards"
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"number_of_replicas"
argument_list|,
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
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
name|prepareCreate
argument_list|(
literal|"indexa"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"typeA"
argument_list|,
name|getMappingForType
argument_list|(
literal|"typeA"
argument_list|)
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"typeB"
argument_list|,
name|getMappingForType
argument_list|(
literal|"typeB"
argument_list|)
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
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
name|prepareCreate
argument_list|(
literal|"indexb"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"typeA"
argument_list|,
name|getMappingForType
argument_list|(
literal|"typeA"
argument_list|)
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"typeB"
argument_list|,
name|getMappingForType
argument_list|(
literal|"typeB"
argument_list|)
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
comment|// Get mappings by full name
name|GetFieldMappingsResponse
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
name|prepareGetFieldMappings
argument_list|(
literal|"indexa"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"typeA"
argument_list|)
operator|.
name|setFields
argument_list|(
literal|"field1"
argument_list|,
literal|"obj.subfield"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"obj.subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"obj.subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"indexa"
argument_list|)
argument_list|,
name|not
argument_list|(
name|hasKey
argument_list|(
literal|"typeB"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|mappings
argument_list|()
argument_list|,
name|not
argument_list|(
name|hasKey
argument_list|(
literal|"indexb"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get mappings by name
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetFieldMappings
argument_list|(
literal|"indexa"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"typeA"
argument_list|)
operator|.
name|setFields
argument_list|(
literal|"field1"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// get mappings by name across multiple indices
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetFieldMappings
argument_list|()
operator|.
name|setTypes
argument_list|(
literal|"typeA"
argument_list|)
operator|.
name|setFields
argument_list|(
literal|"subfield"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// get mappings by name across multiple types
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetFieldMappings
argument_list|(
literal|"indexa"
argument_list|)
operator|.
name|setFields
argument_list|(
literal|"subfield"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeA"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// get mappings by name across multiple types& indices
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetFieldMappings
argument_list|()
operator|.
name|setFields
argument_list|(
literal|"subfield"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeA"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexa"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeA"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|fullName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"obj.subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
argument_list|,
name|hasKey
argument_list|(
literal|"subfield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"indexb"
argument_list|,
literal|"typeB"
argument_list|,
literal|"field1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|simpleGetFieldMappingsWithDefaults
specifier|public
name|void
name|simpleGetFieldMappingsWithDefaults
parameter_list|()
throws|throws
name|Exception
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
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|getMappingForType
argument_list|(
literal|"type"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"num"
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|GetFieldMappingsResponse
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
name|prepareGetFieldMappings
argument_list|()
operator|.
name|setFields
argument_list|(
literal|"num"
argument_list|,
literal|"field1"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|includeDefaults
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"num"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"num"
argument_list|)
argument_list|,
name|hasEntry
argument_list|(
literal|"index"
argument_list|,
operator|(
name|Object
operator|)
literal|"not_analyzed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"num"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"num"
argument_list|)
argument_list|,
name|hasEntry
argument_list|(
literal|"type"
argument_list|,
operator|(
name|Object
operator|)
literal|"long"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
argument_list|,
name|hasEntry
argument_list|(
literal|"index"
argument_list|,
operator|(
name|Object
operator|)
literal|"analyzed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
argument_list|)
argument_list|,
name|hasEntry
argument_list|(
literal|"type"
argument_list|,
operator|(
name|Object
operator|)
literal|"string"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"subfield"
argument_list|)
argument_list|,
name|hasEntry
argument_list|(
literal|"index"
argument_list|,
operator|(
name|Object
operator|)
literal|"not_analyzed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|response
operator|.
name|fieldMappings
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"subfield"
argument_list|)
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"subfield"
argument_list|)
argument_list|,
name|hasEntry
argument_list|(
literal|"type"
argument_list|,
operator|(
name|Object
operator|)
literal|"string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

