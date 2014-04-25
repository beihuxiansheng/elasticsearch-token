begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|ImmutableMap
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
name|search
operator|.
name|Filter
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
name|search
operator|.
name|Sort
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
name|search
operator|.
name|SortField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|Nullable
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
name|search
operator|.
name|MultiValueMode
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
name|ObjectMappers
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
name|core
operator|.
name|NumberFieldMapper
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
name|object
operator|.
name|ObjectMapper
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
name|query
operator|.
name|ParsedFilter
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
name|search
operator|.
name|nested
operator|.
name|NestedFieldComparatorSource
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
name|search
operator|.
name|nested
operator|.
name|NonNestedDocsFilter
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
name|SearchParseElement
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
name|SearchParseException
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SortParseElement
specifier|public
class|class
name|SortParseElement
implements|implements
name|SearchParseElement
block|{
DECL|field|SORT_SCORE
specifier|public
specifier|static
specifier|final
name|SortField
name|SORT_SCORE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
decl_stmt|;
DECL|field|SORT_SCORE_REVERSE
specifier|private
specifier|static
specifier|final
name|SortField
name|SORT_SCORE_REVERSE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|SORT_DOC
specifier|private
specifier|static
specifier|final
name|SortField
name|SORT_DOC
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOC
argument_list|)
decl_stmt|;
DECL|field|SORT_DOC_REVERSE
specifier|private
specifier|static
specifier|final
name|SortField
name|SORT_DOC_REVERSE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOC
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|SCORE_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_FIELD_NAME
init|=
literal|"_score"
decl_stmt|;
DECL|field|DOC_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DOC_FIELD_NAME
init|=
literal|"_doc"
decl_stmt|;
DECL|field|parsers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SortParser
argument_list|>
name|parsers
decl_stmt|;
DECL|method|SortParseElement
specifier|public
name|SortParseElement
parameter_list|()
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|SortParser
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|addParser
argument_list|(
name|builder
argument_list|,
operator|new
name|ScriptSortParser
argument_list|()
argument_list|)
expr_stmt|;
name|addParser
argument_list|(
name|builder
argument_list|,
operator|new
name|GeoDistanceSortParser
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parsers
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|addParser
specifier|private
name|void
name|addParser
parameter_list|(
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|SortParser
argument_list|>
name|parsers
parameter_list|,
name|SortParser
name|parser
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|parser
operator|.
name|names
argument_list|()
control|)
block|{
name|parsers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SortField
argument_list|>
name|sortFields
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
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
name|addCompoundSortField
argument_list|(
name|parser
argument_list|,
name|context
argument_list|,
name|sortFields
argument_list|)
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
name|addSortField
argument_list|(
name|context
argument_list|,
name|sortFields
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"malformed sort format, within the sort array, an object, or an actual string are allowed"
argument_list|)
throw|;
block|}
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
name|VALUE_STRING
condition|)
block|{
name|addSortField
argument_list|(
name|context
argument_list|,
name|sortFields
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
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
name|addCompoundSortField
argument_list|(
name|parser
argument_list|,
name|context
argument_list|,
name|sortFields
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"malformed sort format, either start with array, object, or an actual string"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|sortFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// optimize if we just sort on score non reversed, we don't really need sorting
name|boolean
name|sort
decl_stmt|;
if|if
condition|(
name|sortFields
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|sort
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|SortField
name|sortField
init|=
name|sortFields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortField
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|Type
operator|.
name|SCORE
operator|&&
operator|!
name|sortField
operator|.
name|getReverse
argument_list|()
condition|)
block|{
name|sort
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sort
condition|)
block|{
name|context
operator|.
name|sort
argument_list|(
operator|new
name|Sort
argument_list|(
name|sortFields
operator|.
name|toArray
argument_list|(
operator|new
name|SortField
index|[
name|sortFields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addCompoundSortField
specifier|private
name|void
name|addCompoundSortField
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|List
argument_list|<
name|SortField
argument_list|>
name|sortFields
parameter_list|)
throws|throws
name|Exception
block|{
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
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
name|String
name|missing
init|=
literal|null
decl_stmt|;
name|String
name|innerJsonName
init|=
literal|null
decl_stmt|;
name|boolean
name|ignoreUnmapped
init|=
literal|false
decl_stmt|;
name|MultiValueMode
name|sortMode
init|=
literal|null
decl_stmt|;
name|Filter
name|nestedFilter
init|=
literal|null
decl_stmt|;
name|String
name|nestedPath
init|=
literal|null
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
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
name|String
name|direction
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|direction
operator|.
name|equals
argument_list|(
literal|"asc"
argument_list|)
condition|)
block|{
name|reverse
operator|=
name|SCORE_FIELD_NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|direction
operator|.
name|equals
argument_list|(
literal|"desc"
argument_list|)
condition|)
block|{
name|reverse
operator|=
operator|!
name|SCORE_FIELD_NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"sort direction ["
operator|+
name|fieldName
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
name|addSortField
argument_list|(
name|context
argument_list|,
name|sortFields
argument_list|,
name|fieldName
argument_list|,
name|reverse
argument_list|,
name|ignoreUnmapped
argument_list|,
name|missing
argument_list|,
name|sortMode
argument_list|,
name|nestedPath
argument_list|,
name|nestedFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|parsers
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|sortFields
operator|.
name|add
argument_list|(
name|parsers
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|innerJsonName
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
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"reverse"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
condition|)
block|{
name|reverse
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
literal|"order"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"asc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
condition|)
block|{
name|reverse
operator|=
name|SCORE_FIELD_NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"desc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
condition|)
block|{
name|reverse
operator|=
operator|!
name|SCORE_FIELD_NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"missing"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
condition|)
block|{
name|missing
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ignore_unmapped"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
operator|||
literal|"ignoreUnmapped"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
condition|)
block|{
name|ignoreUnmapped
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
literal|"mode"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
condition|)
block|{
name|sortMode
operator|=
name|MultiValueMode
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
literal|"nested_path"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
operator|||
literal|"nestedPath"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
condition|)
block|{
name|nestedPath
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"sort option ["
operator|+
name|innerJsonName
operator|+
literal|"] not supported"
argument_list|)
throw|;
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
literal|"nested_filter"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
operator|||
literal|"nestedFilter"
operator|.
name|equals
argument_list|(
name|innerJsonName
argument_list|)
condition|)
block|{
name|ParsedFilter
name|parsedFilter
init|=
name|context
operator|.
name|queryParserService
argument_list|()
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|nestedFilter
operator|=
name|parsedFilter
operator|==
literal|null
condition|?
literal|null
else|:
name|parsedFilter
operator|.
name|filter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"sort option ["
operator|+
name|innerJsonName
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
block|}
block|}
name|addSortField
argument_list|(
name|context
argument_list|,
name|sortFields
argument_list|,
name|fieldName
argument_list|,
name|reverse
argument_list|,
name|ignoreUnmapped
argument_list|,
name|missing
argument_list|,
name|sortMode
argument_list|,
name|nestedPath
argument_list|,
name|nestedFilter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|addSortField
specifier|private
name|void
name|addSortField
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|List
argument_list|<
name|SortField
argument_list|>
name|sortFields
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|boolean
name|ignoreUnmapped
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|missing
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|,
name|String
name|nestedPath
parameter_list|,
name|Filter
name|nestedFilter
parameter_list|)
block|{
if|if
condition|(
name|SCORE_FIELD_NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|reverse
condition|)
block|{
name|sortFields
operator|.
name|add
argument_list|(
name|SORT_SCORE_REVERSE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sortFields
operator|.
name|add
argument_list|(
name|SORT_SCORE
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|DOC_FIELD_NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|reverse
condition|)
block|{
name|sortFields
operator|.
name|add
argument_list|(
name|SORT_DOC_REVERSE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sortFields
operator|.
name|add
argument_list|(
name|SORT_DOC
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|FieldMapper
name|fieldMapper
init|=
name|context
operator|.
name|smartNameFieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|ignoreUnmapped
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"No mapping found for ["
operator|+
name|fieldName
operator|+
literal|"] in order to sort on"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|fieldMapper
operator|.
name|isSortable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Sorting not supported for field["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// Enable when we also know how to detect fields that do tokenize, but only emit one token
comment|/*if (fieldMapper instanceof StringFieldMapper) {                 StringFieldMapper stringFieldMapper = (StringFieldMapper) fieldMapper;                 if (stringFieldMapper.fieldType().tokenized()) {                     // Fail early                     throw new SearchParseException(context, "Can't sort on tokenized string field[" + fieldName + "]");                 }             }*/
comment|// We only support AVG and SUM on number based fields
if|if
condition|(
operator|!
operator|(
name|fieldMapper
operator|instanceof
name|NumberFieldMapper
operator|)
operator|&&
operator|(
name|sortMode
operator|==
name|MultiValueMode
operator|.
name|SUM
operator|||
name|sortMode
operator|==
name|MultiValueMode
operator|.
name|AVG
operator|)
condition|)
block|{
name|sortMode
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|sortMode
operator|==
literal|null
condition|)
block|{
name|sortMode
operator|=
name|resolveDefaultSortMode
argument_list|(
name|reverse
argument_list|)
expr_stmt|;
block|}
name|IndexFieldData
operator|.
name|XFieldComparatorSource
name|fieldComparatorSource
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
operator|.
name|comparatorSource
argument_list|(
name|missing
argument_list|,
name|sortMode
argument_list|)
decl_stmt|;
name|ObjectMapper
name|objectMapper
decl_stmt|;
if|if
condition|(
name|nestedPath
operator|!=
literal|null
condition|)
block|{
name|ObjectMappers
name|objectMappers
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|objectMapper
argument_list|(
name|nestedPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|objectMappers
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"failed to find nested object mapping for explicit nested path ["
operator|+
name|nestedPath
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|objectMapper
operator|=
name|objectMappers
operator|.
name|mapper
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|objectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"mapping for explicit nested path is not mapped as nested: ["
operator|+
name|nestedPath
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|objectMapper
operator|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|resolveClosestNestedObjectMapper
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|objectMapper
operator|!=
literal|null
operator|&&
name|objectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
name|Filter
name|rootDocumentsFilter
init|=
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|NonNestedDocsFilter
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|Filter
name|innerDocumentsFilter
decl_stmt|;
if|if
condition|(
name|nestedFilter
operator|!=
literal|null
condition|)
block|{
name|innerDocumentsFilter
operator|=
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|nestedFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|innerDocumentsFilter
operator|=
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|objectMapper
operator|.
name|nestedTypeFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fieldComparatorSource
operator|=
operator|new
name|NestedFieldComparatorSource
argument_list|(
name|sortMode
argument_list|,
name|fieldComparatorSource
argument_list|,
name|rootDocumentsFilter
argument_list|,
name|innerDocumentsFilter
argument_list|)
expr_stmt|;
block|}
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldComparatorSource
argument_list|,
name|reverse
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|resolveDefaultSortMode
specifier|private
specifier|static
name|MultiValueMode
name|resolveDefaultSortMode
parameter_list|(
name|boolean
name|reverse
parameter_list|)
block|{
return|return
name|reverse
condition|?
name|MultiValueMode
operator|.
name|MAX
else|:
name|MultiValueMode
operator|.
name|MIN
return|;
block|}
block|}
end_class

end_unit

