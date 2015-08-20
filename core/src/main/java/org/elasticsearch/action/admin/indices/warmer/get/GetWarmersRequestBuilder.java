begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.warmer.get
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
name|warmer
operator|.
name|get
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ObjectArrays
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
name|support
operator|.
name|master
operator|.
name|info
operator|.
name|ClusterInfoRequestBuilder
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
name|ElasticsearchClient
import|;
end_import

begin_comment
comment|/**  * Builder for {@link GetWarmersRequest}  *  * @see GetWarmersRequest for details  */
end_comment

begin_class
DECL|class|GetWarmersRequestBuilder
specifier|public
class|class
name|GetWarmersRequestBuilder
extends|extends
name|ClusterInfoRequestBuilder
argument_list|<
name|GetWarmersRequest
argument_list|,
name|GetWarmersResponse
argument_list|,
name|GetWarmersRequestBuilder
argument_list|>
block|{
DECL|method|GetWarmersRequestBuilder
specifier|public
name|GetWarmersRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|GetWarmersAction
name|action
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|GetWarmersRequest
argument_list|()
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setWarmers
specifier|public
name|GetWarmersRequestBuilder
name|setWarmers
parameter_list|(
name|String
modifier|...
name|warmers
parameter_list|)
block|{
name|request
operator|.
name|warmers
argument_list|(
name|warmers
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addWarmers
specifier|public
name|GetWarmersRequestBuilder
name|addWarmers
parameter_list|(
name|String
modifier|...
name|warmers
parameter_list|)
block|{
name|request
operator|.
name|warmers
argument_list|(
name|ObjectArrays
operator|.
name|concat
argument_list|(
name|request
operator|.
name|warmers
argument_list|()
argument_list|,
name|warmers
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit
