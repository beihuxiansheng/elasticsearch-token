begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Strings
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|StatisticalFieldsFacetCollector
specifier|public
class|class
name|StatisticalFieldsFacetCollector
extends|extends
name|AbstractFacetCollector
block|{
DECL|field|fieldsNames
specifier|private
specifier|final
name|String
index|[]
name|fieldsNames
decl_stmt|;
DECL|field|indexFieldsNames
specifier|private
specifier|final
name|String
index|[]
name|indexFieldsNames
decl_stmt|;
DECL|field|fieldDataCache
specifier|private
specifier|final
name|FieldDataCache
name|fieldDataCache
decl_stmt|;
DECL|field|fieldsDataType
specifier|private
specifier|final
name|FieldDataType
index|[]
name|fieldsDataType
decl_stmt|;
DECL|field|fieldsData
specifier|private
name|NumericFieldData
index|[]
name|fieldsData
decl_stmt|;
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
DECL|method|StatisticalFieldsFacetCollector
specifier|public
name|StatisticalFieldsFacetCollector
parameter_list|(
name|String
name|facetName
parameter_list|,
name|String
index|[]
name|fieldsNames
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
name|fieldsNames
operator|=
name|fieldsNames
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
name|fieldsDataType
operator|=
operator|new
name|FieldDataType
index|[
name|fieldsNames
operator|.
name|length
index|]
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|NumericFieldData
index|[
name|fieldsNames
operator|.
name|length
index|]
expr_stmt|;
name|indexFieldsNames
operator|=
operator|new
name|String
index|[
name|fieldsNames
operator|.
name|length
index|]
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
name|fieldsNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
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
name|fieldsNames
index|[
name|i
index|]
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
literal|"No mapping found for field ["
operator|+
name|fieldsNames
index|[
name|i
index|]
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|indexFieldsNames
index|[
name|i
index|]
operator|=
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
name|fieldsDataType
index|[
name|i
index|]
operator|=
name|mapper
operator|.
name|fieldDataType
argument_list|()
expr_stmt|;
block|}
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
for|for
control|(
name|NumericFieldData
name|fieldData
range|:
name|fieldsData
control|)
block|{
name|fieldData
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldsNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fieldsData
index|[
name|i
index|]
operator|=
operator|(
name|NumericFieldData
operator|)
name|fieldDataCache
operator|.
name|cache
argument_list|(
name|fieldsDataType
index|[
name|i
index|]
argument_list|,
name|reader
argument_list|,
name|indexFieldsNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|facet
annotation|@
name|Override
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
return|return
operator|new
name|InternalStatisticalFacet
argument_list|(
name|facetName
argument_list|,
name|Strings
operator|.
name|arrayToCommaDelimitedString
argument_list|(
name|fieldsNames
argument_list|)
argument_list|,
name|statsProc
operator|.
name|min
argument_list|()
argument_list|,
name|statsProc
operator|.
name|max
argument_list|()
argument_list|,
name|statsProc
operator|.
name|total
argument_list|()
argument_list|,
name|statsProc
operator|.
name|sumOfSquares
argument_list|()
argument_list|,
name|statsProc
operator|.
name|count
argument_list|()
argument_list|)
return|;
block|}
DECL|class|StatsProc
specifier|public
specifier|static
class|class
name|StatsProc
implements|implements
name|NumericFieldData
operator|.
name|DoubleValueInDocProc
block|{
DECL|field|min
specifier|private
name|double
name|min
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
DECL|field|max
specifier|private
name|double
name|max
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
DECL|field|total
specifier|private
name|double
name|total
init|=
literal|0
decl_stmt|;
DECL|field|sumOfSquares
specifier|private
name|double
name|sumOfSquares
init|=
literal|0.0
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
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
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|<
name|min
operator|||
name|Double
operator|.
name|isNaN
argument_list|(
name|min
argument_list|)
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
operator|||
name|Double
operator|.
name|isNaN
argument_list|(
name|max
argument_list|)
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

