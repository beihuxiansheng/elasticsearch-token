begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.script.expression
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|expression
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|ValueSource
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

begin_comment
comment|/**  * Expressions API for geo_point fields.  */
end_comment

begin_class
DECL|class|GeoField
specifier|final
class|class
name|GeoField
block|{
comment|// no instance
DECL|method|GeoField
specifier|private
name|GeoField
parameter_list|()
block|{}
comment|// supported variables
DECL|field|EMPTY_VARIABLE
specifier|static
specifier|final
name|String
name|EMPTY_VARIABLE
init|=
literal|"empty"
decl_stmt|;
DECL|field|LAT_VARIABLE
specifier|static
specifier|final
name|String
name|LAT_VARIABLE
init|=
literal|"lat"
decl_stmt|;
DECL|field|LON_VARIABLE
specifier|static
specifier|final
name|String
name|LON_VARIABLE
init|=
literal|"lon"
decl_stmt|;
comment|// supported methods
DECL|field|ISEMPTY_METHOD
specifier|static
specifier|final
name|String
name|ISEMPTY_METHOD
init|=
literal|"isEmpty"
decl_stmt|;
DECL|field|GETLAT_METHOD
specifier|static
specifier|final
name|String
name|GETLAT_METHOD
init|=
literal|"getLat"
decl_stmt|;
DECL|field|GETLON_METHOD
specifier|static
specifier|final
name|String
name|GETLON_METHOD
init|=
literal|"getLon"
decl_stmt|;
DECL|method|getVariable
specifier|static
name|ValueSource
name|getVariable
parameter_list|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|variable
parameter_list|)
block|{
switch|switch
condition|(
name|variable
condition|)
block|{
case|case
name|EMPTY_VARIABLE
case|:
return|return
operator|new
name|GeoEmptyValueSource
argument_list|(
name|fieldData
argument_list|)
return|;
case|case
name|LAT_VARIABLE
case|:
return|return
operator|new
name|GeoLatitudeValueSource
argument_list|(
name|fieldData
argument_list|)
return|;
case|case
name|LON_VARIABLE
case|:
return|return
operator|new
name|GeoLongitudeValueSource
argument_list|(
name|fieldData
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Member variable ["
operator|+
name|variable
operator|+
literal|"] does not exist for geo field ["
operator|+
name|fieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
DECL|method|getMethod
specifier|static
name|ValueSource
name|getMethod
parameter_list|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|method
parameter_list|)
block|{
switch|switch
condition|(
name|method
condition|)
block|{
case|case
name|ISEMPTY_METHOD
case|:
return|return
operator|new
name|GeoEmptyValueSource
argument_list|(
name|fieldData
argument_list|)
return|;
case|case
name|GETLAT_METHOD
case|:
return|return
operator|new
name|GeoLatitudeValueSource
argument_list|(
name|fieldData
argument_list|)
return|;
case|case
name|GETLON_METHOD
case|:
return|return
operator|new
name|GeoLongitudeValueSource
argument_list|(
name|fieldData
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Member method ["
operator|+
name|method
operator|+
literal|"] does not exist for geo field ["
operator|+
name|fieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
