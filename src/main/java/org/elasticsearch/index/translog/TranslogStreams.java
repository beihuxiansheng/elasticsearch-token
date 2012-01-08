begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
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
name|BytesStreamInput
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
DECL|class|TranslogStreams
specifier|public
class|class
name|TranslogStreams
block|{
DECL|method|readTranslogOperation
specifier|public
specifier|static
name|Translog
operator|.
name|Operation
name|readTranslogOperation
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Translog
operator|.
name|Operation
operator|.
name|Type
name|type
init|=
name|Translog
operator|.
name|Operation
operator|.
name|Type
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
decl_stmt|;
name|Translog
operator|.
name|Operation
name|operation
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|CREATE
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|Create
argument_list|()
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|Delete
argument_list|()
expr_stmt|;
break|break;
case|case
name|DELETE_BY_QUERY
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|DeleteByQuery
argument_list|()
expr_stmt|;
break|break;
case|case
name|SAVE
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|Index
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No type for ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|operation
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|operation
return|;
block|}
DECL|method|readSource
specifier|public
specifier|static
name|Translog
operator|.
name|Source
name|readSource
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesStreamInput
name|in
init|=
operator|new
name|BytesStreamInput
argument_list|(
name|data
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// the size header
name|Translog
operator|.
name|Operation
operator|.
name|Type
name|type
init|=
name|Translog
operator|.
name|Operation
operator|.
name|Type
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
decl_stmt|;
name|Translog
operator|.
name|Operation
name|operation
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|CREATE
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|Create
argument_list|()
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|Delete
argument_list|()
expr_stmt|;
break|break;
case|case
name|DELETE_BY_QUERY
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|DeleteByQuery
argument_list|()
expr_stmt|;
break|break;
case|case
name|SAVE
case|:
name|operation
operator|=
operator|new
name|Translog
operator|.
name|Index
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No type for ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|operation
operator|.
name|readSource
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|method|writeTranslogOperation
specifier|public
specifier|static
name|void
name|writeTranslogOperation
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|Translog
operator|.
name|Operation
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|op
operator|.
name|opType
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|op
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

