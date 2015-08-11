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
name|inject
operator|.
name|AbstractModule
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

begin_class
DECL|class|CircuitBreakerModule
specifier|public
class|class
name|CircuitBreakerModule
extends|extends
name|AbstractModule
block|{
DECL|field|TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_KEY
init|=
literal|"indices.breaker.type"
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|method|CircuitBreakerModule
specifier|public
name|CircuitBreakerModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|String
name|type
init|=
name|settings
operator|.
name|get
argument_list|(
name|TYPE_KEY
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|CircuitBreakerService
argument_list|>
name|impl
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"hierarchy"
argument_list|)
condition|)
block|{
name|impl
operator|=
name|HierarchyCircuitBreakerService
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"none"
argument_list|)
condition|)
block|{
name|impl
operator|=
name|NoneCircuitBreakerService
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown circuit breaker type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|bind
argument_list|(
name|CircuitBreakerService
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|impl
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

