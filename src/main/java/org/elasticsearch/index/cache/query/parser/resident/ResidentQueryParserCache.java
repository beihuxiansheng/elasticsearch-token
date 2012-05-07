begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.query.parser.resident
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|query
operator|.
name|parser
operator|.
name|resident
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
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
name|queryParser
operator|.
name|QueryParserSettings
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
name|query
operator|.
name|parser
operator|.
name|QueryParserCache
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A small (by default) query parser cache mainly to not parse the same query string several times  * if several shards exists on the same node.  */
end_comment

begin_class
DECL|class|ResidentQueryParserCache
specifier|public
class|class
name|ResidentQueryParserCache
extends|extends
name|AbstractIndexComponent
implements|implements
name|QueryParserCache
block|{
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|QueryParserSettings
argument_list|,
name|Query
argument_list|>
name|cache
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
annotation|@
name|Inject
DECL|method|ResidentQueryParserCache
specifier|public
name|ResidentQueryParserCache
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
name|indexSettings
operator|.
name|getAsInt
argument_list|(
literal|"index.cache.field.max_size"
argument_list|,
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"max_size"
argument_list|,
literal|100
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
literal|"index.cache.field.expire"
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
literal|"using [resident] query cache with max_size [{}], expire [{}]"
argument_list|,
name|maxSize
argument_list|,
name|expire
argument_list|)
expr_stmt|;
name|CacheBuilder
name|cacheBuilder
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|expire
operator|!=
literal|null
condition|)
block|{
name|cacheBuilder
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
name|this
operator|.
name|cache
operator|=
name|cacheBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Query
name|get
parameter_list|(
name|QueryParserSettings
name|queryString
parameter_list|)
block|{
return|return
name|cache
operator|.
name|getIfPresent
argument_list|(
name|queryString
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|QueryParserSettings
name|queryString
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|queryString
argument_list|,
name|query
argument_list|)
expr_stmt|;
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
name|invalidateAll
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
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

