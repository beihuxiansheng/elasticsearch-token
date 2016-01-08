begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
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
name|analysis
operator|.
name|Analyzer
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
name|BlendedTermQuery
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
name|BoostQuery
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
name|DisjunctionMaxQuery
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
name|collect
operator|.
name|Tuple
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
name|query
operator|.
name|AbstractQueryBuilder
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
name|MultiMatchQueryBuilder
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
name|QueryShardContext
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

begin_class
DECL|class|MultiMatchQuery
specifier|public
class|class
name|MultiMatchQuery
extends|extends
name|MatchQuery
block|{
DECL|field|groupTieBreaker
specifier|private
name|Float
name|groupTieBreaker
init|=
literal|null
decl_stmt|;
DECL|method|setTieBreaker
specifier|public
name|void
name|setTieBreaker
parameter_list|(
name|float
name|tieBreaker
parameter_list|)
block|{
name|this
operator|.
name|groupTieBreaker
operator|=
name|tieBreaker
expr_stmt|;
block|}
DECL|method|MultiMatchQuery
specifier|public
name|MultiMatchQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|parseAndApply
specifier|private
name|Query
name|parseAndApply
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|,
name|String
name|minimumShouldMatch
parameter_list|,
name|Float
name|boostValue
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|parse
argument_list|(
name|type
argument_list|,
name|fieldName
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|query
operator|=
name|Queries
operator|.
name|applyMinimumShouldMatch
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|query
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|!=
literal|null
operator|&&
name|boostValue
operator|!=
literal|null
operator|&&
name|boostValue
operator|!=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
condition|)
block|{
name|query
operator|=
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
name|boostValue
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|MultiMatchQueryBuilder
operator|.
name|Type
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldNames
parameter_list|,
name|Object
name|value
parameter_list|,
name|String
name|minimumShouldMatch
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldNames
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldBoost
init|=
name|fieldNames
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Float
name|boostValue
init|=
name|fieldBoost
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
name|parseAndApply
argument_list|(
name|type
operator|.
name|matchQueryType
argument_list|()
argument_list|,
name|fieldBoost
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|,
name|minimumShouldMatch
argument_list|,
name|boostValue
argument_list|)
return|;
block|}
specifier|final
name|float
name|tieBreaker
init|=
name|groupTieBreaker
operator|==
literal|null
condition|?
name|type
operator|.
name|tieBreaker
argument_list|()
else|:
name|groupTieBreaker
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PHRASE
case|:
case|case
name|PHRASE_PREFIX
case|:
case|case
name|BEST_FIELDS
case|:
case|case
name|MOST_FIELDS
case|:
name|queryBuilder
operator|=
operator|new
name|QueryBuilder
argument_list|(
name|tieBreaker
argument_list|)
expr_stmt|;
break|break;
case|case
name|CROSS_FIELDS
case|:
name|queryBuilder
operator|=
operator|new
name|CrossFieldsQueryBuilder
argument_list|(
name|tieBreaker
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No such type: "
operator|+
name|type
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Query
argument_list|>
name|queries
init|=
name|queryBuilder
operator|.
name|buildGroupedQueries
argument_list|(
name|type
argument_list|,
name|fieldNames
argument_list|,
name|value
argument_list|,
name|minimumShouldMatch
argument_list|)
decl_stmt|;
return|return
name|queryBuilder
operator|.
name|combineGrouped
argument_list|(
name|queries
argument_list|)
return|;
block|}
DECL|field|queryBuilder
specifier|private
name|QueryBuilder
name|queryBuilder
decl_stmt|;
DECL|class|QueryBuilder
specifier|public
class|class
name|QueryBuilder
block|{
DECL|field|groupDismax
specifier|protected
specifier|final
name|boolean
name|groupDismax
decl_stmt|;
DECL|field|tieBreaker
specifier|protected
specifier|final
name|float
name|tieBreaker
decl_stmt|;
DECL|method|QueryBuilder
specifier|public
name|QueryBuilder
parameter_list|(
name|float
name|tieBreaker
parameter_list|)
block|{
name|this
argument_list|(
name|tieBreaker
operator|!=
literal|1.0f
argument_list|,
name|tieBreaker
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryBuilder
specifier|public
name|QueryBuilder
parameter_list|(
name|boolean
name|groupDismax
parameter_list|,
name|float
name|tieBreaker
parameter_list|)
block|{
name|this
operator|.
name|groupDismax
operator|=
name|groupDismax
expr_stmt|;
name|this
operator|.
name|tieBreaker
operator|=
name|tieBreaker
expr_stmt|;
block|}
DECL|method|buildGroupedQueries
specifier|public
name|List
argument_list|<
name|Query
argument_list|>
name|buildGroupedQueries
parameter_list|(
name|MultiMatchQueryBuilder
operator|.
name|Type
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldNames
parameter_list|,
name|Object
name|value
parameter_list|,
name|String
name|minimumShouldMatch
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Float
name|boostValue
init|=
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parseGroup
argument_list|(
name|type
operator|.
name|matchQueryType
argument_list|()
argument_list|,
name|fieldName
argument_list|,
name|boostValue
argument_list|,
name|value
argument_list|,
name|minimumShouldMatch
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queries
return|;
block|}
DECL|method|parseGroup
specifier|public
name|Query
name|parseGroup
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|field
parameter_list|,
name|Float
name|boostValue
parameter_list|,
name|Object
name|value
parameter_list|,
name|String
name|minimumShouldMatch
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parseAndApply
argument_list|(
name|type
argument_list|,
name|field
argument_list|,
name|value
argument_list|,
name|minimumShouldMatch
argument_list|,
name|boostValue
argument_list|)
return|;
block|}
DECL|method|combineGrouped
specifier|public
name|Query
name|combineGrouped
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|Query
argument_list|>
name|groupQuery
parameter_list|)
block|{
if|if
condition|(
name|groupQuery
operator|==
literal|null
operator|||
name|groupQuery
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|groupQuery
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|groupQuery
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
if|if
condition|(
name|groupDismax
condition|)
block|{
name|DisjunctionMaxQuery
name|disMaxQuery
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|tieBreaker
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|groupQuery
control|)
block|{
name|disMaxQuery
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|disMaxQuery
return|;
block|}
else|else
block|{
specifier|final
name|BooleanQuery
operator|.
name|Builder
name|booleanQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|groupQuery
control|)
block|{
name|booleanQuery
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|booleanQuery
operator|.
name|build
argument_list|()
return|;
block|}
block|}
DECL|method|blendTerm
specifier|public
name|Query
name|blendTerm
parameter_list|(
name|Term
name|term
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
return|return
name|MultiMatchQuery
operator|.
name|super
operator|.
name|blendTermQuery
argument_list|(
name|term
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
DECL|method|forceAnalyzeQueryString
specifier|public
name|boolean
name|forceAnalyzeQueryString
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|termQuery
specifier|public
name|Query
name|termQuery
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|fieldType
operator|.
name|termQuery
argument_list|(
name|value
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
DECL|class|CrossFieldsQueryBuilder
specifier|public
class|class
name|CrossFieldsQueryBuilder
extends|extends
name|QueryBuilder
block|{
DECL|field|blendedFields
specifier|private
name|FieldAndFieldType
index|[]
name|blendedFields
decl_stmt|;
DECL|method|CrossFieldsQueryBuilder
specifier|public
name|CrossFieldsQueryBuilder
parameter_list|(
name|float
name|tieBreaker
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|,
name|tieBreaker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildGroupedQueries
specifier|public
name|List
argument_list|<
name|Query
argument_list|>
name|buildGroupedQueries
parameter_list|(
name|MultiMatchQueryBuilder
operator|.
name|Type
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldNames
parameter_list|,
name|Object
name|value
parameter_list|,
name|String
name|minimumShouldMatch
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|Analyzer
argument_list|,
name|List
argument_list|<
name|FieldAndFieldType
argument_list|>
argument_list|>
name|groups
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|>
name|missing
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|entry
range|:
name|fieldNames
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|Analyzer
name|actualAnalyzer
init|=
name|getAnalyzer
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
name|name
operator|=
name|fieldType
operator|.
name|name
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|groups
operator|.
name|containsKey
argument_list|(
name|actualAnalyzer
argument_list|)
condition|)
block|{
name|groups
operator|.
name|put
argument_list|(
name|actualAnalyzer
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Float
name|boost
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|boost
operator|=
name|boost
operator|==
literal|null
condition|?
name|Float
operator|.
name|valueOf
argument_list|(
literal|1.0f
argument_list|)
else|:
name|boost
expr_stmt|;
name|groups
operator|.
name|get
argument_list|(
name|actualAnalyzer
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|FieldAndFieldType
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|boost
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|missing
operator|.
name|add
argument_list|(
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|tuple
range|:
name|missing
control|)
block|{
name|Query
name|q
init|=
name|parseGroup
argument_list|(
name|type
operator|.
name|matchQueryType
argument_list|()
argument_list|,
name|tuple
operator|.
name|v1
argument_list|()
argument_list|,
name|tuple
operator|.
name|v2
argument_list|()
argument_list|,
name|value
argument_list|,
name|minimumShouldMatch
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|List
argument_list|<
name|FieldAndFieldType
argument_list|>
name|group
range|:
name|groups
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|group
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|blendedFields
operator|=
operator|new
name|FieldAndFieldType
index|[
name|group
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FieldAndFieldType
name|fieldAndFieldType
range|:
name|group
control|)
block|{
name|blendedFields
index|[
name|i
operator|++
index|]
operator|=
name|fieldAndFieldType
expr_stmt|;
block|}
block|}
else|else
block|{
name|blendedFields
operator|=
literal|null
expr_stmt|;
block|}
comment|/*                  * We have to pick some field to pass through the superclass so                  * we just pick the first field. It shouldn't matter because                  * fields are already grouped by their analyzers/types.                  */
name|String
name|representativeField
init|=
name|group
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|field
decl_stmt|;
name|Query
name|q
init|=
name|parseGroup
argument_list|(
name|type
operator|.
name|matchQueryType
argument_list|()
argument_list|,
name|representativeField
argument_list|,
literal|1f
argument_list|,
name|value
argument_list|,
name|minimumShouldMatch
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queries
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|queries
return|;
block|}
comment|/**          * Pick the field for parsing. If any of the fields in the group do          * *not* useTermQueryWithQueryString then we return that one to force          * analysis. If some of the fields would useTermQueryWithQueryString          * then we assume that that parsing field's parser is good enough for          * them and return it. Otherwise we just return the first field. You          * should only get mixed groups like this when you force a certain          * analyzer on a query and use string and integer fields because of the          * way that grouping is done. That means that the use *asked* for the          * integer fields to be searched using a string analyzer so this is          * technically doing exactly what they asked for even if it is a bit          * funky.          */
DECL|method|fieldForParsing
specifier|private
name|String
name|fieldForParsing
parameter_list|(
name|List
argument_list|<
name|FieldAndFieldType
argument_list|>
name|group
parameter_list|)
block|{
for|for
control|(
name|FieldAndFieldType
name|field
range|:
name|group
control|)
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
operator|.
name|useTermQueryWithQueryString
argument_list|()
condition|)
block|{
return|return
name|field
operator|.
name|field
return|;
block|}
block|}
return|return
name|group
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|forceAnalyzeQueryString
specifier|public
name|boolean
name|forceAnalyzeQueryString
parameter_list|()
block|{
return|return
name|blendedFields
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|blendTerm
specifier|public
name|Query
name|blendTerm
parameter_list|(
name|Term
name|term
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
if|if
condition|(
name|blendedFields
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|blendTerm
argument_list|(
name|term
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
specifier|final
name|Term
index|[]
name|terms
init|=
operator|new
name|Term
index|[
name|blendedFields
operator|.
name|length
index|]
decl_stmt|;
name|float
index|[]
name|blendedBoost
init|=
operator|new
name|float
index|[
name|blendedFields
operator|.
name|length
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
name|blendedFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
index|[
name|i
index|]
operator|=
name|blendedFields
index|[
name|i
index|]
operator|.
name|newTerm
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|blendedBoost
index|[
name|i
index|]
operator|=
name|blendedFields
index|[
name|i
index|]
operator|.
name|boost
expr_stmt|;
block|}
if|if
condition|(
name|commonTermsCutoff
operator|!=
literal|null
condition|)
block|{
return|return
name|BlendedTermQuery
operator|.
name|commonTermsBlendedQuery
argument_list|(
name|terms
argument_list|,
name|blendedBoost
argument_list|,
literal|false
argument_list|,
name|commonTermsCutoff
argument_list|)
return|;
block|}
if|if
condition|(
name|tieBreaker
operator|==
literal|1.0f
condition|)
block|{
return|return
name|BlendedTermQuery
operator|.
name|booleanBlendedQuery
argument_list|(
name|terms
argument_list|,
name|blendedBoost
argument_list|,
literal|false
argument_list|)
return|;
block|}
return|return
name|BlendedTermQuery
operator|.
name|dismaxBlendedQuery
argument_list|(
name|terms
argument_list|,
name|blendedBoost
argument_list|,
name|tieBreaker
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termQuery
specifier|public
name|Query
name|termQuery
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|blendTerm
argument_list|(
name|fieldType
operator|.
name|createTerm
argument_list|(
name|value
argument_list|)
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|blendTermQuery
specifier|protected
name|Query
name|blendTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|blendTermQuery
argument_list|(
name|term
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
return|return
name|queryBuilder
operator|.
name|blendTerm
argument_list|(
name|term
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
DECL|class|FieldAndFieldType
specifier|private
specifier|static
specifier|final
class|class
name|FieldAndFieldType
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|fieldType
specifier|final
name|MappedFieldType
name|fieldType
decl_stmt|;
DECL|field|boost
specifier|final
name|float
name|boost
decl_stmt|;
DECL|method|FieldAndFieldType
specifier|private
name|FieldAndFieldType
parameter_list|(
name|String
name|field
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
DECL|method|newTerm
specifier|public
name|Term
name|newTerm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
try|try
block|{
specifier|final
name|BytesRef
name|bytesRef
init|=
name|fieldType
operator|.
name|indexedValueForSearch
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|bytesRef
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// we can't parse it just use the incoming value -- it will
comment|// just have a DF of 0 at the end of the day and will be ignored
block|}
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|termQuery
specifier|protected
name|Query
name|termQuery
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|queryBuilder
operator|==
literal|null
condition|)
block|{
comment|// Can be null when the MultiMatchQuery collapses into a MatchQuery
return|return
name|super
operator|.
name|termQuery
argument_list|(
name|fieldType
argument_list|,
name|value
argument_list|)
return|;
block|}
return|return
name|queryBuilder
operator|.
name|termQuery
argument_list|(
name|fieldType
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

