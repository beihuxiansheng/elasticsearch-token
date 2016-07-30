begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|subphase
operator|.
name|DocValueFieldsContext
import|;
end_import

begin_comment
comment|/**  * All configuration and context needed by the FetchSubPhase to execute on hits.  * The only required information in this base class is whether or not the sub phase needs to be run at all.  * It can be extended by FetchSubPhases to hold information the phase needs to execute on hits.  * See {@link org.elasticsearch.search.fetch.FetchSubPhase.ContextFactory} and also {@link DocValueFieldsContext} for an example.  */
end_comment

begin_class
DECL|class|FetchSubPhaseContext
specifier|public
class|class
name|FetchSubPhaseContext
block|{
comment|// This is to store if the FetchSubPhase should be executed at all.
DECL|field|hitExecutionNeeded
specifier|private
name|boolean
name|hitExecutionNeeded
init|=
literal|false
decl_stmt|;
comment|/**      * Set if this phase should be executed at all.      */
DECL|method|setHitExecutionNeeded
specifier|public
name|void
name|setHitExecutionNeeded
parameter_list|(
name|boolean
name|hitExecutionNeeded
parameter_list|)
block|{
name|this
operator|.
name|hitExecutionNeeded
operator|=
name|hitExecutionNeeded
expr_stmt|;
block|}
comment|/**      * Returns if this phase be executed at all.      */
DECL|method|hitExecutionNeeded
specifier|public
name|boolean
name|hitExecutionNeeded
parameter_list|()
block|{
return|return
name|hitExecutionNeeded
return|;
block|}
block|}
end_class

end_unit

