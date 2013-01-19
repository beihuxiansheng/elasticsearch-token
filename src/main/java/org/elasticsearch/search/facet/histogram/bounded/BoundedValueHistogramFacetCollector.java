begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.histogram.bounded
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
operator|.
name|bounded
package|;
end_package

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
name|CacheRecycler
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
name|DoubleValues
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
name|LongValues
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
name|histogram
operator|.
name|HistogramFacet
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BoundedValueHistogramFacetCollector
specifier|public
class|class
name|BoundedValueHistogramFacetCollector
extends|extends
name|AbstractFacetCollector
block|{
DECL|field|keyIndexFieldData
specifier|private
specifier|final
name|IndexNumericFieldData
name|keyIndexFieldData
decl_stmt|;
DECL|field|valueIndexFieldData
specifier|private
specifier|final
name|IndexNumericFieldData
name|valueIndexFieldData
decl_stmt|;
DECL|field|interval
specifier|private
specifier|final
name|long
name|interval
decl_stmt|;
DECL|field|comparatorType
specifier|private
specifier|final
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|keyValues
specifier|private
name|LongValues
name|keyValues
decl_stmt|;
DECL|field|histoProc
specifier|private
specifier|final
name|HistogramProc
name|histoProc
decl_stmt|;
DECL|method|BoundedValueHistogramFacetCollector
specifier|public
name|BoundedValueHistogramFacetCollector
parameter_list|(
name|String
name|facetName
parameter_list|,
name|IndexNumericFieldData
name|keyIndexFieldData
parameter_list|,
name|IndexNumericFieldData
name|valueIndexFieldData
parameter_list|,
name|long
name|interval
parameter_list|,
name|long
name|from
parameter_list|,
name|long
name|to
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
name|super
argument_list|(
name|facetName
argument_list|)
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
name|this
operator|.
name|keyIndexFieldData
operator|=
name|keyIndexFieldData
expr_stmt|;
name|this
operator|.
name|valueIndexFieldData
operator|=
name|valueIndexFieldData
expr_stmt|;
name|long
name|normalizedFrom
init|=
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
operator|(
name|double
operator|)
name|from
operator|/
name|interval
argument_list|)
operator|)
operator|*
name|interval
operator|)
decl_stmt|;
name|long
name|normalizedTo
init|=
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
operator|(
name|double
operator|)
name|to
operator|/
name|interval
argument_list|)
operator|)
operator|*
name|interval
operator|)
decl_stmt|;
if|if
condition|(
operator|(
name|to
operator|%
name|interval
operator|)
operator|!=
literal|0
condition|)
block|{
name|normalizedTo
operator|+=
name|interval
expr_stmt|;
block|}
name|long
name|offset
init|=
operator|-
name|normalizedFrom
decl_stmt|;
name|int
name|size
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|normalizedTo
operator|-
name|normalizedFrom
operator|)
operator|/
name|interval
argument_list|)
decl_stmt|;
name|histoProc
operator|=
operator|new
name|HistogramProc
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|interval
argument_list|,
name|offset
argument_list|,
name|size
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
name|keyValues
operator|.
name|forEachValueInDoc
argument_list|(
name|doc
argument_list|,
name|histoProc
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
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|keyValues
operator|=
name|keyIndexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getLongValues
argument_list|()
expr_stmt|;
name|histoProc
operator|.
name|valueValues
operator|=
name|valueIndexFieldData
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
DECL|method|facet
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
return|return
operator|new
name|InternalBoundedFullHistogramFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|interval
argument_list|,
operator|-
name|histoProc
operator|.
name|offset
argument_list|,
name|histoProc
operator|.
name|size
argument_list|,
name|histoProc
operator|.
name|entries
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|class|HistogramProc
specifier|public
specifier|static
class|class
name|HistogramProc
implements|implements
name|LongValues
operator|.
name|ValueInDocProc
block|{
DECL|field|from
specifier|final
name|long
name|from
decl_stmt|;
DECL|field|to
specifier|final
name|long
name|to
decl_stmt|;
DECL|field|interval
specifier|final
name|long
name|interval
decl_stmt|;
DECL|field|offset
specifier|final
name|long
name|offset
decl_stmt|;
DECL|field|size
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|entries
specifier|final
name|Object
index|[]
name|entries
decl_stmt|;
DECL|field|valueValues
name|DoubleValues
name|valueValues
decl_stmt|;
DECL|field|valueAggregator
specifier|final
name|ValueAggregator
name|valueAggregator
init|=
operator|new
name|ValueAggregator
argument_list|()
decl_stmt|;
DECL|method|HistogramProc
specifier|public
name|HistogramProc
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|,
name|long
name|interval
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|CacheRecycler
operator|.
name|popObjectArray
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMissing
specifier|public
name|void
name|onMissing
parameter_list|(
name|int
name|docId
parameter_list|)
block|{         }
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
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|<=
name|from
operator|||
name|value
operator|>
name|to
condition|)
block|{
comment|// bounds check
return|return;
block|}
name|int
name|index
init|=
operator|(
call|(
name|int
call|)
argument_list|(
operator|(
name|value
operator|+
name|offset
operator|)
operator|/
name|interval
argument_list|)
operator|)
decl_stmt|;
name|InternalBoundedFullHistogramFacet
operator|.
name|FullEntry
name|entry
init|=
operator|(
name|InternalBoundedFullHistogramFacet
operator|.
name|FullEntry
operator|)
name|entries
index|[
name|index
index|]
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
name|InternalBoundedFullHistogramFacet
operator|.
name|FullEntry
argument_list|(
name|index
argument_list|,
literal|0
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|entries
index|[
name|index
index|]
operator|=
name|entry
expr_stmt|;
block|}
name|entry
operator|.
name|count
operator|++
expr_stmt|;
name|valueAggregator
operator|.
name|entry
operator|=
name|entry
expr_stmt|;
name|valueValues
operator|.
name|forEachValueInDoc
argument_list|(
name|docId
argument_list|,
name|valueAggregator
argument_list|)
expr_stmt|;
block|}
DECL|class|ValueAggregator
specifier|public
specifier|static
class|class
name|ValueAggregator
implements|implements
name|DoubleValues
operator|.
name|ValueInDocProc
block|{
DECL|field|entry
name|InternalBoundedFullHistogramFacet
operator|.
name|FullEntry
name|entry
decl_stmt|;
annotation|@
name|Override
DECL|method|onMissing
specifier|public
name|void
name|onMissing
parameter_list|(
name|int
name|docId
parameter_list|)
block|{             }
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

