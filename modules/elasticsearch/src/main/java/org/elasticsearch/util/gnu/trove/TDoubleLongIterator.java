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

begin_comment
comment|//////////////////////////////////////////////////
end_comment

begin_comment
comment|// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
end_comment

begin_comment
comment|//////////////////////////////////////////////////
end_comment

begin_comment
comment|/**  * Iterator for maps of type double and long.  *<p/>  *<p>The iterator semantics for Trove's primitive maps is slightly different  * from those defined in<tt>java.util.Iterator</tt>, but still well within  * the scope of the pattern, as defined by Gamma, et al.</p>  *<p/>  *<p>This iterator does<b>not</b> implicitly advance to the next entry when  * the value at the current position is retrieved.  Rather, you must explicitly  * ask the iterator to<tt>advance()</tt> and then retrieve either the<tt>key()</tt>,  * the<tt>value()</tt> or both.  This is done so that you have the option, but not  * the obligation, to retrieve keys and/or values as your application requires, and  * without introducing wrapper objects that would carry both.  As the iteration is  * stateful, access to the key/value parts of the current map entry happens in  * constant time.</p>  *<p/>  *<p>In practice, the iterator is akin to a "search finger" that you move from  * position to position.  Read or write operations affect the current entry only and  * do not assume responsibility for moving the finger.</p>  *<p/>  *<p>Here are some sample scenarios for this class of iterator:</p>  *<p/>  *<pre>  * // accessing keys/values through an iterator:  * for (TDoubleLongIterator it = map.iterator();  *      it.hasNext();) {  *   it.advance();  *   if (satisfiesCondition(it.key()) {  *     doSomethingWithValue(it.value());  *   }  * }  *</pre>  *<p/>  *<pre>  * // modifying values in-place through iteration:  * for (TDoubleLongIterator it = map.iterator();  *      it.hasNext();) {  *   it.advance();  *   if (satisfiesCondition(it.key()) {  *     it.setValue(newValueForKey(it.key()));  *   }  * }  *</pre>  *<p/>  *<pre>  * // deleting entries during iteration:  * for (TDoubleLongIterator it = map.iterator();  *      it.hasNext();) {  *   it.advance();  *   if (satisfiesCondition(it.key()) {  *     it.remove();  *   }  * }  *</pre>  *<p/>  *<pre>  * // faster iteration by avoiding hasNext():  * TDoubleLongIterator iterator = map.iterator();  * for (int i = map.size(); i--> 0;) {  *   iterator.advance();  *   doSomethingWithKeyAndValue(iterator.key(), iterator.value());  * }  *</pre>  *  * @author Eric D. Friedman  * @version $Id: P2PIterator.template,v 1.1 2006/11/10 23:28:00 robeden Exp $  */
end_comment

begin_class
DECL|class|TDoubleLongIterator
specifier|public
class|class
name|TDoubleLongIterator
extends|extends
name|TPrimitiveIterator
block|{
comment|/**      * the collection being iterated over      */
DECL|field|_map
specifier|private
specifier|final
name|TDoubleLongHashMap
name|_map
decl_stmt|;
comment|/**      * Creates an iterator over the specified map      */
DECL|method|TDoubleLongIterator
specifier|public
name|TDoubleLongIterator
parameter_list|(
name|TDoubleLongHashMap
name|map
parameter_list|)
block|{
name|super
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|this
operator|.
name|_map
operator|=
name|map
expr_stmt|;
block|}
comment|/**      * Moves the iterator forward to the next entry in the underlying map.      *      * @throws java.util.NoSuchElementException      *          if the iterator is already exhausted      */
DECL|method|advance
specifier|public
name|void
name|advance
parameter_list|()
block|{
name|moveToNextIndex
argument_list|()
expr_stmt|;
block|}
comment|/**      * Provides access to the key of the mapping at the iterator's position.      * Note that you must<tt>advance()</tt> the iterator at least once      * before invoking this method.      *      * @return the key of the entry at the iterator's current position.      */
DECL|method|key
specifier|public
name|double
name|key
parameter_list|()
block|{
return|return
name|_map
operator|.
name|_set
index|[
name|_index
index|]
return|;
block|}
comment|/**      * Provides access to the value of the mapping at the iterator's position.      * Note that you must<tt>advance()</tt> the iterator at least once      * before invoking this method.      *      * @return the value of the entry at the iterator's current position.      */
DECL|method|value
specifier|public
name|long
name|value
parameter_list|()
block|{
return|return
name|_map
operator|.
name|_values
index|[
name|_index
index|]
return|;
block|}
comment|/**      * Replace the value of the mapping at the iterator's position with the      * specified value. Note that you must<tt>advance()</tt> the iterator at      * least once before invoking this method.      *      * @param val the value to set in the current entry      * @return the old value of the entry.      */
DECL|method|setValue
specifier|public
name|long
name|setValue
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|long
name|old
init|=
name|value
argument_list|()
decl_stmt|;
name|_map
operator|.
name|_values
index|[
name|_index
index|]
operator|=
name|val
expr_stmt|;
return|return
name|old
return|;
block|}
block|}
end_class

begin_comment
comment|// TDoubleLongIterator
end_comment

end_unit

