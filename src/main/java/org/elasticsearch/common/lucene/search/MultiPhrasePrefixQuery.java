begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectOpenHashSet
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
name|LeafReaderContext
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|StringHelper
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
name|ToStringUtils
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ListIterator
import|;
end_import

begin_class
DECL|class|MultiPhrasePrefixQuery
specifier|public
class|class
name|MultiPhrasePrefixQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|termArrays
specifier|private
name|ArrayList
argument_list|<
name|Term
index|[]
argument_list|>
name|termArrays
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|positions
specifier|private
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|positions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|maxExpansions
specifier|private
name|int
name|maxExpansions
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
literal|0
decl_stmt|;
comment|/**      * Sets the phrase slop for this query.      *      * @see org.apache.lucene.search.PhraseQuery#setSlop(int)      */
DECL|method|setSlop
specifier|public
name|void
name|setSlop
parameter_list|(
name|int
name|s
parameter_list|)
block|{
name|slop
operator|=
name|s
expr_stmt|;
block|}
DECL|method|setMaxExpansions
specifier|public
name|void
name|setMaxExpansions
parameter_list|(
name|int
name|maxExpansions
parameter_list|)
block|{
name|this
operator|.
name|maxExpansions
operator|=
name|maxExpansions
expr_stmt|;
block|}
comment|/**      * Sets the phrase slop for this query.      *      * @see org.apache.lucene.search.PhraseQuery#getSlop()      */
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
comment|/**      * Add a single term at the next position in the phrase.      *      * @see org.apache.lucene.search.PhraseQuery#add(Term)      */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
name|term
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add multiple terms at the next position in the phrase.  Any of the terms      * may match.      *      * @see org.apache.lucene.search.PhraseQuery#add(Term)      */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|)
block|{
name|int
name|position
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|positions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|position
operator|=
name|positions
operator|.
name|get
argument_list|(
name|positions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
operator|+
literal|1
expr_stmt|;
name|add
argument_list|(
name|terms
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allows to specify the relative position of terms within the phrase.      *      * @param terms      * @param position      * @see org.apache.lucene.search.PhraseQuery#add(Term, int)      */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|,
name|int
name|position
parameter_list|)
block|{
if|if
condition|(
name|termArrays
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
name|field
operator|=
name|terms
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All phrase terms must be in the same field ("
operator|+
name|field
operator|+
literal|"): "
operator|+
name|terms
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
name|termArrays
operator|.
name|add
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|positions
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a List of the terms in the multiphrase.      * Do not modify the List or its contents.      */
DECL|method|getTermArrays
specifier|public
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|getTermArrays
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|termArrays
argument_list|)
return|;
block|}
comment|/**      * Returns the relative positions of terms in this phrase.      */
DECL|method|getPositions
specifier|public
name|int
index|[]
name|getPositions
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|positions
operator|.
name|size
argument_list|()
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
name|positions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|result
index|[
name|i
index|]
operator|=
name|positions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termArrays
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
name|MultiPhraseQuery
name|query
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|int
name|sizeMinus1
init|=
name|termArrays
operator|.
name|size
argument_list|()
operator|-
literal|1
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
name|sizeMinus1
condition|;
name|i
operator|++
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|termArrays
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|positions
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Term
index|[]
name|suffixTerms
init|=
name|termArrays
operator|.
name|get
argument_list|(
name|sizeMinus1
argument_list|)
decl_stmt|;
name|int
name|position
init|=
name|positions
operator|.
name|get
argument_list|(
name|sizeMinus1
argument_list|)
decl_stmt|;
name|ObjectOpenHashSet
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ObjectOpenHashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|suffixTerms
control|)
block|{
name|getPrefixTerms
argument_list|(
name|terms
argument_list|,
name|term
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|>
name|maxExpansions
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|terms
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|()
return|;
block|}
name|query
operator|.
name|add
argument_list|(
name|terms
operator|.
name|toArray
argument_list|(
name|Term
operator|.
name|class
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
return|return
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
DECL|method|getPrefixTerms
specifier|private
name|void
name|getPrefixTerms
parameter_list|(
name|ObjectOpenHashSet
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|,
specifier|final
name|Term
name|prefix
parameter_list|,
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// SlowCompositeReaderWrapper could be used... but this would merge all terms from each segment into one terms
comment|// instance, which is very expensive. Therefore I think it is better to iterate over each leaf individually.
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|leaf
range|:
name|leaves
control|)
block|{
name|Terms
name|_terms
init|=
name|leaf
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|_terms
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|TermsEnum
name|termsEnum
init|=
name|_terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|TermsEnum
operator|.
name|SeekStatus
name|seekStatus
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|prefix
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
operator|==
name|seekStatus
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|term
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|term
argument_list|,
name|prefix
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|>=
name|maxExpansions
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
operator|||
operator|!
name|field
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Term
index|[]
argument_list|>
name|i
init|=
name|termArrays
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Term
index|[]
name|terms
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|terms
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|terms
index|[
name|j
index|]
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|j
operator|<
name|terms
operator|.
name|length
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"* "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"*)"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|terms
index|[
literal|0
index|]
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|slop
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"~"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|slop
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns true if<code>o</code> is equal to this.      */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|MultiPhrasePrefixQuery
operator|)
condition|)
return|return
literal|false
return|;
name|MultiPhrasePrefixQuery
name|other
init|=
operator|(
name|MultiPhrasePrefixQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|&&
name|this
operator|.
name|slop
operator|==
name|other
operator|.
name|slop
operator|&&
name|termArraysEquals
argument_list|(
name|this
operator|.
name|termArrays
argument_list|,
name|other
operator|.
name|termArrays
argument_list|)
operator|&&
name|this
operator|.
name|positions
operator|.
name|equals
argument_list|(
name|other
operator|.
name|positions
argument_list|)
return|;
block|}
comment|/**      * Returns a hash code value for this object.      */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|slop
operator|^
name|termArraysHashCode
argument_list|()
operator|^
name|positions
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x4AC65113
return|;
block|}
comment|// Breakout calculation of the termArrays hashcode
DECL|method|termArraysHashCode
specifier|private
name|int
name|termArraysHashCode
parameter_list|()
block|{
name|int
name|hashCode
init|=
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|Term
index|[]
name|termArray
range|:
name|termArrays
control|)
block|{
name|hashCode
operator|=
literal|31
operator|*
name|hashCode
operator|+
operator|(
name|termArray
operator|==
literal|null
condition|?
literal|0
else|:
name|Arrays
operator|.
name|hashCode
argument_list|(
name|termArray
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
comment|// Breakout calculation of the termArrays equals
DECL|method|termArraysEquals
specifier|private
name|boolean
name|termArraysEquals
parameter_list|(
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|termArrays1
parameter_list|,
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|termArrays2
parameter_list|)
block|{
if|if
condition|(
name|termArrays1
operator|.
name|size
argument_list|()
operator|!=
name|termArrays2
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ListIterator
argument_list|<
name|Term
index|[]
argument_list|>
name|iterator1
init|=
name|termArrays1
operator|.
name|listIterator
argument_list|()
decl_stmt|;
name|ListIterator
argument_list|<
name|Term
index|[]
argument_list|>
name|iterator2
init|=
name|termArrays2
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Term
index|[]
name|termArray1
init|=
name|iterator1
operator|.
name|next
argument_list|()
decl_stmt|;
name|Term
index|[]
name|termArray2
init|=
name|iterator2
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|termArray1
operator|==
literal|null
condition|?
name|termArray2
operator|==
literal|null
else|:
name|Arrays
operator|.
name|equals
argument_list|(
name|termArray1
argument_list|,
name|termArray2
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
block|}
end_class

end_unit

