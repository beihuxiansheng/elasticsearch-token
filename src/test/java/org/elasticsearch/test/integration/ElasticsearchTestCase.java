begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|ThreadFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakFilters
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|TimeoutSuite
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|AbstractRandomizedTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TimeUnits
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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

begin_class
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|ElasticsearchTestCase
operator|.
name|ElasticSearchThreadFilter
operator|.
name|class
block|}
argument_list|)
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
name|TimeUnits
operator|.
name|HOUR
argument_list|)
comment|// timeout the suite after 1h and fail the test.
DECL|class|ElasticsearchTestCase
specifier|public
specifier|abstract
class|class
name|ElasticsearchTestCase
extends|extends
name|AbstractRandomizedTest
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|CHILD_VM_ID
specifier|public
specifier|static
specifier|final
name|String
name|CHILD_VM_ID
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"junit4.childvm.id"
argument_list|,
literal|""
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
DECL|class|ElasticSearchThreadFilter
specifier|public
specifier|static
class|class
name|ElasticSearchThreadFilter
implements|implements
name|ThreadFilter
block|{
annotation|@
name|Override
DECL|method|reject
specifier|public
name|boolean
name|reject
parameter_list|(
name|Thread
name|t
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|awaitBusy
specifier|public
name|void
name|awaitBusy
parameter_list|(
name|Predicate
argument_list|<
name|?
argument_list|>
name|breakPredicate
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|awaitBusy
argument_list|(
name|breakPredicate
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|awaitBusy
specifier|public
name|void
name|awaitBusy
parameter_list|(
name|Predicate
argument_list|<
name|?
argument_list|>
name|breakPredicate
parameter_list|,
name|long
name|maxWaitTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|maxTimeInMillis
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|maxWaitTime
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|long
name|iterations
init|=
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|log10
argument_list|(
name|maxTimeInMillis
argument_list|)
operator|/
name|Math
operator|.
name|log10
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|timeInMillis
init|=
literal|1
decl_stmt|;
name|long
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|breakPredicate
operator|.
name|apply
argument_list|(
literal|null
argument_list|)
condition|)
block|{
return|return;
block|}
name|sum
operator|+=
name|timeInMillis
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|timeInMillis
argument_list|)
expr_stmt|;
name|timeInMillis
operator|*=
literal|2
expr_stmt|;
block|}
name|timeInMillis
operator|=
name|maxTimeInMillis
operator|-
name|sum
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|timeInMillis
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

