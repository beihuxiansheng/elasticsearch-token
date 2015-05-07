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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
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
name|SortedNumericDocValues
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
name|TestUtil
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
name|geo
operator|.
name|GeoPoint
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
name|fielddata
operator|.
name|AbstractRandomAccessOrds
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
name|fielddata
operator|.
name|MultiGeoPointValues
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
name|fielddata
operator|.
name|SortedBinaryDocValues
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
name|fielddata
operator|.
name|SortedNumericDoubleValues
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|MissingValuesTests
specifier|public
class|class
name|MissingValuesTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|method|testMissingBytes
specifier|public
name|void
name|testMissingBytes
parameter_list|()
block|{
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
index|[]
index|[]
name|values
init|=
operator|new
name|BytesRef
index|[
name|numDocs
index|]
index|[]
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
name|values
index|[
name|i
index|]
operator|=
operator|new
name|BytesRef
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|values
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|SortedBinaryDocValues
name|asBinaryValues
init|=
operator|new
name|SortedBinaryDocValues
argument_list|()
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|i
index|]
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|i
operator|=
name|docId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
index|[
name|i
index|]
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|BytesRef
name|missing
init|=
operator|new
name|BytesRef
argument_list|(
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|SortedBinaryDocValues
name|withMissingReplaced
init|=
name|MissingValues
operator|.
name|replaceMissing
argument_list|(
name|asBinaryValues
argument_list|,
name|missing
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
name|withMissingReplaced
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missing
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMissingOrds
specifier|public
name|void
name|testMissingOrds
parameter_list|()
block|{
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numOrds
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|valueSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|valueSet
operator|.
name|size
argument_list|()
operator|<
name|numOrds
condition|)
block|{
name|valueSet
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BytesRef
index|[]
name|values
init|=
name|valueSet
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
name|numOrds
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
index|[]
name|ords
init|=
operator|new
name|int
index|[
name|numDocs
index|]
index|[]
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
name|ords
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numOrds
argument_list|)
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ords
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|ords
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|j
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
name|ords
index|[
name|i
index|]
operator|.
name|length
operator|-
literal|1
init|;
name|j
operator|>=
literal|0
condition|;
operator|--
name|j
control|)
block|{
specifier|final
name|int
name|maxOrd
init|=
name|j
operator|==
name|ords
index|[
name|i
index|]
operator|.
name|length
operator|-
literal|1
condition|?
name|numOrds
else|:
name|ords
index|[
name|i
index|]
index|[
name|j
operator|+
literal|1
index|]
decl_stmt|;
name|ords
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|ords
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|maxOrd
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|RandomAccessOrds
name|asRandomAccessOrds
init|=
operator|new
name|AbstractRandomAccessOrds
argument_list|()
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|doSetDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|i
operator|=
name|docID
expr_stmt|;
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
return|return
name|values
index|[
operator|(
name|int
operator|)
name|ord
index|]
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
name|values
operator|.
name|length
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
name|ords
index|[
name|i
index|]
index|[
name|index
index|]
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
name|ords
index|[
name|i
index|]
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|BytesRef
name|existingMissing
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|values
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|missingMissing
init|=
operator|new
name|BytesRef
argument_list|(
name|RandomStrings
operator|.
name|randomAsciiOfLength
argument_list|(
name|random
argument_list|()
argument_list|,
literal|5
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|BytesRef
name|missing
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|existingMissing
argument_list|,
name|missingMissing
argument_list|)
control|)
block|{
name|RandomAccessOrds
name|withMissingReplaced
init|=
name|MissingValues
operator|.
name|replaceMissing
argument_list|(
name|asRandomAccessOrds
argument_list|,
name|missing
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueSet
operator|.
name|contains
argument_list|(
name|missing
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|values
operator|.
name|length
argument_list|,
name|withMissingReplaced
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|values
operator|.
name|length
operator|+
literal|1
argument_list|,
name|withMissingReplaced
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|withMissingReplaced
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|ords
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|ords
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|withMissingReplaced
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ords
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|ords
index|[
name|i
index|]
index|[
name|j
index|]
index|]
argument_list|,
name|withMissingReplaced
operator|.
name|lookupOrd
argument_list|(
name|withMissingReplaced
operator|.
name|ordAt
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|withMissingReplaced
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missing
argument_list|,
name|withMissingReplaced
operator|.
name|lookupOrd
argument_list|(
name|withMissingReplaced
operator|.
name|ordAt
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testMissingLongs
specifier|public
name|void
name|testMissingLongs
parameter_list|()
block|{
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
index|[]
name|values
init|=
operator|new
name|int
index|[
name|numDocs
index|]
index|[]
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
name|values
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|values
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|randomInt
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|SortedNumericDocValues
name|asNumericValues
init|=
operator|new
name|SortedNumericDocValues
argument_list|()
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|i
index|]
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|i
operator|=
name|docId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
index|[
name|i
index|]
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|long
name|missing
init|=
name|randomInt
argument_list|()
decl_stmt|;
name|SortedNumericDocValues
name|withMissingReplaced
init|=
name|MissingValues
operator|.
name|replaceMissing
argument_list|(
name|asNumericValues
argument_list|,
name|missing
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
name|withMissingReplaced
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missing
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMissingDoubles
specifier|public
name|void
name|testMissingDoubles
parameter_list|()
block|{
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|double
index|[]
index|[]
name|values
init|=
operator|new
name|double
index|[
name|numDocs
index|]
index|[]
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
name|values
index|[
name|i
index|]
operator|=
operator|new
name|double
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|values
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|randomDouble
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|SortedNumericDoubleValues
name|asNumericValues
init|=
operator|new
name|SortedNumericDoubleValues
argument_list|()
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|double
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|i
index|]
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|i
operator|=
name|docId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
index|[
name|i
index|]
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|long
name|missing
init|=
name|randomInt
argument_list|()
decl_stmt|;
name|SortedNumericDoubleValues
name|withMissingReplaced
init|=
name|MissingValues
operator|.
name|replaceMissing
argument_list|(
name|asNumericValues
argument_list|,
name|missing
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
name|withMissingReplaced
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missing
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMissingGeoPoints
specifier|public
name|void
name|testMissingGeoPoints
parameter_list|()
block|{
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
index|[]
name|values
init|=
operator|new
name|GeoPoint
index|[
name|numDocs
index|]
index|[]
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
name|values
index|[
name|i
index|]
operator|=
operator|new
name|GeoPoint
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|values
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|GeoPoint
argument_list|(
name|randomDouble
argument_list|()
operator|*
literal|90
argument_list|,
name|randomDouble
argument_list|()
operator|*
literal|180
argument_list|)
expr_stmt|;
block|}
block|}
name|MultiGeoPointValues
name|asGeoValues
init|=
operator|new
name|MultiGeoPointValues
argument_list|()
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|GeoPoint
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|i
index|]
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|i
operator|=
name|docId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
index|[
name|i
index|]
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|GeoPoint
name|missing
init|=
operator|new
name|GeoPoint
argument_list|(
name|randomDouble
argument_list|()
operator|*
literal|90
argument_list|,
name|randomDouble
argument_list|()
operator|*
literal|180
argument_list|)
decl_stmt|;
name|MultiGeoPointValues
name|withMissingReplaced
init|=
name|MissingValues
operator|.
name|replaceMissing
argument_list|(
name|asGeoValues
argument_list|,
name|missing
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
name|withMissingReplaced
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|withMissingReplaced
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missing
argument_list|,
name|withMissingReplaced
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

