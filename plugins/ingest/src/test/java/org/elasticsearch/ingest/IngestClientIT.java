begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
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
name|action
operator|.
name|bulk
operator|.
name|BulkItemResponse
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
name|bulk
operator|.
name|BulkRequest
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
name|bulk
operator|.
name|BulkResponse
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
name|delete
operator|.
name|DeleteResponse
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
name|action
operator|.
name|index
operator|.
name|IndexResponse
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
name|plugin
operator|.
name|ingest
operator|.
name|IngestPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|delete
operator|.
name|DeletePipelineAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|delete
operator|.
name|DeletePipelineRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|get
operator|.
name|GetPipelineAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|get
operator|.
name|GetPipelineRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|get
operator|.
name|GetPipelineResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|put
operator|.
name|PutPipelineAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|put
operator|.
name|PutPipelineRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|simulate
operator|.
name|SimulateDocumentSimpleResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|simulate
operator|.
name|SimulatePipelineAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|simulate
operator|.
name|SimulatePipelineRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
operator|.
name|simulate
operator|.
name|SimulatePipelineResponse
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
name|ESIntegTestCase
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
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|instanceOf
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|nullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_class
DECL|class|IngestClientIT
specifier|public
class|class
name|IngestClientIT
extends|extends
name|ESIntegTestCase
block|{
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
name|pluginList
argument_list|(
name|IngestPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|transportClientPlugins
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
name|transportClientPlugins
parameter_list|()
block|{
return|return
name|nodePlugins
argument_list|()
return|;
block|}
DECL|method|testSimulate
specifier|public
name|void
name|testSimulate
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|PutPipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|PutPipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"description"
argument_list|,
literal|"my_pipeline"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"processors"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"grok"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"pattern"
argument_list|,
literal|"%{NUMBER:val:float} %{NUMBER:status:int}<%{WORD:msg}>"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|GetPipelineResponse
name|getResponse
init|=
operator|new
name|GetPipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|GetPipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setIds
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|isFound
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|pipelines
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
name|getResponse
operator|.
name|pipelines
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|SimulatePipelineResponse
name|response
init|=
operator|new
name|SimulatePipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|SimulatePipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startArray
argument_list|(
literal|"docs"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"_index"
argument_list|,
literal|"index"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_type"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_id"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_source"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|isVerbose
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getPipelineId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResults
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
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|SimulateDocumentSimpleResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SimulateDocumentSimpleResult
name|simulateDocumentSimpleResult
init|=
operator|(
name|SimulateDocumentSimpleResult
operator|)
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentSimpleResult
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentSimpleResult
operator|.
name|getFailure
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
operator|new
name|SimulatePipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|SimulatePipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startArray
argument_list|(
literal|"docs"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"_index"
argument_list|,
literal|"index"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_type"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_id"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_source"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"123.42 400<foo>"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|isVerbose
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getPipelineId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResults
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
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|SimulateDocumentSimpleResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|simulateDocumentSimpleResult
operator|=
operator|(
name|SimulateDocumentSimpleResult
operator|)
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"field1"
argument_list|,
literal|"123.42 400<foo>"
argument_list|)
expr_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"val"
argument_list|,
literal|123.42f
argument_list|)
expr_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"msg"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|IngestDocument
name|ingestDocument
init|=
operator|new
name|IngestDocument
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentSimpleResult
operator|.
name|getIngestDocument
argument_list|()
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentSimpleResult
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBulkWithIngestFailures
specifier|public
name|void
name|testBulkWithIngestFailures
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
operator|new
name|PutPipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|PutPipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"description"
argument_list|,
literal|"my_pipeline"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"processors"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"join"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"separator"
argument_list|,
literal|"|"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|int
name|numRequests
init|=
name|scaledRandomIntBetween
argument_list|(
literal|32
argument_list|,
literal|128
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|bulkRequest
operator|.
name|putHeader
argument_list|(
name|IngestPlugin
operator|.
name|PIPELINE_ID_PARAM
argument_list|,
literal|"_id"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numRequests
condition|;
name|i
operator|++
control|)
block|{
name|IndexRequest
name|indexRequest
init|=
operator|new
name|IndexRequest
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|indexRequest
operator|.
name|source
argument_list|(
literal|"field1"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexRequest
operator|.
name|source
argument_list|(
literal|"field2"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bulkRequest
operator|.
name|add
argument_list|(
name|indexRequest
argument_list|)
expr_stmt|;
block|}
name|BulkResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|bulk
argument_list|(
name|bulkRequest
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getItems
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BulkItemResponse
name|itemResponse
init|=
name|response
operator|.
name|getItems
argument_list|()
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|IndexResponse
name|indexResponse
init|=
name|itemResponse
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|indexResponse
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexResponse
operator|.
name|isCreated
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BulkItemResponse
operator|.
name|Failure
name|failure
init|=
name|itemResponse
operator|.
name|getFailure
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|failure
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"java.lang.IllegalArgumentException: field [field1] not present as part of path [field1]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|PutPipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|PutPipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"description"
argument_list|,
literal|"my_pipeline"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"processors"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"grok"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"pattern"
argument_list|,
literal|"%{NUMBER:val:float} %{NUMBER:status:int}<%{WORD:msg}>"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|GetPipelineResponse
name|getResponse
init|=
operator|new
name|GetPipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|GetPipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setIds
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|isFound
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|pipelines
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
name|getResponse
operator|.
name|pipelines
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|XContentBuilder
name|updateMappingBuilder
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"status"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"val"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
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
name|updateMappingBuilder
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
name|putMappingResponse
argument_list|)
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
literal|"field1"
argument_list|,
literal|"123.42 400<foo>"
argument_list|)
operator|.
name|putHeader
argument_list|(
name|IngestPlugin
operator|.
name|PIPELINE_ID_PARAM
argument_list|,
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
init|=
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getSourceAsMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"val"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|123.42
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|400
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"123.42 400<foo>"
argument_list|)
argument_list|)
operator|.
name|putHeader
argument_list|(
name|IngestPlugin
operator|.
name|PIPELINE_ID_PARAM
argument_list|,
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|doc
operator|=
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getSourceAsMap
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"val"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|123.42
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|400
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|DeleteResponse
name|response
init|=
operator|new
name|DeletePipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|DeletePipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|isFound
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
name|getResponse
operator|=
operator|new
name|GetPipelineRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|,
name|GetPipelineAction
operator|.
name|INSTANCE
argument_list|)
operator|.
name|setIds
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|isFound
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|pipelines
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
block|}
annotation|@
name|Override
DECL|method|getMockPlugins
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
name|getMockPlugins
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

