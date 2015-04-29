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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
operator|.
name|Builder
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
name|ConstantScoreQuery
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
name|Filter
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
name|FilteredQuery
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|ScoreFunction
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
name|WeightFactorFunction
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
name|QueryParsingException
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
name|factor
operator|.
name|FactorParser
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FunctionScoreQueryParser
specifier|public
class|class
name|FunctionScoreQueryParser
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
literal|"function_score"
decl_stmt|;
DECL|field|functionParserMapper
name|ScoreFunctionParserMapper
name|functionParserMapper
decl_stmt|;
comment|// For better readability of error message
DECL|field|MISPLACED_FUNCTION_MESSAGE_PREFIX
specifier|static
specifier|final
name|String
name|MISPLACED_FUNCTION_MESSAGE_PREFIX
init|=
literal|"You can either define \"functions\":[...] or a single function, not both. "
decl_stmt|;
DECL|field|MISPLACED_BOOST_FUNCTION_MESSAGE_SUFFIX
specifier|static
specifier|final
name|String
name|MISPLACED_BOOST_FUNCTION_MESSAGE_SUFFIX
init|=
literal|" Did you mean \"boost\" instead?"
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
annotation|@
name|Inject
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
name|NAME
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|NAME
argument_list|)
block|}
return|;
block|}
DECL|field|combineFunctionsMap
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|CombineFunction
argument_list|>
name|combineFunctionsMap
decl_stmt|;
static|static
block|{
name|CombineFunction
index|[]
name|values
init|=
name|CombineFunction
operator|.
name|values
argument_list|()
decl_stmt|;
name|Builder
argument_list|<
name|String
argument_list|,
name|CombineFunction
argument_list|>
name|combineFunctionMapBuilder
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|CombineFunction
decl|>
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|CombineFunction
name|combineFunction
range|:
name|values
control|)
block|{
name|combineFunctionMapBuilder
operator|.
name|put
argument_list|(
name|combineFunction
operator|.
name|getName
argument_list|()
argument_list|,
name|combineFunction
argument_list|)
expr_stmt|;
block|}
name|combineFunctionsMap
operator|=
name|combineFunctionMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
name|scoreMode
init|=
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
operator|.
name|Multiply
decl_stmt|;
name|ArrayList
argument_list|<
name|FiltersFunctionScoreQuery
operator|.
name|FilterFunction
argument_list|>
name|filterFunctions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Float
name|maxBoost
init|=
literal|null
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
name|CombineFunction
operator|.
name|MULT
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|query
operator|=
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
expr_stmt|;
block|}
elseif|else
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
name|parseInnerFilter
argument_list|()
expr_stmt|;
block|}
elseif|else
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
name|parseScoreMode
argument_list|(
name|parseContext
argument_list|,
name|parser
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
name|parseBoostMode
argument_list|(
name|parseContext
argument_list|,
name|parser
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
elseif|else
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
literal|"Found \""
operator|+
name|singleFunctionName
operator|+
literal|"\" already, now encountering \"functions\": [...]."
decl_stmt|;
name|handleMisplacedFunctionsDeclaration
argument_list|(
name|errorString
argument_list|,
name|singleFunctionName
argument_list|)
expr_stmt|;
block|}
name|currentFieldName
operator|=
name|parseFiltersAndFunctions
argument_list|(
name|parseContext
argument_list|,
name|parser
argument_list|,
name|filterFunctions
argument_list|,
name|currentFieldName
argument_list|)
expr_stmt|;
name|functionArrayFound
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|ScoreFunction
name|scoreFunction
decl_stmt|;
if|if
condition|(
name|currentFieldName
operator|.
name|equals
argument_list|(
literal|"weight"
argument_list|)
condition|)
block|{
name|scoreFunction
operator|=
operator|new
name|WeightFactorFunction
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we try to parse a score function. If there is no score
comment|// function for the current field name,
comment|// functionParserMapper.get() will throw an Exception.
name|scoreFunction
operator|=
name|functionParserMapper
operator|.
name|get
argument_list|(
name|parseContext
argument_list|,
name|currentFieldName
argument_list|)
operator|.
name|parse
argument_list|(
name|parseContext
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|functionArrayFound
condition|)
block|{
name|String
name|errorString
init|=
literal|"Found \"functions\": [...] already, now encountering \""
operator|+
name|currentFieldName
operator|+
literal|"\"."
decl_stmt|;
name|handleMisplacedFunctionsDeclaration
argument_list|(
name|errorString
argument_list|,
name|currentFieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterFunctions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|errorString
init|=
literal|"Found function "
operator|+
name|singleFunctionName
operator|+
literal|" already, now encountering \""
operator|+
name|currentFieldName
operator|+
literal|"\". Use functions[{...},...] if you want to define several functions."
decl_stmt|;
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
name|errorString
argument_list|)
throw|;
block|}
name|filterFunctions
operator|.
name|add
argument_list|(
operator|new
name|FiltersFunctionScoreQuery
operator|.
name|FilterFunction
argument_list|(
literal|null
argument_list|,
name|scoreFunction
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
block|}
if|if
condition|(
name|query
operator|==
literal|null
operator|&&
name|filter
operator|==
literal|null
condition|)
block|{
name|query
operator|=
name|Queries
operator|.
name|newMatchAllQuery
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|==
literal|null
operator|&&
name|filter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|!=
literal|null
operator|&&
name|filter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
comment|// if all filter elements returned null, just use the query
if|if
condition|(
name|filterFunctions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|combineFunction
operator|==
literal|null
condition|)
block|{
return|return
name|query
return|;
block|}
if|if
condition|(
name|maxBoost
operator|==
literal|null
condition|)
block|{
name|maxBoost
operator|=
name|Float
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
comment|// handle cases where only one score function and no filter was
comment|// provided. In this case we create a FunctionScoreQuery.
if|if
condition|(
name|filterFunctions
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
name|filterFunctions
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|filterFunctions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|filter
operator|==
literal|null
operator|||
name|Queries
operator|.
name|isConstantMatchAllQuery
argument_list|(
name|filterFunctions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|filter
argument_list|)
operator|)
condition|)
block|{
name|ScoreFunction
name|function
init|=
name|filterFunctions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|filterFunctions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|function
decl_stmt|;
name|FunctionScoreQuery
name|theQuery
init|=
operator|new
name|FunctionScoreQuery
argument_list|(
name|query
argument_list|,
name|function
argument_list|,
name|minScore
argument_list|)
decl_stmt|;
if|if
condition|(
name|combineFunction
operator|!=
literal|null
condition|)
block|{
name|theQuery
operator|.
name|setCombineFunction
argument_list|(
name|combineFunction
argument_list|)
expr_stmt|;
block|}
name|theQuery
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|theQuery
operator|.
name|setMaxBoost
argument_list|(
name|maxBoost
argument_list|)
expr_stmt|;
return|return
name|theQuery
return|;
comment|// in all other cases we create a FiltersFunctionScoreQuery.
block|}
else|else
block|{
name|FiltersFunctionScoreQuery
name|functionScoreQuery
init|=
operator|new
name|FiltersFunctionScoreQuery
argument_list|(
name|query
argument_list|,
name|scoreMode
argument_list|,
name|filterFunctions
operator|.
name|toArray
argument_list|(
operator|new
name|FiltersFunctionScoreQuery
operator|.
name|FilterFunction
index|[
name|filterFunctions
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|maxBoost
argument_list|,
name|minScore
argument_list|)
decl_stmt|;
if|if
condition|(
name|combineFunction
operator|!=
literal|null
condition|)
block|{
name|functionScoreQuery
operator|.
name|setCombineFunction
argument_list|(
name|combineFunction
argument_list|)
expr_stmt|;
block|}
name|functionScoreQuery
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|functionScoreQuery
return|;
block|}
block|}
DECL|method|handleMisplacedFunctionsDeclaration
specifier|private
name|void
name|handleMisplacedFunctionsDeclaration
parameter_list|(
name|String
name|errorString
parameter_list|,
name|String
name|functionName
parameter_list|)
block|{
name|errorString
operator|=
name|MISPLACED_FUNCTION_MESSAGE_PREFIX
operator|+
name|errorString
expr_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|asList
argument_list|(
name|FactorParser
operator|.
name|NAMES
argument_list|)
operator|.
name|contains
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|errorString
operator|=
name|errorString
operator|+
name|MISPLACED_BOOST_FUNCTION_MESSAGE_SUFFIX
expr_stmt|;
block|}
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
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
name|ArrayList
argument_list|<
name|FiltersFunctionScoreQuery
operator|.
name|FilterFunction
argument_list|>
name|filterFunctions
parameter_list|,
name|String
name|currentFieldName
parameter_list|)
throws|throws
name|IOException
block|{
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
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
name|ScoreFunction
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
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
name|NAME
operator|+
literal|": malformed query, expected a "
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|+
literal|" while parsing functions but got a "
operator|+
name|token
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
name|WEIGHT_FIELD
operator|.
name|match
argument_list|(
name|currentFieldName
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
name|parseInnerFilter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// do not need to check null here,
comment|// functionParserMapper throws exception if parser
comment|// non-existent
name|ScoreFunctionParser
name|functionParser
init|=
name|functionParserMapper
operator|.
name|get
argument_list|(
name|parseContext
argument_list|,
name|currentFieldName
argument_list|)
decl_stmt|;
name|scoreFunction
operator|=
name|functionParser
operator|.
name|parse
argument_list|(
name|parseContext
argument_list|,
name|parser
argument_list|)
expr_stmt|;
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
name|scoreFunction
operator|=
operator|new
name|WeightFactorFunction
argument_list|(
name|functionWeight
argument_list|,
name|scoreFunction
argument_list|)
expr_stmt|;
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
name|Queries
operator|.
name|newMatchAllFilter
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
name|ElasticsearchParseException
argument_list|(
literal|"function_score: One entry in functions list is missing a function."
argument_list|)
throw|;
block|}
name|filterFunctions
operator|.
name|add
argument_list|(
operator|new
name|FiltersFunctionScoreQuery
operator|.
name|FilterFunction
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
DECL|method|parseScoreMode
specifier|private
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
name|parseScoreMode
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|scoreMode
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"avg"
operator|.
name|equals
argument_list|(
name|scoreMode
argument_list|)
condition|)
block|{
return|return
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
operator|.
name|Avg
return|;
block|}
elseif|else
if|if
condition|(
literal|"max"
operator|.
name|equals
argument_list|(
name|scoreMode
argument_list|)
condition|)
block|{
return|return
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
operator|.
name|Max
return|;
block|}
elseif|else
if|if
condition|(
literal|"min"
operator|.
name|equals
argument_list|(
name|scoreMode
argument_list|)
condition|)
block|{
return|return
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
operator|.
name|Min
return|;
block|}
elseif|else
if|if
condition|(
literal|"sum"
operator|.
name|equals
argument_list|(
name|scoreMode
argument_list|)
condition|)
block|{
return|return
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
operator|.
name|Sum
return|;
block|}
elseif|else
if|if
condition|(
literal|"multiply"
operator|.
name|equals
argument_list|(
name|scoreMode
argument_list|)
condition|)
block|{
return|return
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
operator|.
name|Multiply
return|;
block|}
elseif|else
if|if
condition|(
literal|"first"
operator|.
name|equals
argument_list|(
name|scoreMode
argument_list|)
condition|)
block|{
return|return
name|FiltersFunctionScoreQuery
operator|.
name|ScoreMode
operator|.
name|First
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
name|NAME
operator|+
literal|" illegal score_mode ["
operator|+
name|scoreMode
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseBoostMode
specifier|private
name|CombineFunction
name|parseBoostMode
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|boostMode
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|CombineFunction
name|cf
init|=
name|combineFunctionsMap
operator|.
name|get
argument_list|(
name|boostMode
argument_list|)
decl_stmt|;
if|if
condition|(
name|cf
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
name|NAME
operator|+
literal|" illegal boost_mode ["
operator|+
name|boostMode
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|cf
return|;
block|}
block|}
end_class

end_unit

