begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
package|;
end_package

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

begin_comment
comment|/**   * abstract class for verifying what was written.  * subclasses override {@link #writeByte(byte)} and {@link #writeBytes(byte[], int, int)}  */
end_comment

begin_comment
comment|// do NOT optimize this class for performance
end_comment

begin_class
DECL|class|VerifyingIndexOutput
specifier|public
specifier|abstract
class|class
name|VerifyingIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|out
specifier|protected
specifier|final
name|IndexOutput
name|out
decl_stmt|;
comment|/** Sole constructor */
DECL|method|VerifyingIndexOutput
name|VerifyingIndexOutput
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**      * Verifies the checksum and compares the written length with the expected file length. This method should be      * called after all data has been written to this output.      */
DECL|method|verify
specifier|public
specifier|abstract
name|void
name|verify
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|// default implementations... forwarding to delegate
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChecksum
specifier|public
specifier|final
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|out
operator|.
name|getChecksum
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
specifier|final
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|out
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
block|}
end_class

end_unit

