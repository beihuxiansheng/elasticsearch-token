begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.action.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|action
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequestBuilder
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
name|mapping
operator|.
name|put
operator|.
name|PutMappingResponse
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
name|common
operator|.
name|component
operator|.
name|AbstractComponent
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
name|index
operator|.
name|mapper
operator|.
name|MapperService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|Mapping
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|settings
operator|.
name|NodeSettingsService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * Called by shards in the cluster when their mapping was dynamically updated and it needs to be updated  * in the cluster state meta data (and broadcast to all members).  */
end_comment

begin_class
DECL|class|MappingUpdatedAction
specifier|public
class|class
name|MappingUpdatedAction
extends|extends
name|AbstractComponent
block|{
DECL|field|INDICES_MAPPING_DYNAMIC_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|INDICES_MAPPING_DYNAMIC_TIMEOUT
init|=
literal|"indices.mapping.dynamic_timeout"
decl_stmt|;
DECL|field|client
specifier|private
name|IndicesAdminClient
name|client
decl_stmt|;
DECL|field|dynamicMappingUpdateTimeout
specifier|private
specifier|volatile
name|TimeValue
name|dynamicMappingUpdateTimeout
decl_stmt|;
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|NodeSettingsService
operator|.
name|Listener
block|{
annotation|@
name|Override
DECL|method|onRefreshSettings
specifier|public
name|void
name|onRefreshSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|TimeValue
name|current
init|=
name|MappingUpdatedAction
operator|.
name|this
operator|.
name|dynamicMappingUpdateTimeout
decl_stmt|;
name|TimeValue
name|newValue
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDICES_MAPPING_DYNAMIC_TIMEOUT
argument_list|,
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|current
operator|.
name|equals
argument_list|(
name|newValue
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating "
operator|+
name|INDICES_MAPPING_DYNAMIC_TIMEOUT
operator|+
literal|" from [{}] to [{}]"
argument_list|,
name|current
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
name|MappingUpdatedAction
operator|.
name|this
operator|.
name|dynamicMappingUpdateTimeout
operator|=
name|newValue
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Inject
DECL|method|MappingUpdatedAction
specifier|public
name|MappingUpdatedAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeSettingsService
name|nodeSettingsService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|dynamicMappingUpdateTimeout
operator|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDICES_MAPPING_DYNAMIC_TIMEOUT
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|nodeSettingsService
operator|.
name|addListener
argument_list|(
operator|new
name|ApplySettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setClient
specifier|public
name|void
name|setClient
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
expr_stmt|;
block|}
DECL|method|updateMappingRequest
specifier|private
name|PutMappingRequestBuilder
name|updateMappingRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Mapping
name|mappingUpdate
parameter_list|,
specifier|final
name|TimeValue
name|timeout
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"_default_ mapping should not be updated"
argument_list|)
throw|;
block|}
return|return
name|client
operator|.
name|preparePutMapping
argument_list|(
name|index
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setSource
argument_list|(
name|mappingUpdate
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setMasterNodeTimeout
argument_list|(
name|timeout
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
return|;
block|}
DECL|method|updateMappingOnMaster
specifier|public
name|void
name|updateMappingOnMaster
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Mapping
name|mappingUpdate
parameter_list|,
specifier|final
name|TimeValue
name|timeout
parameter_list|,
specifier|final
name|MappingUpdateListener
name|listener
parameter_list|)
block|{
specifier|final
name|PutMappingRequestBuilder
name|request
init|=
name|updateMappingRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|mappingUpdate
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
name|request
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|ActionListener
argument_list|<
name|PutMappingResponse
argument_list|>
name|actionListener
init|=
operator|new
name|ActionListener
argument_list|<
name|PutMappingResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|PutMappingResponse
name|response
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isAcknowledged
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onMappingUpdate
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|TimeoutException
argument_list|(
literal|"Failed to acknowledge the mapping response within ["
operator|+
name|timeout
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|request
operator|.
name|execute
argument_list|(
name|actionListener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateMappingOnMasterAsynchronously
specifier|public
name|void
name|updateMappingOnMasterAsynchronously
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Mapping
name|mappingUpdate
parameter_list|)
throws|throws
name|Throwable
block|{
name|updateMappingOnMaster
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|mappingUpdate
argument_list|,
name|dynamicMappingUpdateTimeout
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Same as {@link #updateMappingOnMasterSynchronously(String, String, String, Mapping, TimeValue)}      * using the default timeout.      */
DECL|method|updateMappingOnMasterSynchronously
specifier|public
name|void
name|updateMappingOnMasterSynchronously
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Mapping
name|mappingUpdate
parameter_list|)
throws|throws
name|Throwable
block|{
name|updateMappingOnMasterSynchronously
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|mappingUpdate
argument_list|,
name|dynamicMappingUpdateTimeout
argument_list|)
expr_stmt|;
block|}
comment|/**      * Update mappings synchronously on the master node, waiting for at most      * {@code timeout}. When this method returns successfully mappings have      * been applied to the master node and propagated to data nodes.      */
DECL|method|updateMappingOnMasterSynchronously
specifier|public
name|void
name|updateMappingOnMasterSynchronously
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|Mapping
name|mappingUpdate
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
throws|throws
name|Throwable
block|{
if|if
condition|(
name|updateMappingRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|mappingUpdate
argument_list|,
name|timeout
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isAcknowledged
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Failed to acknowledge mapping update within ["
operator|+
name|timeout
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * A listener to be notified when the mappings were updated      */
DECL|interface|MappingUpdateListener
specifier|public
specifier|static
interface|interface
name|MappingUpdateListener
block|{
DECL|method|onMappingUpdate
name|void
name|onMappingUpdate
parameter_list|()
function_decl|;
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

