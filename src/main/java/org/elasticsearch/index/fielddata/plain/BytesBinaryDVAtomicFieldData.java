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
name|AtomicReader
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
name|BinaryDocValues
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
name|store
operator|.
name|ByteArrayDataInput
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

begin_class
DECL|class|BytesBinaryDVAtomicFieldData
specifier|final
class|class
name|BytesBinaryDVAtomicFieldData
implements|implements
name|AtomicFieldData
argument_list|<
name|ScriptDocValues
argument_list|>
block|{
DECL|field|reader
specifier|private
specifier|final
name|AtomicReader
name|reader
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|BinaryDocValues
name|values
decl_stmt|;
DECL|method|BytesBinaryDVAtomicFieldData
name|BytesBinaryDVAtomicFieldData
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|BinaryDocValues
name|values
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
operator|==
literal|null
condition|?
name|BinaryDocValues
operator|.
name|EMPTY
else|:
name|values
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
name|reader
operator|.
name|maxDoc
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
name|Long
operator|.
name|MAX_VALUE
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
operator|-
literal|1
return|;
comment|// not exposed by Lucene
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
name|BytesValues
name|getBytesValues
parameter_list|(
name|boolean
name|needsHashes
parameter_list|)
block|{
return|return
operator|new
name|BytesValues
argument_list|(
literal|true
argument_list|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|get
argument_list|(
name|docId
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|readVInt
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|nextValue
parameter_list|()
block|{
specifier|final
name|int
name|length
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|scratch
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|scratch
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
return|return
name|scratch
return|;
block|}
block|}
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
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// no-op
block|}
block|}
end_class

end_unit

