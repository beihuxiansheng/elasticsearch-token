begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indexer.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|routing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
import|;
end_import

begin_comment
comment|/**  * The state of the indexer as defined by the cluster.  *  * @author kimchy (shay.banon)  */
end_comment

begin_enum
DECL|enum|IndexerRoutingState
specifier|public
enum|enum
name|IndexerRoutingState
block|{
comment|/**      * The indexer is not assigned to any node.      */
DECL|enum constant|UNASSIGNED
name|UNASSIGNED
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
comment|/**      * The indexer is initializing.      */
DECL|enum constant|INITIALIZING
name|INITIALIZING
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|,
comment|/**      * The indexer is started.      */
DECL|enum constant|STARTED
name|STARTED
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
block|;
DECL|field|value
specifier|private
name|byte
name|value
decl_stmt|;
DECL|method|IndexerRoutingState
name|IndexerRoutingState
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|value
specifier|public
name|byte
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|fromValue
specifier|public
specifier|static
name|IndexerRoutingState
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|1
case|:
return|return
name|UNASSIGNED
return|;
case|case
literal|2
case|:
return|return
name|INITIALIZING
return|;
case|case
literal|3
case|:
return|return
name|STARTED
return|;
default|default:
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"No should routing state mapped for ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

