begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|index
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
name|Nullable
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
name|lease
operator|.
name|Releasable
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
name|lease
operator|.
name|Releasables
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
name|util
operator|.
name|BigArrays
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
name|util
operator|.
name|BytesRefHash
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
name|util
operator|.
name|IntArray
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
name|util
operator|.
name|LongArray
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
comment|/**  * A frequency terms enum that maintains a cache of docFreq, totalTermFreq, or both for repeated term lookup.   */
end_comment

begin_class
DECL|class|FreqTermsEnum
specifier|public
class|class
name|FreqTermsEnum
extends|extends
name|FilterableTermsEnum
implements|implements
name|Releasable
block|{
DECL|field|INITIAL_NUM_TERM_FREQS_CACHED
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_NUM_TERM_FREQS_CACHED
init|=
literal|512
decl_stmt|;
DECL|field|bigArrays
specifier|private
specifier|final
name|BigArrays
name|bigArrays
decl_stmt|;
DECL|field|termDocFreqs
specifier|private
name|IntArray
name|termDocFreqs
decl_stmt|;
DECL|field|termsTotalFreqs
specifier|private
name|LongArray
name|termsTotalFreqs
decl_stmt|;
DECL|field|cachedTermOrds
specifier|private
name|BytesRefHash
name|cachedTermOrds
decl_stmt|;
DECL|field|needDocFreqs
specifier|private
specifier|final
name|boolean
name|needDocFreqs
decl_stmt|;
DECL|field|needTotalTermFreqs
specifier|private
specifier|final
name|boolean
name|needTotalTermFreqs
decl_stmt|;
DECL|method|FreqTermsEnum
specifier|public
name|FreqTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|needDocFreq
parameter_list|,
name|boolean
name|needTotalTermFreq
parameter_list|,
annotation|@
name|Nullable
name|Query
name|filter
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|needTotalTermFreq
condition|?
name|PostingsEnum
operator|.
name|FREQS
else|:
name|PostingsEnum
operator|.
name|NONE
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|this
operator|.
name|bigArrays
operator|=
name|bigArrays
expr_stmt|;
name|this
operator|.
name|needDocFreqs
operator|=
name|needDocFreq
expr_stmt|;
name|this
operator|.
name|needTotalTermFreqs
operator|=
name|needTotalTermFreq
expr_stmt|;
if|if
condition|(
name|needDocFreq
condition|)
block|{
name|termDocFreqs
operator|=
name|bigArrays
operator|.
name|newIntArray
argument_list|(
name|INITIAL_NUM_TERM_FREQS_CACHED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termDocFreqs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|needTotalTermFreq
condition|)
block|{
name|termsTotalFreqs
operator|=
name|bigArrays
operator|.
name|newLongArray
argument_list|(
name|INITIAL_NUM_TERM_FREQS_CACHED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsTotalFreqs
operator|=
literal|null
expr_stmt|;
block|}
name|cachedTermOrds
operator|=
operator|new
name|BytesRefHash
argument_list|(
name|INITIAL_NUM_TERM_FREQS_CACHED
argument_list|,
name|bigArrays
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Check cache
name|long
name|currentTermOrd
init|=
name|cachedTermOrds
operator|.
name|add
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentTermOrd
operator|<
literal|0
condition|)
block|{
comment|// already seen, initialize instance data with the cached frequencies
name|currentTermOrd
operator|=
operator|-
literal|1
operator|-
name|currentTermOrd
expr_stmt|;
name|boolean
name|found
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|needDocFreqs
condition|)
block|{
name|currentDocFreq
operator|=
name|termDocFreqs
operator|.
name|get
argument_list|(
name|currentTermOrd
argument_list|)
expr_stmt|;
name|found
operator|=
name|currentDocFreq
operator|!=
name|NOT_FOUND
expr_stmt|;
block|}
if|if
condition|(
name|needTotalTermFreqs
condition|)
block|{
name|currentTotalTermFreq
operator|=
name|termsTotalFreqs
operator|.
name|get
argument_list|(
name|currentTermOrd
argument_list|)
expr_stmt|;
name|found
operator|=
name|currentTotalTermFreq
operator|!=
name|NOT_FOUND
expr_stmt|;
block|}
name|current
operator|=
name|found
condition|?
name|text
else|:
literal|null
expr_stmt|;
return|return
name|found
return|;
block|}
comment|//Cache miss - gather stats
specifier|final
name|boolean
name|found
init|=
name|super
operator|.
name|seekExact
argument_list|(
name|text
argument_list|)
decl_stmt|;
comment|//Cache the result - found or not.
if|if
condition|(
name|needDocFreqs
condition|)
block|{
name|termDocFreqs
operator|=
name|bigArrays
operator|.
name|grow
argument_list|(
name|termDocFreqs
argument_list|,
name|currentTermOrd
operator|+
literal|1
argument_list|)
expr_stmt|;
name|termDocFreqs
operator|.
name|set
argument_list|(
name|currentTermOrd
argument_list|,
name|currentDocFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|needTotalTermFreqs
condition|)
block|{
name|termsTotalFreqs
operator|=
name|bigArrays
operator|.
name|grow
argument_list|(
name|termsTotalFreqs
argument_list|,
name|currentTermOrd
operator|+
literal|1
argument_list|)
expr_stmt|;
name|termsTotalFreqs
operator|.
name|set
argument_list|(
name|currentTermOrd
argument_list|,
name|currentTotalTermFreq
argument_list|)
expr_stmt|;
block|}
return|return
name|found
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|cachedTermOrds
argument_list|,
name|termDocFreqs
argument_list|,
name|termsTotalFreqs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cachedTermOrds
operator|=
literal|null
expr_stmt|;
name|termDocFreqs
operator|=
literal|null
expr_stmt|;
name|termsTotalFreqs
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

