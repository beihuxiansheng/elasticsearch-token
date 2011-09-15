begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|TermDocs
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
name|FixedBitSet
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
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_comment
comment|// LUCENE MONITOR: Against TermsFilter
end_comment

begin_class
DECL|class|PublicTermsFilter
specifier|public
class|class
name|PublicTermsFilter
extends|extends
name|Filter
block|{
DECL|field|terms
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|TreeSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Adds a term to the list of acceptable terms      *      * @param term      */
DECL|method|addTerm
specifier|public
name|void
name|addTerm
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|getTerms
specifier|public
name|Set
argument_list|<
name|Term
argument_list|>
name|getTerms
parameter_list|()
block|{
return|return
name|terms
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
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
return|return
literal|false
return|;
name|PublicTermsFilter
name|test
init|=
operator|(
name|PublicTermsFilter
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|terms
operator|==
name|test
operator|.
name|terms
operator|||
operator|(
name|terms
operator|!=
literal|null
operator|&&
name|terms
operator|.
name|equals
argument_list|(
name|test
operator|.
name|terms
argument_list|)
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
literal|9
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iter
init|=
name|terms
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Term
name|term
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|term
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|result
init|=
literal|null
decl_stmt|;
name|TermDocs
name|td
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|td
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|td
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|set
argument_list|(
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|td
operator|.
name|next
argument_list|()
condition|)
block|{
name|result
operator|.
name|set
argument_list|(
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|td
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

