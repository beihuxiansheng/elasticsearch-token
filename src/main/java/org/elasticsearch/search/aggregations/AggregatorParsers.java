begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|MapBuilder
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchParseException
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
name|internal
operator|.
name|SearchContext
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
name|Set
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
DECL|field|parsers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Aggregator
operator|.
name|Parser
argument_list|>
name|parsers
decl_stmt|;
comment|/**      * Constructs the AggregatorParsers out of all the given parsers      *      * @param parsers The available aggregator parsers (dynamically injected by the {@link org.elasticsearch.search.aggregations.AggregationModule}).      */
annotation|@
name|Inject
DECL|method|AggregatorParsers
specifier|public
name|AggregatorParsers
parameter_list|(
name|Set
argument_list|<
name|Aggregator
operator|.
name|Parser
argument_list|>
name|parsers
parameter_list|)
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Aggregator
operator|.
name|Parser
argument_list|>
name|builder
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Aggregator
operator|.
name|Parser
name|parser
range|:
name|parsers
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|parser
operator|.
name|type
argument_list|()
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|parsers
operator|=
name|builder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the parser that is registered under the given aggregation type.      *      * @param type  The aggregation type      * @return      The parser associated with the given aggregation type.      */
DECL|method|parser
specifier|public
name|Aggregator
operator|.
name|Parser
name|parser
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|parsers
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**      * Parses the aggregation request recursively generating aggregator factories in turn.      *      * @param parser    The input xcontent that will be parsed.      * @param context   The search context.      *      * @return          The parsed aggregator factories.      *      * @throws IOException When parsing fails for unknown reasons.      */
DECL|method|parseAggregators
specifier|public
name|AggregatorFactories
name|parseAggregators
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parseAggregators
argument_list|(
name|parser
argument_list|,
name|context
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|parseAggregators
specifier|private
name|AggregatorFactories
name|parseAggregators
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
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
name|String
name|aggregationName
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
name|aggregationName
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
name|String
name|aggregatorType
init|=
literal|null
decl_stmt|;
name|AggregatorFactory
name|factory
init|=
literal|null
decl_stmt|;
name|AggregatorFactories
name|subFactories
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
literal|"aggregations"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"aggs"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|subFactories
operator|=
name|parseAggregators
argument_list|(
name|parser
argument_list|,
name|context
argument_list|,
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|aggregatorType
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Found two aggregation type definitions in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|aggregatorType
operator|+
literal|"] and ["
operator|+
name|currentFieldName
operator|+
literal|"]. Only one type is allowed."
argument_list|)
throw|;
block|}
else|else
block|{
name|aggregatorType
operator|=
name|currentFieldName
expr_stmt|;
name|Aggregator
operator|.
name|Parser
name|aggregatorParser
init|=
name|parser
argument_list|(
name|aggregatorType
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregatorParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Could not find aggregator type ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|factory
operator|=
name|aggregatorParser
operator|.
name|parse
argument_list|(
name|aggregationName
argument_list|,
name|parser
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
comment|// skipping the aggregation
continue|continue;
block|}
if|if
condition|(
name|subFactories
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|subFactories
argument_list|(
name|subFactories
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|level
operator|==
literal|0
condition|)
block|{
name|factory
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
name|factories
operator|.
name|add
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|factories
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

