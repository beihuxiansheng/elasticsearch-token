begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

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
name|ParsingException
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
name|NamedWriteable
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
name|lucene
operator|.
name|BytesRefs
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
name|XContentParser
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
name|SuggestionSearchContext
operator|.
name|SuggestionContext
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Base class for the different suggestion implementations.  */
end_comment

begin_class
DECL|class|SuggestionBuilder
specifier|public
specifier|abstract
class|class
name|SuggestionBuilder
parameter_list|<
name|T
extends|extends
name|SuggestionBuilder
parameter_list|<
name|T
parameter_list|>
parameter_list|>
implements|implements
name|NamedWriteable
implements|,
name|ToXContent
block|{
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|text
specifier|protected
name|String
name|text
decl_stmt|;
DECL|field|prefix
specifier|protected
name|String
name|prefix
decl_stmt|;
DECL|field|regex
specifier|protected
name|String
name|regex
decl_stmt|;
DECL|field|analyzer
specifier|protected
name|String
name|analyzer
decl_stmt|;
DECL|field|size
specifier|protected
name|Integer
name|size
decl_stmt|;
DECL|field|shardSize
specifier|protected
name|Integer
name|shardSize
decl_stmt|;
DECL|field|TEXT_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|TEXT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
DECL|field|PREFIX_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|PREFIX_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"prefix"
argument_list|)
decl_stmt|;
DECL|field|REGEX_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|REGEX_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"regex"
argument_list|)
decl_stmt|;
DECL|field|FIELDNAME_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|FIELDNAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
DECL|field|ANALYZER_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|ANALYZER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"analyzer"
argument_list|)
decl_stmt|;
DECL|field|SIZE_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|SIZE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
DECL|field|SHARDSIZE_FIELD
specifier|protected
specifier|static
specifier|final
name|ParseField
name|SHARDSIZE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"shard_size"
argument_list|)
decl_stmt|;
comment|/**      * Creates a new suggestion.      * @param field field to execute suggestions on      */
DECL|method|SuggestionBuilder
specifier|protected
name|SuggestionBuilder
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|,
literal|"suggestion requires a field name"
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"suggestion field name is empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
comment|/**      * internal copy constructor that copies over all class fields from second SuggestionBuilder except field name.      */
DECL|method|SuggestionBuilder
specifier|protected
name|SuggestionBuilder
parameter_list|(
name|String
name|field
parameter_list|,
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|text
operator|=
name|in
operator|.
name|text
expr_stmt|;
name|prefix
operator|=
name|in
operator|.
name|prefix
expr_stmt|;
name|regex
operator|=
name|in
operator|.
name|regex
expr_stmt|;
name|analyzer
operator|=
name|in
operator|.
name|analyzer
expr_stmt|;
name|size
operator|=
name|in
operator|.
name|size
expr_stmt|;
name|shardSize
operator|=
name|in
operator|.
name|shardSize
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|SuggestionBuilder
specifier|protected
name|SuggestionBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|field
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|text
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|prefix
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|regex
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|analyzer
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|size
operator|=
name|in
operator|.
name|readOptionalVInt
argument_list|()
expr_stmt|;
name|shardSize
operator|=
name|in
operator|.
name|readOptionalVInt
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
specifier|final
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
name|writeString
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|regex
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalVInt
argument_list|(
name|shardSize
argument_list|)
expr_stmt|;
name|doWriteTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|doWriteTo
specifier|protected
specifier|abstract
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Same as in {@link SuggestBuilder#setGlobalText(String)}, but in the suggestion scope.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|text
specifier|public
name|T
name|text
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * get the text for this suggestion      */
DECL|method|text
specifier|public
name|String
name|text
parameter_list|()
block|{
return|return
name|this
operator|.
name|text
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|prefix
specifier|protected
name|T
name|prefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * get the prefix for this suggestion      */
DECL|method|prefix
specifier|public
name|String
name|prefix
parameter_list|()
block|{
return|return
name|this
operator|.
name|prefix
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|regex
specifier|protected
name|T
name|regex
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
name|this
operator|.
name|regex
operator|=
name|regex
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * get the regex for this suggestion      */
DECL|method|regex
specifier|public
name|String
name|regex
parameter_list|()
block|{
return|return
name|this
operator|.
name|regex
return|;
block|}
comment|/**      * get the {@link #field()} parameter      */
DECL|method|field
specifier|public
name|String
name|field
parameter_list|()
block|{
return|return
name|this
operator|.
name|field
return|;
block|}
comment|/**      * Sets the analyzer to analyse to suggest text with. Defaults to the search      * analyzer of the suggest field.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|analyzer
specifier|public
name|T
name|analyzer
parameter_list|(
name|String
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * get the {@link #analyzer()} parameter      */
DECL|method|analyzer
specifier|public
name|String
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
comment|/**      * Sets the maximum suggestions to be returned per suggest text term.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|size
specifier|public
name|T
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"size must be positive"
argument_list|)
throw|;
block|}
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * get the {@link #size()} parameter      */
DECL|method|size
specifier|public
name|Integer
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|size
return|;
block|}
comment|/**      * Sets the maximum number of suggested term to be retrieved from each      * individual shard. During the reduce phase the only the top N suggestions      * are returned based on the<code>size</code> option. Defaults to the      *<code>size</code> option.      *<p>      * Setting this to a value higher than the `size` can be useful in order to      * get a more accurate document frequency for suggested terms. Due to the      * fact that terms are partitioned amongst shards, the shard level document      * frequencies of suggestions may not be precise. Increasing this will make      * these document frequencies more precise.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|shardSize
specifier|public
name|T
name|shardSize
parameter_list|(
name|Integer
name|shardSize
parameter_list|)
block|{
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * get the {@link #shardSize()} parameter      */
DECL|method|shardSize
specifier|public
name|Integer
name|shardSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardSize
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
name|text
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|TEXT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|PREFIX_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|regex
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|REGEX_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|regex
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|getSuggesterName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|ANALYZER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|FIELDNAME_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|SIZE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardSize
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|SHARDSIZE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|shardSize
argument_list|)
expr_stmt|;
block|}
name|builder
operator|=
name|innerToXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|innerToXContent
specifier|protected
specifier|abstract
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
function_decl|;
DECL|method|fromXContent
specifier|static
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|Suggesters
name|suggesters
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|ParseFieldMatcher
name|parsefieldMatcher
init|=
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|String
name|suggestText
init|=
literal|null
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
name|String
name|regex
init|=
literal|null
decl_stmt|;
name|SuggestionBuilder
argument_list|<
name|?
argument_list|>
name|suggestionBuilder
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
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
name|token
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
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|TEXT_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|suggestText
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PREFIX_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|prefix
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PREFIX_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|regex
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"suggestion does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|suggestionBuilder
operator|=
name|suggesters
operator|.
name|getSuggester
argument_list|(
name|currentFieldName
argument_list|)
operator|.
name|innerFromXContent
argument_list|(
name|parseContext
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|suggestionBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"missing suggestion object"
argument_list|)
throw|;
block|}
if|if
condition|(
name|suggestText
operator|!=
literal|null
condition|)
block|{
name|suggestionBuilder
operator|.
name|text
argument_list|(
name|suggestText
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|suggestionBuilder
operator|.
name|prefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|regex
operator|!=
literal|null
condition|)
block|{
name|suggestionBuilder
operator|.
name|regex
argument_list|(
name|regex
argument_list|)
expr_stmt|;
block|}
return|return
name|suggestionBuilder
return|;
block|}
DECL|method|build
specifier|protected
specifier|abstract
name|SuggestionContext
name|build
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Transfers the text, prefix, regex, analyzer, field, size and shard size settings from the      * original {@link SuggestionBuilder} to the target {@link SuggestionContext}      */
DECL|method|populateCommonFields
specifier|protected
name|void
name|populateCommonFields
parameter_list|(
name|MapperService
name|mapperService
parameter_list|,
name|SuggestionSearchContext
operator|.
name|SuggestionContext
name|suggestionContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|,
literal|"field must not be null"
argument_list|)
expr_stmt|;
name|MappedFieldType
name|fieldType
init|=
name|mapperService
operator|.
name|fullName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no mapping found for field ["
operator|+
name|field
operator|+
literal|"]"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
comment|// no analyzer name passed in, so try the field's analyzer, or the default analyzer
if|if
condition|(
name|fieldType
operator|.
name|searchAnalyzer
argument_list|()
operator|==
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setAnalyzer
argument_list|(
name|mapperService
operator|.
name|searchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|suggestionContext
operator|.
name|setAnalyzer
argument_list|(
name|fieldType
operator|.
name|searchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Analyzer
name|luceneAnalyzer
init|=
name|mapperService
operator|.
name|getIndexAnalyzers
argument_list|()
operator|.
name|get
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|luceneAnalyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"analyzer ["
operator|+
name|analyzer
operator|+
literal|"] doesn't exists"
argument_list|)
throw|;
block|}
name|suggestionContext
operator|.
name|setAnalyzer
argument_list|(
name|luceneAnalyzer
argument_list|)
expr_stmt|;
block|}
name|suggestionContext
operator|.
name|setField
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardSize
operator|!=
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setShardSize
argument_list|(
name|shardSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if no shard size is set in builder, use size (or at least 5)
name|suggestionContext
operator|.
name|setShardSize
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|suggestionContext
operator|.
name|getSize
argument_list|()
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setText
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setPrefix
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|regex
operator|!=
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setRegex
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|regex
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
name|prefix
operator|==
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setPrefix
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|text
operator|==
literal|null
operator|&&
name|prefix
operator|!=
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setText
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|text
operator|==
literal|null
operator|&&
name|regex
operator|!=
literal|null
condition|)
block|{
name|suggestionContext
operator|.
name|setText
argument_list|(
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|regex
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSuggesterName
specifier|private
name|String
name|getSuggesterName
parameter_list|()
block|{
comment|//default impl returns the same as writeable name, but we keep the distinction between the two just to make sure
return|return
name|getWriteableName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|other
init|=
operator|(
name|T
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|text
argument_list|,
name|other
operator|.
name|text
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|prefix
argument_list|,
name|other
operator|.
name|prefix
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|regex
argument_list|,
name|other
operator|.
name|regex
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|field
argument_list|,
name|other
operator|.
name|field
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|analyzer
argument_list|,
name|other
operator|.
name|analyzer
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|size
argument_list|,
name|other
operator|.
name|size
argument_list|()
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|shardSize
argument_list|,
name|other
operator|.
name|shardSize
argument_list|()
argument_list|)
operator|&&
name|doEquals
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/**      * Indicates whether some other {@link SuggestionBuilder} of the same type is "equal to" this one.      */
DECL|method|doEquals
specifier|protected
specifier|abstract
name|boolean
name|doEquals
parameter_list|(
name|T
name|other
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|text
argument_list|,
name|prefix
argument_list|,
name|regex
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|,
name|size
argument_list|,
name|shardSize
argument_list|,
name|doHashCode
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * HashCode for the subclass of {@link SuggestionBuilder} to implement.      */
DECL|method|doHashCode
specifier|protected
specifier|abstract
name|int
name|doHashCode
parameter_list|()
function_decl|;
block|}
end_class

end_unit

