begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.hamcrest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
package|;
end_package

begin_class
DECL|class|DoubleMatcher
specifier|public
class|class
name|DoubleMatcher
block|{
comment|/**      * Better floating point comparisons courtesy of https://github.com/brazzy/floating-point-gui.de      *      * Snippet adapted to use doubles instead of floats      */
DECL|method|nearlyEqual
specifier|public
specifier|static
name|boolean
name|nearlyEqual
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|,
name|double
name|epsilon
parameter_list|)
block|{
specifier|final
name|double
name|absA
init|=
name|Math
operator|.
name|abs
argument_list|(
name|a
argument_list|)
decl_stmt|;
specifier|final
name|double
name|absB
init|=
name|Math
operator|.
name|abs
argument_list|(
name|b
argument_list|)
decl_stmt|;
specifier|final
name|double
name|diff
init|=
name|Math
operator|.
name|abs
argument_list|(
name|a
operator|-
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
name|b
condition|)
block|{
comment|// shortcut, handles infinities
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|==
literal|0
operator|||
name|b
operator|==
literal|0
operator|||
name|diff
operator|<
name|Double
operator|.
name|MIN_NORMAL
condition|)
block|{
comment|// a or b is zero or both are extremely close to it
comment|// relative error is less meaningful here
return|return
name|diff
operator|<
operator|(
name|epsilon
operator|*
name|Double
operator|.
name|MIN_NORMAL
operator|)
return|;
block|}
else|else
block|{
comment|// use relative error
return|return
name|diff
operator|/
name|Math
operator|.
name|min
argument_list|(
operator|(
name|absA
operator|+
name|absB
operator|)
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|)
operator|<
name|epsilon
return|;
block|}
block|}
block|}
end_class

end_unit

