begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Objects
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
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * Base static connection pool implementation that deals with mutable connections. Marks connections as dead/alive when needed.  * Provides a stream of alive connections or dead ones that should be retried for each {@link #nextConnection()} call, which  * allows to filter connections through a customizable {@link Predicate}, called connection selector.  * In case the returned stream is empty a last resort dead connection should be retrieved by calling {@link #lastResortConnection()}  * and resurrected so that a single request attempt can be performed.  * The {@link #onSuccess(Connection)} method marks the connection provided as an argument alive.  * The {@link #onFailure(Connection)} method marks the connection provided as an argument dead.  * This base implementation doesn't define the list implementation that stores connections, so that concurrency can be  * handled in the subclasses depending on the usecase (e.g. defining the list volatile when needed).  */
end_comment

begin_class
DECL|class|AbstractStaticConnectionPool
specifier|public
specifier|abstract
class|class
name|AbstractStaticConnectionPool
implements|implements
name|ConnectionPool
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
name|AbstractStaticConnectionPool
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|lastConnectionIndex
specifier|private
specifier|final
name|AtomicInteger
name|lastConnectionIndex
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|getConnections
specifier|protected
specifier|abstract
name|List
argument_list|<
name|Connection
argument_list|>
name|getConnections
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|nextConnection
specifier|public
specifier|final
name|Stream
argument_list|<
name|Connection
argument_list|>
name|nextConnection
parameter_list|()
block|{
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
name|getConnections
argument_list|()
decl_stmt|;
if|if
condition|(
name|connections
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no connections available in the connection pool"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Connection
argument_list|>
name|sortedConnections
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|connections
argument_list|)
decl_stmt|;
comment|//TODO is it possible to make this O(1)? (rotate is O(n))
name|Collections
operator|.
name|rotate
argument_list|(
name|sortedConnections
argument_list|,
name|sortedConnections
operator|.
name|size
argument_list|()
operator|-
name|lastConnectionIndex
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sortedConnections
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|connection
lambda|->
name|connection
operator|.
name|isAlive
argument_list|()
operator|||
name|connection
operator|.
name|shouldBeRetried
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createConnections
specifier|protected
name|List
argument_list|<
name|Connection
argument_list|>
name|createConnections
parameter_list|(
name|HttpHost
modifier|...
name|hosts
parameter_list|)
block|{
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|HttpHost
name|host
range|:
name|hosts
control|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|host
argument_list|,
literal|"host cannot be null"
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
operator|new
name|Connection
argument_list|(
name|host
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|connections
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lastResortConnection
specifier|public
name|Connection
name|lastResortConnection
parameter_list|()
block|{
name|Connection
name|Connection
init|=
name|getConnections
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|sorted
argument_list|(
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getDeadUntil
argument_list|()
argument_list|,
name|o2
operator|.
name|getDeadUntil
argument_list|()
argument_list|)
argument_list|)
operator|.
name|findFirst
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|Connection
operator|.
name|markResurrected
argument_list|()
expr_stmt|;
return|return
name|Connection
return|;
block|}
annotation|@
name|Override
DECL|method|onSuccess
specifier|public
name|void
name|onSuccess
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|connection
operator|.
name|markAlive
argument_list|()
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"marked connection alive for "
operator|+
name|connection
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|IOException
block|{
name|connection
operator|.
name|markDead
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"marked connection dead for "
operator|+
name|connection
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

