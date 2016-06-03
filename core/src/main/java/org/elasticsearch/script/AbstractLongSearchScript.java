begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
package|;
end_package

begin_comment
comment|/**  * A simpler base class instead of {@link AbstractSearchScript} for computations  * that return a long number.  */
end_comment

begin_class
DECL|class|AbstractLongSearchScript
specifier|public
specifier|abstract
class|class
name|AbstractLongSearchScript
extends|extends
name|AbstractSearchScript
block|{
annotation|@
name|Override
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
block|{
return|return
name|runAsLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|runAsLong
specifier|public
specifier|abstract
name|long
name|runAsLong
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|runAsDouble
specifier|public
name|double
name|runAsDouble
parameter_list|()
block|{
return|return
name|runAsLong
argument_list|()
return|;
block|}
block|}
end_class

end_unit

