begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|regex
operator|.
name|Regex
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
name|IndexFieldData
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
name|IndexOrdinalFieldData
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
name|script
operator|.
name|SearchScript
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
name|facet
operator|.
name|terms
operator|.
name|doubles
operator|.
name|TermsDoubleFacetCollector
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
name|index
operator|.
name|IndexNameFacetCollector
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
name|longs
operator|.
name|TermsLongFacetCollector
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
name|strings
operator|.
name|FieldsTermsStringFacetCollector
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
name|strings
operator|.
name|ScriptTermsStringFieldFacetCollector
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
name|strings
operator|.
name|TermsStringFacetCollector
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
name|strings
operator|.
name|TermsStringOrdinalsFacetCollector
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
DECL|class|TermsFacetProcessor
specifier|public
class|class
name|TermsFacetProcessor
extends|extends
name|AbstractComponent
implements|implements
name|FacetProcessor
block|{
annotation|@
name|Inject
DECL|method|TermsFacetProcessor
specifier|public
name|TermsFacetProcessor
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
name|InternalTermsFacet
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
name|TermsFacet
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
name|int
name|size
init|=
literal|10
decl_stmt|;
name|String
index|[]
name|fieldsNames
init|=
literal|null
decl_stmt|;
name|ImmutableSet
argument_list|<
name|BytesRef
argument_list|>
name|excluded
init|=
name|ImmutableSet
operator|.
name|of
argument_list|()
decl_stmt|;
name|String
name|regex
init|=
literal|null
decl_stmt|;
name|String
name|regexFlags
init|=
literal|null
decl_stmt|;
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
init|=
name|TermsFacet
operator|.
name|ComparatorType
operator|.
name|COUNT
decl_stmt|;
name|String
name|scriptLang
init|=
literal|null
decl_stmt|;
name|String
name|script
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
name|boolean
name|allTerms
init|=
literal|false
decl_stmt|;
name|String
name|executionHint
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
literal|"exclude"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|BytesRef
argument_list|>
name|builder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
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
name|builder
operator|.
name|add
argument_list|(
name|parser
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|excluded
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
elseif|else
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
literal|"script_field"
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
literal|"size"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|size
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"all_terms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"allTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|allTerms
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"regex"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|regex
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
literal|"regex_flags"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"regexFlags"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|regexFlags
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
literal|"order"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"comparator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|comparatorType
operator|=
name|TermsFacet
operator|.
name|ComparatorType
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
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
elseif|else
if|if
condition|(
literal|"execution_hint"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"executionHint"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|executionHint
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
literal|"_index"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|IndexNameFacetCollector
argument_list|(
name|facetName
argument_list|,
name|context
operator|.
name|shardTarget
argument_list|()
operator|.
name|index
argument_list|()
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|)
return|;
block|}
name|Pattern
name|pattern
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|regex
operator|!=
literal|null
condition|)
block|{
name|pattern
operator|=
name|Regex
operator|.
name|compile
argument_list|(
name|regex
argument_list|,
name|regexFlags
argument_list|)
expr_stmt|;
block|}
name|SearchScript
name|searchScript
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|searchScript
operator|=
name|context
operator|.
name|scriptService
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
argument_list|,
name|scriptLang
argument_list|,
name|script
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldsNames
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|FieldsTermsStringFacetCollector
argument_list|(
name|facetName
argument_list|,
name|fieldsNames
argument_list|,
name|size
argument_list|,
name|comparatorType
argument_list|,
name|allTerms
argument_list|,
name|context
argument_list|,
name|excluded
argument_list|,
name|pattern
argument_list|,
name|searchScript
argument_list|)
return|;
block|}
if|if
condition|(
name|field
operator|==
literal|null
operator|&&
name|fieldsNames
operator|==
literal|null
operator|&&
name|script
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ScriptTermsStringFieldFacetCollector
argument_list|(
name|facetName
argument_list|,
name|size
argument_list|,
name|comparatorType
argument_list|,
name|context
argument_list|,
name|excluded
argument_list|,
name|pattern
argument_list|,
name|scriptLang
argument_list|,
name|script
argument_list|,
name|params
argument_list|)
return|;
block|}
name|FieldMapper
name|fieldMapper
init|=
name|context
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
literal|"failed to find mapping for ["
operator|+
name|field
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|IndexFieldData
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
if|if
condition|(
name|indexFieldData
operator|instanceof
name|IndexNumericFieldData
condition|)
block|{
name|IndexNumericFieldData
name|indexNumericFieldData
init|=
operator|(
name|IndexNumericFieldData
operator|)
name|indexFieldData
decl_stmt|;
if|if
condition|(
name|indexNumericFieldData
operator|.
name|getNumericType
argument_list|()
operator|.
name|isFloatingPoint
argument_list|()
condition|)
block|{
return|return
operator|new
name|TermsDoubleFacetCollector
argument_list|(
name|facetName
argument_list|,
name|indexNumericFieldData
argument_list|,
name|size
argument_list|,
name|comparatorType
argument_list|,
name|allTerms
argument_list|,
name|context
argument_list|,
name|excluded
argument_list|,
name|searchScript
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TermsLongFacetCollector
argument_list|(
name|facetName
argument_list|,
name|indexNumericFieldData
argument_list|,
name|size
argument_list|,
name|comparatorType
argument_list|,
name|allTerms
argument_list|,
name|context
argument_list|,
name|excluded
argument_list|,
name|searchScript
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|script
operator|!=
literal|null
operator|||
literal|"map"
operator|.
name|equals
argument_list|(
name|executionHint
argument_list|)
condition|)
block|{
return|return
operator|new
name|TermsStringFacetCollector
argument_list|(
name|facetName
argument_list|,
name|indexFieldData
argument_list|,
name|size
argument_list|,
name|comparatorType
argument_list|,
name|allTerms
argument_list|,
name|context
argument_list|,
name|excluded
argument_list|,
name|pattern
argument_list|,
name|searchScript
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|indexFieldData
operator|instanceof
name|IndexOrdinalFieldData
condition|)
block|{
return|return
operator|new
name|TermsStringOrdinalsFacetCollector
argument_list|(
name|facetName
argument_list|,
operator|(
name|IndexOrdinalFieldData
operator|)
name|indexFieldData
argument_list|,
name|size
argument_list|,
name|comparatorType
argument_list|,
name|allTerms
argument_list|,
name|context
argument_list|,
name|excluded
argument_list|,
name|pattern
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TermsStringFacetCollector
argument_list|(
name|facetName
argument_list|,
name|indexFieldData
argument_list|,
name|size
argument_list|,
name|comparatorType
argument_list|,
name|allTerms
argument_list|,
name|context
argument_list|,
name|excluded
argument_list|,
name|pattern
argument_list|,
name|searchScript
argument_list|)
return|;
block|}
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
name|InternalTermsFacet
name|first
init|=
operator|(
name|InternalTermsFacet
operator|)
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|first
operator|.
name|reduce
argument_list|(
name|name
argument_list|,
name|facets
argument_list|)
return|;
block|}
block|}
end_class

end_unit

