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
name|search
operator|.
name|FuzzyQuery
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
name|search
operator|.
name|MatchQuery
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
name|search
operator|.
name|MatchQuery
operator|.
name|ZeroTermsQuery
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
comment|/**  *  */
end_comment

begin_class
DECL|class|MatchQueryParser
specifier|public
class|class
name|MatchQueryParser
implements|implements
name|QueryParser
argument_list|<
name|MatchQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|MatchQueryBuilder
operator|.
name|NAME
block|,
literal|"match_phrase"
block|,
literal|"matchPhrase"
block|,
literal|"match_phrase_prefix"
block|,
literal|"matchPhrasePrefix"
block|,
literal|"matchFuzzy"
block|,
literal|"match_fuzzy"
block|,
literal|"fuzzy_match"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|MatchQueryBuilder
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
name|MatchQuery
operator|.
name|Type
name|type
init|=
name|MatchQuery
operator|.
name|Type
operator|.
name|BOOLEAN
decl_stmt|;
if|if
condition|(
literal|"match_phrase"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
operator|||
literal|"matchPhrase"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
operator|||
literal|"text_phrase"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
operator|||
literal|"textPhrase"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
condition|)
block|{
name|type
operator|=
name|MatchQuery
operator|.
name|Type
operator|.
name|PHRASE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"match_phrase_prefix"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
operator|||
literal|"matchPhrasePrefix"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
operator|||
literal|"text_phrase_prefix"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
operator|||
literal|"textPhrasePrefix"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
condition|)
block|{
name|type
operator|=
name|MatchQuery
operator|.
name|Type
operator|.
name|PHRASE_PREFIX
expr_stmt|;
block|}
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
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
literal|"[match] query malformed, no field"
argument_list|)
throw|;
block|}
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|Object
name|value
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
name|minimumShouldMatch
init|=
literal|null
decl_stmt|;
name|String
name|analyzer
init|=
literal|null
decl_stmt|;
name|Operator
name|operator
init|=
name|MatchQueryBuilder
operator|.
name|DEFAULT_OPERATOR
decl_stmt|;
name|int
name|slop
init|=
name|MatchQuery
operator|.
name|DEFAULT_PHRASE_SLOP
decl_stmt|;
name|Fuzziness
name|fuzziness
init|=
literal|null
decl_stmt|;
name|int
name|prefixLength
init|=
name|FuzzyQuery
operator|.
name|defaultPrefixLength
decl_stmt|;
name|int
name|maxExpansion
init|=
name|FuzzyQuery
operator|.
name|defaultMaxExpansions
decl_stmt|;
name|boolean
name|fuzzyTranspositions
init|=
name|FuzzyQuery
operator|.
name|defaultTranspositions
decl_stmt|;
name|String
name|fuzzyRewrite
init|=
literal|null
decl_stmt|;
name|boolean
name|lenient
init|=
name|MatchQuery
operator|.
name|DEFAULT_LENIENCY
decl_stmt|;
name|Float
name|cutOffFrequency
init|=
literal|null
decl_stmt|;
name|ZeroTermsQuery
name|zeroTermsQuery
init|=
name|MatchQuery
operator|.
name|DEFAULT_ZERO_TERMS_QUERY
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
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
name|String
name|currentFieldName
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|value
operator|=
name|parser
operator|.
name|objectText
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|String
name|tStr
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"boolean"
operator|.
name|equals
argument_list|(
name|tStr
argument_list|)
condition|)
block|{
name|type
operator|=
name|MatchQuery
operator|.
name|Type
operator|.
name|BOOLEAN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"phrase"
operator|.
name|equals
argument_list|(
name|tStr
argument_list|)
condition|)
block|{
name|type
operator|=
name|MatchQuery
operator|.
name|Type
operator|.
name|PHRASE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"phrase_prefix"
operator|.
name|equals
argument_list|(
name|tStr
argument_list|)
operator|||
literal|"phrasePrefix"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|type
operator|=
name|MatchQuery
operator|.
name|Type
operator|.
name|PHRASE_PREFIX
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
literal|"[match] query does not support type "
operator|+
name|tStr
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"analyzer"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|analyzer
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"slop"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"phrase_slop"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"phraseSlop"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|slop
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
name|parseFieldMatcher
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
literal|"prefix_length"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"prefixLength"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"max_expansions"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"maxExpansions"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxExpansion
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
literal|"operator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|operator
operator|=
name|Operator
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"minimum_should_match"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minimumShouldMatch"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minimumShouldMatch
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
literal|"fuzzy_rewrite"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"fuzzyRewrite"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fuzzyRewrite
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
literal|"fuzzy_transpositions"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fuzzyTranspositions
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
literal|"lenient"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lenient
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
literal|"cutoff_frequency"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|cutOffFrequency
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
literal|"zero_terms_query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|String
name|zeroTermsDocs
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"none"
operator|.
name|equalsIgnoreCase
argument_list|(
name|zeroTermsDocs
argument_list|)
condition|)
block|{
name|zeroTermsQuery
operator|=
name|MatchQuery
operator|.
name|ZeroTermsQuery
operator|.
name|NONE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"all"
operator|.
name|equalsIgnoreCase
argument_list|(
name|zeroTermsDocs
argument_list|)
condition|)
block|{
name|zeroTermsQuery
operator|=
name|MatchQuery
operator|.
name|ZeroTermsQuery
operator|.
name|ALL
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
literal|"Unsupported zero_terms_docs value ["
operator|+
name|zeroTermsDocs
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"[match] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|parser
operator|.
name|objectText
argument_list|()
expr_stmt|;
comment|// move to the next token
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
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
literal|"[match] query parsed in simplified form, with direct field name, but included more options than just the field name, possibly use its 'options' form, with 'query' element?"
argument_list|)
throw|;
block|}
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
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"No text specified for text query"
argument_list|)
throw|;
block|}
name|MatchQueryBuilder
name|matchQuery
init|=
operator|new
name|MatchQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|matchQuery
operator|.
name|operator
argument_list|(
name|operator
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|slop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|minimumShouldMatch
argument_list|(
name|minimumShouldMatch
argument_list|)
expr_stmt|;
if|if
condition|(
name|fuzziness
operator|!=
literal|null
condition|)
block|{
name|matchQuery
operator|.
name|fuzziness
argument_list|(
name|fuzziness
argument_list|)
expr_stmt|;
block|}
name|matchQuery
operator|.
name|fuzzyRewrite
argument_list|(
name|fuzzyRewrite
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|prefixLength
argument_list|(
name|prefixLength
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|fuzzyTranspositions
argument_list|(
name|fuzzyTranspositions
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|maxExpansions
argument_list|(
name|maxExpansion
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|lenient
argument_list|(
name|lenient
argument_list|)
expr_stmt|;
if|if
condition|(
name|cutOffFrequency
operator|!=
literal|null
condition|)
block|{
name|matchQuery
operator|.
name|cutoffFrequency
argument_list|(
name|cutOffFrequency
argument_list|)
expr_stmt|;
block|}
name|matchQuery
operator|.
name|zeroTermsQuery
argument_list|(
name|zeroTermsQuery
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|matchQuery
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|MatchQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|MatchQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

