begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io.stream
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|trove
operator|.
name|ExtTObjectIntHasMap
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
name|trove
operator|.
name|TObjectIntHashMap
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HandlesStreamOutput
specifier|public
class|class
name|HandlesStreamOutput
extends|extends
name|StreamOutput
block|{
DECL|field|DEFAULT_IDENTITY_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_IDENTITY_THRESHOLD
init|=
literal|50
decl_stmt|;
comment|// a threshold above which strings will use identity check
DECL|field|identityThreshold
specifier|private
specifier|final
name|int
name|identityThreshold
decl_stmt|;
DECL|field|out
specifier|private
name|StreamOutput
name|out
decl_stmt|;
DECL|field|handles
specifier|private
specifier|final
name|TObjectIntHashMap
argument_list|<
name|String
argument_list|>
name|handles
init|=
operator|new
name|ExtTObjectIntHasMap
argument_list|<
name|String
argument_list|>
argument_list|()
operator|.
name|defaultReturnValue
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|identityHandles
specifier|private
specifier|final
name|HandleTable
name|identityHandles
init|=
operator|new
name|HandleTable
argument_list|(
literal|10
argument_list|,
operator|(
name|float
operator|)
literal|3.00
argument_list|)
decl_stmt|;
DECL|method|HandlesStreamOutput
specifier|public
name|HandlesStreamOutput
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
block|{
name|this
argument_list|(
name|out
argument_list|,
name|DEFAULT_IDENTITY_THRESHOLD
argument_list|)
expr_stmt|;
block|}
DECL|method|HandlesStreamOutput
specifier|public
name|HandlesStreamOutput
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|int
name|identityThreshold
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|identityThreshold
operator|=
name|identityThreshold
expr_stmt|;
block|}
DECL|method|writeUTF
annotation|@
name|Override
specifier|public
name|void
name|writeUTF
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
name|identityThreshold
condition|)
block|{
name|int
name|handle
init|=
name|handles
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|handle
operator|==
operator|-
literal|1
condition|)
block|{
name|handle
operator|=
name|handles
operator|.
name|size
argument_list|()
expr_stmt|;
name|handles
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|handle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|handle
init|=
name|identityHandles
operator|.
name|lookup
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|handle
operator|==
operator|-
literal|1
condition|)
block|{
name|handle
operator|=
name|identityHandles
operator|.
name|assign
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeByte
annotation|@
name|Override
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|writeBytes
annotation|@
name|Override
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|handles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|identityHandles
operator|.
name|clear
argument_list|()
expr_stmt|;
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|flush
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|wrappedOut
specifier|public
name|StreamOutput
name|wrappedOut
parameter_list|()
block|{
return|return
name|this
operator|.
name|out
return|;
block|}
comment|/**      * Lightweight identity hash table which maps objects to integer handles,      * assigned in ascending order.      */
DECL|class|HandleTable
specifier|private
specifier|static
class|class
name|HandleTable
block|{
comment|/* number of mappings in table/next available handle */
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
comment|/* size threshold determining when to expand hash spine */
DECL|field|threshold
specifier|private
name|int
name|threshold
decl_stmt|;
comment|/* factor for computing size threshold */
DECL|field|loadFactor
specifier|private
specifier|final
name|float
name|loadFactor
decl_stmt|;
comment|/* maps hash value -> candidate handle value */
DECL|field|spine
specifier|private
name|int
index|[]
name|spine
decl_stmt|;
comment|/* maps handle value -> next candidate handle value */
DECL|field|next
specifier|private
name|int
index|[]
name|next
decl_stmt|;
comment|/* maps handle value -> associated object */
DECL|field|objs
specifier|private
name|Object
index|[]
name|objs
decl_stmt|;
comment|/**          * Creates new HandleTable with given capacity and load factor.          */
DECL|method|HandleTable
name|HandleTable
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|float
name|loadFactor
parameter_list|)
block|{
name|this
operator|.
name|loadFactor
operator|=
name|loadFactor
expr_stmt|;
name|spine
operator|=
operator|new
name|int
index|[
name|initialCapacity
index|]
expr_stmt|;
name|next
operator|=
operator|new
name|int
index|[
name|initialCapacity
index|]
expr_stmt|;
name|objs
operator|=
operator|new
name|Object
index|[
name|initialCapacity
index|]
expr_stmt|;
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|initialCapacity
operator|*
name|loadFactor
argument_list|)
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**          * Assigns next available handle to given object, and returns handle          * value.  Handles are assigned in ascending order starting at 0.          */
DECL|method|assign
name|int
name|assign
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>=
name|next
operator|.
name|length
condition|)
block|{
name|growEntries
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|>=
name|threshold
condition|)
block|{
name|growSpine
argument_list|()
expr_stmt|;
block|}
name|insert
argument_list|(
name|obj
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|size
operator|++
return|;
block|}
comment|/**          * Looks up and returns handle associated with given object, or -1 if          * no mapping found.          */
DECL|method|lookup
name|int
name|lookup
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|index
init|=
name|hash
argument_list|(
name|obj
argument_list|)
operator|%
name|spine
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|spine
index|[
name|index
index|]
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|next
index|[
name|i
index|]
control|)
block|{
if|if
condition|(
name|objs
index|[
name|i
index|]
operator|==
name|obj
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**          * Resets table to its initial (empty) state.          */
DECL|method|clear
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|spine
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|objs
argument_list|,
literal|0
argument_list|,
name|size
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
comment|/**          * Returns the number of mappings currently in table.          */
DECL|method|size
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**          * Inserts mapping object -> handle mapping into table.  Assumes table          * is large enough to accommodate new mapping.          */
DECL|method|insert
specifier|private
name|void
name|insert
parameter_list|(
name|Object
name|obj
parameter_list|,
name|int
name|handle
parameter_list|)
block|{
name|int
name|index
init|=
name|hash
argument_list|(
name|obj
argument_list|)
operator|%
name|spine
operator|.
name|length
decl_stmt|;
name|objs
index|[
name|handle
index|]
operator|=
name|obj
expr_stmt|;
name|next
index|[
name|handle
index|]
operator|=
name|spine
index|[
name|index
index|]
expr_stmt|;
name|spine
index|[
name|index
index|]
operator|=
name|handle
expr_stmt|;
block|}
comment|/**          * Expands the hash "spine" -- equivalent to increasing the number of          * buckets in a conventional hash table.          */
DECL|method|growSpine
specifier|private
name|void
name|growSpine
parameter_list|()
block|{
name|spine
operator|=
operator|new
name|int
index|[
operator|(
name|spine
operator|.
name|length
operator|<<
literal|1
operator|)
operator|+
literal|1
index|]
expr_stmt|;
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|spine
operator|.
name|length
operator|*
name|loadFactor
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|spine
argument_list|,
operator|-
literal|1
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|insert
argument_list|(
name|objs
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Increases hash table capacity by lengthening entry arrays.          */
DECL|method|growEntries
specifier|private
name|void
name|growEntries
parameter_list|()
block|{
name|int
name|newLength
init|=
operator|(
name|next
operator|.
name|length
operator|<<
literal|1
operator|)
operator|+
literal|1
decl_stmt|;
name|int
index|[]
name|newNext
init|=
operator|new
name|int
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|next
argument_list|,
literal|0
argument_list|,
name|newNext
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|next
operator|=
name|newNext
expr_stmt|;
name|Object
index|[]
name|newObjs
init|=
operator|new
name|Object
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|objs
argument_list|,
literal|0
argument_list|,
name|newObjs
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|objs
operator|=
name|newObjs
expr_stmt|;
block|}
comment|/**          * Returns hash value for given object.          */
DECL|method|hash
specifier|private
name|int
name|hash
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|obj
argument_list|)
operator|&
literal|0x7FFFFFFF
return|;
block|}
block|}
block|}
end_class

end_unit

