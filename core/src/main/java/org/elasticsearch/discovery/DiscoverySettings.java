begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|block
operator|.
name|ClusterBlock
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
name|block
operator|.
name|ClusterBlockLevel
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
name|Setting
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
name|settings
operator|.
name|ClusterSettingsService
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_comment
comment|/**  * Exposes common discovery settings that may be supported by all the different discovery implementations  */
end_comment

begin_class
DECL|class|DiscoverySettings
specifier|public
class|class
name|DiscoverySettings
extends|extends
name|AbstractComponent
block|{
comment|/**      * sets the timeout for a complete publishing cycle, including both sending and committing. the master      * will continute to process the next cluster state update after this time has elapsed      **/
DECL|field|PUBLISH_TIMEOUT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|PUBLISH_TIMEOUT_SETTING
init|=
name|Setting
operator|.
name|positiveTimeSetting
argument_list|(
literal|"discovery.zen.publish_timeout"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|Cluster
argument_list|)
decl_stmt|;
comment|/**      * sets the timeout for receiving enough acks for a specific cluster state and committing it. failing      * to receive responses within this window will cause the cluster state change to be rejected.      */
DECL|field|COMMIT_TIMEOUT_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|COMMIT_TIMEOUT_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"discovery.zen.commit_timeout"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|PUBLISH_TIMEOUT_SETTING
operator|.
name|getRaw
argument_list|(
name|s
argument_list|)
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|s
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|,
literal|"discovery.zen.commit_timeout"
argument_list|)
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|Cluster
argument_list|)
decl_stmt|;
DECL|field|NO_MASTER_BLOCK_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|ClusterBlock
argument_list|>
name|NO_MASTER_BLOCK_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"discovery.zen.no_master_block"
argument_list|,
literal|"write"
argument_list|,
name|DiscoverySettings
operator|::
name|parseNoMasterBlock
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|Cluster
argument_list|)
decl_stmt|;
DECL|field|PUBLISH_DIFF_ENABLE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|PUBLISH_DIFF_ENABLE_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"discovery.zen.publish_diff.enable"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|Cluster
argument_list|)
decl_stmt|;
DECL|field|NO_MASTER_BLOCK_ID
specifier|public
specifier|final
specifier|static
name|int
name|NO_MASTER_BLOCK_ID
init|=
literal|2
decl_stmt|;
DECL|field|NO_MASTER_BLOCK_ALL
specifier|public
specifier|final
specifier|static
name|ClusterBlock
name|NO_MASTER_BLOCK_ALL
init|=
operator|new
name|ClusterBlock
argument_list|(
name|NO_MASTER_BLOCK_ID
argument_list|,
literal|"no master"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
name|ClusterBlockLevel
operator|.
name|ALL
argument_list|)
decl_stmt|;
DECL|field|NO_MASTER_BLOCK_WRITES
specifier|public
specifier|final
specifier|static
name|ClusterBlock
name|NO_MASTER_BLOCK_WRITES
init|=
operator|new
name|ClusterBlock
argument_list|(
name|NO_MASTER_BLOCK_ID
argument_list|,
literal|"no master"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|,
name|ClusterBlockLevel
operator|.
name|METADATA_WRITE
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|noMasterBlock
specifier|private
specifier|volatile
name|ClusterBlock
name|noMasterBlock
decl_stmt|;
DECL|field|publishTimeout
specifier|private
specifier|volatile
name|TimeValue
name|publishTimeout
decl_stmt|;
DECL|field|commitTimeout
specifier|private
specifier|volatile
name|TimeValue
name|commitTimeout
decl_stmt|;
DECL|field|publishDiff
specifier|private
specifier|volatile
name|boolean
name|publishDiff
decl_stmt|;
annotation|@
name|Inject
DECL|method|DiscoverySettings
specifier|public
name|DiscoverySettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterSettingsService
name|clusterSettingsService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|clusterSettingsService
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|NO_MASTER_BLOCK_SETTING
argument_list|,
name|this
operator|::
name|setNoMasterBlock
argument_list|)
expr_stmt|;
name|clusterSettingsService
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|PUBLISH_DIFF_ENABLE_SETTING
argument_list|,
name|this
operator|::
name|setPublishDiff
argument_list|)
expr_stmt|;
name|clusterSettingsService
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|COMMIT_TIMEOUT_SETTING
argument_list|,
name|this
operator|::
name|setCommitTimeout
argument_list|)
expr_stmt|;
name|clusterSettingsService
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|PUBLISH_TIMEOUT_SETTING
argument_list|,
name|this
operator|::
name|setPublishTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|noMasterBlock
operator|=
name|NO_MASTER_BLOCK_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|publishTimeout
operator|=
name|PUBLISH_TIMEOUT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|commitTimeout
operator|=
name|COMMIT_TIMEOUT_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|publishDiff
operator|=
name|PUBLISH_DIFF_ENABLE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the current publish timeout      */
DECL|method|getPublishTimeout
specifier|public
name|TimeValue
name|getPublishTimeout
parameter_list|()
block|{
return|return
name|publishTimeout
return|;
block|}
DECL|method|getCommitTimeout
specifier|public
name|TimeValue
name|getCommitTimeout
parameter_list|()
block|{
return|return
name|commitTimeout
return|;
block|}
DECL|method|getNoMasterBlock
specifier|public
name|ClusterBlock
name|getNoMasterBlock
parameter_list|()
block|{
return|return
name|noMasterBlock
return|;
block|}
DECL|method|setNoMasterBlock
specifier|private
name|void
name|setNoMasterBlock
parameter_list|(
name|ClusterBlock
name|noMasterBlock
parameter_list|)
block|{
name|this
operator|.
name|noMasterBlock
operator|=
name|noMasterBlock
expr_stmt|;
block|}
DECL|method|setPublishDiff
specifier|private
name|void
name|setPublishDiff
parameter_list|(
name|boolean
name|publishDiff
parameter_list|)
block|{
name|this
operator|.
name|publishDiff
operator|=
name|publishDiff
expr_stmt|;
block|}
DECL|method|setPublishTimeout
specifier|private
name|void
name|setPublishTimeout
parameter_list|(
name|TimeValue
name|publishTimeout
parameter_list|)
block|{
name|this
operator|.
name|publishTimeout
operator|=
name|publishTimeout
expr_stmt|;
block|}
DECL|method|setCommitTimeout
specifier|private
name|void
name|setCommitTimeout
parameter_list|(
name|TimeValue
name|commitTimeout
parameter_list|)
block|{
name|this
operator|.
name|commitTimeout
operator|=
name|commitTimeout
expr_stmt|;
block|}
DECL|method|getPublishDiff
specifier|public
name|boolean
name|getPublishDiff
parameter_list|()
block|{
return|return
name|publishDiff
return|;
block|}
DECL|method|parseNoMasterBlock
specifier|private
specifier|static
name|ClusterBlock
name|parseNoMasterBlock
parameter_list|(
name|String
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|"all"
case|:
return|return
name|NO_MASTER_BLOCK_ALL
return|;
case|case
literal|"write"
case|:
return|return
name|NO_MASTER_BLOCK_WRITES
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid master block ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

