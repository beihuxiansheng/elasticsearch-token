begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|Nullable
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
name|bytes
operator|.
name|BytesReference
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
name|ToXContent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|parseTimeValue
import|;
end_import

begin_class
DECL|class|RestRequest
specifier|public
specifier|abstract
class|class
name|RestRequest
implements|implements
name|ToXContent
operator|.
name|Params
block|{
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
decl_stmt|;
DECL|field|rawPath
specifier|private
specifier|final
name|String
name|rawPath
decl_stmt|;
DECL|method|RestRequest
specifier|public
name|RestRequest
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|pathEndPos
init|=
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathEndPos
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|rawPath
operator|=
name|uri
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|rawPath
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pathEndPos
argument_list|)
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|pathEndPos
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
DECL|method|RestRequest
specifier|public
name|RestRequest
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|rawPath
operator|=
name|path
expr_stmt|;
block|}
DECL|enum|Method
specifier|public
enum|enum
name|Method
block|{
DECL|enum constant|GET
DECL|enum constant|POST
DECL|enum constant|PUT
DECL|enum constant|DELETE
DECL|enum constant|OPTIONS
DECL|enum constant|HEAD
name|GET
block|,
name|POST
block|,
name|PUT
block|,
name|DELETE
block|,
name|OPTIONS
block|,
name|HEAD
block|}
DECL|method|method
specifier|public
specifier|abstract
name|Method
name|method
parameter_list|()
function_decl|;
comment|/**      * The uri of the rest request, with the query string.      */
DECL|method|uri
specifier|public
specifier|abstract
name|String
name|uri
parameter_list|()
function_decl|;
comment|/**      * The non decoded, raw path provided.      */
DECL|method|rawPath
specifier|public
name|String
name|rawPath
parameter_list|()
block|{
return|return
name|rawPath
return|;
block|}
comment|/**      * The path part of the URI (without the query string), decoded.      */
DECL|method|path
specifier|public
specifier|final
name|String
name|path
parameter_list|()
block|{
return|return
name|RestUtils
operator|.
name|decodeComponent
argument_list|(
name|rawPath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|hasContent
specifier|public
specifier|abstract
name|boolean
name|hasContent
parameter_list|()
function_decl|;
DECL|method|content
specifier|public
specifier|abstract
name|BytesReference
name|content
parameter_list|()
function_decl|;
DECL|method|header
specifier|public
specifier|abstract
name|String
name|header
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|headers
specifier|public
specifier|abstract
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|headers
parameter_list|()
function_decl|;
annotation|@
name|Nullable
DECL|method|getRemoteAddress
specifier|public
name|SocketAddress
name|getRemoteAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
DECL|method|getLocalAddress
specifier|public
name|SocketAddress
name|getLocalAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|hasParam
specifier|public
specifier|final
name|boolean
name|hasParam
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|params
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
specifier|final
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
specifier|final
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|value
return|;
block|}
DECL|method|params
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|()
block|{
return|return
name|params
return|;
block|}
DECL|method|paramAsFloat
specifier|public
name|float
name|paramAsFloat
parameter_list|(
name|String
name|key
parameter_list|,
name|float
name|defaultValue
parameter_list|)
block|{
name|String
name|sValue
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|sValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse float parameter ["
operator|+
name|key
operator|+
literal|"] with value ["
operator|+
name|sValue
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|paramAsInt
specifier|public
name|int
name|paramAsInt
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
name|String
name|sValue
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|sValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse int parameter ["
operator|+
name|key
operator|+
literal|"] with value ["
operator|+
name|sValue
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|paramAsLong
specifier|public
name|long
name|paramAsLong
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|defaultValue
parameter_list|)
block|{
name|String
name|sValue
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|sValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse long parameter ["
operator|+
name|key
operator|+
literal|"] with value ["
operator|+
name|sValue
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|paramAsBoolean
specifier|public
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|param
argument_list|(
name|key
argument_list|)
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|paramAsBoolean
specifier|public
name|Boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|param
argument_list|(
name|key
argument_list|)
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
DECL|method|paramAsTime
specifier|public
name|TimeValue
name|paramAsTime
parameter_list|(
name|String
name|key
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|)
block|{
return|return
name|parseTimeValue
argument_list|(
name|param
argument_list|(
name|key
argument_list|)
argument_list|,
name|defaultValue
argument_list|,
name|key
argument_list|)
return|;
block|}
DECL|method|paramAsSize
specifier|public
name|ByteSizeValue
name|paramAsSize
parameter_list|(
name|String
name|key
parameter_list|,
name|ByteSizeValue
name|defaultValue
parameter_list|)
block|{
return|return
name|parseBytesSizeValue
argument_list|(
name|param
argument_list|(
name|key
argument_list|)
argument_list|,
name|defaultValue
argument_list|,
name|key
argument_list|)
return|;
block|}
DECL|method|paramAsStringArray
specifier|public
name|String
index|[]
name|paramAsStringArray
parameter_list|(
name|String
name|key
parameter_list|,
name|String
index|[]
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|paramAsStringArrayOrEmptyIfAll
specifier|public
name|String
index|[]
name|paramAsStringArrayOrEmptyIfAll
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
index|[]
name|params
init|=
name|paramAsStringArray
argument_list|(
name|key
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isAllOrWildcard
argument_list|(
name|params
argument_list|)
condition|)
block|{
return|return
name|Strings
operator|.
name|EMPTY_ARRAY
return|;
block|}
return|return
name|params
return|;
block|}
block|}
end_class

end_unit

