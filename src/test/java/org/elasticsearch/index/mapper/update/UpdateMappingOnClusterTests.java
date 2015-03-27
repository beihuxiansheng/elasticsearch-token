begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.update
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|update
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
name|util
operator|.
name|LuceneTestCase
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
name|mapping
operator|.
name|get
operator|.
name|GetMappingsResponse
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
name|mapping
operator|.
name|put
operator|.
name|PutMappingResponse
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
name|index
operator|.
name|mapper
operator|.
name|MapperParsingException
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
name|MergeMappingException
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|io
operator|.
name|Streams
operator|.
name|copyToStringFromClasspath
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
name|containsString
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

begin_class
DECL|class|UpdateMappingOnClusterTests
specifier|public
class|class
name|UpdateMappingOnClusterTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|INDEX
specifier|private
specifier|static
specifier|final
name|String
name|INDEX
init|=
literal|"index"
decl_stmt|;
DECL|field|TYPE
specifier|private
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"type"
decl_stmt|;
annotation|@
name|Test
DECL|method|test_all_enabled
specifier|public
name|void
name|test_all_enabled
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|mapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"mappings"
argument_list|)
operator|.
name|startObject
argument_list|(
name|TYPE
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|"false"
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
decl_stmt|;
name|XContentBuilder
name|mappingUpdate
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"text"
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
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|String
name|errorMessage
init|=
literal|"[_all] enabled is false now encountering true"
decl_stmt|;
name|testConflict
argument_list|(
name|mapping
operator|.
name|string
argument_list|()
argument_list|,
name|mappingUpdate
operator|.
name|string
argument_list|()
argument_list|,
name|errorMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_all_conflicts
specifier|public
name|void
name|test_all_conflicts
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/update/all_mapping_create_index.json"
argument_list|)
decl_stmt|;
name|String
name|mappingUpdate
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/update/all_mapping_update_with_conflicts.json"
argument_list|)
decl_stmt|;
name|String
index|[]
name|errorMessage
init|=
block|{
literal|"[_all] enabled is true now encountering false"
block|,
literal|"[_all] cannot enable norms (`norms.enabled`)"
block|,
literal|"[_all] has different store values"
block|,
literal|"[_all] has different store_term_vector values"
block|,
literal|"[_all] has different store_term_vector_offsets values"
block|,
literal|"[_all] has different store_term_vector_positions values"
block|,
literal|"[_all] has different store_term_vector_payloads values"
block|,
literal|"[_all] has different analyzer"
block|,
literal|"[_all] has different similarity"
block|}
decl_stmt|;
comment|// auto_boost and fielddata and search_analyzer should not report conflict
name|testConflict
argument_list|(
name|mapping
argument_list|,
name|mappingUpdate
argument_list|,
name|errorMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_all_with_default
specifier|public
name|void
name|test_all_with_default
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"_default_"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
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
literal|"index"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"_default_"
argument_list|,
name|defaultMapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|String
name|docMapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"doc"
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
name|PutMappingResponse
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
name|preparePutMapping
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|setSource
argument_list|(
name|docMapping
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|docMappingUpdate
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"text"
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
name|preparePutMapping
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|setSource
argument_list|(
name|docMappingUpdate
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|docMappingAllExplicitEnabled
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"doc_all_enabled"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
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
name|preparePutMapping
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"doc_all_enabled"
argument_list|)
operator|.
name|setSource
argument_list|(
name|docMappingAllExplicitEnabled
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|GetMappingsResponse
name|mapping
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
name|prepareGetMappings
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|HashMap
name|props
init|=
operator|(
name|HashMap
operator|)
name|mapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|getSourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"_all"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|Boolean
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"enabled"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|=
operator|(
name|HashMap
operator|)
name|mapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|getSourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"properties"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|=
operator|(
name|HashMap
operator|)
name|mapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|(
literal|"doc_all_enabled"
argument_list|)
operator|.
name|getSourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"_all"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Boolean
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"enabled"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|=
operator|(
name|HashMap
operator|)
name|mapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|get
argument_list|(
literal|"_default_"
argument_list|)
operator|.
name|getSourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"_all"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Boolean
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"enabled"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_doc_valuesInvalidMapping
specifier|public
name|void
name|test_doc_valuesInvalidMapping
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"mappings"
argument_list|)
operator|.
name|startObject
argument_list|(
name|TYPE
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
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
name|string
argument_list|()
decl_stmt|;
try|try
block|{
name|prepareCreate
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setSource
argument_list|(
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getDetailedMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"[_all] is always tokenized and cannot have doc values"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|test_doc_valuesInvalidMappingOnUpdate
specifier|public
name|void
name|test_doc_valuesInvalidMappingOnUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|TYPE
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"text"
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
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|prepareCreate
argument_list|(
name|INDEX
argument_list|)
operator|.
name|addMapping
argument_list|(
name|TYPE
argument_list|,
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|String
name|mappingUpdate
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
name|TYPE
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
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
name|string
argument_list|()
decl_stmt|;
name|GetMappingsResponse
name|mappingsBeforeUpdateResponse
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
name|prepareGetMappings
argument_list|(
name|INDEX
argument_list|)
operator|.
name|addTypes
argument_list|(
name|TYPE
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|preparePutMapping
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setType
argument_list|(
name|TYPE
argument_list|)
operator|.
name|setSource
argument_list|(
name|mappingUpdate
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getDetailedMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"[_all] is always tokenized and cannot have doc values"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// make sure all nodes have same cluster state
name|compareMappingOnNodes
argument_list|(
name|mappingsBeforeUpdateResponse
argument_list|)
expr_stmt|;
block|}
comment|// checks if the setting for timestamp and size are kept even if disabled
annotation|@
name|Test
DECL|method|testDisabledSizeTimestampIndexDoNotLooseMappings
specifier|public
name|void
name|testDisabledSizeTimestampIndexDoNotLooseMappings
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/update/default_mapping_with_disabled_root_types.json"
argument_list|)
decl_stmt|;
name|prepareCreate
argument_list|(
name|INDEX
argument_list|)
operator|.
name|addMapping
argument_list|(
name|TYPE
argument_list|,
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|GetMappingsResponse
name|mappingsBeforeGreen
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
name|prepareGetMappings
argument_list|(
name|INDEX
argument_list|)
operator|.
name|addTypes
argument_list|(
name|TYPE
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|ensureGreen
argument_list|(
name|INDEX
argument_list|)
expr_stmt|;
comment|// make sure all nodes have same cluster state
name|compareMappingOnNodes
argument_list|(
name|mappingsBeforeGreen
argument_list|)
expr_stmt|;
block|}
DECL|method|testConflict
specifier|protected
name|void
name|testConflict
parameter_list|(
name|String
name|mapping
parameter_list|,
name|String
name|mappingUpdate
parameter_list|,
name|String
modifier|...
name|errorMessages
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setSource
argument_list|(
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
name|INDEX
argument_list|)
expr_stmt|;
name|GetMappingsResponse
name|mappingsBeforeUpdateResponse
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
name|prepareGetMappings
argument_list|(
name|INDEX
argument_list|)
operator|.
name|addTypes
argument_list|(
name|TYPE
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|preparePutMapping
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setType
argument_list|(
name|TYPE
argument_list|)
operator|.
name|setSource
argument_list|(
name|mappingUpdate
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MergeMappingException
name|e
parameter_list|)
block|{
for|for
control|(
name|String
name|errorMessage
range|:
name|errorMessages
control|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getDetailedMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|errorMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|compareMappingOnNodes
argument_list|(
name|mappingsBeforeUpdateResponse
argument_list|)
expr_stmt|;
block|}
DECL|method|compareMappingOnNodes
specifier|private
name|void
name|compareMappingOnNodes
parameter_list|(
name|GetMappingsResponse
name|previousMapping
parameter_list|)
block|{
comment|// make sure all nodes have same cluster state
for|for
control|(
name|Client
name|client
range|:
name|cluster
argument_list|()
control|)
block|{
name|GetMappingsResponse
name|currentMapping
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetMappings
argument_list|(
name|INDEX
argument_list|)
operator|.
name|addTypes
argument_list|(
name|TYPE
argument_list|)
operator|.
name|setLocal
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|previousMapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
name|INDEX
argument_list|)
operator|.
name|get
argument_list|(
name|TYPE
argument_list|)
operator|.
name|source
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|currentMapping
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
name|INDEX
argument_list|)
operator|.
name|get
argument_list|(
name|TYPE
argument_list|)
operator|.
name|source
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUpdateTimestamp
specifier|public
name|void
name|testUpdateTimestamp
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"loading"
argument_list|,
literal|"lazy"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"store"
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
decl_stmt|;
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
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|GetMappingsResponse
name|appliedMappings
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
name|prepareGetMappings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|LinkedHashMap
name|timestampMapping
init|=
operator|(
name|LinkedHashMap
operator|)
name|appliedMappings
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
operator|.
name|getSourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"_timestamp"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|Boolean
operator|)
name|timestampMapping
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
operator|(
name|LinkedHashMap
operator|)
name|timestampMapping
operator|.
name|get
argument_list|(
literal|"fielddata"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"loading"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"lazy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
operator|(
name|LinkedHashMap
operator|)
name|timestampMapping
operator|.
name|get
argument_list|(
literal|"fielddata"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"format"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"doc_values"
argument_list|)
argument_list|)
expr_stmt|;
name|mapping
operator|=
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"loading"
argument_list|,
literal|"eager"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"array"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"store"
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
expr_stmt|;
name|PutMappingResponse
name|putMappingResponse
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
name|preparePutMapping
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|appliedMappings
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
name|prepareGetMappings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|timestampMapping
operator|=
operator|(
name|LinkedHashMap
operator|)
name|appliedMappings
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
operator|.
name|getSourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"_timestamp"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Boolean
operator|)
name|timestampMapping
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
operator|(
name|LinkedHashMap
operator|)
name|timestampMapping
operator|.
name|get
argument_list|(
literal|"fielddata"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"loading"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"eager"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
operator|(
name|LinkedHashMap
operator|)
name|timestampMapping
operator|.
name|get
argument_list|(
literal|"fielddata"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"format"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"array"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|LuceneTestCase
operator|.
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://github.com/elastic/elasticsearch/issues/10297"
argument_list|)
DECL|method|testTimestampMergingConflicts
specifier|public
name|void
name|testTimestampMergingConflicts
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
name|TYPE
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"analyzed"
argument_list|)
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"default"
argument_list|,
literal|"1970-01-01"
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
name|INDEX
argument_list|)
operator|.
name|addMapping
argument_list|(
name|TYPE
argument_list|,
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|mapping
operator|=
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"array"
argument_list|)
operator|.
name|endObject
argument_list|()
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
name|field
argument_list|(
literal|"path"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|field
argument_list|(
literal|"default"
argument_list|,
literal|"1970-01-02"
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
expr_stmt|;
name|GetMappingsResponse
name|mappingsBeforeUpdateResponse
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
name|prepareGetMappings
argument_list|(
name|INDEX
argument_list|)
operator|.
name|addTypes
argument_list|(
name|TYPE
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|preparePutMapping
argument_list|(
name|INDEX
argument_list|)
operator|.
name|setType
argument_list|(
name|TYPE
argument_list|)
operator|.
name|setSource
argument_list|(
name|mapping
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"This should result in conflicts when merging the mapping"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MergeMappingException
name|e
parameter_list|)
block|{
name|String
index|[]
name|expectedConflicts
init|=
block|{
literal|"mapper [_timestamp] has different index values"
block|,
literal|"mapper [_timestamp] has different store values"
block|,
literal|"Cannot update default in _timestamp value. Value is 1970-01-01 now encountering 1970-01-02"
block|,
literal|"Cannot update path in _timestamp value. Value is foo path in merged mapping is bar"
block|}
decl_stmt|;
for|for
control|(
name|String
name|conflict
range|:
name|expectedConflicts
control|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getDetailedMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|conflict
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|compareMappingOnNodes
argument_list|(
name|mappingsBeforeUpdateResponse
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

