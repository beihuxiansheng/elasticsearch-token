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
name|HashMap
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
name|ingest
operator|.
name|CompoundProcessor
operator|.
name|ON_FAILURE_MESSAGE_FIELD
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
name|CompoundProcessor
operator|.
name|ON_FAILURE_PROCESSOR_TAG_FIELD
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
name|CompoundProcessor
operator|.
name|ON_FAILURE_PROCESSOR_TYPE_FIELD
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
name|TrackingResultProcessor
operator|.
name|decorate
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|sameInstance
import|;
end_import

begin_class
DECL|class|TrackingResultProcessorTests
specifier|public
class|class
name|TrackingResultProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|field|ingestDocument
specifier|private
name|IngestDocument
name|ingestDocument
decl_stmt|;
DECL|field|resultList
specifier|private
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|resultList
decl_stmt|;
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
block|{
name|ingestDocument
operator|=
operator|new
name|IngestDocument
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|resultList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|testActualProcessor
specifier|public
name|void
name|testActualProcessor
parameter_list|()
throws|throws
name|Exception
block|{
name|TestProcessor
name|actualProcessor
init|=
operator|new
name|TestProcessor
argument_list|(
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|TrackingResultProcessor
name|trackingProcessor
init|=
operator|new
name|TrackingResultProcessor
argument_list|(
literal|false
argument_list|,
name|actualProcessor
argument_list|,
name|resultList
argument_list|)
decl_stmt|;
name|trackingProcessor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|SimulateProcessorResult
name|expectedResult
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|actualProcessor
operator|.
name|getTag
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actualProcessor
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
name|resultList
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
name|resultList
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
name|expectedResult
operator|.
name|getIngestDocument
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
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
name|resultList
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
name|expectedResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testActualCompoundProcessorWithoutOnFailure
specifier|public
name|void
name|testActualCompoundProcessorWithoutOnFailure
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
name|actualProcessor
init|=
operator|new
name|CompoundProcessor
argument_list|(
name|testProcessor
argument_list|)
decl_stmt|;
name|CompoundProcessor
name|trackingProcessor
init|=
name|decorate
argument_list|(
name|actualProcessor
argument_list|,
name|resultList
argument_list|)
decl_stmt|;
try|try
block|{
name|trackingProcessor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"processor should throw exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getRootCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SimulateProcessorResult
name|expectedFirstResult
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|testProcessor
operator|.
name|getTag
argument_list|()
argument_list|,
name|ingestDocument
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
name|resultList
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
name|resultList
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
name|resultList
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
name|exception
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
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
name|expectedFirstResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testActualCompoundProcessorWithOnFailure
specifier|public
name|void
name|testActualCompoundProcessorWithOnFailure
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
literal|"fail"
argument_list|)
decl_stmt|;
name|TestProcessor
name|failProcessor
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"fail"
argument_list|,
literal|"test"
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
name|TestProcessor
name|onFailureProcessor
init|=
operator|new
name|TestProcessor
argument_list|(
literal|"success"
argument_list|,
literal|"test"
argument_list|,
name|ingestDocument
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|CompoundProcessor
name|actualProcessor
init|=
operator|new
name|CompoundProcessor
argument_list|(
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|CompoundProcessor
argument_list|(
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|failProcessor
argument_list|,
name|onFailureProcessor
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|onFailureProcessor
argument_list|,
name|failProcessor
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|onFailureProcessor
argument_list|)
argument_list|)
decl_stmt|;
name|CompoundProcessor
name|trackingProcessor
init|=
name|decorate
argument_list|(
name|actualProcessor
argument_list|,
name|resultList
argument_list|)
decl_stmt|;
name|trackingProcessor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|SimulateProcessorResult
name|expectedFailResult
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|failProcessor
operator|.
name|getTag
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
decl_stmt|;
name|SimulateProcessorResult
name|expectedSuccessResult
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|onFailureProcessor
operator|.
name|getTag
argument_list|()
argument_list|,
name|ingestDocument
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|failProcessor
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
name|onFailureProcessor
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
name|resultList
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
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
name|resultList
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
name|exception
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
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
name|expectedFailResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
name|resultList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
operator|.
name|getIngestMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ON_FAILURE_MESSAGE_FIELD
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"fail"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ON_FAILURE_PROCESSOR_TYPE_FIELD
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ON_FAILURE_PROCESSOR_TAG_FIELD
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"fail"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
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
name|resultList
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
name|expectedSuccessResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
operator|.
name|get
argument_list|(
literal|2
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
name|resultList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getFailure
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
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
name|expectedFailResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|=
name|resultList
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getIngestDocument
argument_list|()
operator|.
name|getIngestMetadata
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ON_FAILURE_MESSAGE_FIELD
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"fail"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ON_FAILURE_PROCESSOR_TYPE_FIELD
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ON_FAILURE_PROCESSOR_TAG_FIELD
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"fail"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
operator|.
name|get
argument_list|(
literal|3
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
name|resultList
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
name|expectedSuccessResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testActualCompoundProcessorWithIgnoreFailure
specifier|public
name|void
name|testActualCompoundProcessorWithIgnoreFailure
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
name|actualProcessor
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
name|CompoundProcessor
name|trackingProcessor
init|=
name|decorate
argument_list|(
name|actualProcessor
argument_list|,
name|resultList
argument_list|)
decl_stmt|;
name|trackingProcessor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
name|SimulateProcessorResult
name|expectedResult
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|testProcessor
operator|.
name|getTag
argument_list|()
argument_list|,
name|ingestDocument
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
name|resultList
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
name|resultList
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
name|expectedResult
operator|.
name|getIngestDocument
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultList
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
name|resultList
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
name|expectedResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

