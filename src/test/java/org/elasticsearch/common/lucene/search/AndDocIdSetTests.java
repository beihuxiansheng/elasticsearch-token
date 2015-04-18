begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
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
name|search
operator|.
name|DocIdSetIterator
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
name|search
operator|.
name|DocValuesDocIdSet
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
name|BitDocIdSet
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
name|Bits
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
name|FixedBitSet
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
name|lucene
operator|.
name|docset
operator|.
name|AndDocIdSet
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

begin_class
DECL|class|AndDocIdSetTests
specifier|public
class|class
name|AndDocIdSetTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|method|randomBitSet
specifier|private
specifier|static
name|FixedBitSet
name|randomBitSet
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
name|FixedBitSet
name|b
init|=
operator|new
name|FixedBitSet
argument_list|(
name|numDocs
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|b
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|b
return|;
block|}
DECL|method|testDuel
specifier|public
name|void
name|testDuel
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|1000
condition|;
operator|++
name|iter
control|)
block|{
specifier|final
name|int
name|numSets
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|FixedBitSet
name|anded
init|=
operator|new
name|FixedBitSet
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|anded
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
specifier|final
name|DocIdSet
index|[]
name|sets
init|=
operator|new
name|DocIdSet
index|[
name|numSets
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
name|numSets
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|FixedBitSet
name|randomSet
init|=
name|randomBitSet
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
name|anded
operator|.
name|and
argument_list|(
name|randomSet
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// will be considered 'fast' by AndDocIdSet
name|sets
index|[
name|i
index|]
operator|=
operator|new
name|BitDocIdSet
argument_list|(
name|randomSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// will be considered 'slow' by AndDocIdSet
name|sets
index|[
name|i
index|]
operator|=
operator|new
name|DocValuesDocIdSet
argument_list|(
name|numDocs
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|randomSet
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
block|}
name|AndDocIdSet
name|andSet
init|=
operator|new
name|AndDocIdSet
argument_list|(
name|sets
argument_list|)
decl_stmt|;
name|Bits
name|andBits
init|=
name|andSet
operator|.
name|bits
argument_list|()
decl_stmt|;
if|if
condition|(
name|andBits
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|anded
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|andBits
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|DocIdSetIterator
name|andIt
init|=
name|andSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|andIt
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|anded
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|previous
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|andIt
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|andIt
operator|.
name|nextDoc
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|previous
operator|+
literal|1
init|;
name|j
operator|<
name|doc
condition|;
operator|++
name|j
control|)
block|{
name|assertFalse
argument_list|(
name|anded
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|anded
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|previous
operator|=
name|doc
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
name|previous
operator|+
literal|1
init|;
name|j
operator|<
name|numDocs
condition|;
operator|++
name|j
control|)
block|{
name|assertFalse
argument_list|(
name|anded
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

