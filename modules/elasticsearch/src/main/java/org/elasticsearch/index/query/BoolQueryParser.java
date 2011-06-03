begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Query
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
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|BoolQueryParser
specifier|public
class|class
name|BoolQueryParser
implements|implements
name|QueryParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"bool"
decl_stmt|;
DECL|method|BoolQueryParser
annotation|@
name|Inject
specifier|public
name|BoolQueryParser
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"index.query.bool.max_clause_count"
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|boolean
name|disableCoord
init|=
literal|false
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|int
name|minimumNumberShouldMatch
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|newArrayList
argument_list|()
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
literal|"must"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"must_not"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"mustNot"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"should"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
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
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"must"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
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
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"must_not"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"mustNot"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
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
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"should"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
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
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"disable_coord"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"disableCoord"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|disableCoord
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
literal|"minimum_number_should_match"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minimumNumberShouldMatch"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minimumNumberShouldMatch
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
name|disableCoord
argument_list|)
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
if|if
condition|(
name|minimumNumberShouldMatch
operator|!=
operator|-
literal|1
condition|)
block|{
name|query
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|minimumNumberShouldMatch
argument_list|)
expr_stmt|;
block|}
return|return
name|optimizeQuery
argument_list|(
name|fixNegativeQueryIfNeeded
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

