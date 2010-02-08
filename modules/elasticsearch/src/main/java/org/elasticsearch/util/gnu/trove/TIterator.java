begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.gnu.trove
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ConcurrentModificationException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Abstract iterator class for THash implementations.  This class provides some  * of the common iterator operations (hasNext(), remove()) and allows subclasses  * to define the mechanism(s) for advancing the iterator and returning data.  *  * @author Eric D. Friedman  * @version $Id: TIterator.java,v 1.3 2007/06/29 20:03:10 robeden Exp $  */
end_comment

begin_class
DECL|class|TIterator
specifier|abstract
class|class
name|TIterator
block|{
comment|/**      * the data structure this iterator traverses      */
DECL|field|_hash
specifier|protected
specifier|final
name|THash
name|_hash
decl_stmt|;
comment|/**      * the number of elements this iterator believes are in the      * data structure it accesses.      */
DECL|field|_expectedSize
specifier|protected
name|int
name|_expectedSize
decl_stmt|;
comment|/**      * the index used for iteration.      */
DECL|field|_index
specifier|protected
name|int
name|_index
decl_stmt|;
comment|/**      * Create an instance of TIterator over the specified THash.      */
DECL|method|TIterator
specifier|public
name|TIterator
parameter_list|(
name|THash
name|hash
parameter_list|)
block|{
name|_hash
operator|=
name|hash
expr_stmt|;
name|_expectedSize
operator|=
name|_hash
operator|.
name|size
argument_list|()
expr_stmt|;
name|_index
operator|=
name|_hash
operator|.
name|capacity
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns true if the iterator can be advanced past its current      * location.      *      * @return a<code>boolean</code> value      */
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIndex
argument_list|()
operator|>=
literal|0
return|;
block|}
comment|/**      * Removes the last entry returned by the iterator.      * Invoking this method more than once for a single entry      * will leave the underlying data structure in a confused      * state.      */
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|_expectedSize
operator|!=
name|_hash
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ConcurrentModificationException
argument_list|()
throw|;
block|}
comment|// Disable auto compaction during the remove. This is a workaround for bug 1642768.
try|try
block|{
name|_hash
operator|.
name|tempDisableAutoCompaction
argument_list|()
expr_stmt|;
name|_hash
operator|.
name|removeAt
argument_list|(
name|_index
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|_hash
operator|.
name|reenableAutoCompaction
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|_expectedSize
operator|--
expr_stmt|;
block|}
comment|/**      * Sets the internal<tt>index</tt> so that the `next' object      * can be returned.      */
DECL|method|moveToNextIndex
specifier|protected
specifier|final
name|void
name|moveToNextIndex
parameter_list|()
block|{
comment|// doing the assignment&&< 0 in one line shaves
comment|// 3 opcodes...
if|if
condition|(
operator|(
name|_index
operator|=
name|nextIndex
argument_list|()
operator|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
comment|/**      * Returns the index of the next value in the data structure      * or a negative value if the iterator is exhausted.      *      * @return an<code>int</code> value      */
DECL|method|nextIndex
specifier|abstract
specifier|protected
name|int
name|nextIndex
parameter_list|()
function_decl|;
block|}
end_class

begin_comment
comment|// TIterator
end_comment

end_unit

