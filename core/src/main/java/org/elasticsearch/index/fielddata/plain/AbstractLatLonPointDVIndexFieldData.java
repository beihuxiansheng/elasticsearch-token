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
name|document
operator|.
name|LatLonDocValuesField
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
name|DocValuesType
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
name|FieldInfo
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
name|LeafReader
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
name|AtomicGeoPointFieldData
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
name|IndexGeoPointFieldData
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

begin_class
DECL|class|AbstractLatLonPointDVIndexFieldData
specifier|public
specifier|abstract
class|class
name|AbstractLatLonPointDVIndexFieldData
extends|extends
name|DocValuesIndexFieldData
implements|implements
name|IndexGeoPointFieldData
block|{
DECL|method|AbstractLatLonPointDVIndexFieldData
name|AbstractLatLonPointDVIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can't sort on geo_point field without using specific sorting feature, like geo_distance"
argument_list|)
throw|;
block|}
DECL|class|LatLonPointDVIndexFieldData
specifier|public
specifier|static
class|class
name|LatLonPointDVIndexFieldData
extends|extends
name|AbstractLatLonPointDVIndexFieldData
block|{
DECL|method|LatLonPointDVIndexFieldData
specifier|public
name|LatLonPointDVIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|AtomicGeoPointFieldData
name|load
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|FieldInfo
name|info
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|checkCompatible
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LatLonPointDVAtomicFieldData
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|AtomicGeoPointFieldData
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
comment|/** helper: checks a fieldinfo and throws exception if its definitely not a LatLonDocValuesField */
DECL|method|checkCompatible
specifier|static
name|void
name|checkCompatible
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
comment|// dv properties could be "unset", if you e.g. used only StoredField with this same name in the segment.
if|if
condition|(
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|NONE
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|LatLonDocValuesField
operator|.
name|TYPE
operator|.
name|docValuesType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" was indexed with docValuesType="
operator|+
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|+
literal|" but this type has docValuesType="
operator|+
name|LatLonDocValuesField
operator|.
name|TYPE
operator|.
name|docValuesType
argument_list|()
operator|+
literal|", is the field really a LatLonDocValuesField?"
argument_list|)
throw|;
block|}
block|}
block|}
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
comment|// ignore breaker
return|return
operator|new
name|LatLonPointDVIndexFieldData
argument_list|(
name|indexSettings
operator|.
name|getIndex
argument_list|()
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

