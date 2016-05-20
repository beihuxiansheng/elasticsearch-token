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
specifier|public
specifier|final
class|class
name|CompilerSettings
block|{
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
comment|/**      * The maximum number of statements allowed to be run in a loop.      */
DECL|field|maxLoopCounter
specifier|private
name|int
name|maxLoopCounter
init|=
literal|10000
decl_stmt|;
comment|/**      * Returns the value for the cumulative total number of statements that can be made in all loops      * in a script before an exception is thrown.  This attempts to prevent infinite loops.  Note if      * the counter is set to 0, no loop counter will be written.      */
DECL|method|getMaxLoopCounter
specifier|public
specifier|final
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
specifier|final
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

