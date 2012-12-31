begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.text
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|text
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
name|bytes
operator|.
name|BytesReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Text represents a (usually) long text data. We use this abstraction instead of {@link String}  * so we can represent it in a more optimized manner in memory as well as serializing it over the  * network as well as converting it to json format.  */
end_comment

begin_interface
DECL|interface|Text
specifier|public
interface|interface
name|Text
extends|extends
name|Comparable
argument_list|<
name|Text
argument_list|>
extends|,
name|Serializable
block|{
comment|/**      * Are bytes available without the need to be converted into bytes when calling {@link #bytes()}.      */
DECL|method|hasBytes
name|boolean
name|hasBytes
parameter_list|()
function_decl|;
comment|/**      * The UTF8 bytes representing the the text, might be converted on the fly, see {@link #hasBytes()}      */
DECL|method|bytes
name|BytesReference
name|bytes
parameter_list|()
function_decl|;
comment|/**      * Is there a {@link String} representation of the text. If not, then it {@link #hasBytes()}.      */
DECL|method|hasString
name|boolean
name|hasString
parameter_list|()
function_decl|;
comment|/**      * Returns the string representation of the text, might be converted to a string on the fly.      */
DECL|method|string
name|String
name|string
parameter_list|()
function_decl|;
comment|/**      * Returns the string representation of the text, might be converted to a string on the fly.      */
DECL|method|toString
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

