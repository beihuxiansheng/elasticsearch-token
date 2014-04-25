begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.fieldcomparator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
package|;
end_package

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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FloatValuesComparator
specifier|public
specifier|final
class|class
name|FloatValuesComparator
extends|extends
name|DoubleValuesComparatorBase
argument_list|<
name|Float
argument_list|>
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|FloatValuesComparator
specifier|public
name|FloatValuesComparator
parameter_list|(
name|IndexNumericFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
parameter_list|,
name|float
name|missingValue
parameter_list|,
name|int
name|numHits
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|)
block|{
name|super
argument_list|(
name|indexFieldData
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|)
expr_stmt|;
assert|assert
name|indexFieldData
operator|.
name|getNumericType
argument_list|()
operator|.
name|requiredBits
argument_list|()
operator|<=
literal|32
assert|;
name|this
operator|.
name|values
operator|=
operator|new
name|float
index|[
name|numHits
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
specifier|final
name|float
name|v1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|float
name|v2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
return|return
name|Float
operator|.
name|compare
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|slot
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|(
name|float
operator|)
name|sortMode
operator|.
name|getRelevantValue
argument_list|(
name|readerValues
argument_list|,
name|doc
argument_list|,
name|missingValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Float
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|values
index|[
name|slot
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|+=
operator|(
name|float
operator|)
name|sortMode
operator|.
name|getRelevantValue
argument_list|(
name|readerValues
argument_list|,
name|doc
argument_list|,
name|missingValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|divide
specifier|public
name|void
name|divide
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|divisor
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|/=
name|divisor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|missing
specifier|public
name|void
name|missing
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|(
name|float
operator|)
name|missingValue
expr_stmt|;
block|}
block|}
end_class

end_unit

