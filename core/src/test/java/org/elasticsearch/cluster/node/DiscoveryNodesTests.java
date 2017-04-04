begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
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
name|Arrays
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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|CoreMatchers
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
name|CoreMatchers
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
name|containsInAnyOrder
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
name|nullValue
import|;
end_import

begin_class
DECL|class|DiscoveryNodesTests
specifier|public
class|class
name|DiscoveryNodesTests
extends|extends
name|ESTestCase
block|{
DECL|method|testResolveNodeByIdOrName
specifier|public
name|void
name|testResolveNodeByIdOrName
parameter_list|()
block|{
name|DiscoveryNodes
name|discoveryNodes
init|=
name|buildDiscoveryNodes
argument_list|()
decl_stmt|;
name|DiscoveryNode
index|[]
name|nodes
init|=
name|discoveryNodes
operator|.
name|getNodes
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
name|DiscoveryNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|node
init|=
name|randomFrom
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|resolvedNode
init|=
name|discoveryNodes
operator|.
name|resolveNode
argument_list|(
name|randomBoolean
argument_list|()
condition|?
name|node
operator|.
name|getId
argument_list|()
else|:
name|node
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resolvedNode
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testResolveNodeByAttribute
specifier|public
name|void
name|testResolveNodeByAttribute
parameter_list|()
block|{
name|DiscoveryNodes
name|discoveryNodes
init|=
name|buildDiscoveryNodes
argument_list|()
decl_stmt|;
name|NodeSelector
name|nodeSelector
init|=
name|randomFrom
argument_list|(
name|NodeSelector
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
init|=
name|nodeSelector
operator|.
name|matchingNodeIds
argument_list|(
name|discoveryNodes
argument_list|)
decl_stmt|;
try|try
block|{
name|DiscoveryNode
name|resolvedNode
init|=
name|discoveryNodes
operator|.
name|resolveNode
argument_list|(
name|nodeSelector
operator|.
name|selector
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|matchingNodeIds
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolvedNode
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|matchingNodeIds
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|matchingNodeIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"failed to resolve ["
operator|+
name|nodeSelector
operator|.
name|selector
operator|+
literal|"], no matching nodes"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matchingNodeIds
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"where expected to be resolved to a single node"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"resolveNode shouldn't have failed for ["
operator|+
name|nodeSelector
operator|.
name|selector
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testResolveNodesIds
specifier|public
name|void
name|testResolveNodesIds
parameter_list|()
block|{
name|DiscoveryNodes
name|discoveryNodes
init|=
name|buildDiscoveryNodes
argument_list|()
decl_stmt|;
name|int
name|numSelectors
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSelectors
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedNodeIdsSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|numSelectors
condition|;
name|i
operator|++
control|)
block|{
name|NodeSelector
name|nodeSelector
init|=
name|randomFrom
argument_list|(
name|NodeSelector
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeSelectors
operator|.
name|add
argument_list|(
name|nodeSelector
operator|.
name|selector
argument_list|)
condition|)
block|{
name|expectedNodeIdsSet
operator|.
name|addAll
argument_list|(
name|nodeSelector
operator|.
name|matchingNodeIds
argument_list|(
name|discoveryNodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|numNodeIds
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|String
index|[]
name|nodeIds
init|=
name|discoveryNodes
operator|.
name|getNodes
argument_list|()
operator|.
name|keys
argument_list|()
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
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
name|numNodeIds
condition|;
name|i
operator|++
control|)
block|{
name|String
name|nodeId
init|=
name|randomFrom
argument_list|(
name|nodeIds
argument_list|)
decl_stmt|;
name|nodeSelectors
operator|.
name|add
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|expectedNodeIdsSet
operator|.
name|add
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
name|int
name|numNodeNames
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|DiscoveryNode
index|[]
name|nodes
init|=
name|discoveryNodes
operator|.
name|getNodes
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
name|DiscoveryNode
operator|.
name|class
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
name|numNodeNames
condition|;
name|i
operator|++
control|)
block|{
name|DiscoveryNode
name|discoveryNode
init|=
name|randomFrom
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|nodeSelectors
operator|.
name|add
argument_list|(
name|discoveryNode
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|expectedNodeIdsSet
operator|.
name|add
argument_list|(
name|discoveryNode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|resolvedNodesIds
init|=
name|discoveryNodes
operator|.
name|resolveNodes
argument_list|(
name|nodeSelectors
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|nodeSelectors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|resolvedNodesIds
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedNodesIds
init|=
name|expectedNodeIdsSet
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|expectedNodeIdsSet
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|expectedNodesIds
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolvedNodesIds
argument_list|,
name|equalTo
argument_list|(
name|expectedNodesIds
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeltas
specifier|public
name|void
name|testDeltas
parameter_list|()
block|{
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesA
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodesA
operator|.
name|addAll
argument_list|(
name|randomNodes
argument_list|(
literal|1
operator|+
name|randomInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesB
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodesB
operator|.
name|addAll
argument_list|(
name|randomNodes
argument_list|(
literal|1
operator|+
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|randomSubsetOf
argument_list|(
name|nodesA
argument_list|)
control|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// change an attribute
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|)
decl_stmt|;
name|attrs
operator|.
name|put
argument_list|(
literal|"new"
argument_list|,
literal|"new"
argument_list|)
expr_stmt|;
name|node
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|node
operator|.
name|getName
argument_list|()
argument_list|,
name|node
operator|.
name|getId
argument_list|()
argument_list|,
name|node
operator|.
name|getAddress
argument_list|()
argument_list|,
name|attrs
argument_list|,
name|node
operator|.
name|getRoles
argument_list|()
argument_list|,
name|node
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nodesB
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|DiscoveryNode
name|masterA
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|nodesA
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|masterB
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|nodesB
argument_list|)
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|builderA
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nodesA
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|builderA
operator|::
name|add
argument_list|)
expr_stmt|;
specifier|final
name|String
name|masterAId
init|=
name|masterA
operator|==
literal|null
condition|?
literal|null
else|:
name|masterA
operator|.
name|getId
argument_list|()
decl_stmt|;
name|builderA
operator|.
name|masterNodeId
argument_list|(
name|masterAId
argument_list|)
expr_stmt|;
name|builderA
operator|.
name|localNodeId
argument_list|(
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|nodesA
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|builderB
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nodesB
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|builderB
operator|::
name|add
argument_list|)
expr_stmt|;
specifier|final
name|String
name|masterBId
init|=
name|masterB
operator|==
literal|null
condition|?
literal|null
else|:
name|masterB
operator|.
name|getId
argument_list|()
decl_stmt|;
name|builderB
operator|.
name|masterNodeId
argument_list|(
name|masterBId
argument_list|)
expr_stmt|;
name|builderB
operator|.
name|localNodeId
argument_list|(
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|nodesB
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DiscoveryNodes
name|discoNodesA
init|=
name|builderA
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNodes
name|discoNodesB
init|=
name|builderB
operator|.
name|build
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"nodes A: {}"
argument_list|,
name|discoNodesA
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"nodes B: {}"
argument_list|,
name|discoNodesB
argument_list|)
expr_stmt|;
name|DiscoveryNodes
operator|.
name|Delta
name|delta
init|=
name|discoNodesB
operator|.
name|delta
argument_list|(
name|discoNodesA
argument_list|)
decl_stmt|;
if|if
condition|(
name|masterB
operator|==
literal|null
operator|||
name|Objects
operator|.
name|equals
argument_list|(
name|masterAId
argument_list|,
name|masterBId
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|delta
operator|.
name|masterNodeChanged
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|previousMasterNode
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|newMasterNode
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|delta
operator|.
name|masterNodeChanged
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|newMasterNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|masterBId
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|previousMasterNode
argument_list|()
operator|!=
literal|null
condition|?
name|delta
operator|.
name|previousMasterNode
argument_list|()
operator|.
name|getId
argument_list|()
else|:
literal|null
argument_list|,
name|equalTo
argument_list|(
name|masterAId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|newNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|nodesB
argument_list|)
decl_stmt|;
name|newNodes
operator|.
name|removeAll
argument_list|(
name|nodesA
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|added
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|newNodes
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|addedNodes
argument_list|()
argument_list|,
name|containsInAnyOrder
argument_list|(
name|newNodes
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|addedNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|newNodes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|removedNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|nodesA
argument_list|)
decl_stmt|;
name|removedNodes
operator|.
name|removeAll
argument_list|(
name|nodesB
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|removed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|removedNodes
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|removedNodes
argument_list|()
argument_list|,
name|containsInAnyOrder
argument_list|(
name|removedNodes
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|delta
operator|.
name|removedNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|removedNodes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|idGenerator
specifier|private
specifier|static
name|AtomicInteger
name|idGenerator
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|randomNodes
specifier|private
specifier|static
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|randomNodes
parameter_list|(
specifier|final
name|int
name|numNodes
parameter_list|)
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|frequently
argument_list|()
condition|)
block|{
name|attributes
operator|.
name|put
argument_list|(
literal|"custom"
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|"match"
else|:
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DiscoveryNode
name|node
init|=
name|newNode
argument_list|(
name|idGenerator
operator|.
name|getAndIncrement
argument_list|()
argument_list|,
name|attributes
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|randomSubsetOf
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|nodesList
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|nodesList
return|;
block|}
DECL|method|buildDiscoveryNodes
specifier|private
specifier|static
name|DiscoveryNodes
name|buildDiscoveryNodes
parameter_list|()
block|{
name|int
name|numNodes
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|discoBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesList
init|=
name|randomNodes
argument_list|(
name|numNodes
argument_list|)
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodesList
control|)
block|{
name|discoBuilder
operator|=
name|discoBuilder
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|discoBuilder
operator|.
name|localNodeId
argument_list|(
name|randomFrom
argument_list|(
name|nodesList
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|masterNodeId
argument_list|(
name|randomFrom
argument_list|(
name|nodesList
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|discoBuilder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|newNode
specifier|private
specifier|static
name|DiscoveryNode
name|newNode
parameter_list|(
name|int
name|nodeId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|,
name|Set
argument_list|<
name|DiscoveryNode
operator|.
name|Role
argument_list|>
name|roles
parameter_list|)
block|{
return|return
operator|new
name|DiscoveryNode
argument_list|(
literal|"name_"
operator|+
name|nodeId
argument_list|,
literal|"node_"
operator|+
name|nodeId
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|attributes
argument_list|,
name|roles
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
return|;
block|}
DECL|enum|NodeSelector
specifier|private
enum|enum
name|NodeSelector
block|{
DECL|enum constant|LOCAL
name|LOCAL
argument_list|(
literal|"_local"
argument_list|)
block|{
annotation|@
name|Override
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|nodes
operator|.
name|getLocalNodeId
argument_list|()
argument_list|)
return|;
block|}
DECL|enum constant|ELECTED_MASTER
block|}
block|,
name|ELECTED_MASTER
argument_list|(
literal|"_master"
argument_list|)
block|{
annotation|@
name|Override
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|nodes
operator|.
name|getMasterNodeId
argument_list|()
argument_list|)
return|;
block|}
DECL|enum constant|MASTER_ELIGIBLE
DECL|enum constant|DiscoveryNode.Role.MASTER.getRoleName
DECL|enum constant|�
block|}
block|,
name|MASTER_ELIGIBLE
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|MASTER
operator|.
name|getRoleName
argument_list|()
operator|+
literal|":true"
argument_list|)
block|{
annotation|@
name|Override
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|getMasterNodes
argument_list|()
operator|.
name|keysIt
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|ids
operator|::
name|add
argument_list|)
expr_stmt|;
return|return
name|ids
return|;
block|}
DECL|enum constant|DATA
DECL|enum constant|DiscoveryNode.Role.DATA.getRoleName
DECL|enum constant|�
block|}
block|,
name|DATA
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|DATA
operator|.
name|getRoleName
argument_list|()
operator|+
literal|":true"
argument_list|)
block|{
annotation|@
name|Override
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|getDataNodes
argument_list|()
operator|.
name|keysIt
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|ids
operator|::
name|add
argument_list|)
expr_stmt|;
return|return
name|ids
return|;
block|}
DECL|enum constant|INGEST
DECL|enum constant|DiscoveryNode.Role.INGEST.getRoleName
DECL|enum constant|�
block|}
block|,
name|INGEST
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|INGEST
operator|.
name|getRoleName
argument_list|()
operator|+
literal|":true"
argument_list|)
block|{
annotation|@
name|Override
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|getIngestNodes
argument_list|()
operator|.
name|keysIt
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|ids
operator|::
name|add
argument_list|)
expr_stmt|;
return|return
name|ids
return|;
block|}
DECL|enum constant|CUSTOM_ATTRIBUTE
block|}
block|,
name|CUSTOM_ATTRIBUTE
argument_list|(
literal|"attr:value"
argument_list|)
block|{
annotation|@
name|Override
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|getNodes
argument_list|()
operator|.
name|valuesIt
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|node
lambda|->
block|{
if|if
condition|(
literal|"value"
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|ids
return|;
block|}
block|}
block|;
DECL|field|selector
specifier|private
specifier|final
name|String
name|selector
decl_stmt|;
DECL|method|NodeSelector
name|NodeSelector
parameter_list|(
name|String
name|selector
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
block|}
DECL|method|matchingNodeIds
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|matchingNodeIds
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|)
function_decl|;
block|}
DECL|method|testMaxMinNodeVersion
specifier|public
name|void
name|testMaxMinNodeVersion
parameter_list|()
block|{
name|DiscoveryNodes
operator|.
name|Builder
name|discoBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|discoBuilder
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"name_"
operator|+
literal|1
argument_list|,
literal|"node_"
operator|+
literal|1
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|randomSubsetOf
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Version
operator|.
name|fromString
argument_list|(
literal|"5.1.0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"name_"
operator|+
literal|2
argument_list|,
literal|"node_"
operator|+
literal|2
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|randomSubsetOf
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Version
operator|.
name|fromString
argument_list|(
literal|"6.3.0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"name_"
operator|+
literal|3
argument_list|,
literal|"node_"
operator|+
literal|3
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|randomSubsetOf
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Version
operator|.
name|fromString
argument_list|(
literal|"1.1.0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|localNodeId
argument_list|(
literal|"name_1"
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|masterNodeId
argument_list|(
literal|"name_2"
argument_list|)
expr_stmt|;
name|DiscoveryNodes
name|build
init|=
name|discoBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|fromString
argument_list|(
literal|"6.3.0"
argument_list|)
argument_list|,
name|build
operator|.
name|getMaxNodeVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|fromString
argument_list|(
literal|"1.1.0"
argument_list|)
argument_list|,
name|build
operator|.
name|getMinNodeVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

