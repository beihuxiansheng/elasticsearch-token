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
name|packed
operator|.
name|PackedInts
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
name|RamUsage
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SinglePackedOrdinals
specifier|public
class|class
name|SinglePackedOrdinals
implements|implements
name|Ordinals
block|{
comment|// ordinals with value 0 indicates no value
DECL|field|reader
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
decl_stmt|;
DECL|field|numOrds
specifier|private
specifier|final
name|int
name|numOrds
decl_stmt|;
DECL|field|maxOrd
specifier|private
specifier|final
name|int
name|maxOrd
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SinglePackedOrdinals
specifier|public
name|SinglePackedOrdinals
parameter_list|(
name|PackedInts
operator|.
name|Reader
name|reader
parameter_list|,
name|int
name|numOrds
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|numOrds
operator|=
name|numOrds
expr_stmt|;
name|this
operator|.
name|maxOrd
operator|=
name|numOrds
operator|+
literal|1
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
name|reader
operator|.
name|hasArray
argument_list|()
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
if|if
condition|(
name|reader
operator|.
name|hasArray
argument_list|()
condition|)
block|{
return|return
name|reader
operator|.
name|getArray
argument_list|()
return|;
block|}
return|return
name|reader
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
name|size
operator|=
name|RamUsage
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
name|reader
operator|.
name|ramBytesUsed
argument_list|()
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
literal|false
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
name|reader
operator|.
name|size
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
name|numOrds
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
name|int
name|getMaxOrd
parameter_list|()
block|{
return|return
name|maxOrd
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
name|reader
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
name|SinglePackedOrdinals
name|parent
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
decl_stmt|;
DECL|field|intsScratch
specifier|private
specifier|final
name|IntsRef
name|intsScratch
init|=
operator|new
name|IntsRef
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|iter
specifier|private
specifier|final
name|SingleValueIter
name|iter
init|=
operator|new
name|SingleValueIter
argument_list|()
decl_stmt|;
DECL|method|Docs
specifier|public
name|Docs
parameter_list|(
name|SinglePackedOrdinals
name|parent
parameter_list|,
name|PackedInts
operator|.
name|Reader
name|reader
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
name|reader
operator|=
name|reader
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
DECL|method|getMaxOrd
specifier|public
name|int
name|getMaxOrd
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getMaxOrd
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
literal|false
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
operator|(
name|int
operator|)
name|reader
operator|.
name|get
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOrds
specifier|public
name|IntsRef
name|getOrds
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
specifier|final
name|int
name|ordinal
init|=
operator|(
name|int
operator|)
name|reader
operator|.
name|get
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordinal
operator|==
literal|0
condition|)
block|{
name|intsScratch
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|intsScratch
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|intsScratch
operator|.
name|length
operator|=
literal|1
expr_stmt|;
name|intsScratch
operator|.
name|ints
index|[
literal|0
index|]
operator|=
name|ordinal
expr_stmt|;
block|}
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
operator|(
name|int
operator|)
name|reader
operator|.
name|get
argument_list|(
name|docId
argument_list|)
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
name|proc
operator|.
name|onOrdinal
argument_list|(
name|docId
argument_list|,
operator|(
name|int
operator|)
name|reader
operator|.
name|get
argument_list|(
name|docId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

