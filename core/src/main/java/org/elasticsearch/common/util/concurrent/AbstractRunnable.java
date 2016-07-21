begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
package|;
end_package

begin_comment
comment|/**  * An extension to runnable.  */
end_comment

begin_class
DECL|class|AbstractRunnable
specifier|public
specifier|abstract
class|class
name|AbstractRunnable
implements|implements
name|Runnable
block|{
comment|/**      * Should the runnable force its execution in case it gets rejected?      */
DECL|method|isForceExecution
specifier|public
name|boolean
name|isForceExecution
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
specifier|final
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|doRun
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|onAfter
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This method is called in a finally block after successful execution      * or on a rejection.      */
DECL|method|onAfter
specifier|public
name|void
name|onAfter
parameter_list|()
block|{
comment|// nothing by default
block|}
comment|/**      * This method is invoked for all exception thrown by {@link #doRun()}      */
DECL|method|onFailure
specifier|public
specifier|abstract
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
function_decl|;
comment|/**      * This should be executed if the thread-pool executing this action rejected the execution.      * The default implementation forwards to {@link #onFailure(Exception)}      */
DECL|method|onRejection
specifier|public
name|void
name|onRejection
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method has the same semantics as {@link Runnable#run()}      * @throws InterruptedException if the run method throws an InterruptedException      */
DECL|method|doRun
specifier|protected
specifier|abstract
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

