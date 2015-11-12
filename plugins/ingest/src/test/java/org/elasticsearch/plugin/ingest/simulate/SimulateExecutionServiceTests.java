begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.simulate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
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
name|Data
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
name|processor
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
name|plugin
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
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
DECL|field|store
specifier|private
name|PipelineStore
name|store
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
DECL|field|data
specifier|private
name|Data
name|data
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|store
operator|=
name|mock
argument_list|(
name|PipelineStore
operator|.
name|class
argument_list|)
expr_stmt|;
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
literal|"_name"
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
name|Processor
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
name|Arrays
operator|.
name|asList
argument_list|(
name|processor
argument_list|,
name|processor
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|Data
argument_list|(
literal|"_index"
argument_list|,
literal|"_type"
argument_list|,
literal|"_id"
argument_list|,
name|Collections
operator|.
name|emptyMap
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
name|SimulatedItemResponse
name|expectedItemResponse
init|=
operator|new
name|SimulatedItemResponse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ProcessedData
argument_list|(
literal|"processor[mock]-0"
argument_list|,
name|data
argument_list|)
argument_list|,
operator|new
name|ProcessedData
argument_list|(
literal|"processor[mock]-1"
argument_list|,
name|data
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SimulatedItemResponse
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeVerboseItem
argument_list|(
name|pipeline
argument_list|,
name|data
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
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualItemResponse
argument_list|,
name|equalTo
argument_list|(
name|expectedItemResponse
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteItem_verboseSuccessful
specifier|public
name|void
name|testExecuteItem_verboseSuccessful
parameter_list|()
throws|throws
name|Exception
block|{
name|SimulatedItemResponse
name|expectedItemResponse
init|=
operator|new
name|SimulatedItemResponse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ProcessedData
argument_list|(
literal|"processor[mock]-0"
argument_list|,
name|data
argument_list|)
argument_list|,
operator|new
name|ProcessedData
argument_list|(
literal|"processor[mock]-1"
argument_list|,
name|data
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SimulatedItemResponse
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeItem
argument_list|(
name|pipeline
argument_list|,
name|data
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
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualItemResponse
argument_list|,
name|equalTo
argument_list|(
name|expectedItemResponse
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteItem_Simple
specifier|public
name|void
name|testExecuteItem_Simple
parameter_list|()
throws|throws
name|Exception
block|{
name|SimulatedItemResponse
name|expectedItemResponse
init|=
operator|new
name|SimulatedItemResponse
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|SimulatedItemResponse
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeItem
argument_list|(
name|pipeline
argument_list|,
name|data
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
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualItemResponse
argument_list|,
name|equalTo
argument_list|(
name|expectedItemResponse
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecuteItem_Failure
specifier|public
name|void
name|testExecuteItem_Failure
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
name|SimulatedItemResponse
name|expectedItemResponse
init|=
operator|new
name|SimulatedItemResponse
argument_list|(
name|e
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
name|data
argument_list|)
expr_stmt|;
name|SimulatedItemResponse
name|actualItemResponse
init|=
name|executionService
operator|.
name|executeItem
argument_list|(
name|pipeline
argument_list|,
name|data
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
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualItemResponse
argument_list|,
name|equalTo
argument_list|(
name|expectedItemResponse
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecute
specifier|public
name|void
name|testExecute
parameter_list|()
throws|throws
name|Exception
block|{
name|SimulateExecutionService
operator|.
name|Listener
name|listener
init|=
name|mock
argument_list|(
name|SimulateExecutionService
operator|.
name|Listener
operator|.
name|class
argument_list|)
decl_stmt|;
name|SimulatedItemResponse
name|itemResponse
init|=
operator|new
name|SimulatedItemResponse
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|ParsedSimulateRequest
name|request
init|=
operator|new
name|ParsedSimulateRequest
argument_list|(
name|pipeline
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|data
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|executionService
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|SimulatePipelineResponse
name|response
init|=
operator|new
name|SimulatePipelineResponse
argument_list|(
literal|"_id"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|itemResponse
argument_list|)
argument_list|)
decl_stmt|;
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
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
name|data
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|listener
argument_list|)
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testExecute_Verbose
specifier|public
name|void
name|testExecute_Verbose
parameter_list|()
throws|throws
name|Exception
block|{
name|SimulateExecutionService
operator|.
name|Listener
name|listener
init|=
name|mock
argument_list|(
name|SimulateExecutionService
operator|.
name|Listener
operator|.
name|class
argument_list|)
decl_stmt|;
name|ParsedSimulateRequest
name|request
init|=
operator|new
name|ParsedSimulateRequest
argument_list|(
name|pipeline
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|data
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SimulatedItemResponse
name|itemResponse
init|=
operator|new
name|SimulatedItemResponse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ProcessedData
argument_list|(
literal|"processor[mock]-0"
argument_list|,
name|data
argument_list|)
argument_list|,
operator|new
name|ProcessedData
argument_list|(
literal|"processor[mock]-1"
argument_list|,
name|data
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|executionService
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|SimulatePipelineResponse
name|response
init|=
operator|new
name|SimulatePipelineResponse
argument_list|(
literal|"_id"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|itemResponse
argument_list|)
argument_list|)
decl_stmt|;
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
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
name|data
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|listener
argument_list|)
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

