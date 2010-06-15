begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.elect
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|elect
package|;
end_package

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
name|settings
operator|.
name|Settings
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ElectMasterService
specifier|public
class|class
name|ElectMasterService
extends|extends
name|AbstractComponent
block|{
DECL|field|nodeComparator
specifier|private
specifier|final
name|NodeComparator
name|nodeComparator
init|=
operator|new
name|NodeComparator
argument_list|()
decl_stmt|;
DECL|method|ElectMasterService
specifier|public
name|ElectMasterService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a list of the next possible masters.      */
DECL|method|nextPossibleMasters
specifier|public
name|DiscoveryNode
index|[]
name|nextPossibleMasters
parameter_list|(
name|Iterable
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
parameter_list|,
name|int
name|numberOfPossibleMasters
parameter_list|)
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|sortedNodes
init|=
name|sortedMasterNodes
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortedNodes
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|DiscoveryNode
index|[
literal|0
index|]
return|;
block|}
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nextPossibleMasters
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|numberOfPossibleMasters
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|nextPossibleMaster
range|:
name|sortedNodes
control|)
block|{
if|if
condition|(
operator|++
name|counter
operator|>=
name|numberOfPossibleMasters
condition|)
block|{
break|break;
block|}
name|nextPossibleMasters
operator|.
name|add
argument_list|(
name|nextPossibleMaster
argument_list|)
expr_stmt|;
block|}
return|return
name|nextPossibleMasters
operator|.
name|toArray
argument_list|(
operator|new
name|DiscoveryNode
index|[
name|nextPossibleMasters
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Elects a new master out of the possible nodes, returning it. Returns<tt>null</tt>      * if no master has been elected.      */
DECL|method|electMaster
specifier|public
name|DiscoveryNode
name|electMaster
parameter_list|(
name|Iterable
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
parameter_list|)
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|sortedNodes
init|=
name|sortedMasterNodes
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortedNodes
operator|==
literal|null
operator|||
name|sortedNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|sortedNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|sortedMasterNodes
specifier|private
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|sortedMasterNodes
parameter_list|(
name|Iterable
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
parameter_list|)
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|possibleNodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|possibleNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// clean non master nodes
for|for
control|(
name|Iterator
argument_list|<
name|DiscoveryNode
argument_list|>
name|it
init|=
name|possibleNodes
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|attributes
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"zen.master"
argument_list|)
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"zen.master"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|possibleNodes
argument_list|,
name|nodeComparator
argument_list|)
expr_stmt|;
return|return
name|possibleNodes
return|;
block|}
DECL|class|NodeComparator
specifier|private
specifier|static
class|class
name|NodeComparator
implements|implements
name|Comparator
argument_list|<
name|DiscoveryNode
argument_list|>
block|{
DECL|method|compare
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|DiscoveryNode
name|o1
parameter_list|,
name|DiscoveryNode
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|id
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|id
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

