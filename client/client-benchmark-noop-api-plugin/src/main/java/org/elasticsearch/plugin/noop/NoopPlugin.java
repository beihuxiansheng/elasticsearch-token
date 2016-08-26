begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.noop
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|noop
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|noop
operator|.
name|action
operator|.
name|bulk
operator|.
name|NoopBulkAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|noop
operator|.
name|action
operator|.
name|bulk
operator|.
name|RestNoopBulkAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|noop
operator|.
name|action
operator|.
name|bulk
operator|.
name|TransportNoopBulkAction
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
name|plugin
operator|.
name|noop
operator|.
name|action
operator|.
name|search
operator|.
name|NoopSearchAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|noop
operator|.
name|action
operator|.
name|search
operator|.
name|RestNoopSearchAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|noop
operator|.
name|action
operator|.
name|search
operator|.
name|TransportNoopSearchAction
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

begin_class
DECL|class|NoopPlugin
specifier|public
class|class
name|NoopPlugin
extends|extends
name|Plugin
implements|implements
name|ActionPlugin
block|{
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
argument_list|<
name|?
argument_list|>
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
name|NoopBulkAction
operator|.
name|INSTANCE
argument_list|,
name|TransportNoopBulkAction
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|ActionHandler
argument_list|<>
argument_list|(
name|NoopSearchAction
operator|.
name|INSTANCE
argument_list|,
name|TransportNoopSearchAction
operator|.
name|class
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
name|RestNoopBulkAction
operator|.
name|class
argument_list|,
name|RestNoopSearchAction
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

