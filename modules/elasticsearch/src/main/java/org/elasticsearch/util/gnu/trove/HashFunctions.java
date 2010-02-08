begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.gnu.trove
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
package|;
end_package

begin_comment
comment|/**  * Provides various hash functions.  *  * @author wolfgang.hoschek@cern.ch  * @version 1.0, 09/24/99  */
end_comment

begin_class
DECL|class|HashFunctions
specifier|public
specifier|final
class|class
name|HashFunctions
block|{
comment|/**      * Returns a hashcode for the specified value.      *      * @return a hash code value for the specified value.      */
DECL|method|hash
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|double
name|value
parameter_list|)
block|{
assert|assert
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|:
literal|"Values of NaN are not supported."
assert|;
name|long
name|bits
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|bits
operator|^
operator|(
name|bits
operator|>>>
literal|32
operator|)
argument_list|)
return|;
comment|//return (int) Double.doubleToLongBits(value*663608941.737);
comment|//this avoids excessive hashCollisions in the case values are
comment|//of the form (1.0, 2.0, 3.0, ...)
block|}
comment|/**      * Returns a hashcode for the specified value.      *      * @return a hash code value for the specified value.      */
DECL|method|hash
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|float
name|value
parameter_list|)
block|{
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|:
literal|"Values of NaN are not supported."
assert|;
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|value
operator|*
literal|663608941.737f
argument_list|)
return|;
comment|// this avoids excessive hashCollisions in the case values are
comment|// of the form (1.0, 2.0, 3.0, ...)
block|}
comment|/**      * Returns a hashcode for the specified value.      *      * @return a hash code value for the specified value.      */
DECL|method|hash
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|value
parameter_list|)
block|{
comment|// Multiply by prime to make sure hash can't be negative (see Knuth v3, p. 515-516)
return|return
name|value
operator|*
literal|31
return|;
block|}
comment|/**      * Returns a hashcode for the specified value.      *      * @return a hash code value for the specified value.      */
DECL|method|hash
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|long
name|value
parameter_list|)
block|{
comment|// Multiply by prime to make sure hash can't be negative (see Knuth v3, p. 515-516)
return|return
operator|(
call|(
name|int
call|)
argument_list|(
name|value
operator|^
operator|(
name|value
operator|>>>
literal|32
operator|)
argument_list|)
operator|)
operator|*
literal|31
return|;
block|}
comment|/**      * Returns a hashcode for the specified object.      *      * @return a hash code value for the specified object.      */
DECL|method|hash
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
return|return
name|object
operator|==
literal|null
condition|?
literal|0
else|:
name|object
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * In profiling, it has been found to be faster to have our own local implementation      * of "ceil" rather than to call to {@link Math#ceil(double)}.      */
DECL|method|fastCeil
specifier|static
name|int
name|fastCeil
parameter_list|(
name|float
name|v
parameter_list|)
block|{
name|int
name|possible_result
init|=
operator|(
name|int
operator|)
name|v
decl_stmt|;
if|if
condition|(
name|v
operator|-
name|possible_result
operator|>
literal|0
condition|)
name|possible_result
operator|++
expr_stmt|;
return|return
name|possible_result
return|;
block|}
block|}
end_class

end_unit

