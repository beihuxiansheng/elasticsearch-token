begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.termvectors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
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
name|Fields
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
name|PostingsEnum
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
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|TermStatistics
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
name|similarities
operator|.
name|ClassicSimilarity
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
name|similarities
operator|.
name|TFIDFSimilarity
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
name|Nullable
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
name|dfs
operator|.
name|AggregatedDfs
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
name|HashMap
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
name|Set
import|;
end_import

begin_class
DECL|class|TermVectorsFilter
specifier|public
class|class
name|TermVectorsFilter
block|{
DECL|field|DEFAULT_MAX_QUERY_TERMS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_QUERY_TERMS
init|=
literal|25
decl_stmt|;
DECL|field|DEFAULT_MIN_TERM_FREQ
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_TERM_FREQ
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_MAX_TERM_FREQ
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TERM_FREQ
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|DEFAULT_MIN_DOC_FREQ
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_DOC_FREQ
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_MAX_DOC_FREQ
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_DOC_FREQ
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|DEFAULT_MIN_WORD_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_WORD_LENGTH
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT_MAX_WORD_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_WORD_LENGTH
init|=
literal|0
decl_stmt|;
DECL|field|maxNumTerms
specifier|private
name|int
name|maxNumTerms
init|=
name|DEFAULT_MAX_QUERY_TERMS
decl_stmt|;
DECL|field|minTermFreq
specifier|private
name|int
name|minTermFreq
init|=
name|DEFAULT_MIN_TERM_FREQ
decl_stmt|;
DECL|field|maxTermFreq
specifier|private
name|int
name|maxTermFreq
init|=
name|DEFAULT_MAX_TERM_FREQ
decl_stmt|;
DECL|field|minDocFreq
specifier|private
name|int
name|minDocFreq
init|=
name|DEFAULT_MIN_DOC_FREQ
decl_stmt|;
DECL|field|maxDocFreq
specifier|private
name|int
name|maxDocFreq
init|=
name|DEFAULT_MAX_DOC_FREQ
decl_stmt|;
DECL|field|minWordLength
specifier|private
name|int
name|minWordLength
init|=
name|DEFAULT_MIN_WORD_LENGTH
decl_stmt|;
DECL|field|maxWordLength
specifier|private
name|int
name|maxWordLength
init|=
name|DEFAULT_MAX_WORD_LENGTH
decl_stmt|;
DECL|field|fields
specifier|private
name|Fields
name|fields
decl_stmt|;
DECL|field|topLevelFields
specifier|private
name|Fields
name|topLevelFields
decl_stmt|;
DECL|field|selectedFields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|selectedFields
decl_stmt|;
DECL|field|dfs
specifier|private
name|AggregatedDfs
name|dfs
decl_stmt|;
DECL|field|scoreTerms
specifier|private
name|Map
argument_list|<
name|Term
argument_list|,
name|ScoreTerm
argument_list|>
name|scoreTerms
decl_stmt|;
DECL|field|sizes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|sizes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|similarity
specifier|private
name|TFIDFSimilarity
name|similarity
decl_stmt|;
DECL|method|TermVectorsFilter
specifier|public
name|TermVectorsFilter
parameter_list|(
name|Fields
name|termVectorsByField
parameter_list|,
name|Fields
name|topLevelFields
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|selectedFields
parameter_list|,
annotation|@
name|Nullable
name|AggregatedDfs
name|dfs
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|termVectorsByField
expr_stmt|;
name|this
operator|.
name|topLevelFields
operator|=
name|topLevelFields
expr_stmt|;
name|this
operator|.
name|selectedFields
operator|=
name|selectedFields
expr_stmt|;
name|this
operator|.
name|dfs
operator|=
name|dfs
expr_stmt|;
name|this
operator|.
name|scoreTerms
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
operator|new
name|ClassicSimilarity
argument_list|()
expr_stmt|;
block|}
DECL|method|setSettings
specifier|public
name|void
name|setSettings
parameter_list|(
name|TermVectorsRequest
operator|.
name|FilterSettings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|settings
operator|.
name|maxNumTerms
operator|!=
literal|null
condition|)
block|{
name|setMaxNumTerms
argument_list|(
name|settings
operator|.
name|maxNumTerms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|minTermFreq
operator|!=
literal|null
condition|)
block|{
name|setMinTermFreq
argument_list|(
name|settings
operator|.
name|minTermFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|maxTermFreq
operator|!=
literal|null
condition|)
block|{
name|setMaxTermFreq
argument_list|(
name|settings
operator|.
name|maxTermFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|minDocFreq
operator|!=
literal|null
condition|)
block|{
name|setMinDocFreq
argument_list|(
name|settings
operator|.
name|minDocFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|maxDocFreq
operator|!=
literal|null
condition|)
block|{
name|setMaxDocFreq
argument_list|(
name|settings
operator|.
name|maxDocFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|minWordLength
operator|!=
literal|null
condition|)
block|{
name|setMinWordLength
argument_list|(
name|settings
operator|.
name|minWordLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|settings
operator|.
name|maxWordLength
operator|!=
literal|null
condition|)
block|{
name|setMaxWordLength
argument_list|(
name|settings
operator|.
name|maxWordLength
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getScoreTerm
specifier|public
name|ScoreTerm
name|getScoreTerm
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
name|scoreTerms
operator|.
name|get
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|hasScoreTerm
specifier|public
name|boolean
name|hasScoreTerm
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
name|getScoreTerm
argument_list|(
name|term
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|sizes
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|getMaxNumTerms
specifier|public
name|int
name|getMaxNumTerms
parameter_list|()
block|{
return|return
name|maxNumTerms
return|;
block|}
DECL|method|getMinTermFreq
specifier|public
name|int
name|getMinTermFreq
parameter_list|()
block|{
return|return
name|minTermFreq
return|;
block|}
DECL|method|getMaxTermFreq
specifier|public
name|int
name|getMaxTermFreq
parameter_list|()
block|{
return|return
name|maxTermFreq
return|;
block|}
DECL|method|getMinDocFreq
specifier|public
name|int
name|getMinDocFreq
parameter_list|()
block|{
return|return
name|minDocFreq
return|;
block|}
DECL|method|getMaxDocFreq
specifier|public
name|int
name|getMaxDocFreq
parameter_list|()
block|{
return|return
name|maxDocFreq
return|;
block|}
DECL|method|getMinWordLength
specifier|public
name|int
name|getMinWordLength
parameter_list|()
block|{
return|return
name|minWordLength
return|;
block|}
DECL|method|getMaxWordLength
specifier|public
name|int
name|getMaxWordLength
parameter_list|()
block|{
return|return
name|maxWordLength
return|;
block|}
DECL|method|setMaxNumTerms
specifier|public
name|void
name|setMaxNumTerms
parameter_list|(
name|int
name|maxNumTerms
parameter_list|)
block|{
name|this
operator|.
name|maxNumTerms
operator|=
name|maxNumTerms
expr_stmt|;
block|}
DECL|method|setMinTermFreq
specifier|public
name|void
name|setMinTermFreq
parameter_list|(
name|int
name|minTermFreq
parameter_list|)
block|{
name|this
operator|.
name|minTermFreq
operator|=
name|minTermFreq
expr_stmt|;
block|}
DECL|method|setMaxTermFreq
specifier|public
name|void
name|setMaxTermFreq
parameter_list|(
name|int
name|maxTermFreq
parameter_list|)
block|{
name|this
operator|.
name|maxTermFreq
operator|=
name|maxTermFreq
expr_stmt|;
block|}
DECL|method|setMinDocFreq
specifier|public
name|void
name|setMinDocFreq
parameter_list|(
name|int
name|minDocFreq
parameter_list|)
block|{
name|this
operator|.
name|minDocFreq
operator|=
name|minDocFreq
expr_stmt|;
block|}
DECL|method|setMaxDocFreq
specifier|public
name|void
name|setMaxDocFreq
parameter_list|(
name|int
name|maxDocFreq
parameter_list|)
block|{
name|this
operator|.
name|maxDocFreq
operator|=
name|maxDocFreq
expr_stmt|;
block|}
DECL|method|setMinWordLength
specifier|public
name|void
name|setMinWordLength
parameter_list|(
name|int
name|minWordLength
parameter_list|)
block|{
name|this
operator|.
name|minWordLength
operator|=
name|minWordLength
expr_stmt|;
block|}
DECL|method|setMaxWordLength
specifier|public
name|void
name|setMaxWordLength
parameter_list|(
name|int
name|maxWordLength
parameter_list|)
block|{
name|this
operator|.
name|maxWordLength
operator|=
name|maxWordLength
expr_stmt|;
block|}
DECL|class|ScoreTerm
specifier|public
specifier|static
specifier|final
class|class
name|ScoreTerm
block|{
DECL|field|field
specifier|public
name|String
name|field
decl_stmt|;
DECL|field|word
specifier|public
name|String
name|word
decl_stmt|;
DECL|field|score
specifier|public
name|float
name|score
decl_stmt|;
DECL|method|ScoreTerm
name|ScoreTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|word
parameter_list|,
name|float
name|score
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
name|word
operator|=
name|word
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|word
parameter_list|,
name|float
name|score
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
name|word
operator|=
name|word
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
block|}
DECL|method|selectBestTerms
specifier|public
name|void
name|selectBestTerms
parameter_list|()
throws|throws
name|IOException
block|{
name|PostingsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|(
name|selectedFields
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|!
name|selectedFields
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
operator|)
condition|)
block|{
continue|continue;
block|}
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|Terms
name|topLevelTerms
init|=
name|topLevelFields
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
comment|// if no terms found, take the retrieved term vector fields for stats
if|if
condition|(
name|topLevelTerms
operator|==
literal|null
condition|)
block|{
name|topLevelTerms
operator|=
name|terms
expr_stmt|;
block|}
name|long
name|numDocs
init|=
name|getDocCount
argument_list|(
name|fieldName
argument_list|,
name|topLevelTerms
argument_list|)
decl_stmt|;
comment|// one queue per field name
name|ScoreTermsQueue
name|queue
init|=
operator|new
name|ScoreTermsQueue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|maxNumTerms
argument_list|,
operator|(
name|int
operator|)
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// select terms with highest tf-idf
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|TermsEnum
name|topLevelTermsEnum
init|=
name|topLevelTerms
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|termBytesRef
init|=
name|termsEnum
operator|.
name|term
argument_list|()
decl_stmt|;
name|boolean
name|foundTerm
init|=
name|topLevelTermsEnum
operator|.
name|seekExact
argument_list|(
name|termBytesRef
argument_list|)
decl_stmt|;
assert|assert
name|foundTerm
operator|:
literal|"Term: "
operator|+
name|termBytesRef
operator|.
name|utf8ToString
argument_list|()
operator|+
literal|" not found!"
assert|;
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|termBytesRef
argument_list|)
decl_stmt|;
comment|// remove noise words
name|int
name|freq
init|=
name|getTermFreq
argument_list|(
name|termsEnum
argument_list|,
name|docsEnum
argument_list|)
decl_stmt|;
if|if
condition|(
name|isNoise
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|freq
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// now call on docFreq
name|long
name|docFreq
init|=
name|getTermStatistics
argument_list|(
name|topLevelTermsEnum
argument_list|,
name|term
argument_list|)
operator|.
name|docFreq
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isAccepted
argument_list|(
name|docFreq
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// filter based on score
name|float
name|score
init|=
name|computeScore
argument_list|(
name|docFreq
argument_list|,
name|freq
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
name|queue
operator|.
name|addOrUpdate
argument_list|(
operator|new
name|ScoreTerm
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// retain the best terms for quick lookups
name|ScoreTerm
name|scoreTerm
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|scoreTerm
operator|=
name|queue
operator|.
name|pop
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|scoreTerms
operator|.
name|put
argument_list|(
operator|new
name|Term
argument_list|(
name|scoreTerm
operator|.
name|field
argument_list|,
name|scoreTerm
operator|.
name|word
argument_list|)
argument_list|,
name|scoreTerm
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|sizes
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isNoise
specifier|private
name|boolean
name|isNoise
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
comment|// filter out words based on length
name|int
name|len
init|=
name|word
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|minWordLength
operator|>
literal|0
operator|&&
name|len
operator|<
name|minWordLength
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|maxWordLength
operator|>
literal|0
operator|&&
name|len
operator|>
name|maxWordLength
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// filter out words that don't occur enough times in the source
if|if
condition|(
name|minTermFreq
operator|>
literal|0
operator|&&
name|freq
operator|<
name|minTermFreq
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// filter out words that occur too many times in the source
if|if
condition|(
name|freq
operator|>
name|maxTermFreq
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|isAccepted
specifier|private
name|boolean
name|isAccepted
parameter_list|(
name|long
name|docFreq
parameter_list|)
block|{
comment|// filter out words that don't occur in enough docs
if|if
condition|(
name|minDocFreq
operator|>
literal|0
operator|&&
name|docFreq
operator|<
name|minDocFreq
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// filter out words that occur in too many docs
if|if
condition|(
name|docFreq
operator|>
name|maxDocFreq
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// index update problem?
if|if
condition|(
name|docFreq
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|getDocCount
specifier|private
name|long
name|getDocCount
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Terms
name|topLevelTerms
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
return|return
name|dfs
operator|.
name|fieldStatistics
argument_list|()
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|docCount
argument_list|()
return|;
block|}
return|return
name|topLevelTerms
operator|.
name|getDocCount
argument_list|()
return|;
block|}
DECL|method|getTermStatistics
specifier|private
name|TermStatistics
name|getTermStatistics
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
return|return
name|dfs
operator|.
name|termStatistics
argument_list|()
operator|.
name|get
argument_list|(
name|term
argument_list|)
return|;
block|}
return|return
operator|new
name|TermStatistics
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getTermFreq
specifier|private
name|int
name|getTermFreq
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|,
name|PostingsEnum
name|docsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
name|docsEnum
argument_list|)
expr_stmt|;
name|docsEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
return|return
name|docsEnum
operator|.
name|freq
argument_list|()
return|;
block|}
DECL|method|computeScore
specifier|private
name|float
name|computeScore
parameter_list|(
name|long
name|docFreq
parameter_list|,
name|int
name|freq
parameter_list|,
name|long
name|numDocs
parameter_list|)
block|{
return|return
name|freq
operator|*
name|similarity
operator|.
name|idf
argument_list|(
name|docFreq
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
DECL|class|ScoreTermsQueue
specifier|private
specifier|static
class|class
name|ScoreTermsQueue
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
argument_list|<
name|ScoreTerm
argument_list|>
block|{
DECL|field|limit
specifier|private
specifier|final
name|int
name|limit
decl_stmt|;
DECL|method|ScoreTermsQueue
name|ScoreTermsQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|maxSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|ScoreTerm
name|a
parameter_list|,
name|ScoreTerm
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|score
operator|<
name|b
operator|.
name|score
return|;
block|}
DECL|method|addOrUpdate
specifier|public
name|void
name|addOrUpdate
parameter_list|(
name|ScoreTerm
name|scoreTerm
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|size
argument_list|()
operator|<
name|limit
condition|)
block|{
comment|// there is still space in the queue
name|this
operator|.
name|add
argument_list|(
name|scoreTerm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise update the smallest in the queue in place and update the queue
name|ScoreTerm
name|scoreTermTop
init|=
name|this
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|scoreTermTop
operator|.
name|score
operator|<
name|scoreTerm
operator|.
name|score
condition|)
block|{
name|scoreTermTop
operator|.
name|update
argument_list|(
name|scoreTerm
operator|.
name|field
argument_list|,
name|scoreTerm
operator|.
name|word
argument_list|,
name|scoreTerm
operator|.
name|score
argument_list|)
expr_stmt|;
name|this
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

