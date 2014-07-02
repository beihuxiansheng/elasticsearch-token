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
name|UnicodeUtil
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
name|IndexOrdinalsFieldData
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
comment|/**  */
end_comment

begin_class
DECL|class|BytesRefFieldComparatorSource
specifier|public
class|class
name|BytesRefFieldComparatorSource
extends|extends
name|IndexFieldData
operator|.
name|XFieldComparatorSource
block|{
comment|/** UTF-8 term containing a single code point: {@link Character#MAX_CODE_POINT} which will compare greater than all other index terms      *  since {@link Character#MAX_CODE_POINT} is a noncharacter and thus shouldn't appear in an index term. */
DECL|field|MAX_TERM
specifier|public
specifier|static
specifier|final
name|BytesRef
name|MAX_TERM
decl_stmt|;
static|static
block|{
name|MAX_TERM
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
specifier|final
name|char
index|[]
name|chars
init|=
name|Character
operator|.
name|toChars
argument_list|(
name|Character
operator|.
name|MAX_CODE_POINT
argument_list|)
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|,
name|MAX_TERM
argument_list|)
expr_stmt|;
block|}
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
name|MultiValueMode
name|sortMode
decl_stmt|;
DECL|field|missingValue
specifier|private
specifier|final
name|Object
name|missingValue
decl_stmt|;
DECL|method|BytesRefFieldComparatorSource
specifier|public
name|BytesRefFieldComparatorSource
parameter_list|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
parameter_list|,
name|Object
name|missingValue
parameter_list|,
name|MultiValueMode
name|sortMode
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
name|sortMode
operator|=
name|sortMode
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
name|STRING
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
name|BytesRef
name|missingBytes
init|=
operator|(
name|BytesRef
operator|)
name|missingObject
argument_list|(
name|missingValue
argument_list|,
name|reversed
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexFieldData
operator|instanceof
name|IndexOrdinalsFieldData
condition|)
block|{
return|return
operator|new
name|BytesRefOrdValComparator
argument_list|(
operator|(
name|IndexOrdinalsFieldData
operator|)
name|indexFieldData
argument_list|,
name|numHits
argument_list|,
name|sortMode
argument_list|,
name|missingBytes
argument_list|)
return|;
block|}
return|return
operator|new
name|BytesRefValComparator
argument_list|(
name|indexFieldData
argument_list|,
name|numHits
argument_list|,
name|sortMode
argument_list|,
name|missingBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

