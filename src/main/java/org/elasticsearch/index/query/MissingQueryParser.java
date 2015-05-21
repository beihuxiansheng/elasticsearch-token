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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|Query
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
name|TermRangeQuery
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
name|index
operator|.
name|mapper
operator|.
name|internal
operator|.
name|FieldNamesFieldMapper
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MissingQueryParser
specifier|public
class|class
name|MissingQueryParser
extends|extends
name|BaseQueryParserTemp
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"missing"
decl_stmt|;
DECL|field|DEFAULT_NULL_VALUE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_NULL_VALUE
init|=
literal|false
decl_stmt|;
DECL|field|DEFAULT_EXISTENCE_VALUE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_EXISTENCE_VALUE
init|=
literal|true
decl_stmt|;
annotation|@
name|Inject
DECL|method|MissingQueryParser
specifier|public
name|MissingQueryParser
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
name|Query
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
name|queryName
init|=
literal|null
decl_stmt|;
name|boolean
name|nullValue
init|=
name|DEFAULT_NULL_VALUE
decl_stmt|;
name|boolean
name|existence
init|=
name|DEFAULT_EXISTENCE_VALUE
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
literal|"null_value"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|nullValue
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
literal|"existence"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|existence
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|queryName
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
argument_list|,
literal|"[missing] query does not support ["
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
argument_list|,
literal|"missing must be provided with a [field]"
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
name|existence
argument_list|,
name|nullValue
argument_list|,
name|queryName
argument_list|)
return|;
block|}
DECL|method|newFilter
specifier|public
specifier|static
name|Query
name|newFilter
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|String
name|fieldPattern
parameter_list|,
name|boolean
name|existence
parameter_list|,
name|boolean
name|nullValue
parameter_list|,
name|String
name|queryName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|existence
operator|&&
operator|!
name|nullValue
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"missing must have either existence, or null_value, or both set to true"
argument_list|)
throw|;
block|}
specifier|final
name|FieldNamesFieldMapper
name|fieldNamesMapper
init|=
operator|(
name|FieldNamesFieldMapper
operator|)
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|fullName
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
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
name|List
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
if|if
condition|(
name|existence
condition|)
block|{
comment|// if we ask for existence of fields, and we found none, then we should match on all
return|return
name|Queries
operator|.
name|newMatchAllQuery
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
name|Query
name|existenceFilter
init|=
literal|null
decl_stmt|;
name|Query
name|nullFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|existence
condition|)
block|{
name|BooleanQuery
name|boolFilter
init|=
operator|new
name|BooleanQuery
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
name|FieldMapper
name|mapper
init|=
name|parseContext
operator|.
name|fieldMapper
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|Query
name|filter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fieldNamesMapper
operator|!=
literal|null
operator|&&
name|fieldNamesMapper
operator|.
name|enabled
argument_list|()
condition|)
block|{
specifier|final
name|String
name|f
decl_stmt|;
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|f
operator|=
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
name|field
expr_stmt|;
block|}
name|filter
operator|=
name|fieldNamesMapper
operator|.
name|termQuery
argument_list|(
name|f
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
comment|// if _field_names are not indexed, we need to go the slow way
if|if
condition|(
name|filter
operator|==
literal|null
operator|&&
name|mapper
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
name|mapper
operator|.
name|rangeQuery
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
name|TermRangeQuery
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
name|existenceFilter
operator|=
name|boolFilter
expr_stmt|;
name|existenceFilter
operator|=
name|Queries
operator|.
name|not
argument_list|(
name|existenceFilter
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
if|if
condition|(
name|nullValue
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|FieldMapper
name|mapper
init|=
name|parseContext
operator|.
name|fieldMapper
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|nullFilter
operator|=
name|mapper
operator|.
name|nullValueFilter
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Query
name|filter
decl_stmt|;
if|if
condition|(
name|nullFilter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|existenceFilter
operator|!=
literal|null
condition|)
block|{
name|BooleanQuery
name|combined
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|combined
operator|.
name|add
argument_list|(
name|existenceFilter
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|combined
operator|.
name|add
argument_list|(
name|nullFilter
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// cache the not filter as well, so it will be faster
name|filter
operator|=
name|combined
expr_stmt|;
block|}
else|else
block|{
name|filter
operator|=
name|nullFilter
expr_stmt|;
block|}
block|}
else|else
block|{
name|filter
operator|=
name|existenceFilter
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|existenceFilter
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

