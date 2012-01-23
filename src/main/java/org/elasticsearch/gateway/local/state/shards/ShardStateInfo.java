begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local.state.shards
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|shards
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
name|Nullable
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardStateInfo
specifier|public
class|class
name|ShardStateInfo
block|{
DECL|field|version
specifier|public
specifier|final
name|long
name|version
decl_stmt|;
comment|// can be null if we don't know...
annotation|@
name|Nullable
DECL|field|primary
specifier|public
specifier|final
name|Boolean
name|primary
decl_stmt|;
DECL|method|ShardStateInfo
specifier|public
name|ShardStateInfo
parameter_list|(
name|long
name|version
parameter_list|,
name|Boolean
name|primary
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
block|}
block|}
end_class

end_unit

