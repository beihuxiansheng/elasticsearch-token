begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|RejectedExecutionHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|EsAbortPolicy
specifier|public
class|class
name|EsAbortPolicy
implements|implements
name|RejectedExecutionHandler
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|EsAbortPolicy
name|INSTANCE
init|=
operator|new
name|EsAbortPolicy
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|rejectedExecution
specifier|public
name|void
name|rejectedExecution
parameter_list|(
name|Runnable
name|r
parameter_list|,
name|ThreadPoolExecutor
name|executor
parameter_list|)
block|{
throw|throw
operator|new
name|EsRejectedExecutionException
argument_list|(
literal|"rejected execution of ["
operator|+
name|r
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

