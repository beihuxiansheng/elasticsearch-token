begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|search
operator|.
name|TermRangeFilter
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
name|lucene
operator|.
name|search
operator|.
name|XBooleanFilter
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
name|MapperService
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
name|Set
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
DECL|class|ExistsFilterParser
specifier|public
class|class
name|ExistsFilterParser
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
literal|"exists"
decl_stmt|;
annotation|@
name|Inject
DECL|method|ExistsFilterParser
specifier|public
name|ExistsFilterParser
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
block|}
return|;
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
name|String
name|fieldPattern
init|=
literal|null
decl_stmt|;
name|String
name|filterName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
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
name|fieldPattern
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
literal|"[exists] filter does not support ["
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
name|fieldPattern
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
literal|"exists must be provided with a [field]"
argument_list|)
throw|;
block|}
return|return
name|newFilter
argument_list|(
name|parseContext
argument_list|,
name|fieldPattern
argument_list|,
name|filterName
argument_list|)
return|;
block|}
DECL|method|newFilter
specifier|public
specifier|static
name|Filter
name|newFilter
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|String
name|fieldPattern
parameter_list|,
name|String
name|filterName
parameter_list|)
block|{
name|MapperService
operator|.
name|SmartNameObjectMapper
name|smartNameObjectMapper
init|=
name|parseContext
operator|.
name|smartObjectMapper
argument_list|(
name|fieldPattern
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartNameObjectMapper
operator|!=
literal|null
operator|&&
name|smartNameObjectMapper
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
comment|// automatic make the object mapper pattern
name|fieldPattern
operator|=
name|fieldPattern
operator|+
literal|".*"
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|parseContext
operator|.
name|simpleMatchToIndexNames
argument_list|(
name|fieldPattern
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no fields exists, so we should not match anything
return|return
name|Queries
operator|.
name|MATCH_NO_FILTER
return|;
block|}
name|MapperService
operator|.
name|SmartNameFieldMappers
name|nonNullFieldMappers
init|=
literal|null
decl_stmt|;
name|XBooleanFilter
name|boolFilter
init|=
operator|new
name|XBooleanFilter
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartNameFieldMappers
init|=
name|parseContext
operator|.
name|smartFieldMappers
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartNameFieldMappers
operator|!=
literal|null
condition|)
block|{
name|nonNullFieldMappers
operator|=
name|smartNameFieldMappers
expr_stmt|;
block|}
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|smartNameFieldMappers
operator|!=
literal|null
operator|&&
name|smartNameFieldMappers
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
name|filter
operator|=
name|smartNameFieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|rangeFilter
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
name|filter
operator|=
operator|new
name|TermRangeFilter
argument_list|(
name|field
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|boolFilter
operator|.
name|add
argument_list|(
name|filter
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
comment|// we always cache this one, really does not change... (exists)
comment|// its ok to cache under the fieldName cacheKey, since its per segment and the mapping applies to this data on this segment...
name|Filter
name|filter
init|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|boolFilter
argument_list|,
operator|new
name|CacheKeyFilter
operator|.
name|Key
argument_list|(
literal|"$exists$"
operator|+
name|fieldPattern
argument_list|)
argument_list|)
decl_stmt|;
name|filter
operator|=
name|wrapSmartNameFilter
argument_list|(
name|filter
argument_list|,
name|nonNullFieldMappers
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
block|}
end_class

end_unit

