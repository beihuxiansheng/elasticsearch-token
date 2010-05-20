begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.os
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JmxOsProbe
specifier|public
class|class
name|JmxOsProbe
extends|extends
name|AbstractComponent
implements|implements
name|OsProbe
block|{
DECL|method|JmxOsProbe
annotation|@
name|Inject
specifier|public
name|JmxOsProbe
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|osInfo
annotation|@
name|Override
specifier|public
name|OsInfo
name|osInfo
parameter_list|()
block|{
return|return
operator|new
name|OsInfo
argument_list|()
return|;
block|}
DECL|method|osStats
annotation|@
name|Override
specifier|public
name|OsStats
name|osStats
parameter_list|()
block|{
name|OsStats
name|stats
init|=
operator|new
name|OsStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|timestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|method|ifconfig
annotation|@
name|Override
specifier|public
name|String
name|ifconfig
parameter_list|()
block|{
return|return
literal|"NA"
return|;
block|}
block|}
end_class

end_unit

