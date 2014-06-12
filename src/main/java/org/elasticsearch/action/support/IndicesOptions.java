begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|rest
operator|.
name|RestRequest
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

begin_comment
comment|/**  * Controls how to deal with unavailable concrete indices (closed or missing), how wildcard expressions are expanded  * to actual indices (all, closed or open indices) and how to deal with wildcard expressions that resolve to no indices.  */
end_comment

begin_class
DECL|class|IndicesOptions
specifier|public
class|class
name|IndicesOptions
block|{
DECL|field|VALUES
specifier|private
specifier|static
specifier|final
name|IndicesOptions
index|[]
name|VALUES
decl_stmt|;
DECL|field|IGNORE_UNAVAILABLE
specifier|private
specifier|static
specifier|final
name|byte
name|IGNORE_UNAVAILABLE
init|=
literal|1
decl_stmt|;
DECL|field|ALLOW_NO_INDICES
specifier|private
specifier|static
specifier|final
name|byte
name|ALLOW_NO_INDICES
init|=
literal|2
decl_stmt|;
DECL|field|EXPAND_WILDCARDS_OPEN
specifier|private
specifier|static
specifier|final
name|byte
name|EXPAND_WILDCARDS_OPEN
init|=
literal|4
decl_stmt|;
DECL|field|EXPAND_WILDCARDS_CLOSED
specifier|private
specifier|static
specifier|final
name|byte
name|EXPAND_WILDCARDS_CLOSED
init|=
literal|8
decl_stmt|;
DECL|field|FORBID_ALIASES_TO_MULTIPLE_INDICES
specifier|private
specifier|static
specifier|final
name|byte
name|FORBID_ALIASES_TO_MULTIPLE_INDICES
init|=
literal|16
decl_stmt|;
DECL|field|FORBID_CLOSED_INDICES
specifier|private
specifier|static
specifier|final
name|byte
name|FORBID_CLOSED_INDICES
init|=
literal|32
decl_stmt|;
static|static
block|{
name|byte
name|max
init|=
literal|1
operator|<<
literal|6
decl_stmt|;
name|VALUES
operator|=
operator|new
name|IndicesOptions
index|[
name|max
index|]
expr_stmt|;
for|for
control|(
name|byte
name|id
init|=
literal|0
init|;
name|id
operator|<
name|max
condition|;
name|id
operator|++
control|)
block|{
name|VALUES
index|[
name|id
index|]
operator|=
operator|new
name|IndicesOptions
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|id
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
DECL|method|IndicesOptions
specifier|private
name|IndicesOptions
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * @return Whether specified concrete indices should be ignored when unavailable (missing or closed)      */
DECL|method|ignoreUnavailable
specifier|public
name|boolean
name|ignoreUnavailable
parameter_list|()
block|{
return|return
operator|(
name|id
operator|&
name|IGNORE_UNAVAILABLE
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * @return Whether to ignore if a wildcard expression resolves to no concrete indices.      *         The `_all` string or empty list of indices count as wildcard expressions too.      */
DECL|method|allowNoIndices
specifier|public
name|boolean
name|allowNoIndices
parameter_list|()
block|{
return|return
operator|(
name|id
operator|&
name|ALLOW_NO_INDICES
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * @return Whether wildcard expressions should get expanded to open indices      */
DECL|method|expandWildcardsOpen
specifier|public
name|boolean
name|expandWildcardsOpen
parameter_list|()
block|{
return|return
operator|(
name|id
operator|&
name|EXPAND_WILDCARDS_OPEN
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * @return Whether wildcard expressions should get expanded to closed indices      */
DECL|method|expandWildcardsClosed
specifier|public
name|boolean
name|expandWildcardsClosed
parameter_list|()
block|{
return|return
operator|(
name|id
operator|&
name|EXPAND_WILDCARDS_CLOSED
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * @return Whether execution on closed indices is allowed.      */
DECL|method|forbidClosedIndices
specifier|public
name|boolean
name|forbidClosedIndices
parameter_list|()
block|{
return|return
operator|(
name|id
operator|&
name|FORBID_CLOSED_INDICES
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**      * @return whether aliases pointing to multiple indices are allowed      */
DECL|method|allowAliasesToMultipleIndices
specifier|public
name|boolean
name|allowAliasesToMultipleIndices
parameter_list|()
block|{
comment|//true is default here, for bw comp we keep the first 16 values
comment|//in the array same as before + the default value for the new flag
return|return
operator|(
name|id
operator|&
name|FORBID_ALIASES_TO_MULTIPLE_INDICES
operator|)
operator|==
literal|0
return|;
block|}
DECL|method|writeIndicesOptions
specifier|public
name|void
name|writeIndicesOptions
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_2_2
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_2_0
argument_list|)
condition|)
block|{
comment|// Target node doesn't know about the FORBID_CLOSED_INDICES and FORBID_ALIASES_TO_MULTIPLE_INDICES flags,
comment|// so unset the bits starting from the 5th position.
name|out
operator|.
name|write
argument_list|(
name|id
operator|&
literal|0xf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Target node doesn't know about the FORBID_CLOSED_INDICES flag,
comment|// so unset the bits starting from the 6th position.
name|out
operator|.
name|write
argument_list|(
name|id
operator|&
literal|0x1f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readIndicesOptions
specifier|public
specifier|static
name|IndicesOptions
name|readIndicesOptions
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if we read from a node that doesn't support the newly added flag (allowAliasesToMultipleIndices)
comment|//we just receive the old corresponding value with the new flag set to true (default)
name|byte
name|id
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|>=
name|VALUES
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"No valid missing index type id: "
operator|+
name|id
argument_list|)
throw|;
block|}
return|return
name|VALUES
index|[
name|id
index|]
return|;
block|}
DECL|method|fromOptions
specifier|public
specifier|static
name|IndicesOptions
name|fromOptions
parameter_list|(
name|boolean
name|ignoreUnavailable
parameter_list|,
name|boolean
name|allowNoIndices
parameter_list|,
name|boolean
name|expandToOpenIndices
parameter_list|,
name|boolean
name|expandToClosedIndices
parameter_list|)
block|{
return|return
name|fromOptions
argument_list|(
name|ignoreUnavailable
argument_list|,
name|allowNoIndices
argument_list|,
name|expandToOpenIndices
argument_list|,
name|expandToClosedIndices
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|fromOptions
specifier|public
specifier|static
name|IndicesOptions
name|fromOptions
parameter_list|(
name|boolean
name|ignoreUnavailable
parameter_list|,
name|boolean
name|allowNoIndices
parameter_list|,
name|boolean
name|expandToOpenIndices
parameter_list|,
name|boolean
name|expandToClosedIndices
parameter_list|,
name|IndicesOptions
name|defaultOptions
parameter_list|)
block|{
return|return
name|fromOptions
argument_list|(
name|ignoreUnavailable
argument_list|,
name|allowNoIndices
argument_list|,
name|expandToOpenIndices
argument_list|,
name|expandToClosedIndices
argument_list|,
name|defaultOptions
operator|.
name|allowAliasesToMultipleIndices
argument_list|()
argument_list|,
name|defaultOptions
operator|.
name|forbidClosedIndices
argument_list|()
argument_list|)
return|;
block|}
DECL|method|fromOptions
specifier|static
name|IndicesOptions
name|fromOptions
parameter_list|(
name|boolean
name|ignoreUnavailable
parameter_list|,
name|boolean
name|allowNoIndices
parameter_list|,
name|boolean
name|expandToOpenIndices
parameter_list|,
name|boolean
name|expandToClosedIndices
parameter_list|,
name|boolean
name|allowAliasesToMultipleIndices
parameter_list|,
name|boolean
name|forbidClosedIndices
parameter_list|)
block|{
name|byte
name|id
init|=
name|toByte
argument_list|(
name|ignoreUnavailable
argument_list|,
name|allowNoIndices
argument_list|,
name|expandToOpenIndices
argument_list|,
name|expandToClosedIndices
argument_list|,
name|allowAliasesToMultipleIndices
argument_list|,
name|forbidClosedIndices
argument_list|)
decl_stmt|;
return|return
name|VALUES
index|[
name|id
index|]
return|;
block|}
DECL|method|fromRequest
specifier|public
specifier|static
name|IndicesOptions
name|fromRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|IndicesOptions
name|defaultSettings
parameter_list|)
block|{
name|String
name|sWildcards
init|=
name|request
operator|.
name|param
argument_list|(
literal|"expand_wildcards"
argument_list|)
decl_stmt|;
name|String
name|sIgnoreUnavailable
init|=
name|request
operator|.
name|param
argument_list|(
literal|"ignore_unavailable"
argument_list|)
decl_stmt|;
name|String
name|sAllowNoIndices
init|=
name|request
operator|.
name|param
argument_list|(
literal|"allow_no_indices"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sWildcards
operator|==
literal|null
operator|&&
name|sIgnoreUnavailable
operator|==
literal|null
operator|&&
name|sAllowNoIndices
operator|==
literal|null
condition|)
block|{
return|return
name|defaultSettings
return|;
block|}
name|boolean
name|expandWildcardsOpen
init|=
name|defaultSettings
operator|.
name|expandWildcardsOpen
argument_list|()
decl_stmt|;
name|boolean
name|expandWildcardsClosed
init|=
name|defaultSettings
operator|.
name|expandWildcardsClosed
argument_list|()
decl_stmt|;
if|if
condition|(
name|sWildcards
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|wildcards
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|sWildcards
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|wildcard
range|:
name|wildcards
control|)
block|{
if|if
condition|(
literal|"open"
operator|.
name|equals
argument_list|(
name|wildcard
argument_list|)
condition|)
block|{
name|expandWildcardsOpen
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"closed"
operator|.
name|equals
argument_list|(
name|wildcard
argument_list|)
condition|)
block|{
name|expandWildcardsClosed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"No valid expand wildcard value ["
operator|+
name|wildcard
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
comment|//note that allowAliasesToMultipleIndices is not exposed, always true (only for internal use)
return|return
name|fromOptions
argument_list|(
name|toBool
argument_list|(
name|sIgnoreUnavailable
argument_list|,
name|defaultSettings
operator|.
name|ignoreUnavailable
argument_list|()
argument_list|)
argument_list|,
name|toBool
argument_list|(
name|sAllowNoIndices
argument_list|,
name|defaultSettings
operator|.
name|allowNoIndices
argument_list|()
argument_list|)
argument_list|,
name|expandWildcardsOpen
argument_list|,
name|expandWildcardsClosed
argument_list|,
name|defaultSettings
operator|.
name|allowAliasesToMultipleIndices
argument_list|()
argument_list|,
name|defaultSettings
operator|.
name|forbidClosedIndices
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @return indices options that requires every specified index to exist, expands wildcards only to open indices and      *         allows that no indices are resolved from wildcard expressions (not returning an error).      */
DECL|method|strictExpandOpen
specifier|public
specifier|static
name|IndicesOptions
name|strictExpandOpen
parameter_list|()
block|{
return|return
name|VALUES
index|[
literal|6
index|]
return|;
block|}
comment|/**      * @return indices options that requires every specified index to exist, expands wildcards only to open indices,      *         allows that no indices are resolved from wildcard expressions (not returning an error) and forbids the      *         use of closed indices by throwing an error.      */
DECL|method|strictExpandOpenAndForbidClosed
specifier|public
specifier|static
name|IndicesOptions
name|strictExpandOpenAndForbidClosed
parameter_list|()
block|{
return|return
name|VALUES
index|[
literal|38
index|]
return|;
block|}
comment|/**      * @return indices option that requires every specified index to exist, expands wildcards to both open and closed      * indices and allows that no indices are resolved from wildcard expressions (not returning an error).      */
DECL|method|strictExpand
specifier|public
specifier|static
name|IndicesOptions
name|strictExpand
parameter_list|()
block|{
return|return
name|VALUES
index|[
literal|14
index|]
return|;
block|}
comment|/**      * @return indices option that requires each specified index or alias to exist, doesn't expand wildcards and      * throws error if any of the aliases resolves to multiple indices      */
DECL|method|strictSingleIndexNoExpand
specifier|public
specifier|static
name|IndicesOptions
name|strictSingleIndexNoExpand
parameter_list|()
block|{
return|return
name|VALUES
index|[
name|FORBID_ALIASES_TO_MULTIPLE_INDICES
index|]
return|;
block|}
comment|/**      * @return indices options that ignores unavailable indices, expands wildcards only to open indices and      *         allows that no indices are resolved from wildcard expressions (not returning an error).      */
DECL|method|lenientExpandOpen
specifier|public
specifier|static
name|IndicesOptions
name|lenientExpandOpen
parameter_list|()
block|{
return|return
name|VALUES
index|[
literal|7
index|]
return|;
block|}
DECL|method|toByte
specifier|private
specifier|static
name|byte
name|toByte
parameter_list|(
name|boolean
name|ignoreUnavailable
parameter_list|,
name|boolean
name|allowNoIndices
parameter_list|,
name|boolean
name|wildcardExpandToOpen
parameter_list|,
name|boolean
name|wildcardExpandToClosed
parameter_list|,
name|boolean
name|allowAliasesToMultipleIndices
parameter_list|,
name|boolean
name|forbidClosedIndices
parameter_list|)
block|{
name|byte
name|id
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|ignoreUnavailable
condition|)
block|{
name|id
operator||=
name|IGNORE_UNAVAILABLE
expr_stmt|;
block|}
if|if
condition|(
name|allowNoIndices
condition|)
block|{
name|id
operator||=
name|ALLOW_NO_INDICES
expr_stmt|;
block|}
if|if
condition|(
name|wildcardExpandToOpen
condition|)
block|{
name|id
operator||=
name|EXPAND_WILDCARDS_OPEN
expr_stmt|;
block|}
if|if
condition|(
name|wildcardExpandToClosed
condition|)
block|{
name|id
operator||=
name|EXPAND_WILDCARDS_CLOSED
expr_stmt|;
block|}
comment|//true is default here, for bw comp we keep the first 16 values
comment|//in the array same as before + the default value for the new flag
if|if
condition|(
operator|!
name|allowAliasesToMultipleIndices
condition|)
block|{
name|id
operator||=
name|FORBID_ALIASES_TO_MULTIPLE_INDICES
expr_stmt|;
block|}
if|if
condition|(
name|forbidClosedIndices
condition|)
block|{
name|id
operator||=
name|FORBID_CLOSED_INDICES
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
DECL|method|toBool
specifier|private
specifier|static
name|boolean
name|toBool
parameter_list|(
name|String
name|sValue
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
operator|!
operator|(
name|sValue
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
operator|||
name|sValue
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
operator|||
name|sValue
operator|.
name|equals
argument_list|(
literal|"off"
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

