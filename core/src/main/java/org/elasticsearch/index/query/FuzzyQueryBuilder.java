begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FuzzyQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|MultiTermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|common
operator|.
name|logging
operator|.
name|DeprecationLogger
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
name|logging
operator|.
name|Loggers
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
name|query
operator|.
name|support
operator|.
name|QueryParsers
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * A Query that does fuzzy matching for a specific value.  *  * @deprecated Fuzzy queries are not useful enough. This class will be removed with Elasticsearch 4.0. In most cases you may want to use  * a match query with the fuzziness parameter for strings or range queries for numeric and date fields.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|FuzzyQueryBuilder
specifier|public
class|class
name|FuzzyQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|FuzzyQueryBuilder
argument_list|>
implements|implements
name|MultiTermQueryBuilder
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"fuzzy"
decl_stmt|;
DECL|field|DEPRECATION_LOGGER
specifier|private
specifier|static
specifier|final
name|DeprecationLogger
name|DEPRECATION_LOGGER
init|=
operator|new
name|DeprecationLogger
argument_list|(
name|Loggers
operator|.
name|getLogger
argument_list|(
name|FuzzyQueryBuilder
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Default maximum edit distance. Defaults to AUTO. */
DECL|field|DEFAULT_FUZZINESS
specifier|public
specifier|static
specifier|final
name|Fuzziness
name|DEFAULT_FUZZINESS
init|=
name|Fuzziness
operator|.
name|AUTO
decl_stmt|;
comment|/** Default number of initial characters which will not be âfuzzifiedâ. Defaults to 0. */
DECL|field|DEFAULT_PREFIX_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PREFIX_LENGTH
init|=
name|FuzzyQuery
operator|.
name|defaultPrefixLength
decl_stmt|;
comment|/** Default maximum number of terms that the fuzzy query will expand to. Defaults to 50. */
DECL|field|DEFAULT_MAX_EXPANSIONS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_EXPANSIONS
init|=
name|FuzzyQuery
operator|.
name|defaultMaxExpansions
decl_stmt|;
comment|/** Default as to whether transpositions should be treated as a primitive edit operation,      * instead of classic Levenshtein algorithm. Defaults to false. */
DECL|field|DEFAULT_TRANSPOSITIONS
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_TRANSPOSITIONS
init|=
literal|false
decl_stmt|;
DECL|field|TERM_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|TERM_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"term"
argument_list|)
decl_stmt|;
DECL|field|VALUE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|VALUE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
DECL|field|PREFIX_LENGTH_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|PREFIX_LENGTH_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"prefix_length"
argument_list|)
decl_stmt|;
DECL|field|MAX_EXPANSIONS_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|MAX_EXPANSIONS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"max_expansions"
argument_list|)
decl_stmt|;
DECL|field|TRANSPOSITIONS_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|TRANSPOSITIONS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"transpositions"
argument_list|)
decl_stmt|;
DECL|field|REWRITE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|REWRITE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"rewrite"
argument_list|)
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|Object
name|value
decl_stmt|;
DECL|field|fuzziness
specifier|private
name|Fuzziness
name|fuzziness
init|=
name|DEFAULT_FUZZINESS
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|int
name|prefixLength
init|=
name|DEFAULT_PREFIX_LENGTH
decl_stmt|;
DECL|field|maxExpansions
specifier|private
name|int
name|maxExpansions
init|=
name|DEFAULT_MAX_EXPANSIONS
decl_stmt|;
comment|//LUCENE 4 UPGRADE  we need a testcase for this + documentation
DECL|field|transpositions
specifier|private
name|boolean
name|transpositions
init|=
name|DEFAULT_TRANSPOSITIONS
decl_stmt|;
DECL|field|rewrite
specifier|private
name|String
name|rewrite
decl_stmt|;
comment|/**      * Constructs a new fuzzy query.      *      * @param fieldName  The name of the field      * @param value The value of the text      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new fuzzy query.      *      * @param fieldName  The name of the field      * @param value The value of the text      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new fuzzy query.      *      * @param fieldName  The name of the field      * @param value The value of the text      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new fuzzy query.      *      * @param fieldName  The name of the field      * @param value The value of the text      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new fuzzy query.      *      * @param fieldName  The name of the field      * @param value The value of the text      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new fuzzy query.      *      * @param fieldName  The name of the field      * @param value The value of the text      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new fuzzy query.      *      * @param fieldName  The name of the field      * @param value The value of the term      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|DEPRECATION_LOGGER
operator|.
name|deprecated
argument_list|(
literal|"{} query is deprecated. Instead use the [match] query with fuzziness parameter"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field name cannot be null or empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"query value cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|convertToBytesRefIfString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|FuzzyQueryBuilder
specifier|public
name|FuzzyQueryBuilder
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
name|fieldName
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|value
operator|=
name|in
operator|.
name|readGenericValue
argument_list|()
expr_stmt|;
name|fuzziness
operator|=
operator|new
name|Fuzziness
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|prefixLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|maxExpansions
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|transpositions
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|rewrite
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
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
name|writeString
argument_list|(
name|this
operator|.
name|fieldName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|this
operator|.
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|fuzziness
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
name|this
operator|.
name|prefixLength
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|this
operator|.
name|maxExpansions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|this
operator|.
name|transpositions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|this
operator|.
name|rewrite
argument_list|)
expr_stmt|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldName
return|;
block|}
DECL|method|value
specifier|public
name|Object
name|value
parameter_list|()
block|{
return|return
name|convertToStringIfBytesRef
argument_list|(
name|this
operator|.
name|value
argument_list|)
return|;
block|}
DECL|method|fuzziness
specifier|public
name|FuzzyQueryBuilder
name|fuzziness
parameter_list|(
name|Fuzziness
name|fuzziness
parameter_list|)
block|{
name|this
operator|.
name|fuzziness
operator|=
operator|(
name|fuzziness
operator|==
literal|null
operator|)
condition|?
name|DEFAULT_FUZZINESS
else|:
name|fuzziness
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fuzziness
specifier|public
name|Fuzziness
name|fuzziness
parameter_list|()
block|{
return|return
name|this
operator|.
name|fuzziness
return|;
block|}
DECL|method|prefixLength
specifier|public
name|FuzzyQueryBuilder
name|prefixLength
parameter_list|(
name|int
name|prefixLength
parameter_list|)
block|{
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|prefixLength
specifier|public
name|int
name|prefixLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|prefixLength
return|;
block|}
DECL|method|maxExpansions
specifier|public
name|FuzzyQueryBuilder
name|maxExpansions
parameter_list|(
name|int
name|maxExpansions
parameter_list|)
block|{
name|this
operator|.
name|maxExpansions
operator|=
name|maxExpansions
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxExpansions
specifier|public
name|int
name|maxExpansions
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxExpansions
return|;
block|}
DECL|method|transpositions
specifier|public
name|FuzzyQueryBuilder
name|transpositions
parameter_list|(
name|boolean
name|transpositions
parameter_list|)
block|{
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|transpositions
specifier|public
name|boolean
name|transpositions
parameter_list|()
block|{
return|return
name|this
operator|.
name|transpositions
return|;
block|}
DECL|method|rewrite
specifier|public
name|FuzzyQueryBuilder
name|rewrite
parameter_list|(
name|String
name|rewrite
parameter_list|)
block|{
name|this
operator|.
name|rewrite
operator|=
name|rewrite
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|rewrite
specifier|public
name|String
name|rewrite
parameter_list|()
block|{
return|return
name|this
operator|.
name|rewrite
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
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
name|builder
operator|.
name|startObject
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|VALUE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|convertToStringIfBytesRef
argument_list|(
name|this
operator|.
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|fuzziness
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|PREFIX_LENGTH_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|MAX_EXPANSIONS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxExpansions
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|TRANSPOSITIONS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|transpositions
argument_list|)
expr_stmt|;
if|if
condition|(
name|rewrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|REWRITE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|rewrite
argument_list|)
expr_stmt|;
block|}
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|Optional
argument_list|<
name|FuzzyQueryBuilder
argument_list|>
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
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
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|Object
name|value
init|=
literal|null
decl_stmt|;
name|Fuzziness
name|fuzziness
init|=
name|FuzzyQueryBuilder
operator|.
name|DEFAULT_FUZZINESS
decl_stmt|;
name|int
name|prefixLength
init|=
name|FuzzyQueryBuilder
operator|.
name|DEFAULT_PREFIX_LENGTH
decl_stmt|;
name|int
name|maxExpansions
init|=
name|FuzzyQueryBuilder
operator|.
name|DEFAULT_MAX_EXPANSIONS
decl_stmt|;
name|boolean
name|transpositions
init|=
name|FuzzyQueryBuilder
operator|.
name|DEFAULT_TRANSPOSITIONS
decl_stmt|;
name|String
name|rewrite
init|=
literal|null
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
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
name|parseContext
operator|.
name|isDeprecatedSetting
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// skip
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
name|throwParsingExceptionOnMultipleFields
argument_list|(
name|NAME
argument_list|,
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|fieldName
argument_list|,
name|currentFieldName
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
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
else|else
block|{
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|TERM_FIELD
argument_list|)
condition|)
block|{
name|value
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|VALUE_FIELD
argument_list|)
condition|)
block|{
name|value
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|BOOST_FIELD
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|Fuzziness
operator|.
name|FIELD
argument_list|)
condition|)
block|{
name|fuzziness
operator|=
name|Fuzziness
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|PREFIX_LENGTH_FIELD
argument_list|)
condition|)
block|{
name|prefixLength
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MAX_EXPANSIONS_FIELD
argument_list|)
condition|)
block|{
name|maxExpansions
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|TRANSPOSITIONS_FIELD
argument_list|)
condition|)
block|{
name|transpositions
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|REWRITE_FIELD
argument_list|)
condition|)
block|{
name|rewrite
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|NAME_FIELD
argument_list|)
condition|)
block|{
name|queryName
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
literal|"[fuzzy] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
else|else
block|{
name|throwParsingExceptionOnMultipleFields
argument_list|(
name|NAME
argument_list|,
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|fieldName
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
name|value
operator|=
name|parser
operator|.
name|objectBytes
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|FuzzyQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
operator|.
name|fuzziness
argument_list|(
name|fuzziness
argument_list|)
operator|.
name|prefixLength
argument_list|(
name|prefixLength
argument_list|)
operator|.
name|maxExpansions
argument_list|(
name|maxExpansions
argument_list|)
operator|.
name|transpositions
argument_list|(
name|transpositions
argument_list|)
operator|.
name|rewrite
argument_list|(
name|rewrite
argument_list|)
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
argument_list|)
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
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|String
name|rewrite
init|=
name|this
operator|.
name|rewrite
decl_stmt|;
if|if
condition|(
name|rewrite
operator|==
literal|null
operator|&&
name|context
operator|.
name|isFilter
argument_list|()
condition|)
block|{
name|rewrite
operator|=
name|QueryParsers
operator|.
name|CONSTANT_SCORE
operator|.
name|getPreferredName
argument_list|()
expr_stmt|;
block|}
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
name|fieldType
operator|.
name|fuzzyQuery
argument_list|(
name|value
argument_list|,
name|fuzziness
argument_list|,
name|prefixLength
argument_list|,
name|maxExpansions
argument_list|,
name|transpositions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|int
name|maxEdits
init|=
name|fuzziness
operator|.
name|asDistance
argument_list|(
name|BytesRefs
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
name|maxEdits
argument_list|,
name|prefixLength
argument_list|,
name|maxExpansions
argument_list|,
name|transpositions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|instanceof
name|MultiTermQuery
condition|)
block|{
name|MultiTermQuery
operator|.
name|RewriteMethod
name|rewriteMethod
init|=
name|QueryParsers
operator|.
name|parseRewriteMethod
argument_list|(
name|context
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|,
name|rewrite
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|QueryParsers
operator|.
name|setRewriteMethod
argument_list|(
operator|(
name|MultiTermQuery
operator|)
name|query
argument_list|,
name|rewriteMethod
argument_list|)
expr_stmt|;
block|}
return|return
name|query
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
name|fieldName
argument_list|,
name|value
argument_list|,
name|fuzziness
argument_list|,
name|prefixLength
argument_list|,
name|maxExpansions
argument_list|,
name|transpositions
argument_list|,
name|rewrite
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|FuzzyQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|,
name|other
operator|.
name|fieldName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|value
argument_list|,
name|other
operator|.
name|value
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|fuzziness
argument_list|,
name|other
operator|.
name|fuzziness
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|prefixLength
argument_list|,
name|other
operator|.
name|prefixLength
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|maxExpansions
argument_list|,
name|other
operator|.
name|maxExpansions
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|transpositions
argument_list|,
name|other
operator|.
name|transpositions
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|rewrite
argument_list|,
name|other
operator|.
name|rewrite
argument_list|)
return|;
block|}
block|}
end_class

end_unit

