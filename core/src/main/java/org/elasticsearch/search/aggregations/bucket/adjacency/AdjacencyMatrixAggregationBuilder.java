begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.adjacency
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
name|adjacency
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
name|XContentBuilder
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
name|IndexSettings
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
name|search
operator|.
name|aggregations
operator|.
name|AbstractAggregationBuilder
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
name|Aggregator
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
name|adjacency
operator|.
name|AdjacencyMatrixAggregator
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
name|internal
operator|.
name|SearchContext
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
name|query
operator|.
name|QueryPhaseExecutionException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
operator|.
name|Entry
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

begin_class
DECL|class|AdjacencyMatrixAggregationBuilder
specifier|public
class|class
name|AdjacencyMatrixAggregationBuilder
extends|extends
name|AbstractAggregationBuilder
argument_list|<
name|AdjacencyMatrixAggregationBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"adjacency_matrix"
decl_stmt|;
DECL|field|DEFAULT_SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_SEPARATOR
init|=
literal|"&"
decl_stmt|;
DECL|field|SEPARATOR_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|SEPARATOR_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"separator"
argument_list|)
decl_stmt|;
DECL|field|FILTERS_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|FILTERS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"filters"
argument_list|)
decl_stmt|;
DECL|field|filters
specifier|private
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|filters
decl_stmt|;
DECL|field|separator
specifier|private
name|String
name|separator
init|=
name|DEFAULT_SEPARATOR
decl_stmt|;
DECL|method|getParser
specifier|public
specifier|static
name|Aggregator
operator|.
name|Parser
name|getParser
parameter_list|()
block|{
name|ObjectParser
argument_list|<
name|AdjacencyMatrixAggregationBuilder
argument_list|,
name|QueryParseContext
argument_list|>
name|parser
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|AdjacencyMatrixAggregationBuilder
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|parser
operator|.
name|declareString
argument_list|(
name|AdjacencyMatrixAggregationBuilder
operator|::
name|separator
argument_list|,
name|SEPARATOR_FIELD
argument_list|)
expr_stmt|;
name|parser
operator|.
name|declareNamedObjects
argument_list|(
name|AdjacencyMatrixAggregationBuilder
operator|::
name|setFiltersAsList
argument_list|,
name|KeyedFilter
operator|.
name|PARSER
argument_list|,
name|FILTERS_FIELD
argument_list|)
expr_stmt|;
return|return
operator|new
name|Aggregator
operator|.
name|Parser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
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
name|AdjacencyMatrixAggregationBuilder
name|result
init|=
name|parser
operator|.
name|parse
argument_list|(
name|context
operator|.
name|parser
argument_list|()
argument_list|,
operator|new
name|AdjacencyMatrixAggregationBuilder
argument_list|(
name|aggregationName
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|result
operator|.
name|checkConsistency
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
return|;
block|}
DECL|method|checkConsistency
specifier|protected
name|void
name|checkConsistency
parameter_list|()
block|{
if|if
condition|(
operator|(
name|filters
operator|==
literal|null
operator|)
operator|||
operator|(
name|filters
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"["
operator|+
name|name
operator|+
literal|"] is missing : "
operator|+
name|FILTERS_FIELD
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|" parameter"
argument_list|)
throw|;
block|}
block|}
DECL|method|setFiltersAsMap
specifier|protected
name|void
name|setFiltersAsMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|QueryBuilder
argument_list|>
name|filters
parameter_list|)
block|{
comment|// Convert uniquely named objects into internal KeyedFilters
name|this
operator|.
name|filters
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|QueryBuilder
argument_list|>
name|kv
range|:
name|filters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|this
operator|.
name|filters
operator|.
name|add
argument_list|(
operator|new
name|KeyedFilter
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
argument_list|,
name|kv
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// internally we want to have a fixed order of filters, regardless of
comment|// the order of the filters in the request
name|Collections
operator|.
name|sort
argument_list|(
name|this
operator|.
name|filters
argument_list|,
name|Comparator
operator|.
name|comparing
argument_list|(
name|KeyedFilter
operator|::
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setFiltersAsList
specifier|protected
name|void
name|setFiltersAsList
parameter_list|(
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|filters
parameter_list|)
block|{
name|this
operator|.
name|filters
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|filters
argument_list|)
expr_stmt|;
comment|// internally we want to have a fixed order of filters, regardless of
comment|// the order of the filters in the request
name|Collections
operator|.
name|sort
argument_list|(
name|this
operator|.
name|filters
argument_list|,
name|Comparator
operator|.
name|comparing
argument_list|(
name|KeyedFilter
operator|::
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param name      *            the name of this aggregation      */
DECL|method|AdjacencyMatrixAggregationBuilder
specifier|protected
name|AdjacencyMatrixAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param name      *            the name of this aggregation      * @param filters      *            the filters and their keys to use with this aggregation.      */
DECL|method|AdjacencyMatrixAggregationBuilder
specifier|public
name|AdjacencyMatrixAggregationBuilder
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
name|this
argument_list|(
name|name
argument_list|,
name|DEFAULT_SEPARATOR
argument_list|,
name|filters
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param name      *            the name of this aggregation      * @param separator      *            the string used to separate keys in intersections buckets e.g.      *&amp; character for keyed filters A and B would return an      *            intersection bucket named A&amp;B      * @param filters      *            the filters and their key to use with this aggregation.      */
DECL|method|AdjacencyMatrixAggregationBuilder
specifier|public
name|AdjacencyMatrixAggregationBuilder
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
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
name|setFiltersAsMap
argument_list|(
name|filters
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|AdjacencyMatrixAggregationBuilder
specifier|public
name|AdjacencyMatrixAggregationBuilder
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
name|int
name|filtersSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|separator
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|filters
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|filtersSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filtersSize
condition|;
name|i
operator|++
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
operator|new
name|KeyedFilter
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|writeVInt
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|separator
argument_list|)
expr_stmt|;
for|for
control|(
name|KeyedFilter
name|keyedFilter
range|:
name|filters
control|)
block|{
name|keyedFilter
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Set the separator used to join pairs of bucket keys      */
DECL|method|separator
specifier|public
name|AdjacencyMatrixAggregationBuilder
name|separator
parameter_list|(
name|String
name|separator
parameter_list|)
block|{
if|if
condition|(
name|separator
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[separator] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the separator used to join pairs of bucket keys      */
DECL|method|separator
specifier|public
name|String
name|separator
parameter_list|()
block|{
return|return
name|separator
return|;
block|}
comment|/**      * Get the filters. This will be an unmodifiable map      */
DECL|method|filters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|QueryBuilder
argument_list|>
name|filters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|QueryBuilder
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|this
operator|.
name|filters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|KeyedFilter
name|keyedFilter
range|:
name|this
operator|.
name|filters
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|keyedFilter
operator|.
name|key
argument_list|()
argument_list|,
name|keyedFilter
operator|.
name|filter
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|doBuild
specifier|protected
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|doBuild
parameter_list|(
name|SearchContext
name|context
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
name|int
name|maxFilters
init|=
name|context
operator|.
name|indexShard
argument_list|()
operator|.
name|indexSettings
argument_list|()
operator|.
name|getMaxAdjacencyMatrixFilters
argument_list|()
decl_stmt|;
if|if
condition|(
name|filters
operator|.
name|size
argument_list|()
operator|>
name|maxFilters
condition|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Number of filters is too large, must be less than or equal to: ["
operator|+
name|maxFilters
operator|+
literal|"] but was ["
operator|+
name|filters
operator|.
name|size
argument_list|()
operator|+
literal|"]."
operator|+
literal|"This limit can be set by changing the ["
operator|+
name|IndexSettings
operator|.
name|MAX_ADJACENCY_MATRIX_FILTERS_SETTING
operator|.
name|getKey
argument_list|()
operator|+
literal|"] index level setting."
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|rewrittenFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|KeyedFilter
name|kf
range|:
name|filters
control|)
block|{
name|rewrittenFilters
operator|.
name|add
argument_list|(
operator|new
name|KeyedFilter
argument_list|(
name|kf
operator|.
name|key
argument_list|()
argument_list|,
name|QueryBuilder
operator|.
name|rewriteQuery
argument_list|(
name|kf
operator|.
name|filter
argument_list|()
argument_list|,
name|context
operator|.
name|getQueryShardContext
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AdjacencyMatrixAggregatorFactory
argument_list|(
name|name
argument_list|,
name|rewrittenFilters
argument_list|,
name|separator
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
annotation|@
name|Override
DECL|method|internalXContent
specifier|protected
name|XContentBuilder
name|internalXContent
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
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|SEPARATOR_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|separator
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|AdjacencyMatrixAggregator
operator|.
name|FILTERS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KeyedFilter
name|keyedFilter
range|:
name|filters
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|keyedFilter
operator|.
name|key
argument_list|()
argument_list|,
name|keyedFilter
operator|.
name|filter
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
return|return
name|builder
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
name|filters
argument_list|,
name|separator
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
name|Object
name|obj
parameter_list|)
block|{
name|AdjacencyMatrixAggregationBuilder
name|other
init|=
operator|(
name|AdjacencyMatrixAggregationBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|filters
argument_list|,
name|other
operator|.
name|filters
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|separator
argument_list|,
name|other
operator|.
name|separator
argument_list|)
return|;
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
block|}
end_class

end_unit

