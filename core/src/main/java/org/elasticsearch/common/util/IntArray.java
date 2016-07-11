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

begin_comment
comment|/**  * Abstraction of an array of integer values.  */
end_comment

begin_interface
DECL|interface|IntArray
specifier|public
interface|interface
name|IntArray
extends|extends
name|BigArray
block|{
comment|/**      * Get an element given its index.      */
DECL|method|get
name|int
name|get
parameter_list|(
name|long
name|index
parameter_list|)
function_decl|;
comment|/**      * Set a value at the given index and return the previous value.      */
DECL|method|set
name|int
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|value
parameter_list|)
function_decl|;
comment|/**      * Increment value at the given index by<code>inc</code> and return the value.      */
DECL|method|increment
name|int
name|increment
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|inc
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
name|int
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

