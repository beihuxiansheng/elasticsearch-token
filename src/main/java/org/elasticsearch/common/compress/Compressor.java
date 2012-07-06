begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
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
comment|/**  */
end_comment

begin_interface
DECL|interface|Compressor
specifier|public
interface|interface
name|Compressor
block|{
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
DECL|method|configure
name|void
name|configure
parameter_list|(
name|Settings
name|settings
parameter_list|)
function_decl|;
DECL|method|isCompressed
name|boolean
name|isCompressed
parameter_list|(
name|BytesReference
name|bytes
parameter_list|)
function_decl|;
DECL|method|isCompressed
name|boolean
name|isCompressed
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
function_decl|;
DECL|method|isCompressed
name|boolean
name|isCompressed
parameter_list|(
name|ChannelBuffer
name|buffer
parameter_list|)
function_decl|;
DECL|method|isCompressed
name|boolean
name|isCompressed
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Uncompress the provided data, data can be detected as compressed using {@link #isCompressed(byte[], int, int)}.      */
DECL|method|uncompress
name|byte
index|[]
name|uncompress
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
comment|/**      * Compresses the provided data, data can be detected as compressed using {@link #isCompressed(byte[], int, int)}.      */
DECL|method|compress
name|byte
index|[]
name|compress
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
DECL|method|streamInput
name|CompressedStreamInput
name|streamInput
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|streamOutput
name|CompressedStreamOutput
name|streamOutput
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|indexInput
name|CompressedIndexInput
name|indexInput
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|indexOutput
name|CompressedIndexOutput
name|indexOutput
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

