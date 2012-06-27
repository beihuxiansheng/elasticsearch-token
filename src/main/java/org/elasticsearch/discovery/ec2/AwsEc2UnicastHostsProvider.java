begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|ec2
operator|.
name|AmazonEC2
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|ec2
operator|.
name|model
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|ImmutableSet
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
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|AbstractComponent
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
name|transport
operator|.
name|TransportAddress
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
name|UnicastHostsProvider
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
name|transport
operator|.
name|TransportService
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
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AwsEc2UnicastHostsProvider
specifier|public
class|class
name|AwsEc2UnicastHostsProvider
extends|extends
name|AbstractComponent
implements|implements
name|UnicastHostsProvider
block|{
DECL|enum|HostType
specifier|private
specifier|static
enum|enum
name|HostType
block|{
DECL|enum constant|PRIVATE_IP
name|PRIVATE_IP
block|,
DECL|enum constant|PUBLIC_IP
name|PUBLIC_IP
block|,
DECL|enum constant|PRIVATE_DNS
name|PRIVATE_DNS
block|,
DECL|enum constant|PUBLIC_DNS
name|PUBLIC_DNS
block|}
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|AmazonEC2
name|client
decl_stmt|;
DECL|field|bindAnyGroup
specifier|private
specifier|final
name|boolean
name|bindAnyGroup
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
DECL|field|tags
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tags
decl_stmt|;
DECL|field|availabilityZones
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|availabilityZones
decl_stmt|;
DECL|field|hostType
specifier|private
specifier|final
name|HostType
name|hostType
decl_stmt|;
annotation|@
name|Inject
DECL|method|AwsEc2UnicastHostsProvider
specifier|public
name|AwsEc2UnicastHostsProvider
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|AmazonEC2
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|hostType
operator|=
name|HostType
operator|.
name|valueOf
argument_list|(
name|componentSettings
operator|.
name|get
argument_list|(
literal|"host_type"
argument_list|,
literal|"private_ip"
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|bindAnyGroup
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"any_group"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|componentSettings
operator|.
name|getAsArray
argument_list|(
literal|"groups"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tags
operator|=
name|componentSettings
operator|.
name|getByPrefix
argument_list|(
literal|"tag."
argument_list|)
operator|.
name|getAsMap
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|availabilityZones
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|componentSettings
operator|.
name|getAsArray
argument_list|(
literal|"availability_zones"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|componentSettings
operator|.
name|get
argument_list|(
literal|"availability_zones"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|availabilityZones
operator|.
name|addAll
argument_list|(
name|Strings
operator|.
name|commaDelimitedListToSet
argument_list|(
name|componentSettings
operator|.
name|get
argument_list|(
literal|"availability_zones"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|availabilityZones
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|availabilityZones
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"using host_type [{}], tags [{}], groups [{}] with any_group [{}], availability_zones [{}]"
argument_list|,
name|hostType
argument_list|,
name|tags
argument_list|,
name|groups
argument_list|,
name|bindAnyGroup
argument_list|,
name|availabilityZones
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|buildDynamicNodes
specifier|public
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|buildDynamicNodes
parameter_list|()
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoNodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|DescribeInstancesResult
name|descInstances
init|=
name|client
operator|.
name|describeInstances
argument_list|(
operator|new
name|DescribeInstancesRequest
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"building dynamic unicast discovery nodes..."
argument_list|)
expr_stmt|;
for|for
control|(
name|Reservation
name|reservation
range|:
name|descInstances
operator|.
name|getReservations
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// lets see if we can filter based on groups
name|List
argument_list|<
name|String
argument_list|>
name|groupNames
init|=
name|reservation
operator|.
name|getGroupNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|bindAnyGroup
condition|)
block|{
if|if
condition|(
name|Collections
operator|.
name|disjoint
argument_list|(
name|groups
argument_list|,
name|groupNames
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"filtering out reservation {} based on groups {}, not part of {}"
argument_list|,
name|reservation
operator|.
name|getReservationId
argument_list|()
argument_list|,
name|groupNames
argument_list|,
name|groups
argument_list|)
expr_stmt|;
comment|// continue to the next reservation
continue|continue;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|groupNames
operator|.
name|containsAll
argument_list|(
name|groups
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"filtering out reservation {} based on groups {}, does not include all of {}"
argument_list|,
name|reservation
operator|.
name|getReservationId
argument_list|()
argument_list|,
name|groupNames
argument_list|,
name|groups
argument_list|)
expr_stmt|;
comment|// continue to the next reservation
continue|continue;
block|}
block|}
block|}
for|for
control|(
name|Instance
name|instance
range|:
name|reservation
operator|.
name|getInstances
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|availabilityZones
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|availabilityZones
operator|.
name|contains
argument_list|(
name|instance
operator|.
name|getPlacement
argument_list|()
operator|.
name|getAvailabilityZone
argument_list|()
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"filtering out instance {} based on availability_zone {}, not part of {}"
argument_list|,
name|instance
operator|.
name|getInstanceId
argument_list|()
argument_list|,
name|instance
operator|.
name|getPlacement
argument_list|()
operator|.
name|getAvailabilityZone
argument_list|()
argument_list|,
name|availabilityZones
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// see if we need to filter by tags
name|boolean
name|filterByTag
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|tags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|instance
operator|.
name|getTags
argument_list|()
operator|==
literal|null
condition|)
block|{
name|filterByTag
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// check that all tags listed are there on the instance
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|tags
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Tag
name|tag
range|:
name|instance
operator|.
name|getTags
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|tag
operator|.
name|getKey
argument_list|()
argument_list|)
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|tag
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|filterByTag
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
if|if
condition|(
name|filterByTag
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"filtering out instance {} based tags {}, not part of {}"
argument_list|,
name|instance
operator|.
name|getInstanceId
argument_list|()
argument_list|,
name|tags
argument_list|,
name|instance
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|InstanceState
name|state
init|=
name|instance
operator|.
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"pending"
argument_list|)
operator|||
name|state
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"running"
argument_list|)
condition|)
block|{
name|String
name|address
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|hostType
condition|)
block|{
case|case
name|PRIVATE_DNS
case|:
name|address
operator|=
name|instance
operator|.
name|getPrivateDnsName
argument_list|()
expr_stmt|;
break|break;
case|case
name|PRIVATE_IP
case|:
name|address
operator|=
name|instance
operator|.
name|getPrivateIpAddress
argument_list|()
expr_stmt|;
break|break;
case|case
name|PUBLIC_DNS
case|:
name|address
operator|=
name|instance
operator|.
name|getPublicDnsName
argument_list|()
expr_stmt|;
break|break;
case|case
name|PUBLIC_IP
case|:
name|address
operator|=
name|instance
operator|.
name|getPublicDnsName
argument_list|()
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|address
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|transportService
operator|.
name|addressesFromString
argument_list|(
name|address
argument_list|)
decl_stmt|;
comment|// we only limit to 1 addresses, makes no sense to ping 100 ports
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|(
name|i
operator|<
name|addresses
operator|.
name|length
operator|&&
name|i
operator|<
name|UnicastZenPing
operator|.
name|LIMIT_PORTS_COUNT
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"adding {}, address {}, transport_address {}"
argument_list|,
name|instance
operator|.
name|getInstanceId
argument_list|()
argument_list|,
name|address
argument_list|,
name|addresses
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|discoNodes
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"#cloud-"
operator|+
name|instance
operator|.
name|getInstanceId
argument_list|()
operator|+
literal|"-"
operator|+
name|i
argument_list|,
name|addresses
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed ot add {}, address {}"
argument_list|,
name|e
argument_list|,
name|instance
operator|.
name|getInstanceId
argument_list|()
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"not adding {}, address is null, host_type {}"
argument_list|,
name|instance
operator|.
name|getInstanceId
argument_list|()
argument_list|,
name|hostType
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"not adding {}, state {} is not pending or running"
argument_list|,
name|instance
operator|.
name|getInstanceId
argument_list|()
argument_list|,
name|state
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"using dynamic discovery nodes {}"
argument_list|,
name|discoNodes
argument_list|)
expr_stmt|;
return|return
name|discoNodes
return|;
block|}
block|}
end_class

end_unit

