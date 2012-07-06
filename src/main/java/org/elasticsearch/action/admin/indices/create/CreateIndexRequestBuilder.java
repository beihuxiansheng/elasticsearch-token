begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.create
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
name|create
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
name|ActionListener
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
name|support
operator|.
name|BaseIndicesRequestBuilder
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|bytes
operator|.
name|BytesReference
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|xcontent
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CreateIndexRequestBuilder
specifier|public
class|class
name|CreateIndexRequestBuilder
extends|extends
name|BaseIndicesRequestBuilder
argument_list|<
name|CreateIndexRequest
argument_list|,
name|CreateIndexResponse
argument_list|>
block|{
DECL|method|CreateIndexRequestBuilder
specifier|public
name|CreateIndexRequestBuilder
parameter_list|(
name|IndicesAdminClient
name|indicesClient
parameter_list|)
block|{
name|super
argument_list|(
name|indicesClient
argument_list|,
operator|new
name|CreateIndexRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CreateIndexRequestBuilder
specifier|public
name|CreateIndexRequestBuilder
parameter_list|(
name|IndicesAdminClient
name|indicesClient
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|indicesClient
argument_list|,
operator|new
name|CreateIndexRequest
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setIndex
specifier|public
name|CreateIndexRequestBuilder
name|setIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|request
operator|.
name|index
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The settings to created the index with.      */
DECL|method|setSettings
specifier|public
name|CreateIndexRequestBuilder
name|setSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The settings to created the index with.      */
DECL|method|setSettings
specifier|public
name|CreateIndexRequestBuilder
name|setSettings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Allows to set the settings using a json builder.      */
DECL|method|setSettings
specifier|public
name|CreateIndexRequestBuilder
name|setSettings
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The settings to crete the index with (either json/yaml/properties format)      */
DECL|method|setSettings
specifier|public
name|CreateIndexRequestBuilder
name|setSettings
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The settings to crete the index with (either json/yaml/properties format)      */
DECL|method|setSettings
specifier|public
name|CreateIndexRequestBuilder
name|setSettings
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds mapping that will be added when the index gets created.      *      * @param type   The mapping type      * @param source The mapping source      */
DECL|method|addMapping
specifier|public
name|CreateIndexRequestBuilder
name|addMapping
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|source
parameter_list|)
block|{
name|request
operator|.
name|mapping
argument_list|(
name|type
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The cause for this index creation.      */
DECL|method|cause
specifier|public
name|CreateIndexRequestBuilder
name|cause
parameter_list|(
name|String
name|cause
parameter_list|)
block|{
name|request
operator|.
name|cause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds mapping that will be added when the index gets created.      *      * @param type   The mapping type      * @param source The mapping source      */
DECL|method|addMapping
specifier|public
name|CreateIndexRequestBuilder
name|addMapping
parameter_list|(
name|String
name|type
parameter_list|,
name|XContentBuilder
name|source
parameter_list|)
block|{
name|request
operator|.
name|mapping
argument_list|(
name|type
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds mapping that will be added when the index gets created.      *      * @param type   The mapping type      * @param source The mapping source      */
DECL|method|addMapping
specifier|public
name|CreateIndexRequestBuilder
name|addMapping
parameter_list|(
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|request
operator|.
name|mapping
argument_list|(
name|type
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the settings and mappings as a single source.      */
DECL|method|setSource
specifier|public
name|CreateIndexRequestBuilder
name|setSource
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the settings and mappings as a single source.      */
DECL|method|setSource
specifier|public
name|CreateIndexRequestBuilder
name|setSource
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the settings and mappings as a single source.      */
DECL|method|setSource
specifier|public
name|CreateIndexRequestBuilder
name|setSource
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the settings and mappings as a single source.      */
DECL|method|setSource
specifier|public
name|CreateIndexRequestBuilder
name|setSource
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the settings and mappings as a single source.      */
DECL|method|setSource
specifier|public
name|CreateIndexRequestBuilder
name|setSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCustom
specifier|public
name|CreateIndexRequestBuilder
name|setCustom
parameter_list|(
name|IndexMetaData
operator|.
name|Custom
name|custom
parameter_list|)
block|{
name|request
operator|.
name|custom
argument_list|(
name|custom
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the settings and mappings as a single source.      */
DECL|method|setSource
specifier|public
name|CreateIndexRequestBuilder
name|setSource
parameter_list|(
name|XContentBuilder
name|source
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Timeout to wait for the index creation to be acknowledged by current cluster nodes. Defaults      * to<tt>10s</tt>.      */
DECL|method|setTimeout
specifier|public
name|CreateIndexRequestBuilder
name|setTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Timeout to wait for the index creation to be acknowledged by current cluster nodes. Defaults      * to<tt>10s</tt>.      */
DECL|method|setTimeout
specifier|public
name|CreateIndexRequestBuilder
name|setTimeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the master node timeout in case the master has not yet been discovered.      */
DECL|method|setMasterNodeTimeout
specifier|public
name|CreateIndexRequestBuilder
name|setMasterNodeTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|masterNodeTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the master node timeout in case the master has not yet been discovered.      */
DECL|method|setMasterNodeTimeout
specifier|public
name|CreateIndexRequestBuilder
name|setMasterNodeTimeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|masterNodeTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|CreateIndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|create
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

