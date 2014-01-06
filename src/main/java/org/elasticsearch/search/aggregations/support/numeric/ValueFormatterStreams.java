begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support.numeric
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|numeric
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
DECL|class|ValueFormatterStreams
specifier|public
class|class
name|ValueFormatterStreams
block|{
DECL|method|read
specifier|public
specifier|static
name|ValueFormatter
name|read
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|id
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|ValueFormatter
name|formatter
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|id
condition|)
block|{
case|case
name|ValueFormatter
operator|.
name|Raw
operator|.
name|ID
case|:
return|return
name|ValueFormatter
operator|.
name|RAW
return|;
case|case
name|ValueFormatter
operator|.
name|IPv4Formatter
operator|.
name|ID
case|:
return|return
name|ValueFormatter
operator|.
name|IPv4
return|;
case|case
name|ValueFormatter
operator|.
name|DateTime
operator|.
name|ID
case|:
name|formatter
operator|=
operator|new
name|ValueFormatter
operator|.
name|DateTime
argument_list|()
expr_stmt|;
break|break;
case|case
name|ValueFormatter
operator|.
name|Number
operator|.
name|Pattern
operator|.
name|ID
case|:
name|formatter
operator|=
operator|new
name|ValueFormatter
operator|.
name|Number
operator|.
name|Pattern
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Unknown value formatter with id ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|formatter
return|;
block|}
DECL|method|readOptional
specifier|public
specifier|static
name|ValueFormatter
name|readOptional
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|read
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|method|write
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|ValueFormatter
name|formatter
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|formatter
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|writeOptional
specifier|public
specifier|static
name|void
name|writeOptional
parameter_list|(
name|ValueFormatter
name|formatter
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
name|formatter
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|formatter
operator|!=
literal|null
condition|)
block|{
name|write
argument_list|(
name|formatter
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

