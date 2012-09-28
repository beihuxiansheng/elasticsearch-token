begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.child
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|child
package|;
end_package

begin_import
import|import
name|gnu
operator|.
name|trove
operator|.
name|set
operator|.
name|hash
operator|.
name|THashSet
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
name|Filter
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
name|Query
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
name|FixedBitSet
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|CacheRecycler
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
name|bytes
operator|.
name|HashedBytesArray
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
name|lucene
operator|.
name|docset
operator|.
name|GetDocSet
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
name|lucene
operator|.
name|search
operator|.
name|NoopCollector
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
name|ScopePhase
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
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|HasChildFilter
specifier|public
specifier|abstract
class|class
name|HasChildFilter
extends|extends
name|Filter
implements|implements
name|ScopePhase
operator|.
name|CollectorPhase
block|{
DECL|field|childQuery
specifier|final
name|Query
name|childQuery
decl_stmt|;
DECL|field|scope
specifier|final
name|String
name|scope
decl_stmt|;
DECL|field|parentType
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|childType
specifier|final
name|String
name|childType
decl_stmt|;
DECL|field|searchContext
specifier|final
name|SearchContext
name|searchContext
decl_stmt|;
DECL|method|HasChildFilter
specifier|protected
name|HasChildFilter
parameter_list|(
name|Query
name|childQuery
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|String
name|childType
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|this
operator|.
name|searchContext
operator|=
name|searchContext
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
name|this
operator|.
name|childType
operator|=
name|childType
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|childQuery
operator|=
name|childQuery
expr_stmt|;
block|}
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|childQuery
return|;
block|}
DECL|method|scope
specifier|public
name|String
name|scope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"child_filter["
argument_list|)
operator|.
name|append
argument_list|(
name|childType
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|parentType
argument_list|)
operator|.
name|append
argument_list|(
literal|"]("
argument_list|)
operator|.
name|append
argument_list|(
name|childQuery
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|create
specifier|public
specifier|static
name|HasChildFilter
name|create
parameter_list|(
name|Query
name|childQuery
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|String
name|childType
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|,
name|String
name|executionType
parameter_list|)
block|{
comment|// This mechanism is experimental and will most likely be removed.
if|if
condition|(
literal|"bitset"
operator|.
name|equals
argument_list|(
name|executionType
argument_list|)
condition|)
block|{
return|return
operator|new
name|Bitset
argument_list|(
name|childQuery
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|searchContext
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"uid"
operator|.
name|endsWith
argument_list|(
name|executionType
argument_list|)
condition|)
block|{
return|return
operator|new
name|Uid
argument_list|(
name|childQuery
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|searchContext
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Illegal has_child execution type: "
operator|+
name|executionType
argument_list|)
throw|;
block|}
DECL|class|Bitset
specifier|static
class|class
name|Bitset
extends|extends
name|HasChildFilter
block|{
DECL|field|parentDocs
specifier|private
name|Map
argument_list|<
name|Object
argument_list|,
name|FixedBitSet
argument_list|>
name|parentDocs
decl_stmt|;
DECL|method|Bitset
specifier|public
name|Bitset
parameter_list|(
name|Query
name|childQuery
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|String
name|childType
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|super
argument_list|(
name|childQuery
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|searchContext
argument_list|)
expr_stmt|;
block|}
DECL|method|requiresProcessing
specifier|public
name|boolean
name|requiresProcessing
parameter_list|()
block|{
return|return
name|parentDocs
operator|==
literal|null
return|;
block|}
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
return|return
operator|new
name|ChildCollector
argument_list|(
name|parentType
argument_list|,
name|searchContext
argument_list|)
return|;
block|}
DECL|method|processCollector
specifier|public
name|void
name|processCollector
parameter_list|(
name|Collector
name|collector
parameter_list|)
block|{
name|this
operator|.
name|parentDocs
operator|=
operator|(
operator|(
name|ChildCollector
operator|)
name|collector
operator|)
operator|.
name|parentDocs
argument_list|()
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|parentDocs
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentDocs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"has_child filter/query hasn't executed properly"
argument_list|)
throw|;
block|}
comment|// ok to return null
return|return
name|parentDocs
operator|.
name|get
argument_list|(
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|Uid
specifier|static
class|class
name|Uid
extends|extends
name|HasChildFilter
block|{
DECL|field|collectedUids
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|collectedUids
decl_stmt|;
DECL|method|Uid
name|Uid
parameter_list|(
name|Query
name|childQuery
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|parentType
parameter_list|,
name|String
name|childType
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|)
block|{
name|super
argument_list|(
name|childQuery
argument_list|,
name|scope
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|searchContext
argument_list|)
expr_stmt|;
block|}
DECL|method|requiresProcessing
specifier|public
name|boolean
name|requiresProcessing
parameter_list|()
block|{
return|return
name|collectedUids
operator|==
literal|null
return|;
block|}
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
name|collectedUids
operator|=
name|CacheRecycler
operator|.
name|popHashSet
argument_list|()
expr_stmt|;
return|return
operator|new
name|UidCollector
argument_list|(
name|parentType
argument_list|,
name|searchContext
argument_list|,
name|collectedUids
argument_list|)
return|;
block|}
DECL|method|processCollector
specifier|public
name|void
name|processCollector
parameter_list|(
name|Collector
name|collector
parameter_list|)
block|{
name|collectedUids
operator|=
operator|(
operator|(
name|UidCollector
operator|)
name|collector
operator|)
operator|.
name|collectedUids
expr_stmt|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|collectedUids
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"has_child filter/query hasn't executed properly"
argument_list|)
throw|;
block|}
name|IdReaderTypeCache
name|idReaderTypeCache
init|=
name|searchContext
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|reader
argument_list|)
operator|.
name|type
argument_list|(
name|parentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|idReaderTypeCache
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ParentDocSet
argument_list|(
name|reader
argument_list|,
name|collectedUids
argument_list|,
name|idReaderTypeCache
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|collectedUids
operator|!=
literal|null
condition|)
block|{
name|CacheRecycler
operator|.
name|pushHashSet
argument_list|(
name|collectedUids
argument_list|)
expr_stmt|;
block|}
name|collectedUids
operator|=
literal|null
expr_stmt|;
block|}
DECL|class|ParentDocSet
specifier|static
class|class
name|ParentDocSet
extends|extends
name|GetDocSet
block|{
DECL|field|reader
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|parents
specifier|final
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|parents
decl_stmt|;
DECL|field|typeCache
specifier|final
name|IdReaderTypeCache
name|typeCache
decl_stmt|;
DECL|method|ParentDocSet
name|ParentDocSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|parents
parameter_list|,
name|IdReaderTypeCache
name|typeCache
parameter_list|)
block|{
name|super
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|parents
operator|=
name|parents
expr_stmt|;
name|this
operator|.
name|typeCache
operator|=
name|typeCache
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|!
name|reader
operator|.
name|isDeleted
argument_list|(
name|doc
argument_list|)
operator|&&
name|parents
operator|.
name|contains
argument_list|(
name|typeCache
operator|.
name|idByDoc
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|UidCollector
specifier|static
class|class
name|UidCollector
extends|extends
name|NoopCollector
block|{
DECL|field|parentType
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|context
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|field|collectedUids
specifier|final
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|collectedUids
decl_stmt|;
DECL|field|typeCache
specifier|private
name|IdReaderTypeCache
name|typeCache
decl_stmt|;
DECL|method|UidCollector
name|UidCollector
parameter_list|(
name|String
name|parentType
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|THashSet
argument_list|<
name|HashedBytesArray
argument_list|>
name|collectedUids
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
name|collectedUids
operator|=
name|collectedUids
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
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
name|collectedUids
operator|.
name|add
argument_list|(
name|typeCache
operator|.
name|parentIdByDoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
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
name|context
operator|.
name|idCache
argument_list|()
operator|.
name|reader
argument_list|(
name|reader
argument_list|)
operator|.
name|type
argument_list|(
name|parentType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

