begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|CircuitBreaker
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * Interface for Circuit Breaker services, which provide breakers to classes  * that load field data.  */
end_comment

begin_class
DECL|class|CircuitBreakerService
specifier|public
specifier|abstract
class|class
name|CircuitBreakerService
extends|extends
name|AbstractLifecycleComponent
block|{
DECL|method|CircuitBreakerService
specifier|protected
name|CircuitBreakerService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allows to register of a custom circuit breaker.      */
DECL|method|registerBreaker
specifier|public
specifier|abstract
name|void
name|registerBreaker
parameter_list|(
name|BreakerSettings
name|breakerSettings
parameter_list|)
function_decl|;
comment|/**      * @return the breaker that can be used to register estimates against      */
DECL|method|getBreaker
specifier|public
specifier|abstract
name|CircuitBreaker
name|getBreaker
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * @return stats about all breakers      */
DECL|method|stats
specifier|public
specifier|abstract
name|AllCircuitBreakerStats
name|stats
parameter_list|()
function_decl|;
comment|/**      * @return stats about a specific breaker      */
DECL|method|stats
specifier|public
specifier|abstract
name|CircuitBreakerStats
name|stats
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{     }
block|}
end_class

end_unit

