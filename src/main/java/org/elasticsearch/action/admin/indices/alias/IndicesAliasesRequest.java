begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.alias
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|alias
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|ElasticSearchGenerationException
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
name|MasterNodeOperationRequest
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
name|metadata
operator|.
name|AliasAction
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
name|unit
operator|.
name|TimeValue
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
name|index
operator|.
name|query
operator|.
name|FilterBuilder
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|AliasAction
operator|.
name|readAliasAction
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
name|readTimeValue
import|;
end_import

begin_comment
comment|/**  * A request to add/remove aliases for one or more indices.  */
end_comment

begin_class
DECL|class|IndicesAliasesRequest
specifier|public
class|class
name|IndicesAliasesRequest
extends|extends
name|MasterNodeOperationRequest
block|{
DECL|field|aliasActions
specifier|private
name|List
argument_list|<
name|AliasAction
argument_list|>
name|aliasActions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|timeout
specifier|private
name|TimeValue
name|timeout
init|=
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|method|IndicesAliasesRequest
specifier|public
name|IndicesAliasesRequest
parameter_list|()
block|{      }
comment|/**      * Adds an alias to the index.      *      * @param index The index      * @param alias The alias      */
DECL|method|addAlias
specifier|public
name|IndicesAliasesRequest
name|addAlias
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|aliasActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|index
argument_list|,
name|alias
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an alias to the index.      *      * @param index  The index      * @param alias  The alias      * @param filter The filter      */
DECL|method|addAlias
specifier|public
name|IndicesAliasesRequest
name|addAlias
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|,
name|String
name|filter
parameter_list|)
block|{
name|aliasActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|index
argument_list|,
name|alias
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an alias to the index.      *      * @param index  The index      * @param alias  The alias      * @param filter The filter      */
DECL|method|addAlias
specifier|public
name|IndicesAliasesRequest
name|addAlias
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|==
literal|null
operator|||
name|filter
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|aliasActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|index
argument_list|,
name|alias
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|aliasActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|index
argument_list|,
name|alias
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchGenerationException
argument_list|(
literal|"Failed to generate ["
operator|+
name|filter
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Adds an alias to the index.      *      * @param index         The index      * @param alias         The alias      * @param filterBuilder The filter      */
DECL|method|addAlias
specifier|public
name|IndicesAliasesRequest
name|addAlias
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|,
name|FilterBuilder
name|filterBuilder
parameter_list|)
block|{
if|if
condition|(
name|filterBuilder
operator|==
literal|null
condition|)
block|{
name|aliasActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|index
argument_list|,
name|alias
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|filterBuilder
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
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|addAlias
argument_list|(
name|index
argument_list|,
name|alias
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchGenerationException
argument_list|(
literal|"Failed to build json for alias request"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Removes an alias to the index.      *      * @param index The index      * @param alias The alias      */
DECL|method|removeAlias
specifier|public
name|IndicesAliasesRequest
name|removeAlias
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|aliasActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|REMOVE
argument_list|,
name|index
argument_list|,
name|alias
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addAliasAction
specifier|public
name|IndicesAliasesRequest
name|addAliasAction
parameter_list|(
name|AliasAction
name|action
parameter_list|)
block|{
name|aliasActions
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|aliasActions
name|List
argument_list|<
name|AliasAction
argument_list|>
name|aliasActions
parameter_list|()
block|{
return|return
name|this
operator|.
name|aliasActions
return|;
block|}
comment|/**      * Timeout to wait till the put mapping gets acknowledged of all current cluster nodes. Defaults to      *<tt>10s</tt>.      */
DECL|method|timeout
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
comment|/**      * Timeout to wait till the alias operations get acknowledged of all current cluster nodes. Defaults to      *<tt>10s</tt>.      */
DECL|method|timeout
specifier|public
name|IndicesAliasesRequest
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Timeout to wait till the alias operations get acknowledged of all current cluster nodes. Defaults to      *<tt>10s</tt>.      */
DECL|method|timeout
specifier|public
name|IndicesAliasesRequest
name|timeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
return|return
name|timeout
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|timeout
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
return|;
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
name|aliasActions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"Must specify at least one alias action"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|aliasActions
operator|.
name|add
argument_list|(
name|readAliasAction
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|timeout
operator|=
name|readTimeValue
argument_list|(
name|in
argument_list|)
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
name|writeVInt
argument_list|(
name|aliasActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AliasAction
name|aliasAction
range|:
name|aliasActions
control|)
block|{
name|aliasAction
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|timeout
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

