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
name|LeafReaderContext
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
name|NumericDocValues
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
name|BitSet
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
name|Nullable
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|MultiValueMode
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
comment|/**  * Comparator source for long values.  */
end_comment

begin_class
DECL|class|LongValuesComparatorSource
specifier|public
class|class
name|LongValuesComparatorSource
extends|extends
name|IndexFieldData
operator|.
name|XFieldComparatorSource
block|{
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexNumericFieldData
name|indexFieldData
decl_stmt|;
DECL|field|missingValue
specifier|private
specifier|final
name|Object
name|missingValue
decl_stmt|;
DECL|field|sortMode
specifier|private
specifier|final
name|MultiValueMode
name|sortMode
decl_stmt|;
DECL|field|nested
specifier|private
specifier|final
name|Nested
name|nested
decl_stmt|;
DECL|method|LongValuesComparatorSource
specifier|public
name|LongValuesComparatorSource
parameter_list|(
name|IndexNumericFieldData
name|indexFieldData
parameter_list|,
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|,
name|Nested
name|nested
parameter_list|)
block|{
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
name|this
operator|.
name|sortMode
operator|=
name|sortMode
expr_stmt|;
name|this
operator|.
name|nested
operator|=
name|nested
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reducedType
specifier|public
name|SortField
operator|.
name|Type
name|reducedType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|Type
operator|.
name|LONG
return|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|indexFieldData
operator|==
literal|null
operator|||
name|fieldname
operator|.
name|equals
argument_list|(
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
assert|;
specifier|final
name|Long
name|dMissingValue
init|=
operator|(
name|Long
operator|)
name|missingObject
argument_list|(
name|missingValue
argument_list|,
name|reversed
argument_list|)
decl_stmt|;
comment|// NOTE: it's important to pass null as a missing value in the constructor so that
comment|// the comparator doesn't check docsWithField since we replace missing values in select()
return|return
operator|new
name|FieldComparator
operator|.
name|LongComparator
argument_list|(
name|numHits
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getLongValues
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValues
name|selectedValues
decl_stmt|;
if|if
condition|(
name|nested
operator|==
literal|null
condition|)
block|{
name|selectedValues
operator|=
name|sortMode
operator|.
name|select
argument_list|(
name|values
argument_list|,
name|dMissingValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|BitSet
name|rootDocs
init|=
name|nested
operator|.
name|rootDocs
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSet
name|innerDocs
init|=
name|nested
operator|.
name|innerDocs
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|selectedValues
operator|=
name|sortMode
operator|.
name|select
argument_list|(
name|values
argument_list|,
name|dMissingValue
argument_list|,
name|rootDocs
argument_list|,
name|innerDocs
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|selectedValues
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

