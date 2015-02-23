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
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|*
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
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|fielddata
operator|.
name|ordinals
operator|.
name|GlobalOrdinalsBuilder
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
operator|.
name|Names
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

begin_class
DECL|class|SortedSetDVOrdinalsIndexFieldData
specifier|public
class|class
name|SortedSetDVOrdinalsIndexFieldData
extends|extends
name|DocValuesIndexFieldData
implements|implements
name|IndexOrdinalsFieldData
block|{
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|IndexFieldDataCache
name|cache
decl_stmt|;
DECL|field|breakerService
specifier|private
specifier|final
name|CircuitBreakerService
name|breakerService
decl_stmt|;
DECL|method|SortedSetDVOrdinalsIndexFieldData
specifier|public
name|SortedSetDVOrdinalsIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|Names
name|fieldNames
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|breakerService
operator|=
name|breakerService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|comparatorSource
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldData
operator|.
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
name|Object
name|missingValue
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|,
name|Nested
name|nested
parameter_list|)
block|{
return|return
operator|new
name|BytesRefFieldComparatorSource
argument_list|(
operator|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
operator|)
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|AtomicOrdinalsFieldData
name|load
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|SortedSetDVBytesAtomicFieldData
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|)
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
name|load
argument_list|(
name|context
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
name|IndexReader
name|indexReader
parameter_list|)
block|{
if|if
condition|(
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
comment|// ordinals are already global
return|return
name|this
return|;
block|}
try|try
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|indexReader
argument_list|,
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ElasticsearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticsearchException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|localGlobalDirect
specifier|public
name|IndexOrdinalsFieldData
name|localGlobalDirect
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|GlobalOrdinalsBuilder
operator|.
name|build
argument_list|(
name|indexReader
argument_list|,
name|this
argument_list|,
name|indexSettings
argument_list|,
name|breakerService
argument_list|,
name|logger
argument_list|)
return|;
block|}
block|}
end_class

end_unit

