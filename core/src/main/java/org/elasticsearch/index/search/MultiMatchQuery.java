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
name|BooleanClause
operator|.
name|Occur
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
name|MatchNoDocsQuery
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
name|TermQuery
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
name|ElasticsearchParseException
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
name|Arrays
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|query
operator|=
name|Queries
operator|.
name|maybeApplyMinimumShouldMatch
argument_list|(
name|query
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
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
name|Query
name|result
decl_stmt|;
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
name|result
operator|=
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
expr_stmt|;
block|}
else|else
block|{
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
name|result
operator|=
name|queryBuilder
operator|.
name|combineGrouped
argument_list|(
name|queries
argument_list|)
expr_stmt|;
block|}
assert|assert
name|result
operator|!=
literal|null
assert|;
return|return
name|result
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
specifier|private
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
operator|new
name|MatchNoDocsQuery
argument_list|(
literal|"[multi_match] list of group queries was empty"
argument_list|)
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
name|Query
name|query
range|:
name|groupQuery
control|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|queries
argument_list|,
name|tieBreaker
argument_list|)
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
DECL|method|blendTerms
specifier|public
name|Query
name|blendTerms
parameter_list|(
name|Term
index|[]
name|terms
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
name|blendTermsQuery
argument_list|(
name|terms
argument_list|,
name|fieldType
argument_list|)
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
name|MultiMatchQuery
operator|.
name|this
operator|.
name|termQuery
argument_list|(
name|fieldType
argument_list|,
name|value
argument_list|,
name|lenient
argument_list|)
return|;
block|}
block|}
DECL|class|CrossFieldsQueryBuilder
specifier|final
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
name|fieldType
operator|.
name|name
argument_list|()
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
annotation|@
name|Override
DECL|method|blendTerms
specifier|public
name|Query
name|blendTerms
parameter_list|(
name|Term
index|[]
name|terms
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
operator|||
name|blendedFields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|super
operator|.
name|blendTerms
argument_list|(
name|terms
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
name|BytesRef
index|[]
name|values
init|=
operator|new
name|BytesRef
index|[
name|terms
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|terms
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
return|return
name|MultiMatchQuery
operator|.
name|blendTerms
argument_list|(
name|context
argument_list|,
name|values
argument_list|,
name|commonTermsCutoff
argument_list|,
name|tieBreaker
argument_list|,
name|blendedFields
argument_list|)
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
return|return
name|MultiMatchQuery
operator|.
name|blendTerm
argument_list|(
name|context
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|commonTermsCutoff
argument_list|,
name|tieBreaker
argument_list|,
name|blendedFields
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
comment|/*              * Use the string value of the term because we're reusing the              * portion of the query is usually after the analyzer has run on              * each term. We just skip that analyzer phase.              */
return|return
name|blendTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
block|}
DECL|method|blendTerm
specifier|static
name|Query
name|blendTerm
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
name|BytesRef
name|value
parameter_list|,
name|Float
name|commonTermsCutoff
parameter_list|,
name|float
name|tieBreaker
parameter_list|,
name|FieldAndFieldType
modifier|...
name|blendedFields
parameter_list|)
block|{
return|return
name|blendTerms
argument_list|(
name|context
argument_list|,
operator|new
name|BytesRef
index|[]
block|{
name|value
block|}
argument_list|,
name|commonTermsCutoff
argument_list|,
name|tieBreaker
argument_list|,
name|blendedFields
argument_list|)
return|;
block|}
DECL|method|blendTerms
specifier|static
name|Query
name|blendTerms
parameter_list|(
name|QueryShardContext
name|context
parameter_list|,
name|BytesRef
index|[]
name|values
parameter_list|,
name|Float
name|commonTermsCutoff
parameter_list|,
name|float
name|tieBreaker
parameter_list|,
name|FieldAndFieldType
modifier|...
name|blendedFields
parameter_list|)
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
operator|*
name|values
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
operator|*
name|values
operator|.
name|length
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FieldAndFieldType
name|ft
range|:
name|blendedFields
control|)
block|{
for|for
control|(
name|BytesRef
name|term
range|:
name|values
control|)
block|{
name|Query
name|query
decl_stmt|;
try|try
block|{
name|query
operator|=
name|ft
operator|.
name|fieldType
operator|.
name|termQuery
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// the query expects a certain class of values such as numbers
comment|// of ip addresses and the value can't be parsed, so ignore this
comment|// field
continue|continue;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|parseException
parameter_list|)
block|{
comment|// date fields throw an ElasticsearchParseException with the
comment|// underlying IAE as the cause, ignore this field if that is
comment|// the case
if|if
condition|(
name|parseException
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
continue|continue;
block|}
throw|throw
name|parseException
throw|;
block|}
name|float
name|boost
init|=
name|ft
operator|.
name|boost
decl_stmt|;
while|while
condition|(
name|query
operator|instanceof
name|BoostQuery
condition|)
block|{
name|BoostQuery
name|bq
init|=
operator|(
name|BoostQuery
operator|)
name|query
decl_stmt|;
name|query
operator|=
name|bq
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|boost
operator|*=
name|bq
operator|.
name|getBoost
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|.
name|getClass
argument_list|()
operator|==
name|TermQuery
operator|.
name|class
condition|)
block|{
name|terms
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|TermQuery
operator|)
name|query
operator|)
operator|.
name|getTerm
argument_list|()
expr_stmt|;
name|blendedBoost
index|[
name|i
index|]
operator|=
name|boost
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|boost
operator|!=
literal|1f
condition|)
block|{
name|query
operator|=
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
name|queries
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|terms
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|terms
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|blendedBoost
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|blendedBoost
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|commonTermsCutoff
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|BlendedTermQuery
operator|.
name|commonTermsBlendedQuery
argument_list|(
name|terms
argument_list|,
name|blendedBoost
argument_list|,
name|commonTermsCutoff
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tieBreaker
operator|==
literal|1.0f
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|BlendedTermQuery
operator|.
name|booleanBlendedQuery
argument_list|(
name|terms
argument_list|,
name|blendedBoost
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queries
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|queries
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|queries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
comment|// best effort: add clauses that are not term queries so that they have an opportunity to match
comment|// however their score contribution will be different
comment|// TODO: can we improve this?
name|BooleanQuery
operator|.
name|Builder
name|bq
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
name|queries
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|bq
operator|.
name|build
argument_list|()
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
annotation|@
name|Override
DECL|method|blendTermsQuery
specifier|protected
name|Query
name|blendTermsQuery
parameter_list|(
name|Term
index|[]
name|terms
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
name|blendTermsQuery
argument_list|(
name|terms
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
return|return
name|queryBuilder
operator|.
name|blendTerms
argument_list|(
name|terms
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
DECL|class|FieldAndFieldType
specifier|static
specifier|final
class|class
name|FieldAndFieldType
block|{
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
name|FieldAndFieldType
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|fieldType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

