begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.fieldcaps
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|fieldcaps
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

begin_class
DECL|class|FieldCapabilitiesRequestTests
specifier|public
class|class
name|FieldCapabilitiesRequestTests
extends|extends
name|ESTestCase
block|{
DECL|method|randomRequest
specifier|private
name|FieldCapabilitiesRequest
name|randomRequest
parameter_list|()
block|{
name|FieldCapabilitiesRequest
name|request
init|=
operator|new
name|FieldCapabilitiesRequest
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|String
index|[]
name|randomFields
init|=
operator|new
name|String
index|[
name|size
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|randomFields
index|[
name|i
index|]
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|fields
argument_list|(
name|randomFields
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|testFieldCapsRequestSerialization
specifier|public
name|void
name|testFieldCapsRequestSerialization
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|FieldCapabilitiesRequest
name|request
init|=
name|randomRequest
argument_list|()
decl_stmt|;
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|request
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
name|StreamInput
name|input
init|=
name|output
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
decl_stmt|;
name|FieldCapabilitiesRequest
name|deserialized
init|=
operator|new
name|FieldCapabilitiesRequest
argument_list|()
decl_stmt|;
name|deserialized
operator|.
name|readFrom
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserialized
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|request
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

