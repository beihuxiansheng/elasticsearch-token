begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|ingest
operator|.
name|PipelineStore
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
name|ProcessorsRegistry
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
name|TestProcessor
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
name|TestTemplateService
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
name|CompoundProcessor
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
name|ingest
operator|.
name|core
operator|.
name|Pipeline
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
name|Processor
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
name|ESTestCase
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
name|ArrayList
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
name|Iterator
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
name|action
operator|.
name|ingest
operator|.
name|SimulatePipelineRequest
operator|.
name|Fields
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|IngestDocument
operator|.
name|MetaData
operator|.
name|ID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|IngestDocument
operator|.
name|MetaData
operator|.
name|INDEX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|IngestDocument
operator|.
name|MetaData
operator|.
name|TYPE
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
name|nullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|SimulatePipelineRequestParsingTests
specifier|public
class|class
name|SimulatePipelineRequestParsingTests
extends|extends
name|ESTestCase
block|{
DECL|field|store
specifier|private
name|PipelineStore
name|store
decl_stmt|;
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|TestProcessor
name|processor
init|=
operator|new
name|TestProcessor
argument_list|(
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|CompoundProcessor
name|pipelineCompoundProcessor
init|=
operator|new
name|CompoundProcessor
argument_list|(
name|processor
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
name|SimulatePipelineRequest
operator|.
name|SIMULATED_PIPELINE_ID
argument_list|,
literal|null
argument_list|,
name|pipelineCompoundProcessor
argument_list|)
decl_stmt|;
name|ProcessorsRegistry
operator|.
name|Builder
name|processorRegistryBuilder
init|=
operator|new
name|ProcessorsRegistry
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|processorRegistryBuilder
operator|.
name|registerProcessor
argument_list|(
literal|"mock_processor"
argument_list|,
operator|(
parameter_list|(
name|templateService
parameter_list|,
name|registry
parameter_list|)
lambda|->
name|mock
argument_list|(
name|Processor
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|ProcessorsRegistry
name|processorRegistry
init|=
name|processorRegistryBuilder
operator|.
name|build
argument_list|(
name|TestTemplateService
operator|.
name|instance
argument_list|()
argument_list|)
decl_stmt|;
name|store
operator|=
name|mock
argument_list|(
name|PipelineStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|store
operator|.
name|get
argument_list|(
name|SimulatePipelineRequest
operator|.
name|SIMULATED_PIPELINE_ID
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|store
operator|.
name|getProcessorRegistry
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|processorRegistry
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseUsingPipelineStore
specifier|public
name|void
name|testParseUsingPipelineStore
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDocs
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|requestContent
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|expectedDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|requestContent
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|DOCS
argument_list|,
name|docs
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|index
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|INDEX
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|TYPE
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|ID
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|fieldValue
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|SOURCE
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedDoc
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|INDEX
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|TYPE
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|ID
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|SOURCE
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
argument_list|)
argument_list|)
expr_stmt|;
name|expectedDocs
operator|.
name|add
argument_list|(
name|expectedDoc
argument_list|)
expr_stmt|;
block|}
name|SimulatePipelineRequest
operator|.
name|Parsed
name|actualRequest
init|=
name|SimulatePipelineRequest
operator|.
name|parseWithPipelineId
argument_list|(
name|SimulatePipelineRequest
operator|.
name|SIMULATED_PIPELINE_ID
argument_list|,
name|requestContent
argument_list|,
literal|false
argument_list|,
name|store
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actualRequest
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
name|actualRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|expectedDocsIterator
init|=
name|expectedDocs
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|IngestDocument
name|ingestDocument
range|:
name|actualRequest
operator|.
name|getDocuments
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedDocument
init|=
name|expectedDocsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|IngestDocument
operator|.
name|MetaData
argument_list|,
name|String
argument_list|>
name|metadataMap
init|=
name|ingestDocument
operator|.
name|extractMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metadataMap
operator|.
name|get
argument_list|(
name|INDEX
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|INDEX
operator|.
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadataMap
operator|.
name|get
argument_list|(
name|TYPE
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|TYPE
operator|.
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadataMap
operator|.
name|get
argument_list|(
name|ID
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|ID
operator|.
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|Fields
operator|.
name|SOURCE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|actualRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SimulatePipelineRequest
operator|.
name|SIMULATED_PIPELINE_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getDescription
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProcessors
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
block|}
DECL|method|testParseWithProvidedPipeline
specifier|public
name|void
name|testParseWithProvidedPipeline
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDocs
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|requestContent
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|expectedDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|requestContent
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|DOCS
argument_list|,
name|docs
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|index
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|INDEX
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|TYPE
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|ID
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|fieldValue
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|SOURCE
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedDoc
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|INDEX
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|TYPE
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|ID
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|expectedDoc
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|SOURCE
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
argument_list|)
argument_list|)
expr_stmt|;
name|expectedDocs
operator|.
name|add
argument_list|(
name|expectedDoc
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pipelineConfig
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|processors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numProcessors
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
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
name|numProcessors
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|processorConfig
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|onFailureProcessors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numOnFailureProcessors
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1
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
name|numOnFailureProcessors
condition|;
name|j
operator|++
control|)
block|{
name|onFailureProcessors
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"mock_processor"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numOnFailureProcessors
operator|>
literal|0
condition|)
block|{
name|processorConfig
operator|.
name|put
argument_list|(
literal|"on_failure"
argument_list|,
name|onFailureProcessors
argument_list|)
expr_stmt|;
block|}
name|processors
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"mock_processor"
argument_list|,
name|processorConfig
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pipelineConfig
operator|.
name|put
argument_list|(
literal|"processors"
argument_list|,
name|processors
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|onFailureProcessors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numOnFailureProcessors
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
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
name|numOnFailureProcessors
condition|;
name|i
operator|++
control|)
block|{
name|onFailureProcessors
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"mock_processor"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numOnFailureProcessors
operator|>
literal|0
condition|)
block|{
name|pipelineConfig
operator|.
name|put
argument_list|(
literal|"on_failure"
argument_list|,
name|onFailureProcessors
argument_list|)
expr_stmt|;
block|}
name|requestContent
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|PIPELINE
argument_list|,
name|pipelineConfig
argument_list|)
expr_stmt|;
name|SimulatePipelineRequest
operator|.
name|Parsed
name|actualRequest
init|=
name|SimulatePipelineRequest
operator|.
name|parse
argument_list|(
name|requestContent
argument_list|,
literal|false
argument_list|,
name|store
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actualRequest
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
name|actualRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|expectedDocsIterator
init|=
name|expectedDocs
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|IngestDocument
name|ingestDocument
range|:
name|actualRequest
operator|.
name|getDocuments
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedDocument
init|=
name|expectedDocsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|IngestDocument
operator|.
name|MetaData
argument_list|,
name|String
argument_list|>
name|metadataMap
init|=
name|ingestDocument
operator|.
name|extractMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metadataMap
operator|.
name|get
argument_list|(
name|INDEX
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|INDEX
operator|.
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadataMap
operator|.
name|get
argument_list|(
name|TYPE
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|TYPE
operator|.
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadataMap
operator|.
name|get
argument_list|(
name|ID
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|ID
operator|.
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedDocument
operator|.
name|get
argument_list|(
name|Fields
operator|.
name|SOURCE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|actualRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SimulatePipelineRequest
operator|.
name|SIMULATED_PIPELINE_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getDescription
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualRequest
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProcessors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numProcessors
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

