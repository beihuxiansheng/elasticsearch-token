begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|component
operator|.
name|AbstractComponent
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
name|settings
operator|.
name|Settings
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
name|FacetCollector
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
name|facet
operator|.
name|FacetProcessor
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|StatisticalFacetProcessor
specifier|public
class|class
name|StatisticalFacetProcessor
extends|extends
name|AbstractComponent
implements|implements
name|FacetProcessor
block|{
annotation|@
name|Inject
DECL|method|StatisticalFacetProcessor
specifier|public
name|StatisticalFacetProcessor
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|InternalStatisticalFacet
operator|.
name|registerStreams
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|StatisticalFacet
operator|.
name|TYPE
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|FacetCollector
name|parse
parameter_list|(
name|String
name|facetName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|field
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|fieldsNames
init|=
literal|null
decl_stmt|;
name|String
name|script
init|=
literal|null
decl_stmt|;
name|String
name|scriptLang
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
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
literal|"params"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|params
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
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
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"fields"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|4
argument_list|)
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
name|END_ARRAY
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fieldsNames
operator|=
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|field
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"script"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|script
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"lang"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scriptLang
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|fieldsNames
operator|!=
literal|null
condition|)
block|{
name|IndexNumericFieldData
index|[]
name|indexFieldDatas
init|=
operator|new
name|IndexNumericFieldData
index|[
name|fieldsNames
operator|.
name|length
index|]
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
name|fieldsNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FieldMapper
name|fieldMapper
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
name|fieldMapper
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
name|indexFieldDatas
index|[
name|i
index|]
operator|=
name|context
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StatisticalFieldsFacetCollector
argument_list|(
name|facetName
argument_list|,
name|indexFieldDatas
argument_list|,
name|context
argument_list|)
return|;
block|}
if|if
condition|(
name|script
operator|==
literal|null
operator|&&
name|field
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
literal|"statistical facet requires either [script] or [field] to be set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|FieldMapper
name|fieldMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMapper
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
name|field
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|IndexNumericFieldData
name|indexFieldData
init|=
name|context
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|fieldMapper
argument_list|)
decl_stmt|;
return|return
operator|new
name|StatisticalFacetCollector
argument_list|(
name|facetName
argument_list|,
name|indexFieldData
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ScriptStatisticalFacetCollector
argument_list|(
name|facetName
argument_list|,
name|scriptLang
argument_list|,
name|script
argument_list|,
name|params
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|Facet
name|reduce
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Facet
argument_list|>
name|facets
parameter_list|)
block|{
if|if
condition|(
name|facets
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|double
name|min
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
name|double
name|max
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
name|double
name|total
init|=
literal|0
decl_stmt|;
name|double
name|sumOfSquares
init|=
literal|0
decl_stmt|;
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Facet
name|facet
range|:
name|facets
control|)
block|{
if|if
condition|(
operator|!
name|facet
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|InternalStatisticalFacet
name|statsFacet
init|=
operator|(
name|InternalStatisticalFacet
operator|)
name|facet
decl_stmt|;
if|if
condition|(
name|statsFacet
operator|.
name|min
argument_list|()
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
name|statsFacet
operator|.
name|min
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|statsFacet
operator|.
name|max
argument_list|()
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
name|statsFacet
operator|.
name|max
argument_list|()
expr_stmt|;
block|}
name|total
operator|+=
name|statsFacet
operator|.
name|total
argument_list|()
expr_stmt|;
name|sumOfSquares
operator|+=
name|statsFacet
operator|.
name|sumOfSquares
argument_list|()
expr_stmt|;
name|count
operator|+=
name|statsFacet
operator|.
name|count
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|InternalStatisticalFacet
argument_list|(
name|name
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
block|}
end_class

end_unit

