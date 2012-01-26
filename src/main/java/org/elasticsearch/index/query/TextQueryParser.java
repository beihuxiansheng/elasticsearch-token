begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|BooleanClause
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
name|inject
operator|.
name|Inject
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TextQueryParser
specifier|public
class|class
name|TextQueryParser
implements|implements
name|QueryParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"text"
decl_stmt|;
annotation|@
name|Inject
DECL|method|TextQueryParser
specifier|public
name|TextQueryParser
parameter_list|()
block|{     }
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
name|NAME
block|,
literal|"text_phrase"
block|,
literal|"textPhrase"
block|,
literal|"text_phrase_prefix"
block|,
literal|"textPhrasePrefix"
block|,
literal|"fuzzyText"
block|,
literal|"fuzzy_text"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
operator|.
name|Type
name|type
init|=
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
operator|.
name|Type
operator|.
name|BOOLEAN
decl_stmt|;
if|if
condition|(
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
operator|.
name|Type
operator|.
name|PHRASE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
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
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[text] query malformed, no field"
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
name|String
name|text
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|int
name|phraseSlop
init|=
literal|0
decl_stmt|;
name|String
name|analyzer
init|=
literal|null
decl_stmt|;
name|String
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
name|maxExpansions
init|=
name|FuzzyQuery
operator|.
name|defaultMaxExpansions
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
name|occur
init|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
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
name|text
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
operator|.
name|Type
operator|.
name|PHRASE_PREFIX
expr_stmt|;
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
name|textOrNull
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
name|phraseSlop
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
literal|"fuzziness"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fuzziness
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
literal|"operator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|String
name|op
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"or"
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"and"
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"text query requires operator to be either 'and' or 'or', not ["
operator|+
name|op
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[text] query does not support ["
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
name|text
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
comment|// move to the next token
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|text
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"No text specified for text query"
argument_list|)
throw|;
block|}
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
name|tQP
init|=
operator|new
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|TextQueryParser
argument_list|(
name|parseContext
argument_list|,
name|fieldName
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|tQP
operator|.
name|setPhraseSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
name|tQP
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|tQP
operator|.
name|setFuzziness
argument_list|(
name|fuzziness
argument_list|)
expr_stmt|;
name|tQP
operator|.
name|setFuzzyPrefixLength
argument_list|(
name|prefixLength
argument_list|)
expr_stmt|;
name|tQP
operator|.
name|setMaxExpansions
argument_list|(
name|maxExpansions
argument_list|)
expr_stmt|;
name|tQP
operator|.
name|setOccur
argument_list|(
name|occur
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|tQP
operator|.
name|parse
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

