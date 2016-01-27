begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.percolate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|percolate
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
name|percolate
operator|.
name|MultiPercolateRequest
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
name|percolate
operator|.
name|MultiPercolateResponse
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
name|IndicesOptions
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
name|Client
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
name|Strings
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
name|inject
operator|.
name|Inject
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|BaseRestHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestActions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestToXContentListener
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|POST
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestMultiPercolateAction
specifier|public
class|class
name|RestMultiPercolateAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|allowExplicitIndex
specifier|private
specifier|final
name|boolean
name|allowExplicitIndex
decl_stmt|;
annotation|@
name|Inject
DECL|method|RestMultiPercolateAction
specifier|public
name|RestMultiPercolateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/_mpercolate"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/_mpercolate"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/{type}/_mpercolate"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_mpercolate"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_mpercolate"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/{type}/_mpercolate"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|allowExplicitIndex
operator|=
name|MULTI_ALLOW_EXPLICIT_INDEX
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
specifier|final
name|RestRequest
name|restRequest
parameter_list|,
specifier|final
name|RestChannel
name|restChannel
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|MultiPercolateRequest
name|multiPercolateRequest
init|=
operator|new
name|MultiPercolateRequest
argument_list|()
decl_stmt|;
name|multiPercolateRequest
operator|.
name|indicesOptions
argument_list|(
name|IndicesOptions
operator|.
name|fromRequest
argument_list|(
name|restRequest
argument_list|,
name|multiPercolateRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|multiPercolateRequest
operator|.
name|indices
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|restRequest
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|multiPercolateRequest
operator|.
name|documentType
argument_list|(
name|restRequest
operator|.
name|param
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|multiPercolateRequest
operator|.
name|add
argument_list|(
name|RestActions
operator|.
name|getRestContent
argument_list|(
name|restRequest
argument_list|)
argument_list|,
name|allowExplicitIndex
argument_list|)
expr_stmt|;
name|client
operator|.
name|multiPercolate
argument_list|(
name|multiPercolateRequest
argument_list|,
operator|new
name|RestToXContentListener
argument_list|<
name|MultiPercolateResponse
argument_list|>
argument_list|(
name|restChannel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

