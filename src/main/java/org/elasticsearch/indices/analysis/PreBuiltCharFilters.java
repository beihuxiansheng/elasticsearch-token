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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|charfilter
operator|.
name|HTMLStripCharFilter
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|CharFilterFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|PreBuiltCacheFactory
operator|.
name|CachingStrategy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|PreBuiltCharFilters
specifier|public
enum|enum
name|PreBuiltCharFilters
block|{
DECL|method|HTML_STRIP
DECL|method|HTML_STRIP
name|HTML_STRIP
parameter_list|(
name|CachingStrategy
operator|.
name|ONE
parameter_list|)
block|{
annotation|@
name|Override
specifier|public
name|Reader
name|create
parameter_list|(
name|Reader
name|tokenStream
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
return|return
operator|new
name|HTMLStripCharFilter
argument_list|(
name|tokenStream
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|create
specifier|abstract
specifier|public
name|Reader
name|create
parameter_list|(
name|Reader
name|tokenStream
parameter_list|,
name|Version
name|version
parameter_list|)
function_decl|;
DECL|field|cache
specifier|protected
specifier|final
name|PreBuiltCacheFactory
operator|.
name|PreBuiltCache
argument_list|<
name|CharFilterFactory
argument_list|>
name|cache
decl_stmt|;
DECL|method|PreBuiltCharFilters
name|PreBuiltCharFilters
parameter_list|(
name|CachingStrategy
name|cachingStrategy
parameter_list|)
block|{
name|cache
operator|=
name|PreBuiltCacheFactory
operator|.
name|getCache
argument_list|(
name|cachingStrategy
argument_list|)
expr_stmt|;
block|}
DECL|method|getCharFilterFactory
specifier|public
specifier|synchronized
name|CharFilterFactory
name|getCharFilterFactory
parameter_list|(
specifier|final
name|Version
name|version
parameter_list|)
block|{
name|CharFilterFactory
name|charFilterFactory
init|=
name|cache
operator|.
name|get
argument_list|(
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|charFilterFactory
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|finalName
init|=
name|name
argument_list|()
decl_stmt|;
name|charFilterFactory
operator|=
operator|new
name|CharFilterFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|finalName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Reader
name|create
parameter_list|(
name|Reader
name|tokenStream
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|finalName
argument_list|)
operator|.
name|create
argument_list|(
name|tokenStream
argument_list|,
name|version
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|version
argument_list|,
name|charFilterFactory
argument_list|)
expr_stmt|;
block|}
return|return
name|charFilterFactory
return|;
block|}
block|}
end_enum

end_unit

