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
name|IndexReader
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|IndexNumericFieldData
operator|.
name|NumericType
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
name|MappedFieldType
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
name|internal
operator|.
name|IdFieldMapper
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
name|TimestampFieldMapper
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
name|UidFieldMapper
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
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableSet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|set
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_comment
comment|/** {@link IndexFieldData} impl based on Lucene's doc values. Caching is done on the Lucene side. */
end_comment

begin_class
DECL|class|DocValuesIndexFieldData
specifier|public
specifier|abstract
class|class
name|DocValuesIndexFieldData
block|{
DECL|field|index
specifier|protected
specifier|final
name|Index
name|index
decl_stmt|;
DECL|field|fieldNames
specifier|protected
specifier|final
name|Names
name|fieldNames
decl_stmt|;
DECL|field|fieldDataType
specifier|protected
specifier|final
name|FieldDataType
name|fieldDataType
decl_stmt|;
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|method|DocValuesIndexFieldData
specifier|public
name|DocValuesIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|fieldNames
operator|=
name|fieldNames
expr_stmt|;
name|this
operator|.
name|fieldDataType
operator|=
name|fieldDataType
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldNames
specifier|public
specifier|final
name|Names
name|getFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
return|;
block|}
DECL|method|getFieldDataType
specifier|public
specifier|final
name|FieldDataType
name|getFieldDataType
parameter_list|()
block|{
return|return
name|fieldDataType
return|;
block|}
DECL|method|clear
specifier|public
specifier|final
name|void
name|clear
parameter_list|()
block|{
comment|// can't do
block|}
DECL|method|clear
specifier|public
specifier|final
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
comment|// can't do
block|}
DECL|method|index
specifier|public
specifier|final
name|Index
name|index
parameter_list|()
block|{
return|return
name|index
return|;
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
DECL|field|BINARY_INDEX_FIELD_NAMES
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|BINARY_INDEX_FIELD_NAMES
init|=
name|unmodifiableSet
argument_list|(
name|newHashSet
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|IdFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|numericType
specifier|private
name|NumericType
name|numericType
decl_stmt|;
DECL|method|numericType
specifier|public
name|Builder
name|numericType
parameter_list|(
name|NumericType
name|type
parameter_list|)
block|{
name|this
operator|.
name|numericType
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
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
name|Index
name|index
parameter_list|,
name|Settings
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
comment|// Ignore Circuit Breaker
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
name|Settings
name|fdSettings
init|=
name|fieldType
operator|.
name|fieldDataType
argument_list|()
operator|.
name|getSettings
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|filter
init|=
name|fdSettings
operator|.
name|getGroups
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
operator|&&
operator|!
name|filter
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Doc values field data doesn't support filters ["
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
if|if
condition|(
name|BINARY_INDEX_FIELD_NAMES
operator|.
name|contains
argument_list|(
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|)
condition|)
block|{
assert|assert
name|numericType
operator|==
literal|null
assert|;
return|return
operator|new
name|BinaryDVIndexFieldData
argument_list|(
name|index
argument_list|,
name|fieldNames
argument_list|,
name|fieldType
operator|.
name|fieldDataType
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|numericType
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|TimestampFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|fieldNames
operator|.
name|indexName
argument_list|()
argument_list|)
operator|||
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_4_0_Beta1
argument_list|)
condition|)
block|{
return|return
operator|new
name|SortedNumericDVIndexFieldData
argument_list|(
name|index
argument_list|,
name|fieldNames
argument_list|,
name|numericType
argument_list|,
name|fieldType
operator|.
name|fieldDataType
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// prior to ES 1.4: multi-valued numerics were boxed inside a byte[] as BINARY
return|return
operator|new
name|BinaryDVNumericIndexFieldData
argument_list|(
name|index
argument_list|,
name|fieldNames
argument_list|,
name|numericType
argument_list|,
name|fieldType
operator|.
name|fieldDataType
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|SortedSetDVOrdinalsIndexFieldData
argument_list|(
name|index
argument_list|,
name|cache
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|breakerService
argument_list|,
name|fieldType
operator|.
name|fieldDataType
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

