begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.filter.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|filter
operator|.
name|support
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
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|MapMaker
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
name|DocSet
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
name|filter
operator|.
name|FilterCache
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
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import static
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
name|DocSets
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A base concurrent filter cache that accepts the actual cache to use.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AbstractConcurrentMapFilterCache
specifier|public
specifier|abstract
class|class
name|AbstractConcurrentMapFilterCache
extends|extends
name|AbstractIndexComponent
implements|implements
name|FilterCache
block|{
DECL|field|cache
specifier|final
name|ConcurrentMap
argument_list|<
name|Object
argument_list|,
name|ConcurrentMap
argument_list|<
name|Filter
argument_list|,
name|DocSet
argument_list|>
argument_list|>
name|cache
decl_stmt|;
DECL|method|AbstractConcurrentMapFilterCache
specifier|protected
name|AbstractConcurrentMapFilterCache
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
comment|// weak keys is fine, it will only be cleared once IndexReader references will be removed
comment|// (assuming clear(...) will not be called)
name|this
operator|.
name|cache
operator|=
operator|new
name|MapMaker
argument_list|()
operator|.
name|weakKeys
argument_list|()
operator|.
name|makeMap
argument_list|()
expr_stmt|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|clear
annotation|@
name|Override
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
DECL|method|clear
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|ConcurrentMap
argument_list|<
name|Filter
argument_list|,
name|DocSet
argument_list|>
name|map
init|=
name|cache
operator|.
name|remove
argument_list|(
name|reader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|)
decl_stmt|;
comment|// help soft/weak handling GC
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|clearUnreferenced
annotation|@
name|Override
specifier|public
name|void
name|clearUnreferenced
parameter_list|()
block|{
comment|// can't do this, since we cache on cacheKey...
comment|//        int totalCount = cache.size();
comment|//        int cleaned = 0;
comment|//        for (Iterator<IndexReader> readerIt = cache.keySet().iterator(); readerIt.hasNext();) {
comment|//            IndexReader reader = readerIt.next();
comment|//            if (reader.getRefCount()<= 0) {
comment|//                readerIt.remove();
comment|//                cleaned++;
comment|//            }
comment|//        }
comment|//        if (logger.isDebugEnabled()) {
comment|//            if (cleaned> 0) {
comment|//                logger.debug("Cleaned [{}] out of estimated total [{}]", cleaned, totalCount);
comment|//            }
comment|//        } else if (logger.isTraceEnabled()) {
comment|//            logger.trace("Cleaned [{}] out of estimated total [{}]", cleaned, totalCount);
comment|//        }
block|}
DECL|method|cache
annotation|@
name|Override
specifier|public
name|Filter
name|cache
parameter_list|(
name|Filter
name|filterToCache
parameter_list|)
block|{
if|if
condition|(
name|isCached
argument_list|(
name|filterToCache
argument_list|)
condition|)
block|{
return|return
name|filterToCache
return|;
block|}
return|return
operator|new
name|FilterCacheFilterWrapper
argument_list|(
name|filterToCache
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|isCached
annotation|@
name|Override
specifier|public
name|boolean
name|isCached
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|filter
operator|instanceof
name|FilterCacheFilterWrapper
return|;
block|}
DECL|method|buildFilterMap
specifier|protected
name|ConcurrentMap
argument_list|<
name|Filter
argument_list|,
name|DocSet
argument_list|>
name|buildFilterMap
parameter_list|()
block|{
return|return
name|newConcurrentMap
argument_list|()
return|;
block|}
comment|// LUCENE MONITOR: Check next version Lucene for CachingWrapperFilter, consider using that logic
comment|// and not use the DeletableConstantScoreQuery, instead pass the DeletesMode enum to the cache method
comment|// see: https://issues.apache.org/jira/browse/LUCENE-2468
DECL|class|FilterCacheFilterWrapper
specifier|static
class|class
name|FilterCacheFilterWrapper
extends|extends
name|Filter
block|{
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|AbstractConcurrentMapFilterCache
name|cache
decl_stmt|;
DECL|method|FilterCacheFilterWrapper
name|FilterCacheFilterWrapper
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|AbstractConcurrentMapFilterCache
name|cache
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
DECL|method|getDocIdSet
annotation|@
name|Override
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
name|ConcurrentMap
argument_list|<
name|Filter
argument_list|,
name|DocSet
argument_list|>
name|cachedFilters
init|=
name|cache
operator|.
name|cache
operator|.
name|get
argument_list|(
name|reader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedFilters
operator|==
literal|null
condition|)
block|{
name|cachedFilters
operator|=
name|cache
operator|.
name|buildFilterMap
argument_list|()
expr_stmt|;
name|cache
operator|.
name|cache
operator|.
name|putIfAbsent
argument_list|(
name|reader
operator|.
name|getFieldCacheKey
argument_list|()
argument_list|,
name|cachedFilters
argument_list|)
expr_stmt|;
block|}
name|DocSet
name|docSet
init|=
name|cachedFilters
operator|.
name|get
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|docSet
operator|!=
literal|null
condition|)
block|{
return|return
name|docSet
return|;
block|}
name|DocIdSet
name|docIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|docSet
operator|=
name|cacheable
argument_list|(
name|reader
argument_list|,
name|docIdSet
argument_list|)
expr_stmt|;
name|cachedFilters
operator|.
name|putIfAbsent
argument_list|(
name|filter
argument_list|,
name|docSet
argument_list|)
expr_stmt|;
return|return
name|docIdSet
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FilterCacheFilterWrapper("
operator|+
name|filter
operator|+
literal|")"
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FilterCacheFilterWrapper
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|this
operator|.
name|filter
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|FilterCacheFilterWrapper
operator|)
name|o
operator|)
operator|.
name|filter
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|filter
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x1117BF25
return|;
block|}
block|}
block|}
end_class

end_unit

