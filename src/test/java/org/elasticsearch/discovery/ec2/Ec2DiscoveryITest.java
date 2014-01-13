begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.ec2
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|ec2
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
operator|.
name|AbstractAwsTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
operator|.
name|AbstractAwsTest
operator|.
name|AwsTest
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
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
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
operator|.
name|Scope
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
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
import|;
end_import

begin_comment
comment|/**  * Just an empty Node Start test to check eveything if fine when  * starting.  * This test requires AWS to run.  */
end_comment

begin_class
annotation|@
name|AwsTest
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numNodes
operator|=
literal|0
argument_list|)
DECL|class|Ec2DiscoveryITest
specifier|public
class|class
name|Ec2DiscoveryITest
extends|extends
name|AbstractAwsTest
block|{
annotation|@
name|Test
DECL|method|testStart
specifier|public
name|void
name|testStart
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cloud.enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.type"
argument_list|,
literal|"ec2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

