begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|File
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_comment
comment|/**  * A translog stream that will read and write operations in the  * version-specific format  */
end_comment

begin_interface
DECL|interface|TranslogStream
specifier|public
interface|interface
name|TranslogStream
block|{
comment|/**      * Read the next translog operation from the input stream      */
DECL|method|read
specifier|public
name|Translog
operator|.
name|Operation
name|read
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Write the given translog operation to the output stream      */
DECL|method|write
specifier|public
name|void
name|write
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
function_decl|;
comment|/**      * Optionally write a header identifying the translog version to the      * file channel      */
DECL|method|writeHeader
specifier|public
name|int
name|writeHeader
parameter_list|(
name|FileChannel
name|channel
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Seek past the header, if any header is present      */
DECL|method|openInput
specifier|public
name|StreamInput
name|openInput
parameter_list|(
name|File
name|translogFile
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

