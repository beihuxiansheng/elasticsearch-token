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
name|AtomicReaderContext
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
name|fieldcomparator
operator|.
name|SortMode
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

begin_class
DECL|class|SortedSetDVBytesIndexFieldData
specifier|public
class|class
name|SortedSetDVBytesIndexFieldData
extends|extends
name|DocValuesIndexFieldData
implements|implements
name|IndexFieldData
operator|.
name|WithOrdinals
argument_list|<
name|SortedSetDVBytesAtomicFieldData
argument_list|>
block|{
DECL|method|SortedSetDVBytesIndexFieldData
specifier|public
name|SortedSetDVBytesIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|Names
name|fieldNames
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|valuesOrdered
specifier|public
name|boolean
name|valuesOrdered
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
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
name|SortMode
name|sortMode
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
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|SortedSetDVBytesAtomicFieldData
name|load
parameter_list|(
name|AtomicReaderContext
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
name|SortedSetDVBytesAtomicFieldData
name|loadDirect
parameter_list|(
name|AtomicReaderContext
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
block|}
end_class

end_unit

