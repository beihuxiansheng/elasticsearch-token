begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facets.range
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facets
operator|.
name|range
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
name|facets
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
name|facets
operator|.
name|collector
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
name|facets
operator|.
name|collector
operator|.
name|FacetCollectorParser
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
DECL|class|RangeFacetCollectorParser
specifier|public
class|class
name|RangeFacetCollectorParser
implements|implements
name|FacetCollectorParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"range"
decl_stmt|;
DECL|method|names
annotation|@
name|Override
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|NAME
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
name|List
argument_list|<
name|RangeFacet
operator|.
name|Entry
argument_list|>
name|entries
init|=
name|Lists
operator|.
name|newArrayList
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
name|START_ARRAY
condition|)
block|{
if|if
condition|(
operator|!
literal|"ranges"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
comment|// this is the actual field name, so also update the keyField
name|keyField
operator|=
name|fieldName
expr_stmt|;
block|}
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
name|RangeFacet
operator|.
name|Entry
name|entry
init|=
operator|new
name|RangeFacet
operator|.
name|Entry
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
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
literal|"from"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|entry
operator|.
name|fromAsString
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
literal|"to"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|entry
operator|.
name|toAsString
operator|=
name|parser
operator|.
name|text
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
literal|"from"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|entry
operator|.
name|from
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"to"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|entry
operator|.
name|to
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|entries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
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
block|}
block|}
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FacetPhaseExecutionException
argument_list|(
name|facetName
argument_list|,
literal|"no ranges defined for range facet"
argument_list|)
throw|;
block|}
name|RangeFacet
operator|.
name|Entry
index|[]
name|rangeEntries
init|=
name|entries
operator|.
name|toArray
argument_list|(
operator|new
name|RangeFacet
operator|.
name|Entry
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// fix the range entries if needed
if|if
condition|(
name|keyField
operator|!=
literal|null
condition|)
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
name|keyField
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
literal|"No mapping found for key_field ["
operator|+
name|keyField
operator|+
literal|"]"
argument_list|)
throw|;
block|}
for|for
control|(
name|RangeFacet
operator|.
name|Entry
name|entry
range|:
name|rangeEntries
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|fromAsString
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|from
operator|=
operator|(
operator|(
name|Number
operator|)
name|mapper
operator|.
name|valueFromString
argument_list|(
name|entry
operator|.
name|fromAsString
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|toAsString
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|to
operator|=
operator|(
operator|(
name|Number
operator|)
name|mapper
operator|.
name|valueFromString
argument_list|(
name|entry
operator|.
name|toAsString
argument_list|)
operator|)
operator|.
name|doubleValue
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
name|ScriptRangeFacetCollector
argument_list|(
name|facetName
argument_list|,
name|keyScript
argument_list|,
name|valueScript
argument_list|,
name|params
argument_list|,
name|rangeEntries
argument_list|,
name|context
operator|.
name|scriptService
argument_list|()
argument_list|,
name|context
operator|.
name|fieldDataCache
argument_list|()
argument_list|,
name|context
operator|.
name|mapperService
argument_list|()
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
literal|"key field is required to be set for range facet, either using [field] or using [key_field]"
argument_list|)
throw|;
block|}
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
name|RangeFacetCollector
argument_list|(
name|facetName
argument_list|,
name|keyField
argument_list|,
name|rangeEntries
argument_list|,
name|context
operator|.
name|fieldDataCache
argument_list|()
argument_list|,
name|context
operator|.
name|mapperService
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// we have a value field, and its different than the key
return|return
operator|new
name|KeyValueRangeFacetCollector
argument_list|(
name|facetName
argument_list|,
name|keyField
argument_list|,
name|valueField
argument_list|,
name|rangeEntries
argument_list|,
name|context
operator|.
name|fieldDataCache
argument_list|()
argument_list|,
name|context
operator|.
name|mapperService
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

