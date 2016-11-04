begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.exists.indices
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
name|exists
operator|.
name|indices
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
name|support
operator|.
name|master
operator|.
name|MasterNodeReadOperationRequestBuilder
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

begin_class
DECL|class|IndicesExistsRequestBuilder
specifier|public
class|class
name|IndicesExistsRequestBuilder
extends|extends
name|MasterNodeReadOperationRequestBuilder
argument_list|<
name|IndicesExistsRequest
argument_list|,
name|IndicesExistsResponse
argument_list|,
name|IndicesExistsRequestBuilder
argument_list|>
block|{
DECL|method|IndicesExistsRequestBuilder
specifier|public
name|IndicesExistsRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|IndicesExistsAction
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
name|IndicesExistsRequest
argument_list|(
name|indices
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setIndices
specifier|public
name|IndicesExistsRequestBuilder
name|setIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|request
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls whether wildcard expressions will be expanded to existing open indices      */
DECL|method|setExpandWildcardsOpen
specifier|public
name|IndicesExistsRequestBuilder
name|setExpandWildcardsOpen
parameter_list|(
name|boolean
name|expandWildcardsOpen
parameter_list|)
block|{
name|request
operator|.
name|expandWilcardsOpen
argument_list|(
name|expandWildcardsOpen
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls whether wildcard expressions will be expanded to existing closed indices      */
DECL|method|setExpandWildcardsClosed
specifier|public
name|IndicesExistsRequestBuilder
name|setExpandWildcardsClosed
parameter_list|(
name|boolean
name|expandWildcardsClosed
parameter_list|)
block|{
name|request
operator|.
name|expandWilcardsClosed
argument_list|(
name|expandWildcardsClosed
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

