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
name|PagedBytes
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
name|MonotonicAppendingLongBuffer
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
name|BytesValues
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
name|Ordinals
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|PagedBytesAtomicFieldData
specifier|public
class|class
name|PagedBytesAtomicFieldData
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
DECL|field|bytes
specifier|private
specifier|final
name|PagedBytes
operator|.
name|Reader
name|bytes
decl_stmt|;
DECL|field|termOrdToBytesOffset
specifier|private
specifier|final
name|MonotonicAppendingLongBuffer
name|termOrdToBytesOffset
decl_stmt|;
DECL|field|ordinals
specifier|protected
specifier|final
name|Ordinals
name|ordinals
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|readerBytesSize
specifier|private
specifier|final
name|long
name|readerBytesSize
decl_stmt|;
DECL|method|PagedBytesAtomicFieldData
specifier|public
name|PagedBytesAtomicFieldData
parameter_list|(
name|PagedBytes
operator|.
name|Reader
name|bytes
parameter_list|,
name|long
name|readerBytesSize
parameter_list|,
name|MonotonicAppendingLongBuffer
name|termOrdToBytesOffset
parameter_list|,
name|Ordinals
name|ordinals
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|termOrdToBytesOffset
operator|=
name|termOrdToBytesOffset
expr_stmt|;
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
name|this
operator|.
name|readerBytesSize
operator|=
name|readerBytesSize
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
comment|// PackedBytes
name|size
operator|+=
name|readerBytesSize
expr_stmt|;
comment|// PackedInts
name|size
operator|+=
name|termOrdToBytesOffset
operator|.
name|ramBytesUsed
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
parameter_list|()
block|{
return|return
name|ordinals
operator|.
name|ordinals
argument_list|(
operator|new
name|ValuesHolder
argument_list|(
name|bytes
argument_list|,
name|termOrdToBytesOffset
argument_list|)
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
operator|new
name|ScriptDocValues
operator|.
name|Strings
argument_list|(
name|getBytesValues
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ValuesHolder
specifier|private
specifier|static
class|class
name|ValuesHolder
implements|implements
name|Ordinals
operator|.
name|ValuesHolder
block|{
DECL|field|scratch
specifier|private
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|PagedBytes
operator|.
name|Reader
name|bytes
decl_stmt|;
DECL|field|termOrdToBytesOffset
specifier|private
specifier|final
name|MonotonicAppendingLongBuffer
name|termOrdToBytesOffset
decl_stmt|;
DECL|method|ValuesHolder
name|ValuesHolder
parameter_list|(
name|PagedBytes
operator|.
name|Reader
name|bytes
parameter_list|,
name|MonotonicAppendingLongBuffer
name|termOrdToBytesOffset
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|termOrdToBytesOffset
operator|=
name|termOrdToBytesOffset
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
name|BytesValues
operator|.
name|WithOrdinals
operator|.
name|MISSING_ORDINAL
assert|;
name|bytes
operator|.
name|fill
argument_list|(
name|scratch
argument_list|,
name|termOrdToBytesOffset
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
block|}
block|}
block|}
end_class

end_unit

