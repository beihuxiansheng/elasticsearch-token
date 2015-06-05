begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_comment
comment|/**  * Holds a value that is either:  * a) set implicitly e.g. through some default value  * b) set explicitly e.g. from a user selection  *   * When merging conflicting configuration settings such as  * field mapping settings it is preferable to preserve an explicit  * choice rather than a choice made only made implicitly by defaults.   *   */
end_comment

begin_class
DECL|class|Explicit
specifier|public
class|class
name|Explicit
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|value
specifier|private
specifier|final
name|T
name|value
decl_stmt|;
DECL|field|explicit
specifier|private
specifier|final
name|boolean
name|explicit
decl_stmt|;
comment|/**      * Create a value with an indication if this was an explicit choice      * @param value a setting value      * @param explicit true if the value passed is a conscious decision, false if using some kind of default      */
DECL|method|Explicit
specifier|public
name|Explicit
parameter_list|(
name|T
name|value
parameter_list|,
name|boolean
name|explicit
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|explicit
operator|=
name|explicit
expr_stmt|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
comment|/**      *       * @return true if the value passed is a conscious decision, false if using some kind of default      */
DECL|method|explicit
specifier|public
name|boolean
name|explicit
parameter_list|()
block|{
return|return
name|this
operator|.
name|explicit
return|;
block|}
block|}
end_class

end_unit

