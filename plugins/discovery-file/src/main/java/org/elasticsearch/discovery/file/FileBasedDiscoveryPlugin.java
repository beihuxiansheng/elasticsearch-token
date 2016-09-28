begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.file
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|file
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|logging
operator|.
name|Loggers
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
name|discovery
operator|.
name|DiscoveryModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|DiscoveryPlugin
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

begin_comment
comment|/**  * Plugin for providing file-based unicast hosts discovery. The list of unicast hosts  * is obtained by reading the {@link FileBasedUnicastHostsProvider#UNICAST_HOSTS_FILE} in  * the {@link Environment#configFile()}/discovery-file directory.  */
end_comment

begin_class
DECL|class|FileBasedDiscoveryPlugin
specifier|public
class|class
name|FileBasedDiscoveryPlugin
extends|extends
name|Plugin
implements|implements
name|DiscoveryPlugin
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|FileBasedDiscoveryPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|method|FileBasedDiscoveryPlugin
specifier|public
name|FileBasedDiscoveryPlugin
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"starting file-based discovery plugin..."
argument_list|)
expr_stmt|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|DiscoveryModule
name|discoveryModule
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"registering file-based unicast hosts provider"
argument_list|)
expr_stmt|;
comment|// using zen discovery for the discovery type and we're just adding a unicast host provider for it
name|discoveryModule
operator|.
name|addUnicastHostProvider
argument_list|(
literal|"zen"
argument_list|,
name|FileBasedUnicastHostsProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
