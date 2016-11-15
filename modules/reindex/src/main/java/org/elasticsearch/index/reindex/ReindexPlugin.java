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
name|ActionRequest
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
name|ActionResponse
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|plugins
operator|.
name|ActionPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|RestHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_class
DECL|class|ReindexPlugin
specifier|public
class|class
name|ReindexPlugin
extends|extends
name|Plugin
implements|implements
name|ActionPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"reindex"
decl_stmt|;
annotation|@
name|Override
DECL|method|getActions
specifier|public
name|List
argument_list|<
name|ActionHandler
argument_list|<
name|?
extends|extends
name|ActionRequest
argument_list|,
name|?
extends|extends
name|ActionResponse
argument_list|>
argument_list|>
name|getActions
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ActionHandler
argument_list|<>
argument_list|(
name|ReindexAction
operator|.
name|INSTANCE
argument_list|,
name|TransportReindexAction
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|ActionHandler
argument_list|<>
argument_list|(
name|UpdateByQueryAction
operator|.
name|INSTANCE
argument_list|,
name|TransportUpdateByQueryAction
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|ActionHandler
argument_list|<>
argument_list|(
name|DeleteByQueryAction
operator|.
name|INSTANCE
argument_list|,
name|TransportDeleteByQueryAction
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|ActionHandler
argument_list|<>
argument_list|(
name|RethrottleAction
operator|.
name|INSTANCE
argument_list|,
name|TransportRethrottleAction
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNamedWriteables
specifier|public
name|List
argument_list|<
name|NamedWriteableRegistry
operator|.
name|Entry
argument_list|>
name|getNamedWriteables
parameter_list|()
block|{
return|return
name|singletonList
argument_list|(
operator|new
name|NamedWriteableRegistry
operator|.
name|Entry
argument_list|(
name|Task
operator|.
name|Status
operator|.
name|class
argument_list|,
name|BulkByScrollTask
operator|.
name|Status
operator|.
name|NAME
argument_list|,
name|BulkByScrollTask
operator|.
name|Status
operator|::
operator|new
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRestHandlers
specifier|public
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RestHandler
argument_list|>
argument_list|>
name|getRestHandlers
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|RestReindexAction
operator|.
name|class
argument_list|,
name|RestUpdateByQueryAction
operator|.
name|class
argument_list|,
name|RestDeleteByQueryAction
operator|.
name|class
argument_list|,
name|RestRethrottleAction
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSettings
specifier|public
name|List
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|getSettings
parameter_list|()
block|{
return|return
name|singletonList
argument_list|(
name|TransportReindexAction
operator|.
name|REMOTE_CLUSTER_WHITELIST
argument_list|)
return|;
block|}
block|}
end_class

end_unit

