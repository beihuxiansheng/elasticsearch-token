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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|WritePipelineResponseTests
specifier|public
class|class
name|WritePipelineResponseTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSerializationWithoutError
specifier|public
name|void
name|testSerializationWithoutError
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|isAcknowledged
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|WritePipelineResponse
name|response
decl_stmt|;
name|response
operator|=
operator|new
name|WritePipelineResponse
argument_list|(
name|isAcknowledged
argument_list|)
expr_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|response
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
name|WritePipelineResponse
name|otherResponse
init|=
operator|new
name|WritePipelineResponse
argument_list|()
decl_stmt|;
name|otherResponse
operator|.
name|readFrom
argument_list|(
name|streamInput
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|otherResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSerializationWithError
specifier|public
name|void
name|testSerializationWithError
parameter_list|()
throws|throws
name|IOException
block|{
name|WritePipelineResponse
name|response
init|=
operator|new
name|WritePipelineResponse
argument_list|()
decl_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|response
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
name|WritePipelineResponse
name|otherResponse
init|=
operator|new
name|WritePipelineResponse
argument_list|()
decl_stmt|;
name|otherResponse
operator|.
name|readFrom
argument_list|(
name|streamInput
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|otherResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

