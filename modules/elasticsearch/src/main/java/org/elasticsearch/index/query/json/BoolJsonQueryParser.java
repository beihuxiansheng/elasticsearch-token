begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|json
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonToken
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
name|AbstractIndexComponent
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
name|Index
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
name|QueryParsingException
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
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|Settings
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
name|com
operator|.
name|google
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|BoolJsonQueryParser
specifier|public
class|class
name|BoolJsonQueryParser
extends|extends
name|AbstractIndexComponent
implements|implements
name|JsonQueryParser
block|{
DECL|method|BoolJsonQueryParser
annotation|@
name|Inject
specifier|public
name|BoolJsonQueryParser
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|name
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"bool"
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|(
name|JsonQueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|JsonParser
name|jp
init|=
name|parseContext
operator|.
name|jp
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
name|JsonToken
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|jp
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|JsonToken
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|jp
operator|.
name|getCurrentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|JsonToken
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
name|JsonToken
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
name|jp
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|JsonToken
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
name|jp
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|JsonToken
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
name|jp
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|JsonToken
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
operator|==
name|JsonToken
operator|.
name|VALUE_TRUE
operator|||
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_FALSE
condition|)
block|{
if|if
condition|(
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
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_TRUE
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_NUMBER_INT
condition|)
block|{
if|if
condition|(
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
name|jp
operator|.
name|getIntValue
argument_list|()
operator|!=
literal|0
expr_stmt|;
block|}
block|}
else|else
block|{
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
name|jp
operator|.
name|getFloatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
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
name|jp
operator|.
name|getIntValue
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
name|query
return|;
block|}
block|}
end_class

end_unit

