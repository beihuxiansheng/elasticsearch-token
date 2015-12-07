begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.transport.simulate
package|package
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
name|processor
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
name|Arrays
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
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
name|times
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
name|verify
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
name|CompoundProcessor
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
name|mock
argument_list|(
name|CompoundProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|processor
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"mock"
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
DECL|method|testExecuteVerboseItem
specifier|public
name|void
name|testExecuteVerboseItem
parameter_list|()
throws|throws
name|Exception
block|{
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
name|verify
argument_list|(
name|processor
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|(
name|ingestDocument
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
name|getProcessorId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor[mock]-0"
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
name|getProcessorId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor[mock]-1"
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
name|verify
argument_list|(
name|processor
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualItemResponse
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
name|actualItemResponse
decl_stmt|;
name|assertThat
argument_list|(
name|simulateDocumentSimpleResult
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
DECL|method|testExecuteVerboseItemWithFailure
specifier|public
name|void
name|testExecuteVerboseItemWithFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|Exception
name|e
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"processor failed"
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
name|e
argument_list|)
operator|.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
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
name|verify
argument_list|(
name|processor
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|(
name|ingestDocument
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
name|getProcessorId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor[mock]-0"
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
name|getProcessorId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"processor[mock]-1"
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
name|runtimeException
operator|=
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
expr_stmt|;
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
DECL|method|testExecuteItemWithFailure
specifier|public
name|void
name|testExecuteItemWithFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|Exception
name|e
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"processor failed"
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
name|e
argument_list|)
operator|.
name|when
argument_list|(
name|processor
argument_list|)
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
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
name|verify
argument_list|(
name|processor
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualItemResponse
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
name|actualItemResponse
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
name|simulateDocumentSimpleResult
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

