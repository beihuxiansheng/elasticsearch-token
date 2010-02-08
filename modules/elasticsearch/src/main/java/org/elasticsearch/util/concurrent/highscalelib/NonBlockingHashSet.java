begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_comment
comment|/*  * Written by Cliff Click and released to the public domain, as explained at  * http://creativecommons.org/licenses/publicdomain  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.concurrent.highscalelib
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|highscalelib
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
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

begin_comment
comment|/**  * A simple wrapper around {@link NonBlockingHashMap} making it implement the  * {@link Set} interface.  All operations are Non-Blocking and multi-thread safe.  *  * @author Cliff Click  * @since 1.5  */
end_comment

begin_class
DECL|class|NonBlockingHashSet
specifier|public
class|class
name|NonBlockingHashSet
parameter_list|<
name|E
parameter_list|>
extends|extends
name|AbstractSet
argument_list|<
name|E
argument_list|>
implements|implements
name|Serializable
block|{
DECL|field|V
specifier|private
specifier|static
specifier|final
name|Object
name|V
init|=
literal|""
decl_stmt|;
DECL|field|_map
specifier|private
specifier|final
name|NonBlockingHashMap
argument_list|<
name|E
argument_list|,
name|Object
argument_list|>
name|_map
decl_stmt|;
comment|/**      * Make a new empty {@link NonBlockingHashSet}.      */
DECL|method|NonBlockingHashSet
specifier|public
name|NonBlockingHashSet
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|_map
operator|=
operator|new
name|NonBlockingHashMap
argument_list|<
name|E
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Add {@code o} to the set.      *      * @return<tt>true</tt> if {@code o} was added to the set,<tt>false</tt>      *         if {@code o} was already in the set.      */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
specifier|final
name|E
name|o
parameter_list|)
block|{
return|return
name|_map
operator|.
name|putIfAbsent
argument_list|(
name|o
argument_list|,
name|V
argument_list|)
operator|!=
name|V
return|;
block|}
comment|/**      * @return<tt>true</tt> if {@code o} is in the set.      */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
return|return
name|_map
operator|.
name|containsKey
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/**      * Remove {@code o} from the set.      *      * @return<tt>true</tt> if {@code o} was removed to the set,<tt>false</tt>      *         if {@code o} was not in the set.      */
DECL|method|remove
specifier|public
name|boolean
name|remove
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
return|return
name|_map
operator|.
name|remove
argument_list|(
name|o
argument_list|)
operator|==
name|V
return|;
block|}
comment|/**      * Current count of elements in the set.  Due to concurrent racing updates,      * the size is only ever approximate.  Updates due to the calling thread are      * immediately visible to calling thread.      *      * @return count of elements.      */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|_map
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Empty the set.      */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|_map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|_map
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|// ---
comment|/**      * Atomically make the set immutable.  Future calls to mutate will throw an      * IllegalStateException.  Existing mutator calls in other threads racing      * with this thread and will either throw IllegalStateException or their      * update will be visible to this thread.  This implies that a simple flag      * cannot make the Set immutable, because a late-arriving update in another      * thread might see immutable flag not set yet, then mutate the Set after      * the {@link #readOnly} call returns.  This call can be called concurrently      * (and indeed until the operation completes, all calls on the Set from any      * thread either complete normally or end up calling {@link #readOnly}      * internally).      *<p/>      *<p> This call is useful in debugging multi-threaded programs where the      * Set is constructed in parallel, but construction completes after some      * time; and after construction the Set is only read.  Making the Set      * read-only will cause updates arriving after construction is supposedly      * complete to throw an {@link IllegalStateException}.      */
comment|// (1) call _map's immutable() call
comment|// (2) get snapshot
comment|// (3) CAS down a local map, power-of-2 larger than _map.size()+1/8th
comment|// (4) start @ random, visit all snapshot, insert live keys
comment|// (5) CAS _map to null, needs happens-after (4)
comment|// (6) if Set call sees _map is null, needs happens-after (4) for readers
DECL|method|readOnly
specifier|public
name|void
name|readOnly
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unimplemented"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

