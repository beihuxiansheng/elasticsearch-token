begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.type.child
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|type
operator|.
name|child
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
name|IndexReader
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
name|Collector
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
name|Scorer
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
name|OpenBitSet
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
name|BytesWrap
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
name|id
operator|.
name|IdReaderTypeCache
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
name|internal
operator|.
name|SearchContext
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
name|HashMap
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ChildCollector
specifier|public
class|class
name|ChildCollector
extends|extends
name|Collector
block|{
DECL|field|parentType
specifier|private
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|field|typeCacheMap
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|IdReaderTypeCache
argument_list|>
name|typeCacheMap
decl_stmt|;
DECL|field|parentDocs
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|OpenBitSet
argument_list|>
name|parentDocs
decl_stmt|;
DECL|field|typeCache
specifier|private
name|IdReaderTypeCache
name|typeCache
decl_stmt|;
DECL|method|ChildCollector
specifier|public
name|ChildCollector
parameter_list|(
name|String
name|parentType
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|parentDocs
operator|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|OpenBitSet
argument_list|>
argument_list|()
expr_stmt|;
comment|// create a specific type map lookup for faster lookup operations per doc
name|this
operator|.
name|typeCacheMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|IdReaderTypeCache
argument_list|>
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|subReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexReader
name|indexReader
range|:
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|subReaders
argument_list|()
control|)
block|{
name|typeCacheMap
operator|.
name|put
argument_list|(
name|indexReader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|,
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|indexReader
argument_list|)
operator|.
name|type
argument_list|(
name|parentType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parentDocs
specifier|public
name|Map
argument_list|<
name|Object
argument_list|,
name|OpenBitSet
argument_list|>
name|parentDocs
parameter_list|()
block|{
return|return
name|this
operator|.
name|parentDocs
return|;
block|}
DECL|method|setScorer
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{      }
DECL|method|collect
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesWrap
name|parentId
init|=
name|typeCache
operator|.
name|parentIdByDoc
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentId
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|IndexReader
name|indexReader
range|:
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|subReaders
argument_list|()
control|)
block|{
name|int
name|parentDocId
init|=
name|typeCacheMap
operator|.
name|get
argument_list|(
name|indexReader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|)
operator|.
name|docById
argument_list|(
name|parentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentDocId
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|indexReader
operator|.
name|isDeleted
argument_list|(
name|parentDocId
argument_list|)
condition|)
block|{
name|OpenBitSet
name|docIdSet
init|=
name|parentDocs
argument_list|()
operator|.
name|get
argument_list|(
name|indexReader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
condition|)
block|{
name|docIdSet
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|indexReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|parentDocs
operator|.
name|put
argument_list|(
name|indexReader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|,
name|docIdSet
argument_list|)
expr_stmt|;
block|}
name|docIdSet
operator|.
name|fastSet
argument_list|(
name|parentDocId
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
DECL|method|setNextReader
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|typeCache
operator|=
name|typeCacheMap
operator|.
name|get
argument_list|(
name|reader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|acceptsDocsOutOfOrder
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

