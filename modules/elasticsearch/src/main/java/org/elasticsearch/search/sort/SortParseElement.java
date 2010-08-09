begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
comment|/**  * @author kimchy (shay.banon)  */
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
specifier|private
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
DECL|method|parse
annotation|@
name|Override
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
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
if|if
condition|(
operator|!
name|sortFields
operator|.
name|isEmpty
argument_list|()
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
name|innerJsonName
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
name|addSortField
argument_list|(
name|context
argument_list|,
name|sortFields
argument_list|,
name|fieldName
argument_list|,
name|reverse
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
name|mapperService
argument_list|()
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
literal|"]"
argument_list|)
throw|;
block|}
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|fieldName
argument_list|,
name|fieldMapper
operator|.
name|fieldDataType
argument_list|()
operator|.
name|newFieldComparatorSource
argument_list|(
name|context
operator|.
name|fieldDataCache
argument_list|()
argument_list|)
argument_list|,
name|reverse
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

