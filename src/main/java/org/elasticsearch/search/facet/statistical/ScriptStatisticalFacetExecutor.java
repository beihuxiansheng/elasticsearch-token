begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.statistical
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|statistical
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
name|AtomicReaderContext
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
name|ScriptService
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
name|FacetExecutor
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
name|InternalFacet
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
DECL|class|ScriptStatisticalFacetExecutor
specifier|public
class|class
name|ScriptStatisticalFacetExecutor
extends|extends
name|FacetExecutor
block|{
DECL|field|script
specifier|private
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|field|min
specifier|private
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|max
specifier|private
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|total
specifier|private
name|double
name|total
init|=
literal|0
decl_stmt|;
DECL|field|sumOfSquares
specifier|private
name|double
name|sumOfSquares
init|=
literal|0.0
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|method|ScriptStatisticalFacetExecutor
specifier|public
name|ScriptStatisticalFacetExecutor
parameter_list|(
name|String
name|scriptLang
parameter_list|,
name|String
name|script
parameter_list|,
name|ScriptService
operator|.
name|ScriptType
name|scriptType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|script
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
name|script
argument_list|,
name|scriptType
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
return|return
operator|new
name|Collector
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|buildFacet
specifier|public
name|InternalFacet
name|buildFacet
parameter_list|(
name|String
name|facetName
parameter_list|)
block|{
return|return
operator|new
name|InternalStatisticalFacet
argument_list|(
name|facetName
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|total
argument_list|,
name|sumOfSquares
argument_list|,
name|count
argument_list|)
return|;
block|}
DECL|class|Collector
class|class
name|Collector
extends|extends
name|FacetExecutor
operator|.
name|Collector
block|{
DECL|field|min
specifier|private
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|max
specifier|private
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|total
specifier|private
name|double
name|total
init|=
literal|0
decl_stmt|;
DECL|field|sumOfSquares
specifier|private
name|double
name|sumOfSquares
init|=
literal|0.0
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
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
name|script
operator|.
name|setScorer
argument_list|(
name|scorer
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
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|script
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
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
name|script
operator|.
name|setNextDocId
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|double
name|value
init|=
name|script
operator|.
name|runAsDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|<
name|min
condition|)
block|{
name|min
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|value
expr_stmt|;
block|}
name|sumOfSquares
operator|+=
name|value
operator|*
name|value
expr_stmt|;
name|total
operator|+=
name|value
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
block|{
name|ScriptStatisticalFacetExecutor
operator|.
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|ScriptStatisticalFacetExecutor
operator|.
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|ScriptStatisticalFacetExecutor
operator|.
name|this
operator|.
name|total
operator|=
name|total
expr_stmt|;
name|ScriptStatisticalFacetExecutor
operator|.
name|this
operator|.
name|sumOfSquares
operator|=
name|sumOfSquares
expr_stmt|;
name|ScriptStatisticalFacetExecutor
operator|.
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

