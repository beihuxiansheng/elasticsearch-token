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
name|BytesRefIterator
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
name|CharsRef
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
name|ElasticsearchIllegalArgumentException
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
name|common
operator|.
name|settings
operator|.
name|Settings
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
name|Index
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
name|*
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
name|fieldcomparator
operator|.
name|SortMode
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
name|mapper
operator|.
name|FieldMapper
operator|.
name|Names
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

begin_class
DECL|class|AbstractGeoPointIndexFieldData
specifier|abstract
class|class
name|AbstractGeoPointIndexFieldData
extends|extends
name|AbstractIndexFieldData
argument_list|<
name|AtomicGeoPointFieldData
argument_list|<
name|ScriptDocValues
argument_list|>
argument_list|>
implements|implements
name|IndexGeoPointFieldData
argument_list|<
name|AtomicGeoPointFieldData
argument_list|<
name|ScriptDocValues
argument_list|>
argument_list|>
block|{
DECL|class|Empty
specifier|protected
specifier|static
class|class
name|Empty
extends|extends
name|AtomicGeoPointFieldData
argument_list|<
name|ScriptDocValues
argument_list|>
block|{
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|method|Empty
name|Empty
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isValuesOrdered
specifier|public
name|boolean
name|isValuesOrdered
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberUniqueValues
specifier|public
name|long
name|getNumberUniqueValues
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
name|BytesValues
name|getBytesValues
parameter_list|(
name|boolean
name|needsHashes
parameter_list|)
block|{
return|return
name|BytesValues
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Override
DECL|method|getGeoPointValues
specifier|public
name|GeoPointValues
name|getGeoPointValues
parameter_list|()
block|{
return|return
name|GeoPointValues
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Override
DECL|method|getScriptValues
specifier|public
name|ScriptDocValues
name|getScriptValues
parameter_list|()
block|{
return|return
name|ScriptDocValues
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// no-op
block|}
block|}
DECL|class|GeoPointEnum
specifier|protected
specifier|static
class|class
name|GeoPointEnum
block|{
DECL|field|termsEnum
specifier|private
specifier|final
name|BytesRefIterator
name|termsEnum
decl_stmt|;
DECL|field|next
specifier|private
specifier|final
name|GeoPoint
name|next
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|CharsRef
name|spare
decl_stmt|;
DECL|method|GeoPointEnum
specifier|protected
name|GeoPointEnum
parameter_list|(
name|BytesRefIterator
name|termsEnum
parameter_list|)
block|{
name|this
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
name|next
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
name|spare
operator|=
operator|new
name|CharsRef
argument_list|()
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|GeoPoint
name|next
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|term
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|int
name|commaIndex
init|=
operator|-
literal|1
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
name|spare
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|spare
operator|.
name|chars
index|[
name|spare
operator|.
name|offset
operator|+
name|i
index|]
operator|==
literal|','
condition|)
block|{
comment|// safes a string creation
name|commaIndex
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|commaIndex
operator|==
operator|-
literal|1
condition|)
block|{
assert|assert
literal|false
assert|;
return|return
name|next
operator|.
name|reset
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|final
name|double
name|lat
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
operator|new
name|String
argument_list|(
name|spare
operator|.
name|chars
argument_list|,
name|spare
operator|.
name|offset
argument_list|,
operator|(
name|commaIndex
operator|-
name|spare
operator|.
name|offset
operator|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lon
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
operator|new
name|String
argument_list|(
name|spare
operator|.
name|chars
argument_list|,
operator|(
name|spare
operator|.
name|offset
operator|+
operator|(
name|commaIndex
operator|+
literal|1
operator|)
operator|)
argument_list|,
name|spare
operator|.
name|length
operator|-
operator|(
operator|(
name|commaIndex
operator|+
literal|1
operator|)
operator|-
name|spare
operator|.
name|offset
operator|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|next
operator|.
name|reset
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
return|;
block|}
block|}
DECL|method|AbstractGeoPointIndexFieldData
specifier|public
name|AbstractGeoPointIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|valuesOrdered
specifier|public
name|boolean
name|valuesOrdered
parameter_list|()
block|{
comment|// because we might have single values? we can dynamically update a flag to reflect that
comment|// based on the atomic field data loaded
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|comparatorSource
specifier|public
specifier|final
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|,
name|SortMode
name|sortMode
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"can't sort on geo_point field without using specific sorting feature, like geo_distance"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

