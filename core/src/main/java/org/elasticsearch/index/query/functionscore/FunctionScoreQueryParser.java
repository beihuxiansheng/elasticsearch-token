begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|CombineFunction
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
name|search
operator|.
name|function
operator|.
name|FiltersFunctionScoreQuery
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
name|search
operator|.
name|function
operator|.
name|FunctionScoreQuery
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
name|XContentLocation
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
name|query
operator|.
name|AbstractQueryBuilder
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
name|EmptyQueryBuilder
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
name|MatchAllQueryBuilder
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
name|QueryParser
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
name|functionscore
operator|.
name|weight
operator|.
name|WeightBuilder
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

begin_comment
comment|/**  * Parser for function_score query  */
end_comment

begin_class
DECL|class|FunctionScoreQueryParser
specifier|public
class|class
name|FunctionScoreQueryParser
implements|implements
name|QueryParser
argument_list|<
name|FunctionScoreQueryBuilder
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|private
specifier|static
specifier|final
name|FunctionScoreQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|EmptyQueryBuilder
operator|.
name|PROTOTYPE
argument_list|,
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// For better readability of error message
DECL|field|MISPLACED_FUNCTION_MESSAGE_PREFIX
specifier|static
specifier|final
name|String
name|MISPLACED_FUNCTION_MESSAGE_PREFIX
init|=
literal|"you can either define [functions] array or a single function, not both. "
decl_stmt|;
DECL|field|WEIGHT_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|WEIGHT_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"weight"
argument_list|)
decl_stmt|;
DECL|field|functionParserMapper
specifier|private
specifier|final
name|ScoreFunctionParserMapper
name|functionParserMapper
decl_stmt|;
DECL|method|FunctionScoreQueryParser
specifier|public
name|FunctionScoreQueryParser
parameter_list|(
name|ScoreFunctionParserMapper
name|functionParserMapper
parameter_list|)
block|{
name|this
operator|.
name|functionParserMapper
operator|=
name|functionParserMapper
expr_stmt|;
block|}
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
name|FunctionScoreQueryBuilder
operator|.
name|NAME
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|FunctionScoreQueryBuilder
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
name|QueryBuilder
name|query
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
name|queryName
init|=
literal|null
decl_stmt|;
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
name|scoreMode
init|=
name|FunctionScoreQueryBuilder
operator|.
name|DEFAULT_SCORE_MODE
decl_stmt|;
name|float
name|maxBoost
init|=
name|FunctionScoreQuery
operator|.
name|DEFAULT_MAX_BOOST
decl_stmt|;
name|Float
name|minScore
init|=
literal|null
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
name|CombineFunction
name|combineFunction
init|=
literal|null
decl_stmt|;
comment|// Either define array of functions and filters or only one function
name|boolean
name|functionArrayFound
init|=
literal|false
decl_stmt|;
name|boolean
name|singleFunctionFound
init|=
literal|false
decl_stmt|;
name|String
name|singleFunctionName
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|>
name|filterFunctionBuilders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|query
operator|!=
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
literal|"failed to parse [{}] query. [query] is already defined."
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|)
throw|;
block|}
name|query
operator|=
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|singleFunctionFound
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
literal|"failed to parse [{}] query. already found function [{}], now encountering [{}]. use [functions] array if you want to define several functions."
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|,
name|singleFunctionName
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
if|if
condition|(
name|functionArrayFound
condition|)
block|{
name|String
name|errorString
init|=
literal|"already found [functions] array, now encountering ["
operator|+
name|currentFieldName
operator|+
literal|"]."
decl_stmt|;
name|handleMisplacedFunctionsDeclaration
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|errorString
argument_list|)
expr_stmt|;
block|}
name|singleFunctionFound
operator|=
literal|true
expr_stmt|;
name|singleFunctionName
operator|=
name|currentFieldName
expr_stmt|;
comment|// we try to parse a score function. If there is no score function for the current field name,
comment|// functionParserMapper.get() may throw an Exception.
name|ScoreFunctionBuilder
argument_list|<
name|?
argument_list|>
name|scoreFunction
init|=
name|functionParserMapper
operator|.
name|get
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|currentFieldName
argument_list|)
operator|.
name|fromXContent
argument_list|(
name|parseContext
argument_list|,
name|parser
argument_list|)
decl_stmt|;
name|filterFunctionBuilders
operator|.
name|add
argument_list|(
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|(
name|scoreFunction
argument_list|)
argument_list|)
expr_stmt|;
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
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"functions"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|singleFunctionFound
condition|)
block|{
name|String
name|errorString
init|=
literal|"already found ["
operator|+
name|singleFunctionName
operator|+
literal|"], now encountering [functions]."
decl_stmt|;
name|handleMisplacedFunctionsDeclaration
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|errorString
argument_list|)
expr_stmt|;
block|}
name|functionArrayFound
operator|=
literal|true
expr_stmt|;
name|currentFieldName
operator|=
name|parseFiltersAndFunctions
argument_list|(
name|parseContext
argument_list|,
name|parser
argument_list|,
name|filterFunctionBuilders
argument_list|)
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
literal|"failed to parse [{}] query. array [{}] is not supported"
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|,
name|currentFieldName
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
literal|"score_mode"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"scoreMode"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scoreMode
operator|=
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
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
literal|"boost_mode"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"boostMode"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|combineFunction
operator|=
name|CombineFunction
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
literal|"max_boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"maxBoost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxBoost
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
elseif|else
if|if
condition|(
literal|"min_score"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minScore"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minScore
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|singleFunctionFound
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
literal|"failed to parse [{}] query. already found function [{}], now encountering [{}]. use [functions] array if you want to define several functions."
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|,
name|singleFunctionName
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
if|if
condition|(
name|functionArrayFound
condition|)
block|{
name|String
name|errorString
init|=
literal|"already found [functions] array, now encountering ["
operator|+
name|currentFieldName
operator|+
literal|"]."
decl_stmt|;
name|handleMisplacedFunctionsDeclaration
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|errorString
argument_list|)
expr_stmt|;
block|}
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
name|WEIGHT_FIELD
argument_list|)
condition|)
block|{
name|filterFunctionBuilders
operator|.
name|add
argument_list|(
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|(
operator|new
name|WeightBuilder
argument_list|()
operator|.
name|setWeight
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|singleFunctionFound
operator|=
literal|true
expr_stmt|;
name|singleFunctionName
operator|=
name|currentFieldName
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
literal|"failed to parse [{}] query. field [{}] is not supported"
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|MatchAllQueryBuilder
argument_list|()
expr_stmt|;
block|}
name|FunctionScoreQueryBuilder
name|functionScoreQueryBuilder
init|=
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|query
argument_list|,
name|filterFunctionBuilders
operator|.
name|toArray
argument_list|(
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
index|[
name|filterFunctionBuilders
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|combineFunction
operator|!=
literal|null
condition|)
block|{
name|functionScoreQueryBuilder
operator|.
name|boostMode
argument_list|(
name|combineFunction
argument_list|)
expr_stmt|;
block|}
name|functionScoreQueryBuilder
operator|.
name|scoreMode
argument_list|(
name|scoreMode
argument_list|)
expr_stmt|;
name|functionScoreQueryBuilder
operator|.
name|maxBoost
argument_list|(
name|maxBoost
argument_list|)
expr_stmt|;
if|if
condition|(
name|minScore
operator|!=
literal|null
condition|)
block|{
name|functionScoreQueryBuilder
operator|.
name|setMinScore
argument_list|(
name|minScore
argument_list|)
expr_stmt|;
block|}
name|functionScoreQueryBuilder
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|functionScoreQueryBuilder
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
return|return
name|functionScoreQueryBuilder
return|;
block|}
DECL|method|handleMisplacedFunctionsDeclaration
specifier|private
specifier|static
name|void
name|handleMisplacedFunctionsDeclaration
parameter_list|(
name|XContentLocation
name|contentLocation
parameter_list|,
name|String
name|errorString
parameter_list|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|contentLocation
argument_list|,
literal|"failed to parse [{}] query. [{}]"
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|,
name|MISPLACED_FUNCTION_MESSAGE_PREFIX
operator|+
name|errorString
argument_list|)
throw|;
block|}
DECL|method|parseFiltersAndFunctions
specifier|private
name|String
name|parseFiltersAndFunctions
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|List
argument_list|<
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|>
name|filterFunctionBuilders
parameter_list|)
throws|throws
name|IOException
block|{
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
name|END_ARRAY
condition|)
block|{
name|QueryBuilder
name|filter
init|=
literal|null
decl_stmt|;
name|ScoreFunctionBuilder
argument_list|<
name|?
argument_list|>
name|scoreFunction
init|=
literal|null
decl_stmt|;
name|Float
name|functionWeight
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
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
literal|"failed to parse [{}]. malformed query, expected a [{}] while parsing functions but got a [{}] instead"
argument_list|,
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|token
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|)
throw|;
block|}
else|else
block|{
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
literal|"filter"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|scoreFunction
operator|!=
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
literal|"failed to parse function_score functions. already found [{}], now encountering [{}]."
argument_list|,
name|scoreFunction
operator|.
name|getName
argument_list|()
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
comment|// do not need to check null here, functionParserMapper does it already
name|ScoreFunctionParser
name|functionParser
init|=
name|functionParserMapper
operator|.
name|get
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|currentFieldName
argument_list|)
decl_stmt|;
name|scoreFunction
operator|=
name|functionParser
operator|.
name|fromXContent
argument_list|(
name|parseContext
argument_list|,
name|parser
argument_list|)
expr_stmt|;
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|WEIGHT_FIELD
argument_list|)
condition|)
block|{
name|functionWeight
operator|=
name|parser
operator|.
name|floatValue
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
literal|"failed to parse [{}] query. field [{}] is not supported"
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|functionWeight
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|scoreFunction
operator|==
literal|null
condition|)
block|{
name|scoreFunction
operator|=
operator|new
name|WeightBuilder
argument_list|()
operator|.
name|setWeight
argument_list|(
name|functionWeight
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scoreFunction
operator|.
name|setWeight
argument_list|(
name|functionWeight
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
name|filter
operator|=
operator|new
name|MatchAllQueryBuilder
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|scoreFunction
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
literal|"failed to parse [{}] query. an entry in functions list is missing a function."
argument_list|,
name|FunctionScoreQueryBuilder
operator|.
name|NAME
argument_list|)
throw|;
block|}
name|filterFunctionBuilders
operator|.
name|add
argument_list|(
operator|new
name|FunctionScoreQueryBuilder
operator|.
name|FilterFunctionBuilder
argument_list|(
name|filter
argument_list|,
name|scoreFunction
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|currentFieldName
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|FunctionScoreQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

