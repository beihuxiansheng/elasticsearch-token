begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ArrayList
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
name|List
argument_list|<
name|DisplayHeader
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
name|builder
operator|.
name|startArray
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|table
operator|.
name|getRows
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|row
operator|++
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
for|for
control|(
name|DisplayHeader
name|header
range|:
name|displayHeaders
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|header
operator|.
name|display
argument_list|,
name|renderValue
argument_list|(
name|request
argument_list|,
name|table
operator|.
name|getAsMap
argument_list|()
operator|.
name|get
argument_list|(
name|header
operator|.
name|name
argument_list|)
operator|.
name|get
argument_list|(
name|row
argument_list|)
operator|.
name|value
argument_list|)
argument_list|)
expr_stmt|;
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
literal|false
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DisplayHeader
argument_list|>
name|headers
init|=
name|buildDisplayHeaders
argument_list|(
name|table
argument_list|,
name|request
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
argument_list|,
name|headers
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
for|for
control|(
name|int
name|col
init|=
literal|0
init|;
name|col
operator|<
name|headers
operator|.
name|size
argument_list|()
condition|;
name|col
operator|++
control|)
block|{
name|DisplayHeader
name|header
init|=
name|headers
operator|.
name|get
argument_list|(
name|col
argument_list|)
decl_stmt|;
name|pad
argument_list|(
operator|new
name|Table
operator|.
name|Cell
argument_list|(
name|header
operator|.
name|display
argument_list|,
name|table
operator|.
name|findHeaderByName
argument_list|(
name|header
operator|.
name|name
argument_list|)
argument_list|)
argument_list|,
name|width
index|[
name|col
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
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|table
operator|.
name|getRows
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|row
operator|++
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
name|headers
operator|.
name|size
argument_list|()
condition|;
name|col
operator|++
control|)
block|{
name|DisplayHeader
name|header
init|=
name|headers
operator|.
name|get
argument_list|(
name|col
argument_list|)
decl_stmt|;
name|pad
argument_list|(
name|table
operator|.
name|getAsMap
argument_list|()
operator|.
name|get
argument_list|(
name|header
operator|.
name|name
argument_list|)
operator|.
name|get
argument_list|(
name|row
argument_list|)
argument_list|,
name|width
index|[
name|col
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
name|List
argument_list|<
name|DisplayHeader
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
literal|"h"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DisplayHeader
argument_list|>
name|display
init|=
operator|new
name|ArrayList
argument_list|<
name|DisplayHeader
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|pHeaders
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|possibility
range|:
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|pHeaders
argument_list|)
control|)
block|{
if|if
condition|(
name|table
operator|.
name|getAsMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|possibility
argument_list|)
condition|)
block|{
name|display
operator|.
name|add
argument_list|(
operator|new
name|DisplayHeader
argument_list|(
name|possibility
argument_list|,
name|possibility
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Table
operator|.
name|Cell
name|headerCell
range|:
name|table
operator|.
name|getHeaders
argument_list|()
control|)
block|{
name|String
name|aliases
init|=
name|headerCell
operator|.
name|attr
operator|.
name|get
argument_list|(
literal|"alias"
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliases
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|alias
range|:
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|aliases
argument_list|)
control|)
block|{
if|if
condition|(
name|possibility
operator|.
name|equals
argument_list|(
name|alias
argument_list|)
condition|)
block|{
name|display
operator|.
name|add
argument_list|(
operator|new
name|DisplayHeader
argument_list|(
name|headerCell
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|alias
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
else|else
block|{
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
operator|new
name|DisplayHeader
argument_list|(
name|cell
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|cell
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|display
return|;
block|}
DECL|method|buildHelpWidths
specifier|public
specifier|static
name|int
index|[]
name|buildHelpWidths
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
literal|3
index|]
decl_stmt|;
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
name|v
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
literal|0
index|]
operator|<
name|vWidth
condition|)
block|{
name|width
index|[
literal|0
index|]
operator|=
name|vWidth
expr_stmt|;
block|}
name|v
operator|=
name|renderValue
argument_list|(
name|request
argument_list|,
name|cell
operator|.
name|attr
operator|.
name|containsKey
argument_list|(
literal|"alias"
argument_list|)
condition|?
name|cell
operator|.
name|attr
operator|.
name|get
argument_list|(
literal|"alias"
argument_list|)
else|:
literal|""
argument_list|)
expr_stmt|;
name|vWidth
operator|=
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
expr_stmt|;
if|if
condition|(
name|width
index|[
literal|1
index|]
operator|<
name|vWidth
condition|)
block|{
name|width
index|[
literal|1
index|]
operator|=
name|vWidth
expr_stmt|;
block|}
name|v
operator|=
name|renderValue
argument_list|(
name|request
argument_list|,
name|cell
operator|.
name|attr
operator|.
name|containsKey
argument_list|(
literal|"desc"
argument_list|)
condition|?
name|cell
operator|.
name|attr
operator|.
name|get
argument_list|(
literal|"desc"
argument_list|)
else|:
literal|"not available"
argument_list|)
expr_stmt|;
name|vWidth
operator|=
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
expr_stmt|;
if|if
condition|(
name|width
index|[
literal|2
index|]
operator|<
name|vWidth
condition|)
block|{
name|width
index|[
literal|2
index|]
operator|=
name|vWidth
expr_stmt|;
block|}
block|}
return|return
name|width
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
parameter_list|,
name|List
argument_list|<
name|DisplayHeader
argument_list|>
name|headers
parameter_list|)
block|{
name|int
index|[]
name|width
init|=
operator|new
name|int
index|[
name|headers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|DisplayHeader
name|hdr
range|:
name|headers
control|)
block|{
name|int
name|vWidth
init|=
name|hdr
operator|.
name|display
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|width
index|[
name|i
index|]
operator|<
name|vWidth
condition|)
block|{
name|width
index|[
name|i
index|]
operator|=
name|vWidth
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|DisplayHeader
name|hdr
range|:
name|headers
control|)
block|{
for|for
control|(
name|Table
operator|.
name|Cell
name|cell
range|:
name|table
operator|.
name|getAsMap
argument_list|()
operator|.
name|get
argument_list|(
name|hdr
operator|.
name|name
argument_list|)
control|)
block|{
name|String
name|v
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
name|i
index|]
operator|<
name|vWidth
condition|)
block|{
name|width
index|[
name|i
index|]
operator|=
name|vWidth
expr_stmt|;
block|}
block|}
name|i
operator|++
expr_stmt|;
block|}
return|return
name|width
return|;
block|}
DECL|method|pad
specifier|public
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
DECL|class|DisplayHeader
specifier|static
class|class
name|DisplayHeader
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|display
specifier|public
specifier|final
name|String
name|display
decl_stmt|;
DECL|method|DisplayHeader
name|DisplayHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|display
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|display
operator|=
name|display
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

