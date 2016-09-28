begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.reroute
package|package
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
name|support
operator|.
name|master
operator|.
name|AcknowledgedRequest
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
name|support
operator|.
name|master
operator|.
name|MasterNodeRequest
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|AllocateEmptyPrimaryAllocationCommand
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|AllocateReplicaAllocationCommand
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|AllocateStalePrimaryAllocationCommand
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|AllocationCommand
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|AllocationCommandRegistry
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|CancelAllocationCommand
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|MoveAllocationCommand
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
name|ParseFieldMatcher
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableAwareStreamInput
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|NetworkModule
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|XContentType
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
name|RestRequest
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
name|admin
operator|.
name|cluster
operator|.
name|RestClusterRerouteAction
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
name|rest
operator|.
name|FakeRestRequest
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
name|Arrays
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
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableList
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
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueMillis
import|;
end_import

begin_comment
comment|/**  * Test for serialization and parsing of {@link ClusterRerouteRequest} and its commands. See the superclass for, well, everything.  */
end_comment

begin_class
DECL|class|ClusterRerouteRequestTests
specifier|public
class|class
name|ClusterRerouteRequestTests
extends|extends
name|ESTestCase
block|{
DECL|field|ROUNDS
specifier|private
specifier|static
specifier|final
name|int
name|ROUNDS
init|=
literal|30
decl_stmt|;
DECL|field|RANDOM_COMMAND_GENERATORS
specifier|private
specifier|final
name|List
argument_list|<
name|Supplier
argument_list|<
name|AllocationCommand
argument_list|>
argument_list|>
name|RANDOM_COMMAND_GENERATORS
init|=
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
parameter_list|()
lambda|->
operator|new
name|AllocateReplicaAllocationCommand
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|,
parameter_list|()
lambda|->
operator|new
name|AllocateEmptyPrimaryAllocationCommand
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
parameter_list|()
lambda|->
operator|new
name|AllocateStalePrimaryAllocationCommand
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
parameter_list|()
lambda|->
operator|new
name|CancelAllocationCommand
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
parameter_list|()
lambda|->
operator|new
name|MoveAllocationCommand
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|namedWriteableRegistry
specifier|private
specifier|final
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|allocationCommandRegistry
specifier|private
specifier|final
name|AllocationCommandRegistry
name|allocationCommandRegistry
decl_stmt|;
DECL|method|ClusterRerouteRequestTests
specifier|public
name|ClusterRerouteRequestTests
parameter_list|()
block|{
name|allocationCommandRegistry
operator|=
name|NetworkModule
operator|.
name|getAllocationCommandRegistry
argument_list|()
expr_stmt|;
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|(
name|NetworkModule
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|randomRequest
specifier|private
name|ClusterRerouteRequest
name|randomRequest
parameter_list|()
block|{
name|ClusterRerouteRequest
name|request
init|=
operator|new
name|ClusterRerouteRequest
argument_list|()
decl_stmt|;
name|int
name|commands
init|=
name|between
argument_list|(
literal|0
argument_list|,
literal|10
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
name|commands
condition|;
name|i
operator|++
control|)
block|{
name|request
operator|.
name|add
argument_list|(
name|randomFrom
argument_list|(
name|RANDOM_COMMAND_GENERATORS
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|dryRun
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|explain
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRetryFailed
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|testEqualsAndHashCode
specifier|public
name|void
name|testEqualsAndHashCode
parameter_list|()
block|{
for|for
control|(
name|int
name|round
init|=
literal|0
init|;
name|round
operator|<
name|ROUNDS
condition|;
name|round
operator|++
control|)
block|{
name|ClusterRerouteRequest
name|request
init|=
name|randomRequest
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|request
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterRerouteRequest
name|copy
init|=
operator|new
name|ClusterRerouteRequest
argument_list|()
operator|.
name|add
argument_list|(
name|request
operator|.
name|getCommands
argument_list|()
operator|.
name|commands
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|AllocationCommand
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|copy
operator|.
name|dryRun
argument_list|(
name|request
operator|.
name|dryRun
argument_list|()
argument_list|)
operator|.
name|explain
argument_list|(
name|request
operator|.
name|explain
argument_list|()
argument_list|)
operator|.
name|timeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
operator|.
name|setRetryFailed
argument_list|(
name|request
operator|.
name|isRetryFailed
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|copy
argument_list|,
name|request
argument_list|)
expr_stmt|;
comment|// Commutative
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Changing dryRun makes requests not equal
name|copy
operator|.
name|dryRun
argument_list|(
operator|!
name|copy
operator|.
name|dryRun
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|dryRun
argument_list|(
operator|!
name|copy
operator|.
name|dryRun
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Changing explain makes requests not equal
name|copy
operator|.
name|explain
argument_list|(
operator|!
name|copy
operator|.
name|explain
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|explain
argument_list|(
operator|!
name|copy
operator|.
name|explain
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Changing timeout makes requests not equal
name|copy
operator|.
name|timeout
argument_list|(
name|timeValueMillis
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
operator|.
name|millis
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|timeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Changing masterNodeTime makes requests not equal
name|copy
operator|.
name|masterNodeTimeout
argument_list|(
name|timeValueMillis
argument_list|(
name|request
operator|.
name|masterNodeTimeout
argument_list|()
operator|.
name|millis
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Changing commands makes requests not equal
name|copy
operator|.
name|add
argument_list|(
name|randomFrom
argument_list|(
name|RANDOM_COMMAND_GENERATORS
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
comment|// Can't check hashCode because we can't be sure that changing commands changes the hashCode. It usually does but might not.
block|}
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|round
init|=
literal|0
init|;
name|round
operator|<
name|ROUNDS
condition|;
name|round
operator|++
control|)
block|{
name|ClusterRerouteRequest
name|request
init|=
name|randomRequest
argument_list|()
decl_stmt|;
name|ClusterRerouteRequest
name|copy
init|=
name|roundTripThroughBytes
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParsing
specifier|public
name|void
name|testParsing
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|round
init|=
literal|0
init|;
name|round
operator|<
name|ROUNDS
condition|;
name|round
operator|++
control|)
block|{
name|ClusterRerouteRequest
name|request
init|=
name|randomRequest
argument_list|()
decl_stmt|;
name|ClusterRerouteRequest
name|copy
init|=
name|roundTripThroughRestRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|request
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|roundTripThroughBytes
specifier|private
name|ClusterRerouteRequest
name|roundTripThroughBytes
parameter_list|(
name|ClusterRerouteRequest
name|original
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|original
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|output
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
name|ClusterRerouteRequest
name|copy
init|=
operator|new
name|ClusterRerouteRequest
argument_list|()
decl_stmt|;
name|copy
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
block|}
block|}
DECL|method|roundTripThroughRestRequest
specifier|private
name|ClusterRerouteRequest
name|roundTripThroughRestRequest
parameter_list|(
name|ClusterRerouteRequest
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|RestRequest
name|restRequest
init|=
name|toRestRequest
argument_list|(
name|original
argument_list|)
decl_stmt|;
return|return
name|RestClusterRerouteAction
operator|.
name|createRequest
argument_list|(
name|restRequest
argument_list|,
name|allocationCommandRegistry
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
return|;
block|}
DECL|method|toRestRequest
specifier|private
specifier|static
name|RestRequest
name|toRestRequest
parameter_list|(
name|ClusterRerouteRequest
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|hasBody
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"dry_run"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|original
operator|.
name|dryRun
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hasBody
operator|=
literal|true
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"dry_run"
argument_list|,
name|original
operator|.
name|dryRun
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|put
argument_list|(
literal|"explain"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|original
operator|.
name|explain
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|original
operator|.
name|timeout
argument_list|()
operator|.
name|equals
argument_list|(
name|AcknowledgedRequest
operator|.
name|DEFAULT_ACK_TIMEOUT
argument_list|)
operator|||
name|randomBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"timeout"
argument_list|,
name|original
operator|.
name|timeout
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|original
operator|.
name|isRetryFailed
argument_list|()
operator|||
name|randomBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"retry_failed"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|original
operator|.
name|isRetryFailed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|false
operator|==
name|original
operator|.
name|masterNodeTimeout
argument_list|()
operator|.
name|equals
argument_list|(
name|MasterNodeRequest
operator|.
name|DEFAULT_MASTER_NODE_TIMEOUT
argument_list|)
operator|||
name|randomBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"master_timeout"
argument_list|,
name|original
operator|.
name|masterNodeTimeout
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|original
operator|.
name|getCommands
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|hasBody
operator|=
literal|true
expr_stmt|;
name|original
operator|.
name|getCommands
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|FakeRestRequest
operator|.
name|Builder
name|requestBuilder
init|=
operator|new
name|FakeRestRequest
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|requestBuilder
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasBody
condition|)
block|{
name|requestBuilder
operator|.
name|withContent
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|requestBuilder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit
