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
name|ElasticsearchException
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
name|RandomDocumentPicks
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
name|Pipeline
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
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Collections
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
name|ingest
operator|.
name|IngestDocumentMatcher
operator|.
name|assertIngestDocument
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
name|not
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
name|Matchers
operator|.
name|sameInstance
import|;
end_import

begin_class
DECL|class|SimulateExecutionServiceTests
specifier|public
class|class
name|SimulateExecutionServiceTests
extends|extends
name|ESTestCase
block|{
DECL|field|version
specifier|private
specifier|final
name|Integer
name|version
init|=
name|randomBoolean
argument_list|()
condition|?
name|randomInt
argument_list|()
else|:
literal|null
decl_stmt|;
DECL|field|threadPool
specifier|private
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|executionService
specifier|private
name|SimulateExecutionService
name|executionService
decl_stmt|;
DECL|field|ingestDocument
specifier|private
name|IngestDocument
name|ingestDocument
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|threadPool
operator|=
operator|new
name|ThreadPool
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|executionService
operator|=
operator|new
name|SimulateExecutionService
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
name|ingestDocument
operator|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|testExecuteVerboseItem
specifier|public
name|void
name|testExecuteVerboseItem
parameter_list|()
throws|throws
name|Exception
block|{
name|TestProcessor
name|processor
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"test-id"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
name|version
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
name|processor
argument_list|,
name|processor
argument_list|)
argument_list|)
decl_stmt|;
name|SimulateDocumentResult
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeDocument
argument_list|(
name|pipeline
argument_list|,
name|ingestDocument
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getInvokedCounter
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
name|actualItemResponse
argument_list|,
name|instanceOf
argument_list|(
name|SimulateDocumentVerboseResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SimulateDocumentVerboseResult
name|simulateDocumentVerboseResult
init|=
operator|(
name|SimulateDocumentVerboseResult
operator|)
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
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
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test-id"
argument_list|)
argument_list|)
expr_stmt|;
name|IngestDocument
name|firstProcessorIngestDocument
init|=
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|firstProcessorIngestDocument
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|this
operator|.
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|firstProcessorIngestDocument
argument_list|,
name|this
operator|.
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|firstProcessorIngestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|this
operator|.
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test-id"
argument_list|)
argument_list|)
expr_stmt|;
name|IngestDocument
name|secondProcessorIngestDocument
init|=
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|secondProcessorIngestDocument
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|this
operator|.
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|secondProcessorIngestDocument
argument_list|,
name|this
operator|.
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondProcessorIngestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|this
operator|.
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|secondProcessorIngestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|firstProcessorIngestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteItem
specifier|public
name|void
name|testExecuteItem
parameter_list|()
throws|throws
name|Exception
block|{
name|TestProcessor
name|processor
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_0"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
name|version
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
name|processor
argument_list|,
name|processor
argument_list|)
argument_list|)
decl_stmt|;
name|SimulateDocumentResult
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeDocument
argument_list|(
name|pipeline
argument_list|,
name|ingestDocument
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getInvokedCounter
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
name|actualItemResponse
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
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentBaseResult
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ingestDocument
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
DECL|method|testExecuteVerboseItemExceptionWithoutOnFailure
specifier|public
name|void
name|testExecuteVerboseItemExceptionWithoutOnFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|TestProcessor
name|processor1
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_0"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|TestProcessor
name|processor2
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_1"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"processor failed"
argument_list|)
throw|;
block|}
argument_list|)
decl_stmt|;
name|TestProcessor
name|processor3
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_2"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
name|version
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
name|processor1
argument_list|,
name|processor2
argument_list|,
name|processor3
argument_list|)
argument_list|)
decl_stmt|;
name|SimulateDocumentResult
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeDocument
argument_list|(
name|pipeline
argument_list|,
name|ingestDocument
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor1
operator|.
name|getInvokedCounter
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
name|processor2
operator|.
name|getInvokedCounter
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
name|processor3
operator|.
name|getInvokedCounter
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
name|actualItemResponse
argument_list|,
name|instanceOf
argument_list|(
name|SimulateDocumentVerboseResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SimulateDocumentVerboseResult
name|simulateDocumentVerboseResult
init|=
operator|(
name|SimulateDocumentVerboseResult
operator|)
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
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
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor_1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
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
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|RuntimeException
name|runtimeException
init|=
operator|(
name|RuntimeException
operator|)
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getFailure
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|runtimeException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor failed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteVerboseItemWithOnFailure
specifier|public
name|void
name|testExecuteVerboseItemWithOnFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|TestProcessor
name|processor1
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_0"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"processor failed"
argument_list|)
throw|;
block|}
argument_list|)
decl_stmt|;
name|TestProcessor
name|processor2
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_1"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|TestProcessor
name|processor3
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_2"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
name|version
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
operator|new
name|CompoundProcessor
argument_list|(
literal|false
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|processor1
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|processor2
argument_list|)
argument_list|)
argument_list|,
name|processor3
argument_list|)
argument_list|)
decl_stmt|;
name|SimulateDocumentResult
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeDocument
argument_list|(
name|pipeline
argument_list|,
name|ingestDocument
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor1
operator|.
name|getInvokedCounter
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
name|processor2
operator|.
name|getInvokedCounter
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
name|actualItemResponse
argument_list|,
name|instanceOf
argument_list|(
name|SimulateDocumentVerboseResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SimulateDocumentVerboseResult
name|simulateDocumentVerboseResult
init|=
operator|(
name|SimulateDocumentVerboseResult
operator|)
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
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
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|RuntimeException
name|runtimeException
init|=
operator|(
name|RuntimeException
operator|)
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFailure
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|runtimeException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor failed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor_1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|IngestDocument
name|ingestDocumentWithOnFailureMetadata
init|=
operator|new
name|IngestDocument
argument_list|(
name|ingestDocument
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metadata
init|=
name|ingestDocumentWithOnFailureMetadata
operator|.
name|getIngestMetadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|CompoundProcessor
operator|.
name|ON_FAILURE_PROCESSOR_TYPE_FIELD
argument_list|,
literal|"mock"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|CompoundProcessor
operator|.
name|ON_FAILURE_PROCESSOR_TAG_FIELD
argument_list|,
literal|"processor_0"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|CompoundProcessor
operator|.
name|ON_FAILURE_MESSAGE_FIELD
argument_list|,
literal|"processor failed"
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|ingestDocumentWithOnFailureMetadata
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor_2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteVerboseItemExceptionWithIgnoreFailure
specifier|public
name|void
name|testExecuteVerboseItemExceptionWithIgnoreFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|RuntimeException
name|exception
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"processor failed"
argument_list|)
decl_stmt|;
name|TestProcessor
name|testProcessor
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_0"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{
throw|throw
name|exception
throw|;
block|}
argument_list|)
decl_stmt|;
name|CompoundProcessor
name|processor
init|=
operator|new
name|CompoundProcessor
argument_list|(
literal|true
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|testProcessor
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
name|version
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
name|processor
argument_list|)
argument_list|)
decl_stmt|;
name|SimulateDocumentResult
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeDocument
argument_list|(
name|pipeline
argument_list|,
name|ingestDocument
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testProcessor
operator|.
name|getInvokedCounter
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
name|actualItemResponse
argument_list|,
name|instanceOf
argument_list|(
name|SimulateDocumentVerboseResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SimulateDocumentVerboseResult
name|simulateDocumentVerboseResult
init|=
operator|(
name|SimulateDocumentVerboseResult
operator|)
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
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
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|sameInstance
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteVerboseItemWithoutExceptionAndWithIgnoreFailure
specifier|public
name|void
name|testExecuteVerboseItemWithoutExceptionAndWithIgnoreFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|TestProcessor
name|testProcessor
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"processor_0"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{ }
argument_list|)
decl_stmt|;
name|CompoundProcessor
name|processor
init|=
operator|new
name|CompoundProcessor
argument_list|(
literal|true
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|testProcessor
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
name|version
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
name|processor
argument_list|)
argument_list|)
decl_stmt|;
name|SimulateDocumentResult
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeDocument
argument_list|(
name|pipeline
argument_list|,
name|ingestDocument
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testProcessor
operator|.
name|getInvokedCounter
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
name|actualItemResponse
argument_list|,
name|instanceOf
argument_list|(
name|SimulateDocumentVerboseResult
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SimulateDocumentVerboseResult
name|simulateDocumentVerboseResult
init|=
operator|(
name|SimulateDocumentVerboseResult
operator|)
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
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
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor_0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertIngestDocument
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentVerboseResult
operator|.
name|getProcessorResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|,
name|not
argument_list|(
name|sameInstance
argument_list|(
name|ingestDocument
operator|.
name|getSourceAndMetadata
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteItemWithFailure
specifier|public
name|void
name|testExecuteItemWithFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|TestProcessor
name|processor
init|=
operator|new
name|TestProcessor
argument_list|(
name|ingestDocument
lambda|->
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"processor failed"
argument_list|)
throw|;
block|}
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
name|version
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
name|processor
argument_list|,
name|processor
argument_list|)
argument_list|)
decl_stmt|;
name|SimulateDocumentResult
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeDocument
argument_list|(
name|pipeline
argument_list|,
name|ingestDocument
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processor
operator|.
name|getInvokedCounter
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
name|actualItemResponse
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
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentBaseResult
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
name|simulateDocumentBaseResult
operator|.
name|getFailure
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Exception
name|exception
init|=
name|simulateDocumentBaseResult
operator|.
name|getFailure
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|instanceOf
argument_list|(
name|ElasticsearchException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"java.lang.IllegalArgumentException: java.lang.RuntimeException: processor failed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

