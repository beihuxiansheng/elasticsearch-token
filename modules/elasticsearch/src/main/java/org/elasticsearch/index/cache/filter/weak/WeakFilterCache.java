begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.filter.weak
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
name|weak
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
comment|/**  * A weak reference based filter cache that has weak keys on the<tt>IndexReader</tt>.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|WeakFilterCache
specifier|public
class|class
name|WeakFilterCache
extends|extends
name|AbstractConcurrentMapFilterCache
block|{
DECL|field|maxSize
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|field|expire
specifier|private
specifier|final
name|TimeValue
name|expire
decl_stmt|;
DECL|method|WeakFilterCache
annotation|@
name|Inject
specifier|public
name|WeakFilterCache
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
name|maxSize
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"max_size"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|expire
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"expire"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|buildFilterMap
annotation|@
name|Override
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
comment|// DocSet are not really stored with strong reference only when searching on them...
comment|// Filter might be stored in query cache
name|MapMaker
name|mapMaker
init|=
operator|new
name|MapMaker
argument_list|()
operator|.
name|weakValues
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
literal|"weak"
return|;
block|}
block|}
end_class

end_unit

