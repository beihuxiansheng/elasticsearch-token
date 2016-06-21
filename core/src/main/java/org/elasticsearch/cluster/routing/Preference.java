begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
package|;
end_package

begin_comment
comment|/**  * Routing Preference Type  */
end_comment

begin_enum
DECL|enum|Preference
specifier|public
enum|enum
name|Preference
block|{
comment|/**      * Route to specific shards      */
DECL|enum constant|SHARDS
name|SHARDS
argument_list|(
literal|"_shards"
argument_list|)
block|,
comment|/**      * Route to preferred nodes, if possible      */
DECL|enum constant|PREFER_NODES
name|PREFER_NODES
argument_list|(
literal|"_prefer_nodes"
argument_list|)
block|,
comment|/**      * Route to local node, if possible      */
DECL|enum constant|LOCAL
name|LOCAL
argument_list|(
literal|"_local"
argument_list|)
block|,
comment|/**      * Route to primary shards      */
DECL|enum constant|PRIMARY
name|PRIMARY
argument_list|(
literal|"_primary"
argument_list|)
block|,
comment|/**      * Route to replica shards      */
DECL|enum constant|REPLICA
name|REPLICA
argument_list|(
literal|"_replica"
argument_list|)
block|,
comment|/**      * Route to primary shards first      */
DECL|enum constant|PRIMARY_FIRST
name|PRIMARY_FIRST
argument_list|(
literal|"_primary_first"
argument_list|)
block|,
comment|/**      * Route to replica shards first      */
DECL|enum constant|REPLICA_FIRST
name|REPLICA_FIRST
argument_list|(
literal|"_replica_first"
argument_list|)
block|,
comment|/**      * Route to the local shard only      */
DECL|enum constant|ONLY_LOCAL
name|ONLY_LOCAL
argument_list|(
literal|"_only_local"
argument_list|)
block|,
comment|/**      * Route to only node with attribute      */
DECL|enum constant|ONLY_NODES
name|ONLY_NODES
argument_list|(
literal|"_only_nodes"
argument_list|)
block|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|method|Preference
name|Preference
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Parses the Preference Type given a string      */
DECL|method|parse
specifier|public
specifier|static
name|Preference
name|parse
parameter_list|(
name|String
name|preference
parameter_list|)
block|{
name|String
name|preferenceType
decl_stmt|;
name|int
name|colonIndex
init|=
name|preference
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colonIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|preferenceType
operator|=
name|preference
expr_stmt|;
block|}
else|else
block|{
name|preferenceType
operator|=
name|preference
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colonIndex
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|preferenceType
condition|)
block|{
case|case
literal|"_shards"
case|:
return|return
name|SHARDS
return|;
case|case
literal|"_prefer_nodes"
case|:
return|return
name|PREFER_NODES
return|;
case|case
literal|"_local"
case|:
return|return
name|LOCAL
return|;
case|case
literal|"_primary"
case|:
return|return
name|PRIMARY
return|;
case|case
literal|"_replica"
case|:
return|return
name|REPLICA
return|;
case|case
literal|"_primary_first"
case|:
case|case
literal|"_primaryFirst"
case|:
return|return
name|PRIMARY_FIRST
return|;
case|case
literal|"_replica_first"
case|:
case|case
literal|"_replicaFirst"
case|:
return|return
name|REPLICA_FIRST
return|;
case|case
literal|"_only_local"
case|:
case|case
literal|"_onlyLocal"
case|:
return|return
name|ONLY_LOCAL
return|;
case|case
literal|"_only_nodes"
case|:
return|return
name|ONLY_NODES
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no Preference for ["
operator|+
name|preferenceType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

