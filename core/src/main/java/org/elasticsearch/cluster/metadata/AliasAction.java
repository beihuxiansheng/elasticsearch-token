begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesRequest
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
name|Strings
import|;
end_import

begin_comment
comment|/**  * Individual operation to perform on the cluster state as part of an {@link IndicesAliasesRequest}.  */
end_comment

begin_class
DECL|class|AliasAction
specifier|public
specifier|abstract
class|class
name|AliasAction
block|{
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|method|AliasAction
specifier|private
name|AliasAction
parameter_list|(
name|String
name|index
parameter_list|)
block|{
if|if
condition|(
literal|false
operator|==
name|Strings
operator|.
name|hasText
argument_list|(
name|index
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[index] is required"
argument_list|)
throw|;
block|}
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|/**      * Get the index on which the operation should act.      */
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
comment|/**      * Should this action remove the index? Actions that return true from this will never execute      * {@link #apply(NewAliasValidator, MetaData.Builder, IndexMetaData)}.      */
DECL|method|removeIndex
specifier|abstract
name|boolean
name|removeIndex
parameter_list|()
function_decl|;
comment|/**      * Apply the action.      *       * @param aliasValidator call to validate a new alias before adding it to the builder      * @param metadata metadata builder for the changes made by all actions as part of this request      * @param index metadata for the index being changed      * @return did this action make any changes?      */
DECL|method|apply
specifier|abstract
name|boolean
name|apply
parameter_list|(
name|NewAliasValidator
name|aliasValidator
parameter_list|,
name|MetaData
operator|.
name|Builder
name|metadata
parameter_list|,
name|IndexMetaData
name|index
parameter_list|)
function_decl|;
comment|/**      * Validate a new alias.      */
annotation|@
name|FunctionalInterface
DECL|interface|NewAliasValidator
specifier|public
interface|interface
name|NewAliasValidator
block|{
DECL|method|validate
name|void
name|validate
parameter_list|(
name|String
name|alias
parameter_list|,
annotation|@
name|Nullable
name|String
name|indexRouting
parameter_list|,
annotation|@
name|Nullable
name|String
name|filter
parameter_list|)
function_decl|;
block|}
comment|/**      * Operation to add an alias to an index.      */
DECL|class|Add
specifier|public
specifier|static
class|class
name|Add
extends|extends
name|AliasAction
block|{
DECL|field|alias
specifier|private
specifier|final
name|String
name|alias
decl_stmt|;
annotation|@
name|Nullable
DECL|field|filter
specifier|private
specifier|final
name|String
name|filter
decl_stmt|;
annotation|@
name|Nullable
DECL|field|indexRouting
specifier|private
specifier|final
name|String
name|indexRouting
decl_stmt|;
annotation|@
name|Nullable
DECL|field|searchRouting
specifier|private
specifier|final
name|String
name|searchRouting
decl_stmt|;
comment|/**          * Build the operation.          */
DECL|method|Add
specifier|public
name|Add
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|,
annotation|@
name|Nullable
name|String
name|filter
parameter_list|,
annotation|@
name|Nullable
name|String
name|indexRouting
parameter_list|,
annotation|@
name|Nullable
name|String
name|searchRouting
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|Strings
operator|.
name|hasText
argument_list|(
name|alias
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[alias] is required"
argument_list|)
throw|;
block|}
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|indexRouting
operator|=
name|indexRouting
expr_stmt|;
name|this
operator|.
name|searchRouting
operator|=
name|searchRouting
expr_stmt|;
block|}
comment|/**          * Alias to add to the index.          */
DECL|method|getAlias
specifier|public
name|String
name|getAlias
parameter_list|()
block|{
return|return
name|alias
return|;
block|}
annotation|@
name|Override
DECL|method|removeIndex
name|boolean
name|removeIndex
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|apply
name|boolean
name|apply
parameter_list|(
name|NewAliasValidator
name|aliasValidator
parameter_list|,
name|MetaData
operator|.
name|Builder
name|metadata
parameter_list|,
name|IndexMetaData
name|index
parameter_list|)
block|{
name|aliasValidator
operator|.
name|validate
argument_list|(
name|alias
argument_list|,
name|indexRouting
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|AliasMetaData
name|newAliasMd
init|=
name|AliasMetaData
operator|.
name|newAliasMetaDataBuilder
argument_list|(
name|alias
argument_list|)
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
operator|.
name|indexRouting
argument_list|(
name|indexRouting
argument_list|)
operator|.
name|searchRouting
argument_list|(
name|searchRouting
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Check if this alias already exists
name|AliasMetaData
name|currentAliasMd
init|=
name|index
operator|.
name|getAliases
argument_list|()
operator|.
name|get
argument_list|(
name|alias
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentAliasMd
operator|!=
literal|null
operator|&&
name|currentAliasMd
operator|.
name|equals
argument_list|(
name|newAliasMd
argument_list|)
condition|)
block|{
comment|// It already exists, ignore it
return|return
literal|false
return|;
block|}
name|metadata
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|index
argument_list|)
operator|.
name|putAlias
argument_list|(
name|newAliasMd
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Operation to remove an alias from an index.      */
DECL|class|Remove
specifier|public
specifier|static
class|class
name|Remove
extends|extends
name|AliasAction
block|{
DECL|field|alias
specifier|private
specifier|final
name|String
name|alias
decl_stmt|;
comment|/**          * Build the operation.          */
DECL|method|Remove
specifier|public
name|Remove
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|Strings
operator|.
name|hasText
argument_list|(
name|alias
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[alias] is required"
argument_list|)
throw|;
block|}
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
block|}
comment|/**          * Alias to remove from the index.          */
DECL|method|getAlias
specifier|public
name|String
name|getAlias
parameter_list|()
block|{
return|return
name|alias
return|;
block|}
annotation|@
name|Override
DECL|method|removeIndex
name|boolean
name|removeIndex
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|apply
name|boolean
name|apply
parameter_list|(
name|NewAliasValidator
name|aliasValidator
parameter_list|,
name|MetaData
operator|.
name|Builder
name|metadata
parameter_list|,
name|IndexMetaData
name|index
parameter_list|)
block|{
if|if
condition|(
literal|false
operator|==
name|index
operator|.
name|getAliases
argument_list|()
operator|.
name|containsKey
argument_list|(
name|alias
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|metadata
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|index
argument_list|)
operator|.
name|removeAlias
argument_list|(
name|alias
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Operation to remove an index. This is an "alias action" because it allows us to remove an index at the same time as we remove add an      * alias to replace it.      */
DECL|class|RemoveIndex
specifier|public
specifier|static
class|class
name|RemoveIndex
extends|extends
name|AliasAction
block|{
DECL|method|RemoveIndex
specifier|public
name|RemoveIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeIndex
name|boolean
name|removeIndex
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|apply
name|boolean
name|apply
parameter_list|(
name|NewAliasValidator
name|aliasValidator
parameter_list|,
name|MetaData
operator|.
name|Builder
name|metadata
parameter_list|,
name|IndexMetaData
name|index
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

