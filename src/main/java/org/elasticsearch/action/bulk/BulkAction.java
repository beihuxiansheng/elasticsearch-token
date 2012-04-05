begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
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
name|transport
operator|.
name|TransportRequestOptions
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|BulkAction
specifier|public
class|class
name|BulkAction
extends|extends
name|Action
argument_list|<
name|BulkRequest
argument_list|,
name|BulkResponse
argument_list|,
name|BulkRequestBuilder
argument_list|>
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|BulkAction
name|INSTANCE
init|=
operator|new
name|BulkAction
argument_list|()
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"bulk"
decl_stmt|;
DECL|method|BulkAction
specifier|private
name|BulkAction
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
DECL|method|newResponse
specifier|public
name|BulkResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|BulkResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newRequestBuilder
specifier|public
name|BulkRequestBuilder
name|newRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
return|return
operator|new
name|BulkRequestBuilder
argument_list|(
name|client
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|transportOptions
specifier|public
name|TransportRequestOptions
name|transportOptions
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|TransportRequestOptions
operator|.
name|options
argument_list|()
operator|.
name|withType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|fromString
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"action.bulk.transport.type"
argument_list|,
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|LOW
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withCompress
argument_list|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"action.bulk.compress"
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

