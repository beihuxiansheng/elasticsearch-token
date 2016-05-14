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
name|ElasticsearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|action
operator|.
name|ingest
operator|.
name|DeletePipelineRequest
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
name|ingest
operator|.
name|GetPipelineRequest
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
name|ingest
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
name|action
operator|.
name|ingest
operator|.
name|PutPipelineRequest
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
name|ingest
operator|.
name|SimulateDocumentBaseResult
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
name|ingest
operator|.
name|SimulatePipelineRequest
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
name|ingest
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
name|action
operator|.
name|ingest
operator|.
name|WritePipelineResponse
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
name|bytes
operator|.
name|BytesReference
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
name|ingest
operator|.
name|core
operator|.
name|IngestDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeModule
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
name|Collection
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
name|concurrent
operator|.
name|ExecutionException
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
annotation|@
name|ESIntegTestCase
operator|.
name|ClusterScope
argument_list|(
name|minNumDataNodes
operator|=
literal|2
argument_list|)
DECL|class|IngestClientIT
specifier|public
class|class
name|IngestClientIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
comment|// TODO: Remove this method once gets in: https://github.com/elastic/elasticsearch/issues/16019
if|if
condition|(
name|nodeOrdinal
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.ingest"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
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
name|pluginList
argument_list|(
name|IngestPlugin
operator|.
name|class
argument_list|)
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
name|BytesReference
name|pipelineSource
init|=
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
literal|"test"
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
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutPipeline
argument_list|(
literal|"_id"
argument_list|,
name|pipelineSource
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|GetPipelineResponse
name|getResponse
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
name|prepareGetPipeline
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
name|BytesReference
name|bytes
init|=
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
name|field
argument_list|(
literal|"fail"
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
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|SimulatePipelineResponse
name|response
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareSimulatePipeline
argument_list|(
name|bytes
argument_list|)
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SimulatePipelineRequest
name|request
init|=
operator|new
name|SimulatePipelineRequest
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|request
operator|.
name|setId
argument_list|(
literal|"_id"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|simulatePipeline
argument_list|(
name|request
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
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
name|SimulateDocumentBaseResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SimulateDocumentBaseResult
name|simulateDocumentBaseResult
init|=
operator|(
name|SimulateDocumentBaseResult
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
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"fail"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"processed"
argument_list|,
literal|true
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
name|simulateDocumentBaseResult
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
name|simulateDocumentBaseResult
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
name|BytesReference
name|source
init|=
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
literal|"test"
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
decl_stmt|;
name|PutPipelineRequest
name|putPipelineRequest
init|=
operator|new
name|PutPipelineRequest
argument_list|(
literal|"_id"
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|putPipeline
argument_list|(
name|putPipelineRequest
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
operator|.
name|setPipeline
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
name|indexRequest
operator|.
name|source
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|,
literal|"fail"
argument_list|,
name|i
operator|%
literal|2
operator|==
literal|0
argument_list|)
expr_stmt|;
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
literal|"java.lang.IllegalArgumentException: test processor failed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"Expected a successful response but found failure ["
operator|+
name|itemResponse
operator|.
name|getFailure
argument_list|()
operator|+
literal|"]."
argument_list|,
name|itemResponse
operator|.
name|isFailed
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
name|indexResponse
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
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
name|BytesReference
name|source
init|=
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
literal|"test"
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
decl_stmt|;
name|PutPipelineRequest
name|putPipelineRequest
init|=
operator|new
name|PutPipelineRequest
argument_list|(
literal|"_id"
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|putPipeline
argument_list|(
name|putPipelineRequest
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|GetPipelineRequest
name|getPipelineRequest
init|=
operator|new
name|GetPipelineRequest
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
name|GetPipelineResponse
name|getResponse
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
name|getPipeline
argument_list|(
name|getPipelineRequest
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
name|setPipeline
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|,
literal|"fail"
argument_list|,
literal|false
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
literal|"field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"processed"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
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
literal|"field"
argument_list|,
literal|"value2"
argument_list|,
literal|"fail"
argument_list|,
literal|false
argument_list|)
operator|.
name|setPipeline
argument_list|(
literal|"_id"
argument_list|)
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
literal|"field"
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
name|doc
operator|.
name|get
argument_list|(
literal|"processed"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|DeletePipelineRequest
name|deletePipelineRequest
init|=
operator|new
name|DeletePipelineRequest
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
name|WritePipelineResponse
name|response
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
name|deletePipeline
argument_list|(
name|deletePipelineRequest
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareGetPipeline
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
DECL|method|testPutWithPipelineFactoryError
specifier|public
name|void
name|testPutWithPipelineFactoryError
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesReference
name|source
init|=
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
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"unused"
argument_list|,
literal|":sad_face:"
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
decl_stmt|;
name|PutPipelineRequest
name|putPipelineRequest
init|=
operator|new
name|PutPipelineRequest
argument_list|(
literal|"_id"
argument_list|,
name|source
argument_list|)
decl_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|putPipeline
argument_list|(
name|putPipelineRequest
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|ElasticsearchParseException
name|ex
init|=
operator|(
name|ElasticsearchParseException
operator|)
name|ExceptionsHelper
operator|.
name|unwrap
argument_list|(
name|e
argument_list|,
name|ElasticsearchParseException
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor [test] doesn't support one or more provided configuration parameters [unused]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|singletonList
argument_list|(
name|TestSeedPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|class|IngestPlugin
specifier|public
specifier|static
class|class
name|IngestPlugin
extends|extends
name|Plugin
block|{
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"ingest"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"ingest mock"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|NodeModule
name|nodeModule
parameter_list|)
block|{
name|nodeModule
operator|.
name|registerProcessor
argument_list|(
literal|"test"
argument_list|,
parameter_list|(
name|templateService
parameter_list|,
name|registry
parameter_list|)
lambda|->
name|config
lambda|->
operator|new
name|TestProcessor
argument_list|(
literal|"id"
argument_list|,
literal|"test"
argument_list|,
name|ingestDocument
lambda|->
block|{
name|ingestDocument
operator|.
name|setFieldValue
argument_list|(
literal|"processed"
argument_list|,
literal|true
argument_list|)
argument_list|;                     if
operator|(
name|ingestDocument
operator|.
name|getFieldValue
argument_list|(
literal|"fail"
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
operator|)
block|{
throw|throw
argument_list|new
name|IllegalArgumentException
argument_list|(
literal|"test processor failed"
argument_list|)
block|;                     }
block|}
block|)
block|)
class|;
end_class

unit|}     } }
end_unit

