begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.fieldcomparator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
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
name|AtomicReaderContext
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
name|BytesValues
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
name|IndexFieldData
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
comment|/**  * Sorts by field's natural Term sort order.  All  * comparisons are done using BytesRef.compareTo, which is  * slow for medium to large result sets but possibly  * very fast for very small results sets.  */
end_comment

begin_class
DECL|class|BytesRefValComparator
specifier|public
specifier|final
class|class
name|BytesRefValComparator
extends|extends
name|NestedWrappableComparator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
decl_stmt|;
DECL|field|sortMode
specifier|private
specifier|final
name|SortMode
name|sortMode
decl_stmt|;
DECL|field|missingValue
specifier|private
specifier|final
name|BytesRef
name|missingValue
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|BytesRef
index|[]
name|values
decl_stmt|;
DECL|field|bottom
specifier|private
name|BytesRef
name|bottom
decl_stmt|;
DECL|field|top
specifier|private
name|BytesRef
name|top
decl_stmt|;
DECL|field|docTerms
specifier|private
name|BytesValues
name|docTerms
decl_stmt|;
DECL|method|BytesRefValComparator
name|BytesRefValComparator
parameter_list|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
parameter_list|,
name|int
name|numHits
parameter_list|,
name|SortMode
name|sortMode
parameter_list|,
name|BytesRef
name|missingValue
parameter_list|)
block|{
name|this
operator|.
name|sortMode
operator|=
name|sortMode
expr_stmt|;
name|values
operator|=
operator|new
name|BytesRef
index|[
name|numHits
index|]
expr_stmt|;
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
specifier|final
name|BytesRef
name|val1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|BytesRef
name|val2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
return|return
name|compareValues
argument_list|(
name|val1
argument_list|,
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|val2
init|=
name|sortMode
operator|.
name|getRelevantValue
argument_list|(
name|docTerms
argument_list|,
name|doc
argument_list|,
name|missingValue
argument_list|)
decl_stmt|;
return|return
name|compareValues
argument_list|(
name|bottom
argument_list|,
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTop
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|top
operator|.
name|compareTo
argument_list|(
name|sortMode
operator|.
name|getRelevantValue
argument_list|(
name|docTerms
argument_list|,
name|doc
argument_list|,
name|missingValue
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|relevantValue
init|=
name|sortMode
operator|.
name|getRelevantValue
argument_list|(
name|docTerms
argument_list|,
name|doc
argument_list|,
name|missingValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|relevantValue
operator|==
name|missingValue
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
name|missingValue
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
operator|||
name|values
index|[
name|slot
index|]
operator|==
name|missingValue
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|slot
index|]
operator|.
name|copyBytes
argument_list|(
name|relevantValue
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docTerms
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getBytesValues
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|bottom
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTopValue
specifier|public
name|void
name|setTopValue
parameter_list|(
name|BytesRef
name|top
parameter_list|)
block|{
name|this
operator|.
name|top
operator|=
name|top
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|BytesRef
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
index|[
name|slot
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|compareValues
specifier|public
name|int
name|compareValues
parameter_list|(
name|BytesRef
name|val1
parameter_list|,
name|BytesRef
name|val2
parameter_list|)
block|{
if|if
condition|(
name|val1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|val1
operator|.
name|compareTo
argument_list|(
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|missing
specifier|public
name|void
name|missing
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|=
name|missingValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottomMissing
specifier|public
name|int
name|compareBottomMissing
parameter_list|()
block|{
return|return
name|compareValues
argument_list|(
name|bottom
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTopMissing
specifier|public
name|int
name|compareTopMissing
parameter_list|()
block|{
return|return
name|compareValues
argument_list|(
name|top
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

