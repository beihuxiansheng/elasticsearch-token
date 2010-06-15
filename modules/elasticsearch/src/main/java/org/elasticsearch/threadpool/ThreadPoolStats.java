begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.threadpool
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Thread Pool level stats.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ThreadPoolStats
specifier|public
class|class
name|ThreadPoolStats
implements|implements
name|Streamable
implements|,
name|Serializable
implements|,
name|ToXContent
block|{
DECL|field|poolSize
specifier|private
name|int
name|poolSize
decl_stmt|;
DECL|field|activeCount
specifier|private
name|int
name|activeCount
decl_stmt|;
DECL|field|schedulerPoolSize
specifier|private
name|int
name|schedulerPoolSize
decl_stmt|;
DECL|field|schedulerActiveCount
specifier|private
name|int
name|schedulerActiveCount
decl_stmt|;
DECL|method|ThreadPoolStats
name|ThreadPoolStats
parameter_list|()
block|{     }
DECL|method|ThreadPoolStats
specifier|public
name|ThreadPoolStats
parameter_list|(
name|int
name|poolSize
parameter_list|,
name|int
name|activeCount
parameter_list|,
name|int
name|schedulerPoolSize
parameter_list|,
name|int
name|schedulerActiveCount
parameter_list|)
block|{
name|this
operator|.
name|poolSize
operator|=
name|poolSize
expr_stmt|;
name|this
operator|.
name|activeCount
operator|=
name|activeCount
expr_stmt|;
name|this
operator|.
name|schedulerPoolSize
operator|=
name|schedulerPoolSize
expr_stmt|;
name|this
operator|.
name|schedulerActiveCount
operator|=
name|schedulerActiveCount
expr_stmt|;
block|}
DECL|method|readThreadPoolStats
specifier|public
specifier|static
name|ThreadPoolStats
name|readThreadPoolStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ThreadPoolStats
name|stats
init|=
operator|new
name|ThreadPoolStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|poolSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|activeCount
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|schedulerPoolSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|schedulerActiveCount
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|poolSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|activeCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|schedulerPoolSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|schedulerActiveCount
argument_list|)
expr_stmt|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"thread_pool"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"pool_size"
argument_list|,
name|poolSize
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"active_count"
argument_list|,
name|activeCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"scheduler_pool_size"
argument_list|,
name|schedulerPoolSize
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"scheduler_active_count"
argument_list|,
name|schedulerActiveCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the current number of threads in the pool.      *      * @return the number of threads      */
DECL|method|poolSize
specifier|public
name|int
name|poolSize
parameter_list|()
block|{
return|return
name|poolSize
return|;
block|}
comment|/**      * Returns the current number of threads in the pool.      *      * @return the number of threads      */
DECL|method|getPoolSize
specifier|public
name|int
name|getPoolSize
parameter_list|()
block|{
return|return
name|poolSize
argument_list|()
return|;
block|}
comment|/**      * Returns the approximate number of threads that are actively      * executing tasks.      *      * @return the number of threads      */
DECL|method|activeCount
specifier|public
name|int
name|activeCount
parameter_list|()
block|{
return|return
name|activeCount
return|;
block|}
comment|/**      * Returns the approximate number of threads that are actively      * executing tasks.      *      * @return the number of threads      */
DECL|method|getActiveCount
specifier|public
name|int
name|getActiveCount
parameter_list|()
block|{
return|return
name|activeCount
argument_list|()
return|;
block|}
comment|/**      * The size of the scheduler thread pool.      */
DECL|method|schedulerPoolSize
specifier|public
name|int
name|schedulerPoolSize
parameter_list|()
block|{
return|return
name|schedulerPoolSize
return|;
block|}
comment|/**      * The size of the scheduler thread pool.      */
DECL|method|getSchedulerPoolSize
specifier|public
name|int
name|getSchedulerPoolSize
parameter_list|()
block|{
return|return
name|schedulerPoolSize
argument_list|()
return|;
block|}
comment|/**      * The approximate number of threads that are actively executing scheduled      * tasks.      */
DECL|method|schedulerActiveCount
specifier|public
name|int
name|schedulerActiveCount
parameter_list|()
block|{
return|return
name|schedulerActiveCount
return|;
block|}
comment|/**      * The approximate number of threads that are actively executing scheduled      * tasks.      */
DECL|method|getSchedulerActiveCount
specifier|public
name|int
name|getSchedulerActiveCount
parameter_list|()
block|{
return|return
name|schedulerActiveCount
argument_list|()
return|;
block|}
block|}
end_class

end_unit

