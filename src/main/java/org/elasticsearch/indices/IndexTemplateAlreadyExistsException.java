begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
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
name|ElasticSearchException
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
name|RestStatus
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndexTemplateAlreadyExistsException
specifier|public
class|class
name|IndexTemplateAlreadyExistsException
extends|extends
name|ElasticSearchException
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|IndexTemplateAlreadyExistsException
specifier|public
name|IndexTemplateAlreadyExistsException
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
literal|"index_template ["
operator|+
name|name
operator|+
literal|"] already exists"
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|RestStatus
operator|.
name|BAD_REQUEST
return|;
block|}
block|}
end_class

end_unit

