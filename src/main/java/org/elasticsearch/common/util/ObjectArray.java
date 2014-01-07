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
comment|/**  * Abstraction of an array of object values.  */
end_comment

begin_interface
DECL|interface|ObjectArray
specifier|public
interface|interface
name|ObjectArray
parameter_list|<
name|T
parameter_list|>
extends|extends
name|BigArray
block|{
comment|/**      * Get an element given its index.      */
DECL|method|get
specifier|public
specifier|abstract
name|T
name|get
parameter_list|(
name|long
name|index
parameter_list|)
function_decl|;
comment|/**      * Set a value at the given index and return the previous value.      */
DECL|method|set
specifier|public
specifier|abstract
name|T
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|T
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

