begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.cluster.reroute
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|reroute
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
name|admin
operator|.
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteRequest
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
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteResponse
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
name|Requests
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
name|ClusterState
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
name|Strings
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
name|settings
operator|.
name|SettingsFilter
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
name|ToXContent
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|*
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
name|action
operator|.
name|support
operator|.
name|AcknowledgedRestListener
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
comment|/**  */
end_comment

begin_class
DECL|class|RestClusterRerouteAction
specifier|public
class|class
name|RestClusterRerouteAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|settingsFilter
specifier|private
specifier|final
name|SettingsFilter
name|settingsFilter
decl_stmt|;
DECL|field|DEFAULT_METRICS
specifier|private
specifier|static
name|String
name|DEFAULT_METRICS
init|=
name|Strings
operator|.
name|arrayToCommaDelimitedString
argument_list|(
name|EnumSet
operator|.
name|complementOf
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|ClusterState
operator|.
name|Metric
operator|.
name|METADATA
argument_list|)
argument_list|)
operator|.
name|toArray
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|RestClusterRerouteAction
specifier|public
name|RestClusterRerouteAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|,
name|SettingsFilter
name|settingsFilter
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|this
operator|.
name|settingsFilter
operator|=
name|settingsFilter
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|POST
argument_list|,
literal|"/_cluster/reroute"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ClusterRerouteRequest
name|clusterRerouteRequest
init|=
name|Requests
operator|.
name|clusterRerouteRequest
argument_list|()
decl_stmt|;
name|clusterRerouteRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|clusterRerouteRequest
operator|.
name|dryRun
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"dry_run"
argument_list|,
name|clusterRerouteRequest
operator|.
name|dryRun
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|clusterRerouteRequest
operator|.
name|explain
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"explain"
argument_list|,
name|clusterRerouteRequest
operator|.
name|explain
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|clusterRerouteRequest
operator|.
name|timeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"timeout"
argument_list|,
name|clusterRerouteRequest
operator|.
name|timeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|clusterRerouteRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|clusterRerouteRequest
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|hasContent
argument_list|()
condition|)
block|{
name|clusterRerouteRequest
operator|.
name|source
argument_list|(
name|request
operator|.
name|content
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|reroute
argument_list|(
name|clusterRerouteRequest
argument_list|,
operator|new
name|AcknowledgedRestListener
argument_list|<
name|ClusterRerouteResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|addCustomFields
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ClusterRerouteResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"state"
argument_list|)
expr_stmt|;
comment|// by default, return everything but metadata
if|if
condition|(
name|request
operator|.
name|param
argument_list|(
literal|"metric"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|request
operator|.
name|params
argument_list|()
operator|.
name|put
argument_list|(
literal|"metric"
argument_list|,
name|DEFAULT_METRICS
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|getState
argument_list|()
operator|.
name|settingsFilter
argument_list|(
name|settingsFilter
argument_list|)
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|clusterRerouteRequest
operator|.
name|explain
argument_list|()
condition|)
block|{
assert|assert
name|response
operator|.
name|getExplanations
argument_list|()
operator|!=
literal|null
assert|;
name|response
operator|.
name|getExplanations
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

