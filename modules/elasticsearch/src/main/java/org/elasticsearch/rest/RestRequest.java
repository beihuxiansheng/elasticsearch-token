begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|util
operator|.
name|Map
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|RestRequest
specifier|public
interface|interface
name|RestRequest
extends|extends
name|ToXContent
operator|.
name|Params
block|{
DECL|enum|Method
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
name|Method
name|method
parameter_list|()
function_decl|;
comment|/**      * The uri of the rest request, with the query string.      */
DECL|method|uri
name|String
name|uri
parameter_list|()
function_decl|;
comment|/**      * The path part of the URI (without the query string).      */
DECL|method|path
name|String
name|path
parameter_list|()
function_decl|;
DECL|method|hasContent
name|boolean
name|hasContent
parameter_list|()
function_decl|;
comment|/**      * Is the byte array content safe or unsafe for usage on other threads      */
DECL|method|contentUnsafe
name|boolean
name|contentUnsafe
parameter_list|()
function_decl|;
DECL|method|contentByteArray
name|byte
index|[]
name|contentByteArray
parameter_list|()
function_decl|;
DECL|method|contentByteArrayOffset
name|int
name|contentByteArrayOffset
parameter_list|()
function_decl|;
DECL|method|contentLength
name|int
name|contentLength
parameter_list|()
function_decl|;
DECL|method|contentAsString
name|String
name|contentAsString
parameter_list|()
function_decl|;
DECL|method|headerNames
name|Set
argument_list|<
name|String
argument_list|>
name|headerNames
parameter_list|()
function_decl|;
DECL|method|header
name|String
name|header
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|cookie
name|String
name|cookie
parameter_list|()
function_decl|;
DECL|method|hasParam
name|boolean
name|hasParam
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|param
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|paramAsStringArray
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
function_decl|;
DECL|method|paramAsFloat
name|float
name|paramAsFloat
parameter_list|(
name|String
name|key
parameter_list|,
name|float
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsInt
name|int
name|paramAsInt
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsLong
name|long
name|paramAsLong
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsBoolean
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsBoolean
name|Boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsTime
name|TimeValue
name|paramAsTime
parameter_list|(
name|String
name|key
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsSize
name|ByteSizeValue
name|paramAsSize
parameter_list|(
name|String
name|key
parameter_list|,
name|ByteSizeValue
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|params
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

