begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticsearchException
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
name|river
operator|.
name|cluster
operator|.
name|RiverClusterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|routing
operator|.
name|RiversRouter
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RiversManager
specifier|public
class|class
name|RiversManager
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|RiversManager
argument_list|>
block|{
DECL|field|riversService
specifier|private
specifier|final
name|RiversService
name|riversService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|RiverClusterService
name|clusterService
decl_stmt|;
DECL|field|riversRouter
specifier|private
specifier|final
name|RiversRouter
name|riversRouter
decl_stmt|;
annotation|@
name|Inject
DECL|method|RiversManager
specifier|public
name|RiversManager
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RiversService
name|riversService
parameter_list|,
name|RiverClusterService
name|clusterService
parameter_list|,
name|RiversRouter
name|riversRouter
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|riversService
operator|=
name|riversService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|riversRouter
operator|=
name|riversRouter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|riversRouter
operator|.
name|start
argument_list|()
expr_stmt|;
name|riversService
operator|.
name|start
argument_list|()
expr_stmt|;
name|clusterService
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
throws|throws
name|ElasticsearchException
block|{
name|riversRouter
operator|.
name|stop
argument_list|()
expr_stmt|;
name|clusterService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|riversService
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
throws|throws
name|ElasticsearchException
block|{
name|riversRouter
operator|.
name|close
argument_list|()
expr_stmt|;
name|clusterService
operator|.
name|close
argument_list|()
expr_stmt|;
name|riversService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

