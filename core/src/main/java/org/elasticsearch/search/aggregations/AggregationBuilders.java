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
name|geo
operator|.
name|GeoDistance
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
name|geo
operator|.
name|GeoPoint
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
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|adjacency
operator|.
name|AdjacencyMatrix
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
name|adjacency
operator|.
name|AdjacencyMatrixAggregationBuilder
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
name|filter
operator|.
name|Filter
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
name|filter
operator|.
name|FilterAggregationBuilder
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
name|filters
operator|.
name|Filters
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
name|filters
operator|.
name|FiltersAggregator
operator|.
name|KeyedFilter
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
name|filters
operator|.
name|FiltersAggregationBuilder
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
name|geogrid
operator|.
name|GeoGridAggregationBuilder
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
name|geogrid
operator|.
name|GeoHashGrid
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
name|global
operator|.
name|Global
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
name|global
operator|.
name|GlobalAggregationBuilder
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
name|histogram
operator|.
name|DateHistogramAggregationBuilder
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
name|histogram
operator|.
name|Histogram
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
name|histogram
operator|.
name|HistogramAggregationBuilder
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
name|missing
operator|.
name|Missing
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
name|missing
operator|.
name|MissingAggregationBuilder
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
name|nested
operator|.
name|Nested
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
name|nested
operator|.
name|NestedAggregationBuilder
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
name|nested
operator|.
name|ReverseNested
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
name|nested
operator|.
name|ReverseNestedAggregationBuilder
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
name|bucket
operator|.
name|range
operator|.
name|RangeAggregationBuilder
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
name|date
operator|.
name|DateRangeAggregationBuilder
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
name|geodistance
operator|.
name|GeoDistanceAggregationBuilder
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
name|ip
operator|.
name|IpRangeAggregationBuilder
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
name|sampler
operator|.
name|DiversifiedAggregationBuilder
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
name|sampler
operator|.
name|Sampler
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
name|sampler
operator|.
name|SamplerAggregationBuilder
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
name|significant
operator|.
name|SignificantTerms
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
name|significant
operator|.
name|SignificantTermsAggregationBuilder
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
name|terms
operator|.
name|Terms
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
name|terms
operator|.
name|TermsAggregationBuilder
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
name|metrics
operator|.
name|avg
operator|.
name|Avg
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
name|metrics
operator|.
name|avg
operator|.
name|AvgAggregationBuilder
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
name|metrics
operator|.
name|cardinality
operator|.
name|Cardinality
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
name|metrics
operator|.
name|cardinality
operator|.
name|CardinalityAggregationBuilder
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
name|metrics
operator|.
name|geobounds
operator|.
name|GeoBounds
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
name|metrics
operator|.
name|geobounds
operator|.
name|GeoBoundsAggregationBuilder
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
name|metrics
operator|.
name|geocentroid
operator|.
name|GeoCentroid
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
name|metrics
operator|.
name|geocentroid
operator|.
name|GeoCentroidAggregationBuilder
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
name|metrics
operator|.
name|max
operator|.
name|Max
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
name|metrics
operator|.
name|max
operator|.
name|MaxAggregationBuilder
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
name|metrics
operator|.
name|min
operator|.
name|Min
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
name|metrics
operator|.
name|min
operator|.
name|MinAggregationBuilder
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
name|metrics
operator|.
name|percentiles
operator|.
name|PercentileRanks
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
name|metrics
operator|.
name|percentiles
operator|.
name|PercentileRanksAggregationBuilder
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
name|metrics
operator|.
name|percentiles
operator|.
name|Percentiles
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
name|metrics
operator|.
name|percentiles
operator|.
name|PercentilesAggregationBuilder
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
name|metrics
operator|.
name|scripted
operator|.
name|ScriptedMetric
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
name|metrics
operator|.
name|scripted
operator|.
name|ScriptedMetricAggregationBuilder
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
name|metrics
operator|.
name|stats
operator|.
name|Stats
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
name|metrics
operator|.
name|stats
operator|.
name|StatsAggregationBuilder
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
name|metrics
operator|.
name|stats
operator|.
name|extended
operator|.
name|ExtendedStats
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
name|metrics
operator|.
name|stats
operator|.
name|extended
operator|.
name|ExtendedStatsAggregationBuilder
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
name|metrics
operator|.
name|sum
operator|.
name|Sum
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
name|metrics
operator|.
name|sum
operator|.
name|SumAggregationBuilder
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
name|metrics
operator|.
name|tophits
operator|.
name|TopHits
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
name|metrics
operator|.
name|tophits
operator|.
name|TopHitsAggregationBuilder
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
name|metrics
operator|.
name|valuecount
operator|.
name|ValueCount
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
name|metrics
operator|.
name|valuecount
operator|.
name|ValueCountAggregationBuilder
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

