begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
DECL|class|PreBuiltCacheFactory
specifier|public
class|class
name|PreBuiltCacheFactory
block|{
comment|/**      * The strategy of caching the analyzer      *      * ONE               Exactly one version is stored. Useful for analyzers which do not store version information      * LUCENE            Exactly one version for each lucene version is stored. Useful to prevent different analyzers with the same version      * ELASTICSEARCH     Exactly one version per elasticsearch version is stored. Useful if you change an analyzer between elasticsearch releases, when the lucene version does not change      */
DECL|enum|CachingStrategy
DECL|enum constant|ONE
DECL|enum constant|LUCENE
DECL|enum constant|ELASTICSEARCH
specifier|static
enum|enum
name|CachingStrategy
block|{
name|ONE
block|,
name|LUCENE
block|,
name|ELASTICSEARCH
block|}
empty_stmt|;
DECL|interface|PreBuiltCache
specifier|public
interface|interface
name|PreBuiltCache
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|get
name|T
name|get
parameter_list|(
name|Version
name|version
parameter_list|)
function_decl|;
DECL|method|put
name|void
name|put
parameter_list|(
name|Version
name|version
parameter_list|,
name|T
name|t
parameter_list|)
function_decl|;
block|}
DECL|method|PreBuiltCacheFactory
specifier|private
name|PreBuiltCacheFactory
parameter_list|()
block|{}
DECL|method|getCache
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PreBuiltCache
argument_list|<
name|T
argument_list|>
name|getCache
parameter_list|(
name|CachingStrategy
name|cachingStrategy
parameter_list|)
block|{
switch|switch
condition|(
name|cachingStrategy
condition|)
block|{
case|case
name|ONE
case|:
return|return
operator|new
name|PreBuiltCacheStrategyOne
argument_list|<
name|T
argument_list|>
argument_list|()
return|;
case|case
name|LUCENE
case|:
return|return
operator|new
name|PreBuiltCacheStrategyLucene
argument_list|<
name|T
argument_list|>
argument_list|()
return|;
case|case
name|ELASTICSEARCH
case|:
return|return
operator|new
name|PreBuiltCacheStrategyElasticsearch
argument_list|<
name|T
argument_list|>
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"No action configured for caching strategy["
operator|+
name|cachingStrategy
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * This is a pretty simple cache, it only contains one version      */
DECL|class|PreBuiltCacheStrategyOne
specifier|private
specifier|static
class|class
name|PreBuiltCacheStrategyOne
parameter_list|<
name|T
parameter_list|>
implements|implements
name|PreBuiltCache
argument_list|<
name|T
argument_list|>
block|{
DECL|field|model
specifier|private
name|T
name|model
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
name|model
return|;
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|Version
name|version
parameter_list|,
name|T
name|model
parameter_list|)
block|{
name|this
operator|.
name|model
operator|=
name|model
expr_stmt|;
block|}
block|}
comment|/**      * This cache contains one version for each elasticsearch version object      */
DECL|class|PreBuiltCacheStrategyElasticsearch
specifier|private
specifier|static
class|class
name|PreBuiltCacheStrategyElasticsearch
parameter_list|<
name|T
parameter_list|>
implements|implements
name|PreBuiltCache
argument_list|<
name|T
argument_list|>
block|{
DECL|field|mapModel
name|Map
argument_list|<
name|Version
argument_list|,
name|T
argument_list|>
name|mapModel
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
literal|2
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
name|mapModel
operator|.
name|get
argument_list|(
name|version
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
name|Version
name|version
parameter_list|,
name|T
name|model
parameter_list|)
block|{
name|mapModel
operator|.
name|put
argument_list|(
name|version
argument_list|,
name|model
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This cache uses the lucene version for caching      */
DECL|class|PreBuiltCacheStrategyLucene
specifier|private
specifier|static
class|class
name|PreBuiltCacheStrategyLucene
parameter_list|<
name|T
parameter_list|>
implements|implements
name|PreBuiltCache
argument_list|<
name|T
argument_list|>
block|{
DECL|field|mapModel
specifier|private
name|Map
argument_list|<
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
argument_list|,
name|T
argument_list|>
name|mapModel
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
literal|2
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
name|mapModel
operator|.
name|get
argument_list|(
name|version
operator|.
name|luceneVersion
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
name|org
operator|.
name|elasticsearch
operator|.
name|Version
name|version
parameter_list|,
name|T
name|model
parameter_list|)
block|{
name|mapModel
operator|.
name|put
argument_list|(
name|version
operator|.
name|luceneVersion
argument_list|,
name|model
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

