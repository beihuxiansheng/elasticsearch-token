begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|recovery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|IndicesAdminClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|IndicesAction
import|;
end_import

begin_comment
comment|/**  * Recovery information action  */
end_comment

begin_class
DECL|class|RecoveryAction
specifier|public
class|class
name|RecoveryAction
extends|extends
name|IndicesAction
argument_list|<
name|RecoveryRequest
argument_list|,
name|RecoveryResponse
argument_list|,
name|RecoveryRequestBuilder
argument_list|>
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|RecoveryAction
name|INSTANCE
init|=
operator|new
name|RecoveryAction
argument_list|()
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"indices:monitor/recovery"
decl_stmt|;
DECL|method|RecoveryAction
specifier|private
name|RecoveryAction
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newRequestBuilder
specifier|public
name|RecoveryRequestBuilder
name|newRequestBuilder
parameter_list|(
name|IndicesAdminClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|RecoveryRequestBuilder
argument_list|(
name|client
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|public
name|RecoveryResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|RecoveryResponse
argument_list|()
return|;
block|}
block|}
end_class

end_unit

