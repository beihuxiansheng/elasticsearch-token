begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.controller
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|controller
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
name|search
operator|.
name|FieldComparator
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
name|FieldDoc
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
name|SortField
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
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
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

begin_comment
comment|/**  *  */
end_comment

begin_comment
comment|// LUCENE TRACK, Had to copy over in order ot improve same order tie break to take shards into account
end_comment

begin_class
DECL|class|ShardFieldDocSortedHitQueue
specifier|public
class|class
name|ShardFieldDocSortedHitQueue
extends|extends
name|PriorityQueue
argument_list|<
name|FieldDoc
argument_list|>
block|{
DECL|field|fields
specifier|volatile
name|SortField
index|[]
name|fields
init|=
literal|null
decl_stmt|;
comment|// used in the case where the fields are sorted by locale
comment|// based strings
comment|//volatile Collator[] collators = null;
DECL|field|comparators
name|FieldComparator
index|[]
name|comparators
init|=
literal|null
decl_stmt|;
comment|/**      * Creates a hit queue sorted by the given list of fields.      *      * @param fields Fieldable names, in priority order (highest priority first).      * @param size   The number of hits to retain.  Must be greater than zero.      */
DECL|method|ShardFieldDocSortedHitQueue
specifier|public
name|ShardFieldDocSortedHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|setFields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allows redefinition of sort fields if they are<code>null</code>.      * This is to handle the case using ParallelMultiSearcher where the      * original list contains AUTO and we don't know the actual sort      * type until the values come back.  The fields can only be set once.      * This method should be synchronized external like all other PQ methods.      *      * @param fields      */
DECL|method|setFields
specifier|public
name|void
name|setFields
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
comment|//this.collators = hasCollators(fields);
try|try
block|{
name|comparators
operator|=
operator|new
name|FieldComparator
index|[
name|fields
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|fieldIDX
init|=
literal|0
init|;
name|fieldIDX
operator|<
name|fields
operator|.
name|length
condition|;
name|fieldIDX
operator|++
control|)
block|{
name|comparators
index|[
name|fieldIDX
index|]
operator|=
name|fields
index|[
name|fieldIDX
index|]
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|fieldIDX
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"failed to get comparator"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the fields being used to sort.      */
DECL|method|getFields
name|SortField
index|[]
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/**      * Returns whether<code>a</code> is less relevant than<code>b</code>.      *      * @param docA ScoreDoc      * @param docB ScoreDoc      * @return<code>true</code> if document<code>a</code> should be sorted after document<code>b</code>.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|FieldDoc
name|docA
parameter_list|,
specifier|final
name|FieldDoc
name|docB
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|fields
operator|.
name|length
decl_stmt|;
name|int
name|c
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
name|n
operator|&&
name|c
operator|==
literal|0
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|SortField
operator|.
name|Type
name|type
init|=
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|Type
operator|.
name|STRING
condition|)
block|{
specifier|final
name|BytesRef
name|s1
init|=
operator|(
name|BytesRef
operator|)
name|docA
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|BytesRef
name|s2
init|=
operator|(
name|BytesRef
operator|)
name|docB
operator|.
name|fields
index|[
name|i
index|]
decl_stmt|;
comment|// null values need to be sorted first, because of how FieldCache.getStringIndex()
comment|// works - in that routine, any documents without a value in the given field are
comment|// put first.  If both are null, the next SortField is used
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
block|{
name|c
operator|=
operator|(
name|s2
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s2
operator|==
literal|null
condition|)
block|{
name|c
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|//if (fields[i].getLocale() == null) {
name|c
operator|=
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
expr_stmt|;
block|}
comment|//                } else {
comment|//                    c = collators[i].compare(s1, s2);
comment|//                }
block|}
else|else
block|{
name|c
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|compareValues
argument_list|(
name|docA
operator|.
name|fields
index|[
name|i
index|]
argument_list|,
name|docB
operator|.
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// reverse sort
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|)
block|{
name|c
operator|=
operator|-
name|c
expr_stmt|;
block|}
block|}
comment|// avoid random sort order that could lead to duplicates (bug #31241):
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
comment|// CHANGE: Add shard base tie breaking
name|c
operator|=
name|docA
operator|.
name|shardIndex
operator|-
name|docB
operator|.
name|shardIndex
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
return|return
name|docA
operator|.
name|doc
operator|>
name|docB
operator|.
name|doc
return|;
block|}
block|}
return|return
name|c
operator|>
literal|0
return|;
block|}
block|}
end_class

end_unit

