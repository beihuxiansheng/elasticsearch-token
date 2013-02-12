begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_comment
comment|/**  * Defines what values to pick in the case a document contains multiple values for a particular field.  */
end_comment

begin_enum
DECL|enum|SortMode
specifier|public
enum|enum
name|SortMode
block|{
comment|/**      * Sum of all the values.      */
DECL|enum constant|SUM
name|SUM
block|,
comment|/**      * Average of all the values.      */
DECL|enum constant|AVG
name|AVG
block|,
comment|/**      * Pick the lowest value.      */
DECL|enum constant|MIN
name|MIN
block|,
comment|/**      * Pick the highest value.      */
DECL|enum constant|MAX
name|MAX
block|;
DECL|method|fromString
specifier|public
specifier|static
name|SortMode
name|fromString
parameter_list|(
name|String
name|sortMode
parameter_list|)
block|{
if|if
condition|(
literal|"min"
operator|.
name|equals
argument_list|(
name|sortMode
argument_list|)
condition|)
block|{
return|return
name|MIN
return|;
block|}
elseif|else
if|if
condition|(
literal|"max"
operator|.
name|equals
argument_list|(
name|sortMode
argument_list|)
condition|)
block|{
return|return
name|MAX
return|;
block|}
elseif|else
if|if
condition|(
literal|"sum"
operator|.
name|equals
argument_list|(
name|sortMode
argument_list|)
condition|)
block|{
return|return
name|SUM
return|;
block|}
elseif|else
if|if
condition|(
literal|"avg"
operator|.
name|equals
argument_list|(
name|sortMode
argument_list|)
condition|)
block|{
return|return
name|AVG
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Illegal sort_mode "
operator|+
name|sortMode
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

