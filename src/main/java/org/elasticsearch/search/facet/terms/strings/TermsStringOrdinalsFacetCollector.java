begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms.strings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
operator|.
name|strings
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
name|ImmutableSet
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
name|index
operator|.
name|IndexReader
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
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|CacheRecycler
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
name|BoundedTreeSet
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
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|FieldDataCache
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
name|field
operator|.
name|data
operator|.
name|FieldData
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|field
operator|.
name|data
operator|.
name|strings
operator|.
name|StringFieldData
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
name|mapper
operator|.
name|MapperService
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
name|facet
operator|.
name|AbstractFacetCollector
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
name|facet
operator|.
name|Facet
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
name|facet
operator|.
name|terms
operator|.
name|TermsFacet
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
name|facet
operator|.
name|terms
operator|.
name|support
operator|.
name|EntryPriorityQueue
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
comment|/**  *  */
end_comment

begin_class
DECL|class|TermsStringOrdinalsFacetCollector
specifier|public
class|class
name|TermsStringOrdinalsFacetCollector
extends|extends
name|AbstractFacetCollector
block|{
DECL|field|fieldDataCache
specifier|private
specifier|final
name|FieldDataCache
name|fieldDataCache
decl_stmt|;
DECL|field|indexFieldName
specifier|private
specifier|final
name|String
name|indexFieldName
decl_stmt|;
DECL|field|comparatorType
specifier|private
specifier|final
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|numberOfShards
specifier|private
specifier|final
name|int
name|numberOfShards
decl_stmt|;
DECL|field|minCount
specifier|private
specifier|final
name|int
name|minCount
decl_stmt|;
DECL|field|fieldDataType
specifier|private
specifier|final
name|FieldDataType
name|fieldDataType
decl_stmt|;
DECL|field|fieldData
specifier|private
name|StringFieldData
name|fieldData
decl_stmt|;
DECL|field|aggregators
specifier|private
specifier|final
name|List
argument_list|<
name|ReaderAggregator
argument_list|>
name|aggregators
decl_stmt|;
DECL|field|current
specifier|private
name|ReaderAggregator
name|current
decl_stmt|;
DECL|field|missing
name|long
name|missing
decl_stmt|;
DECL|field|total
name|long
name|total
decl_stmt|;
DECL|field|excluded
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|excluded
decl_stmt|;
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
DECL|method|TermsStringOrdinalsFacetCollector
specifier|public
name|TermsStringOrdinalsFacetCollector
parameter_list|(
name|String
name|facetName
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|size
parameter_list|,
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|,
name|boolean
name|allTerms
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|excluded
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
name|super
argument_list|(
name|facetName
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldDataCache
operator|=
name|context
operator|.
name|fieldDataCache
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
name|this
operator|.
name|numberOfShards
operator|=
name|context
operator|.
name|numberOfShards
argument_list|()
expr_stmt|;
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartMappers
init|=
name|context
operator|.
name|smartFieldMappers
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartMappers
operator|==
literal|null
operator|||
operator|!
name|smartMappers
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Field ["
operator|+
name|fieldName
operator|+
literal|"] doesn't have a type, can't run terms long facet collector on it"
argument_list|)
throw|;
block|}
comment|// add type filter if there is exact doc mapper associated with it
if|if
condition|(
name|smartMappers
operator|.
name|hasDocMapper
argument_list|()
operator|&&
name|smartMappers
operator|.
name|explicitTypeInName
argument_list|()
condition|)
block|{
name|setFilter
argument_list|(
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|smartMappers
operator|.
name|docMapper
argument_list|()
operator|.
name|typeFilter
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|smartMappers
operator|.
name|mapper
argument_list|()
operator|.
name|fieldDataType
argument_list|()
operator|!=
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|STRING
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Field ["
operator|+
name|fieldName
operator|+
literal|"] is not of string type, can't run terms string facet collector on it"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexFieldName
operator|=
name|smartMappers
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
name|this
operator|.
name|fieldDataType
operator|=
name|smartMappers
operator|.
name|mapper
argument_list|()
operator|.
name|fieldDataType
argument_list|()
expr_stmt|;
if|if
condition|(
name|excluded
operator|==
literal|null
operator|||
name|excluded
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|excluded
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|excluded
operator|=
name|excluded
expr_stmt|;
block|}
name|this
operator|.
name|matcher
operator|=
name|pattern
operator|!=
literal|null
condition|?
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
else|:
literal|null
expr_stmt|;
comment|// minCount is offset by -1
if|if
condition|(
name|allTerms
condition|)
block|{
name|minCount
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|minCount
operator|=
literal|0
expr_stmt|;
block|}
name|this
operator|.
name|aggregators
operator|=
operator|new
name|ArrayList
argument_list|<
name|ReaderAggregator
argument_list|>
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|subReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|missing
operator|+=
name|current
operator|.
name|counts
index|[
literal|0
index|]
expr_stmt|;
name|total
operator|+=
name|current
operator|.
name|total
operator|-
name|current
operator|.
name|counts
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|values
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|aggregators
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
name|fieldData
operator|=
operator|(
name|StringFieldData
operator|)
name|fieldDataCache
operator|.
name|cache
argument_list|(
name|fieldDataType
argument_list|,
name|reader
argument_list|,
name|indexFieldName
argument_list|)
expr_stmt|;
name|current
operator|=
operator|new
name|ReaderAggregator
argument_list|(
name|fieldData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doCollect
specifier|protected
name|void
name|doCollect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldData
operator|.
name|forEachOrdinalInDoc
argument_list|(
name|doc
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|facet
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|missing
operator|+=
name|current
operator|.
name|counts
index|[
literal|0
index|]
expr_stmt|;
name|total
operator|+=
name|current
operator|.
name|total
operator|-
name|current
operator|.
name|counts
index|[
literal|0
index|]
expr_stmt|;
comment|// if we have values for this one, add it
if|if
condition|(
name|current
operator|.
name|values
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|aggregators
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
name|AggregatorPriorityQueue
name|queue
init|=
operator|new
name|AggregatorPriorityQueue
argument_list|(
name|aggregators
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ReaderAggregator
name|aggregator
range|:
name|aggregators
control|)
block|{
if|if
condition|(
name|aggregator
operator|.
name|nextPosition
argument_list|()
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|aggregator
argument_list|)
expr_stmt|;
block|}
block|}
comment|// YACK, we repeat the same logic, but once with an optimizer priority queue for smaller sizes
if|if
condition|(
name|size
operator|<
name|EntryPriorityQueue
operator|.
name|LIMIT
condition|)
block|{
comment|// optimize to use priority size
name|EntryPriorityQueue
name|ordered
init|=
operator|new
name|EntryPriorityQueue
argument_list|(
name|size
argument_list|,
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ReaderAggregator
name|agg
init|=
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|agg
operator|.
name|current
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
do|do
block|{
name|count
operator|+=
name|agg
operator|.
name|counts
index|[
name|agg
operator|.
name|position
index|]
expr_stmt|;
if|if
condition|(
name|agg
operator|.
name|nextPosition
argument_list|()
condition|)
block|{
name|agg
operator|=
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// we are done with this reader
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|agg
operator|=
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|agg
operator|!=
literal|null
operator|&&
name|value
operator|.
name|equals
argument_list|(
name|agg
operator|.
name|current
argument_list|)
condition|)
do|;
if|if
condition|(
name|count
operator|>
name|minCount
condition|)
block|{
if|if
condition|(
name|excluded
operator|!=
literal|null
operator|&&
name|excluded
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|matcher
operator|!=
literal|null
operator|&&
operator|!
name|matcher
operator|.
name|reset
argument_list|(
name|value
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|InternalStringTermsFacet
operator|.
name|StringEntry
name|entry
init|=
operator|new
name|InternalStringTermsFacet
operator|.
name|StringEntry
argument_list|(
name|value
argument_list|,
name|count
argument_list|)
decl_stmt|;
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
name|InternalStringTermsFacet
operator|.
name|StringEntry
index|[]
name|list
init|=
operator|new
name|InternalStringTermsFacet
operator|.
name|StringEntry
index|[
name|ordered
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ordered
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|list
index|[
name|i
index|]
operator|=
operator|(
name|InternalStringTermsFacet
operator|.
name|StringEntry
operator|)
name|ordered
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ReaderAggregator
name|aggregator
range|:
name|aggregators
control|)
block|{
name|CacheRecycler
operator|.
name|pushIntArray
argument_list|(
name|aggregator
operator|.
name|counts
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalStringTermsFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
argument_list|)
argument_list|,
name|missing
argument_list|,
name|total
argument_list|)
return|;
block|}
name|BoundedTreeSet
argument_list|<
name|InternalStringTermsFacet
operator|.
name|StringEntry
argument_list|>
name|ordered
init|=
operator|new
name|BoundedTreeSet
argument_list|<
name|InternalStringTermsFacet
operator|.
name|StringEntry
argument_list|>
argument_list|(
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|,
name|size
argument_list|)
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ReaderAggregator
name|agg
init|=
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|agg
operator|.
name|current
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
do|do
block|{
name|count
operator|+=
name|agg
operator|.
name|counts
index|[
name|agg
operator|.
name|position
index|]
expr_stmt|;
if|if
condition|(
name|agg
operator|.
name|nextPosition
argument_list|()
condition|)
block|{
name|agg
operator|=
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// we are done with this reader
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|agg
operator|=
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|agg
operator|!=
literal|null
operator|&&
name|value
operator|.
name|equals
argument_list|(
name|agg
operator|.
name|current
argument_list|)
condition|)
do|;
if|if
condition|(
name|count
operator|>
name|minCount
condition|)
block|{
if|if
condition|(
name|excluded
operator|!=
literal|null
operator|&&
name|excluded
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|matcher
operator|!=
literal|null
operator|&&
operator|!
name|matcher
operator|.
name|reset
argument_list|(
name|value
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|InternalStringTermsFacet
operator|.
name|StringEntry
name|entry
init|=
operator|new
name|InternalStringTermsFacet
operator|.
name|StringEntry
argument_list|(
name|value
argument_list|,
name|count
argument_list|)
decl_stmt|;
name|ordered
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|ReaderAggregator
name|aggregator
range|:
name|aggregators
control|)
block|{
name|CacheRecycler
operator|.
name|pushIntArray
argument_list|(
name|aggregator
operator|.
name|counts
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalStringTermsFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|ordered
argument_list|,
name|missing
argument_list|,
name|total
argument_list|)
return|;
block|}
DECL|class|ReaderAggregator
specifier|public
specifier|static
class|class
name|ReaderAggregator
implements|implements
name|FieldData
operator|.
name|OrdinalInDocProc
block|{
DECL|field|values
specifier|final
name|String
index|[]
name|values
decl_stmt|;
DECL|field|counts
specifier|final
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|position
name|int
name|position
init|=
literal|0
decl_stmt|;
DECL|field|current
name|String
name|current
decl_stmt|;
DECL|field|total
name|int
name|total
decl_stmt|;
DECL|method|ReaderAggregator
specifier|public
name|ReaderAggregator
parameter_list|(
name|StringFieldData
name|fieldData
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|fieldData
operator|.
name|values
argument_list|()
expr_stmt|;
name|this
operator|.
name|counts
operator|=
name|CacheRecycler
operator|.
name|popIntArray
argument_list|(
name|fieldData
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onOrdinal
specifier|public
name|void
name|onOrdinal
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|counts
index|[
name|ordinal
index|]
operator|++
expr_stmt|;
name|total
operator|++
expr_stmt|;
block|}
DECL|method|nextPosition
specifier|public
name|boolean
name|nextPosition
parameter_list|()
block|{
if|if
condition|(
operator|++
name|position
operator|>=
name|values
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|current
operator|=
name|values
index|[
name|position
index|]
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|class|AggregatorPriorityQueue
specifier|public
specifier|static
class|class
name|AggregatorPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|ReaderAggregator
argument_list|>
block|{
DECL|method|AggregatorPriorityQueue
specifier|public
name|AggregatorPriorityQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|ReaderAggregator
name|a
parameter_list|,
name|ReaderAggregator
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|current
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|current
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

