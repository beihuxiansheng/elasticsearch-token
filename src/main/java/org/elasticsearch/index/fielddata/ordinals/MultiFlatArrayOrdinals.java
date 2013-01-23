begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.ordinals
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
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
name|RamUsage
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
name|util
operator|.
name|IntArrayRef
import|;
end_import

begin_comment
comment|/**  * "Flat" multi valued ordinals, the first level array size is as the maximum  * values a docId has. Ordinals are populated in order from the first flat array  * value to the next.  */
end_comment

begin_class
DECL|class|MultiFlatArrayOrdinals
specifier|public
class|class
name|MultiFlatArrayOrdinals
implements|implements
name|Ordinals
block|{
DECL|field|intArrayRefCache
specifier|private
name|ThreadLocal
argument_list|<
name|IntArrayRef
argument_list|>
name|intArrayRefCache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|IntArrayRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|IntArrayRef
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|IntArrayRef
argument_list|(
operator|new
name|int
index|[
name|ordinals
operator|.
name|length
index|]
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// ordinals with value 0 indicates no value
DECL|field|ordinals
specifier|private
specifier|final
name|int
index|[]
index|[]
name|ordinals
decl_stmt|;
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|numOrds
specifier|private
specifier|final
name|int
name|numOrds
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|MultiFlatArrayOrdinals
specifier|public
name|MultiFlatArrayOrdinals
parameter_list|(
name|int
index|[]
index|[]
name|ordinals
parameter_list|,
name|int
name|numOrds
parameter_list|)
block|{
assert|assert
name|ordinals
operator|.
name|length
operator|>
literal|0
assert|;
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|ordinals
index|[
literal|0
index|]
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|numOrds
operator|=
name|numOrds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasSingleArrayBackingStorage
specifier|public
name|boolean
name|hasSingleArrayBackingStorage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getBackingStorage
specifier|public
name|Object
name|getBackingStorage
parameter_list|()
block|{
return|return
name|ordinals
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
literal|0
decl_stmt|;
name|size
operator|+=
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
expr_stmt|;
comment|// for the top level array
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
name|size
operator|+=
name|RamUsage
operator|.
name|NUM_BYTES_INT
operator|*
name|ordinal
operator|.
name|length
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
expr_stmt|;
block|}
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
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|getNumOrds
specifier|public
name|int
name|getNumOrds
parameter_list|()
block|{
return|return
name|numOrds
return|;
block|}
annotation|@
name|Override
DECL|method|ordinals
specifier|public
name|Docs
name|ordinals
parameter_list|()
block|{
return|return
operator|new
name|Docs
argument_list|(
name|this
argument_list|,
name|ordinals
argument_list|,
name|intArrayRefCache
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Docs
specifier|public
specifier|static
class|class
name|Docs
implements|implements
name|Ordinals
operator|.
name|Docs
block|{
DECL|field|parent
specifier|private
specifier|final
name|MultiFlatArrayOrdinals
name|parent
decl_stmt|;
DECL|field|ordinals
specifier|private
specifier|final
name|int
index|[]
index|[]
name|ordinals
decl_stmt|;
DECL|field|iter
specifier|private
specifier|final
name|IterImpl
name|iter
decl_stmt|;
DECL|field|intsScratch
specifier|private
specifier|final
name|IntArrayRef
name|intsScratch
decl_stmt|;
DECL|method|Docs
specifier|public
name|Docs
parameter_list|(
name|MultiFlatArrayOrdinals
name|parent
parameter_list|,
name|int
index|[]
index|[]
name|ordinals
parameter_list|,
name|IntArrayRef
name|intsScratch
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
name|this
operator|.
name|iter
operator|=
operator|new
name|IterImpl
argument_list|(
name|ordinals
argument_list|)
expr_stmt|;
name|this
operator|.
name|intsScratch
operator|=
name|intsScratch
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ordinals
specifier|public
name|Ordinals
name|ordinals
parameter_list|()
block|{
return|return
name|this
operator|.
name|parent
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getNumDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumOrds
specifier|public
name|int
name|getNumOrds
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getNumOrds
argument_list|()
return|;
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
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|ordinals
index|[
literal|0
index|]
index|[
name|docId
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getOrds
specifier|public
name|IntArrayRef
name|getOrds
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|intsScratch
operator|.
name|end
operator|=
literal|0
expr_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
name|ordinals
index|[
name|i
index|]
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|ordinal
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
return|return
name|IntArrayRef
operator|.
name|EMPTY
return|;
break|break;
block|}
name|intsScratch
operator|.
name|values
index|[
name|i
index|]
operator|=
name|ordinal
expr_stmt|;
block|}
name|intsScratch
operator|.
name|end
operator|=
name|i
expr_stmt|;
return|return
name|intsScratch
return|;
block|}
annotation|@
name|Override
DECL|method|getIter
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|iter
operator|.
name|reset
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|forEachOrdinalInDoc
specifier|public
name|void
name|forEachOrdinalInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|OrdinalInDocProc
name|proc
parameter_list|)
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
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
name|ordinals
index|[
name|i
index|]
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|ordinal
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
name|proc
operator|.
name|onOrdinal
argument_list|(
name|docId
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
name|proc
operator|.
name|onOrdinal
argument_list|(
name|docId
argument_list|,
name|ordinal
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|IterImpl
specifier|public
specifier|static
class|class
name|IterImpl
implements|implements
name|Docs
operator|.
name|Iter
block|{
DECL|field|ordinals
specifier|private
specifier|final
name|int
index|[]
index|[]
name|ordinals
decl_stmt|;
DECL|field|docId
specifier|private
name|int
name|docId
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
decl_stmt|;
DECL|method|IterImpl
specifier|public
name|IterImpl
parameter_list|(
name|int
index|[]
index|[]
name|ordinals
parameter_list|)
block|{
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|IterImpl
name|reset
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|i
operator|=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
if|if
condition|(
name|i
operator|>=
name|ordinals
operator|.
name|length
condition|)
return|return
literal|0
return|;
return|return
name|ordinals
index|[
name|i
operator|++
index|]
index|[
name|docId
index|]
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

