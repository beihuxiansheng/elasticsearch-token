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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * A {@link Function}-like interface which allows throwing checked exceptions.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|CheckedFunction
specifier|public
interface|interface
name|CheckedFunction
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|,
name|E
extends|extends
name|Exception
parameter_list|>
block|{
DECL|method|apply
name|R
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
throws|throws
name|E
function_decl|;
block|}
end_interface

end_unit

