begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
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
name|IntsRef
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
name|fst
operator|.
name|*
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
name|fst
operator|.
name|FST
operator|.
name|Arc
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
name|fst
operator|.
name|FST
operator|.
name|BytesReader
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
name|IntArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|AtomicFieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ScriptDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|EmptyOrdinals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|Ordinals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|Ordinals
operator|.
name|Docs
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
comment|/**  */
end_comment

begin_class
DECL|class|FSTBytesAtomicFieldData
specifier|public
class|class
name|FSTBytesAtomicFieldData
implements|implements
name|AtomicFieldData
operator|.
name|WithOrdinals
argument_list|<
name|ScriptDocValues
operator|.
name|Strings
argument_list|>
block|{
DECL|method|empty
specifier|public
specifier|static
name|FSTBytesAtomicFieldData
name|empty
parameter_list|()
block|{
return|return
operator|new
name|Empty
argument_list|()
return|;
block|}
comment|// 0 ordinal in values means no value (its null)
DECL|field|ordinals
specifier|protected
specifier|final
name|Ordinals
name|ordinals
decl_stmt|;
DECL|field|hashes
specifier|private
specifier|volatile
name|IntArray
name|hashes
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
decl_stmt|;
DECL|method|FSTBytesAtomicFieldData
specifier|public
name|FSTBytesAtomicFieldData
parameter_list|(
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
parameter_list|,
name|Ordinals
name|ordinals
parameter_list|)
block|{
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|ordinals
operator|.
name|isMultiValued
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberUniqueValues
specifier|public
name|long
name|getNumberUniqueValues
parameter_list|()
block|{
return|return
name|ordinals
operator|.
name|getMaxOrd
argument_list|()
operator|-
name|Ordinals
operator|.
name|MIN_ORDINAL
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|long
name|size
init|=
name|ordinals
operator|.
name|getMemorySizeInBytes
argument_list|()
decl_stmt|;
comment|// FST
name|size
operator|+=
name|fst
operator|==
literal|null
condition|?
literal|0
else|:
name|fst
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
name|BytesValues
operator|.
name|WithOrdinals
name|getBytesValues
parameter_list|(
name|boolean
name|needsHashes
parameter_list|)
block|{
assert|assert
name|fst
operator|!=
literal|null
assert|;
if|if
condition|(
name|needsHashes
condition|)
block|{
if|if
condition|(
name|hashes
operator|==
literal|null
condition|)
block|{
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
name|fstEnum
init|=
operator|new
name|BytesRefFSTEnum
argument_list|<>
argument_list|(
name|fst
argument_list|)
decl_stmt|;
name|IntArray
name|hashes
init|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|newIntArray
argument_list|(
name|ordinals
operator|.
name|getMaxOrd
argument_list|()
argument_list|)
decl_stmt|;
comment|// we don't store an ord 0 in the FST since we could have an empty string in there and FST don't support
comment|// empty strings twice. ie. them merge fails for long output.
name|hashes
operator|.
name|set
argument_list|(
literal|0
argument_list|,
operator|new
name|BytesRef
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|long
name|i
init|=
literal|1
init|,
name|maxOrd
init|=
name|ordinals
operator|.
name|getMaxOrd
argument_list|()
init|;
name|i
operator|<
name|maxOrd
condition|;
operator|++
name|i
control|)
block|{
name|hashes
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|fstEnum
operator|.
name|next
argument_list|()
operator|.
name|input
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
assert|assert
name|fstEnum
operator|.
name|next
argument_list|()
operator|==
literal|null
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Cannot happen"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|hashes
operator|=
name|hashes
expr_stmt|;
block|}
return|return
operator|new
name|HashedBytesValues
argument_list|(
name|fst
argument_list|,
name|ordinals
operator|.
name|ordinals
argument_list|()
argument_list|,
name|hashes
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BytesValues
argument_list|(
name|fst
argument_list|,
name|ordinals
operator|.
name|ordinals
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getScriptValues
specifier|public
name|ScriptDocValues
operator|.
name|Strings
name|getScriptValues
parameter_list|()
block|{
assert|assert
name|fst
operator|!=
literal|null
assert|;
return|return
operator|new
name|ScriptDocValues
operator|.
name|Strings
argument_list|(
name|getBytesValues
argument_list|(
literal|false
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|public
name|TermsEnum
name|getTermsEnum
parameter_list|()
block|{
return|return
operator|new
name|AtomicFieldDataWithOrdinalsTermsEnum
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|class|BytesValues
specifier|static
class|class
name|BytesValues
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|BytesValues
operator|.
name|WithOrdinals
block|{
DECL|field|fst
specifier|protected
specifier|final
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
decl_stmt|;
DECL|field|ordinals
specifier|protected
specifier|final
name|Ordinals
operator|.
name|Docs
name|ordinals
decl_stmt|;
comment|// per-thread resources
DECL|field|in
specifier|protected
specifier|final
name|BytesReader
name|in
decl_stmt|;
DECL|field|firstArc
specifier|protected
specifier|final
name|Arc
argument_list|<
name|Long
argument_list|>
name|firstArc
init|=
operator|new
name|Arc
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|scratchArc
specifier|protected
specifier|final
name|Arc
argument_list|<
name|Long
argument_list|>
name|scratchArc
init|=
operator|new
name|Arc
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|scratchInts
specifier|protected
specifier|final
name|IntsRef
name|scratchInts
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
DECL|method|BytesValues
name|BytesValues
parameter_list|(
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
parameter_list|,
name|Ordinals
operator|.
name|Docs
name|ordinals
parameter_list|)
block|{
name|super
argument_list|(
name|ordinals
argument_list|)
expr_stmt|;
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
name|in
operator|=
name|fst
operator|.
name|getBytesReader
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueByOrd
specifier|public
name|BytesRef
name|getValueByOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
assert|assert
name|ord
operator|!=
name|Ordinals
operator|.
name|MISSING_ORDINAL
assert|;
name|in
operator|.
name|setPosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fst
operator|.
name|getFirstArc
argument_list|(
name|firstArc
argument_list|)
expr_stmt|;
try|try
block|{
name|IntsRef
name|output
init|=
name|Util
operator|.
name|getByOutput
argument_list|(
name|fst
argument_list|,
name|ord
argument_list|,
name|in
argument_list|,
name|firstArc
argument_list|,
name|scratchArc
argument_list|,
name|scratchInts
argument_list|)
decl_stmt|;
name|scratch
operator|.
name|length
operator|=
name|scratch
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|scratch
operator|.
name|grow
argument_list|(
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
name|Util
operator|.
name|toBytesRef
argument_list|(
name|output
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//bogus
block|}
return|return
name|scratch
return|;
block|}
block|}
DECL|class|HashedBytesValues
specifier|static
specifier|final
class|class
name|HashedBytesValues
extends|extends
name|BytesValues
block|{
DECL|field|hashes
specifier|private
specifier|final
name|IntArray
name|hashes
decl_stmt|;
DECL|method|HashedBytesValues
name|HashedBytesValues
parameter_list|(
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
parameter_list|,
name|Docs
name|ordinals
parameter_list|,
name|IntArray
name|hashes
parameter_list|)
block|{
name|super
argument_list|(
name|fst
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashes
operator|=
name|hashes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|currentValueHash
specifier|public
name|int
name|currentValueHash
parameter_list|()
block|{
assert|assert
name|ordinals
operator|.
name|currentOrd
argument_list|()
operator|>=
literal|0
assert|;
return|return
name|hashes
operator|.
name|get
argument_list|(
name|ordinals
operator|.
name|currentOrd
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|Empty
specifier|final
specifier|static
class|class
name|Empty
extends|extends
name|FSTBytesAtomicFieldData
block|{
DECL|method|Empty
name|Empty
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|EmptyOrdinals
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
name|BytesValues
operator|.
name|WithOrdinals
name|getBytesValues
parameter_list|(
name|boolean
name|needsHashes
parameter_list|)
block|{
return|return
operator|new
name|EmptyByteValuesWithOrdinals
argument_list|(
name|ordinals
operator|.
name|ordinals
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getScriptValues
specifier|public
name|ScriptDocValues
operator|.
name|Strings
name|getScriptValues
parameter_list|()
block|{
return|return
name|ScriptDocValues
operator|.
name|EMPTY_STRINGS
return|;
block|}
block|}
block|}
end_class

end_unit

