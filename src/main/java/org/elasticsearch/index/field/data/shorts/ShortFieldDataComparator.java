begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.shorts
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
name|shorts
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
name|support
operator|.
name|NumericFieldDataComparator
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_comment
comment|// LUCENE MONITOR: Monitor against FieldComparator.Short
end_comment

begin_class
DECL|class|ShortFieldDataComparator
specifier|public
class|class
name|ShortFieldDataComparator
extends|extends
name|NumericFieldDataComparator
block|{
DECL|field|values
specifier|private
specifier|final
name|short
index|[]
name|values
decl_stmt|;
DECL|field|bottom
specifier|private
name|short
name|bottom
decl_stmt|;
DECL|method|ShortFieldDataComparator
specifier|public
name|ShortFieldDataComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|FieldDataCache
name|fieldDataCache
parameter_list|)
block|{
name|super
argument_list|(
name|fieldName
argument_list|,
name|fieldDataCache
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|short
index|[
name|numHits
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldDataType
specifier|public
name|FieldDataType
name|fieldDataType
parameter_list|()
block|{
return|return
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|SHORT
return|;
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
return|return
name|values
index|[
name|slot1
index|]
operator|-
name|values
index|[
name|slot2
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|bottom
operator|-
name|currentFieldData
operator|.
name|shortValue
argument_list|(
name|doc
argument_list|)
return|;
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
block|{
name|values
index|[
name|slot
index|]
operator|=
name|currentFieldData
operator|.
name|shortValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|bottom
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Comparable
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|Short
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
block|}
end_class

end_unit

