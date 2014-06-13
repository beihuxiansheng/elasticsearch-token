begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|MultiDocValues
operator|.
name|OrdinalMap
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
name|index
operator|.
name|TermsEnum
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
name|settings
operator|.
name|Settings
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
name|Index
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
name|FieldDataType
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
name|plain
operator|.
name|AtomicFieldDataWithOrdinalsTermsEnum
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
name|mapper
operator|.
name|FieldMapper
import|;
end_import

begin_comment
comment|/**  * {@link org.elasticsearch.index.fielddata.IndexFieldData} impl based on global ordinals.  */
end_comment

begin_class
DECL|class|InternalGlobalOrdinalsIndexFieldData
specifier|final
class|class
name|InternalGlobalOrdinalsIndexFieldData
extends|extends
name|GlobalOrdinalsIndexFieldData
block|{
DECL|field|atomicReaders
specifier|private
specifier|final
name|Atomic
index|[]
name|atomicReaders
decl_stmt|;
DECL|method|InternalGlobalOrdinalsIndexFieldData
name|InternalGlobalOrdinalsIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|AtomicFieldData
operator|.
name|WithOrdinals
index|[]
name|segmentAfd
parameter_list|,
name|OrdinalMap
name|ordinalMap
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|memorySizeInBytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|atomicReaders
operator|=
operator|new
name|Atomic
index|[
name|segmentAfd
operator|.
name|length
index|]
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
name|segmentAfd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|atomicReaders
index|[
name|i
index|]
operator|=
operator|new
name|Atomic
argument_list|(
name|segmentAfd
index|[
name|i
index|]
argument_list|,
name|ordinalMap
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|AtomicFieldData
operator|.
name|WithOrdinals
name|load
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
return|return
name|atomicReaders
index|[
name|context
operator|.
name|ord
index|]
return|;
block|}
DECL|class|Atomic
specifier|private
specifier|final
class|class
name|Atomic
implements|implements
name|AtomicFieldData
operator|.
name|WithOrdinals
block|{
DECL|field|afd
specifier|private
specifier|final
name|WithOrdinals
name|afd
decl_stmt|;
DECL|field|ordinalMap
specifier|private
specifier|final
name|OrdinalMap
name|ordinalMap
decl_stmt|;
DECL|field|segmentIndex
specifier|private
specifier|final
name|int
name|segmentIndex
decl_stmt|;
DECL|method|Atomic
specifier|private
name|Atomic
parameter_list|(
name|WithOrdinals
name|afd
parameter_list|,
name|OrdinalMap
name|ordinalMap
parameter_list|,
name|int
name|segmentIndex
parameter_list|)
block|{
name|this
operator|.
name|afd
operator|=
name|afd
expr_stmt|;
name|this
operator|.
name|ordinalMap
operator|=
name|ordinalMap
expr_stmt|;
name|this
operator|.
name|segmentIndex
operator|=
name|segmentIndex
expr_stmt|;
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
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
name|values
init|=
name|afd
operator|.
name|getBytesValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|getMaxOrd
argument_list|()
operator|==
name|ordinalMap
operator|.
name|getValueCount
argument_list|()
condition|)
block|{
comment|// segment ordinals match global ordinals
return|return
name|values
return|;
block|}
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
index|[]
name|bytesValues
init|=
operator|new
name|BytesValues
operator|.
name|WithOrdinals
index|[
name|atomicReaders
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytesValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytesValues
index|[
name|i
index|]
operator|=
name|atomicReaders
index|[
name|i
index|]
operator|.
name|afd
operator|.
name|getBytesValues
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|GlobalOrdinalMapping
argument_list|(
name|ordinalMap
argument_list|,
name|bytesValues
argument_list|,
name|segmentIndex
argument_list|)
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
return|return
name|afd
operator|.
name|getMemorySizeInBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getScriptValues
specifier|public
name|ScriptDocValues
name|getScriptValues
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Script values not supported on global ordinals"
argument_list|)
throw|;
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
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{         }
block|}
block|}
end_class

end_unit

