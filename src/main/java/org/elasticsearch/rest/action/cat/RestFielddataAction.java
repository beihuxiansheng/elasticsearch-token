begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongHashMap
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
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsRequest
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|common
operator|.
name|Table
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
name|unit
operator|.
name|ByteSizeValue
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
name|RestResponseListener
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
name|RestTable
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_comment
comment|/**  * Cat API class to display information about the size of fielddata fields per node  */
end_comment

begin_class
DECL|class|RestFielddataAction
specifier|public
class|class
name|RestFielddataAction
extends|extends
name|AbstractCatAction
block|{
annotation|@
name|Inject
DECL|method|RestFielddataAction
specifier|public
name|RestFielddataAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
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
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/fielddata"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/fielddata/{fields}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doRequest
name|void
name|doRequest
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
block|{
specifier|final
name|NodesStatsRequest
name|nodesStatsRequest
init|=
operator|new
name|NodesStatsRequest
argument_list|()
decl_stmt|;
name|nodesStatsRequest
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nodesStatsRequest
operator|.
name|indices
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
index|[]
name|fields
init|=
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"fields"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|nodesStatsRequest
operator|.
name|indices
argument_list|()
operator|.
name|fieldDataFields
argument_list|(
name|fields
operator|==
literal|null
condition|?
operator|new
name|String
index|[]
block|{
literal|"*"
block|}
else|:
name|fields
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|nodesStats
argument_list|(
name|nodesStatsRequest
argument_list|,
operator|new
name|RestResponseListener
argument_list|<
name|NodesStatsResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|NodesStatsResponse
name|nodeStatses
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|RestTable
operator|.
name|buildResponse
argument_list|(
name|buildTable
argument_list|(
name|request
argument_list|,
name|nodeStatses
argument_list|)
argument_list|,
name|channel
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|documentation
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/fielddata\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/fielddata/{fields}\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableWithHeader
name|Table
name|getTableWithHeader
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|Table
name|table
init|=
operator|new
name|Table
argument_list|()
decl_stmt|;
name|table
operator|.
name|startHeaders
argument_list|()
operator|.
name|addCell
argument_list|(
literal|"id"
argument_list|,
literal|"desc:node id"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"host"
argument_list|,
literal|"alias:h;desc:host name"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"ip"
argument_list|,
literal|"desc:ip address"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"node"
argument_list|,
literal|"alias:n;desc:node name"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"total"
argument_list|,
literal|"text-align:right;desc:total field data usage"
argument_list|)
operator|.
name|endHeaders
argument_list|()
expr_stmt|;
return|return
name|table
return|;
block|}
DECL|method|buildTable
specifier|private
name|Table
name|buildTable
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|NodesStatsResponse
name|nodeStatses
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|NodeStats
argument_list|,
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
argument_list|>
name|nodesFields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Collect all the field names so a new table can be built
for|for
control|(
name|NodeStats
name|ns
range|:
name|nodeStatses
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|ObjectLongHashMap
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|ns
operator|.
name|getIndices
argument_list|()
operator|.
name|getFieldData
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|nodesFields
operator|.
name|put
argument_list|(
name|ns
argument_list|,
name|fields
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|key
range|:
name|fields
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
control|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// The table must be rebuilt because it has dynamic headers based on the fields
name|Table
name|table
init|=
operator|new
name|Table
argument_list|()
decl_stmt|;
name|table
operator|.
name|startHeaders
argument_list|()
operator|.
name|addCell
argument_list|(
literal|"id"
argument_list|,
literal|"desc:node id"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"host"
argument_list|,
literal|"alias:h;desc:host name"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"ip"
argument_list|,
literal|"desc:ip address"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"node"
argument_list|,
literal|"alias:n;desc:node name"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"total"
argument_list|,
literal|"text-align:right;desc:total field data usage"
argument_list|)
expr_stmt|;
comment|// The table columns must be built dynamically since the number of fields is unknown
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|table
operator|.
name|addCell
argument_list|(
name|fieldName
argument_list|,
literal|"text-align:right;desc:"
operator|+
name|fieldName
operator|+
literal|" field"
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|endHeaders
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|NodeStats
argument_list|,
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
argument_list|>
name|statsEntry
range|:
name|nodesFields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|table
operator|.
name|startRow
argument_list|()
expr_stmt|;
comment|// add the node info and field data total before each individual field
name|NodeStats
name|ns
init|=
name|statsEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|ns
operator|.
name|getNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|ns
operator|.
name|getNode
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|ns
operator|.
name|getNode
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|ns
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|ns
operator|.
name|getIndices
argument_list|()
operator|.
name|getFieldData
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|statsEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|table
operator|.
name|addCell
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|fields
operator|==
literal|null
condition|?
literal|0L
else|:
name|fields
operator|.
name|getOrDefault
argument_list|(
name|fieldName
argument_list|,
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|endRow
argument_list|()
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class

end_unit

