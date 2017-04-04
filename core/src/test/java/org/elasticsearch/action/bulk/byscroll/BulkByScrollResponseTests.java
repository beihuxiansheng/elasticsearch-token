begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk.byscroll
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
operator|.
name|byscroll
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
operator|.
name|BulkItemResponse
operator|.
name|Failure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
operator|.
name|byscroll
operator|.
name|ScrollableHitSource
operator|.
name|SearchFailure
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
name|BytesStreamOutput
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
name|StreamInput
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
operator|.
name|randomSimpleString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueMillis
import|;
end_import

begin_class
DECL|class|BulkByScrollResponseTests
specifier|public
class|class
name|BulkByScrollResponseTests
extends|extends
name|ESTestCase
block|{
DECL|method|testRountTrip
specifier|public
name|void
name|testRountTrip
parameter_list|()
throws|throws
name|IOException
block|{
name|BulkByScrollResponse
name|response
init|=
operator|new
name|BulkByScrollResponse
argument_list|(
name|timeValueMillis
argument_list|(
name|randomNonNegativeLong
argument_list|()
argument_list|)
argument_list|,
name|BulkByScrollTaskStatusTests
operator|.
name|randomStatus
argument_list|()
argument_list|,
name|randomIndexingFailures
argument_list|()
argument_list|,
name|randomSearchFailures
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|BulkByScrollResponse
name|tripped
init|=
operator|new
name|BulkByScrollResponse
argument_list|()
decl_stmt|;
try|try
init|(
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|response
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
init|)
block|{
name|tripped
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
name|assertResponseEquals
argument_list|(
name|response
argument_list|,
name|tripped
argument_list|)
expr_stmt|;
block|}
DECL|method|randomIndexingFailures
specifier|private
name|List
argument_list|<
name|Failure
argument_list|>
name|randomIndexingFailures
parameter_list|()
block|{
return|return
name|usually
argument_list|()
condition|?
name|emptyList
argument_list|()
else|:
name|singletonList
argument_list|(
operator|new
name|Failure
argument_list|(
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
operator|new
name|IllegalArgumentException
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|randomSearchFailures
specifier|private
name|List
argument_list|<
name|SearchFailure
argument_list|>
name|randomSearchFailures
parameter_list|()
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
name|String
name|index
init|=
literal|null
decl_stmt|;
name|Integer
name|shardId
init|=
literal|null
decl_stmt|;
name|String
name|nodeId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|index
operator|=
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|shardId
operator|=
name|randomInt
argument_list|()
expr_stmt|;
name|nodeId
operator|=
name|usually
argument_list|()
condition|?
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
return|return
name|singletonList
argument_list|(
operator|new
name|SearchFailure
argument_list|(
operator|new
name|ElasticsearchException
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|index
argument_list|,
name|shardId
argument_list|,
name|nodeId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertResponseEquals
specifier|private
name|void
name|assertResponseEquals
parameter_list|(
name|BulkByScrollResponse
name|expected
parameter_list|,
name|BulkByScrollResponse
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|getTook
argument_list|()
argument_list|,
name|actual
operator|.
name|getTook
argument_list|()
argument_list|)
expr_stmt|;
name|BulkByScrollTaskStatusTests
operator|.
name|assertTaskStatusEquals
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|,
name|expected
operator|.
name|getStatus
argument_list|()
argument_list|,
name|actual
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getBulkFailures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actual
operator|.
name|getBulkFailures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expected
operator|.
name|getBulkFailures
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Failure
name|expectedFailure
init|=
name|expected
operator|.
name|getBulkFailures
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Failure
name|actualFailure
init|=
name|actual
operator|.
name|getBulkFailures
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getIndex
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getType
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getId
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getMessage
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getStatus
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
operator|.
name|getSearchFailures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actual
operator|.
name|getSearchFailures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expected
operator|.
name|getSearchFailures
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SearchFailure
name|expectedFailure
init|=
name|expected
operator|.
name|getSearchFailures
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SearchFailure
name|actualFailure
init|=
name|actual
operator|.
name|getSearchFailures
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getIndex
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getShardId
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getShardId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getReason
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getReason
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFailure
operator|.
name|getReason
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|actualFailure
operator|.
name|getReason
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

