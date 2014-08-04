begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.alias.exists
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
name|alias
operator|.
name|exists
package|;
end_package

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
name|alias
operator|.
name|get
operator|.
name|GetAliasesRequest
import|;
end_import

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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|AliasesExistAction
specifier|public
class|class
name|AliasesExistAction
extends|extends
name|IndicesAction
argument_list|<
name|GetAliasesRequest
argument_list|,
name|AliasesExistResponse
argument_list|,
name|AliasesExistRequestBuilder
argument_list|>
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|AliasesExistAction
name|INSTANCE
init|=
operator|new
name|AliasesExistAction
argument_list|()
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"indices:admin/aliases/exists"
decl_stmt|;
DECL|method|AliasesExistAction
specifier|private
name|AliasesExistAction
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
name|AliasesExistRequestBuilder
name|newRequestBuilder
parameter_list|(
name|IndicesAdminClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|AliasesExistRequestBuilder
argument_list|(
name|client
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|public
name|AliasesExistResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|AliasesExistResponse
argument_list|()
return|;
block|}
block|}
end_class

end_unit

