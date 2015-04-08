begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|get
operator|.
name|GetIndexAction
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
name|repositories
operator|.
name|verify
operator|.
name|VerifyRepositoryAction
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
name|exists
operator|.
name|ExistsAction
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
name|zen
operator|.
name|ping
operator|.
name|unicast
operator|.
name|UnicastZenPing
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|action
operator|.
name|SearchServiceTransportAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|VerifyNodeRepositoryAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|either
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|startsWith
import|;
end_import

begin_comment
comment|/**  * This test verifies that all of the action names follow our defined naming conventions.  * The identified categories are:  * - indices:admin: apis that allow to perform administration tasks against indices  * - indices:data: apis that are about data  * - indices:read: apis that read data  * - indices:write: apis that write data  * - cluster:admin: cluster apis that allow to perform administration tasks  * - cluster:monitor: cluster apis that allow to monitor the system  * - internal: internal actions that are used from node to node but not directly exposed to users  *  * Any transport action belongs to one of the above categories and its name starts with its category, followed by a '/'  * and the name of the api itself (e.g. cluster:admin/nodes/restart).  * When an api exposes multiple transport handlers, some of which are invoked internally during the execution of the api,  * we use the `[n]` suffix to identify node actions and the `[s]` suffix to identify shard actions.  */
end_comment

begin_class
DECL|class|ActionNamesTests
specifier|public
class|class
name|ActionNamesTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testActionNamesCategories
specifier|public
name|void
name|testActionNamesCategories
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|TransportService
name|transportService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|action
range|:
name|transportService
operator|.
name|serverHandlers
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
literal|"action doesn't belong to known category"
argument_list|,
name|action
argument_list|,
name|either
argument_list|(
name|startsWith
argument_list|(
literal|"indices:admin"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|startsWith
argument_list|(
literal|"indices:monitor"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|startsWith
argument_list|(
literal|"indices:data/read"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|startsWith
argument_list|(
literal|"indices:data/write"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|startsWith
argument_list|(
literal|"cluster:admin"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|startsWith
argument_list|(
literal|"cluster:monitor"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|startsWith
argument_list|(
literal|"internal:"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

