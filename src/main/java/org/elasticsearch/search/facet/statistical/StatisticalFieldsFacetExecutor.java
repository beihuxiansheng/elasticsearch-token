begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.statistical
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|statistical
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|StatisticalFieldsFacetExecutor
specifier|public
class|class
name|StatisticalFieldsFacetExecutor
extends|extends
name|FacetExecutor
block|{
DECL|field|indexFieldDatas
specifier|private
specifier|final
name|IndexNumericFieldData
index|[]
name|indexFieldDatas
decl_stmt|;
DECL|field|min
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|max
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|total
name|double
name|total
init|=
literal|0
decl_stmt|;
DECL|field|sumOfSquares
name|double
name|sumOfSquares
init|=
literal|0.0
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|missing
name|int
name|missing
decl_stmt|;
DECL|method|StatisticalFieldsFacetExecutor
specifier|public
name|StatisticalFieldsFacetExecutor
parameter_list|(
name|IndexNumericFieldData
index|[]
name|indexFieldDatas
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|indexFieldDatas
operator|=
name|indexFieldDatas
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
return|return
operator|new
name|InternalStatisticalFacet
argument_list|(
name|facetName
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|total
argument_list|,
name|sumOfSquares
argument_list|,
name|count
argument_list|)
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
DECL|field|statsProc
specifier|private
specifier|final
name|StatsProc
name|statsProc
init|=
operator|new
name|StatsProc
argument_list|()
decl_stmt|;
DECL|field|values
specifier|private
name|DoubleValues
index|[]
name|values
decl_stmt|;
DECL|method|Collector
specifier|public
name|Collector
parameter_list|()
block|{
name|this
operator|.
name|values
operator|=
operator|new
name|DoubleValues
index|[
name|indexFieldDatas
operator|.
name|length
index|]
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexFieldDatas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|indexFieldDatas
index|[
name|i
index|]
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
for|for
control|(
name|DoubleValues
name|value
range|:
name|values
control|)
block|{
name|value
operator|.
name|forEachValueInDoc
argument_list|(
name|doc
argument_list|,
name|statsProc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
block|{
name|StatisticalFieldsFacetExecutor
operator|.
name|this
operator|.
name|min
operator|=
name|statsProc
operator|.
name|min
expr_stmt|;
name|StatisticalFieldsFacetExecutor
operator|.
name|this
operator|.
name|max
operator|=
name|statsProc
operator|.
name|max
expr_stmt|;
name|StatisticalFieldsFacetExecutor
operator|.
name|this
operator|.
name|total
operator|=
name|statsProc
operator|.
name|total
expr_stmt|;
name|StatisticalFieldsFacetExecutor
operator|.
name|this
operator|.
name|sumOfSquares
operator|=
name|statsProc
operator|.
name|sumOfSquares
expr_stmt|;
name|StatisticalFieldsFacetExecutor
operator|.
name|this
operator|.
name|count
operator|=
name|statsProc
operator|.
name|count
expr_stmt|;
name|StatisticalFieldsFacetExecutor
operator|.
name|this
operator|.
name|missing
operator|=
name|statsProc
operator|.
name|missing
expr_stmt|;
block|}
block|}
DECL|class|StatsProc
specifier|public
specifier|static
class|class
name|StatsProc
implements|implements
name|DoubleValues
operator|.
name|ValueInDocProc
block|{
DECL|field|min
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|max
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|total
name|double
name|total
init|=
literal|0
decl_stmt|;
DECL|field|sumOfSquares
name|double
name|sumOfSquares
init|=
literal|0.0
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|missing
name|int
name|missing
decl_stmt|;
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
if|if
condition|(
name|value
operator|<
name|min
condition|)
block|{
name|min
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|value
expr_stmt|;
block|}
name|sumOfSquares
operator|+=
name|value
operator|*
name|value
expr_stmt|;
name|total
operator|+=
name|value
expr_stmt|;
name|count
operator|++
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
block|{
name|missing
operator|++
expr_stmt|;
block|}
DECL|method|min
specifier|public
specifier|final
name|double
name|min
parameter_list|()
block|{
return|return
name|min
return|;
block|}
DECL|method|max
specifier|public
specifier|final
name|double
name|max
parameter_list|()
block|{
return|return
name|max
return|;
block|}
DECL|method|total
specifier|public
specifier|final
name|double
name|total
parameter_list|()
block|{
return|return
name|total
return|;
block|}
DECL|method|count
specifier|public
specifier|final
name|long
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|sumOfSquares
specifier|public
specifier|final
name|double
name|sumOfSquares
parameter_list|()
block|{
return|return
name|sumOfSquares
return|;
block|}
block|}
block|}
end_class

end_unit

