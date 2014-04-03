begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
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
name|aggregations
operator|.
name|support
operator|.
name|format
operator|.
name|ValueFormat
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|ValueType
specifier|public
enum|enum
name|ValueType
block|{
DECL|enum constant|Deprecated
DECL|enum constant|ANY
annotation|@
name|Deprecated
name|ANY
argument_list|(
literal|"any"
argument_list|,
name|ValuesSource
operator|.
name|class
argument_list|,
name|IndexFieldData
operator|.
name|class
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|STRING
name|STRING
argument_list|(
literal|"string"
argument_list|,
name|ValuesSource
operator|.
name|Bytes
operator|.
name|class
argument_list|,
name|IndexFieldData
operator|.
name|class
argument_list|,
literal|null
argument_list|)
block|,
DECL|enum constant|LONG
name|LONG
argument_list|(
literal|"byte|short|integer|long"
argument_list|,
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|,
name|IndexNumericFieldData
operator|.
name|class
argument_list|,
name|ValueFormat
operator|.
name|RAW
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|enum constant|DOUBLE
name|DOUBLE
argument_list|(
literal|"float|double"
argument_list|,
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|,
name|IndexNumericFieldData
operator|.
name|class
argument_list|,
name|ValueFormat
operator|.
name|RAW
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFloatingPoint
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|enum constant|NUMBER
name|NUMBER
argument_list|(
literal|"number"
argument_list|,
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|,
name|IndexNumericFieldData
operator|.
name|class
argument_list|,
name|ValueFormat
operator|.
name|RAW
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|enum constant|DATE
name|DATE
argument_list|(
literal|"date"
argument_list|,
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|,
name|IndexNumericFieldData
operator|.
name|class
argument_list|,
name|ValueFormat
operator|.
name|DateTime
operator|.
name|DEFAULT
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|enum constant|IP
name|IP
argument_list|(
literal|"ip"
argument_list|,
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|,
name|IndexNumericFieldData
operator|.
name|class
argument_list|,
name|ValueFormat
operator|.
name|IPv4
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|enum constant|NUMERIC
name|NUMERIC
argument_list|(
literal|"numeric"
argument_list|,
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|,
name|IndexNumericFieldData
operator|.
name|class
argument_list|,
name|ValueFormat
operator|.
name|RAW
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|enum constant|GEOPOINT
name|GEOPOINT
argument_list|(
literal|"geo_point"
argument_list|,
name|ValuesSource
operator|.
name|GeoPoint
operator|.
name|class
argument_list|,
name|IndexGeoPointFieldData
operator|.
name|class
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isGeoPoint
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|;
DECL|field|description
specifier|final
name|String
name|description
decl_stmt|;
DECL|field|valuesSourceType
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ValuesSource
argument_list|>
name|valuesSourceType
decl_stmt|;
DECL|field|fieldDataType
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|IndexFieldData
argument_list|>
name|fieldDataType
decl_stmt|;
DECL|field|defaultFormat
specifier|final
name|ValueFormat
name|defaultFormat
decl_stmt|;
DECL|method|ValueType
specifier|private
name|ValueType
parameter_list|(
name|String
name|description
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|ValuesSource
argument_list|>
name|valuesSourceType
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|IndexFieldData
argument_list|>
name|fieldDataType
parameter_list|,
name|ValueFormat
name|defaultFormat
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|valuesSourceType
operator|=
name|valuesSourceType
expr_stmt|;
name|this
operator|.
name|fieldDataType
operator|=
name|fieldDataType
expr_stmt|;
name|this
operator|.
name|defaultFormat
operator|=
name|defaultFormat
expr_stmt|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|getValuesSourceType
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|ValuesSource
argument_list|>
name|getValuesSourceType
parameter_list|()
block|{
return|return
name|valuesSourceType
return|;
block|}
DECL|method|compatibleWith
specifier|public
name|boolean
name|compatibleWith
parameter_list|(
name|IndexFieldData
name|fieldData
parameter_list|)
block|{
return|return
name|fieldDataType
operator|.
name|isInstance
argument_list|(
name|fieldData
argument_list|)
return|;
block|}
DECL|method|isA
specifier|public
name|boolean
name|isA
parameter_list|(
name|ValueType
name|valueType
parameter_list|)
block|{
return|return
name|valueType
operator|.
name|valuesSourceType
operator|.
name|isAssignableFrom
argument_list|(
name|valuesSourceType
argument_list|)
operator|&&
name|valueType
operator|.
name|fieldDataType
operator|.
name|isAssignableFrom
argument_list|(
name|fieldDataType
argument_list|)
return|;
block|}
DECL|method|isNotA
specifier|public
name|boolean
name|isNotA
parameter_list|(
name|ValueType
name|valueType
parameter_list|)
block|{
return|return
operator|!
name|isA
argument_list|(
name|valueType
argument_list|)
return|;
block|}
DECL|method|defaultFormat
specifier|public
name|ValueFormat
name|defaultFormat
parameter_list|()
block|{
return|return
name|defaultFormat
return|;
block|}
DECL|method|isNumeric
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|isFloatingPoint
specifier|public
name|boolean
name|isFloatingPoint
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|isGeoPoint
specifier|public
name|boolean
name|isGeoPoint
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|resolveForScript
specifier|public
specifier|static
name|ValueType
name|resolveForScript
parameter_list|(
name|String
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
literal|"string"
case|:
return|return
name|STRING
return|;
case|case
literal|"double"
case|:
case|case
literal|"float"
case|:
return|return
name|DOUBLE
return|;
case|case
literal|"long"
case|:
case|case
literal|"integer"
case|:
case|case
literal|"short"
case|:
case|case
literal|"byte"
case|:
return|return
name|LONG
return|;
case|case
literal|"date"
case|:
return|return
name|DATE
return|;
case|case
literal|"ip"
case|:
return|return
name|IP
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
end_enum

end_unit

