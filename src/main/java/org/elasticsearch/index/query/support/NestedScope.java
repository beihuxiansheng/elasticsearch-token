begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * During query parsing this keeps track of the current nested level.  */
end_comment

begin_class
DECL|class|NestedScope
specifier|public
specifier|final
class|class
name|NestedScope
block|{
DECL|field|levelStack
specifier|private
specifier|final
name|Deque
argument_list|<
name|ObjectMapper
argument_list|>
name|levelStack
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * @return For the current nested level returns the object mapper that belongs to that      */
DECL|method|getObjectMapper
specifier|public
name|ObjectMapper
name|getObjectMapper
parameter_list|()
block|{
return|return
name|levelStack
operator|.
name|peek
argument_list|()
return|;
block|}
comment|/**      * Sets the new current nested level and moves old current nested level down      */
DECL|method|nextLevel
specifier|public
name|void
name|nextLevel
parameter_list|(
name|ObjectMapper
name|level
parameter_list|)
block|{
name|levelStack
operator|.
name|push
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the previous nested level as current nested level and removes the current nested level.      */
DECL|method|previousLevel
specifier|public
name|void
name|previousLevel
parameter_list|()
block|{
name|ObjectMapper
name|level
init|=
name|levelStack
operator|.
name|pop
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

