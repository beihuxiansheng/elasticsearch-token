begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.bytes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * A reference to bytes.  */
end_comment

begin_interface
DECL|interface|BytesReference
specifier|public
interface|interface
name|BytesReference
block|{
comment|/**      * Returns the byte at the specified index. Need to be between 0 and length.      */
DECL|method|get
name|byte
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * The length.      */
DECL|method|length
name|int
name|length
parameter_list|()
function_decl|;
comment|/**      * Slice the bytes from the<tt>from</tt> index up to<tt>length</tt>.      */
DECL|method|slice
name|BytesReference
name|slice
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**      * A stream input of the bytes.      */
DECL|method|streamInput
name|StreamInput
name|streamInput
parameter_list|()
function_decl|;
comment|/**      * Writes the bytes into the output, with an optional length header (variable encoded).      */
DECL|method|writeTo
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|boolean
name|withLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeTo
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the bytes as a single byte array.      */
DECL|method|toBytes
name|byte
index|[]
name|toBytes
parameter_list|()
function_decl|;
comment|/**      * Returns the bytes as a byte array, possibly sharing the underlying byte buffer.      */
DECL|method|toBytesArray
name|BytesArray
name|toBytesArray
parameter_list|()
function_decl|;
comment|/**      * Returns the bytes copied over as a byte array.      */
DECL|method|copyBytesArray
name|BytesArray
name|copyBytesArray
parameter_list|()
function_decl|;
comment|/**      * Is there an underlying byte array for this bytes reference.      */
DECL|method|hasArray
name|boolean
name|hasArray
parameter_list|()
function_decl|;
comment|/**      * The underlying byte array (if exists).      */
DECL|method|array
name|byte
index|[]
name|array
parameter_list|()
function_decl|;
comment|/**      * The offset into the underlying byte array.      */
DECL|method|arrayOffset
name|int
name|arrayOffset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

