begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_comment
comment|/**  * Settings to use when compiling a script.  */
end_comment

begin_class
DECL|class|CompilerSettings
specifier|final
class|class
name|CompilerSettings
block|{
comment|/**      * Constant to be used when specifying numeric overflow when compiling a script.      */
DECL|field|NUMERIC_OVERFLOW
specifier|public
specifier|static
specifier|final
name|String
name|NUMERIC_OVERFLOW
init|=
literal|"numeric_overflow"
decl_stmt|;
comment|/**      * Constant to be used when specifying the maximum loop counter when compiling a script.      */
DECL|field|MAX_LOOP_COUNTER
specifier|public
specifier|static
specifier|final
name|String
name|MAX_LOOP_COUNTER
init|=
literal|"max_loop_counter"
decl_stmt|;
comment|/**      * Whether or not to allow numeric values to overflow without exception.      */
DECL|field|numericOverflow
specifier|private
name|boolean
name|numericOverflow
init|=
literal|true
decl_stmt|;
comment|/**      * The maximum number of statements allowed to be run in a loop.      */
DECL|field|maxLoopCounter
specifier|private
name|int
name|maxLoopCounter
init|=
literal|10000
decl_stmt|;
comment|/**      * Returns {@code true} if numeric operations should overflow, {@code false}      * if they should signal an exception.      *<p>      * If this value is {@code true} (default), then things behave like java:      * overflow for integer types can result in unexpected values / unexpected      * signs, and overflow for floating point types can result in infinite or      * {@code NaN} values.      */
DECL|method|getNumericOverflow
specifier|public
name|boolean
name|getNumericOverflow
parameter_list|()
block|{
return|return
name|numericOverflow
return|;
block|}
comment|/**      * Set {@code true} for numerics to overflow, false to deliver exceptions.      * @see #getNumericOverflow      */
DECL|method|setNumericOverflow
specifier|public
name|void
name|setNumericOverflow
parameter_list|(
name|boolean
name|allow
parameter_list|)
block|{
name|this
operator|.
name|numericOverflow
operator|=
name|allow
expr_stmt|;
block|}
comment|/**      * Returns the value for the cumulative total number of statements that can be made in all loops      * in a script before an exception is thrown.  This attempts to prevent infinite loops.      */
DECL|method|getMaxLoopCounter
specifier|public
name|int
name|getMaxLoopCounter
parameter_list|()
block|{
return|return
name|maxLoopCounter
return|;
block|}
comment|/**      * Set the cumulative total number of statements that can be made in all loops.      * @see #getMaxLoopCounter      */
DECL|method|setMaxLoopCounter
specifier|public
name|void
name|setMaxLoopCounter
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|this
operator|.
name|maxLoopCounter
operator|=
name|max
expr_stmt|;
block|}
block|}
end_class

end_unit

