begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.filter.soft
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
name|soft
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
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|base
operator|.
name|Objects
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
name|MapEvictionListener
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
name|inject
operator|.
name|Inject
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
name|metrics
operator|.
name|CounterMetric
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
name|unit
operator|.
name|TimeValue
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
name|support
operator|.
name|AbstractConcurrentMapFilterCache
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettingsService
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A soft reference based filter cache that has soft keys on the<tt>IndexReader</tt>.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SoftFilterCache
specifier|public
class|class
name|SoftFilterCache
extends|extends
name|AbstractConcurrentMapFilterCache
implements|implements
name|MapEvictionListener
argument_list|<
name|Filter
argument_list|,
name|DocSet
argument_list|>
block|{
DECL|field|indexSettingsService
specifier|private
specifier|final
name|IndexSettingsService
name|indexSettingsService
decl_stmt|;
DECL|field|maxSize
specifier|private
specifier|volatile
name|int
name|maxSize
decl_stmt|;
DECL|field|expire
specifier|private
specifier|volatile
name|TimeValue
name|expire
decl_stmt|;
DECL|field|evictions
specifier|private
specifier|final
name|CounterMetric
name|evictions
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|applySettings
specifier|private
specifier|final
name|ApplySettings
name|applySettings
init|=
operator|new
name|ApplySettings
argument_list|()
decl_stmt|;
DECL|method|SoftFilterCache
annotation|@
name|Inject
specifier|public
name|SoftFilterCache
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|IndexSettingsService
name|indexSettingsService
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
name|indexSettingsService
operator|=
name|indexSettingsService
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|indexSettings
operator|.
name|getAsInt
argument_list|(
literal|"index.cache.filter.max_size"
argument_list|,
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"max_size"
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|expire
operator|=
name|indexSettings
operator|.
name|getAsTime
argument_list|(
literal|"index.cache.filter.expire"
argument_list|,
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"expire"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using [soft] filter cache with max_size [{}], expire [{}]"
argument_list|,
name|maxSize
argument_list|,
name|expire
argument_list|)
expr_stmt|;
name|indexSettingsService
operator|.
name|addListener
argument_list|(
name|applySettings
argument_list|)
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
name|indexSettingsService
operator|.
name|removeListener
argument_list|(
name|applySettings
argument_list|)
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|buildFilterMap
annotation|@
name|Override
specifier|protected
name|ConcurrentMap
argument_list|<
name|Object
argument_list|,
name|DocSet
argument_list|>
name|buildFilterMap
parameter_list|()
block|{
comment|// DocSet are not really stored with strong reference only when searching on them...
comment|// Filter might be stored in query cache
name|MapMaker
name|mapMaker
init|=
operator|new
name|MapMaker
argument_list|()
operator|.
name|softValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxSize
operator|!=
operator|-
literal|1
condition|)
block|{
name|mapMaker
operator|.
name|maximumSize
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expire
operator|!=
literal|null
operator|&&
name|expire
operator|.
name|nanos
argument_list|()
operator|>
literal|0
condition|)
block|{
name|mapMaker
operator|.
name|expireAfterAccess
argument_list|(
name|expire
operator|.
name|nanos
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
name|mapMaker
operator|.
name|evictionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|mapMaker
operator|.
name|makeMap
argument_list|()
return|;
block|}
DECL|method|type
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
literal|"soft"
return|;
block|}
DECL|method|evictions
annotation|@
name|Override
specifier|public
name|long
name|evictions
parameter_list|()
block|{
return|return
name|evictions
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|onEviction
annotation|@
name|Override
specifier|public
name|void
name|onEviction
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|DocSet
name|docSet
parameter_list|)
block|{
name|evictions
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
static|static
block|{
name|IndexMetaData
operator|.
name|addDynamicSettings
argument_list|(
literal|"index.cache.field.max_size"
argument_list|,
literal|"index.cache.field.expire"
argument_list|)
expr_stmt|;
block|}
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|IndexSettingsService
operator|.
name|Listener
block|{
DECL|method|onRefreshSettings
annotation|@
name|Override
specifier|public
name|void
name|onRefreshSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|int
name|maxSize
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"index.cache.filter.max_size"
argument_list|,
name|SoftFilterCache
operator|.
name|this
operator|.
name|maxSize
argument_list|)
decl_stmt|;
name|TimeValue
name|expire
init|=
name|settings
operator|.
name|getAsTime
argument_list|(
literal|"index.cache.filter.expire"
argument_list|,
name|SoftFilterCache
operator|.
name|this
operator|.
name|expire
argument_list|)
decl_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|maxSize
operator|!=
name|SoftFilterCache
operator|.
name|this
operator|.
name|maxSize
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating index.cache.filter.max_size from [{}] to [{}]"
argument_list|,
name|SoftFilterCache
operator|.
name|this
operator|.
name|maxSize
argument_list|,
name|maxSize
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
name|SoftFilterCache
operator|.
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equal
argument_list|(
name|expire
argument_list|,
name|SoftFilterCache
operator|.
name|this
operator|.
name|expire
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating index.cache.filter.expire from [{}] to [{}]"
argument_list|,
name|SoftFilterCache
operator|.
name|this
operator|.
name|expire
argument_list|,
name|expire
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
name|SoftFilterCache
operator|.
name|this
operator|.
name|expire
operator|=
name|expire
expr_stmt|;
block|}
if|if
condition|(
name|changed
condition|)
block|{
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

