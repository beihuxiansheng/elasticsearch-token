begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.strings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|data
operator|.
name|strings
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
name|search
operator|.
name|FieldComparator
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
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|FieldDataCache
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
comment|/**  *  */
end_comment

begin_class
DECL|class|StringFieldDataType
specifier|public
class|class
name|StringFieldDataType
implements|implements
name|FieldDataType
argument_list|<
name|StringFieldData
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newFieldComparatorSource
specifier|public
name|ExtendedFieldComparatorSource
name|newFieldComparatorSource
parameter_list|(
specifier|final
name|FieldDataCache
name|cache
parameter_list|,
specifier|final
name|String
name|missing
parameter_list|)
block|{
if|if
condition|(
name|missing
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Sorting on string type field does not support missing parameter"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ExtendedFieldComparatorSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FieldComparator
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StringOrdValFieldDataComparator
argument_list|(
name|numHits
argument_list|,
name|fieldname
argument_list|,
name|sortPos
argument_list|,
name|reversed
argument_list|,
name|cache
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortField
operator|.
name|Type
name|reducedType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|Type
operator|.
name|STRING
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|StringFieldData
name|load
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StringFieldData
operator|.
name|load
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

