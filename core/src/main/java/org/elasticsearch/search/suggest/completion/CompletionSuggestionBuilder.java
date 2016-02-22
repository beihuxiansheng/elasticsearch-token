begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
package|;
end_package

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
name|Fuzziness
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
name|QueryShardContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|SuggestionBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|SuggestionSearchContext
operator|.
name|SuggestionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|context
operator|.
name|CategoryQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|context
operator|.
name|GeoQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|context
operator|.
name|QueryContext
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
name|Collections
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
name|Objects
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
comment|/**  * Defines a suggest command based on a prefix, typically to provide "auto-complete" functionality  * for users as they type search terms. The implementation of the completion service uses FSTs that  * are created at index-time and so must be defined in the mapping with the type "completion" before  * indexing.  */
end_comment

begin_class
DECL|class|CompletionSuggestionBuilder
specifier|public
class|class
name|CompletionSuggestionBuilder
extends|extends
name|SuggestionBuilder
argument_list|<
name|CompletionSuggestionBuilder
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|public
specifier|static
specifier|final
name|CompletionSuggestionBuilder
name|PROTOTYPE
init|=
operator|new
name|CompletionSuggestionBuilder
argument_list|()
decl_stmt|;
DECL|field|SUGGESTION_NAME
specifier|static
specifier|final
name|String
name|SUGGESTION_NAME
init|=
literal|"completion"
decl_stmt|;
DECL|field|PAYLOAD_FIELD
specifier|static
specifier|final
name|ParseField
name|PAYLOAD_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"payload"
argument_list|)
decl_stmt|;
DECL|field|CONTEXTS_FIELD
specifier|static
specifier|final
name|ParseField
name|CONTEXTS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"contexts"
argument_list|,
literal|"context"
argument_list|)
decl_stmt|;
DECL|field|fuzzyOptions
specifier|private
name|FuzzyOptions
name|fuzzyOptions
decl_stmt|;
DECL|field|regexOptions
specifier|private
name|RegexOptions
name|regexOptions
decl_stmt|;
DECL|field|queryContexts
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|QueryContext
argument_list|>
argument_list|>
name|queryContexts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|payloadFields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|payloadFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Sets the prefix to provide completions for.      * The prefix gets analyzed by the suggest analyzer.      */
annotation|@
name|Override
DECL|method|prefix
specifier|public
name|CompletionSuggestionBuilder
name|prefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|super
operator|.
name|prefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #prefix(String)} with fuzziness of<code>fuzziness</code>      */
DECL|method|prefix
specifier|public
name|CompletionSuggestionBuilder
name|prefix
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Fuzziness
name|fuzziness
parameter_list|)
block|{
name|super
operator|.
name|prefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|fuzzyOptions
operator|=
operator|new
name|FuzzyOptions
operator|.
name|Builder
argument_list|()
operator|.
name|setFuzziness
argument_list|(
name|fuzziness
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #prefix(String)} with full fuzzy options      * see {@link FuzzyOptions.Builder}      */
DECL|method|prefix
specifier|public
name|CompletionSuggestionBuilder
name|prefix
parameter_list|(
name|String
name|prefix
parameter_list|,
name|FuzzyOptions
name|fuzzyOptions
parameter_list|)
block|{
name|super
operator|.
name|prefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|fuzzyOptions
operator|=
name|fuzzyOptions
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets a regular expression pattern for prefixes to provide completions for.      */
annotation|@
name|Override
DECL|method|regex
specifier|public
name|CompletionSuggestionBuilder
name|regex
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
name|super
operator|.
name|regex
argument_list|(
name|regex
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #regex(String)} with full regular expression options      * see {@link RegexOptions.Builder}      */
DECL|method|regex
specifier|public
name|CompletionSuggestionBuilder
name|regex
parameter_list|(
name|String
name|regex
parameter_list|,
name|RegexOptions
name|regexOptions
parameter_list|)
block|{
name|this
operator|.
name|regex
argument_list|(
name|regex
argument_list|)
expr_stmt|;
name|this
operator|.
name|regexOptions
operator|=
name|regexOptions
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the fields to be returned as suggestion payload.      * Note: Only doc values enabled fields are supported      */
DECL|method|payload
specifier|public
name|CompletionSuggestionBuilder
name|payload
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|payloadFields
operator|.
name|addAll
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets query contexts for a category context      * @param name of the category context to execute on      * @param queryContexts a list of {@link CategoryQueryContext}      */
DECL|method|categoryContexts
specifier|public
name|CompletionSuggestionBuilder
name|categoryContexts
parameter_list|(
name|String
name|name
parameter_list|,
name|CategoryQueryContext
modifier|...
name|queryContexts
parameter_list|)
block|{
return|return
name|contexts
argument_list|(
name|name
argument_list|,
name|queryContexts
argument_list|)
return|;
block|}
comment|/**      * Sets query contexts for a geo context      * @param name of the geo context to execute on      * @param queryContexts a list of {@link GeoQueryContext}      */
DECL|method|geoContexts
specifier|public
name|CompletionSuggestionBuilder
name|geoContexts
parameter_list|(
name|String
name|name
parameter_list|,
name|GeoQueryContext
modifier|...
name|queryContexts
parameter_list|)
block|{
return|return
name|contexts
argument_list|(
name|name
argument_list|,
name|queryContexts
argument_list|)
return|;
block|}
DECL|method|contexts
specifier|private
name|CompletionSuggestionBuilder
name|contexts
parameter_list|(
name|String
name|name
parameter_list|,
name|QueryContext
modifier|...
name|queryContexts
parameter_list|)
block|{
name|List
argument_list|<
name|QueryContext
argument_list|>
name|contexts
init|=
name|this
operator|.
name|queryContexts
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|contexts
operator|==
literal|null
condition|)
block|{
name|contexts
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryContexts
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|contexts
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|addAll
argument_list|(
name|contexts
argument_list|,
name|queryContexts
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|innerToXContent
specifier|protected
name|XContentBuilder
name|innerToXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|payloadFields
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|PAYLOAD_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|payloadFields
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyOptions
operator|!=
literal|null
condition|)
block|{
name|fuzzyOptions
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|regexOptions
operator|!=
literal|null
condition|)
block|{
name|regexOptions
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryContexts
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|CONTEXTS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|QueryContext
argument_list|>
argument_list|>
name|entry
range|:
name|this
operator|.
name|queryContexts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ToXContent
name|queryContext
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|queryContext
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|innerFromXContent
specifier|protected
name|CompletionSuggestionBuilder
name|innerFromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NORELEASE implement parsing logic
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|innerBuild
specifier|protected
name|SuggestionContext
name|innerBuild
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|CompletionSuggestionContext
name|suggestionContext
init|=
operator|new
name|CompletionSuggestionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
comment|// copy over common settings to each suggestion builder
name|populateCommonFields
argument_list|(
name|context
operator|.
name|getMapperService
argument_list|()
argument_list|,
name|suggestionContext
argument_list|)
expr_stmt|;
comment|// NORELEASE
comment|// still need to populate CompletionSuggestionContext's specific settings
return|return
name|suggestionContext
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|SUGGESTION_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|public
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|payloadFieldExists
init|=
name|payloadFields
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|payloadFieldExists
argument_list|)
expr_stmt|;
if|if
condition|(
name|payloadFieldExists
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|payloadFields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|payloadField
range|:
name|payloadFields
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|payloadField
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|fuzzyOptions
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|fuzzyOptions
operator|!=
literal|null
condition|)
block|{
name|fuzzyOptions
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|regexOptions
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|regexOptions
operator|!=
literal|null
condition|)
block|{
name|regexOptions
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|boolean
name|queryContextsExists
init|=
name|queryContexts
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|queryContextsExists
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryContextsExists
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|queryContexts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|QueryContext
argument_list|>
argument_list|>
name|namedQueryContexts
range|:
name|queryContexts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|namedQueryContexts
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|QueryContext
argument_list|>
name|queryContexts
init|=
name|namedQueryContexts
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|queryContexts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryContext
name|queryContext
range|:
name|queryContexts
control|)
block|{
name|out
operator|.
name|writeCompletionSuggestionQueryContext
argument_list|(
name|queryContext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|public
name|CompletionSuggestionBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CompletionSuggestionBuilder
name|completionSuggestionBuilder
init|=
operator|new
name|CompletionSuggestionBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|numPayloadField
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
name|numPayloadField
condition|;
name|i
operator|++
control|)
block|{
name|completionSuggestionBuilder
operator|.
name|payloadFields
operator|.
name|add
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|completionSuggestionBuilder
operator|.
name|fuzzyOptions
operator|=
name|FuzzyOptions
operator|.
name|readFuzzyOptions
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|completionSuggestionBuilder
operator|.
name|regexOptions
operator|=
name|RegexOptions
operator|.
name|readRegexOptions
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|numNamedQueryContexts
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
name|numNamedQueryContexts
condition|;
name|i
operator|++
control|)
block|{
name|String
name|queryContextName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|int
name|numQueryContexts
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryContext
argument_list|>
name|queryContexts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numQueryContexts
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numQueryContexts
condition|;
name|j
operator|++
control|)
block|{
name|queryContexts
operator|.
name|add
argument_list|(
name|in
operator|.
name|readCompletionSuggestionQueryContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|completionSuggestionBuilder
operator|.
name|queryContexts
operator|.
name|put
argument_list|(
name|queryContextName
argument_list|,
name|queryContexts
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|completionSuggestionBuilder
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|CompletionSuggestionBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|payloadFields
argument_list|,
name|other
operator|.
name|payloadFields
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|fuzzyOptions
argument_list|,
name|other
operator|.
name|fuzzyOptions
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|regexOptions
argument_list|,
name|other
operator|.
name|regexOptions
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|queryContexts
argument_list|,
name|other
operator|.
name|queryContexts
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|payloadFields
argument_list|,
name|fuzzyOptions
argument_list|,
name|regexOptions
argument_list|,
name|queryContexts
argument_list|)
return|;
block|}
block|}
end_class

end_unit

