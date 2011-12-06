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
name|list
operator|.
name|array
operator|.
name|TIntArrayList
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ExtTIntArrayList
specifier|public
class|class
name|ExtTIntArrayList
extends|extends
name|TIntArrayList
block|{
DECL|method|ExtTIntArrayList
specifier|public
name|ExtTIntArrayList
parameter_list|()
block|{     }
DECL|method|ExtTIntArrayList
specifier|public
name|ExtTIntArrayList
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|super
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
DECL|method|ExtTIntArrayList
specifier|public
name|ExtTIntArrayList
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
DECL|method|unsafeArray
specifier|public
name|int
index|[]
name|unsafeArray
parameter_list|()
block|{
return|return
name|_data
return|;
block|}
block|}
end_class

end_unit

