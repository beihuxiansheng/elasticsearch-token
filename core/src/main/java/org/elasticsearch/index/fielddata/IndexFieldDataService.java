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
name|util
operator|.
name|Accountable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|core
operator|.
name|BooleanFieldMapper
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
name|IndexFieldMapper
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
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
operator|.
name|Names
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
DECL|field|FIELDDATA_CACHE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FIELDDATA_CACHE_KEY
init|=
literal|"index.fielddata.cache"
decl_stmt|;
DECL|field|FIELDDATA_CACHE_VALUE_NODE
specifier|public
specifier|static
specifier|final
name|String
name|FIELDDATA_CACHE_VALUE_NODE
init|=
literal|"node"
decl_stmt|;
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
name|IndexFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|IndexIndexFieldData
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
name|put
argument_list|(
name|BooleanFieldMapper
operator|.
name|CONTENT_TYPE
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
name|BOOLEAN
argument_list|)
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
name|put
argument_list|(
name|BooleanFieldMapper
operator|.
name|CONTENT_TYPE
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
name|BOOLEAN
argument_list|)
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
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|BooleanFieldMapper
operator|.
name|CONTENT_TYPE
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
name|BOOLEAN
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|BooleanFieldMapper
operator|.
name|CONTENT_TYPE
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
name|BOOLEAN
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|BooleanFieldMapper
operator|.
name|CONTENT_TYPE
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
comment|// the below map needs to be modified under a lock
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
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|DEFAULT_NOOP_LISTENER
specifier|private
specifier|static
specifier|final
name|IndexFieldDataCache
operator|.
name|Listener
name|DEFAULT_NOOP_LISTENER
init|=
operator|new
name|IndexFieldDataCache
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|Accountable
name|ramUsage
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|boolean
name|wasEvicted
parameter_list|,
name|long
name|sizeInBytes
parameter_list|)
block|{         }
block|}
decl_stmt|;
DECL|field|listener
specifier|private
specifier|volatile
name|IndexFieldDataCache
operator|.
name|Listener
name|listener
init|=
name|DEFAULT_NOOP_LISTENER
decl_stmt|;
comment|// We need to cache fielddata on the _parent field because of 1.x indices.
comment|// When we don't support 1.x anymore (3.0) then remove this caching
comment|// This variable needs to be read/written under lock
DECL|field|parentIndexFieldData
specifier|private
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|parentIndexFieldData
decl_stmt|;
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
name|MapperService
name|mapperService
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
name|mapperService
operator|=
name|mapperService
expr_stmt|;
block|}
DECL|method|clear
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|parentIndexFieldData
operator|=
literal|null
expr_stmt|;
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|IndexFieldDataCache
argument_list|>
name|fieldDataCacheValues
init|=
name|fieldDataCaches
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexFieldDataCache
name|cache
range|:
name|fieldDataCacheValues
control|)
block|{
try|try
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|fieldDataCacheValues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ExceptionsHelper
operator|.
name|maybeThrowRuntimeAndSuppress
argument_list|(
name|exceptions
argument_list|)
expr_stmt|;
block|}
DECL|method|clearField
specifier|public
specifier|synchronized
name|void
name|clearField
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|ParentFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|parentIndexFieldData
operator|=
literal|null
expr_stmt|;
block|}
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
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
try|try
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|ExceptionsHelper
operator|.
name|maybeThrowRuntimeAndSuppress
argument_list|(
name|exceptions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|MappedFieldType
name|fieldType
parameter_list|)
block|{
specifier|final
name|Names
name|fieldNames
init|=
name|fieldType
operator|.
name|names
argument_list|()
decl_stmt|;
specifier|final
name|FieldDataType
name|type
init|=
name|fieldType
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
name|IllegalArgumentException
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
name|fieldType
operator|.
name|hasDocValues
argument_list|()
decl_stmt|;
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
name|IllegalArgumentException
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
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|cache
operator|=
name|fieldDataCaches
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
name|FIELDDATA_CACHE_KEY
argument_list|,
name|FIELDDATA_CACHE_VALUE_NODE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|FIELDDATA_CACHE_VALUE_NODE
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
name|listener
argument_list|,
name|index
argument_list|,
name|fieldNames
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
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
name|None
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
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
block|}
return|return
operator|(
name|IFD
operator|)
name|builder
operator|.
name|build
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldType
argument_list|,
name|cache
argument_list|,
name|circuitBreakerService
argument_list|,
name|mapperService
argument_list|)
return|;
block|}
comment|/**      * Sets a {@link org.elasticsearch.index.fielddata.IndexFieldDataCache.Listener} passed to each {@link IndexFieldData}      * creation to capture onCache and onRemoval events. Setting a listener on this method will override any previously      * set listeners.      * @throws IllegalStateException if the listener is set more than once      */
DECL|method|setListener
specifier|public
name|void
name|setListener
parameter_list|(
name|IndexFieldDataCache
operator|.
name|Listener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"listener must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|listener
operator|!=
name|DEFAULT_NOOP_LISTENER
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't set listener more than once"
argument_list|)
throw|;
block|}
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
block|}
end_class

end_unit
