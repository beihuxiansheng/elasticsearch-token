begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|IndexNumericFieldData
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
DECL|class|IntValuesComparatorSource
specifier|public
class|class
name|IntValuesComparatorSource
extends|extends
name|IndexFieldData
operator|.
name|XFieldComparatorSource
block|{
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexNumericFieldData
name|indexFieldData
decl_stmt|;
DECL|field|missingValue
specifier|private
specifier|final
name|Object
name|missingValue
decl_stmt|;
DECL|method|IntValuesComparatorSource
specifier|public
name|IntValuesComparatorSource
parameter_list|(
name|IndexNumericFieldData
name|indexFieldData
parameter_list|,
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|)
block|{
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reducedType
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
name|INT
return|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
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
assert|assert
name|fieldname
operator|.
name|equals
argument_list|(
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
assert|;
name|int
name|dMissingValue
decl_stmt|;
if|if
condition|(
name|missingValue
operator|==
literal|null
operator|||
literal|"_last"
operator|.
name|equals
argument_list|(
name|missingValue
argument_list|)
condition|)
block|{
name|dMissingValue
operator|=
name|reversed
condition|?
name|Integer
operator|.
name|MIN_VALUE
else|:
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_first"
operator|.
name|equals
argument_list|(
name|missingValue
argument_list|)
condition|)
block|{
name|dMissingValue
operator|=
name|reversed
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
else|else
block|{
name|dMissingValue
operator|=
name|missingValue
operator|instanceof
name|Number
condition|?
operator|(
operator|(
name|Number
operator|)
name|missingValue
operator|)
operator|.
name|intValue
argument_list|()
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|missingValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|IntValuesComparator
argument_list|(
name|indexFieldData
argument_list|,
name|dMissingValue
argument_list|,
name|numHits
argument_list|,
name|reversed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

