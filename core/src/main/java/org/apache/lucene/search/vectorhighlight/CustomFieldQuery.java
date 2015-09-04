begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|IndexReader
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
name|FilteredQuery
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
name|MultiPhraseQuery
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
name|PhraseQuery
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
name|QueryWrapperFilter
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|MultiPhrasePrefixQuery
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
name|function
operator|.
name|FiltersFunctionScoreQuery
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
name|function
operator|.
name|FunctionScoreQuery
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
name|Collection
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

begin_comment
comment|// LUCENE MONITOR
end_comment

begin_comment
comment|// TODO: remove me!
end_comment

begin_class
DECL|class|CustomFieldQuery
specifier|public
class|class
name|CustomFieldQuery
extends|extends
name|FieldQuery
block|{
DECL|field|highlightFilters
specifier|public
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
name|highlightFilters
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|CustomFieldQuery
specifier|public
name|CustomFieldQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|FastVectorHighlighter
name|highlighter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|query
argument_list|,
name|reader
argument_list|,
name|highlighter
operator|.
name|isPhraseHighlight
argument_list|()
argument_list|,
name|highlighter
operator|.
name|isFieldMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CustomFieldQuery
specifier|public
name|CustomFieldQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|phraseHighlight
parameter_list|,
name|boolean
name|fieldMatch
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|reader
argument_list|,
name|phraseHighlight
argument_list|,
name|fieldMatch
argument_list|)
expr_stmt|;
name|highlightFilters
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flatten
name|void
name|flatten
parameter_list|(
name|Query
name|sourceQuery
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
name|flatQueries
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sourceQuery
operator|instanceof
name|SpanTermQuery
condition|)
block|{
name|super
operator|.
name|flatten
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|(
operator|(
name|SpanTermQuery
operator|)
name|sourceQuery
operator|)
operator|.
name|getTerm
argument_list|()
argument_list|)
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
name|flatten
argument_list|(
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|sourceQuery
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|FunctionScoreQuery
condition|)
block|{
name|flatten
argument_list|(
operator|(
operator|(
name|FunctionScoreQuery
operator|)
name|sourceQuery
operator|)
operator|.
name|getSubQuery
argument_list|()
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|FilteredQuery
condition|)
block|{
name|flatten
argument_list|(
operator|(
operator|(
name|FilteredQuery
operator|)
name|sourceQuery
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
name|flatten
argument_list|(
operator|(
operator|(
name|FilteredQuery
operator|)
name|sourceQuery
operator|)
operator|.
name|getFilter
argument_list|()
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|MultiPhrasePrefixQuery
condition|)
block|{
name|flatten
argument_list|(
name|sourceQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|FiltersFunctionScoreQuery
condition|)
block|{
name|flatten
argument_list|(
operator|(
operator|(
name|FiltersFunctionScoreQuery
operator|)
name|sourceQuery
operator|)
operator|.
name|getSubQuery
argument_list|()
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|MultiPhraseQuery
condition|)
block|{
name|MultiPhraseQuery
name|q
init|=
operator|(
operator|(
name|MultiPhraseQuery
operator|)
name|sourceQuery
operator|)
decl_stmt|;
name|convertMultiPhraseQuery
argument_list|(
literal|0
argument_list|,
operator|new
name|int
index|[
name|q
operator|.
name|getTermArrays
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|,
name|q
argument_list|,
name|q
operator|.
name|getTermArrays
argument_list|()
argument_list|,
name|q
operator|.
name|getPositions
argument_list|()
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|BlendedTermQuery
condition|)
block|{
specifier|final
name|BlendedTermQuery
name|blendedTermQuery
init|=
operator|(
name|BlendedTermQuery
operator|)
name|sourceQuery
decl_stmt|;
name|flatten
argument_list|(
name|blendedTermQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|flatten
argument_list|(
name|sourceQuery
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|convertMultiPhraseQuery
specifier|private
name|void
name|convertMultiPhraseQuery
parameter_list|(
name|int
name|currentPos
parameter_list|,
name|int
index|[]
name|termsIdx
parameter_list|,
name|MultiPhraseQuery
name|orig
parameter_list|,
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|terms
parameter_list|,
name|int
index|[]
name|pos
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
name|flatQueries
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentPos
operator|==
literal|0
condition|)
block|{
comment|// if we have more than 16 terms
name|int
name|numTerms
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Term
index|[]
name|currentPosTerm
range|:
name|terms
control|)
block|{
name|numTerms
operator|+=
name|currentPosTerm
operator|.
name|length
expr_stmt|;
block|}
if|if
condition|(
name|numTerms
operator|>
literal|16
condition|)
block|{
for|for
control|(
name|Term
index|[]
name|currentPosTerm
range|:
name|terms
control|)
block|{
for|for
control|(
name|Term
name|term
range|:
name|currentPosTerm
control|)
block|{
name|super
operator|.
name|flatten
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
block|}
return|return;
block|}
block|}
comment|/*          * we walk all possible ways and for each path down the MPQ we create a PhraseQuery this is what FieldQuery supports.          * It seems expensive but most queries will pretty small.          */
if|if
condition|(
name|currentPos
operator|==
name|terms
operator|.
name|size
argument_list|()
condition|)
block|{
name|PhraseQuery
operator|.
name|Builder
name|queryBuilder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|queryBuilder
operator|.
name|setSlop
argument_list|(
name|orig
operator|.
name|getSlop
argument_list|()
argument_list|)
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
name|termsIdx
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|queryBuilder
operator|.
name|add
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
index|[
name|termsIdx
index|[
name|i
index|]
index|]
argument_list|,
name|pos
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|PhraseQuery
name|query
init|=
name|queryBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|orig
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|flatten
argument_list|(
name|query
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Term
index|[]
name|t
init|=
name|terms
operator|.
name|get
argument_list|(
name|currentPos
argument_list|)
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
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|termsIdx
index|[
name|currentPos
index|]
operator|=
name|i
expr_stmt|;
name|convertMultiPhraseQuery
argument_list|(
name|currentPos
operator|+
literal|1
argument_list|,
name|termsIdx
argument_list|,
name|orig
argument_list|,
name|terms
argument_list|,
name|pos
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|flatten
name|void
name|flatten
parameter_list|(
name|Filter
name|sourceFilter
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
name|flatQueries
parameter_list|)
throws|throws
name|IOException
block|{
name|Boolean
name|highlight
init|=
name|highlightFilters
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|highlight
operator|==
literal|null
operator|||
name|highlight
operator|.
name|equals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|sourceFilter
operator|instanceof
name|QueryWrapperFilter
condition|)
block|{
name|flatten
argument_list|(
operator|(
operator|(
name|QueryWrapperFilter
operator|)
name|sourceFilter
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|,
name|reader
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

