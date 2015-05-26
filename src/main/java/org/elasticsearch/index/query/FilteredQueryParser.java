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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|FilteredQueryParser
specifier|public
class|class
name|FilteredQueryParser
extends|extends
name|BaseQueryParserTemp
block|{
annotation|@
name|Inject
DECL|method|FilteredQueryParser
specifier|public
name|FilteredQueryParser
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
name|FilteredQueryBuilder
operator|.
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
name|Query
name|query
init|=
name|Queries
operator|.
name|newMatchAllQuery
argument_list|()
decl_stmt|;
name|Query
name|filter
init|=
literal|null
decl_stmt|;
name|boolean
name|filterFound
init|=
literal|false
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|String
name|queryName
init|=
literal|null
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
name|parseContext
operator|.
name|isDeprecatedSetting
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// skip
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|query
operator|=
name|parseContext
operator|.
name|parseInnerQuery
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"filter"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filterFound
operator|=
literal|true
expr_stmt|;
name|filter
operator|=
name|parseContext
operator|.
name|parseInnerFilter
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
literal|"[filtered] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
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
literal|"strategy"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// ignore
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
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[filtered] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
comment|// parsed internally, but returned null during parsing...
if|if
condition|(
name|query
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
name|filter
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|filterFound
condition|)
block|{
comment|// we allow for null filter, so it makes compositions on the client side to be simpler
return|return
name|query
return|;
block|}
else|else
block|{
comment|// even if the filter is not found, and its null, we should simply ignore it, and go
comment|// by the query
return|return
name|query
return|;
block|}
block|}
if|if
condition|(
name|Queries
operator|.
name|isConstantMatchAllQuery
argument_list|(
name|filter
argument_list|)
condition|)
block|{
comment|// this is an instance of match all filter, just execute the query
return|return
name|query
return|;
block|}
comment|// if its a match_all query, use constant_score
if|if
condition|(
name|Queries
operator|.
name|isConstantMatchAllQuery
argument_list|(
name|query
argument_list|)
condition|)
block|{
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|q
return|;
block|}
name|BooleanQuery
name|filteredQuery
init|=
name|Queries
operator|.
name|filtered
argument_list|(
name|query
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|filteredQuery
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
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
name|filteredQuery
argument_list|)
expr_stmt|;
block|}
return|return
name|filteredQuery
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|FilteredQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|FilteredQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

