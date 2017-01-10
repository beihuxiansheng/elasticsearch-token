begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.date
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|range
operator|.
name|date
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
name|AggregationBuilder
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
name|AggregatorFactories
operator|.
name|Builder
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
name|AggregatorFactory
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
name|bucket
operator|.
name|range
operator|.
name|AbstractRangeBuilder
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
name|bucket
operator|.
name|range
operator|.
name|RangeAggregator
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
name|bucket
operator|.
name|range
operator|.
name|RangeAggregator
operator|.
name|Range
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
name|support
operator|.
name|ValuesSource
operator|.
name|Numeric
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
name|support
operator|.
name|ValuesSourceConfig
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
name|support
operator|.
name|ValuesSourceParserHelper
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
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
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

begin_class
DECL|class|DateRangeAggregationBuilder
specifier|public
class|class
name|DateRangeAggregationBuilder
extends|extends
name|AbstractRangeBuilder
argument_list|<
name|DateRangeAggregationBuilder
argument_list|,
name|RangeAggregator
operator|.
name|Range
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"date_range"
decl_stmt|;
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|DateRangeAggregationBuilder
argument_list|,
name|QueryParseContext
argument_list|>
name|PARSER
decl_stmt|;
static|static
block|{
name|PARSER
operator|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|DateRangeAggregationBuilder
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ValuesSourceParserHelper
operator|.
name|declareNumericFields
argument_list|(
name|PARSER
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareBoolean
argument_list|(
name|DateRangeAggregationBuilder
operator|::
name|keyed
argument_list|,
name|RangeAggregator
operator|.
name|KEYED_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareObjectArray
argument_list|(
parameter_list|(
name|agg
parameter_list|,
name|ranges
parameter_list|)
lambda|->
block|{
for|for
control|(
name|Range
name|range
range|:
name|ranges
control|)
block|{
name|agg
operator|.
name|addRange
argument_list|(
name|range
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|DateRangeAggregationBuilder
operator|::
name|parseRange
argument_list|,
name|RangeAggregator
operator|.
name|RANGES_FIELD
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|AggregationBuilder
name|parse
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PARSER
operator|.
name|parse
argument_list|(
name|context
operator|.
name|parser
argument_list|()
argument_list|,
operator|new
name|DateRangeAggregationBuilder
argument_list|(
name|aggregationName
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|parseRange
specifier|private
specifier|static
name|Range
name|parseRange
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Range
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
return|;
block|}
DECL|method|DateRangeAggregationBuilder
specifier|public
name|DateRangeAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalDateRange
operator|.
name|FACTORY
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|DateRangeAggregationBuilder
specifier|public
name|DateRangeAggregationBuilder
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
argument_list|,
name|InternalDateRange
operator|.
name|FACTORY
argument_list|,
name|Range
operator|::
operator|new
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
comment|/**      * Add a new range to this aggregation.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the dates, inclusive      * @param to      *            the upper bound on the dates, exclusive      */
DECL|method|addRange
specifier|public
name|DateRangeAggregationBuilder
name|addRange
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addRange(String, String, String)} but the key will be      * automatically generated based on<code>from</code> and<code>to</code>.      */
DECL|method|addRange
specifier|public
name|DateRangeAggregationBuilder
name|addRange
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
return|return
name|addRange
argument_list|(
literal|null
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no lower bound.      *      * @param key      *            the key to use for this range in the response      * @param to      *            the upper bound on the dates, exclusive      */
DECL|method|addUnboundedTo
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedTo
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedTo(String, String)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedTo
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedTo
parameter_list|(
name|String
name|to
parameter_list|)
block|{
return|return
name|addUnboundedTo
argument_list|(
literal|null
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no upper bound.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the distances, inclusive      */
DECL|method|addUnboundedFrom
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedFrom
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|from
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedFrom(String, String)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedFrom
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedFrom
parameter_list|(
name|String
name|from
parameter_list|)
block|{
return|return
name|addUnboundedFrom
argument_list|(
literal|null
argument_list|,
name|from
argument_list|)
return|;
block|}
comment|/**      * Add a new range to this aggregation.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the dates, inclusive      * @param to      *            the upper bound on the dates, exclusive      */
DECL|method|addRange
specifier|public
name|DateRangeAggregationBuilder
name|addRange
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addRange(String, double, double)} but the key will be      * automatically generated based on<code>from</code> and<code>to</code>.      */
DECL|method|addRange
specifier|public
name|DateRangeAggregationBuilder
name|addRange
parameter_list|(
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|)
block|{
return|return
name|addRange
argument_list|(
literal|null
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no lower bound.      *      * @param key      *            the key to use for this range in the response      * @param to      *            the upper bound on the dates, exclusive      */
DECL|method|addUnboundedTo
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedTo
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedTo(String, double)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedTo
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedTo
parameter_list|(
name|double
name|to
parameter_list|)
block|{
return|return
name|addUnboundedTo
argument_list|(
literal|null
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no upper bound.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the distances, inclusive      */
DECL|method|addUnboundedFrom
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedFrom
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedFrom(String, double)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedFrom
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedFrom
parameter_list|(
name|double
name|from
parameter_list|)
block|{
return|return
name|addUnboundedFrom
argument_list|(
literal|null
argument_list|,
name|from
argument_list|)
return|;
block|}
comment|/**      * Add a new range to this aggregation.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the dates, inclusive      * @param to      *            the upper bound on the dates, exclusive      */
DECL|method|addRange
specifier|public
name|DateRangeAggregationBuilder
name|addRange
parameter_list|(
name|String
name|key
parameter_list|,
name|DateTime
name|from
parameter_list|,
name|DateTime
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|convertDateTime
argument_list|(
name|from
argument_list|)
argument_list|,
name|convertDateTime
argument_list|(
name|to
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|convertDateTime
specifier|private
name|Double
name|convertDateTime
parameter_list|(
name|DateTime
name|dateTime
parameter_list|)
block|{
if|if
condition|(
name|dateTime
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|(
name|double
operator|)
name|dateTime
operator|.
name|getMillis
argument_list|()
return|;
block|}
block|}
comment|/**      * Same as {@link #addRange(String, DateTime, DateTime)} but the key will be      * automatically generated based on<code>from</code> and<code>to</code>.      */
DECL|method|addRange
specifier|public
name|DateRangeAggregationBuilder
name|addRange
parameter_list|(
name|DateTime
name|from
parameter_list|,
name|DateTime
name|to
parameter_list|)
block|{
return|return
name|addRange
argument_list|(
literal|null
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no lower bound.      *      * @param key      *            the key to use for this range in the response      * @param to      *            the upper bound on the dates, exclusive      */
DECL|method|addUnboundedTo
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedTo
parameter_list|(
name|String
name|key
parameter_list|,
name|DateTime
name|to
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|convertDateTime
argument_list|(
name|to
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedTo(String, DateTime)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedTo
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedTo
parameter_list|(
name|DateTime
name|to
parameter_list|)
block|{
return|return
name|addUnboundedTo
argument_list|(
literal|null
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Add a new range with no upper bound.      *      * @param key      *            the key to use for this range in the response      * @param from      *            the lower bound on the distances, inclusive      */
DECL|method|addUnboundedFrom
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedFrom
parameter_list|(
name|String
name|key
parameter_list|,
name|DateTime
name|from
parameter_list|)
block|{
name|addRange
argument_list|(
operator|new
name|Range
argument_list|(
name|key
argument_list|,
name|convertDateTime
argument_list|(
name|from
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Same as {@link #addUnboundedFrom(String, DateTime)} but the key will be      * computed automatically.      */
DECL|method|addUnboundedFrom
specifier|public
name|DateRangeAggregationBuilder
name|addUnboundedFrom
parameter_list|(
name|DateTime
name|from
parameter_list|)
block|{
return|return
name|addUnboundedFrom
argument_list|(
literal|null
argument_list|,
name|from
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerBuild
specifier|protected
name|DateRangeAggregatorFactory
name|innerBuild
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|Numeric
argument_list|>
name|config
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|Builder
name|subFactoriesBuilder
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We need to call processRanges here so they are parsed and we know whether `now` has been used before we make
comment|// the decision of whether to cache the request
name|Range
index|[]
name|ranges
init|=
name|processRanges
argument_list|(
name|context
argument_list|,
name|config
argument_list|)
decl_stmt|;
return|return
operator|new
name|DateRangeAggregatorFactory
argument_list|(
name|name
argument_list|,
name|config
argument_list|,
name|ranges
argument_list|,
name|keyed
argument_list|,
name|rangeFactory
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|subFactoriesBuilder
argument_list|,
name|metaData
argument_list|)
return|;
block|}
block|}
end_class

end_unit

