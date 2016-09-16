begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
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
name|transport
operator|.
name|LocalTransportAddress
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
name|ElectMasterService
operator|.
name|MasterCandidate
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|equalTo
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
name|greaterThan
import|;
end_import

begin_class
DECL|class|ElectMasterServiceTests
specifier|public
class|class
name|ElectMasterServiceTests
extends|extends
name|ESTestCase
block|{
DECL|method|electMasterService
name|ElectMasterService
name|electMasterService
parameter_list|()
block|{
return|return
operator|new
name|ElectMasterService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
return|;
block|}
DECL|method|generateRandomNodes
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|generateRandomNodes
parameter_list|()
block|{
name|int
name|count
init|=
name|scaledRandomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|count
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Set
argument_list|<
name|DiscoveryNode
operator|.
name|Role
argument_list|>
name|roles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|roles
operator|.
name|add
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|MASTER
argument_list|)
expr_stmt|;
block|}
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"n_"
operator|+
name|i
argument_list|,
literal|"n_"
operator|+
name|i
argument_list|,
name|LocalTransportAddress
operator|.
name|buildUnique
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|roles
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|nodes
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nodes
return|;
block|}
DECL|method|generateRandomCandidates
name|List
argument_list|<
name|MasterCandidate
argument_list|>
name|generateRandomCandidates
parameter_list|()
block|{
name|int
name|count
init|=
name|scaledRandomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|MasterCandidate
argument_list|>
name|candidates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|count
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Set
argument_list|<
name|DiscoveryNode
operator|.
name|Role
argument_list|>
name|roles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|roles
operator|.
name|add
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|MASTER
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"n_"
operator|+
name|i
argument_list|,
literal|"n_"
operator|+
name|i
argument_list|,
name|LocalTransportAddress
operator|.
name|buildUnique
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|roles
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|candidates
operator|.
name|add
argument_list|(
operator|new
name|MasterCandidate
argument_list|(
name|node
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|MasterCandidate
operator|.
name|UNRECOVERED_CLUSTER_VERSION
else|:
name|randomPositiveLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|candidates
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|candidates
return|;
block|}
DECL|method|testSortByMasterLikelihood
specifier|public
name|void
name|testSortByMasterLikelihood
parameter_list|()
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
name|generateRandomNodes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|sortedNodes
init|=
name|electMasterService
argument_list|()
operator|.
name|sortByMasterLikelihood
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|,
name|sortedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|prevNode
init|=
name|sortedNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|sortedNodes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|sortedNodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|prevNode
operator|.
name|isMasterNode
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|node
operator|.
name|isMasterNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isMasterNode
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|prevNode
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
name|prevNode
operator|=
name|node
expr_stmt|;
block|}
block|}
DECL|method|testTieBreakActiveMasters
specifier|public
name|void
name|testTieBreakActiveMasters
parameter_list|()
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
name|generateRandomCandidates
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|MasterCandidate
operator|::
name|getNode
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|bestMaster
init|=
name|electMasterService
argument_list|()
operator|.
name|tieBreakActiveMasters
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|node
operator|.
name|equals
argument_list|(
name|bestMaster
argument_list|)
operator|==
literal|false
condition|)
block|{
name|assertTrue
argument_list|(
name|bestMaster
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testHasEnoughNodes
specifier|public
name|void
name|testHasEnoughNodes
parameter_list|()
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
name|rarely
argument_list|()
condition|?
name|Collections
operator|.
name|emptyList
argument_list|()
else|:
name|generateRandomNodes
argument_list|()
decl_stmt|;
name|ElectMasterService
name|service
init|=
name|electMasterService
argument_list|()
decl_stmt|;
name|int
name|masterNodes
init|=
operator|(
name|int
operator|)
name|nodes
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|DiscoveryNode
operator|::
name|isMasterNode
argument_list|)
operator|.
name|count
argument_list|()
decl_stmt|;
name|service
operator|.
name|minimumMasterNodes
argument_list|(
name|randomIntBetween
argument_list|(
operator|-
literal|1
argument_list|,
name|masterNodes
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|service
operator|.
name|hasEnoughMasterNodes
argument_list|(
name|nodes
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|masterNodes
operator|>
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|minimumMasterNodes
argument_list|(
name|masterNodes
operator|+
literal|1
operator|+
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|service
operator|.
name|hasEnoughMasterNodes
argument_list|(
name|nodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHasEnoughCandidates
specifier|public
name|void
name|testHasEnoughCandidates
parameter_list|()
block|{
name|List
argument_list|<
name|MasterCandidate
argument_list|>
name|candidates
init|=
name|rarely
argument_list|()
condition|?
name|Collections
operator|.
name|emptyList
argument_list|()
else|:
name|generateRandomCandidates
argument_list|()
decl_stmt|;
name|ElectMasterService
name|service
init|=
name|electMasterService
argument_list|()
decl_stmt|;
name|service
operator|.
name|minimumMasterNodes
argument_list|(
name|randomIntBetween
argument_list|(
operator|-
literal|1
argument_list|,
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|service
operator|.
name|hasEnoughCandidates
argument_list|(
name|candidates
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|minimumMasterNodes
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|+
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|service
operator|.
name|hasEnoughCandidates
argument_list|(
name|candidates
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testElectMaster
specifier|public
name|void
name|testElectMaster
parameter_list|()
block|{
name|List
argument_list|<
name|MasterCandidate
argument_list|>
name|candidates
init|=
name|generateRandomCandidates
argument_list|()
decl_stmt|;
name|ElectMasterService
name|service
init|=
name|electMasterService
argument_list|()
decl_stmt|;
name|int
name|minMasterNodes
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|service
operator|.
name|minimumMasterNodes
argument_list|(
name|minMasterNodes
argument_list|)
expr_stmt|;
name|MasterCandidate
name|master
init|=
name|service
operator|.
name|electMaster
argument_list|(
name|candidates
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|master
argument_list|)
expr_stmt|;
for|for
control|(
name|MasterCandidate
name|candidate
range|:
name|candidates
control|)
block|{
if|if
condition|(
name|candidate
operator|.
name|getNode
argument_list|()
operator|.
name|equals
argument_list|(
name|master
operator|.
name|getNode
argument_list|()
argument_list|)
condition|)
block|{
comment|// nothing much to test here
block|}
elseif|else
if|if
condition|(
name|candidate
operator|.
name|getClusterStateVersion
argument_list|()
operator|==
name|master
operator|.
name|getClusterStateVersion
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
literal|"candidate "
operator|+
name|candidate
operator|+
literal|" has a lower or equal id than master "
operator|+
name|master
argument_list|,
name|candidate
operator|.
name|getNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
name|master
operator|.
name|getNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
literal|"candidate "
operator|+
name|master
operator|+
literal|" has a higher cluster state version than candidate "
operator|+
name|candidate
argument_list|,
name|master
operator|.
name|getClusterStateVersion
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
name|candidate
operator|.
name|getClusterStateVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

