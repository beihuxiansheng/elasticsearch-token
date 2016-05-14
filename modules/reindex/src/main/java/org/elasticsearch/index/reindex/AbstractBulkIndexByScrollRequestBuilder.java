begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
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
name|Action
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
name|search
operator|.
name|SearchRequestBuilder
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
import|;
end_import

begin_class
DECL|class|AbstractBulkIndexByScrollRequestBuilder
specifier|public
specifier|abstract
class|class
name|AbstractBulkIndexByScrollRequestBuilder
parameter_list|<
name|Request
extends|extends
name|AbstractBulkIndexByScrollRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|Response
extends|extends
name|BulkIndexByScrollResponse
parameter_list|,
name|Self
extends|extends
name|AbstractBulkIndexByScrollRequestBuilder
parameter_list|<
name|Request
parameter_list|,
name|Response
parameter_list|,
name|Self
parameter_list|>
parameter_list|>
extends|extends
name|AbstractBulkByScrollRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|Self
argument_list|>
block|{
DECL|method|AbstractBulkIndexByScrollRequestBuilder
specifier|protected
name|AbstractBulkIndexByScrollRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|Self
argument_list|>
name|action
parameter_list|,
name|SearchRequestBuilder
name|search
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
name|search
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|/**      * Script to modify the documents before they are processed.      */
DECL|method|script
specifier|public
name|Self
name|script
parameter_list|(
name|Script
name|script
parameter_list|)
block|{
name|request
operator|.
name|setScript
argument_list|(
name|script
argument_list|)
expr_stmt|;
return|return
name|self
argument_list|()
return|;
block|}
block|}
end_class

end_unit

