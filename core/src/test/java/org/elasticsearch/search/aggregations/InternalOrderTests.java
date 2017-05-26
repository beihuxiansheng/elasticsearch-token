begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
package|;
end_package

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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Writeable
operator|.
name|Reader
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
name|XContentFactory
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
name|XContentParser
operator|.
name|Token
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
name|search
operator|.
name|aggregations
operator|.
name|InternalOrder
operator|.
name|CompoundOrder
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
name|AbstractSerializingTestCase
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
name|VersionUtils
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
name|List
import|;
end_import

begin_class
DECL|class|InternalOrderTests
specifier|public
class|class
name|InternalOrderTests
extends|extends
name|AbstractSerializingTestCase
argument_list|<
name|BucketOrder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|BucketOrder
name|createTestInstance
parameter_list|()
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
return|return
name|getRandomOrder
argument_list|()
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|BucketOrder
argument_list|>
name|orders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|randomInt
argument_list|(
literal|3
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|orders
operator|.
name|add
argument_list|(
name|getRandomOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|BucketOrder
operator|.
name|compound
argument_list|(
name|orders
argument_list|)
return|;
block|}
block|}
DECL|method|getRandomOrder
specifier|private
name|BucketOrder
name|getRandomOrder
parameter_list|()
block|{
switch|switch
condition|(
name|randomInt
argument_list|(
literal|2
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|BucketOrder
operator|.
name|key
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|BucketOrder
operator|.
name|count
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
return|;
default|default:
return|return
name|BucketOrder
operator|.
name|aggregation
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Reader
argument_list|<
name|BucketOrder
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalOrder
operator|.
name|Streams
operator|::
name|readOrder
return|;
block|}
annotation|@
name|Override
DECL|method|doParseInstance
specifier|protected
name|BucketOrder
name|doParseInstance
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
return|return
name|InternalOrder
operator|.
name|Parser
operator|.
name|parseOrderParam
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
return|;
block|}
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|List
argument_list|<
name|BucketOrder
argument_list|>
name|orders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|==
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|orders
operator|.
name|add
argument_list|(
name|InternalOrder
operator|.
name|Parser
operator|.
name|parseOrderParam
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|BucketOrder
operator|.
name|compound
argument_list|(
name|orders
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|assertSerialization
specifier|protected
name|BucketOrder
name|assertSerialization
parameter_list|(
name|BucketOrder
name|testInstance
parameter_list|)
throws|throws
name|IOException
block|{
comment|// identical behavior to AbstractWireSerializingTestCase, except assertNotSame is only called for
comment|// compound and aggregation order because _key and _count orders are static instances.
name|BucketOrder
name|deserializedInstance
init|=
name|copyInstance
argument_list|(
name|testInstance
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testInstance
argument_list|,
name|deserializedInstance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testInstance
operator|.
name|hashCode
argument_list|()
argument_list|,
name|deserializedInstance
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|testInstance
operator|instanceof
name|CompoundOrder
operator|||
name|testInstance
operator|instanceof
name|InternalOrder
operator|.
name|Aggregation
condition|)
block|{
name|assertNotSame
argument_list|(
name|testInstance
argument_list|,
name|deserializedInstance
argument_list|)
expr_stmt|;
block|}
return|return
name|deserializedInstance
return|;
block|}
annotation|@
name|Override
DECL|method|assertParsedInstance
specifier|protected
name|void
name|assertParsedInstance
parameter_list|(
name|XContentType
name|xContentType
parameter_list|,
name|BytesReference
name|instanceAsBytes
parameter_list|,
name|BucketOrder
name|expectedInstance
parameter_list|)
throws|throws
name|IOException
block|{
comment|// identical behavior to AbstractSerializingTestCase, except assertNotSame is only called for
comment|// compound and aggregation order because _key and _count orders are static instances.
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|xContentType
argument_list|)
argument_list|,
name|instanceAsBytes
argument_list|)
decl_stmt|;
name|BucketOrder
name|newInstance
init|=
name|parseInstance
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedInstance
argument_list|,
name|newInstance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInstance
operator|.
name|hashCode
argument_list|()
argument_list|,
name|newInstance
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedInstance
operator|instanceof
name|CompoundOrder
operator|||
name|expectedInstance
operator|instanceof
name|InternalOrder
operator|.
name|Aggregation
condition|)
block|{
name|assertNotSame
argument_list|(
name|newInstance
argument_list|,
name|expectedInstance
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testHistogramOrderBwc
specifier|public
name|void
name|testHistogramOrderBwc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TEST_RUNS
condition|;
name|runs
operator|++
control|)
block|{
name|BucketOrder
name|order
init|=
name|createTestInstance
argument_list|()
decl_stmt|;
name|Version
name|bwcVersion
init|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|getFirstVersion
argument_list|()
argument_list|,
name|VersionUtils
operator|.
name|getPreviousVersion
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha2
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|bwcOrderFlag
init|=
name|randomBoolean
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
name|out
operator|.
name|setVersion
argument_list|(
name|bwcVersion
argument_list|)
expr_stmt|;
name|InternalOrder
operator|.
name|Streams
operator|.
name|writeHistogramOrder
argument_list|(
name|order
argument_list|,
name|out
argument_list|,
name|bwcOrderFlag
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
name|in
operator|.
name|setVersion
argument_list|(
name|bwcVersion
argument_list|)
expr_stmt|;
name|BucketOrder
name|actual
init|=
name|InternalOrder
operator|.
name|Streams
operator|.
name|readHistogramOrder
argument_list|(
name|in
argument_list|,
name|bwcOrderFlag
argument_list|)
decl_stmt|;
name|BucketOrder
name|expected
init|=
name|order
decl_stmt|;
if|if
condition|(
name|order
operator|instanceof
name|CompoundOrder
condition|)
block|{
name|expected
operator|=
operator|(
operator|(
name|CompoundOrder
operator|)
name|order
operator|)
operator|.
name|orderElements
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testAggregationOrderEqualsAndHashCode
specifier|public
name|void
name|testAggregationOrderEqualsAndHashCode
parameter_list|()
block|{
name|String
name|path
init|=
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|boolean
name|asc
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|BucketOrder
name|o1
init|=
name|BucketOrder
operator|.
name|aggregation
argument_list|(
name|path
argument_list|,
name|asc
argument_list|)
decl_stmt|;
name|BucketOrder
name|o2
init|=
name|BucketOrder
operator|.
name|aggregation
argument_list|(
name|path
operator|+
literal|"test"
argument_list|,
name|asc
argument_list|)
decl_stmt|;
name|BucketOrder
name|o3
init|=
name|BucketOrder
operator|.
name|aggregation
argument_list|(
name|path
argument_list|,
operator|!
name|asc
argument_list|)
decl_stmt|;
name|BucketOrder
name|o4
init|=
name|BucketOrder
operator|.
name|aggregation
argument_list|(
name|path
argument_list|,
name|asc
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|o2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
argument_list|,
name|o3
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|o3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|o1
argument_list|,
name|o4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|o1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|o4
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|o1
operator|=
name|InternalOrder
operator|.
name|compound
argument_list|(
name|o1
argument_list|)
expr_stmt|;
name|o2
operator|=
name|InternalOrder
operator|.
name|compound
argument_list|(
name|o2
argument_list|)
expr_stmt|;
name|o3
operator|=
name|InternalOrder
operator|.
name|compound
argument_list|(
name|o3
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|o2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|o2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
argument_list|,
name|o3
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|o3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
argument_list|,
name|o4
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|o1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|o4
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

