begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|gnu.trove
package|package
name|gnu
operator|.
name|trove
package|;
end_package

begin_import
import|import
name|gnu
operator|.
name|trove
operator|.
name|map
operator|.
name|TLongObjectMap
import|;
end_import

begin_import
import|import
name|gnu
operator|.
name|trove
operator|.
name|map
operator|.
name|hash
operator|.
name|TLongObjectHashMap
import|;
end_import

begin_class
DECL|class|ExtTLongObjectHashMap
specifier|public
class|class
name|ExtTLongObjectHashMap
parameter_list|<
name|V
parameter_list|>
extends|extends
name|TLongObjectHashMap
argument_list|<
name|V
argument_list|>
block|{
DECL|method|ExtTLongObjectHashMap
specifier|public
name|ExtTLongObjectHashMap
parameter_list|()
block|{     }
DECL|method|ExtTLongObjectHashMap
specifier|public
name|ExtTLongObjectHashMap
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
block|}
DECL|method|ExtTLongObjectHashMap
specifier|public
name|ExtTLongObjectHashMap
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|float
name|loadFactor
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|,
name|loadFactor
argument_list|)
expr_stmt|;
block|}
DECL|method|ExtTLongObjectHashMap
specifier|public
name|ExtTLongObjectHashMap
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|long
name|noEntryKey
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|,
name|loadFactor
argument_list|,
name|noEntryKey
argument_list|)
expr_stmt|;
block|}
DECL|method|ExtTLongObjectHashMap
specifier|public
name|ExtTLongObjectHashMap
parameter_list|(
name|TLongObjectMap
argument_list|<
name|V
argument_list|>
name|vtLongObjectMap
parameter_list|)
block|{
name|super
argument_list|(
name|vtLongObjectMap
argument_list|)
expr_stmt|;
block|}
comment|/**      * Internal method to get the actual values associated. Some values might have "null" or no entry      * values.      */
DECL|method|internalValues
specifier|public
name|Object
index|[]
name|internalValues
parameter_list|()
block|{
return|return
name|this
operator|.
name|_values
return|;
block|}
block|}
end_class

end_unit

