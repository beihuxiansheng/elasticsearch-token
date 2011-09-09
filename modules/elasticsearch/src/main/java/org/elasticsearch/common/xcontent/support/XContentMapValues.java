begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentMapValues
specifier|public
class|class
name|XContentMapValues
block|{
DECL|method|isObject
specifier|public
specifier|static
name|boolean
name|isObject
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
return|return
name|node
operator|instanceof
name|Map
return|;
block|}
DECL|method|isArray
specifier|public
specifier|static
name|boolean
name|isArray
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
return|return
name|node
operator|instanceof
name|List
return|;
block|}
DECL|method|nodeStringValue
specifier|public
specifier|static
name|String
name|nodeStringValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|node
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|nodeFloatValue
specifier|public
specifier|static
name|float
name|nodeFloatValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|float
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeFloatValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeFloatValue
specifier|public
specifier|static
name|float
name|nodeFloatValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeDoubleValue
specifier|public
specifier|static
name|double
name|nodeDoubleValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeDoubleValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeDoubleValue
specifier|public
specifier|static
name|double
name|nodeDoubleValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeIntegerValue
specifier|public
specifier|static
name|int
name|nodeIntegerValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeIntegerValue
specifier|public
specifier|static
name|int
name|nodeIntegerValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeShortValue
specifier|public
specifier|static
name|short
name|nodeShortValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|short
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeShortValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeShortValue
specifier|public
specifier|static
name|short
name|nodeShortValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|shortValue
argument_list|()
return|;
block|}
return|return
name|Short
operator|.
name|parseShort
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeByteValue
specifier|public
specifier|static
name|byte
name|nodeByteValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|byte
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeByteValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeByteValue
specifier|public
specifier|static
name|byte
name|nodeByteValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|byteValue
argument_list|()
return|;
block|}
return|return
name|Byte
operator|.
name|parseByte
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeLongValue
specifier|public
specifier|static
name|long
name|nodeLongValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|long
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeLongValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeLongValue
specifier|public
specifier|static
name|long
name|nodeLongValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodeBooleanValue
specifier|public
specifier|static
name|boolean
name|nodeBooleanValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeBooleanValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeBooleanValue
specifier|public
specifier|static
name|boolean
name|nodeBooleanValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|(
name|Boolean
operator|)
name|node
return|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|intValue
argument_list|()
operator|!=
literal|0
return|;
block|}
name|String
name|value
init|=
name|node
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|!
operator|(
name|value
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"off"
argument_list|)
operator|)
return|;
block|}
DECL|method|nodeTimeValue
specifier|public
specifier|static
name|TimeValue
name|nodeTimeValue
parameter_list|(
name|Object
name|node
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|nodeTimeValue
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|nodeTimeValue
specifier|public
specifier|static
name|TimeValue
name|nodeTimeValue
parameter_list|(
name|Object
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Number
condition|)
block|{
return|return
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|node
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
return|return
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

