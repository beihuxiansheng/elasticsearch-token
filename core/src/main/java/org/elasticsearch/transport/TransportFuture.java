begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_interface
DECL|interface|TransportFuture
specifier|public
interface|interface
name|TransportFuture
parameter_list|<
name|V
parameter_list|>
extends|extends
name|Future
argument_list|<
name|V
argument_list|>
block|{
comment|/**      * Waits if necessary for the computation to complete, and then      * retrieves its result.      */
DECL|method|txGet
name|V
name|txGet
parameter_list|()
function_decl|;
comment|/**      * Waits if necessary for at most the given time for the computation      * to complete, and then retrieves its result, if available.      */
DECL|method|txGet
name|V
name|txGet
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

