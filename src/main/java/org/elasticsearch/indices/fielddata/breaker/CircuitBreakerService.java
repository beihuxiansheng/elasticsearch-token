begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.fielddata.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|fielddata
operator|.
name|breaker
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
name|breaker
operator|.
name|MemoryCircuitBreaker
import|;
end_import

begin_comment
comment|/**  * Interface for Circuit Breaker services, which provide breakers to classes  * that load field data.  */
end_comment

begin_interface
DECL|interface|CircuitBreakerService
specifier|public
interface|interface
name|CircuitBreakerService
block|{
comment|/**      * @return the breaker that can be used to register estimates against      */
DECL|method|getBreaker
specifier|public
name|MemoryCircuitBreaker
name|getBreaker
parameter_list|()
function_decl|;
comment|/**      * @return stats about the breaker      */
DECL|method|stats
specifier|public
name|FieldDataBreakerStats
name|stats
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

