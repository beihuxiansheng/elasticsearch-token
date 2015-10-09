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
name|QueryWrapperFilter
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
operator|.
name|BitSetProducer
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
name|ParseField
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|IndexFieldData
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|MappedFieldType
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
name|LongFieldMapper
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
name|support
operator|.
name|NestedInnerQueryParseSupport
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
name|HashMap
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
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
DECL|field|IGNORE_UNMAPPED
specifier|public
specifier|static
specifier|final
name|ParseField
name|IGNORE_UNMAPPED
init|=
operator|new
name|ParseField
argument_list|(
literal|"ignore_unmapped"
argument_list|)
decl_stmt|;
DECL|field|UNMAPPED_TYPE
specifier|public
specifier|static
specifier|final
name|ParseField
name|UNMAPPED_TYPE
init|=
operator|new
name|ParseField
argument_list|(
literal|"unmapped_type"
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
DECL|field|PARSERS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SortParser
argument_list|>
name|PARSERS
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|SortParser
argument_list|>
name|parsers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|addParser
argument_list|(
name|parsers
argument_list|,
operator|new
name|ScriptSortParser
argument_list|()
argument_list|)
expr_stmt|;
name|addParser
argument_list|(
name|parsers
argument_list|,
operator|new
name|GeoDistanceSortParser
argument_list|()
argument_list|)
expr_stmt|;
name|PARSERS
operator|=
name|unmodifiableMap
argument_list|(
name|parsers
argument_list|)
expr_stmt|;
block|}
DECL|method|addParser
specifier|private
specifier|static
name|void
name|addParser
parameter_list|(
name|Map
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
operator|new
name|ArrayList
argument_list|<>
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
name|IllegalArgumentException
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
name|IllegalArgumentException
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
name|String
name|unmappedType
init|=
literal|null
decl_stmt|;
name|MultiValueMode
name|sortMode
init|=
literal|null
decl_stmt|;
name|NestedInnerQueryParseSupport
name|nestedFilterParseHelper
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
name|IllegalArgumentException
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
name|unmappedType
argument_list|,
name|missing
argument_list|,
name|sortMode
argument_list|,
name|nestedFilterParseHelper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|PARSERS
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
name|PARSERS
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
name|context
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|innerJsonName
argument_list|,
name|IGNORE_UNMAPPED
argument_list|)
condition|)
block|{
comment|// backward compatibility: ignore_unmapped has been replaced with unmapped_type
if|if
condition|(
name|unmappedType
operator|==
literal|null
comment|// don't override if unmapped_type has been provided too
operator|&&
name|parser
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|unmappedType
operator|=
name|LongFieldMapper
operator|.
name|CONTENT_TYPE
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|innerJsonName
argument_list|,
name|UNMAPPED_TYPE
argument_list|)
condition|)
block|{
name|unmappedType
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
if|if
condition|(
name|nestedFilterParseHelper
operator|==
literal|null
condition|)
block|{
name|nestedFilterParseHelper
operator|=
operator|new
name|NestedInnerQueryParseSupport
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|nestedFilterParseHelper
operator|.
name|setPath
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
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
if|if
condition|(
name|nestedFilterParseHelper
operator|==
literal|null
condition|)
block|{
name|nestedFilterParseHelper
operator|=
operator|new
name|NestedInnerQueryParseSupport
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|nestedFilterParseHelper
operator|.
name|filter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
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
name|unmappedType
argument_list|,
name|missing
argument_list|,
name|sortMode
argument_list|,
name|nestedFilterParseHelper
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
name|String
name|unmappedType
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
name|NestedInnerQueryParseSupport
name|nestedHelper
parameter_list|)
throws|throws
name|IOException
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
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|smartNameFieldType
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|unmappedType
operator|!=
literal|null
condition|)
block|{
name|fieldType
operator|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|unmappedFieldType
argument_list|(
name|unmappedType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
argument_list|,
literal|null
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|fieldType
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
argument_list|,
literal|null
argument_list|)
throw|;
block|}
comment|// Enable when we also know how to detect fields that do tokenize, but only emit one token
comment|/*if (fieldMapper instanceof StringFieldMapper) {                 StringFieldMapper stringFieldMapper = (StringFieldMapper) fieldMapper;                 if (stringFieldMapper.fieldType().tokenized()) {                     // Fail early                     throw new SearchParseException(context, "Can't sort on tokenized string field[" + fieldName + "]");                 }             }*/
comment|// We only support AVG and SUM on number based fields
if|if
condition|(
name|fieldType
operator|.
name|isNumeric
argument_list|()
operator|==
literal|false
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
specifier|final
name|Nested
name|nested
decl_stmt|;
if|if
condition|(
name|nestedHelper
operator|!=
literal|null
operator|&&
name|nestedHelper
operator|.
name|getPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|BitSetProducer
name|rootDocumentsFilter
init|=
name|context
operator|.
name|bitsetFilterCache
argument_list|()
operator|.
name|getBitSetProducer
argument_list|(
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
argument_list|)
decl_stmt|;
name|Filter
name|innerDocumentsFilter
decl_stmt|;
if|if
condition|(
name|nestedHelper
operator|.
name|filterFound
argument_list|()
condition|)
block|{
comment|// TODO: use queries instead
name|innerDocumentsFilter
operator|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|nestedHelper
operator|.
name|getInnerFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|innerDocumentsFilter
operator|=
name|nestedHelper
operator|.
name|getNestedObjectMapper
argument_list|()
operator|.
name|nestedTypeFilter
argument_list|()
expr_stmt|;
block|}
name|nested
operator|=
operator|new
name|Nested
argument_list|(
name|rootDocumentsFilter
argument_list|,
name|innerDocumentsFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nested
operator|=
literal|null
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
name|fieldType
argument_list|)
operator|.
name|comparatorSource
argument_list|(
name|missing
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
decl_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|fieldType
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

