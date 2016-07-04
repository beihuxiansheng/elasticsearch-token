begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.bytes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
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
name|util
operator|.
name|Accountable
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
name|BytesRefIterator
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|ToIntBiFunction
import|;
end_import

begin_comment
comment|/**  * A reference to bytes.  */
end_comment

begin_class
DECL|class|BytesReference
specifier|public
specifier|abstract
class|class
name|BytesReference
implements|implements
name|Accountable
implements|,
name|Comparable
argument_list|<
name|BytesReference
argument_list|>
block|{
DECL|field|hash
specifier|private
name|Integer
name|hash
init|=
literal|null
decl_stmt|;
comment|// we cache the hash of this reference since it can be quite costly to re-calculated it
comment|/**      * Returns the byte at the specified index. Need to be between 0 and length.      */
DECL|method|get
specifier|public
specifier|abstract
name|byte
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * The length.      */
DECL|method|length
specifier|public
specifier|abstract
name|int
name|length
parameter_list|()
function_decl|;
comment|/**      * Slice the bytes from the<tt>from</tt> index up to<tt>length</tt>.      */
DECL|method|slice
specifier|public
specifier|abstract
name|BytesReference
name|slice
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**      * A stream input of the bytes.      */
DECL|method|streamInput
specifier|public
name|StreamInput
name|streamInput
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
name|toBytesRef
argument_list|()
decl_stmt|;
return|return
name|StreamInput
operator|.
name|wrap
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**      * Writes the bytes directly to the output stream.      */
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRefIterator
name|iterator
init|=
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|ref
decl_stmt|;
while|while
condition|(
operator|(
name|ref
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Interprets the referenced bytes as UTF8 bytes, returning the resulting string      */
DECL|method|utf8ToString
specifier|public
name|String
name|utf8ToString
parameter_list|()
block|{
return|return
name|toBytesRef
argument_list|()
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
comment|/**      * Converts to Lucene BytesRef.      */
DECL|method|toBytesRef
specifier|public
specifier|abstract
name|BytesRef
name|toBytesRef
parameter_list|()
function_decl|;
comment|/**      * Returns a BytesRefIterator for this BytesReference. This method allows      * access to the internal pages of this reference without copying them. Use with care!      * @see BytesRefIterator      */
DECL|method|iterator
specifier|public
name|BytesRefIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|BytesRefIterator
argument_list|()
block|{
name|BytesRef
name|ref
init|=
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|toBytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|r
init|=
name|ref
decl_stmt|;
name|ref
operator|=
literal|null
expr_stmt|;
comment|// only return it once...
return|return
name|r
return|;
block|}
block|}
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
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|BytesReference
condition|)
block|{
specifier|final
name|BytesReference
name|otherRef
init|=
operator|(
name|BytesReference
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|length
argument_list|()
operator|!=
name|otherRef
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|compareIterators
argument_list|(
name|this
argument_list|,
name|otherRef
argument_list|,
operator|(
name|a
operator|,
name|b
operator|)
operator|->
name|a
operator|.
name|bytesEquals
argument_list|(
name|b
argument_list|)
condition|?
literal|0
else|:
literal|1
comment|// this is a call to BytesRef#bytesEquals - this method is the hot one in the comparison
argument_list|)
operator|==
literal|0
return|;
block|}
return|return
literal|false
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
if|if
condition|(
name|hash
operator|==
literal|null
condition|)
block|{
specifier|final
name|BytesRefIterator
name|iterator
init|=
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|ref
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|ref
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ref
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|ref
operator|.
name|bytes
index|[
name|ref
operator|.
name|offset
operator|+
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"wont happen"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
return|return
name|hash
operator|=
name|result
return|;
block|}
else|else
block|{
return|return
name|hash
operator|.
name|intValue
argument_list|()
return|;
block|}
block|}
comment|/**      * Returns a compact array from the given BytesReference. The returned array won't be copied unless necessary. If you need      * to modify the returned array use<tt>BytesRef.deepCopyOf(reference.toBytesRef()</tt> instead      */
DECL|method|toBytes
specifier|public
specifier|static
name|byte
index|[]
name|toBytes
parameter_list|(
name|BytesReference
name|reference
parameter_list|)
block|{
specifier|final
name|BytesRef
name|bytesRef
init|=
name|reference
operator|.
name|toBytesRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytesRef
operator|.
name|offset
operator|==
literal|0
operator|&&
name|bytesRef
operator|.
name|length
operator|==
name|bytesRef
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
return|return
name|bytesRef
operator|.
name|bytes
return|;
block|}
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytesRef
argument_list|)
operator|.
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|BytesReference
name|other
parameter_list|)
block|{
return|return
name|compareIterators
argument_list|(
name|this
argument_list|,
name|other
argument_list|,
parameter_list|(
name|a
parameter_list|,
name|b
parameter_list|)
lambda|->
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Compares the two references using the given int function.      */
DECL|method|compareIterators
specifier|private
specifier|static
name|int
name|compareIterators
parameter_list|(
specifier|final
name|BytesReference
name|a
parameter_list|,
specifier|final
name|BytesReference
name|b
parameter_list|,
specifier|final
name|ToIntBiFunction
argument_list|<
name|BytesRef
argument_list|,
name|BytesRef
argument_list|>
name|f
parameter_list|)
block|{
try|try
block|{
comment|// we use the iterators since it's a 0-copy comparison where possible!
specifier|final
name|long
name|lengthToCompare
init|=
name|Math
operator|.
name|min
argument_list|(
name|a
operator|.
name|length
argument_list|()
argument_list|,
name|b
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BytesRefIterator
name|aIter
init|=
name|a
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|BytesRefIterator
name|bIter
init|=
name|b
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|aRef
init|=
name|aIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|BytesRef
name|bRef
init|=
name|bIter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|aRef
operator|!=
literal|null
operator|&&
name|bRef
operator|!=
literal|null
condition|)
block|{
comment|// do we have any data?
name|aRef
operator|=
name|aRef
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// we clone since we modify the offsets and length in the iteration below
name|bRef
operator|=
name|bRef
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|aRef
operator|.
name|length
operator|==
name|a
operator|.
name|length
argument_list|()
operator|&&
name|bRef
operator|.
name|length
operator|==
name|b
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// is it only one array slice we are comparing?
return|return
name|f
operator|.
name|applyAsInt
argument_list|(
name|aRef
argument_list|,
name|bRef
argument_list|)
return|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lengthToCompare
condition|;
control|)
block|{
if|if
condition|(
name|aRef
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|aRef
operator|=
name|aIter
operator|.
name|next
argument_list|()
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// must be non null otherwise we have a bug
block|}
if|if
condition|(
name|bRef
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|bRef
operator|=
name|bIter
operator|.
name|next
argument_list|()
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// must be non null otherwise we have a bug
block|}
specifier|final
name|int
name|aLength
init|=
name|aRef
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|bLength
init|=
name|bRef
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|Math
operator|.
name|min
argument_list|(
name|aLength
argument_list|,
name|bLength
argument_list|)
decl_stmt|;
comment|// shrink to the same length and use the fast compare in lucene
name|aRef
operator|.
name|length
operator|=
name|bRef
operator|.
name|length
operator|=
name|length
expr_stmt|;
comment|// now we move to the fast comparison - this is the hot part of the loop
name|int
name|diff
init|=
name|f
operator|.
name|applyAsInt
argument_list|(
name|aRef
argument_list|,
name|bRef
argument_list|)
decl_stmt|;
name|aRef
operator|.
name|length
operator|=
name|aLength
expr_stmt|;
name|bRef
operator|.
name|length
operator|=
name|bLength
expr_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
name|advance
argument_list|(
name|aRef
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|advance
argument_list|(
name|bRef
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|i
operator|+=
name|length
expr_stmt|;
block|}
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|a
operator|.
name|length
argument_list|()
operator|-
name|b
operator|.
name|length
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"can not happen"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|advance
specifier|private
specifier|static
name|void
name|advance
parameter_list|(
specifier|final
name|BytesRef
name|ref
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
block|{
assert|assert
name|ref
operator|.
name|length
operator|>=
name|length
operator|:
literal|" ref.length: "
operator|+
name|ref
operator|.
name|length
operator|+
literal|" length: "
operator|+
name|length
assert|;
assert|assert
name|ref
operator|.
name|offset
operator|+
name|length
operator|<
name|ref
operator|.
name|bytes
operator|.
name|length
operator|||
operator|(
name|ref
operator|.
name|offset
operator|+
name|length
operator|==
name|ref
operator|.
name|bytes
operator|.
name|length
operator|&&
name|ref
operator|.
name|length
operator|-
name|length
operator|==
literal|0
operator|)
operator|:
literal|"offset: "
operator|+
name|ref
operator|.
name|offset
operator|+
literal|" ref.bytes.length: "
operator|+
name|ref
operator|.
name|bytes
operator|.
name|length
operator|+
literal|" length: "
operator|+
name|length
operator|+
literal|" ref.length: "
operator|+
name|ref
operator|.
name|length
assert|;
name|ref
operator|.
name|length
operator|-=
name|length
expr_stmt|;
name|ref
operator|.
name|offset
operator|+=
name|length
expr_stmt|;
block|}
block|}
end_class

end_unit

