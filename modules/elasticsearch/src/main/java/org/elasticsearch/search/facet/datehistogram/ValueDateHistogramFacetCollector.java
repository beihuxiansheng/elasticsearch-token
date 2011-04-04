begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.datehistogram
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|datehistogram
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
name|IndexReader
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
name|joda
operator|.
name|time
operator|.
name|MutableDateTime
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
name|trove
operator|.
name|ExtTLongObjectHashMap
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
name|NumericFieldData
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
name|longs
operator|.
name|LongFieldData
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
name|FieldMapper
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
name|FacetPhaseExecutionException
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
comment|/**  * A histogram facet collector that uses different fields for the key and the value.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ValueDateHistogramFacetCollector
specifier|public
class|class
name|ValueDateHistogramFacetCollector
extends|extends
name|AbstractFacetCollector
block|{
DECL|field|keyIndexFieldName
specifier|private
specifier|final
name|String
name|keyIndexFieldName
decl_stmt|;
DECL|field|valueIndexFieldName
specifier|private
specifier|final
name|String
name|valueIndexFieldName
decl_stmt|;
DECL|field|dateTime
specifier|private
name|MutableDateTime
name|dateTime
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
name|DateHistogramFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|fieldDataCache
specifier|private
specifier|final
name|FieldDataCache
name|fieldDataCache
decl_stmt|;
DECL|field|keyFieldDataType
specifier|private
specifier|final
name|FieldDataType
name|keyFieldDataType
decl_stmt|;
DECL|field|keyFieldData
specifier|private
name|LongFieldData
name|keyFieldData
decl_stmt|;
DECL|field|valueFieldDataType
specifier|private
specifier|final
name|FieldDataType
name|valueFieldDataType
decl_stmt|;
DECL|field|histoProc
specifier|private
specifier|final
name|DateHistogramProc
name|histoProc
decl_stmt|;
DECL|method|ValueDateHistogramFacetCollector
specifier|public
name|ValueDateHistogramFacetCollector
parameter_list|(
name|String
name|facetName
parameter_list|,
name|String
name|keyFieldName
parameter_list|,
name|String
name|valueFieldName
parameter_list|,
name|MutableDateTime
name|dateTime
parameter_list|,
name|long
name|interval
parameter_list|,
name|DateHistogramFacet
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
name|dateTime
operator|=
name|dateTime
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
name|fieldDataCache
operator|=
name|context
operator|.
name|fieldDataCache
argument_list|()
expr_stmt|;
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartMappers
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|smartName
argument_list|(
name|keyFieldName
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
name|FacetPhaseExecutionException
argument_list|(
name|facetName
argument_list|,
literal|"No mapping found for field ["
operator|+
name|keyFieldName
operator|+
literal|"]"
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
name|keyIndexFieldName
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
name|keyFieldDataType
operator|=
name|smartMappers
operator|.
name|mapper
argument_list|()
operator|.
name|fieldDataType
argument_list|()
expr_stmt|;
name|FieldMapper
name|mapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|valueFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FacetPhaseExecutionException
argument_list|(
name|facetName
argument_list|,
literal|"No mapping found for value_field ["
operator|+
name|valueFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|valueIndexFieldName
operator|=
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
name|valueFieldDataType
operator|=
name|mapper
operator|.
name|fieldDataType
argument_list|()
expr_stmt|;
name|this
operator|.
name|histoProc
operator|=
operator|new
name|DateHistogramProc
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
DECL|method|doCollect
annotation|@
name|Override
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
name|keyFieldData
operator|.
name|forEachValueInDoc
argument_list|(
name|doc
argument_list|,
name|dateTime
argument_list|,
name|histoProc
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNextReader
annotation|@
name|Override
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
name|keyFieldData
operator|=
operator|(
name|LongFieldData
operator|)
name|fieldDataCache
operator|.
name|cache
argument_list|(
name|keyFieldDataType
argument_list|,
name|reader
argument_list|,
name|keyIndexFieldName
argument_list|)
expr_stmt|;
name|histoProc
operator|.
name|valueFieldData
operator|=
operator|(
name|NumericFieldData
operator|)
name|fieldDataCache
operator|.
name|cache
argument_list|(
name|valueFieldDataType
argument_list|,
name|reader
argument_list|,
name|valueIndexFieldName
argument_list|)
expr_stmt|;
block|}
DECL|method|facet
annotation|@
name|Override
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
name|CacheRecycler
operator|.
name|pushLongObjectMap
argument_list|(
name|histoProc
operator|.
name|entries
argument_list|)
expr_stmt|;
return|return
operator|new
name|InternalFullDateHistogramFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|histoProc
operator|.
name|entries
operator|.
name|valueCollection
argument_list|()
argument_list|)
return|;
block|}
DECL|class|DateHistogramProc
specifier|public
specifier|static
class|class
name|DateHistogramProc
implements|implements
name|LongFieldData
operator|.
name|DateValueInDocProc
block|{
DECL|field|entries
specifier|final
name|ExtTLongObjectHashMap
argument_list|<
name|InternalFullDateHistogramFacet
operator|.
name|FullEntry
argument_list|>
name|entries
init|=
name|CacheRecycler
operator|.
name|popLongObjectMap
argument_list|()
decl_stmt|;
DECL|field|interval
specifier|private
specifier|final
name|long
name|interval
decl_stmt|;
DECL|field|valueFieldData
name|NumericFieldData
name|valueFieldData
decl_stmt|;
DECL|method|DateHistogramProc
specifier|public
name|DateHistogramProc
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
block|}
DECL|method|onValue
annotation|@
name|Override
specifier|public
name|void
name|onValue
parameter_list|(
name|int
name|docId
parameter_list|,
name|MutableDateTime
name|dateTime
parameter_list|)
block|{
name|long
name|time
init|=
name|dateTime
operator|.
name|getMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|interval
operator|!=
literal|1
condition|)
block|{
name|time
operator|=
name|CountDateHistogramFacetCollector
operator|.
name|bucket
argument_list|(
name|time
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
name|InternalFullDateHistogramFacet
operator|.
name|FullEntry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
name|time
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|valueFieldData
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|double
index|[]
name|valuesValues
init|=
name|valueFieldData
operator|.
name|doubleValues
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|entry
operator|=
operator|new
name|InternalFullDateHistogramFacet
operator|.
name|FullEntry
argument_list|(
name|time
argument_list|,
literal|1
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|Double
operator|.
name|MIN_VALUE
argument_list|,
name|valuesValues
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|double
name|valueValue
range|:
name|valuesValues
control|)
block|{
name|entry
operator|.
name|total
operator|+=
name|valueValue
expr_stmt|;
if|if
condition|(
name|valueValue
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
name|valueValue
expr_stmt|;
block|}
if|if
condition|(
name|valueValue
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
name|valueValue
expr_stmt|;
block|}
block|}
name|entries
operator|.
name|put
argument_list|(
name|time
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|double
name|valueValue
init|=
name|valueFieldData
operator|.
name|doubleValue
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|entry
operator|=
operator|new
name|InternalFullDateHistogramFacet
operator|.
name|FullEntry
argument_list|(
name|time
argument_list|,
literal|1
argument_list|,
name|valueValue
argument_list|,
name|valueValue
argument_list|,
literal|1
argument_list|,
name|valueValue
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|time
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|entry
operator|.
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|valueFieldData
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|double
index|[]
name|valuesValues
init|=
name|valueFieldData
operator|.
name|doubleValues
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|entry
operator|.
name|totalCount
operator|+=
name|valuesValues
operator|.
name|length
expr_stmt|;
for|for
control|(
name|double
name|valueValue
range|:
name|valuesValues
control|)
block|{
name|entry
operator|.
name|total
operator|+=
name|valueValue
expr_stmt|;
if|if
condition|(
name|valueValue
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
name|valueValue
expr_stmt|;
block|}
if|if
condition|(
name|valueValue
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
name|valueValue
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|entry
operator|.
name|totalCount
operator|++
expr_stmt|;
name|double
name|valueValue
init|=
name|valueFieldData
operator|.
name|doubleValue
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|entry
operator|.
name|total
operator|+=
name|valueValue
expr_stmt|;
if|if
condition|(
name|valueValue
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
name|valueValue
expr_stmt|;
block|}
if|if
condition|(
name|valueValue
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
name|valueValue
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

