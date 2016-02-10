begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectCursor
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
name|AliasesRequest
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
name|CompositeIndicesRequest
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
name|IndicesRequest
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
name|IndicesOptions
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
name|AcknowledgedRequest
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
name|cluster
operator|.
name|metadata
operator|.
name|AliasAction
operator|.
name|Type
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
name|AliasMetaData
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
name|MetaData
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
name|ImmutableOpenMap
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
name|util
operator|.
name|CollectionUtils
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
name|QueryBuilder
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
name|ArrayList
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
name|Locale
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

begin_comment
comment|/**  * A request to add/remove aliases for one or more indices.  */
end_comment

begin_class
DECL|class|IndicesAliasesRequest
specifier|public
class|class
name|IndicesAliasesRequest
extends|extends
name|AcknowledgedRequest
argument_list|<
name|IndicesAliasesRequest
argument_list|>
implements|implements
name|CompositeIndicesRequest
block|{
DECL|field|allAliasActions
specifier|private
name|List
argument_list|<
name|AliasActions
argument_list|>
name|allAliasActions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//indices options that require every specified index to exist, expand wildcards only to open indices and
comment|//don't allow that no indices are resolved from wildcard expressions
DECL|field|INDICES_OPTIONS
specifier|private
specifier|static
specifier|final
name|IndicesOptions
name|INDICES_OPTIONS
init|=
name|IndicesOptions
operator|.
name|fromOptions
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|method|IndicesAliasesRequest
specifier|public
name|IndicesAliasesRequest
parameter_list|()
block|{      }
comment|/*      * Aliases can be added by passing multiple indices to the Request and      * deleted by passing multiple indices and aliases. They are expanded into      * distinct AliasAction instances when the request is processed. This class      * holds the AliasAction and in addition the arrays or alias names and      * indices that is later used to create the final AliasAction instances.      */
DECL|class|AliasActions
specifier|public
specifier|static
class|class
name|AliasActions
implements|implements
name|AliasesRequest
block|{
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|aliases
specifier|private
name|String
index|[]
name|aliases
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|aliasAction
specifier|private
name|AliasAction
name|aliasAction
decl_stmt|;
DECL|method|AliasActions
specifier|public
name|AliasActions
parameter_list|(
name|AliasAction
operator|.
name|Type
name|type
parameter_list|,
name|String
index|[]
name|indices
parameter_list|,
name|String
index|[]
name|aliases
parameter_list|)
block|{
name|aliasAction
operator|=
operator|new
name|AliasAction
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|aliases
argument_list|(
name|aliases
argument_list|)
expr_stmt|;
block|}
DECL|method|AliasActions
specifier|public
name|AliasActions
parameter_list|(
name|AliasAction
operator|.
name|Type
name|type
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|aliasAction
operator|=
operator|new
name|AliasAction
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|indices
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|aliases
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
DECL|method|AliasActions
name|AliasActions
parameter_list|(
name|AliasAction
operator|.
name|Type
name|type
parameter_list|,
name|String
index|[]
name|index
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|aliasAction
operator|=
operator|new
name|AliasAction
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|indices
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|aliases
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
DECL|method|AliasActions
specifier|public
name|AliasActions
parameter_list|(
name|AliasAction
name|action
parameter_list|)
block|{
name|this
operator|.
name|aliasAction
operator|=
name|action
expr_stmt|;
name|indices
argument_list|(
name|action
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|aliases
argument_list|(
name|action
operator|.
name|alias
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AliasActions
specifier|public
name|AliasActions
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|index
parameter_list|,
name|String
index|[]
name|aliases
parameter_list|)
block|{
name|aliasAction
operator|=
operator|new
name|AliasAction
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|indices
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|aliases
argument_list|(
name|aliases
argument_list|)
expr_stmt|;
block|}
DECL|method|AliasActions
specifier|public
name|AliasActions
parameter_list|()
block|{         }
DECL|method|filter
specifier|public
name|AliasActions
name|filter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|filter
parameter_list|)
block|{
name|aliasAction
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|filter
specifier|public
name|AliasActions
name|filter
parameter_list|(
name|QueryBuilder
name|filter
parameter_list|)
block|{
name|aliasAction
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|actionType
specifier|public
name|Type
name|actionType
parameter_list|()
block|{
return|return
name|aliasAction
operator|.
name|actionType
argument_list|()
return|;
block|}
DECL|method|routing
specifier|public
name|void
name|routing
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|aliasAction
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
DECL|method|searchRouting
specifier|public
name|void
name|searchRouting
parameter_list|(
name|String
name|searchRouting
parameter_list|)
block|{
name|aliasAction
operator|.
name|searchRouting
argument_list|(
name|searchRouting
argument_list|)
expr_stmt|;
block|}
DECL|method|indexRouting
specifier|public
name|void
name|indexRouting
parameter_list|(
name|String
name|indexRouting
parameter_list|)
block|{
name|aliasAction
operator|.
name|indexRouting
argument_list|(
name|indexRouting
argument_list|)
expr_stmt|;
block|}
DECL|method|filter
specifier|public
name|AliasActions
name|filter
parameter_list|(
name|String
name|filter
parameter_list|)
block|{
name|aliasAction
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|AliasActions
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|aliases
specifier|public
name|AliasActions
name|aliases
parameter_list|(
name|String
modifier|...
name|aliases
parameter_list|)
block|{
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|aliases
specifier|public
name|String
index|[]
name|aliases
parameter_list|()
block|{
return|return
name|aliases
return|;
block|}
annotation|@
name|Override
DECL|method|expandAliasesWildcards
specifier|public
name|boolean
name|expandAliasesWildcards
parameter_list|()
block|{
comment|//remove operations support wildcards among aliases, add operations don't
return|return
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|==
name|Type
operator|.
name|REMOVE
return|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|INDICES_OPTIONS
return|;
block|}
DECL|method|aliasAction
specifier|public
name|AliasAction
name|aliasAction
parameter_list|()
block|{
return|return
name|aliasAction
return|;
block|}
DECL|method|concreteAliases
specifier|public
name|String
index|[]
name|concreteAliases
parameter_list|(
name|MetaData
name|metaData
parameter_list|,
name|String
name|concreteIndex
parameter_list|)
block|{
if|if
condition|(
name|expandAliasesWildcards
argument_list|()
condition|)
block|{
comment|//for DELETE we expand the aliases
name|String
index|[]
name|indexAsArray
init|=
block|{
name|concreteIndex
block|}
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliasMetaData
init|=
name|metaData
operator|.
name|findAliases
argument_list|(
name|aliases
argument_list|,
name|indexAsArray
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|finalAliases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|curAliases
range|:
name|aliasMetaData
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AliasMetaData
name|aliasMeta
range|:
name|curAliases
operator|.
name|value
control|)
block|{
name|finalAliases
operator|.
name|add
argument_list|(
name|aliasMeta
operator|.
name|alias
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|finalAliases
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|finalAliases
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
else|else
block|{
comment|//for add we just return the current aliases
return|return
name|aliases
return|;
block|}
block|}
DECL|method|readFrom
specifier|public
name|AliasActions
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|aliases
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|aliasAction
operator|=
name|readAliasAction
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
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
name|out
operator|.
name|writeStringArray
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|aliases
argument_list|)
expr_stmt|;
name|this
operator|.
name|aliasAction
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Adds an alias to the index.      * @param alias The alias      * @param indices The indices      */
DECL|method|addAlias
specifier|public
name|IndicesAliasesRequest
name|addAlias
parameter_list|(
name|String
name|alias
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|addAliasAction
argument_list|(
operator|new
name|AliasActions
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|indices
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
name|void
name|addAliasAction
parameter_list|(
name|AliasActions
name|aliasAction
parameter_list|)
block|{
name|allAliasActions
operator|.
name|add
argument_list|(
name|aliasAction
argument_list|)
expr_stmt|;
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
name|addAliasAction
argument_list|(
operator|new
name|AliasActions
argument_list|(
name|action
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an alias to the index.      * @param alias  The alias      * @param filter The filter      * @param indices  The indices      */
DECL|method|addAlias
specifier|public
name|IndicesAliasesRequest
name|addAlias
parameter_list|(
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
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|addAliasAction
argument_list|(
operator|new
name|AliasActions
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|indices
argument_list|,
name|alias
argument_list|)
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an alias to the index.      * @param alias         The alias      * @param filterBuilder The filter      * @param indices         The indices      */
DECL|method|addAlias
specifier|public
name|IndicesAliasesRequest
name|addAlias
parameter_list|(
name|String
name|alias
parameter_list|,
name|QueryBuilder
name|filterBuilder
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|addAliasAction
argument_list|(
operator|new
name|AliasActions
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
argument_list|,
name|indices
argument_list|,
name|alias
argument_list|)
operator|.
name|filter
argument_list|(
name|filterBuilder
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Removes an alias to the index.      *      * @param indices The indices      * @param aliases The aliases      */
DECL|method|removeAlias
specifier|public
name|IndicesAliasesRequest
name|removeAlias
parameter_list|(
name|String
index|[]
name|indices
parameter_list|,
name|String
modifier|...
name|aliases
parameter_list|)
block|{
name|addAliasAction
argument_list|(
operator|new
name|AliasActions
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|REMOVE
argument_list|,
name|indices
argument_list|,
name|aliases
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Removes an alias to the index.      *      * @param index The index      * @param aliases The aliases      */
DECL|method|removeAlias
specifier|public
name|IndicesAliasesRequest
name|removeAlias
parameter_list|(
name|String
name|index
parameter_list|,
name|String
modifier|...
name|aliases
parameter_list|)
block|{
name|addAliasAction
argument_list|(
operator|new
name|AliasActions
argument_list|(
name|AliasAction
operator|.
name|Type
operator|.
name|REMOVE
argument_list|,
name|index
argument_list|,
name|aliases
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|aliasActions
name|List
argument_list|<
name|AliasActions
argument_list|>
name|aliasActions
parameter_list|()
block|{
return|return
name|this
operator|.
name|allAliasActions
return|;
block|}
DECL|method|getAliasActions
specifier|public
name|List
argument_list|<
name|AliasActions
argument_list|>
name|getAliasActions
parameter_list|()
block|{
return|return
name|aliasActions
argument_list|()
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
name|allAliasActions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|addValidationError
argument_list|(
literal|"Must specify at least one alias action"
argument_list|,
name|validationException
argument_list|)
return|;
block|}
for|for
control|(
name|AliasActions
name|aliasAction
range|:
name|allAliasActions
control|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|aliasAction
operator|.
name|aliases
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"Alias action ["
operator|+
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
literal|"]: Property [alias/aliases] is either missing or null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|alias
range|:
name|aliasAction
operator|.
name|aliases
control|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|alias
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"Alias action ["
operator|+
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
literal|"]: [alias/aliases] may not be empty string"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|aliasAction
operator|.
name|indices
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"Alias action ["
operator|+
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
literal|"]: Property [index/indices] is either missing or null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|index
range|:
name|aliasAction
operator|.
name|indices
control|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"Alias action ["
operator|+
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
literal|"]: [index/indices] may not be empty string"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|allAliasActions
operator|.
name|add
argument_list|(
name|readAliasActions
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readTimeout
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
name|allAliasActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AliasActions
name|aliasAction
range|:
name|allAliasActions
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
name|writeTimeout
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|INDICES_OPTIONS
return|;
block|}
DECL|method|readAliasActions
specifier|private
specifier|static
name|AliasActions
name|readAliasActions
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|AliasActions
name|actions
init|=
operator|new
name|AliasActions
argument_list|()
decl_stmt|;
return|return
name|actions
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|subRequests
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|IndicesRequest
argument_list|>
name|subRequests
parameter_list|()
block|{
return|return
name|allAliasActions
return|;
block|}
block|}
end_class

end_unit

