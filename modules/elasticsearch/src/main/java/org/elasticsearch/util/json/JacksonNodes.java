begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
package|;
end_package

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonNode
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JacksonNodes
specifier|public
class|class
name|JacksonNodes
block|{
DECL|method|nodeFloatValue
specifier|public
specifier|static
name|float
name|nodeFloatValue
parameter_list|(
name|JsonNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isNumber
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getNumberValue
argument_list|()
operator|.
name|floatValue
argument_list|()
return|;
block|}
name|String
name|value
init|=
name|node
operator|.
name|getTextValue
argument_list|()
decl_stmt|;
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|nodeDoubleValue
specifier|public
specifier|static
name|double
name|nodeDoubleValue
parameter_list|(
name|JsonNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isNumber
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getNumberValue
argument_list|()
operator|.
name|doubleValue
argument_list|()
return|;
block|}
name|String
name|value
init|=
name|node
operator|.
name|getTextValue
argument_list|()
decl_stmt|;
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|nodeIntegerValue
specifier|public
specifier|static
name|int
name|nodeIntegerValue
parameter_list|(
name|JsonNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isNumber
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getNumberValue
argument_list|()
operator|.
name|intValue
argument_list|()
return|;
block|}
name|String
name|value
init|=
name|node
operator|.
name|getTextValue
argument_list|()
decl_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|nodeLongValue
specifier|public
specifier|static
name|long
name|nodeLongValue
parameter_list|(
name|JsonNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isNumber
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getNumberValue
argument_list|()
operator|.
name|longValue
argument_list|()
return|;
block|}
name|String
name|value
init|=
name|node
operator|.
name|getTextValue
argument_list|()
decl_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|nodeBooleanValue
specifier|public
specifier|static
name|boolean
name|nodeBooleanValue
parameter_list|(
name|JsonNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isBoolean
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getBooleanValue
argument_list|()
return|;
block|}
name|String
name|value
init|=
name|node
operator|.
name|getTextValue
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
block|}
end_class

end_unit

