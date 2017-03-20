begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|GroupShardsIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ShardIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ShardRouting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchPhaseResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
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
name|AtomicLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThanOrEqualTo
import|;
end_import

begin_class
DECL|class|AbstractSearchAsyncActionTookTests
specifier|public
class|class
name|AbstractSearchAsyncActionTookTests
extends|extends
name|ESTestCase
block|{
DECL|method|createAction
specifier|private
name|AbstractSearchAsyncAction
argument_list|<
name|SearchPhaseResult
argument_list|>
name|createAction
parameter_list|(
specifier|final
name|boolean
name|controlled
parameter_list|,
specifier|final
name|AtomicLong
name|expected
parameter_list|)
block|{
specifier|final
name|Runnable
name|runnable
decl_stmt|;
specifier|final
name|TransportSearchAction
operator|.
name|SearchTimeProvider
name|timeProvider
decl_stmt|;
if|if
condition|(
name|controlled
condition|)
block|{
name|runnable
operator|=
parameter_list|()
lambda|->
name|expected
operator|.
name|set
argument_list|(
name|randomNonNegativeLong
argument_list|()
argument_list|)
expr_stmt|;
name|timeProvider
operator|=
operator|new
name|TransportSearchAction
operator|.
name|SearchTimeProvider
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|expected
operator|::
name|get
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|runnable
operator|=
parameter_list|()
lambda|->
block|{
name|long
name|elapsed
init|=
name|spinForAtLeastNMilliseconds
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|expected
operator|.
name|set
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
block|}
expr_stmt|;
name|timeProvider
operator|=
operator|new
name|TransportSearchAction
operator|.
name|SearchTimeProvider
argument_list|(
literal|0
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|System
operator|::
name|nanoTime
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ShardIterator
name|it
init|=
operator|new
name|ShardIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{              }
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|ShardIterator
name|o
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|sizeActive
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|ShardRouting
name|nextOrNull
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|remaining
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|ShardRouting
argument_list|>
name|asUnordered
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|AbstractSearchAsyncAction
argument_list|<
name|SearchPhaseResult
argument_list|>
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|GroupShardsIterator
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|it
argument_list|)
argument_list|)
argument_list|,
name|timeProvider
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|SearchPhase
name|getNextPhase
parameter_list|(
specifier|final
name|SearchPhaseResults
argument_list|<
name|SearchPhaseResult
argument_list|>
name|results
parameter_list|,
specifier|final
name|SearchPhaseContext
name|context
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|executePhaseOnShard
parameter_list|(
specifier|final
name|ShardIterator
name|shardIt
parameter_list|,
specifier|final
name|ShardRouting
name|shard
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchPhaseResult
argument_list|>
name|listener
parameter_list|)
block|{              }
annotation|@
name|Override
name|long
name|buildTookInMillis
parameter_list|()
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|buildTookInMillis
argument_list|()
return|;
block|}
block|}
return|;
block|}
DECL|method|testTookWithControlledClock
specifier|public
name|void
name|testTookWithControlledClock
parameter_list|()
block|{
name|runTestTook
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testTookWithRealClock
specifier|public
name|void
name|testTookWithRealClock
parameter_list|()
block|{
name|runTestTook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestTook
specifier|private
name|void
name|runTestTook
parameter_list|(
specifier|final
name|boolean
name|controlled
parameter_list|)
block|{
specifier|final
name|AtomicLong
name|expected
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|AbstractSearchAsyncAction
argument_list|<
name|SearchPhaseResult
argument_list|>
name|action
init|=
name|createAction
argument_list|(
name|controlled
argument_list|,
name|expected
argument_list|)
decl_stmt|;
specifier|final
name|long
name|actual
init|=
name|action
operator|.
name|buildTookInMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|controlled
condition|)
block|{
comment|// with a controlled clock, we can assert the exact took time
name|assertThat
argument_list|(
name|actual
argument_list|,
name|equalTo
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|expected
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// with a real clock, the best we can say is that it took as long as we spun for
name|assertThat
argument_list|(
name|actual
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|expected
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

