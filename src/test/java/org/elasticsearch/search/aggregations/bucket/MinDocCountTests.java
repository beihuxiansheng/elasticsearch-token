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
name|LongOpenHashSet
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
name|LongSet
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
name|generators
operator|.
name|RandomStrings
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
name|index
operator|.
name|IndexRequestBuilder
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
name|search
operator|.
name|SearchResponse
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
name|search
operator|.
name|SearchType
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
name|query
operator|.
name|QueryBuilder
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
name|query
operator|.
name|QueryBuilders
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
name|bucket
operator|.
name|histogram
operator|.
name|DateHistogram
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
name|bucket
operator|.
name|histogram
operator|.
name|Histogram
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
name|bucket
operator|.
name|terms
operator|.
name|Terms
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
name|bucket
operator|.
name|terms
operator|.
name|TermsBuilder
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
name|ElasticsearchIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|AggregationBuilders
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAllSuccessful
import|;
end_import

begin_class
DECL|class|MinDocCountTests
specifier|public
class|class
name|MinDocCountTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|QUERY
specifier|private
specifier|static
specifier|final
name|QueryBuilder
name|QUERY
init|=
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"match"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|cardinality
specifier|private
name|int
name|cardinality
decl_stmt|;
annotation|@
name|Before
DECL|method|indexData
specifier|public
name|void
name|indexData
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"idx"
argument_list|)
expr_stmt|;
name|cardinality
operator|=
name|randomIntBetween
argument_list|(
literal|8
argument_list|,
literal|30
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|indexRequests
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexRequestBuilder
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|stringTerms
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|LongSet
name|longTerms
init|=
operator|new
name|LongOpenHashSet
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|dateTerms
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
name|cardinality
condition|;
operator|++
name|i
control|)
block|{
name|String
name|stringTerm
decl_stmt|;
do|do
block|{
name|stringTerm
operator|=
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|stringTerms
operator|.
name|add
argument_list|(
name|stringTerm
argument_list|)
condition|)
do|;
name|long
name|longTerm
decl_stmt|;
do|do
block|{
name|longTerm
operator|=
name|randomInt
argument_list|(
name|cardinality
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|longTerms
operator|.
name|add
argument_list|(
name|longTerm
argument_list|)
condition|)
do|;
name|double
name|doubleTerm
init|=
name|longTerm
operator|*
name|Math
operator|.
name|PI
decl_stmt|;
name|String
name|dateTerm
init|=
name|DateTimeFormat
operator|.
name|forPattern
argument_list|(
literal|"yyyy-MM-dd"
argument_list|)
operator|.
name|print
argument_list|(
operator|new
name|DateTime
argument_list|(
literal|2014
argument_list|,
literal|1
argument_list|,
operator|(
operator|(
name|int
operator|)
name|longTerm
operator|%
literal|20
operator|)
operator|+
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|frequency
init|=
name|randomBoolean
argument_list|()
condition|?
literal|1
else|:
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|20
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
name|frequency
condition|;
operator|++
name|j
control|)
block|{
name|indexRequests
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"s"
argument_list|,
name|stringTerm
argument_list|)
operator|.
name|field
argument_list|(
literal|"l"
argument_list|,
name|longTerm
argument_list|)
operator|.
name|field
argument_list|(
literal|"d"
argument_list|,
name|doubleTerm
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|,
name|dateTerm
argument_list|)
operator|.
name|field
argument_list|(
literal|"match"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|cardinality
operator|=
name|stringTerms
operator|.
name|size
argument_list|()
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|indexRequests
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
block|}
DECL|enum|Script
specifier|private
enum|enum
name|Script
block|{
DECL|enum constant|NO
name|NO
block|{
annotation|@
name|Override
name|TermsBuilder
name|apply
parameter_list|(
name|TermsBuilder
name|builder
parameter_list|,
name|String
name|field
parameter_list|)
block|{
return|return
name|builder
operator|.
name|field
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|YES
name|YES
block|{
annotation|@
name|Override
name|TermsBuilder
name|apply
parameter_list|(
name|TermsBuilder
name|builder
parameter_list|,
name|String
name|field
parameter_list|)
block|{
return|return
name|builder
operator|.
name|script
argument_list|(
literal|"doc['"
operator|+
name|field
operator|+
literal|"'].values"
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|apply
specifier|abstract
name|TermsBuilder
name|apply
parameter_list|(
name|TermsBuilder
name|builder
parameter_list|,
name|String
name|field
parameter_list|)
function_decl|;
block|}
comment|// check that terms2 is a subset of terms1
DECL|method|assertSubset
specifier|private
name|void
name|assertSubset
parameter_list|(
name|Terms
name|terms1
parameter_list|,
name|Terms
name|terms2
parameter_list|,
name|long
name|minDocCount
parameter_list|,
name|int
name|size
parameter_list|,
name|String
name|include
parameter_list|)
block|{
specifier|final
name|Matcher
name|matcher
init|=
name|include
operator|==
literal|null
condition|?
literal|null
else|:
name|Pattern
operator|.
name|compile
argument_list|(
name|include
argument_list|)
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
empty_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|it1
init|=
name|terms1
operator|.
name|getBuckets
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|it2
init|=
name|terms2
operator|.
name|getBuckets
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|size2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Terms
operator|.
name|Bucket
name|bucket1
init|=
name|it1
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|bucket1
operator|.
name|getDocCount
argument_list|()
operator|>=
name|minDocCount
operator|&&
operator|(
name|matcher
operator|==
literal|null
operator|||
name|matcher
operator|.
name|reset
argument_list|(
name|bucket1
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|size2
operator|++
operator|==
name|size
condition|)
block|{
break|break;
block|}
name|assertTrue
argument_list|(
name|it2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Terms
operator|.
name|Bucket
name|bucket2
init|=
name|it2
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|bucket1
operator|.
name|getKeyAsText
argument_list|()
argument_list|,
name|bucket2
operator|.
name|getKeyAsText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bucket1
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|bucket2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|it2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSubset
specifier|private
name|void
name|assertSubset
parameter_list|(
name|Histogram
name|histo1
parameter_list|,
name|Histogram
name|histo2
parameter_list|,
name|long
name|minDocCount
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|Histogram
operator|.
name|Bucket
argument_list|>
name|it2
init|=
name|histo2
operator|.
name|getBuckets
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Histogram
operator|.
name|Bucket
name|b1
range|:
name|histo1
operator|.
name|getBuckets
argument_list|()
control|)
block|{
if|if
condition|(
name|b1
operator|.
name|getDocCount
argument_list|()
operator|>=
name|minDocCount
condition|)
block|{
specifier|final
name|Histogram
operator|.
name|Bucket
name|b2
init|=
name|it2
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|b1
operator|.
name|getKeyAsNumber
argument_list|()
argument_list|,
name|b2
operator|.
name|getKeyAsNumber
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b1
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|b2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertSubset
specifier|private
name|void
name|assertSubset
parameter_list|(
name|DateHistogram
name|histo1
parameter_list|,
name|DateHistogram
name|histo2
parameter_list|,
name|long
name|minDocCount
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|DateHistogram
operator|.
name|Bucket
argument_list|>
name|it2
init|=
name|histo2
operator|.
name|getBuckets
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|DateHistogram
operator|.
name|Bucket
name|b1
range|:
name|histo1
operator|.
name|getBuckets
argument_list|()
control|)
block|{
if|if
condition|(
name|b1
operator|.
name|getDocCount
argument_list|()
operator|>=
name|minDocCount
condition|)
block|{
specifier|final
name|DateHistogram
operator|.
name|Bucket
name|b2
init|=
name|it2
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|b1
operator|.
name|getKeyAsNumber
argument_list|()
argument_list|,
name|b2
operator|.
name|getKeyAsNumber
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b1
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|b2
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testStringTermAsc
specifier|public
name|void
name|testStringTermAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringScriptTermAsc
specifier|public
name|void
name|testStringScriptTermAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringTermDesc
specifier|public
name|void
name|testStringTermDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringScriptTermDesc
specifier|public
name|void
name|testStringScriptTermDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringCountAsc
specifier|public
name|void
name|testStringCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringScriptCountAsc
specifier|public
name|void
name|testStringScriptCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringCountDesc
specifier|public
name|void
name|testStringCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringScriptCountDesc
specifier|public
name|void
name|testStringScriptCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringCountAscWithInclude
specifier|public
name|void
name|testStringCountAscWithInclude
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|".*a.*"
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringScriptCountAscWithInclude
specifier|public
name|void
name|testStringScriptCountAscWithInclude
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|".*a.*"
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringCountDescWithInclude
specifier|public
name|void
name|testStringCountDescWithInclude
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|".*a.*"
argument_list|)
expr_stmt|;
block|}
DECL|method|testStringScriptCountDescWithInclude
specifier|public
name|void
name|testStringScriptCountDescWithInclude
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"s"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|".*a.*"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongTermAsc
specifier|public
name|void
name|testLongTermAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongScriptTermAsc
specifier|public
name|void
name|testLongScriptTermAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongTermDesc
specifier|public
name|void
name|testLongTermDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongScriptTermDesc
specifier|public
name|void
name|testLongScriptTermDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongCountAsc
specifier|public
name|void
name|testLongCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongScriptCountAsc
specifier|public
name|void
name|testLongScriptCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongCountDesc
specifier|public
name|void
name|testLongCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongScriptCountDesc
specifier|public
name|void
name|testLongScriptCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"l"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleTermAsc
specifier|public
name|void
name|testDoubleTermAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleScriptTermAsc
specifier|public
name|void
name|testDoubleScriptTermAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleTermDesc
specifier|public
name|void
name|testDoubleTermDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleScriptTermDesc
specifier|public
name|void
name|testDoubleScriptTermDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleCountAsc
specifier|public
name|void
name|testDoubleCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleScriptCountAsc
specifier|public
name|void
name|testDoubleScriptCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleCountDesc
specifier|public
name|void
name|testDoubleCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|NO
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubleScriptCountDesc
specifier|public
name|void
name|testDoubleScriptCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
literal|"d"
argument_list|,
name|Script
operator|.
name|YES
argument_list|,
name|Terms
operator|.
name|Order
operator|.
name|count
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinDocCountOnTerms
specifier|private
name|void
name|testMinDocCountOnTerms
parameter_list|(
name|String
name|field
parameter_list|,
name|Script
name|script
parameter_list|,
name|Terms
operator|.
name|Order
name|order
parameter_list|)
throws|throws
name|Exception
block|{
name|testMinDocCountOnTerms
argument_list|(
name|field
argument_list|,
name|script
argument_list|,
name|order
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinDocCountOnTerms
specifier|private
name|void
name|testMinDocCountOnTerms
parameter_list|(
name|String
name|field
parameter_list|,
name|Script
name|script
parameter_list|,
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|String
name|include
parameter_list|)
throws|throws
name|Exception
block|{
comment|// all terms
specifier|final
name|SearchResponse
name|allTermsResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|COUNT
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QUERY
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|script
operator|.
name|apply
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
argument_list|,
name|field
argument_list|)
operator|.
name|executionHint
argument_list|(
name|StringTermsTests
operator|.
name|randomExecutionHint
argument_list|()
argument_list|)
operator|.
name|order
argument_list|(
name|order
argument_list|)
operator|.
name|size
argument_list|(
name|cardinality
operator|+
name|randomInt
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertAllSuccessful
argument_list|(
name|allTermsResponse
argument_list|)
expr_stmt|;
specifier|final
name|Terms
name|allTerms
init|=
name|allTermsResponse
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|cardinality
argument_list|,
name|allTerms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|minDocCount
init|=
literal|0
init|;
name|minDocCount
operator|<
literal|20
condition|;
operator|++
name|minDocCount
control|)
block|{
specifier|final
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|cardinality
operator|+
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|COUNT
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QUERY
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|script
operator|.
name|apply
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
argument_list|,
name|field
argument_list|)
operator|.
name|executionHint
argument_list|(
name|StringTermsTests
operator|.
name|randomExecutionHint
argument_list|()
argument_list|)
operator|.
name|order
argument_list|(
name|order
argument_list|)
operator|.
name|size
argument_list|(
name|size
argument_list|)
operator|.
name|include
argument_list|(
name|include
argument_list|)
operator|.
name|shardSize
argument_list|(
name|cardinality
operator|+
name|randomInt
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|minDocCount
argument_list|(
name|minDocCount
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertAllSuccessful
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|assertSubset
argument_list|(
name|allTerms
argument_list|,
operator|(
name|Terms
operator|)
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
argument_list|,
name|minDocCount
argument_list|,
name|size
argument_list|,
name|include
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testHistogramCountAsc
specifier|public
name|void
name|testHistogramCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|COUNT_ASC
argument_list|)
expr_stmt|;
block|}
DECL|method|testHistogramCountDesc
specifier|public
name|void
name|testHistogramCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|COUNT_DESC
argument_list|)
expr_stmt|;
block|}
DECL|method|testHistogramKeyAsc
specifier|public
name|void
name|testHistogramKeyAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|KEY_ASC
argument_list|)
expr_stmt|;
block|}
DECL|method|testHistogramKeyDesc
specifier|public
name|void
name|testHistogramKeyDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|KEY_DESC
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateHistogramCountAsc
specifier|public
name|void
name|testDateHistogramCountAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnDateHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|COUNT_ASC
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateHistogramCountDesc
specifier|public
name|void
name|testDateHistogramCountDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnDateHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|COUNT_DESC
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateHistogramKeyAsc
specifier|public
name|void
name|testDateHistogramKeyAsc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnDateHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|KEY_ASC
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateHistogramKeyDesc
specifier|public
name|void
name|testDateHistogramKeyDesc
parameter_list|()
throws|throws
name|Exception
block|{
name|testMinDocCountOnDateHistogram
argument_list|(
name|Histogram
operator|.
name|Order
operator|.
name|KEY_DESC
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinDocCountOnHistogram
specifier|private
name|void
name|testMinDocCountOnHistogram
parameter_list|(
name|Histogram
operator|.
name|Order
name|order
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|interval
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|SearchResponse
name|allResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|COUNT
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QUERY
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|histogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"d"
argument_list|)
operator|.
name|interval
argument_list|(
name|interval
argument_list|)
operator|.
name|order
argument_list|(
name|order
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
specifier|final
name|Histogram
name|allHisto
init|=
name|allResponse
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|minDocCount
init|=
literal|0
init|;
name|minDocCount
operator|<
literal|50
condition|;
operator|++
name|minDocCount
control|)
block|{
specifier|final
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|COUNT
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QUERY
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|histogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"d"
argument_list|)
operator|.
name|interval
argument_list|(
name|interval
argument_list|)
operator|.
name|order
argument_list|(
name|order
argument_list|)
operator|.
name|minDocCount
argument_list|(
name|minDocCount
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSubset
argument_list|(
name|allHisto
argument_list|,
operator|(
name|Histogram
operator|)
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
argument_list|,
name|minDocCount
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMinDocCountOnDateHistogram
specifier|private
name|void
name|testMinDocCountOnDateHistogram
parameter_list|(
name|Histogram
operator|.
name|Order
name|order
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|interval
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|SearchResponse
name|allResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|COUNT
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QUERY
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|interval
argument_list|(
name|DateHistogram
operator|.
name|Interval
operator|.
name|DAY
argument_list|)
operator|.
name|order
argument_list|(
name|order
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
specifier|final
name|DateHistogram
name|allHisto
init|=
name|allResponse
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|minDocCount
init|=
literal|0
init|;
name|minDocCount
operator|<
literal|50
condition|;
operator|++
name|minDocCount
control|)
block|{
specifier|final
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|COUNT
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QUERY
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|dateHistogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|)
operator|.
name|interval
argument_list|(
name|DateHistogram
operator|.
name|Interval
operator|.
name|DAY
argument_list|)
operator|.
name|order
argument_list|(
name|order
argument_list|)
operator|.
name|minDocCount
argument_list|(
name|minDocCount
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSubset
argument_list|(
name|allHisto
argument_list|,
operator|(
name|DateHistogram
operator|)
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
argument_list|,
name|minDocCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

