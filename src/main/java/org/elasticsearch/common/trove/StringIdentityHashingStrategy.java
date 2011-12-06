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
name|strategy
operator|.
name|HashingStrategy
import|;
end_import

begin_comment
comment|/**  * A string based hash code with identity equality.  */
end_comment

begin_class
DECL|class|StringIdentityHashingStrategy
specifier|public
class|class
name|StringIdentityHashingStrategy
implements|implements
name|HashingStrategy
argument_list|<
name|String
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5188534454583764905L
decl_stmt|;
DECL|method|computeHashCode
specifier|public
name|int
name|computeHashCode
parameter_list|(
name|String
name|object
parameter_list|)
block|{
return|return
name|object
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"StringEquality"
block|}
argument_list|)
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|String
name|o1
parameter_list|,
name|String
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|==
name|o2
return|;
block|}
block|}
end_class

end_unit

