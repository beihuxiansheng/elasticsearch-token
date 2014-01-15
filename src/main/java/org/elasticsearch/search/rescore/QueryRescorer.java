begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.rescore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|rescore
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
name|index
operator|.
name|AtomicReaderContext
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
name|search
operator|.
name|*
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
name|Bits
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
name|IntroSorter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
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
name|ParsedQuery
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
name|ContextIndexSearcher
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
name|Arrays
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

begin_class
DECL|class|QueryRescorer
specifier|public
specifier|final
class|class
name|QueryRescorer
implements|implements
name|Rescorer
block|{
DECL|enum|ScoreMode
specifier|private
specifier|static
enum|enum
name|ScoreMode
block|{
DECL|enum constant|Avg
name|Avg
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|float
name|primary
parameter_list|,
name|float
name|secondary
parameter_list|)
block|{
return|return
operator|(
name|primary
operator|+
name|secondary
operator|)
operator|/
literal|2
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"avg"
return|;
block|}
block|}
block|,
DECL|enum constant|Max
name|Max
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|float
name|primary
parameter_list|,
name|float
name|secondary
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|primary
argument_list|,
name|secondary
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"max"
return|;
block|}
block|}
block|,
DECL|enum constant|Min
name|Min
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|float
name|primary
parameter_list|,
name|float
name|secondary
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|primary
argument_list|,
name|secondary
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"min"
return|;
block|}
block|}
block|,
DECL|enum constant|Total
name|Total
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|float
name|primary
parameter_list|,
name|float
name|secondary
parameter_list|)
block|{
return|return
name|primary
operator|+
name|secondary
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"sum"
return|;
block|}
block|}
block|,
DECL|enum constant|Multiply
name|Multiply
block|{
annotation|@
name|Override
specifier|public
name|float
name|combine
parameter_list|(
name|float
name|primary
parameter_list|,
name|float
name|secondary
parameter_list|)
block|{
return|return
name|primary
operator|*
name|secondary
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"product"
return|;
block|}
block|}
block|;
DECL|method|combine
specifier|public
specifier|abstract
name|float
name|combine
parameter_list|(
name|float
name|primary
parameter_list|,
name|float
name|secondary
parameter_list|)
function_decl|;
block|}
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|Rescorer
name|INSTANCE
init|=
operator|new
name|QueryRescorer
argument_list|()
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"query"
decl_stmt|;
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|rescore
specifier|public
name|void
name|rescore
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|RescoreSearchContext
name|rescoreContext
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|rescoreContext
operator|!=
literal|null
assert|;
if|if
condition|(
name|topDocs
operator|==
literal|null
operator|||
name|topDocs
operator|.
name|totalHits
operator|==
literal|0
operator|||
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|QueryRescoreContext
name|rescore
init|=
operator|(
name|QueryRescoreContext
operator|)
name|rescoreContext
decl_stmt|;
name|ContextIndexSearcher
name|searcher
init|=
name|context
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|TopDocsFilter
name|filter
init|=
operator|new
name|TopDocsFilter
argument_list|(
name|topDocs
argument_list|,
name|rescoreContext
operator|.
name|window
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|rescored
init|=
name|searcher
operator|.
name|search
argument_list|(
name|rescore
operator|.
name|query
argument_list|()
argument_list|,
name|filter
argument_list|,
name|rescoreContext
operator|.
name|window
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|(
name|merge
argument_list|(
name|topDocs
argument_list|,
name|rescored
argument_list|,
name|rescore
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|topLevelDocId
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|RescoreSearchContext
name|rescoreContext
parameter_list|,
name|Explanation
name|sourceExplanation
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryRescoreContext
name|rescore
init|=
operator|(
operator|(
name|QueryRescoreContext
operator|)
name|rescoreContext
operator|)
decl_stmt|;
name|ContextIndexSearcher
name|searcher
init|=
name|context
operator|.
name|searcher
argument_list|()
decl_stmt|;
if|if
condition|(
name|sourceExplanation
operator|==
literal|null
condition|)
block|{
comment|// this should not happen but just in case
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|false
argument_list|,
literal|0.0f
argument_list|,
literal|"nothing matched"
argument_list|)
return|;
block|}
name|Explanation
name|rescoreExplain
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|rescore
operator|.
name|query
argument_list|()
argument_list|,
name|topLevelDocId
argument_list|)
decl_stmt|;
name|float
name|primaryWeight
init|=
name|rescore
operator|.
name|queryWeight
argument_list|()
decl_stmt|;
name|ComplexExplanation
name|prim
init|=
operator|new
name|ComplexExplanation
argument_list|(
name|sourceExplanation
operator|.
name|isMatch
argument_list|()
argument_list|,
name|sourceExplanation
operator|.
name|getValue
argument_list|()
operator|*
name|primaryWeight
argument_list|,
literal|"product of:"
argument_list|)
decl_stmt|;
name|prim
operator|.
name|addDetail
argument_list|(
name|sourceExplanation
argument_list|)
expr_stmt|;
name|prim
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|primaryWeight
argument_list|,
literal|"primaryWeight"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rescoreExplain
operator|!=
literal|null
operator|&&
name|rescoreExplain
operator|.
name|isMatch
argument_list|()
condition|)
block|{
name|float
name|secondaryWeight
init|=
name|rescore
operator|.
name|rescoreQueryWeight
argument_list|()
decl_stmt|;
name|ComplexExplanation
name|sec
init|=
operator|new
name|ComplexExplanation
argument_list|(
name|rescoreExplain
operator|.
name|isMatch
argument_list|()
argument_list|,
name|rescoreExplain
operator|.
name|getValue
argument_list|()
operator|*
name|secondaryWeight
argument_list|,
literal|"product of:"
argument_list|)
decl_stmt|;
name|sec
operator|.
name|addDetail
argument_list|(
name|rescoreExplain
argument_list|)
expr_stmt|;
name|sec
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|secondaryWeight
argument_list|,
literal|"secondaryWeight"
argument_list|)
argument_list|)
expr_stmt|;
name|ScoreMode
name|scoreMode
init|=
name|rescore
operator|.
name|scoreMode
argument_list|()
decl_stmt|;
name|ComplexExplanation
name|calcExpl
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|calcExpl
operator|.
name|setDescription
argument_list|(
name|scoreMode
operator|+
literal|" of:"
argument_list|)
expr_stmt|;
name|calcExpl
operator|.
name|addDetail
argument_list|(
name|prim
argument_list|)
expr_stmt|;
name|calcExpl
operator|.
name|setMatch
argument_list|(
name|prim
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
name|calcExpl
operator|.
name|addDetail
argument_list|(
name|sec
argument_list|)
expr_stmt|;
name|calcExpl
operator|.
name|setValue
argument_list|(
name|scoreMode
operator|.
name|combine
argument_list|(
name|prim
operator|.
name|getValue
argument_list|()
argument_list|,
name|sec
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|calcExpl
return|;
block|}
else|else
block|{
return|return
name|prim
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|RescoreSearchContext
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|token
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|QueryRescoreContext
name|rescoreContext
init|=
operator|new
name|QueryRescoreContext
argument_list|(
name|this
argument_list|)
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
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"rescore_query"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|ParsedQuery
name|parsedQuery
init|=
name|context
operator|.
name|queryParserService
argument_list|()
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|rescoreContext
operator|.
name|setParsedQuery
argument_list|(
name|parsedQuery
argument_list|)
expr_stmt|;
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
literal|"query_weight"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|rescoreContext
operator|.
name|setQueryWeight
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"rescore_query_weight"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|rescoreContext
operator|.
name|setRescoreQueryWeight
argument_list|(
name|parser
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"score_mode"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
name|sScoreMode
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"avg"
operator|.
name|equals
argument_list|(
name|sScoreMode
argument_list|)
condition|)
block|{
name|rescoreContext
operator|.
name|setScoreMode
argument_list|(
name|ScoreMode
operator|.
name|Avg
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"max"
operator|.
name|equals
argument_list|(
name|sScoreMode
argument_list|)
condition|)
block|{
name|rescoreContext
operator|.
name|setScoreMode
argument_list|(
name|ScoreMode
operator|.
name|Max
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"min"
operator|.
name|equals
argument_list|(
name|sScoreMode
argument_list|)
condition|)
block|{
name|rescoreContext
operator|.
name|setScoreMode
argument_list|(
name|ScoreMode
operator|.
name|Min
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"total"
operator|.
name|equals
argument_list|(
name|sScoreMode
argument_list|)
condition|)
block|{
name|rescoreContext
operator|.
name|setScoreMode
argument_list|(
name|ScoreMode
operator|.
name|Total
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"multiply"
operator|.
name|equals
argument_list|(
name|sScoreMode
argument_list|)
condition|)
block|{
name|rescoreContext
operator|.
name|setScoreMode
argument_list|(
name|ScoreMode
operator|.
name|Multiply
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"[rescore] illegal score_mode ["
operator|+
name|sScoreMode
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"rescore doesn't support ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|rescoreContext
return|;
block|}
DECL|class|QueryRescoreContext
specifier|public
specifier|static
class|class
name|QueryRescoreContext
extends|extends
name|RescoreSearchContext
block|{
DECL|method|QueryRescoreContext
specifier|public
name|QueryRescoreContext
parameter_list|(
name|QueryRescorer
name|rescorer
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
literal|10
argument_list|,
name|rescorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|ScoreMode
operator|.
name|Total
expr_stmt|;
block|}
DECL|field|parsedQuery
specifier|private
name|ParsedQuery
name|parsedQuery
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
init|=
literal|1.0f
decl_stmt|;
DECL|field|rescoreQueryWeight
specifier|private
name|float
name|rescoreQueryWeight
init|=
literal|1.0f
decl_stmt|;
DECL|field|scoreMode
specifier|private
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|method|setParsedQuery
specifier|public
name|void
name|setParsedQuery
parameter_list|(
name|ParsedQuery
name|parsedQuery
parameter_list|)
block|{
name|this
operator|.
name|parsedQuery
operator|=
name|parsedQuery
expr_stmt|;
block|}
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|parsedQuery
operator|.
name|query
argument_list|()
return|;
block|}
DECL|method|queryWeight
specifier|public
name|float
name|queryWeight
parameter_list|()
block|{
return|return
name|queryWeight
return|;
block|}
DECL|method|rescoreQueryWeight
specifier|public
name|float
name|rescoreQueryWeight
parameter_list|()
block|{
return|return
name|rescoreQueryWeight
return|;
block|}
DECL|method|scoreMode
specifier|public
name|ScoreMode
name|scoreMode
parameter_list|()
block|{
return|return
name|scoreMode
return|;
block|}
DECL|method|setRescoreQueryWeight
specifier|public
name|void
name|setRescoreQueryWeight
parameter_list|(
name|float
name|rescoreQueryWeight
parameter_list|)
block|{
name|this
operator|.
name|rescoreQueryWeight
operator|=
name|rescoreQueryWeight
expr_stmt|;
block|}
DECL|method|setQueryWeight
specifier|public
name|void
name|setQueryWeight
parameter_list|(
name|float
name|queryWeight
parameter_list|)
block|{
name|this
operator|.
name|queryWeight
operator|=
name|queryWeight
expr_stmt|;
block|}
DECL|method|setScoreMode
specifier|public
name|void
name|setScoreMode
parameter_list|(
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
block|}
block|}
DECL|method|merge
specifier|private
name|TopDocs
name|merge
parameter_list|(
name|TopDocs
name|primary
parameter_list|,
name|TopDocs
name|secondary
parameter_list|,
name|QueryRescoreContext
name|context
parameter_list|)
block|{
name|DocIdSorter
name|sorter
init|=
operator|new
name|DocIdSorter
argument_list|()
decl_stmt|;
name|sorter
operator|.
name|array
operator|=
name|primary
operator|.
name|scoreDocs
expr_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|sorter
operator|.
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|primaryDocs
init|=
name|sorter
operator|.
name|array
decl_stmt|;
name|sorter
operator|.
name|array
operator|=
name|secondary
operator|.
name|scoreDocs
expr_stmt|;
name|sorter
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|sorter
operator|.
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|secondaryDocs
init|=
name|sorter
operator|.
name|array
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
name|float
name|primaryWeight
init|=
name|context
operator|.
name|queryWeight
argument_list|()
decl_stmt|;
name|float
name|secondaryWeight
init|=
name|context
operator|.
name|rescoreQueryWeight
argument_list|()
decl_stmt|;
name|ScoreMode
name|scoreMode
init|=
name|context
operator|.
name|scoreMode
argument_list|()
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
name|primaryDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|<
name|secondaryDocs
operator|.
name|length
operator|&&
name|primaryDocs
index|[
name|i
index|]
operator|.
name|doc
operator|==
name|secondaryDocs
index|[
name|j
index|]
operator|.
name|doc
condition|)
block|{
name|primaryDocs
index|[
name|i
index|]
operator|.
name|score
operator|=
name|scoreMode
operator|.
name|combine
argument_list|(
name|primaryDocs
index|[
name|i
index|]
operator|.
name|score
operator|*
name|primaryWeight
argument_list|,
name|secondaryDocs
index|[
name|j
operator|++
index|]
operator|.
name|score
operator|*
name|secondaryWeight
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|primaryDocs
index|[
name|i
index|]
operator|.
name|score
operator|*=
name|primaryWeight
expr_stmt|;
block|}
block|}
name|ScoreSorter
name|scoreSorter
init|=
operator|new
name|ScoreSorter
argument_list|()
decl_stmt|;
name|scoreSorter
operator|.
name|array
operator|=
name|primaryDocs
expr_stmt|;
name|scoreSorter
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|primaryDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|primary
operator|.
name|setMaxScore
argument_list|(
name|primaryDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
return|return
name|primary
return|;
block|}
DECL|class|DocIdSorter
specifier|private
specifier|static
specifier|final
class|class
name|DocIdSorter
extends|extends
name|IntroSorter
block|{
DECL|field|array
specifier|private
name|ScoreDoc
index|[]
name|array
decl_stmt|;
DECL|field|pivot
specifier|private
name|ScoreDoc
name|pivot
decl_stmt|;
annotation|@
name|Override
DECL|method|swap
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|ScoreDoc
name|scoreDoc
init|=
name|array
index|[
name|i
index|]
decl_stmt|;
name|array
index|[
name|i
index|]
operator|=
name|array
index|[
name|j
index|]
expr_stmt|;
name|array
index|[
name|j
index|]
operator|=
name|scoreDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|compareDocId
argument_list|(
name|array
index|[
name|i
index|]
argument_list|,
name|array
index|[
name|j
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setPivot
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|pivot
operator|=
name|array
index|[
name|i
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|comparePivot
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
return|return
name|compareDocId
argument_list|(
name|pivot
argument_list|,
name|array
index|[
name|j
index|]
argument_list|)
return|;
block|}
block|}
DECL|method|compareDocId
specifier|private
specifier|static
specifier|final
name|int
name|compareDocId
parameter_list|(
name|ScoreDoc
name|left
parameter_list|,
name|ScoreDoc
name|right
parameter_list|)
block|{
if|if
condition|(
name|left
operator|.
name|doc
operator|<
name|right
operator|.
name|doc
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|left
operator|.
name|doc
operator|==
name|right
operator|.
name|doc
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|class|ScoreSorter
specifier|private
specifier|static
specifier|final
class|class
name|ScoreSorter
extends|extends
name|IntroSorter
block|{
DECL|field|array
specifier|private
name|ScoreDoc
index|[]
name|array
decl_stmt|;
DECL|field|pivot
specifier|private
name|ScoreDoc
name|pivot
decl_stmt|;
annotation|@
name|Override
DECL|method|swap
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|ScoreDoc
name|scoreDoc
init|=
name|array
index|[
name|i
index|]
decl_stmt|;
name|array
index|[
name|i
index|]
operator|=
name|array
index|[
name|j
index|]
expr_stmt|;
name|array
index|[
name|j
index|]
operator|=
name|scoreDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Float
operator|.
name|compare
argument_list|(
name|array
index|[
name|j
index|]
operator|.
name|score
argument_list|,
name|array
index|[
name|i
index|]
operator|.
name|score
argument_list|)
decl_stmt|;
return|return
name|cmp
operator|==
literal|0
condition|?
name|compareDocId
argument_list|(
name|array
index|[
name|i
index|]
argument_list|,
name|array
index|[
name|j
index|]
argument_list|)
else|:
name|cmp
return|;
block|}
annotation|@
name|Override
DECL|method|setPivot
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|pivot
operator|=
name|array
index|[
name|i
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|comparePivot
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Float
operator|.
name|compare
argument_list|(
name|array
index|[
name|j
index|]
operator|.
name|score
argument_list|,
name|pivot
operator|.
name|score
argument_list|)
decl_stmt|;
return|return
name|cmp
operator|==
literal|0
condition|?
name|compareDocId
argument_list|(
name|pivot
argument_list|,
name|array
index|[
name|j
index|]
argument_list|)
else|:
name|cmp
return|;
block|}
block|}
DECL|class|TopDocsFilter
specifier|private
specifier|static
specifier|final
class|class
name|TopDocsFilter
extends|extends
name|Filter
block|{
DECL|field|docIds
specifier|private
specifier|final
name|int
index|[]
name|docIds
decl_stmt|;
DECL|method|TopDocsFilter
specifier|public
name|TopDocsFilter
parameter_list|(
name|TopDocs
name|topDocs
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|max
operator|=
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|docIds
operator|=
operator|new
name|int
index|[
name|max
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
name|docIds
index|[
name|i
index|]
operator|=
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|docIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docBase
init|=
name|context
operator|.
name|docBase
decl_stmt|;
name|int
name|limit
init|=
name|docBase
operator|+
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|docIds
argument_list|,
name|docBase
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
name|offset
operator|=
operator|(
operator|-
name|offset
operator|)
operator|-
literal|1
expr_stmt|;
block|}
name|int
name|end
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|docIds
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|<
literal|0
condition|)
block|{
name|end
operator|=
operator|(
operator|-
name|end
operator|)
operator|-
literal|1
expr_stmt|;
block|}
specifier|final
name|int
name|start
init|=
name|offset
decl_stmt|;
specifier|final
name|int
name|stop
init|=
name|end
decl_stmt|;
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
specifier|private
name|int
name|current
init|=
name|start
decl_stmt|;
specifier|private
name|int
name|docId
init|=
name|NO_MORE_DOCS
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|current
operator|<
name|stop
condition|)
block|{
return|return
name|docId
operator|=
name|docIds
index|[
name|current
operator|++
index|]
operator|-
name|docBase
return|;
block|}
return|return
name|docId
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docId
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|current
operator|=
name|stop
expr_stmt|;
return|return
name|docId
operator|=
name|NO_MORE_DOCS
return|;
block|}
while|while
condition|(
name|nextDoc
argument_list|()
operator|<
name|target
condition|)
block|{                             }
return|return
name|docId
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|docIds
operator|.
name|length
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|RescoreSearchContext
name|rescoreContext
parameter_list|,
name|Set
argument_list|<
name|Term
argument_list|>
name|termsSet
parameter_list|)
block|{
operator|(
operator|(
name|QueryRescoreContext
operator|)
name|rescoreContext
operator|)
operator|.
name|query
argument_list|()
operator|.
name|extractTerms
argument_list|(
name|termsSet
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

