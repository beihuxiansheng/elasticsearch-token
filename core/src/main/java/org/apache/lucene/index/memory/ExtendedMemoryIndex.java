begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
package|;
end_package

begin_comment
comment|/**  * This class overwrites {@link MemoryIndex} to make the reuse constructor visible.  */
end_comment

begin_class
DECL|class|ExtendedMemoryIndex
specifier|public
specifier|final
class|class
name|ExtendedMemoryIndex
extends|extends
name|MemoryIndex
block|{
DECL|method|ExtendedMemoryIndex
specifier|public
name|ExtendedMemoryIndex
parameter_list|(
name|boolean
name|storeOffsets
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|long
name|maxReusedBytes
parameter_list|)
block|{
name|super
argument_list|(
name|storeOffsets
argument_list|,
name|storePayloads
argument_list|,
name|maxReusedBytes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

