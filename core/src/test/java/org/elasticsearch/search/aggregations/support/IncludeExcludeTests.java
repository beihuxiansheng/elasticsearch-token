begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocValues
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
name|index
operator|.
name|RandomAccessOrds
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
name|index
operator|.
name|SortedSetDocValues
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
name|LongBitSet
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
name|ParseField
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
name|ParseFieldMatcher
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
name|ToXContent
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
name|XContentBuilder
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
name|query
operator|.
name|QueryParseContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|query
operator|.
name|IndicesQueriesRegistry
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
name|DocValueFormat
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
name|support
operator|.
name|IncludeExclude
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
name|support
operator|.
name|IncludeExclude
operator|.
name|OrdinalsFilter
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_class
DECL|class|IncludeExcludeTests
specifier|public
class|class
name|IncludeExcludeTests
extends|extends
name|ESTestCase
block|{
DECL|field|parseFieldMatcher
specifier|private
specifier|final
name|ParseFieldMatcher
name|parseFieldMatcher
init|=
name|ParseFieldMatcher
operator|.
name|STRICT
decl_stmt|;
DECL|field|queriesRegistry
specifier|private
specifier|final
name|IndicesQueriesRegistry
name|queriesRegistry
init|=
operator|new
name|IndicesQueriesRegistry
argument_list|()
decl_stmt|;
DECL|method|testEmptyTermsWithOrds
specifier|public
name|void
name|testEmptyTermsWithOrds
parameter_list|()
throws|throws
name|IOException
block|{
name|IncludeExclude
name|inexcl
init|=
operator|new
name|IncludeExclude
argument_list|(
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|OrdinalsFilter
name|filter
init|=
name|inexcl
operator|.
name|convertToOrdinalsFilter
argument_list|(
name|DocValueFormat
operator|.
name|RAW
argument_list|)
decl_stmt|;
name|LongBitSet
name|acceptedOrds
init|=
name|filter
operator|.
name|acceptedGlobalOrdinals
argument_list|(
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acceptedOrds
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|inexcl
operator|=
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|inexcl
operator|.
name|convertToOrdinalsFilter
argument_list|(
name|DocValueFormat
operator|.
name|RAW
argument_list|)
expr_stmt|;
name|acceptedOrds
operator|=
name|filter
operator|.
name|acceptedGlobalOrdinals
argument_list|(
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acceptedOrds
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleTermWithOrds
specifier|public
name|void
name|testSingleTermWithOrds
parameter_list|()
throws|throws
name|IOException
block|{
name|RandomAccessOrds
name|ords
init|=
operator|new
name|RandomAccessOrds
argument_list|()
block|{
name|boolean
name|consumed
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|consumed
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
if|if
condition|(
name|consumed
condition|)
block|{
return|return
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
return|;
block|}
else|else
block|{
name|consumed
operator|=
literal|true
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ord
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ordAt
parameter_list|(
name|int
name|index
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
name|cardinality
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
decl_stmt|;
name|IncludeExclude
name|inexcl
init|=
operator|new
name|IncludeExclude
argument_list|(
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|OrdinalsFilter
name|filter
init|=
name|inexcl
operator|.
name|convertToOrdinalsFilter
argument_list|(
name|DocValueFormat
operator|.
name|RAW
argument_list|)
decl_stmt|;
name|LongBitSet
name|acceptedOrds
init|=
name|filter
operator|.
name|acceptedGlobalOrdinals
argument_list|(
name|ords
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|acceptedOrds
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acceptedOrds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|inexcl
operator|=
operator|new
name|IncludeExclude
argument_list|(
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|=
name|inexcl
operator|.
name|convertToOrdinalsFilter
argument_list|(
name|DocValueFormat
operator|.
name|RAW
argument_list|)
expr_stmt|;
name|acceptedOrds
operator|=
name|filter
operator|.
name|acceptedGlobalOrdinals
argument_list|(
name|ords
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|acceptedOrds
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acceptedOrds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|inexcl
operator|=
operator|new
name|IncludeExclude
argument_list|(
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|inexcl
operator|.
name|convertToOrdinalsFilter
argument_list|(
name|DocValueFormat
operator|.
name|RAW
argument_list|)
expr_stmt|;
name|acceptedOrds
operator|=
name|filter
operator|.
name|acceptedGlobalOrdinals
argument_list|(
name|ords
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|acceptedOrds
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acceptedOrds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|inexcl
operator|=
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
comment|// means everything included
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|inexcl
operator|.
name|convertToOrdinalsFilter
argument_list|(
name|DocValueFormat
operator|.
name|RAW
argument_list|)
expr_stmt|;
name|acceptedOrds
operator|=
name|filter
operator|.
name|acceptedGlobalOrdinals
argument_list|(
name|ords
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|acceptedOrds
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acceptedOrds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPartitionedEquals
specifier|public
name|void
name|testPartitionedEquals
parameter_list|()
throws|throws
name|IOException
block|{
name|IncludeExclude
name|serialized
init|=
name|serialize
argument_list|(
operator|new
name|IncludeExclude
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
argument_list|,
name|IncludeExclude
operator|.
name|INCLUDE_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isRegexBased
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|isPartitionBased
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|same
init|=
operator|new
name|IncludeExclude
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|serialized
argument_list|,
name|same
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|same
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|differentParam1
init|=
operator|new
name|IncludeExclude
argument_list|(
literal|4
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|equals
argument_list|(
name|differentParam1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
operator|!=
name|differentParam1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|differentParam2
init|=
operator|new
name|IncludeExclude
argument_list|(
literal|3
argument_list|,
literal|21
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|equals
argument_list|(
name|differentParam2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
operator|!=
name|differentParam2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExactIncludeValuesEquals
specifier|public
name|void
name|testExactIncludeValuesEquals
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|incValues
init|=
block|{
literal|"a"
block|,
literal|"b"
block|}
decl_stmt|;
name|String
index|[]
name|differentIncValues
init|=
block|{
literal|"a"
block|,
literal|"c"
block|}
decl_stmt|;
name|IncludeExclude
name|serialized
init|=
name|serialize
argument_list|(
operator|new
name|IncludeExclude
argument_list|(
name|incValues
argument_list|,
literal|null
argument_list|)
argument_list|,
name|IncludeExclude
operator|.
name|INCLUDE_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isPartitionBased
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isRegexBased
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|same
init|=
operator|new
name|IncludeExclude
argument_list|(
name|incValues
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|serialized
argument_list|,
name|same
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|same
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|different
init|=
operator|new
name|IncludeExclude
argument_list|(
name|differentIncValues
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|equals
argument_list|(
name|different
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
operator|!=
name|different
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExactExcludeValuesEquals
specifier|public
name|void
name|testExactExcludeValuesEquals
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|excValues
init|=
block|{
literal|"a"
block|,
literal|"b"
block|}
decl_stmt|;
name|String
index|[]
name|differentExcValues
init|=
block|{
literal|"a"
block|,
literal|"c"
block|}
decl_stmt|;
name|IncludeExclude
name|serialized
init|=
name|serialize
argument_list|(
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
name|excValues
argument_list|)
argument_list|,
name|IncludeExclude
operator|.
name|EXCLUDE_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isPartitionBased
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isRegexBased
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|same
init|=
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
name|excValues
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|serialized
argument_list|,
name|same
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|same
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|different
init|=
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
name|differentExcValues
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|equals
argument_list|(
name|different
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
operator|!=
name|different
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegexInclude
specifier|public
name|void
name|testRegexInclude
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|incRegex
init|=
literal|"foo.*"
decl_stmt|;
name|String
name|differentRegex
init|=
literal|"bar.*"
decl_stmt|;
name|IncludeExclude
name|serialized
init|=
name|serialize
argument_list|(
operator|new
name|IncludeExclude
argument_list|(
name|incRegex
argument_list|,
literal|null
argument_list|)
argument_list|,
name|IncludeExclude
operator|.
name|INCLUDE_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isPartitionBased
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|isRegexBased
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|same
init|=
operator|new
name|IncludeExclude
argument_list|(
name|incRegex
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|serialized
argument_list|,
name|same
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|same
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|different
init|=
operator|new
name|IncludeExclude
argument_list|(
name|differentRegex
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|equals
argument_list|(
name|different
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
operator|!=
name|different
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegexExclude
specifier|public
name|void
name|testRegexExclude
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|excRegex
init|=
literal|"foo.*"
decl_stmt|;
name|String
name|differentRegex
init|=
literal|"bar.*"
decl_stmt|;
name|IncludeExclude
name|serialized
init|=
name|serialize
argument_list|(
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
name|excRegex
argument_list|)
argument_list|,
name|IncludeExclude
operator|.
name|EXCLUDE_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isPartitionBased
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|isRegexBased
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|same
init|=
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
name|excRegex
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|serialized
argument_list|,
name|same
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|same
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|different
init|=
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
name|differentRegex
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|equals
argument_list|(
name|different
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
operator|!=
name|different
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Serializes/deserializes an IncludeExclude statement with a single clause
DECL|method|serialize
specifier|private
name|IncludeExclude
name|serialize
parameter_list|(
name|IncludeExclude
name|incExc
parameter_list|,
name|ParseField
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|incExc
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|token
argument_list|,
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|token
argument_list|,
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|field
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|queriesRegistry
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"include"
argument_list|)
condition|)
block|{
return|return
name|IncludeExclude
operator|.
name|parseInclude
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"exclude"
argument_list|)
condition|)
block|{
return|return
name|IncludeExclude
operator|.
name|parseExclude
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected field name serialized in test: "
operator|+
name|field
operator|.
name|getPreferredName
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|testRegexIncludeAndExclude
specifier|public
name|void
name|testRegexIncludeAndExclude
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|incRegex
init|=
literal|"foo.*"
decl_stmt|;
name|String
name|excRegex
init|=
literal|"football"
decl_stmt|;
name|String
name|differentExcRegex
init|=
literal|"foosball"
decl_stmt|;
name|IncludeExclude
name|serialized
init|=
name|serializeMixedRegex
argument_list|(
operator|new
name|IncludeExclude
argument_list|(
name|incRegex
argument_list|,
name|excRegex
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|isPartitionBased
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|isRegexBased
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|same
init|=
operator|new
name|IncludeExclude
argument_list|(
name|incRegex
argument_list|,
name|excRegex
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|serialized
argument_list|,
name|same
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
argument_list|,
name|same
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|IncludeExclude
name|different
init|=
operator|new
name|IncludeExclude
argument_list|(
name|incRegex
argument_list|,
name|differentExcRegex
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|serialized
operator|.
name|equals
argument_list|(
name|different
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|serialized
operator|.
name|hashCode
argument_list|()
operator|!=
name|different
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Serializes/deserializes the IncludeExclude statement with include AND
comment|// exclude clauses
DECL|method|serializeMixedRegex
specifier|private
name|IncludeExclude
name|serializeMixedRegex
parameter_list|(
name|IncludeExclude
name|incExc
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|incExc
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|queriesRegistry
argument_list|,
name|parser
argument_list|,
name|parseFieldMatcher
argument_list|)
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|token
argument_list|,
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
expr_stmt|;
name|IncludeExclude
name|inc
init|=
literal|null
decl_stmt|;
name|IncludeExclude
name|exc
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|,
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|IncludeExclude
operator|.
name|INCLUDE_FIELD
argument_list|)
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|inc
operator|=
name|IncludeExclude
operator|.
name|parseInclude
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|IncludeExclude
operator|.
name|EXCLUDE_FIELD
argument_list|)
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|exc
operator|=
name|IncludeExclude
operator|.
name|parseExclude
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected field name serialized in test: "
operator|+
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|assertNotNull
argument_list|(
name|inc
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|exc
argument_list|)
expr_stmt|;
comment|// Include and Exclude clauses are parsed independently and then merged
return|return
name|IncludeExclude
operator|.
name|merge
argument_list|(
name|inc
argument_list|,
name|exc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

