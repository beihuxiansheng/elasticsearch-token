begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|ElasticsearchIllegalArgumentException
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
name|collect
operator|.
name|MapBuilder
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
name|collect
operator|.
name|Tuple
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
name|inject
operator|.
name|Inject
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
name|ImmutableSettings
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|AbstractIndexComponent
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
name|fielddata
operator|.
name|ordinals
operator|.
name|InternalGlobalOrdinalsBuilder
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
name|mapper
operator|.
name|FieldMapper
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
name|internal
operator|.
name|ParentFieldMapper
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
name|service
operator|.
name|IndexService
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
name|settings
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
name|indices
operator|.
name|fielddata
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
name|indices
operator|.
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCache
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
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCacheListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IndexFieldDataService
specifier|public
class|class
name|IndexFieldDataService
extends|extends
name|AbstractIndexComponent
block|{
DECL|field|DISABLED_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|DISABLED_FORMAT
init|=
literal|"disabled"
decl_stmt|;
DECL|field|DOC_VALUES_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|DOC_VALUES_FORMAT
init|=
literal|"doc_values"
decl_stmt|;
DECL|field|ARRAY_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|ARRAY_FORMAT
init|=
literal|"array"
decl_stmt|;
DECL|field|PAGED_BYTES_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|PAGED_BYTES_FORMAT
init|=
literal|"paged_bytes"
decl_stmt|;
DECL|field|FST_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|FST_FORMAT
init|=
literal|"fst"
decl_stmt|;
DECL|field|COMPRESSED_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|COMPRESSED_FORMAT
init|=
literal|"compressed"
decl_stmt|;
DECL|field|buildersByType
specifier|private
specifier|final
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|IndexFieldData
operator|.
name|Builder
argument_list|>
name|buildersByType
decl_stmt|;
DECL|field|docValuesBuildersByType
specifier|private
specifier|final
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|IndexFieldData
operator|.
name|Builder
argument_list|>
name|docValuesBuildersByType
decl_stmt|;
DECL|field|buildersByTypeAndFormat
specifier|private
specifier|final
specifier|static
name|ImmutableMap
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|,
name|IndexFieldData
operator|.
name|Builder
argument_list|>
name|buildersByTypeAndFormat
decl_stmt|;
DECL|field|circuitBreakerService
specifier|private
specifier|final
name|CircuitBreakerService
name|circuitBreakerService
decl_stmt|;
DECL|field|indicesFieldDataCacheListener
specifier|private
specifier|final
name|IndicesFieldDataCacheListener
name|indicesFieldDataCacheListener
decl_stmt|;
static|static
block|{
name|buildersByType
operator|=
name|MapBuilder
operator|.
expr|<
name|String
operator|,
name|IndexFieldData
operator|.
name|Builder
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"string"
argument_list|,
operator|new
name|PagedBytesIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"float"
argument_list|,
operator|new
name|FloatArrayIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"double"
argument_list|,
operator|new
name|DoubleArrayIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"byte"
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|BYTE
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"short"
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|SHORT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|INT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"long"
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|LONG
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"geo_point"
argument_list|,
operator|new
name|GeoPointDoubleArrayIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|ParentChildIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"binary"
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|docValuesBuildersByType
operator|=
name|MapBuilder
operator|.
expr|<
name|String
operator|,
name|IndexFieldData
operator|.
name|Builder
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"string"
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"float"
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|FLOAT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"double"
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|DOUBLE
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"byte"
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|BYTE
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"short"
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|SHORT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|INT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"long"
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|LONG
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"geo_point"
argument_list|,
operator|new
name|GeoPointBinaryDVIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"binary"
argument_list|,
operator|new
name|BytesBinaryDVIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|buildersByTypeAndFormat
operator|=
name|MapBuilder
operator|.
expr|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|,
name|IndexFieldData
operator|.
name|Builder
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"string"
argument_list|,
name|PAGED_BYTES_FORMAT
argument_list|)
argument_list|,
operator|new
name|PagedBytesIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"string"
argument_list|,
name|FST_FORMAT
argument_list|)
argument_list|,
operator|new
name|FSTBytesIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"string"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"string"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"float"
argument_list|,
name|ARRAY_FORMAT
argument_list|)
argument_list|,
operator|new
name|FloatArrayIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"float"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|FLOAT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"float"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"double"
argument_list|,
name|ARRAY_FORMAT
argument_list|)
argument_list|,
operator|new
name|DoubleArrayIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"double"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|DOUBLE
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"double"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"byte"
argument_list|,
name|ARRAY_FORMAT
argument_list|)
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|BYTE
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"byte"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|BYTE
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"byte"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"short"
argument_list|,
name|ARRAY_FORMAT
argument_list|)
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|SHORT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"short"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|SHORT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"short"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"int"
argument_list|,
name|ARRAY_FORMAT
argument_list|)
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|INT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"int"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|INT
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"int"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"long"
argument_list|,
name|ARRAY_FORMAT
argument_list|)
argument_list|,
operator|new
name|PackedArrayIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|setNumericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|LONG
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"long"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|DocValuesIndexFieldData
operator|.
name|Builder
argument_list|()
operator|.
name|numericType
argument_list|(
name|IndexNumericFieldData
operator|.
name|NumericType
operator|.
name|LONG
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"long"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"geo_point"
argument_list|,
name|ARRAY_FORMAT
argument_list|)
argument_list|,
operator|new
name|GeoPointDoubleArrayIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"geo_point"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|GeoPointBinaryDVIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"geo_point"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"geo_point"
argument_list|,
name|COMPRESSED_FORMAT
argument_list|)
argument_list|,
operator|new
name|GeoPointCompressedIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"binary"
argument_list|,
name|DOC_VALUES_FORMAT
argument_list|)
argument_list|,
operator|new
name|BytesBinaryDVIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
literal|"binary"
argument_list|,
name|DISABLED_FORMAT
argument_list|)
argument_list|,
operator|new
name|DisabledIndexFieldData
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|field|indicesFieldDataCache
specifier|private
specifier|final
name|IndicesFieldDataCache
name|indicesFieldDataCache
decl_stmt|;
DECL|field|loadedFieldData
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|IndexFieldData
argument_list|<
name|?
argument_list|>
argument_list|>
name|loadedFieldData
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|fieldDataCaches
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexFieldDataCache
argument_list|>
name|fieldDataCaches
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|// no need for concurrency support, always used under lock
DECL|field|indexService
name|IndexService
name|indexService
decl_stmt|;
comment|// public for testing
DECL|method|IndexFieldDataService
specifier|public
name|IndexFieldDataService
parameter_list|(
name|Index
name|index
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
operator|new
name|IndicesFieldDataCache
argument_list|(
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
operator|new
name|IndicesFieldDataCacheListener
argument_list|(
name|circuitBreakerService
argument_list|)
argument_list|)
argument_list|,
name|circuitBreakerService
argument_list|,
operator|new
name|IndicesFieldDataCacheListener
argument_list|(
name|circuitBreakerService
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// public for testing
DECL|method|IndexFieldDataService
specifier|public
name|IndexFieldDataService
parameter_list|(
name|Index
name|index
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|IndicesFieldDataCache
name|indicesFieldDataCache
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
name|indicesFieldDataCache
argument_list|,
name|circuitBreakerService
argument_list|,
operator|new
name|IndicesFieldDataCacheListener
argument_list|(
name|circuitBreakerService
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|IndexFieldDataService
specifier|public
name|IndexFieldDataService
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndicesFieldDataCache
name|indicesFieldDataCache
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|IndicesFieldDataCacheListener
name|indicesFieldDataCacheListener
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesFieldDataCache
operator|=
name|indicesFieldDataCache
expr_stmt|;
name|this
operator|.
name|circuitBreakerService
operator|=
name|circuitBreakerService
expr_stmt|;
name|this
operator|.
name|indicesFieldDataCacheListener
operator|=
name|indicesFieldDataCacheListener
expr_stmt|;
block|}
comment|// we need to "inject" the index service to not create cyclic dep
DECL|method|setIndexService
specifier|public
name|void
name|setIndexService
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{
name|this
operator|.
name|indexService
operator|=
name|indexService
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
synchronized|synchronized
init|(
name|loadedFieldData
init|)
block|{
for|for
control|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
range|:
name|loadedFieldData
operator|.
name|values
argument_list|()
control|)
block|{
name|fieldData
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|loadedFieldData
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|IndexFieldDataCache
name|cache
range|:
name|fieldDataCaches
operator|.
name|values
argument_list|()
control|)
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|fieldDataCaches
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|clearField
specifier|public
name|void
name|clearField
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|loadedFieldData
init|)
block|{
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
init|=
name|loadedFieldData
operator|.
name|remove
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldData
operator|!=
literal|null
condition|)
block|{
name|fieldData
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|IndexFieldDataCache
name|cache
init|=
name|fieldDataCaches
operator|.
name|remove
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
synchronized|synchronized
init|(
name|loadedFieldData
init|)
block|{
for|for
control|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
range|:
name|loadedFieldData
operator|.
name|values
argument_list|()
control|)
block|{
name|indexFieldData
operator|.
name|clear
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|IndexFieldDataCache
name|cache
range|:
name|fieldDataCaches
operator|.
name|values
argument_list|()
control|)
block|{
name|cache
operator|.
name|clear
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onMappingUpdate
specifier|public
name|void
name|onMappingUpdate
parameter_list|()
block|{
comment|// synchronize to make sure to not miss field data instances that are being loaded
synchronized|synchronized
init|(
name|loadedFieldData
init|)
block|{
comment|// important: do not clear fieldDataCaches: the cache may be reused
name|loadedFieldData
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getForField
specifier|public
parameter_list|<
name|IFD
extends|extends
name|IndexFieldData
argument_list|<
name|?
argument_list|>
parameter_list|>
name|IFD
name|getForField
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
parameter_list|)
block|{
specifier|final
name|FieldMapper
operator|.
name|Names
name|fieldNames
init|=
name|mapper
operator|.
name|names
argument_list|()
decl_stmt|;
specifier|final
name|FieldDataType
name|type
init|=
name|mapper
operator|.
name|fieldDataType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"found no fielddata type for field ["
operator|+
name|fieldNames
operator|.
name|fullName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|docValues
init|=
name|mapper
operator|.
name|hasDocValues
argument_list|()
decl_stmt|;
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
init|=
name|loadedFieldData
operator|.
name|get
argument_list|(
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldData
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|loadedFieldData
init|)
block|{
name|fieldData
operator|=
name|loadedFieldData
operator|.
name|get
argument_list|(
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldData
operator|==
literal|null
condition|)
block|{
name|IndexFieldData
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
name|String
name|format
init|=
name|type
operator|.
name|getFormat
argument_list|(
name|indexSettings
argument_list|)
decl_stmt|;
if|if
condition|(
name|format
operator|!=
literal|null
operator|&&
name|FieldDataType
operator|.
name|DOC_VALUES_FORMAT_VALUE
operator|.
name|equals
argument_list|(
name|format
argument_list|)
operator|&&
operator|!
name|docValues
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"field ["
operator|+
name|fieldNames
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has no doc values, will use default field data format"
argument_list|)
expr_stmt|;
name|format
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|builder
operator|=
name|buildersByTypeAndFormat
operator|.
name|get
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|type
operator|.
name|getType
argument_list|()
argument_list|,
name|format
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to find format ["
operator|+
name|format
operator|+
literal|"] for field ["
operator|+
name|fieldNames
operator|.
name|fullName
argument_list|()
operator|+
literal|"], will use default"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|builder
operator|==
literal|null
operator|&&
name|docValues
condition|)
block|{
name|builder
operator|=
name|docValuesBuildersByType
operator|.
name|get
argument_list|(
name|type
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|buildersByType
operator|.
name|get
argument_list|(
name|type
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"failed to find field data builder for field "
operator|+
name|fieldNames
operator|.
name|fullName
argument_list|()
operator|+
literal|", and type "
operator|+
name|type
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
name|IndexFieldDataCache
name|cache
init|=
name|fieldDataCaches
operator|.
name|get
argument_list|(
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
comment|//  we default to node level cache, which in turn defaults to be unbounded
comment|// this means changing the node level settings is simple, just set the bounds there
name|String
name|cacheType
init|=
name|type
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
literal|"cache"
argument_list|,
name|indexSettings
operator|.
name|get
argument_list|(
literal|"index.fielddata.cache"
argument_list|,
literal|"node"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"resident"
operator|.
name|equals
argument_list|(
name|cacheType
argument_list|)
condition|)
block|{
name|cache
operator|=
operator|new
name|IndexFieldDataCache
operator|.
name|Resident
argument_list|(
name|indexService
argument_list|,
name|fieldNames
argument_list|,
name|type
argument_list|,
name|indicesFieldDataCacheListener
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"soft"
operator|.
name|equals
argument_list|(
name|cacheType
argument_list|)
condition|)
block|{
name|cache
operator|=
operator|new
name|IndexFieldDataCache
operator|.
name|Soft
argument_list|(
name|indexService
argument_list|,
name|fieldNames
argument_list|,
name|type
argument_list|,
name|indicesFieldDataCacheListener
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"node"
operator|.
name|equals
argument_list|(
name|cacheType
argument_list|)
condition|)
block|{
name|cache
operator|=
name|indicesFieldDataCache
operator|.
name|buildIndexFieldDataCache
argument_list|(
name|indexService
argument_list|,
name|index
argument_list|,
name|fieldNames
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"cache type not supported ["
operator|+
name|cacheType
operator|+
literal|"] for field ["
operator|+
name|fieldNames
operator|.
name|fullName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|fieldDataCaches
operator|.
name|put
argument_list|(
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
name|GlobalOrdinalsBuilder
name|globalOrdinalBuilder
init|=
operator|new
name|InternalGlobalOrdinalsBuilder
argument_list|(
name|index
argument_list|()
argument_list|,
name|indexSettings
argument_list|)
decl_stmt|;
name|fieldData
operator|=
name|builder
operator|.
name|build
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|mapper
argument_list|,
name|cache
argument_list|,
name|circuitBreakerService
argument_list|,
name|indexService
operator|.
name|mapperService
argument_list|()
argument_list|,
name|globalOrdinalBuilder
argument_list|)
expr_stmt|;
name|loadedFieldData
operator|.
name|put
argument_list|(
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldData
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|(
name|IFD
operator|)
name|fieldData
return|;
block|}
block|}
end_class

end_unit

