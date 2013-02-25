begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|queries
operator|.
name|TermsFilter
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
name|BooleanClause
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
name|lucene
operator|.
name|BytesRefs
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
name|*
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
name|cache
operator|.
name|filter
operator|.
name|support
operator|.
name|CacheKeyFilter
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
name|indices
operator|.
name|cache
operator|.
name|filter
operator|.
name|terms
operator|.
name|IndicesTermsFilterCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cache
operator|.
name|filter
operator|.
name|terms
operator|.
name|TermsLookup
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
import|import static
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
name|QueryParsers
operator|.
name|wrapSmartNameFilter
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TermsFilterParser
specifier|public
class|class
name|TermsFilterParser
implements|implements
name|FilterParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"terms"
decl_stmt|;
DECL|field|termsFilterCache
specifier|private
name|IndicesTermsFilterCache
name|termsFilterCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|TermsFilterParser
specifier|public
name|TermsFilterParser
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|names
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
block|,
literal|"in"
block|}
return|;
block|}
annotation|@
name|Inject
argument_list|(
name|optional
operator|=
literal|true
argument_list|)
DECL|method|setIndicesTermsFilterCache
specifier|public
name|void
name|setIndicesTermsFilterCache
parameter_list|(
name|IndicesTermsFilterCache
name|termsFilterCache
parameter_list|)
block|{
name|this
operator|.
name|termsFilterCache
operator|=
name|termsFilterCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Filter
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartNameFieldMappers
decl_stmt|;
name|Boolean
name|cache
init|=
literal|null
decl_stmt|;
name|String
name|filterName
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|String
name|lookupIndex
init|=
name|parseContext
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|String
name|lookupType
init|=
literal|null
decl_stmt|;
name|String
name|lookupId
init|=
literal|null
decl_stmt|;
name|String
name|lookupPath
init|=
literal|null
decl_stmt|;
name|CacheKeyFilter
operator|.
name|Key
name|cacheKey
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|execution
init|=
literal|"plain"
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|terms
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|START_ARRAY
condition|)
block|{
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
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
name|Object
name|value
init|=
name|parser
operator|.
name|objectBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"No value specified for term filter"
argument_list|)
throw|;
block|}
name|terms
operator|.
name|add
argument_list|(
name|value
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
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
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
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lookupIndex
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
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lookupType
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
literal|"id"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lookupId
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
literal|"path"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lookupPath
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
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[terms] filter does not support ["
operator|+
name|currentFieldName
operator|+
literal|"] within lookup element"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|lookupType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[terms] filter lookup element requires specifying the type"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lookupId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[terms] filter lookup element requires specifying the id"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lookupPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[terms] filter lookup element requires specifying the path"
argument_list|)
throw|;
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
literal|"execution"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|execution
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filterName
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
literal|"_cache"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|cache
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
literal|"_cache_key"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"_cacheKey"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|cacheKey
operator|=
operator|new
name|CacheKeyFilter
operator|.
name|Key
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
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[terms] filter does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"terms filter requires a field name, followed by array of terms"
argument_list|)
throw|;
block|}
name|FieldMapper
name|fieldMapper
init|=
literal|null
decl_stmt|;
name|smartNameFieldMappers
operator|=
name|parseContext
operator|.
name|smartFieldMappers
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|String
index|[]
name|previousTypes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|smartNameFieldMappers
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|smartNameFieldMappers
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
name|fieldMapper
operator|=
name|smartNameFieldMappers
operator|.
name|mapper
argument_list|()
expr_stmt|;
name|fieldName
operator|=
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
comment|// if we have a doc mapper, its explicit type, mark it
if|if
condition|(
name|smartNameFieldMappers
operator|.
name|explicitTypeInNameWithDocMapper
argument_list|()
condition|)
block|{
name|previousTypes
operator|=
name|QueryParseContext
operator|.
name|setTypesWithPrevious
argument_list|(
operator|new
name|String
index|[]
block|{
name|smartNameFieldMappers
operator|.
name|docMapper
argument_list|()
operator|.
name|type
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lookupId
operator|!=
literal|null
condition|)
block|{
comment|// external lookup, use it
name|TermsLookup
name|termsLookup
init|=
operator|new
name|TermsLookup
argument_list|(
name|fieldMapper
argument_list|,
name|lookupIndex
argument_list|,
name|lookupType
argument_list|,
name|lookupId
argument_list|,
name|lookupPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheKey
operator|==
literal|null
condition|)
block|{
name|cacheKey
operator|=
operator|new
name|CacheKeyFilter
operator|.
name|Key
argument_list|(
name|termsLookup
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Filter
name|filter
init|=
name|termsFilterCache
operator|.
name|lookupTermsFilter
argument_list|(
name|cacheKey
argument_list|,
name|termsLookup
argument_list|)
decl_stmt|;
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// cacheKey is passed as null, so we don't double cache the key
return|return
name|filter
return|;
block|}
if|if
condition|(
name|terms
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Queries
operator|.
name|MATCH_NO_FILTER
return|;
block|}
try|try
block|{
name|Filter
name|filter
decl_stmt|;
if|if
condition|(
literal|"plain"
operator|.
name|equals
argument_list|(
name|execution
argument_list|)
condition|)
block|{
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
name|fieldMapper
operator|.
name|termsFilter
argument_list|(
name|terms
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesRef
index|[]
name|filterValues
init|=
operator|new
name|BytesRef
index|[
name|terms
operator|.
name|size
argument_list|()
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
name|filterValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|filterValues
index|[
name|i
index|]
operator|=
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|filter
operator|=
operator|new
name|TermsFilter
argument_list|(
name|fieldName
argument_list|,
name|filterValues
argument_list|)
expr_stmt|;
block|}
comment|// cache the whole filter by default, or if explicitly told to
if|if
condition|(
name|cache
operator|==
literal|null
operator|||
name|cache
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"bool"
operator|.
name|equals
argument_list|(
name|execution
argument_list|)
condition|)
block|{
name|XBooleanFilter
name|boolFiler
init|=
operator|new
name|XBooleanFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|boolFiler
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|fieldMapper
operator|.
name|termFilter
argument_list|(
name|term
argument_list|,
name|parseContext
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|boolFiler
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|cacheFilter
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
name|filter
operator|=
name|boolFiler
expr_stmt|;
comment|// only cache if explicitly told to, since we cache inner filters
if|if
condition|(
name|cache
operator|!=
literal|null
operator|&&
name|cache
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"bool_nocache"
operator|.
name|equals
argument_list|(
name|execution
argument_list|)
condition|)
block|{
name|XBooleanFilter
name|boolFiler
init|=
operator|new
name|XBooleanFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|boolFiler
operator|.
name|add
argument_list|(
name|fieldMapper
operator|.
name|termFilter
argument_list|(
name|term
argument_list|,
name|parseContext
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|boolFiler
operator|.
name|add
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
name|filter
operator|=
name|boolFiler
expr_stmt|;
comment|// cache the whole filter by default, or if explicitly told to
if|if
condition|(
name|cache
operator|==
literal|null
operator|||
name|cache
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"and"
operator|.
name|equals
argument_list|(
name|execution
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Filter
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|fieldMapper
operator|.
name|termFilter
argument_list|(
name|term
argument_list|,
name|parseContext
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|cacheFilter
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|filter
operator|=
operator|new
name|AndFilter
argument_list|(
name|filters
argument_list|)
expr_stmt|;
comment|// only cache if explicitly told to, since we cache inner filters
if|if
condition|(
name|cache
operator|!=
literal|null
operator|&&
name|cache
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"and_nocache"
operator|.
name|equals
argument_list|(
name|execution
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Filter
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|fieldMapper
operator|.
name|termFilter
argument_list|(
name|term
argument_list|,
name|parseContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|filter
operator|=
operator|new
name|AndFilter
argument_list|(
name|filters
argument_list|)
expr_stmt|;
comment|// cache the whole filter by default, or if explicitly told to
if|if
condition|(
name|cache
operator|==
literal|null
operator|||
name|cache
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"or"
operator|.
name|equals
argument_list|(
name|execution
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Filter
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|fieldMapper
operator|.
name|termFilter
argument_list|(
name|term
argument_list|,
name|parseContext
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|cacheFilter
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|filter
operator|=
operator|new
name|OrFilter
argument_list|(
name|filters
argument_list|)
expr_stmt|;
comment|// only cache if explicitly told to, since we cache inner filters
if|if
condition|(
name|cache
operator|!=
literal|null
operator|&&
name|cache
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"or_nocache"
operator|.
name|equals
argument_list|(
name|execution
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Filter
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|fieldMapper
operator|.
name|termFilter
argument_list|(
name|term
argument_list|,
name|parseContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|filter
operator|=
operator|new
name|OrFilter
argument_list|(
name|filters
argument_list|)
expr_stmt|;
comment|// cache the whole filter by default, or if explicitly told to
if|if
condition|(
name|cache
operator|==
literal|null
operator|||
name|cache
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"bool filter execution value ["
operator|+
name|execution
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
name|filter
operator|=
name|wrapSmartNameFilter
argument_list|(
name|filter
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedFilter
argument_list|(
name|filterName
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
return|return
name|filter
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|smartNameFieldMappers
operator|!=
literal|null
operator|&&
name|smartNameFieldMappers
operator|.
name|explicitTypeInNameWithDocMapper
argument_list|()
condition|)
block|{
name|QueryParseContext
operator|.
name|setTypes
argument_list|(
name|previousTypes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

