begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
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
name|xcontent
operator|.
name|XContentGenerator
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractXContentGenerator
specifier|public
specifier|abstract
class|class
name|AbstractXContentGenerator
implements|implements
name|XContentGenerator
block|{
annotation|@
name|Override
DECL|method|writeStringField
specifier|public
name|void
name|writeStringField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBooleanField
specifier|public
name|void
name|writeBooleanField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNullField
specifier|public
name|void
name|writeNullField
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumberField
specifier|public
name|void
name|writeNumberField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBinaryField
specifier|public
name|void
name|writeBinaryField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeBinary
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayFieldStart
specifier|public
name|void
name|writeArrayFieldStart
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeStartArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeObjectFieldStart
specifier|public
name|void
name|writeObjectFieldStart
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeStartObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

