begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.range
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|range
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
name|Scorer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
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
name|facet
operator|.
name|AbstractFacetCollector
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
name|facet
operator|.
name|Facet
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
DECL|class|ScriptRangeFacetCollector
specifier|public
class|class
name|ScriptRangeFacetCollector
extends|extends
name|AbstractFacetCollector
block|{
DECL|field|keyScript
specifier|private
specifier|final
name|SearchScript
name|keyScript
decl_stmt|;
DECL|field|valueScript
specifier|private
specifier|final
name|SearchScript
name|valueScript
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|RangeFacet
operator|.
name|Entry
index|[]
name|entries
decl_stmt|;
DECL|method|ScriptRangeFacetCollector
specifier|public
name|ScriptRangeFacetCollector
parameter_list|(
name|String
name|facetName
parameter_list|,
name|String
name|scriptLang
parameter_list|,
name|String
name|keyScript
parameter_list|,
name|String
name|valueScript
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|RangeFacet
operator|.
name|Entry
index|[]
name|entries
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|facetName
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyScript
operator|=
name|context
operator|.
name|scriptService
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
argument_list|,
name|scriptLang
argument_list|,
name|keyScript
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueScript
operator|=
name|context
operator|.
name|scriptService
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
argument_list|,
name|scriptLang
argument_list|,
name|valueScript
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|keyScript
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|valueScript
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
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
name|keyScript
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|valueScript
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doCollect
specifier|protected
name|void
name|doCollect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|keyScript
operator|.
name|setNextDocId
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|valueScript
operator|.
name|setNextDocId
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|double
name|key
init|=
name|keyScript
operator|.
name|runAsDouble
argument_list|()
decl_stmt|;
name|double
name|value
init|=
name|valueScript
operator|.
name|runAsDouble
argument_list|()
decl_stmt|;
for|for
control|(
name|RangeFacet
operator|.
name|Entry
name|entry
range|:
name|entries
control|)
block|{
if|if
condition|(
name|key
operator|>=
name|entry
operator|.
name|getFrom
argument_list|()
operator|&&
name|key
operator|<
name|entry
operator|.
name|getTo
argument_list|()
condition|)
block|{
name|entry
operator|.
name|count
operator|++
expr_stmt|;
name|entry
operator|.
name|totalCount
operator|++
expr_stmt|;
name|entry
operator|.
name|total
operator|+=
name|value
expr_stmt|;
if|if
condition|(
name|value
operator|<
name|entry
operator|.
name|min
condition|)
block|{
name|entry
operator|.
name|min
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|>
name|entry
operator|.
name|max
condition|)
block|{
name|entry
operator|.
name|max
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|facet
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
return|return
operator|new
name|InternalRangeFacet
argument_list|(
name|facetName
argument_list|,
name|entries
argument_list|)
return|;
block|}
block|}
end_class

end_unit

