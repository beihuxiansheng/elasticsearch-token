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
name|DirectoryReader
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
name|DocValues
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
name|LeafReaderContext
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|search
operator|.
name|SortField
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
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
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
name|IndexSettings
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
name|AbstractSortedDocValues
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
name|AtomicOrdinalsFieldData
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
name|IndexFieldData
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
name|IndexFieldDataCache
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
name|IndexOrdinalsFieldData
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
name|fieldcomparator
operator|.
name|BytesRefFieldComparatorSource
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
name|MappedFieldType
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
name|MapperService
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
name|TextFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|MultiValueMode
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Function
import|;
end_import

begin_class
DECL|class|ConstantIndexFieldData
specifier|public
class|class
name|ConstantIndexFieldData
extends|extends
name|AbstractIndexOrdinalsFieldData
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
implements|implements
name|IndexFieldData
operator|.
name|Builder
block|{
DECL|field|valueFunction
specifier|private
specifier|final
name|Function
argument_list|<
name|MapperService
argument_list|,
name|String
argument_list|>
name|valueFunction
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|Function
argument_list|<
name|MapperService
argument_list|,
name|String
argument_list|>
name|valueFunction
parameter_list|)
block|{
name|this
operator|.
name|valueFunction
operator|=
name|valueFunction
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|build
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
return|return
operator|new
name|ConstantIndexFieldData
argument_list|(
name|indexSettings
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
name|valueFunction
operator|.
name|apply
argument_list|(
name|mapperService
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|ConstantAtomicFieldData
specifier|private
specifier|static
class|class
name|ConstantAtomicFieldData
extends|extends
name|AbstractAtomicOrdinalsFieldData
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|ConstantAtomicFieldData
name|ConstantAtomicFieldData
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|DEFAULT_SCRIPT_FUNCTION
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOrdinalsValues
specifier|public
name|SortedSetDocValues
name|getOrdinalsValues
parameter_list|()
block|{
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
decl_stmt|;
specifier|final
name|SortedDocValues
name|sortedValues
init|=
operator|new
name|AbstractSortedDocValues
argument_list|()
block|{
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|ordValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|docID
operator|=
name|target
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
block|}
decl_stmt|;
return|return
operator|(
name|SortedSetDocValues
operator|)
name|DocValues
operator|.
name|singleton
argument_list|(
name|sortedValues
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
DECL|field|atomicFieldData
specifier|private
specifier|final
name|AtomicOrdinalsFieldData
name|atomicFieldData
decl_stmt|;
DECL|method|ConstantIndexFieldData
specifier|private
name|ConstantIndexFieldData
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|,
name|name
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|TextFieldMapper
operator|.
name|Defaults
operator|.
name|FIELDDATA_MIN_FREQUENCY
argument_list|,
name|TextFieldMapper
operator|.
name|Defaults
operator|.
name|FIELDDATA_MAX_FREQUENCY
argument_list|,
name|TextFieldMapper
operator|.
name|Defaults
operator|.
name|FIELDDATA_MIN_SEGMENT_SIZE
argument_list|)
expr_stmt|;
name|atomicFieldData
operator|=
operator|new
name|ConstantAtomicFieldData
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|load
specifier|public
specifier|final
name|AtomicOrdinalsFieldData
name|load
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
return|return
name|atomicFieldData
return|;
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|AtomicOrdinalsFieldData
name|loadDirect
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|atomicFieldData
return|;
block|}
annotation|@
name|Override
DECL|method|sortField
specifier|public
name|SortField
name|sortField
parameter_list|(
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|,
name|XFieldComparatorSource
operator|.
name|Nested
name|nested
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
specifier|final
name|XFieldComparatorSource
name|source
init|=
operator|new
name|BytesRefFieldComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortField
argument_list|(
name|getFieldName
argument_list|()
argument_list|,
name|source
argument_list|,
name|reverse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|loadGlobal
specifier|public
name|IndexOrdinalsFieldData
name|loadGlobal
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|localGlobalDirect
specifier|public
name|IndexOrdinalsFieldData
name|localGlobalDirect
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|loadGlobal
argument_list|(
name|indexReader
argument_list|)
return|;
block|}
block|}
end_class

end_unit

