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

begin_comment
comment|/**  * Implements all iterator functions for the hashed object set.  * Subclasses may override objectAtIndex to vary the object  * returned by calls to next() (e.g. for values, and Map.Entry  * objects).  *<p/>  *<p> Note that iteration is fastest if you forego the calls to  *<tt>hasNext</tt> in favor of checking the size of the structure  * yourself and then call next() that many times:  *<p/>  *<pre>  * Iterator i = collection.iterator();  * for (int size = collection.size(); size--> 0;) {  *   Object o = i.next();  * }  *</pre>  *<p/>  *<p>You may, of course, use the hasNext(), next() idiom too if  * you aren't in a performance critical spot.</p>  */
end_comment

begin_class
DECL|class|TPrimitiveIterator
specifier|abstract
class|class
name|TPrimitiveIterator
extends|extends
name|TIterator
block|{
comment|/**      * the collection on which this iterator operates.      */
DECL|field|_hash
specifier|protected
specifier|final
name|TPrimitiveHash
name|_hash
decl_stmt|;
comment|/**      * Creates a TPrimitiveIterator for the specified collection.      */
DECL|method|TPrimitiveIterator
specifier|public
name|TPrimitiveIterator
parameter_list|(
name|TPrimitiveHash
name|hash
parameter_list|)
block|{
name|super
argument_list|(
name|hash
argument_list|)
expr_stmt|;
name|_hash
operator|=
name|hash
expr_stmt|;
block|}
comment|/**      * Returns the index of the next value in the data structure      * or a negative value if the iterator is exhausted.      *      * @return an<code>int</code> value      * @throws ConcurrentModificationException      *          if the underlying collection's      *          size has been modified since the iterator was created.      */
DECL|method|nextIndex
specifier|protected
specifier|final
name|int
name|nextIndex
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
name|byte
index|[]
name|states
init|=
name|_hash
operator|.
name|_states
decl_stmt|;
name|int
name|i
init|=
name|_index
decl_stmt|;
while|while
condition|(
name|i
operator|--
operator|>
literal|0
operator|&&
operator|(
name|states
index|[
name|i
index|]
operator|!=
name|TPrimitiveHash
operator|.
name|FULL
operator|)
condition|)
empty_stmt|;
return|return
name|i
return|;
block|}
block|}
end_class

begin_comment
comment|// TPrimitiveIterator
end_comment

end_unit

