begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.bytes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|BytesReferenceTests
specifier|public
class|class
name|BytesReferenceTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
specifier|final
name|int
name|len
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|10
else|:
literal|100000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset1
init|=
name|randomInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|array1
init|=
operator|new
name|byte
index|[
name|offset1
operator|+
name|len
operator|+
name|randomInt
argument_list|(
literal|5
argument_list|)
index|]
decl_stmt|;
name|getRandom
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|array1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|offset2
init|=
name|randomInt
argument_list|(
name|offset1
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|array2
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|array1
argument_list|,
name|offset1
operator|-
name|offset2
argument_list|,
name|array1
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|BytesArray
name|b1
init|=
operator|new
name|BytesArray
argument_list|(
name|array1
argument_list|,
name|offset1
argument_list|,
name|len
argument_list|)
decl_stmt|;
specifier|final
name|BytesArray
name|b2
init|=
operator|new
name|BytesArray
argument_list|(
name|array2
argument_list|,
name|offset2
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesEqual
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesEquals
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|hashCode
argument_list|(
name|b1
operator|.
name|toBytes
argument_list|()
argument_list|)
argument_list|,
name|b1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesHashCode
argument_list|(
name|b1
argument_list|)
argument_list|,
name|BytesReference
operator|.
name|Helper
operator|.
name|slowHashCode
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
comment|// test same instance
name|assertTrue
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesEqual
argument_list|(
name|b1
argument_list|,
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesEquals
argument_list|(
name|b1
argument_list|,
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesHashCode
argument_list|(
name|b1
argument_list|)
argument_list|,
name|BytesReference
operator|.
name|Helper
operator|.
name|slowHashCode
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
comment|// test different length
name|BytesArray
name|differentLen
init|=
operator|new
name|BytesArray
argument_list|(
name|array1
argument_list|,
name|offset1
argument_list|,
name|randomInt
argument_list|(
name|len
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesEqual
argument_list|(
name|b1
argument_list|,
name|differentLen
argument_list|)
argument_list|)
expr_stmt|;
comment|// test changed bytes
name|array1
index|[
name|offset1
operator|+
name|randomInt
argument_list|(
name|len
operator|-
literal|1
argument_list|)
index|]
operator|+=
literal|13
expr_stmt|;
name|assertFalse
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesEqual
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BytesReference
operator|.
name|Helper
operator|.
name|bytesEquals
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

