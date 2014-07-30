begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|SortedDocValues
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
name|ArrayUtil
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
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|AtomicParentChildFieldData
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
name|ScriptDocValues
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|AbstractAtomicParentChildFieldData
specifier|abstract
class|class
name|AbstractAtomicParentChildFieldData
implements|implements
name|AtomicParentChildFieldData
block|{
annotation|@
name|Override
DECL|method|getScriptValues
specifier|public
specifier|final
name|ScriptDocValues
name|getScriptValues
parameter_list|()
block|{
return|return
operator|new
name|ScriptDocValues
operator|.
name|Strings
argument_list|(
name|getBytesValues
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
specifier|final
name|SortedBinaryDocValues
name|getBytesValues
parameter_list|()
block|{
return|return
operator|new
name|SortedBinaryDocValues
argument_list|()
block|{
specifier|private
specifier|final
name|BytesRef
index|[]
name|terms
init|=
operator|new
name|BytesRef
index|[
literal|2
index|]
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
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
name|count
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|types
argument_list|()
control|)
block|{
specifier|final
name|SortedDocValues
name|values
init|=
name|getOrdinalsValues
argument_list|(
name|type
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ord
init|=
name|values
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|terms
index|[
name|count
operator|++
index|]
operator|=
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|count
operator|<=
literal|2
operator|:
literal|"A single doc can potentially be both parent and child, so the maximum allowed values is 2"
assert|;
if|if
condition|(
name|count
operator|>
literal|1
condition|)
block|{
name|int
name|cmp
init|=
name|terms
index|[
literal|0
index|]
operator|.
name|compareTo
argument_list|(
name|terms
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|ArrayUtil
operator|.
name|swap
argument_list|(
name|terms
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
comment|// If the id is the same between types the only omit one. For example: a doc has parent#1 in _uid field and has grand_parent#1 in _parent field.
name|count
operator|=
literal|1
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
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
name|terms
index|[
name|index
index|]
return|;
block|}
block|}
return|;
block|}
DECL|method|empty
specifier|public
specifier|static
name|AtomicParentChildFieldData
name|empty
parameter_list|()
block|{
return|return
operator|new
name|AbstractAtomicParentChildFieldData
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{             }
annotation|@
name|Override
specifier|public
name|SortedDocValues
name|getOrdinalsValues
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|DocValues
operator|.
name|emptySorted
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|types
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

