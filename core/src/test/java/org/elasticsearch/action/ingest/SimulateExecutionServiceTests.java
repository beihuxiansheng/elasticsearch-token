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
name|Processor
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
name|ArrayList
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
name|Collections
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
DECL|field|pipeline
specifier|private
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|processor
specifier|private
name|Processor
name|processor
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
literal|"name"
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
name|processor
operator|=
operator|new
name|TestProcessor
argument_list|(
literal|"id"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
expr_stmt|;
name|pipeline
operator|=
operator|new
name|Pipeline
argument_list|(
literal|"_id"
argument_list|,
literal|"_description"
argument_list|,
operator|new
name|CompoundProcessor
argument_list|(
name|processor
argument_list|,
name|processor
argument_list|)
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
DECL|method|testExecuteVerboseDocumentSimple
specifier|public
name|void
name|testExecuteVerboseDocumentSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|processorResultList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|executionService
operator|.
name|executeVerboseDocument
argument_list|(
name|processor
argument_list|,
name|ingestDocument
argument_list|,
name|processorResultList
argument_list|)
expr_stmt|;
name|SimulateProcessorResult
name|result
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
literal|"id"
argument_list|,
name|ingestDocument
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processorResultList
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
name|processorResultList
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
name|result
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|result
operator|.
name|getIngestDocument
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
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
block|}
DECL|method|testExecuteVerboseDocumentSimpleException
specifier|public
name|void
name|testExecuteVerboseDocumentSimpleException
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
literal|"mock_exception"
argument_list|)
decl_stmt|;
name|TestProcessor
name|processor
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"id"
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
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|processorResultList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|executionService
operator|.
name|executeVerboseDocument
argument_list|(
name|processor
argument_list|,
name|ingestDocument
argument_list|,
name|processorResultList
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should throw exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"mock_exception"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SimulateProcessorResult
name|result
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
literal|"id"
argument_list|,
name|exception
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|processorResultList
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
name|processorResultList
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
name|result
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|result
operator|.
name|getFailure
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteVerboseDocumentCompoundSuccess
specifier|public
name|void
name|testExecuteVerboseDocumentCompoundSuccess
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
literal|"p1"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{ }
argument_list|)
decl_stmt|;
name|TestProcessor
name|processor2
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"p2"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{ }
argument_list|)
decl_stmt|;
name|Processor
name|compoundProcessor
init|=
operator|new
name|CompoundProcessor
argument_list|(
name|processor1
argument_list|,
name|processor2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|processorResultList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|executionService
operator|.
name|executeVerboseDocument
argument_list|(
name|compoundProcessor
argument_list|,
name|ingestDocument
argument_list|,
name|processorResultList
argument_list|)
expr_stmt|;
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
name|processorResultList
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
name|processorResultList
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
literal|"p1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|processorResultList
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
name|processorResultList
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
literal|"p2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
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
name|processorResultList
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
DECL|method|testExecuteVerboseDocumentCompoundOnFailure
specifier|public
name|void
name|testExecuteVerboseDocumentCompoundOnFailure
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
literal|"p1"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{ }
argument_list|)
decl_stmt|;
name|TestProcessor
name|processor2
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"p2"
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
literal|"p2_exception"
argument_list|)
throw|;
block|}
argument_list|)
decl_stmt|;
name|TestProcessor
name|onFailureProcessor1
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"fail_p1"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{ }
argument_list|)
decl_stmt|;
name|TestProcessor
name|onFailureProcessor2
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"fail_p2"
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
literal|"fail_p2_exception"
argument_list|)
throw|;
block|}
argument_list|)
decl_stmt|;
name|TestProcessor
name|onFailureProcessor3
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"fail_p3"
argument_list|,
literal|"mock"
argument_list|,
name|ingestDocument
lambda|->
block|{ }
argument_list|)
decl_stmt|;
name|CompoundProcessor
name|onFailureCompoundProcessor
init|=
operator|new
name|CompoundProcessor
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|onFailureProcessor2
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|onFailureProcessor3
argument_list|)
argument_list|)
decl_stmt|;
name|Processor
name|compoundProcessor
init|=
operator|new
name|CompoundProcessor
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|processor1
argument_list|,
name|processor2
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|onFailureProcessor1
argument_list|,
name|onFailureCompoundProcessor
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|processorResultList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|executionService
operator|.
name|executeVerboseDocument
argument_list|(
name|compoundProcessor
argument_list|,
name|ingestDocument
argument_list|,
name|processorResultList
argument_list|)
expr_stmt|;
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
name|onFailureProcessor1
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
name|onFailureProcessor2
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
name|onFailureProcessor3
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
name|processorResultList
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
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
literal|"p1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
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
literal|"p2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
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
literal|"fail_p1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"fail_p2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|processorResultList
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"fail_p3"
argument_list|)
argument_list|)
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
name|equalTo
argument_list|(
name|ingestDocument
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
name|equalTo
argument_list|(
name|ingestDocument
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
DECL|method|testExecuteVerboseItemWithFailure
specifier|public
name|void
name|testExecuteVerboseItemWithFailure
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
operator|new
name|CompoundProcessor
argument_list|(
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
name|equalTo
argument_list|(
name|ingestDocument
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
name|RuntimeException
name|runtimeException
init|=
operator|(
name|RuntimeException
operator|)
name|simulateDocumentBaseResult
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
block|}
end_class

end_unit

