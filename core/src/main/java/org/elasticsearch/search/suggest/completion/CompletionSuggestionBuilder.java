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
name|ElasticsearchParseException
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
name|bytes
operator|.
name|BytesReference
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
name|XContentParser
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
name|mapper
operator|.
name|CompletionFieldMapper
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
name|mapper
operator|.
name|MappedFieldType
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
name|mapper
operator|.
name|MapperService
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
name|ContextMapping
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
name|ContextMappings
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
name|HashMap
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
DECL|field|SUGGESTION_NAME
specifier|static
specifier|final
name|String
name|SUGGESTION_NAME
init|=
literal|"completion"
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
comment|/**      * {      *     "field" : STRING      *     "size" : INT      *     "fuzzy" : BOOLEAN | FUZZY_OBJECT      *     "contexts" : QUERY_CONTEXTS      *     "regex" : REGEX_OBJECT      *     "payload" : STRING_ARRAY      * }      */
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|CompletionSuggestionBuilder
operator|.
name|InnerBuilder
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|SUGGESTION_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
static|static
block|{
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|parser
parameter_list|,
name|completionSuggestionContext
parameter_list|,
name|context
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|completionSuggestionContext
operator|.
name|fuzzyOptions
operator|=
operator|new
name|FuzzyOptions
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|completionSuggestionContext
operator|.
name|fuzzyOptions
operator|=
name|FuzzyOptions
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|FuzzyOptions
operator|.
name|FUZZY_OPTIONS
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT_OR_BOOLEAN
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|parser
parameter_list|,
name|completionSuggestionContext
parameter_list|,
name|context
parameter_list|)
lambda|->
name|completionSuggestionContext
operator|.
name|regexOptions
operator|=
name|RegexOptions
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
argument_list|,
name|RegexOptions
operator|.
name|REGEX_OPTIONS
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareString
argument_list|(
name|CompletionSuggestionBuilder
operator|.
name|InnerBuilder
operator|::
name|field
argument_list|,
name|FIELDNAME_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareString
argument_list|(
name|CompletionSuggestionBuilder
operator|.
name|InnerBuilder
operator|::
name|analyzer
argument_list|,
name|ANALYZER_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareInt
argument_list|(
name|CompletionSuggestionBuilder
operator|.
name|InnerBuilder
operator|::
name|size
argument_list|,
name|SIZE_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareInt
argument_list|(
name|CompletionSuggestionBuilder
operator|.
name|InnerBuilder
operator|::
name|shardSize
argument_list|,
name|SHARDSIZE_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|p
parameter_list|,
name|v
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
comment|// Copy the current structure. We will parse, once the mapping is provided
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
name|copyCurrentStructure
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|v
operator|.
name|contextBytes
operator|=
name|builder
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|p
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
argument_list|,
name|CONTEXTS_FIELD
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
comment|// context is deprecated
block|}
DECL|field|fuzzyOptions
specifier|protected
name|FuzzyOptions
name|fuzzyOptions
decl_stmt|;
DECL|field|regexOptions
specifier|protected
name|RegexOptions
name|regexOptions
decl_stmt|;
DECL|field|contextBytes
specifier|protected
name|BytesReference
name|contextBytes
init|=
literal|null
decl_stmt|;
DECL|method|CompletionSuggestionBuilder
specifier|public
name|CompletionSuggestionBuilder
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
comment|/**      * internal copy constructor that copies over all class fields except for the field which is      * set to the one provided in the first argument      */
DECL|method|CompletionSuggestionBuilder
specifier|private
name|CompletionSuggestionBuilder
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|CompletionSuggestionBuilder
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|fieldname
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|fuzzyOptions
operator|=
name|in
operator|.
name|fuzzyOptions
expr_stmt|;
name|regexOptions
operator|=
name|in
operator|.
name|regexOptions
expr_stmt|;
name|contextBytes
operator|=
name|in
operator|.
name|contextBytes
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|CompletionSuggestionBuilder
specifier|public
name|CompletionSuggestionBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fuzzyOptions
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|FuzzyOptions
operator|::
operator|new
argument_list|)
expr_stmt|;
name|regexOptions
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|RegexOptions
operator|::
operator|new
argument_list|)
expr_stmt|;
name|contextBytes
operator|=
name|in
operator|.
name|readOptionalBytesReference
argument_list|()
expr_stmt|;
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
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|fuzzyOptions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|regexOptions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalBytesReference
argument_list|(
name|contextBytes
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * Sets query contexts for completion      * @param queryContexts named query contexts      *                      see {@link org.elasticsearch.search.suggest.completion.context.CategoryQueryContext}      *                      and {@link org.elasticsearch.search.suggest.completion.context.GeoQueryContext}      */
DECL|method|contexts
specifier|public
name|CompletionSuggestionBuilder
name|contexts
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|ToXContent
argument_list|>
argument_list|>
name|queryContexts
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryContexts
argument_list|,
literal|"contexts must not be null"
argument_list|)
expr_stmt|;
try|try
block|{
name|XContentBuilder
name|contentBuilder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|contentBuilder
operator|.
name|startObject
argument_list|()
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
name|?
extends|extends
name|ToXContent
argument_list|>
argument_list|>
name|contextEntry
range|:
name|queryContexts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|contentBuilder
operator|.
name|startArray
argument_list|(
name|contextEntry
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
name|contextEntry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|queryContext
operator|.
name|toXContent
argument_list|(
name|contentBuilder
argument_list|,
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
block|}
name|contentBuilder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|contentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|contexts
argument_list|(
name|contentBuilder
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
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|contexts
specifier|private
name|CompletionSuggestionBuilder
name|contexts
parameter_list|(
name|XContentBuilder
name|contextBuilder
parameter_list|)
block|{
name|contextBytes
operator|=
name|contextBuilder
operator|.
name|bytes
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|class|InnerBuilder
specifier|private
specifier|static
class|class
name|InnerBuilder
extends|extends
name|CompletionSuggestionBuilder
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|method|InnerBuilder
specifier|public
name|InnerBuilder
parameter_list|()
block|{
name|super
argument_list|(
literal|"_na_"
argument_list|)
expr_stmt|;
block|}
DECL|method|field
specifier|private
name|InnerBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
return|return
name|this
return|;
block|}
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
name|contextBytes
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|rawField
argument_list|(
name|CONTEXTS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|contextBytes
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|CompletionSuggestionBuilder
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|CompletionSuggestionBuilder
operator|.
name|InnerBuilder
name|builder
init|=
operator|new
name|CompletionSuggestionBuilder
operator|.
name|InnerBuilder
argument_list|()
decl_stmt|;
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|builder
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|field
init|=
name|builder
operator|.
name|field
decl_stmt|;
comment|// now we should have field name, check and copy fields over to the suggestion builder we return
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"the required field option ["
operator|+
name|FIELDNAME_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|"] is missing"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CompletionSuggestionBuilder
argument_list|(
name|field
argument_list|,
name|builder
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|SuggestionContext
name|build
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
specifier|final
name|MapperService
name|mapperService
init|=
name|context
operator|.
name|getMapperService
argument_list|()
decl_stmt|;
name|populateCommonFields
argument_list|(
name|mapperService
argument_list|,
name|suggestionContext
argument_list|)
expr_stmt|;
name|suggestionContext
operator|.
name|setFuzzyOptions
argument_list|(
name|fuzzyOptions
argument_list|)
expr_stmt|;
name|suggestionContext
operator|.
name|setRegexOptions
argument_list|(
name|regexOptions
argument_list|)
expr_stmt|;
name|MappedFieldType
name|mappedFieldType
init|=
name|mapperService
operator|.
name|fullName
argument_list|(
name|suggestionContext
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappedFieldType
operator|==
literal|null
operator|||
name|mappedFieldType
operator|instanceof
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field ["
operator|+
name|suggestionContext
operator|.
name|getField
argument_list|()
operator|+
literal|"] is not a completion suggest field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|mappedFieldType
operator|instanceof
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
condition|)
block|{
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|type
init|=
operator|(
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
operator|)
name|mappedFieldType
decl_stmt|;
name|suggestionContext
operator|.
name|setFieldType
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|hasContextMappings
argument_list|()
operator|&&
name|contextBytes
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|XContentParser
name|contextParser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contextBytes
argument_list|)
operator|.
name|createParser
argument_list|(
name|context
operator|.
name|getXContentRegistry
argument_list|()
argument_list|,
name|contextBytes
argument_list|)
init|)
block|{
if|if
condition|(
name|type
operator|.
name|hasContextMappings
argument_list|()
operator|&&
name|contextParser
operator|!=
literal|null
condition|)
block|{
name|ContextMappings
name|contextMappings
init|=
name|type
operator|.
name|getContextMappings
argument_list|()
decl_stmt|;
name|contextParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ContextMapping
operator|.
name|InternalQueryContext
argument_list|>
argument_list|>
name|queryContexts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|contextMappings
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|contextParser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
name|XContentParser
operator|.
name|Token
name|currentToken
decl_stmt|;
name|String
name|currentFieldName
decl_stmt|;
while|while
condition|(
operator|(
name|currentToken
operator|=
name|contextParser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|currentToken
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|contextParser
operator|.
name|currentName
argument_list|()
expr_stmt|;
specifier|final
name|ContextMapping
name|mapping
init|=
name|contextMappings
operator|.
name|get
argument_list|(
name|currentFieldName
argument_list|)
decl_stmt|;
name|queryContexts
operator|.
name|put
argument_list|(
name|currentFieldName
argument_list|,
name|mapping
operator|.
name|parseQueryContext
argument_list|(
name|context
operator|.
name|newParseContext
argument_list|(
name|contextParser
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|suggestionContext
operator|.
name|setQueryContexts
argument_list|(
name|queryContexts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|contextBytes
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"suggester ["
operator|+
name|type
operator|.
name|name
argument_list|()
operator|+
literal|"] doesn't expect any context"
argument_list|)
throw|;
block|}
block|}
assert|assert
name|suggestionContext
operator|.
name|getFieldType
argument_list|()
operator|!=
literal|null
operator|:
literal|"no completion field type set"
assert|;
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
name|contextBytes
argument_list|,
name|other
operator|.
name|contextBytes
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
name|fuzzyOptions
argument_list|,
name|regexOptions
argument_list|,
name|contextBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

