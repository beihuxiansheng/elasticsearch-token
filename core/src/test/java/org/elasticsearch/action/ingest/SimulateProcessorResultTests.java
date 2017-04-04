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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|is
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

begin_class
DECL|class|SimulateProcessorResultTests
specifier|public
class|class
name|SimulateProcessorResultTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|processorTag
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|boolean
name|isSuccessful
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|isIgnoredException
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|SimulateProcessorResult
name|simulateProcessorResult
decl_stmt|;
if|if
condition|(
name|isSuccessful
condition|)
block|{
name|IngestDocument
name|ingestDocument
init|=
name|RandomDocumentPicks
operator|.
name|randomIngestDocument
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isIgnoredException
condition|)
block|{
name|simulateProcessorResult
operator|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|processorTag
argument_list|,
name|ingestDocument
argument_list|,
operator|new
name|IllegalArgumentException
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|simulateProcessorResult
operator|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|processorTag
argument_list|,
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|simulateProcessorResult
operator|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|processorTag
argument_list|,
operator|new
name|IllegalArgumentException
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|simulateProcessorResult
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|StreamInput
name|streamInput
init|=
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
decl_stmt|;
name|SimulateProcessorResult
name|otherSimulateProcessorResult
init|=
operator|new
name|SimulateProcessorResult
argument_list|(
name|streamInput
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|otherSimulateProcessorResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|simulateProcessorResult
operator|.
name|getProcessorTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSuccessful
condition|)
block|{
name|assertIngestDocument
argument_list|(
name|otherSimulateProcessorResult
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|simulateProcessorResult
operator|.
name|getIngestDocument
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isIgnoredException
condition|)
block|{
name|assertThat
argument_list|(
name|otherSimulateProcessorResult
operator|.
name|getFailure
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|e
init|=
operator|(
name|IllegalArgumentException
operator|)
name|otherSimulateProcessorResult
operator|.
name|getFailure
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|otherSimulateProcessorResult
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertThat
argument_list|(
name|otherSimulateProcessorResult
operator|.
name|getIngestDocument
argument_list|()
argument_list|,
name|is
argument_list|(
name|nullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|otherSimulateProcessorResult
operator|.
name|getFailure
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|e
init|=
operator|(
name|IllegalArgumentException
operator|)
name|otherSimulateProcessorResult
operator|.
name|getFailure
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

