begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.histogram
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|histogram
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|LongObjectOpenHashMap
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
name|AtomicReaderContext
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
name|recycler
operator|.
name|Recycler
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
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|fielddata
operator|.
name|SortedNumericDoubleValues
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
name|DoubleFacetAggregatorBase
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
name|FacetExecutor
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
name|InternalFacet
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
name|List
import|;
end_import

begin_comment
comment|/**  * A histogram facet collector that uses the same field as the key as well as the  * value.  */
end_comment

begin_class
DECL|class|FullHistogramFacetExecutor
specifier|public
class|class
name|FullHistogramFacetExecutor
extends|extends
name|FacetExecutor
block|{
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexNumericFieldData
name|indexFieldData
decl_stmt|;
DECL|field|comparatorType
specifier|private
specifier|final
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|interval
specifier|final
name|long
name|interval
decl_stmt|;
DECL|field|entries
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|LongObjectOpenHashMap
argument_list|<
name|InternalFullHistogramFacet
operator|.
name|FullEntry
argument_list|>
argument_list|>
name|entries
decl_stmt|;
DECL|method|FullHistogramFacetExecutor
specifier|public
name|FullHistogramFacetExecutor
parameter_list|(
name|IndexNumericFieldData
name|indexFieldData
parameter_list|,
name|long
name|interval
parameter_list|,
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|context
operator|.
name|cacheRecycler
argument_list|()
operator|.
name|longObjectMap
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
return|return
operator|new
name|Collector
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|buildFacet
specifier|public
name|InternalFacet
name|buildFacet
parameter_list|(
name|String
name|facetName
parameter_list|)
block|{
name|List
argument_list|<
name|InternalFullHistogramFacet
operator|.
name|FullEntry
argument_list|>
name|fullEntries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|entries
operator|.
name|v
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
index|[]
name|states
init|=
name|entries
operator|.
name|v
argument_list|()
operator|.
name|allocated
decl_stmt|;
name|Object
index|[]
name|values
init|=
name|entries
operator|.
name|v
argument_list|()
operator|.
name|values
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|states
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|states
index|[
name|i
index|]
condition|)
block|{
name|fullEntries
operator|.
name|add
argument_list|(
operator|(
name|InternalFullHistogramFacet
operator|.
name|FullEntry
operator|)
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|entries
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|InternalFullHistogramFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|fullEntries
argument_list|)
return|;
block|}
DECL|method|bucket
specifier|public
specifier|static
name|long
name|bucket
parameter_list|(
name|double
name|value
parameter_list|,
name|long
name|interval
parameter_list|)
block|{
return|return
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|value
operator|/
name|interval
argument_list|)
operator|)
operator|*
name|interval
operator|)
return|;
block|}
DECL|class|Collector
class|class
name|Collector
extends|extends
name|FacetExecutor
operator|.
name|Collector
block|{
DECL|field|histoProc
specifier|private
specifier|final
name|HistogramProc
name|histoProc
decl_stmt|;
DECL|field|values
specifier|private
name|SortedNumericDoubleValues
name|values
decl_stmt|;
DECL|method|Collector
name|Collector
parameter_list|()
block|{
name|this
operator|.
name|histoProc
operator|=
operator|new
name|HistogramProc
argument_list|(
name|interval
argument_list|,
name|entries
operator|.
name|v
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getDoubleValues
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|histoProc
operator|.
name|onDoc
argument_list|(
name|doc
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
block|{         }
block|}
DECL|class|HistogramProc
specifier|public
specifier|final
specifier|static
class|class
name|HistogramProc
extends|extends
name|DoubleFacetAggregatorBase
block|{
DECL|field|interval
specifier|final
name|long
name|interval
decl_stmt|;
DECL|field|entries
specifier|final
name|LongObjectOpenHashMap
argument_list|<
name|InternalFullHistogramFacet
operator|.
name|FullEntry
argument_list|>
name|entries
decl_stmt|;
DECL|method|HistogramProc
specifier|public
name|HistogramProc
parameter_list|(
name|long
name|interval
parameter_list|,
name|LongObjectOpenHashMap
argument_list|<
name|InternalFullHistogramFacet
operator|.
name|FullEntry
argument_list|>
name|entries
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onValue
specifier|public
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|long
name|bucket
init|=
name|bucket
argument_list|(
name|value
argument_list|,
name|interval
argument_list|)
decl_stmt|;
name|InternalFullHistogramFacet
operator|.
name|FullEntry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|entry
operator|=
operator|new
name|InternalFullHistogramFacet
operator|.
name|FullEntry
argument_list|(
name|bucket
argument_list|,
literal|1
argument_list|,
name|value
argument_list|,
name|value
argument_list|,
literal|1
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|bucket
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|count
operator|++
expr_stmt|;
name|entry
operator|.
name|totalCount
operator|++
expr_stmt|;
name|entry
operator|.
name|total
operator|+=
name|value
expr_stmt|;
if|if
condition|(
name|value
operator|<
name|entry
operator|.
name|min
condition|)
block|{
name|entry
operator|.
name|min
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|>
name|entry
operator|.
name|max
condition|)
block|{
name|entry
operator|.
name|max
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

