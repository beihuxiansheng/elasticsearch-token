begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.io
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|NotThreadSafe
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
DECL|class|ByteArrayDataInputStream
specifier|public
class|class
name|ByteArrayDataInputStream
extends|extends
name|DataInputStream
block|{
comment|/**      * Creates a DataInputStream that uses the specified      * underlying InputStream.      *      * @param source the specified source      */
DECL|method|ByteArrayDataInputStream
specifier|public
name|ByteArrayDataInputStream
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|FastByteArrayInputStream
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

