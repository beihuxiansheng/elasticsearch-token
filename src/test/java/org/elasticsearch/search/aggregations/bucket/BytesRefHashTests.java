begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongOpenHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectLongCursor
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
name|BytesRef
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
name|_TestUtil
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
name|util
operator|.
name|BigArraysTests
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_class
DECL|class|BytesRefHashTests
specifier|public
class|class
name|BytesRefHashTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|hash
name|BytesRefHash
name|hash
decl_stmt|;
DECL|method|newHash
specifier|private
name|void
name|newHash
parameter_list|()
block|{
if|if
condition|(
name|hash
operator|!=
literal|null
condition|)
block|{
name|hash
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
comment|// Test high load factors to make sure that collision resolution works fine
specifier|final
name|float
name|maxLoadFactor
init|=
literal|0.6f
operator|+
name|randomFloat
argument_list|()
operator|*
literal|0.39f
decl_stmt|;
name|hash
operator|=
operator|new
name|BytesRefHash
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|,
name|maxLoadFactor
argument_list|,
name|BigArraysTests
operator|.
name|randomCacheRecycler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|newHash
argument_list|()
expr_stmt|;
block|}
DECL|method|testDuell
specifier|public
name|void
name|testDuell
parameter_list|()
block|{
specifier|final
name|int
name|len
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
index|[]
name|values
init|=
operator|new
name|BytesRef
index|[
name|len
index|]
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
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ObjectLongMap
argument_list|<
name|BytesRef
argument_list|>
name|valueToId
init|=
operator|new
name|ObjectLongOpenHashMap
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
index|[]
name|idToValue
init|=
operator|new
name|BytesRef
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|randomInt
argument_list|(
literal|1000000
argument_list|)
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|BytesRef
name|value
init|=
name|randomFrom
argument_list|(
name|values
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueToId
operator|.
name|containsKey
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
operator|-
name|valueToId
operator|.
name|get
argument_list|(
name|value
argument_list|)
argument_list|,
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|,
name|value
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|valueToId
operator|.
name|size
argument_list|()
argument_list|,
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|,
name|value
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|idToValue
index|[
name|valueToId
operator|.
name|size
argument_list|()
index|]
operator|=
name|value
expr_stmt|;
name|valueToId
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|valueToId
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|valueToId
operator|.
name|size
argument_list|()
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ObjectLongCursor
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|iterator
init|=
name|valueToId
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|ObjectLongCursor
argument_list|<
name|BytesRef
argument_list|>
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|next
operator|.
name|value
argument_list|,
name|hash
operator|.
name|find
argument_list|(
name|next
operator|.
name|key
argument_list|,
name|next
operator|.
name|key
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hash
operator|.
name|capacity
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|id
init|=
name|hash
operator|.
name|id
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|>=
literal|0
condition|)
block|{
name|hash
operator|.
name|get
argument_list|(
name|id
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|idToValue
index|[
operator|(
name|int
operator|)
name|id
index|]
argument_list|,
name|spare
argument_list|)
expr_stmt|;
block|}
block|}
name|hash
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
comment|// START - tests borrowed from LUCENE
comment|/**      * Test method for {@link org.apache.lucene.util.BytesRefHash#size()}.      */
annotation|@
name|Test
DECL|method|testSize
specifier|public
name|void
name|testSize
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|num
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|mod
init|=
literal|1
operator|+
name|randomInt
argument_list|(
literal|40
argument_list|)
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|<
literal|0
condition|)
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
name|mod
operator|==
literal|0
condition|)
block|{
name|newHash
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|hash
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test method for      * {@link org.apache.lucene.util.BytesRefHash#get(int, BytesRef)}      * .      */
annotation|@
name|Test
DECL|method|testGet
specifier|public
name|void
name|testGet
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|num
condition|;
name|j
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|strings
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|uniqueCount
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|>=
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|strings
operator|.
name|put
argument_list|(
name|str
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueCount
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|uniqueCount
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
operator|<
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|strings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ref
argument_list|,
name|hash
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|scratch
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newHash
argument_list|()
expr_stmt|;
block|}
name|hash
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test method for      * {@link org.apache.lucene.util.BytesRefHash#add(org.apache.lucene.util.BytesRef)}      * .      */
annotation|@
name|Test
DECL|method|testAdd
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|num
condition|;
name|j
operator|++
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|uniqueCount
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|>=
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueCount
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|uniqueCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
operator|<
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str
argument_list|,
name|hash
operator|.
name|get
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertAllIn
argument_list|(
name|strings
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|newHash
argument_list|()
expr_stmt|;
block|}
name|hash
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFind
specifier|public
name|void
name|testFind
parameter_list|()
throws|throws
name|Exception
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|num
condition|;
name|j
operator|++
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|uniqueCount
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
literal|797
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
decl_stmt|;
do|do
block|{
name|str
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
do|;
name|ref
operator|.
name|copyChars
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|key
init|=
name|hash
operator|.
name|find
argument_list|(
name|ref
argument_list|)
decl_stmt|;
comment|//hash.add(ref);
if|if
condition|(
name|key
operator|>=
literal|0
condition|)
block|{
comment|// string found in hash
name|assertFalse
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|key
operator|<
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str
argument_list|,
name|hash
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|key
operator|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|strings
operator|.
name|add
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueCount
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hash
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|uniqueCount
operator|++
expr_stmt|;
block|}
block|}
name|assertAllIn
argument_list|(
name|strings
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|newHash
argument_list|()
expr_stmt|;
block|}
name|hash
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
DECL|method|assertAllIn
specifier|private
name|void
name|assertAllIn
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|,
name|BytesRefHash
name|hash
parameter_list|)
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|long
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|long
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|ref
argument_list|)
decl_stmt|;
comment|// add again to check duplicates
name|assertEquals
argument_list|(
name|string
argument_list|,
name|hash
operator|.
name|get
argument_list|(
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|hash
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"key: "
operator|+
name|key
operator|+
literal|" count: "
operator|+
name|count
operator|+
literal|" string: "
operator|+
name|string
argument_list|,
name|key
operator|<
name|count
argument_list|)
expr_stmt|;
block|}
block|}
comment|// END - tests borrowed from LUCENE
block|}
end_class

end_unit

