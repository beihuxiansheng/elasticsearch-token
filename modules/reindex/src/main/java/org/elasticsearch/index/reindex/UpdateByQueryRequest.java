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
name|search
operator|.
name|SearchRequest
import|;
end_import

begin_comment
comment|/**  * Request to reindex a set of documents where they are without changing their  * locations or IDs.  */
end_comment

begin_class
DECL|class|UpdateByQueryRequest
specifier|public
class|class
name|UpdateByQueryRequest
extends|extends
name|AbstractBulkIndexByScrollRequest
argument_list|<
name|UpdateByQueryRequest
argument_list|>
block|{
DECL|method|UpdateByQueryRequest
specifier|public
name|UpdateByQueryRequest
parameter_list|()
block|{     }
DECL|method|UpdateByQueryRequest
specifier|public
name|UpdateByQueryRequest
parameter_list|(
name|SearchRequest
name|search
parameter_list|)
block|{
name|super
argument_list|(
name|search
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|self
specifier|protected
name|UpdateByQueryRequest
name|self
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"update-by-query "
argument_list|)
expr_stmt|;
name|searchToString
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

