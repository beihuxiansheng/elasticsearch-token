begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Objects
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|DoubleDocValues
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
name|MultiGeoPointValues
import|;
end_import

begin_comment
comment|/**  * ValueSource to return latitudes as a double "stream" for geopoint fields  */
end_comment

begin_class
DECL|class|GeoLatitudeValueSource
specifier|final
class|class
name|GeoLatitudeValueSource
extends|extends
name|ValueSource
block|{
DECL|field|fieldData
specifier|final
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
decl_stmt|;
DECL|method|GeoLatitudeValueSource
name|GeoLatitudeValueSource
parameter_list|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|fieldData
parameter_list|)
block|{
name|this
operator|.
name|fieldData
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
comment|// ValueSource uses a rawtype
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|leaf
parameter_list|)
throws|throws
name|IOException
block|{
name|AtomicGeoPointFieldData
name|leafData
init|=
operator|(
name|AtomicGeoPointFieldData
operator|)
name|fieldData
operator|.
name|load
argument_list|(
name|leaf
argument_list|)
decl_stmt|;
specifier|final
name|MultiGeoPointValues
name|values
init|=
name|leafData
operator|.
name|getGeoPointValues
argument_list|()
decl_stmt|;
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|values
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|values
operator|.
name|nextValue
argument_list|()
operator|.
name|getLat
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0.0
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|fieldData
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|GeoLatitudeValueSource
name|other
init|=
operator|(
name|GeoLatitudeValueSource
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|fieldData
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldData
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"lat: field("
operator|+
name|fieldData
operator|.
name|getFieldName
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

