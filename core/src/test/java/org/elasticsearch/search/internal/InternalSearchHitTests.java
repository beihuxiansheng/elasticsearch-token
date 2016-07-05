begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
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
name|InputStreamStreamInput
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
name|text
operator|.
name|Text
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|nullValue
import|;
end_import

begin_class
DECL|class|InternalSearchHitTests
specifier|public
class|class
name|InternalSearchHitTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSerializeShardTarget
specifier|public
name|void
name|testSerializeShardTarget
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchShardTarget
name|target
init|=
operator|new
name|SearchShardTarget
argument_list|(
literal|"_node_id"
argument_list|,
operator|new
name|Index
argument_list|(
literal|"_index"
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|InternalSearchHits
argument_list|>
name|innerHits
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|InternalSearchHit
name|innerHit1
init|=
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"_id"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"_type"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|innerHit1
operator|.
name|shardTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|InternalSearchHit
name|innerInnerHit2
init|=
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"_id"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"_type"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|innerInnerHit2
operator|.
name|shardTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|innerHits
operator|.
name|put
argument_list|(
literal|"1"
argument_list|,
operator|new
name|InternalSearchHits
argument_list|(
operator|new
name|InternalSearchHit
index|[]
block|{
name|innerInnerHit2
block|}
argument_list|,
literal|1
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|innerHit1
operator|.
name|setInnerHits
argument_list|(
name|innerHits
argument_list|)
expr_stmt|;
name|InternalSearchHit
name|innerHit2
init|=
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"_id"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"_type"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|innerHit2
operator|.
name|shardTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|InternalSearchHit
name|innerHit3
init|=
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"_id"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"_type"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|innerHit3
operator|.
name|shardTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|innerHits
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|InternalSearchHit
name|hit1
init|=
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"_id"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"_type"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|innerHits
operator|.
name|put
argument_list|(
literal|"1"
argument_list|,
operator|new
name|InternalSearchHits
argument_list|(
operator|new
name|InternalSearchHit
index|[]
block|{
name|innerHit1
block|,
name|innerHit2
block|}
argument_list|,
literal|1
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|innerHits
operator|.
name|put
argument_list|(
literal|"2"
argument_list|,
operator|new
name|InternalSearchHits
argument_list|(
operator|new
name|InternalSearchHit
index|[]
block|{
name|innerHit3
block|}
argument_list|,
literal|1
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
name|hit1
operator|.
name|shardTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|hit1
operator|.
name|setInnerHits
argument_list|(
name|innerHits
argument_list|)
expr_stmt|;
name|InternalSearchHit
name|hit2
init|=
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"_id"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"_type"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|hit2
operator|.
name|shardTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|InternalSearchHits
name|hits
init|=
operator|new
name|InternalSearchHits
argument_list|(
operator|new
name|InternalSearchHit
index|[]
block|{
name|hit1
block|,
name|hit2
block|}
argument_list|,
literal|2
argument_list|,
literal|1f
argument_list|)
decl_stmt|;
name|InternalSearchHits
operator|.
name|StreamContext
name|context
init|=
operator|new
name|InternalSearchHits
operator|.
name|StreamContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|streamShardTarget
argument_list|(
name|InternalSearchHits
operator|.
name|StreamContext
operator|.
name|ShardTargetType
operator|.
name|STREAM
argument_list|)
expr_stmt|;
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|hits
operator|.
name|writeTo
argument_list|(
name|output
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|InputStream
name|input
init|=
name|output
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
decl_stmt|;
name|context
operator|=
operator|new
name|InternalSearchHits
operator|.
name|StreamContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|streamShardTarget
argument_list|(
name|InternalSearchHits
operator|.
name|StreamContext
operator|.
name|ShardTargetType
operator|.
name|STREAM
argument_list|)
expr_stmt|;
name|InternalSearchHits
name|results
init|=
name|InternalSearchHits
operator|.
name|readSearchHits
argument_list|(
operator|new
name|InputStreamStreamInput
argument_list|(
name|input
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|shard
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getInnerHits
argument_list|()
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|shard
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getInnerHits
argument_list|()
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getInnerHits
argument_list|()
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|shard
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getInnerHits
argument_list|()
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|shard
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getInnerHits
argument_list|()
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|shard
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|shard
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

