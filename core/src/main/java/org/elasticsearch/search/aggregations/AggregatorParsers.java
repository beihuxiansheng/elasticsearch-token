begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|xcontent
operator|.
name|ParseFieldRegistry
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
name|search
operator|.
name|aggregations
operator|.
name|pipeline
operator|.
name|PipelineAggregator
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
name|aggregations
operator|.
name|pipeline
operator|.
name|PipelineAggregatorBuilder
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * A registry for all the aggregator parser, also servers as the main parser for the aggregations module  */
end_comment

begin_class
DECL|class|AggregatorParsers
specifier|public
class|class
name|AggregatorParsers
block|{
DECL|field|VALID_AGG_NAME
specifier|public
specifier|static
specifier|final
name|Pattern
name|VALID_AGG_NAME
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^\\[\\]>]+"
argument_list|)
decl_stmt|;
DECL|field|aggregationParserRegistry
specifier|private
specifier|final
name|ParseFieldRegistry
argument_list|<
name|Aggregator
operator|.
name|Parser
argument_list|>
name|aggregationParserRegistry
decl_stmt|;
DECL|field|pipelineAggregationParserRegistry
specifier|private
specifier|final
name|ParseFieldRegistry
argument_list|<
name|PipelineAggregator
operator|.
name|Parser
argument_list|>
name|pipelineAggregationParserRegistry
decl_stmt|;
DECL|method|AggregatorParsers
specifier|public
name|AggregatorParsers
parameter_list|(
name|ParseFieldRegistry
argument_list|<
name|Aggregator
operator|.
name|Parser
argument_list|>
name|aggregationParserRegistry
parameter_list|,
name|ParseFieldRegistry
argument_list|<
name|PipelineAggregator
operator|.
name|Parser
argument_list|>
name|pipelineAggregationParserRegistry
parameter_list|)
block|{
name|this
operator|.
name|aggregationParserRegistry
operator|=
name|aggregationParserRegistry
expr_stmt|;
name|this
operator|.
name|pipelineAggregationParserRegistry
operator|=
name|pipelineAggregationParserRegistry
expr_stmt|;
block|}
comment|/**      * Returns the parser that is registered under the given aggregation type.      *      * @param type The aggregation type      * @param parseFieldMatcher used for making error messages.      * @return The parser associated with the given aggregation type or null if it wasn't found.      */
DECL|method|parser
specifier|public
name|Aggregator
operator|.
name|Parser
name|parser
parameter_list|(
name|String
name|type
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
block|{
return|return
name|aggregationParserRegistry
operator|.
name|lookupReturningNullIfNotFound
argument_list|(
name|type
argument_list|,
name|parseFieldMatcher
argument_list|)
return|;
block|}
comment|/**      * Returns the parser that is registered under the given pipeline aggregator type.      *      * @param type The pipeline aggregator type      * @param parseFieldMatcher used for making error messages.      * @return The parser associated with the given pipeline aggregator type or null if it wasn't found.      */
DECL|method|pipelineParser
specifier|public
name|PipelineAggregator
operator|.
name|Parser
name|pipelineParser
parameter_list|(
name|String
name|type
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
block|{
return|return
name|pipelineAggregationParserRegistry
operator|.
name|lookupReturningNullIfNotFound
argument_list|(
name|type
argument_list|,
name|parseFieldMatcher
argument_list|)
return|;
block|}
comment|/**      * Parses the aggregation request recursively generating aggregator factories in turn.      *      * @param parseContext   The parse context.      *      * @return          The parsed aggregator factories.      *      * @throws IOException When parsing fails for unknown reasons.      */
DECL|method|parseAggregators
specifier|public
name|AggregatorFactories
operator|.
name|Builder
name|parseAggregators
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parseAggregators
argument_list|(
name|parseContext
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|parseAggregators
specifier|private
name|AggregatorFactories
operator|.
name|Builder
name|parseAggregators
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
name|Matcher
name|validAggMatcher
init|=
name|VALID_AGG_NAME
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|AggregatorFactories
operator|.
name|Builder
name|factories
init|=
operator|new
name|AggregatorFactories
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
literal|null
decl_stmt|;
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
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
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" in [aggs]: aggregations definitions must start with the name of the aggregation."
argument_list|)
throw|;
block|}
specifier|final
name|String
name|aggregationName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|validAggMatcher
operator|.
name|reset
argument_list|(
name|aggregationName
argument_list|)
operator|.
name|matches
argument_list|()
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
literal|"Invalid aggregation name ["
operator|+
name|aggregationName
operator|+
literal|"]. Aggregation names must be alpha-numeric and can only contain '_' and '-'"
argument_list|)
throw|;
block|}
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
literal|"Aggregation definition for ["
operator|+
name|aggregationName
operator|+
literal|" starts with a ["
operator|+
name|token
operator|+
literal|"], expected a ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|+
literal|"]."
argument_list|)
throw|;
block|}
name|AggregatorBuilder
argument_list|<
name|?
argument_list|>
name|aggFactory
init|=
literal|null
decl_stmt|;
name|PipelineAggregatorBuilder
argument_list|<
name|?
argument_list|>
name|pipelineAggregatorFactory
init|=
literal|null
decl_stmt|;
name|AggregatorFactories
operator|.
name|Builder
name|subFactories
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
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
literal|"Expected ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
operator|+
literal|"] under a ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|+
literal|"], but got a ["
operator|+
name|token
operator|+
literal|"] in ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|,
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
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
switch|switch
condition|(
name|fieldName
condition|)
block|{
case|case
literal|"meta"
case|:
name|metaData
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"aggregations"
case|:
case|case
literal|"aggs"
case|:
if|if
condition|(
name|subFactories
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
literal|"Found two sub aggregation definitions under ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|subFactories
operator|=
name|parseAggregators
argument_list|(
name|parseContext
argument_list|,
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|aggFactory
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
literal|"Found two aggregation type definitions in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|aggFactory
operator|.
name|type
operator|+
literal|"] and ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|pipelineAggregatorFactory
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
literal|"Found two aggregation type definitions in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|pipelineAggregatorFactory
operator|+
literal|"] and ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Aggregator
operator|.
name|Parser
name|aggregatorParser
init|=
name|parser
argument_list|(
name|fieldName
argument_list|,
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregatorParser
operator|==
literal|null
condition|)
block|{
name|PipelineAggregator
operator|.
name|Parser
name|pipelineAggregatorParser
init|=
name|pipelineParser
argument_list|(
name|fieldName
argument_list|,
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipelineAggregatorParser
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
literal|"Could not find aggregator type ["
operator|+
name|fieldName
operator|+
literal|"] in ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
name|pipelineAggregatorFactory
operator|=
name|pipelineAggregatorParser
operator|.
name|parse
argument_list|(
name|aggregationName
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|aggFactory
operator|=
name|aggregatorParser
operator|.
name|parse
argument_list|(
name|aggregationName
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Expected ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|+
literal|"] under ["
operator|+
name|fieldName
operator|+
literal|"], but got a ["
operator|+
name|token
operator|+
literal|"] in ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|aggFactory
operator|==
literal|null
operator|&&
name|pipelineAggregatorFactory
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
literal|"Missing definition for aggregation ["
operator|+
name|aggregationName
operator|+
literal|"]"
argument_list|,
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|aggFactory
operator|!=
literal|null
condition|)
block|{
assert|assert
name|pipelineAggregatorFactory
operator|==
literal|null
assert|;
if|if
condition|(
name|metaData
operator|!=
literal|null
condition|)
block|{
name|aggFactory
operator|.
name|setMetaData
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subFactories
operator|!=
literal|null
condition|)
block|{
name|aggFactory
operator|.
name|subAggregations
argument_list|(
name|subFactories
argument_list|)
expr_stmt|;
block|}
name|factories
operator|.
name|addAggregator
argument_list|(
name|aggFactory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|pipelineAggregatorFactory
operator|!=
literal|null
assert|;
if|if
condition|(
name|subFactories
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
literal|"Aggregation ["
operator|+
name|aggregationName
operator|+
literal|"] cannot define sub-aggregations"
argument_list|,
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|metaData
operator|!=
literal|null
condition|)
block|{
name|pipelineAggregatorFactory
operator|.
name|setMetaData
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
block|}
name|factories
operator|.
name|addPipelineAggregator
argument_list|(
name|pipelineAggregatorFactory
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|factories
return|;
block|}
block|}
end_class

end_unit

