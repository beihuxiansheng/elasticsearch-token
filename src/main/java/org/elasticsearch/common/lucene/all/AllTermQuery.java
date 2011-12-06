begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.all
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|all
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
name|index
operator|.
name|TermPositions
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
name|search
operator|.
name|spans
operator|.
name|SpanScorer
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
operator|.
name|SpanWeight
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
name|TermSpans
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
operator|.
name|PayloadHelper
operator|.
name|decodeFloat
import|;
end_import

begin_comment
comment|/**  * A term query that takes all payload boost values into account.  *  *  */
end_comment

begin_class
DECL|class|AllTermQuery
specifier|public
class|class
name|AllTermQuery
extends|extends
name|SpanTermQuery
block|{
DECL|field|includeSpanScore
specifier|private
name|boolean
name|includeSpanScore
decl_stmt|;
DECL|method|AllTermQuery
specifier|public
name|AllTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|AllTermQuery
specifier|public
name|AllTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|boolean
name|includeSpanScore
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeSpanScore
operator|=
name|includeSpanScore
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AllTermWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|)
return|;
block|}
DECL|class|AllTermWeight
specifier|protected
class|class
name|AllTermWeight
extends|extends
name|SpanWeight
block|{
DECL|method|AllTermWeight
specifier|public
name|AllTermWeight
parameter_list|(
name|AllTermQuery
name|query
parameter_list|,
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AllTermSpanScorer
argument_list|(
operator|(
name|TermSpans
operator|)
name|query
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AllTermSpanScorer
specifier|protected
class|class
name|AllTermSpanScorer
extends|extends
name|SpanScorer
block|{
comment|// TODO: is this the best way to allocate this?
DECL|field|payload
specifier|protected
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
DECL|field|positions
specifier|protected
name|TermPositions
name|positions
decl_stmt|;
DECL|field|payloadScore
specifier|protected
name|float
name|payloadScore
decl_stmt|;
DECL|field|payloadsSeen
specifier|protected
name|int
name|payloadsSeen
decl_stmt|;
DECL|method|AllTermSpanScorer
specifier|public
name|AllTermSpanScorer
parameter_list|(
name|TermSpans
name|spans
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|spans
argument_list|,
name|weight
argument_list|,
name|similarity
argument_list|,
name|norms
argument_list|)
expr_stmt|;
name|positions
operator|=
name|spans
operator|.
name|getPositions
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setFreqCurrentDoc
specifier|protected
name|boolean
name|setFreqCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|more
condition|)
block|{
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|spans
operator|.
name|doc
argument_list|()
expr_stmt|;
name|freq
operator|=
literal|0.0f
expr_stmt|;
name|payloadScore
operator|=
literal|0
expr_stmt|;
name|payloadsSeen
operator|=
literal|0
expr_stmt|;
name|Similarity
name|similarity1
init|=
name|getSimilarity
argument_list|()
decl_stmt|;
while|while
condition|(
name|more
operator|&&
name|doc
operator|==
name|spans
operator|.
name|doc
argument_list|()
condition|)
block|{
name|int
name|matchLength
init|=
name|spans
operator|.
name|end
argument_list|()
operator|-
name|spans
operator|.
name|start
argument_list|()
decl_stmt|;
name|freq
operator|+=
name|similarity1
operator|.
name|sloppyFreq
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
name|processPayload
argument_list|(
name|similarity1
argument_list|)
expr_stmt|;
name|more
operator|=
name|spans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// this moves positions to the next match in this
comment|// document
block|}
return|return
name|more
operator|||
operator|(
name|freq
operator|!=
literal|0
operator|)
return|;
block|}
DECL|method|processPayload
specifier|protected
name|void
name|processPayload
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|positions
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|payload
operator|=
name|positions
operator|.
name|getPayload
argument_list|(
name|payload
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|payloadScore
operator|+=
name|decodeFloat
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|payloadsSeen
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// zero out the payload?
block|}
block|}
comment|/**              * @return {@link #getSpanScore()} * {@link #getPayloadScore()}              * @throws IOException              */
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|includeSpanScore
condition|?
name|getSpanScore
argument_list|()
operator|*
name|getPayloadScore
argument_list|()
else|:
name|getPayloadScore
argument_list|()
return|;
block|}
comment|/**              * Returns the SpanScorer score only.              *<p/>              * Should not be overridden without good cause!              *              * @return the score for just the Span part w/o the payload              * @throws IOException              * @see #score()              */
DECL|method|getSpanScore
specifier|protected
name|float
name|getSpanScore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|score
argument_list|()
return|;
block|}
comment|/**              * The score for the payload              */
DECL|method|getPayloadScore
specifier|protected
name|float
name|getPayloadScore
parameter_list|()
block|{
return|return
name|payloadsSeen
operator|>
literal|0
condition|?
operator|(
name|payloadScore
operator|/
name|payloadsSeen
operator|)
else|:
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|protected
name|Explanation
name|explain
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|Explanation
name|nonPayloadExpl
init|=
name|super
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|nonPayloadExpl
argument_list|)
expr_stmt|;
comment|// QUESTION: Is there a way to avoid this skipTo call? We need to know
comment|// whether to load the payload or not
name|Explanation
name|payloadBoost
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|payloadBoost
argument_list|)
expr_stmt|;
name|float
name|payloadScore
init|=
name|getPayloadScore
argument_list|()
decl_stmt|;
name|payloadBoost
operator|.
name|setValue
argument_list|(
name|payloadScore
argument_list|)
expr_stmt|;
comment|// GSI: I suppose we could toString the payload, but I don't think that
comment|// would be a good idea
name|payloadBoost
operator|.
name|setDescription
argument_list|(
literal|"allPayload(...)"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|nonPayloadExpl
operator|.
name|getValue
argument_list|()
operator|*
name|payloadScore
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"btq, product of:"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|nonPayloadExpl
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|?
name|Boolean
operator|.
name|FALSE
else|:
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// LUCENE-1303
return|return
name|result
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|includeSpanScore
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|AllTermQuery
name|other
init|=
operator|(
name|AllTermQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|includeSpanScore
operator|!=
name|other
operator|.
name|includeSpanScore
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

