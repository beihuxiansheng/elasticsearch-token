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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Holds the state of a dead connection to a host. Keeps track of how many failed attempts were performed and  * when the host should be retried (based on number of previous failed attempts).  * Class is immutable, a new copy of it should be created each time the state has to be changed.  */
end_comment

begin_class
DECL|class|DeadHostState
class|class
name|DeadHostState
block|{
DECL|field|MIN_CONNECTION_TIMEOUT_NANOS
specifier|private
specifier|static
specifier|final
name|long
name|MIN_CONNECTION_TIMEOUT_NANOS
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toNanos
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|MAX_CONNECTION_TIMEOUT_NANOS
specifier|private
specifier|static
specifier|final
name|long
name|MAX_CONNECTION_TIMEOUT_NANOS
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toNanos
argument_list|(
literal|30
argument_list|)
decl_stmt|;
DECL|field|INITIAL_DEAD_STATE
specifier|static
specifier|final
name|DeadHostState
name|INITIAL_DEAD_STATE
init|=
operator|new
name|DeadHostState
argument_list|()
decl_stmt|;
DECL|field|failedAttempts
specifier|private
specifier|final
name|int
name|failedAttempts
decl_stmt|;
DECL|field|deadUntilNanos
specifier|private
specifier|final
name|long
name|deadUntilNanos
decl_stmt|;
DECL|method|DeadHostState
specifier|private
name|DeadHostState
parameter_list|()
block|{
name|this
operator|.
name|failedAttempts
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|deadUntilNanos
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|MIN_CONNECTION_TIMEOUT_NANOS
expr_stmt|;
block|}
DECL|method|DeadHostState
name|DeadHostState
parameter_list|(
name|DeadHostState
name|previousDeadHostState
parameter_list|)
block|{
name|long
name|timeoutNanos
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|min
argument_list|(
name|MIN_CONNECTION_TIMEOUT_NANOS
operator|*
literal|2
operator|*
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|previousDeadHostState
operator|.
name|failedAttempts
operator|*
literal|0.5
operator|-
literal|1
argument_list|)
argument_list|,
name|MAX_CONNECTION_TIMEOUT_NANOS
argument_list|)
decl_stmt|;
name|this
operator|.
name|deadUntilNanos
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|timeoutNanos
expr_stmt|;
name|this
operator|.
name|failedAttempts
operator|=
name|previousDeadHostState
operator|.
name|failedAttempts
operator|+
literal|1
expr_stmt|;
block|}
comment|/**      * Returns the timestamp (nanos) till the host is supposed to stay dead without being retried.      * After that the host should be retried.      */
DECL|method|getDeadUntilNanos
name|long
name|getDeadUntilNanos
parameter_list|()
block|{
return|return
name|deadUntilNanos
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DeadHostState{"
operator|+
literal|"failedAttempts="
operator|+
name|failedAttempts
operator|+
literal|", deadUntilNanos="
operator|+
name|deadUntilNanos
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

