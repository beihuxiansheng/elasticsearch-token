begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.sniff
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|sniff
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RestClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RestClientBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|util
operator|.
name|List
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
name|Executors
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
name|ScheduledExecutorService
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
name|ScheduledFuture
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Class responsible for sniffing nodes from some source (default is elasticsearch itself) and setting them to a provided instance of  * {@link RestClient}. Must be created via {@link SnifferBuilder}, which allows to set all of the different options or rely on defaults.  * A background task fetches the nodes through the {@link HostsSniffer} and sets them to the {@link RestClient} instance.  * It is possible to perform sniffing on failure by creating a {@link SniffOnFailureListener} and providing it as an argument to  * {@link RestClientBuilder#setFailureListener(RestClient.FailureListener)}. The Sniffer implementation needs to be lazily set to the  * previously created SniffOnFailureListener through {@link SniffOnFailureListener#setSniffer(Sniffer)}.  */
end_comment

begin_class
DECL|class|Sniffer
specifier|public
specifier|final
class|class
name|Sniffer
implements|implements
name|Closeable
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Log
name|logger
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Sniffer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|task
specifier|private
specifier|final
name|Task
name|task
decl_stmt|;
DECL|method|Sniffer
name|Sniffer
parameter_list|(
name|RestClient
name|restClient
parameter_list|,
name|HostsSniffer
name|hostsSniffer
parameter_list|,
name|long
name|sniffInterval
parameter_list|,
name|long
name|sniffAfterFailureDelay
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
operator|new
name|Task
argument_list|(
name|hostsSniffer
argument_list|,
name|restClient
argument_list|,
name|sniffInterval
argument_list|,
name|sniffAfterFailureDelay
argument_list|)
expr_stmt|;
block|}
comment|/**      * Triggers a new sniffing round and explicitly takes out the failed host provided as argument      */
DECL|method|sniffOnFailure
specifier|public
name|void
name|sniffOnFailure
parameter_list|(
name|HttpHost
name|failedHost
parameter_list|)
block|{
name|this
operator|.
name|task
operator|.
name|sniffOnFailure
argument_list|(
name|failedHost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|task
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|class|Task
specifier|private
specifier|static
class|class
name|Task
implements|implements
name|Runnable
block|{
DECL|field|hostsSniffer
specifier|private
specifier|final
name|HostsSniffer
name|hostsSniffer
decl_stmt|;
DECL|field|restClient
specifier|private
specifier|final
name|RestClient
name|restClient
decl_stmt|;
DECL|field|sniffIntervalMillis
specifier|private
specifier|final
name|long
name|sniffIntervalMillis
decl_stmt|;
DECL|field|sniffAfterFailureDelayMillis
specifier|private
specifier|final
name|long
name|sniffAfterFailureDelayMillis
decl_stmt|;
DECL|field|scheduledExecutorService
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduledExecutorService
decl_stmt|;
DECL|field|running
specifier|private
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|scheduledFuture
specifier|private
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduledFuture
decl_stmt|;
DECL|method|Task
specifier|private
name|Task
parameter_list|(
name|HostsSniffer
name|hostsSniffer
parameter_list|,
name|RestClient
name|restClient
parameter_list|,
name|long
name|sniffIntervalMillis
parameter_list|,
name|long
name|sniffAfterFailureDelayMillis
parameter_list|)
block|{
name|this
operator|.
name|hostsSniffer
operator|=
name|hostsSniffer
expr_stmt|;
name|this
operator|.
name|restClient
operator|=
name|restClient
expr_stmt|;
name|this
operator|.
name|sniffIntervalMillis
operator|=
name|sniffIntervalMillis
expr_stmt|;
name|this
operator|.
name|sniffAfterFailureDelayMillis
operator|=
name|sniffAfterFailureDelayMillis
expr_stmt|;
name|this
operator|.
name|scheduledExecutorService
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|scheduleNextRun
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|scheduleNextRun
specifier|synchronized
name|void
name|scheduleNextRun
parameter_list|(
name|long
name|delayMillis
parameter_list|)
block|{
if|if
condition|(
name|scheduledExecutorService
operator|.
name|isShutdown
argument_list|()
operator|==
literal|false
condition|)
block|{
try|try
block|{
if|if
condition|(
name|scheduledFuture
operator|!=
literal|null
condition|)
block|{
comment|//regardless of when the next sniff is scheduled, cancel it and schedule a new one with updated delay
name|this
operator|.
name|scheduledFuture
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"scheduling next sniff in "
operator|+
name|delayMillis
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduledFuture
operator|=
name|this
operator|.
name|scheduledExecutorService
operator|.
name|schedule
argument_list|(
name|this
argument_list|,
name|delayMillis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"error while scheduling next sniffer task"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|sniff
argument_list|(
literal|null
argument_list|,
name|sniffIntervalMillis
argument_list|)
expr_stmt|;
block|}
DECL|method|sniffOnFailure
name|void
name|sniffOnFailure
parameter_list|(
name|HttpHost
name|failedHost
parameter_list|)
block|{
name|sniff
argument_list|(
name|failedHost
argument_list|,
name|sniffAfterFailureDelayMillis
argument_list|)
expr_stmt|;
block|}
DECL|method|sniff
name|void
name|sniff
parameter_list|(
name|HttpHost
name|excludeHost
parameter_list|,
name|long
name|nextSniffDelayMillis
parameter_list|)
block|{
if|if
condition|(
name|running
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
name|List
argument_list|<
name|HttpHost
argument_list|>
name|sniffedHosts
init|=
name|hostsSniffer
operator|.
name|sniffHosts
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"sniffed hosts: "
operator|+
name|sniffedHosts
argument_list|)
expr_stmt|;
if|if
condition|(
name|excludeHost
operator|!=
literal|null
condition|)
block|{
name|sniffedHosts
operator|.
name|remove
argument_list|(
name|excludeHost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sniffedHosts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"no hosts to set, hosts will be updated at the next sniffing round"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|restClient
operator|.
name|setHosts
argument_list|(
name|sniffedHosts
operator|.
name|toArray
argument_list|(
operator|new
name|HttpHost
index|[
name|sniffedHosts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"error while sniffing nodes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|scheduleNextRun
argument_list|(
name|nextSniffDelayMillis
argument_list|)
expr_stmt|;
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|shutdown
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
name|scheduledExecutorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|scheduledExecutorService
operator|.
name|awaitTermination
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
return|return;
block|}
name|scheduledExecutorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Returns a new {@link SnifferBuilder} to help with {@link Sniffer} creation.      *      * @param restClient the client that gets its hosts set (via {@link RestClient#setHosts(HttpHost...)}) once they are fetched      * @return a new instance of {@link SnifferBuilder}      */
DECL|method|builder
specifier|public
specifier|static
name|SnifferBuilder
name|builder
parameter_list|(
name|RestClient
name|restClient
parameter_list|)
block|{
return|return
operator|new
name|SnifferBuilder
argument_list|(
name|restClient
argument_list|)
return|;
block|}
block|}
end_class

end_unit

