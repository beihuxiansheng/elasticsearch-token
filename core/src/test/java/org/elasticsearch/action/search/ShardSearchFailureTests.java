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
name|common
operator|.
name|ParsingException
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
name|bytes
operator|.
name|BytesReference
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
name|XContentParser
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
name|XContentType
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
name|Index
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
name|SearchShardTarget
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
operator|.
name|toXContent
import|;
end_import

begin_class
DECL|class|ShardSearchFailureTests
specifier|public
class|class
name|ShardSearchFailureTests
extends|extends
name|ESTestCase
block|{
DECL|method|createTestItem
specifier|public
specifier|static
name|ShardSearchFailure
name|createTestItem
parameter_list|()
block|{
name|String
name|randomMessage
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|Exception
name|ex
init|=
operator|new
name|ParsingException
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|randomMessage
argument_list|,
operator|new
name|IllegalArgumentException
argument_list|(
literal|"some bad argument"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|nodeId
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|indexName
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
name|indexUuid
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|shardId
init|=
name|randomInt
argument_list|()
decl_stmt|;
return|return
operator|new
name|ShardSearchFailure
argument_list|(
name|ex
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
name|nodeId
argument_list|,
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
name|indexName
argument_list|,
name|indexUuid
argument_list|)
argument_list|,
name|shardId
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|ShardSearchFailure
name|response
init|=
name|createTestItem
argument_list|()
decl_stmt|;
name|XContentType
name|xContentType
init|=
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|humanReadable
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|BytesReference
name|originalBytes
init|=
name|toXContent
argument_list|(
name|response
argument_list|,
name|xContentType
argument_list|,
name|humanReadable
argument_list|)
decl_stmt|;
name|ShardSearchFailure
name|parsed
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|xContentType
operator|.
name|xContent
argument_list|()
argument_list|,
name|originalBytes
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|parsed
operator|=
name|ShardSearchFailure
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|response
operator|.
name|index
argument_list|()
argument_list|,
name|parsed
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|shard
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|parsed
operator|.
name|shard
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|shardId
argument_list|()
argument_list|,
name|parsed
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
comment|// we cannot compare the cause, because it will be wrapped in an outer ElasticSearchException
comment|// best effort: try to check that the original message appears somewhere in the rendered xContent
name|String
name|originalMsg
init|=
name|response
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|parsed
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Elasticsearch exception [type=parsing_exception, reason="
operator|+
name|originalMsg
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|String
name|nestedMsg
init|=
name|response
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|parsed
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Elasticsearch exception [type=illegal_argument_exception, reason="
operator|+
name|nestedMsg
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testToXContent
specifier|public
name|void
name|testToXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|ShardSearchFailure
name|failure
init|=
operator|new
name|ShardSearchFailure
argument_list|(
operator|new
name|ParsingException
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|"some message"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"nodeId"
argument_list|,
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
literal|"indexName"
argument_list|,
literal|"indexUuid"
argument_list|)
argument_list|,
literal|123
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|BytesReference
name|xContent
init|=
name|toXContent
argument_list|(
name|failure
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"shard\":123,"
operator|+
literal|"\"index\":\"indexName\","
operator|+
literal|"\"node\":\"nodeId\","
operator|+
literal|"\"reason\":{"
operator|+
literal|"\"type\":\"parsing_exception\","
operator|+
literal|"\"reason\":\"some message\","
operator|+
literal|"\"line\":0,"
operator|+
literal|"\"col\":0"
operator|+
literal|"}"
operator|+
literal|"}"
argument_list|,
name|xContent
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

