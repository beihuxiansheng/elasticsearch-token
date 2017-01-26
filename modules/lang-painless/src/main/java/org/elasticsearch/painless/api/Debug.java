begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.api
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|api
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|PainlessExplainError
import|;
end_import

begin_comment
comment|/**  * Utility methods for debugging painless scripts that are accessible to painless scripts.  */
end_comment

begin_class
DECL|class|Debug
specifier|public
class|class
name|Debug
block|{
DECL|method|Debug
specifier|private
name|Debug
parameter_list|()
block|{}
comment|/**      * Throw an {@link Error} that "explains" an object.      */
DECL|method|explain
specifier|public
specifier|static
name|void
name|explain
parameter_list|(
name|Object
name|objectToExplain
parameter_list|)
throws|throws
name|PainlessExplainError
block|{
throw|throw
operator|new
name|PainlessExplainError
argument_list|(
name|objectToExplain
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

