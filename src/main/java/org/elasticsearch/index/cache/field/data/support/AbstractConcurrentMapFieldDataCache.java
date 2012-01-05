begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.field.data.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|support
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
name|cache
operator|.
name|Cache
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
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|AbstractIndexComponent
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
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|FieldDataCache
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
name|field
operator|.
name|data
operator|.
name|FieldData
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|settings
operator|.
name|IndexSettings
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractConcurrentMapFieldDataCache
specifier|public
specifier|abstract
class|class
name|AbstractConcurrentMapFieldDataCache
extends|extends
name|AbstractIndexComponent
implements|implements
name|FieldDataCache
implements|,
name|IndexReader
operator|.
name|ReaderFinishedListener
block|{
DECL|field|cache
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|Object
argument_list|,
name|Cache
argument_list|<
name|String
argument_list|,
name|FieldData
argument_list|>
argument_list|>
name|cache
decl_stmt|;
DECL|field|creationMutex
specifier|private
specifier|final
name|Object
name|creationMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|AbstractConcurrentMapFieldDataCache
specifier|protected
name|AbstractConcurrentMapFieldDataCache
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Cache
argument_list|<
name|String
argument_list|,
name|FieldData
argument_list|>
argument_list|>
name|entry
range|:
name|cache
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|invalidate
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finished
specifier|public
name|void
name|finished
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|clear
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|cache
operator|.
name|remove
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
comment|// the overhead of the map is not really relevant...
name|long
name|sizeInBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Cache
argument_list|<
name|String
argument_list|,
name|FieldData
argument_list|>
name|map
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|FieldData
name|fieldData
range|:
name|map
operator|.
name|asMap
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|sizeInBytes
operator|+=
name|fieldData
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|sizeInBytes
return|;
block|}
annotation|@
name|Override
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|long
name|sizeInBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Cache
argument_list|<
name|String
argument_list|,
name|FieldData
argument_list|>
name|map
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|FieldData
name|fieldData
init|=
name|map
operator|.
name|getIfPresent
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldData
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|+=
name|fieldData
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|sizeInBytes
return|;
block|}
annotation|@
name|Override
DECL|method|cache
specifier|public
name|FieldData
name|cache
parameter_list|(
name|FieldDataType
name|type
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|Cache
argument_list|<
name|String
argument_list|,
name|FieldData
argument_list|>
name|fieldDataCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldDataCache
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|creationMutex
init|)
block|{
name|fieldDataCache
operator|=
name|cache
operator|.
name|get
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldDataCache
operator|==
literal|null
condition|)
block|{
name|fieldDataCache
operator|=
name|buildFieldDataMap
argument_list|()
expr_stmt|;
name|reader
operator|.
name|addReaderFinishedListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|fieldDataCache
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|FieldData
name|fieldData
init|=
name|fieldDataCache
operator|.
name|getIfPresent
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldData
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|fieldDataCache
init|)
block|{
name|fieldData
operator|=
name|fieldDataCache
operator|.
name|getIfPresent
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldData
operator|==
literal|null
condition|)
block|{
name|fieldData
operator|=
name|FieldData
operator|.
name|load
argument_list|(
name|type
argument_list|,
name|reader
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|fieldDataCache
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|fieldData
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|fieldData
return|;
block|}
DECL|method|buildFieldDataMap
specifier|protected
specifier|abstract
name|Cache
argument_list|<
name|String
argument_list|,
name|FieldData
argument_list|>
name|buildFieldDataMap
parameter_list|()
function_decl|;
block|}
end_class

end_unit

