begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
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
name|inject
operator|.
name|AbstractModule
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
name|Module
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
name|PreProcessModule
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
name|plugins
operator|.
name|PluginsService
import|;
end_import

begin_comment
comment|/**  * A module that simply calls the {@link PluginsService#processModule(org.elasticsearch.common.inject.Module)}  * in order to allow plugins to pre process specific river modules.  */
end_comment

begin_class
DECL|class|RiversPluginsModule
specifier|public
class|class
name|RiversPluginsModule
extends|extends
name|AbstractModule
implements|implements
name|PreProcessModule
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|pluginsService
specifier|private
specifier|final
name|PluginsService
name|pluginsService
decl_stmt|;
DECL|method|RiversPluginsModule
specifier|public
name|RiversPluginsModule
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|PluginsService
name|pluginsService
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|pluginsService
operator|=
name|pluginsService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processModule
specifier|public
name|void
name|processModule
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
name|pluginsService
operator|.
name|processModule
argument_list|(
name|module
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{     }
block|}
end_class

end_unit

