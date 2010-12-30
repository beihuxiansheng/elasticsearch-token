begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|trove
operator|.
name|TLongDoubleHashMap
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
name|TLongDoubleIterator
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
name|TLongLongHashMap
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
name|TLongLongIterator
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
name|unit
operator|.
name|TimeValue
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HistogramFacetProcessor
specifier|public
class|class
name|HistogramFacetProcessor
extends|extends
name|AbstractComponent
implements|implements
name|FacetProcessor
block|{
DECL|method|HistogramFacetProcessor
annotation|@
name|Inject
specifier|public
name|HistogramFacetProcessor
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
name|InternalHistogramFacet
operator|.
name|registerStreams
argument_list|()
expr_stmt|;
block|}
DECL|method|types
annotation|@
name|Override
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
name|HistogramFacet
operator|.
name|TYPE
block|}
return|;
block|}
DECL|method|parse
annotation|@
name|Override
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
name|keyField
init|=
literal|null
decl_stmt|;
name|String
name|valueField
init|=
literal|null
decl_stmt|;
name|String
name|keyScript
init|=
literal|null
decl_stmt|;
name|String
name|valueScript
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
name|long
name|interval
init|=
literal|0
decl_stmt|;
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
init|=
name|HistogramFacet
operator|.
name|ComparatorType
operator|.
name|KEY
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
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
name|fieldName
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
name|fieldName
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
name|fieldName
argument_list|)
condition|)
block|{
name|keyField
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
literal|"key_field"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"keyField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|keyField
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
literal|"value_field"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"valueField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|valueField
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
literal|"interval"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|interval
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"time_interval"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|interval
operator|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|.
name|millis
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key_script"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"keyScript"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|keyScript
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
literal|"value_script"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"valueScript"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|valueScript
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
name|fieldName
argument_list|)
operator|||
literal|"comparator"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|comparatorType
operator|=
name|HistogramFacet
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
literal|"lang"
operator|.
name|equals
argument_list|(
name|fieldName
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
name|keyScript
operator|!=
literal|null
operator|&&
name|valueScript
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ScriptHistogramFacetCollector
argument_list|(
name|facetName
argument_list|,
name|scriptLang
argument_list|,
name|keyScript
argument_list|,
name|valueScript
argument_list|,
name|params
argument_list|,
name|interval
argument_list|,
name|comparatorType
argument_list|,
name|context
argument_list|)
return|;
block|}
if|if
condition|(
name|keyField
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
literal|"key field is required to be set for histogram facet, either using [field] or using [key_field]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|interval
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|FacetPhaseExecutionException
argument_list|(
name|facetName
argument_list|,
literal|"[interval] is required to be set for histogram facet"
argument_list|)
throw|;
block|}
if|if
condition|(
name|valueScript
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|KeyValueScriptHistogramFacetCollector
argument_list|(
name|facetName
argument_list|,
name|keyField
argument_list|,
name|scriptLang
argument_list|,
name|valueScript
argument_list|,
name|params
argument_list|,
name|interval
argument_list|,
name|comparatorType
argument_list|,
name|context
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|valueField
operator|==
literal|null
operator|||
name|keyField
operator|.
name|equals
argument_list|(
name|valueField
argument_list|)
condition|)
block|{
return|return
operator|new
name|HistogramFacetCollector
argument_list|(
name|facetName
argument_list|,
name|keyField
argument_list|,
name|interval
argument_list|,
name|comparatorType
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
comment|// we have a value field, and its different than the key
return|return
operator|new
name|KeyValueHistogramFacetCollector
argument_list|(
name|facetName
argument_list|,
name|keyField
argument_list|,
name|valueField
argument_list|,
name|interval
argument_list|,
name|comparatorType
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
DECL|method|reduce
annotation|@
name|Override
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
name|TLongLongHashMap
name|counts
init|=
literal|null
decl_stmt|;
name|TLongDoubleHashMap
name|totals
init|=
literal|null
decl_stmt|;
name|InternalHistogramFacet
name|firstHistoFacet
init|=
operator|(
name|InternalHistogramFacet
operator|)
name|facets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Facet
name|facet
range|:
name|facets
control|)
block|{
name|InternalHistogramFacet
name|histoFacet
init|=
operator|(
name|InternalHistogramFacet
operator|)
name|facet
decl_stmt|;
if|if
condition|(
operator|!
name|histoFacet
operator|.
name|counts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|counts
operator|==
literal|null
condition|)
block|{
name|counts
operator|=
name|histoFacet
operator|.
name|counts
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|TLongLongIterator
name|it
init|=
name|histoFacet
operator|.
name|counts
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|it
operator|.
name|advance
argument_list|()
expr_stmt|;
name|counts
operator|.
name|adjustOrPutValue
argument_list|(
name|it
operator|.
name|key
argument_list|()
argument_list|,
name|it
operator|.
name|value
argument_list|()
argument_list|,
name|it
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|histoFacet
operator|.
name|totals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|totals
operator|==
literal|null
condition|)
block|{
name|totals
operator|=
name|histoFacet
operator|.
name|totals
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|TLongDoubleIterator
name|it
init|=
name|histoFacet
operator|.
name|totals
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|it
operator|.
name|advance
argument_list|()
expr_stmt|;
name|totals
operator|.
name|adjustOrPutValue
argument_list|(
name|it
operator|.
name|key
argument_list|()
argument_list|,
name|it
operator|.
name|value
argument_list|()
argument_list|,
name|it
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|counts
operator|==
literal|null
condition|)
block|{
name|counts
operator|=
name|InternalHistogramFacet
operator|.
name|EMPTY_LONG_LONG_MAP
expr_stmt|;
block|}
if|if
condition|(
name|totals
operator|==
literal|null
condition|)
block|{
name|totals
operator|=
name|InternalHistogramFacet
operator|.
name|EMPTY_LONG_DOUBLE_MAP
expr_stmt|;
block|}
name|firstHistoFacet
operator|.
name|counts
operator|=
name|counts
expr_stmt|;
name|firstHistoFacet
operator|.
name|totals
operator|=
name|totals
expr_stmt|;
return|return
name|firstHistoFacet
return|;
block|}
block|}
end_class

end_unit