begin_comment
comment|/**  * Utility class to create aggregations.  */
end_comment

begin_class
DECL|class|AggregationBuilders
specifier|public
class|class
name|AggregationBuilders
block|{
DECL|method|AggregationBuilders
specifier|private
name|AggregationBuilders
parameter_list|()
block|{     }
comment|/**      * Create a new {@link ValueCount} aggregation with the given name.      */
DECL|method|count
specifier|public
specifier|static
name|ValueCountAggregationBuilder
name|count
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ValueCountAggregationBuilder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Avg} aggregation with the given name.      */
DECL|method|avg
specifier|public
specifier|static
name|AvgAggregationBuilder
name|avg
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|AvgAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Max} aggregation with the given name.      */
DECL|method|max
specifier|public
specifier|static
name|MaxAggregationBuilder
name|max
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MaxAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Min} aggregation with the given name.      */
DECL|method|min
specifier|public
specifier|static
name|MinAggregationBuilder
name|min
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MinAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Sum} aggregation with the given name.      */
DECL|method|sum
specifier|public
specifier|static
name|SumAggregationBuilder
name|sum
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SumAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Stats} aggregation with the given name.      */
DECL|method|stats
specifier|public
specifier|static
name|StatsAggregationBuilder
name|stats
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|StatsAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link ExtendedStats} aggregation with the given name.      */
DECL|method|extendedStats
specifier|public
specifier|static
name|ExtendedStatsAggregationBuilder
name|extendedStats
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ExtendedStatsAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Filter} aggregation with the given name.      */
DECL|method|filter
specifier|public
specifier|static
name|FilterAggregationBuilder
name|filter
parameter_list|(
name|String
name|name
parameter_list|,
name|QueryBuilder
name|filter
parameter_list|)
block|{
return|return
operator|new
name|FilterAggregationBuilder
argument_list|(
name|name
argument_list|,
name|filter
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Filters} aggregation with the given name.      */
DECL|method|filters
specifier|public
specifier|static
name|FiltersAggregationBuilder
name|filters
parameter_list|(
name|String
name|name
parameter_list|,
name|KeyedFilter
modifier|...
name|filters
parameter_list|)
block|{
return|return
operator|new
name|FiltersAggregationBuilder
argument_list|(
name|name
argument_list|,
name|filters
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Filters} aggregation with the given name.      */
DECL|method|filters
specifier|public
specifier|static
name|FiltersAggregationBuilder
name|filters
parameter_list|(
name|String
name|name
parameter_list|,
name|QueryBuilder
modifier|...
name|filters
parameter_list|)
block|{
return|return
operator|new
name|FiltersAggregationBuilder
argument_list|(
name|name
argument_list|,
name|filters
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link AdjacencyMatrix} aggregation with the given name.      */
DECL|method|adjacencyMatrix
specifier|public
specifier|static
name|AdjacencyMatrixAggregationBuilder
name|adjacencyMatrix
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|QueryBuilder
argument_list|>
name|filters
parameter_list|)
block|{
return|return
operator|new
name|AdjacencyMatrixAggregationBuilder
argument_list|(
name|name
argument_list|,
name|filters
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link AdjacencyMatrix} aggregation with the given name and separator      */
DECL|method|adjacencyMatrix
specifier|public
specifier|static
name|AdjacencyMatrixAggregationBuilder
name|adjacencyMatrix
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|separator
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|QueryBuilder
argument_list|>
name|filters
parameter_list|)
block|{
return|return
operator|new
name|AdjacencyMatrixAggregationBuilder
argument_list|(
name|name
argument_list|,
name|separator
argument_list|,
name|filters
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Sampler} aggregation with the given name.      */
DECL|method|sampler
specifier|public
specifier|static
name|SamplerAggregationBuilder
name|sampler
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SamplerAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Sampler} aggregation with the given name.      */
DECL|method|diversifiedSampler
specifier|public
specifier|static
name|DiversifiedAggregationBuilder
name|diversifiedSampler
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|DiversifiedAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Global} aggregation with the given name.      */
DECL|method|global
specifier|public
specifier|static
name|GlobalAggregationBuilder
name|global
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GlobalAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Missing} aggregation with the given name.      */
DECL|method|missing
specifier|public
specifier|static
name|MissingAggregationBuilder
name|missing
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MissingAggregationBuilder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Nested} aggregation with the given name.      */
DECL|method|nested
specifier|public
specifier|static
name|NestedAggregationBuilder
name|nested
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|NestedAggregationBuilder
argument_list|(
name|name
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link ReverseNested} aggregation with the given name.      */
DECL|method|reverseNested
specifier|public
specifier|static
name|ReverseNestedAggregationBuilder
name|reverseNested
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ReverseNestedAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link GeoDistance} aggregation with the given name.      */
DECL|method|geoDistance
specifier|public
specifier|static
name|GeoDistanceAggregationBuilder
name|geoDistance
parameter_list|(
name|String
name|name
parameter_list|,
name|GeoPoint
name|origin
parameter_list|)
block|{
return|return
operator|new
name|GeoDistanceAggregationBuilder
argument_list|(
name|name
argument_list|,
name|origin
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Histogram} aggregation with the given name.      */
DECL|method|histogram
specifier|public
specifier|static
name|HistogramAggregationBuilder
name|histogram
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|HistogramAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link GeoHashGrid} aggregation with the given name.      */
DECL|method|geohashGrid
specifier|public
specifier|static
name|GeoGridAggregationBuilder
name|geohashGrid
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeoGridAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link SignificantTerms} aggregation with the given name.      */
DECL|method|significantTerms
specifier|public
specifier|static
name|SignificantTermsAggregationBuilder
name|significantTerms
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SignificantTermsAggregationBuilder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link DateHistogramAggregationBuilder} aggregation with the given      * name.      */
DECL|method|dateHistogram
specifier|public
specifier|static
name|DateHistogramAggregationBuilder
name|dateHistogram
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|DateHistogramAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Range} aggregation with the given name.      */
DECL|method|range
specifier|public
specifier|static
name|RangeAggregationBuilder
name|range
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|RangeAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link DateRangeAggregationBuilder} aggregation with the      * given name.      */
DECL|method|dateRange
specifier|public
specifier|static
name|DateRangeAggregationBuilder
name|dateRange
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|DateRangeAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link IpRangeAggregationBuilder} aggregation with the      * given name.      */
DECL|method|ipRange
specifier|public
specifier|static
name|IpRangeAggregationBuilder
name|ipRange
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|IpRangeAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Terms} aggregation with the given name.      */
DECL|method|terms
specifier|public
specifier|static
name|TermsAggregationBuilder
name|terms
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|TermsAggregationBuilder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Percentiles} aggregation with the given name.      */
DECL|method|percentiles
specifier|public
specifier|static
name|PercentilesAggregationBuilder
name|percentiles
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|PercentilesAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link PercentileRanks} aggregation with the given name.      */
DECL|method|percentileRanks
specifier|public
specifier|static
name|PercentileRanksAggregationBuilder
name|percentileRanks
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|PercentileRanksAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link Cardinality} aggregation with the given name.      */
DECL|method|cardinality
specifier|public
specifier|static
name|CardinalityAggregationBuilder
name|cardinality
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|CardinalityAggregationBuilder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link TopHits} aggregation with the given name.      */
DECL|method|topHits
specifier|public
specifier|static
name|TopHitsAggregationBuilder
name|topHits
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|TopHitsAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link GeoBounds} aggregation with the given name.      */
DECL|method|geoBounds
specifier|public
specifier|static
name|GeoBoundsAggregationBuilder
name|geoBounds
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeoBoundsAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link GeoCentroid} aggregation with the given name.      */
DECL|method|geoCentroid
specifier|public
specifier|static
name|GeoCentroidAggregationBuilder
name|geoCentroid
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeoCentroidAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link ScriptedMetric} aggregation with the given name.      */
DECL|method|scriptedMetric
specifier|public
specifier|static
name|ScriptedMetricAggregationBuilder
name|scriptedMetric
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ScriptedMetricAggregationBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

