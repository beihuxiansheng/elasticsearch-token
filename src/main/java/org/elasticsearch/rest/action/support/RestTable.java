begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
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
name|Booleans
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
name|Strings
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
name|Table
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
operator|.
name|SizeValue
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
name|unit
operator|.
name|TimeValue
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|RestTable
specifier|public
class|class
name|RestTable
block|{
DECL|method|buildResponse
specifier|public
specifier|static
name|RestResponse
name|buildResponse
parameter_list|(
name|Table
name|table
parameter_list|,
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentType
name|xContentType
init|=
name|XContentType
operator|.
name|fromRestContentType
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"format"
argument_list|,
name|request
operator|.
name|header
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|xContentType
operator|!=
literal|null
condition|)
block|{
return|return
name|buildXContentBuilder
argument_list|(
name|table
argument_list|,
name|request
argument_list|,
name|channel
argument_list|)
return|;
block|}
return|return
name|buildTextPlainResponse
argument_list|(
name|table
argument_list|,
name|request
argument_list|,
name|channel
argument_list|)
return|;
block|}
DECL|method|buildXContentBuilder
specifier|public
specifier|static
name|RestResponse
name|buildXContentBuilder
parameter_list|(
name|Table
name|table
parameter_list|,
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentBuilder
name|builder
init|=
name|RestXContentBuilder
operator|.
name|restContentBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|displayHeaders
init|=
name|buildDisplayHeaders
argument_list|(
name|table
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|headers
init|=
name|table
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startArray
argument_list|()
expr_stmt|;
for|for
control|(
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|row
range|:
name|table
operator|.
name|getRows
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|headers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|headerName
init|=
name|headers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|displayHeaders
operator|.
name|contains
argument_list|(
name|headerName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|headerName
argument_list|,
name|renderValue
argument_list|(
name|request
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
operator|new
name|XContentRestResponse
argument_list|(
name|request
argument_list|,
name|RestStatus
operator|.
name|OK
argument_list|,
name|builder
argument_list|)
return|;
block|}
DECL|method|buildTextPlainResponse
specifier|public
specifier|static
name|RestResponse
name|buildTextPlainResponse
parameter_list|(
name|Table
name|table
parameter_list|,
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
block|{
name|boolean
name|verbose
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"v"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
index|[]
name|width
init|=
name|buildWidths
argument_list|(
name|table
argument_list|,
name|request
argument_list|,
name|verbose
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|displayHeaders
init|=
name|buildDisplayHeaders
argument_list|(
name|table
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
comment|// print the headers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|width
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|headerName
init|=
name|table
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|displayHeaders
operator|.
name|contains
argument_list|(
name|headerName
argument_list|)
condition|)
block|{
name|pad
argument_list|(
name|table
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|width
index|[
name|i
index|]
argument_list|,
name|request
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|row
range|:
name|table
operator|.
name|getRows
argument_list|()
control|)
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
name|width
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|headerName
init|=
name|table
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|displayHeaders
operator|.
name|contains
argument_list|(
name|headerName
argument_list|)
condition|)
block|{
name|pad
argument_list|(
name|row
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|width
index|[
name|i
index|]
argument_list|,
name|request
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StringRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|buildDisplayHeaders
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|buildDisplayHeaders
parameter_list|(
name|Table
name|table
parameter_list|,
name|RestRequest
name|request
parameter_list|)
block|{
name|String
name|pHeaders
init|=
name|request
operator|.
name|param
argument_list|(
literal|"headers"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|display
decl_stmt|;
if|if
condition|(
name|pHeaders
operator|!=
literal|null
condition|)
block|{
name|display
operator|=
name|Strings
operator|.
name|commaDelimitedListToSet
argument_list|(
name|pHeaders
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|display
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Table
operator|.
name|Cell
name|cell
range|:
name|table
operator|.
name|getHeaders
argument_list|()
control|)
block|{
name|String
name|d
init|=
name|cell
operator|.
name|attr
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|d
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|display
operator|.
name|add
argument_list|(
name|cell
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|display
return|;
block|}
DECL|method|buildWidths
specifier|private
specifier|static
name|int
index|[]
name|buildWidths
parameter_list|(
name|Table
name|table
parameter_list|,
name|RestRequest
name|request
parameter_list|,
name|boolean
name|verbose
parameter_list|)
block|{
name|int
index|[]
name|width
init|=
operator|new
name|int
index|[
name|table
operator|.
name|getHeaders
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
for|for
control|(
name|int
name|col
init|=
literal|0
init|;
name|col
operator|<
name|width
operator|.
name|length
condition|;
name|col
operator|++
control|)
block|{
name|String
name|v
init|=
name|renderValue
argument_list|(
name|request
argument_list|,
name|table
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
name|col
argument_list|)
operator|.
name|value
argument_list|)
decl_stmt|;
name|int
name|vWidth
init|=
name|v
operator|==
literal|null
condition|?
literal|0
else|:
name|v
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|width
index|[
name|col
index|]
operator|<
name|vWidth
condition|)
block|{
name|width
index|[
name|col
index|]
operator|=
name|vWidth
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|row
range|:
name|table
operator|.
name|getRows
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|col
init|=
literal|0
init|;
name|col
operator|<
name|width
operator|.
name|length
condition|;
name|col
operator|++
control|)
block|{
name|String
name|v
init|=
name|renderValue
argument_list|(
name|request
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|col
argument_list|)
operator|.
name|value
argument_list|)
decl_stmt|;
name|int
name|vWidth
init|=
name|v
operator|==
literal|null
condition|?
literal|0
else|:
name|v
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|width
index|[
name|col
index|]
operator|<
name|vWidth
condition|)
block|{
name|width
index|[
name|col
index|]
operator|=
name|vWidth
expr_stmt|;
block|}
block|}
block|}
return|return
name|width
return|;
block|}
DECL|method|pad
specifier|private
specifier|static
name|void
name|pad
parameter_list|(
name|Table
operator|.
name|Cell
name|cell
parameter_list|,
name|int
name|width
parameter_list|,
name|RestRequest
name|request
parameter_list|,
name|StringBuilder
name|out
parameter_list|)
block|{
name|String
name|sValue
init|=
name|renderValue
argument_list|(
name|request
argument_list|,
name|cell
operator|.
name|value
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|sValue
operator|==
literal|null
condition|?
literal|0
else|:
name|sValue
operator|.
name|length
argument_list|()
decl_stmt|;
name|byte
name|leftOver
init|=
call|(
name|byte
call|)
argument_list|(
name|width
operator|-
name|length
argument_list|)
decl_stmt|;
name|String
name|textAlign
init|=
name|cell
operator|.
name|attr
operator|.
name|get
argument_list|(
literal|"text-align"
argument_list|)
decl_stmt|;
if|if
condition|(
name|textAlign
operator|==
literal|null
condition|)
block|{
name|textAlign
operator|=
literal|"left"
expr_stmt|;
block|}
if|if
condition|(
name|leftOver
operator|>
literal|0
operator|&&
name|textAlign
operator|.
name|equals
argument_list|(
literal|"right"
argument_list|)
condition|)
block|{
for|for
control|(
name|byte
name|i
init|=
literal|0
init|;
name|i
operator|<
name|leftOver
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sValue
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|sValue
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|sValue
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|sValue
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|byte
name|i
init|=
literal|0
init|;
name|i
operator|<
name|leftOver
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|renderValue
specifier|private
specifier|static
name|String
name|renderValue
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|ByteSizeValue
condition|)
block|{
name|ByteSizeValue
name|v
init|=
operator|(
name|ByteSizeValue
operator|)
name|value
decl_stmt|;
name|String
name|resolution
init|=
name|request
operator|.
name|param
argument_list|(
literal|"bytes"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"b"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"k"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|kb
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"m"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|mb
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"g"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|gb
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|v
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
if|if
condition|(
name|value
operator|instanceof
name|SizeValue
condition|)
block|{
name|SizeValue
name|v
init|=
operator|(
name|SizeValue
operator|)
name|value
decl_stmt|;
name|String
name|resolution
init|=
name|request
operator|.
name|param
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"b"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|singles
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"k"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|kilo
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"m"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|mega
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"g"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|giga
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|v
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
if|if
condition|(
name|value
operator|instanceof
name|TimeValue
condition|)
block|{
name|TimeValue
name|v
init|=
operator|(
name|TimeValue
operator|)
name|value
decl_stmt|;
name|String
name|resolution
init|=
name|request
operator|.
name|param
argument_list|(
literal|"time"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"ms"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|millis
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"s"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|seconds
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"m"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|minutes
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"h"
operator|.
name|equals
argument_list|(
name|resolution
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|v
operator|.
name|hours
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|v
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|// Add additional built in data points we can render based on request parameters?
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

