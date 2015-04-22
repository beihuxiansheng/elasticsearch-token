begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.prefix.tree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|tree
package|;
end_package

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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * An Iterator of SpatialPrefixTree Cells. The order is always sorted without duplicates.  *  * @lucene.experimental  *  * NOTE: Will be removed upon commit of LUCENE-6422  */
end_comment

begin_class
DECL|class|CellIterator
specifier|public
specifier|abstract
class|class
name|CellIterator
implements|implements
name|Iterator
argument_list|<
name|Cell
argument_list|>
block|{
comment|//note: nextCell or thisCell can be non-null but neither at the same time. That's
comment|// because they might return the same instance when re-used!
DECL|field|nextCell
specifier|protected
name|Cell
name|nextCell
decl_stmt|;
comment|//to be returned by next(), and null'ed after
DECL|field|thisCell
specifier|protected
name|Cell
name|thisCell
decl_stmt|;
comment|//see next()& thisCell(). Should be cleared in hasNext().
comment|/** Returns the cell last returned from {@link #next()}. It's cleared by hasNext(). */
DECL|method|thisCell
specifier|public
name|Cell
name|thisCell
parameter_list|()
block|{
assert|assert
name|thisCell
operator|!=
literal|null
operator|:
literal|"Only call thisCell() after next(), not hasNext()"
assert|;
return|return
name|thisCell
return|;
block|}
comment|// Arguably this belongs here and not on Cell
comment|//public SpatialRelation getShapeRel()
comment|/**    * Gets the next cell that is&gt;= {@code fromCell}, compared using non-leaf bytes. If it returns null then    * the iterator is exhausted.    */
DECL|method|nextFrom
specifier|public
name|Cell
name|nextFrom
parameter_list|(
name|Cell
name|fromCell
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
return|return
literal|null
return|;
name|Cell
name|c
init|=
name|next
argument_list|()
decl_stmt|;
comment|//will update thisCell
if|if
condition|(
name|c
operator|.
name|compareToNoLeaf
argument_list|(
name|fromCell
argument_list|)
operator|>=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
block|}
block|}
comment|/** This prevents sub-cells (those underneath the current cell) from being iterated to,    *  if applicable, otherwise a NO-OP. */
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
assert|assert
name|thisCell
operator|!=
literal|null
assert|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Cell
name|next
parameter_list|()
block|{
if|if
condition|(
name|nextCell
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|thisCell
operator|=
name|nextCell
expr_stmt|;
name|nextCell
operator|=
literal|null
expr_stmt|;
return|return
name|thisCell
return|;
block|}
block|}
end_class

end_unit

