begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|env
operator|.
name|NodeEnvironment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|fs
operator|.
name|FsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmMonitorService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
operator|.
name|OsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|process
operator|.
name|ProcessService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MonitorService
specifier|public
class|class
name|MonitorService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|MonitorService
argument_list|>
block|{
DECL|field|jvmMonitorService
specifier|private
specifier|final
name|JvmMonitorService
name|jvmMonitorService
decl_stmt|;
DECL|field|osService
specifier|private
specifier|final
name|OsService
name|osService
decl_stmt|;
DECL|field|processService
specifier|private
specifier|final
name|ProcessService
name|processService
decl_stmt|;
DECL|field|jvmService
specifier|private
specifier|final
name|JvmService
name|jvmService
decl_stmt|;
DECL|field|fsService
specifier|private
specifier|final
name|FsService
name|fsService
decl_stmt|;
annotation|@
name|Inject
DECL|method|MonitorService
specifier|public
name|MonitorService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeEnvironment
name|nodeEnvironment
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|jvmMonitorService
operator|=
operator|new
name|JvmMonitorService
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|osService
operator|=
operator|new
name|OsService
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|processService
operator|=
operator|new
name|ProcessService
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|jvmService
operator|=
operator|new
name|JvmService
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|fsService
operator|=
operator|new
name|FsService
argument_list|(
name|settings
argument_list|,
name|nodeEnvironment
argument_list|)
expr_stmt|;
block|}
DECL|method|osService
specifier|public
name|OsService
name|osService
parameter_list|()
block|{
return|return
name|this
operator|.
name|osService
return|;
block|}
DECL|method|processService
specifier|public
name|ProcessService
name|processService
parameter_list|()
block|{
return|return
name|this
operator|.
name|processService
return|;
block|}
DECL|method|jvmService
specifier|public
name|JvmService
name|jvmService
parameter_list|()
block|{
return|return
name|this
operator|.
name|jvmService
return|;
block|}
DECL|method|fsService
specifier|public
name|FsService
name|fsService
parameter_list|()
block|{
return|return
name|this
operator|.
name|fsService
return|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
name|jvmMonitorService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{
name|jvmMonitorService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
name|jvmMonitorService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

