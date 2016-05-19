begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.allocation
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
name|allocation
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|ActionRequestValidationException
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
name|common
operator|.
name|Nullable
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
name|ParseField
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
name|ParseFieldMatcherSupplier
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|ObjectParser
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
name|XContentParser
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_comment
comment|/**  * A request to explain the allocation of a shard in the cluster  */
end_comment

begin_class
DECL|class|ClusterAllocationExplainRequest
specifier|public
class|class
name|ClusterAllocationExplainRequest
extends|extends
name|MasterNodeRequest
argument_list|<
name|ClusterAllocationExplainRequest
argument_list|>
block|{
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|ClusterAllocationExplainRequest
argument_list|,
name|ParseFieldMatcherSupplier
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|(
literal|"cluster/allocation/explain"
argument_list|)
decl_stmt|;
static|static
block|{
name|PARSER
operator|.
name|declareString
argument_list|(
name|ClusterAllocationExplainRequest
operator|::
name|setIndex
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareInt
argument_list|(
name|ClusterAllocationExplainRequest
operator|::
name|setShard
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"shard"
argument_list|)
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareBoolean
argument_list|(
name|ClusterAllocationExplainRequest
operator|::
name|setPrimary
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"primary"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|shard
specifier|private
name|Integer
name|shard
decl_stmt|;
DECL|field|primary
specifier|private
name|Boolean
name|primary
decl_stmt|;
DECL|field|includeYesDecisions
specifier|private
name|boolean
name|includeYesDecisions
init|=
literal|false
decl_stmt|;
comment|/** Explain the first unassigned shard */
DECL|method|ClusterAllocationExplainRequest
specifier|public
name|ClusterAllocationExplainRequest
parameter_list|()
block|{
name|this
operator|.
name|index
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|shard
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|primary
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Create a new allocation explain request. If {@code primary} is false, the first unassigned replica      * will be picked for explanation. If no replicas are unassigned, the first assigned replica will      * be explained.      */
DECL|method|ClusterAllocationExplainRequest
specifier|public
name|ClusterAllocationExplainRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shard
parameter_list|,
name|boolean
name|primary
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|shard
operator|=
name|shard
expr_stmt|;
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|useAnyUnassignedShard
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|index
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"index must be specified"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|shard
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"shard must be specified"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|primary
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"primary must be specified"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * Returns {@code true} iff the first unassigned shard is to be used      */
DECL|method|useAnyUnassignedShard
specifier|public
name|boolean
name|useAnyUnassignedShard
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
operator|==
literal|null
operator|&&
name|this
operator|.
name|shard
operator|==
literal|null
operator|&&
name|this
operator|.
name|primary
operator|==
literal|null
return|;
block|}
DECL|method|setIndex
specifier|public
name|ClusterAllocationExplainRequest
name|setIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nullable
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
DECL|method|setShard
specifier|public
name|ClusterAllocationExplainRequest
name|setShard
parameter_list|(
name|Integer
name|shard
parameter_list|)
block|{
name|this
operator|.
name|shard
operator|=
name|shard
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nullable
DECL|method|getShard
specifier|public
name|Integer
name|getShard
parameter_list|()
block|{
return|return
name|this
operator|.
name|shard
return|;
block|}
DECL|method|setPrimary
specifier|public
name|ClusterAllocationExplainRequest
name|setPrimary
parameter_list|(
name|Boolean
name|primary
parameter_list|)
block|{
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Nullable
DECL|method|isPrimary
specifier|public
name|Boolean
name|isPrimary
parameter_list|()
block|{
return|return
name|this
operator|.
name|primary
return|;
block|}
DECL|method|includeYesDecisions
specifier|public
name|void
name|includeYesDecisions
parameter_list|(
name|boolean
name|includeYesDecisions
parameter_list|)
block|{
name|this
operator|.
name|includeYesDecisions
operator|=
name|includeYesDecisions
expr_stmt|;
block|}
comment|/** Returns true if all decisions should be included. Otherwise only "NO" and "THROTTLE" decisions are returned */
DECL|method|includeYesDecisions
specifier|public
name|boolean
name|includeYesDecisions
parameter_list|()
block|{
return|return
name|this
operator|.
name|includeYesDecisions
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ClusterAllocationExplainRequest["
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|useAnyUnassignedShard
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"useAnyUnassignedShard=true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"index="
argument_list|)
operator|.
name|append
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",shard="
argument_list|)
operator|.
name|append
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",primary?="
argument_list|)
operator|.
name|append
argument_list|(
name|primary
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|",includeYesDecisions?="
argument_list|)
operator|.
name|append
argument_list|(
name|includeYesDecisions
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|ClusterAllocationExplainRequest
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterAllocationExplainRequest
name|req
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
operator|new
name|ClusterAllocationExplainRequest
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|Exception
name|e
init|=
name|req
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"'index', 'shard', and 'primary' must be specified in allocation explain request"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|req
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|this
operator|.
name|shard
operator|=
name|in
operator|.
name|readOptionalVInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|primary
operator|=
name|in
operator|.
name|readOptionalBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|includeYesDecisions
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalVInt
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalBoolean
argument_list|(
name|primary
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|includeYesDecisions
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

