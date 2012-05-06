begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
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
name|BytesHolder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A generic abstraction on top of handling content, inspired by JSON and pull parsing.  */
end_comment

begin_interface
DECL|interface|XContent
specifier|public
interface|interface
name|XContent
block|{
comment|/**      * The type this content handles and produces.      */
DECL|method|type
name|XContentType
name|type
parameter_list|()
function_decl|;
DECL|method|streamSeparator
name|byte
name|streamSeparator
parameter_list|()
function_decl|;
comment|/**      * Creates a new generator using the provided output stream.      */
DECL|method|createGenerator
name|XContentGenerator
name|createGenerator
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a new generator using the provided writer.      */
DECL|method|createGenerator
name|XContentGenerator
name|createGenerator
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided string content.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided input stream.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided bytes.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided bytes.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided bytes.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|BytesHolder
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Creates a parser over the provided reader.      */
DECL|method|createParser
name|XContentParser
name|createParser
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

