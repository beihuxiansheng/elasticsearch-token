begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Abstraction of an array of byte values.  */
end_comment

begin_interface
DECL|interface|ByteArray
specifier|public
interface|interface
name|ByteArray
extends|extends
name|BigArray
block|{
comment|/**      * Get an element given its index.      */
DECL|method|get
name|byte
name|get
parameter_list|(
name|long
name|index
parameter_list|)
function_decl|;
comment|/**      * Set a value at the given index and return the previous value.      */
DECL|method|set
name|byte
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|byte
name|value
parameter_list|)
function_decl|;
comment|/**      * Get a reference to a slice.      *      * @return<code>true</code> when a byte[] was materialized,<code>false</code> otherwise.      */
DECL|method|get
name|boolean
name|get
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|len
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
function_decl|;
comment|/**      * Bulk set.      */
DECL|method|set
name|void
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/**      * Fill slots between<code>fromIndex</code> inclusive to<code>toIndex</code> exclusive with<code>value</code>.      */
DECL|method|fill
name|void
name|fill
parameter_list|(
name|long
name|fromIndex
parameter_list|,
name|long
name|toIndex
parameter_list|,
name|byte
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

