begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.threadpool.blocking
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|blocking
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|jmx
operator|.
name|MBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|jmx
operator|.
name|ManagedAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|guice
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|MBean
argument_list|(
name|objectName
operator|=
literal|"service=threadpool,threadpoolType=blocking"
argument_list|,
name|description
operator|=
literal|"Blocking Thread Pool"
argument_list|)
DECL|class|BlockingThreadPoolManagement
specifier|public
class|class
name|BlockingThreadPoolManagement
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|BlockingThreadPool
name|threadPool
decl_stmt|;
DECL|method|BlockingThreadPoolManagement
annotation|@
name|Inject
specifier|public
name|BlockingThreadPoolManagement
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|this
operator|.
name|threadPool
operator|=
operator|(
name|BlockingThreadPool
operator|)
name|threadPool
expr_stmt|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Minimum number Of threads"
argument_list|)
DECL|method|getMin
specifier|public
name|long
name|getMin
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|min
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Maximum number of threads"
argument_list|)
DECL|method|getMax
specifier|public
name|int
name|getMax
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|max
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Number of scheduler threads"
argument_list|)
DECL|method|getScheduleSize
specifier|public
name|int
name|getScheduleSize
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|scheduledSize
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Thread keep alive"
argument_list|)
DECL|method|getKeepAlive
specifier|public
name|String
name|getKeepAlive
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|keepAlive
operator|.
name|format
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Thread keep alive (in seconds)"
argument_list|)
DECL|method|getKeepAliveInSeconds
specifier|public
name|long
name|getKeepAliveInSeconds
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|keepAlive
operator|.
name|seconds
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Current number of threads in the pool"
argument_list|)
DECL|method|getPoolSize
specifier|public
name|long
name|getPoolSize
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|getPoolSize
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Approximate number of threads that are actively executing tasks"
argument_list|)
DECL|method|getActiveCount
specifier|public
name|long
name|getActiveCount
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|getActiveCount
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Current number of threads in the scheduler pool"
argument_list|)
DECL|method|getSchedulerPoolSize
specifier|public
name|long
name|getSchedulerPoolSize
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|getSchedulerPoolSize
argument_list|()
return|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Approximate number of threads that are actively executing scheduled tasks"
argument_list|)
DECL|method|getSchedulerActiveCount
specifier|public
name|long
name|getSchedulerActiveCount
parameter_list|()
block|{
return|return
name|threadPool
operator|.
name|getSchedulerActiveCount
argument_list|()
return|;
block|}
block|}
end_class

end_unit

