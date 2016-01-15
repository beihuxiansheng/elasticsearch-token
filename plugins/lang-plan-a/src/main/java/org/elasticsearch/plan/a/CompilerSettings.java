begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plan.a
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plan
operator|.
name|a
package|;
end_package

begin_comment
comment|/**   * Settings to use when compiling a script   */
end_comment

begin_class
DECL|class|CompilerSettings
specifier|final
class|class
name|CompilerSettings
block|{
DECL|field|numericOverflow
specifier|private
name|boolean
name|numericOverflow
init|=
literal|true
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
block|}
end_class

end_unit

