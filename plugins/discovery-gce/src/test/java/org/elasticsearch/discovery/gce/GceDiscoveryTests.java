begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.gce
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|gce
package|;
end_package

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
name|cloud
operator|.
name|gce
operator|.
name|GceComputeService
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
name|ClusterName
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
name|node
operator|.
name|DiscoveryNode
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
name|network
operator|.
name|NetworkService
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
name|ESTestCase
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
name|transport
operator|.
name|MockTransportService
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
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasSize
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_comment
comment|/**  * This test class uses a GCE HTTP Mock system which allows to simulate JSON Responses.  *  * To implement a new test you'll need to create an `instances.json` file which contains expected response  * for a given project-id and zone under the src/test/resources/org/elasticsearch/discovery/gce with dir name:  *  * compute/v1/projects/[project-id]/zones/[zone]  *  * By default, project-id is the test method name, lowercase and missing the "test" prefix.  *  * For example, if you create a test `myNewAwesomeTest` with following settings:  *  * Settings nodeSettings = Settings.builder()  *  .put(GceComputeService.PROJECT, projectName)  *  .put(GceComputeService.ZONE, "europe-west1-b")  *  .build();  *  *  You need to create a file under `src/test/resources/org/elasticsearch/discovery/gce/` named:  *  *  compute/v1/projects/mynewawesometest/zones/europe-west1-b/instances.json  *  */
end_comment

begin_class
DECL|class|GceDiscoveryTests
specifier|public
class|class
name|GceDiscoveryTests
extends|extends
name|ESTestCase
block|{
DECL|field|threadPool
specifier|protected
specifier|static
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|transportService
specifier|protected
name|MockTransportService
name|transportService
decl_stmt|;
DECL|field|mock
specifier|protected
name|GceComputeService
name|mock
decl_stmt|;
DECL|field|projectName
specifier|protected
name|String
name|projectName
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createThreadPool
specifier|public
specifier|static
name|void
name|createThreadPool
parameter_list|()
block|{
name|threadPool
operator|=
operator|new
name|ThreadPool
argument_list|(
name|GceDiscoveryTests
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|stopThreadPool
specifier|public
specifier|static
name|void
name|stopThreadPool
parameter_list|()
block|{
if|if
condition|(
name|threadPool
operator|!=
literal|null
condition|)
block|{
name|threadPool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|threadPool
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|setProjectName
specifier|public
name|void
name|setProjectName
parameter_list|()
block|{
name|projectName
operator|=
name|getTestName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
comment|// Slice off the "test" part of the method names so the project names
if|if
condition|(
name|projectName
operator|.
name|startsWith
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
name|projectName
operator|=
name|projectName
operator|.
name|substring
argument_list|(
literal|"test"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|createTransportService
specifier|public
name|void
name|createTransportService
parameter_list|()
block|{
name|transportService
operator|=
name|MockTransportService
operator|.
name|local
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|threadPool
argument_list|,
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stopGceComputeService
specifier|public
name|void
name|stopGceComputeService
parameter_list|()
block|{
if|if
condition|(
name|mock
operator|!=
literal|null
condition|)
block|{
name|mock
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|buildDynamicNodes
specifier|protected
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|buildDynamicNodes
parameter_list|(
name|GceComputeService
name|gceComputeService
parameter_list|,
name|Settings
name|nodeSettings
parameter_list|)
block|{
name|GceUnicastHostsProvider
name|provider
init|=
operator|new
name|GceUnicastHostsProvider
argument_list|(
name|nodeSettings
argument_list|,
name|gceComputeService
argument_list|,
name|transportService
argument_list|,
operator|new
name|NetworkService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|provider
operator|.
name|buildDynamicNodes
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> nodes found: {}"
argument_list|,
name|discoveryNodes
argument_list|)
expr_stmt|;
return|return
name|discoveryNodes
return|;
block|}
DECL|method|testNodesWithDifferentTagsAndNoTagSet
specifier|public
name|void
name|testNodesWithDifferentTagsAndNoTagSet
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNodesWithDifferentTagsAndOneTagSet
specifier|public
name|void
name|testNodesWithDifferentTagsAndOneTagSet
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|putArray
argument_list|(
name|GceUnicastHostsProvider
operator|.
name|TAGS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"elasticsearch"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"#cloud-test2-0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNodesWithDifferentTagsAndTwoTagSet
specifier|public
name|void
name|testNodesWithDifferentTagsAndTwoTagSet
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|putArray
argument_list|(
name|GceUnicastHostsProvider
operator|.
name|TAGS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"elasticsearch"
argument_list|,
literal|"dev"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"#cloud-test2-0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNodesWithSameTagsAndNoTagSet
specifier|public
name|void
name|testNodesWithSameTagsAndNoTagSet
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNodesWithSameTagsAndOneTagSet
specifier|public
name|void
name|testNodesWithSameTagsAndOneTagSet
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|putArray
argument_list|(
name|GceUnicastHostsProvider
operator|.
name|TAGS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"elasticsearch"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNodesWithSameTagsAndTwoTagsSet
specifier|public
name|void
name|testNodesWithSameTagsAndTwoTagsSet
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|putArray
argument_list|(
name|GceUnicastHostsProvider
operator|.
name|TAGS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"elasticsearch"
argument_list|,
literal|"dev"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleZonesAndTwoNodesInSameZone
specifier|public
name|void
name|testMultipleZonesAndTwoNodesInSameZone
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|putArray
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"us-central1-a"
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleZonesAndTwoNodesInDifferentZones
specifier|public
name|void
name|testMultipleZonesAndTwoNodesInDifferentZones
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|putArray
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"us-central1-a"
argument_list|,
literal|"europe-west1-b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * For issue https://github.com/elastic/elasticsearch-cloud-gce/issues/43      */
DECL|method|testZeroNode43
specifier|public
name|void
name|testZeroNode43
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|putArray
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"us-central1-a"
argument_list|,
literal|"us-central1-b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNodes
argument_list|,
name|hasSize
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalSettingsMissingAllRequired
specifier|public
name|void
name|testIllegalSettingsMissingAllRequired
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
try|try
block|{
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We expect an IllegalArgumentException for incomplete settings"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"one or more gce discovery settings are missing."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalSettingsMissingProject
specifier|public
name|void
name|testIllegalSettingsMissingProject
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|putArray
argument_list|(
name|GceComputeService
operator|.
name|ZONE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"us-central1-a"
argument_list|,
literal|"us-central1-b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
try|try
block|{
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We expect an IllegalArgumentException for incomplete settings"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"one or more gce discovery settings are missing."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalSettingsMissingZone
specifier|public
name|void
name|testIllegalSettingsMissingZone
parameter_list|()
block|{
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|GceComputeService
operator|.
name|PROJECT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|projectName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|mock
operator|=
operator|new
name|GceComputeServiceMock
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
try|try
block|{
name|buildDynamicNodes
argument_list|(
name|mock
argument_list|,
name|nodeSettings
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We expect an IllegalArgumentException for incomplete settings"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"one or more gce discovery settings are missing."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

