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
name|lucene
operator|.
name|store
operator|.
name|FilterIndexOutput
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
comment|/**  * abstract class for verifying what was written.  * subclasses override {@link #writeByte(byte)} and {@link #writeBytes(byte[], int, int)}  */
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
name|FilterIndexOutput
block|{
comment|/** Sole constructor */
DECL|method|VerifyingIndexOutput
name|VerifyingIndexOutput
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
block|{
name|super
argument_list|(
literal|"VerifyingIndexOutput(out="
operator|+
name|out
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|,
name|out
argument_list|)
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
block|}
end_class

end_unit

