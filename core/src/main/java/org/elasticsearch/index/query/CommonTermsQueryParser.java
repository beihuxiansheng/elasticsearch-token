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
comment|/**  * Parser for common terms query  */
end_comment

begin_class
DECL|class|CommonTermsQueryParser
specifier|public
class|class
name|CommonTermsQueryParser
extends|extends
name|BaseQueryParser
argument_list|<
name|CommonTermsQueryBuilder
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|CommonTermsQueryParser
specifier|public
name|CommonTermsQueryParser
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
name|CommonTermsQueryBuilder
operator|.
name|NAME
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|CommonTermsQueryBuilder
name|fromXContent
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
argument_list|,
literal|"[common] query malformed, no field"
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
name|text
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
name|analyzer
init|=
literal|null
decl_stmt|;
name|String
name|lowFreqMinimumShouldMatch
init|=
literal|null
decl_stmt|;
name|String
name|highFreqMinimumShouldMatch
init|=
literal|null
decl_stmt|;
name|boolean
name|disableCoord
init|=
name|CommonTermsQueryBuilder
operator|.
name|DEFAULT_DISABLE_COORD
decl_stmt|;
name|Operator
name|highFreqOperator
init|=
name|CommonTermsQueryBuilder
operator|.
name|DEFAULT_HIGH_FREQ_OCCUR
decl_stmt|;
name|Operator
name|lowFreqOperator
init|=
name|CommonTermsQueryBuilder
operator|.
name|DEFAULT_LOW_FREQ_OCCUR
decl_stmt|;
name|float
name|cutoffFrequency
init|=
name|CommonTermsQueryBuilder
operator|.
name|DEFAULT_CUTOFF_FREQ
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
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
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
name|String
name|innerFieldName
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
name|innerFieldName
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
literal|"low_freq"
operator|.
name|equals
argument_list|(
name|innerFieldName
argument_list|)
operator|||
literal|"lowFreq"
operator|.
name|equals
argument_list|(
name|innerFieldName
argument_list|)
condition|)
block|{
name|lowFreqMinimumShouldMatch
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
literal|"high_freq"
operator|.
name|equals
argument_list|(
name|innerFieldName
argument_list|)
operator|||
literal|"highFreq"
operator|.
name|equals
argument_list|(
name|innerFieldName
argument_list|)
condition|)
block|{
name|highFreqMinimumShouldMatch
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
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[common] query does not support ["
operator|+
name|innerFieldName
operator|+
literal|"] for ["
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
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[common] query does not support ["
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
name|objectText
argument_list|()
expr_stmt|;
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
literal|"disable_coord"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"disableCoord"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|disableCoord
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
literal|"high_freq_operator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"highFreqOperator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|highFreqOperator
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
literal|"low_freq_operator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"lowFreqOperator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lowFreqOperator
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
name|lowFreqMinimumShouldMatch
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
literal|"cutoff_frequency"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|cutoffFrequency
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
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[common] query does not support ["
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
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[common] query parsed in simplified form, with direct field name, but included more options than just the field name, possibly use its 'options' form, with 'query' element?"
argument_list|)
throw|;
block|}
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
argument_list|,
literal|"No text specified for text query"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CommonTermsQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|text
argument_list|)
operator|.
name|lowFreqMinimumShouldMatch
argument_list|(
name|lowFreqMinimumShouldMatch
argument_list|)
operator|.
name|highFreqMinimumShouldMatch
argument_list|(
name|highFreqMinimumShouldMatch
argument_list|)
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
operator|.
name|highFreqOperator
argument_list|(
name|highFreqOperator
argument_list|)
operator|.
name|lowFreqOperator
argument_list|(
name|lowFreqOperator
argument_list|)
operator|.
name|disableCoord
argument_list|(
name|disableCoord
argument_list|)
operator|.
name|cutoffFrequency
argument_list|(
name|cutoffFrequency
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
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|CommonTermsQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|CommonTermsQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit
