begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
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
name|Lists
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
name|AbstractModule
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
name|multibindings
operator|.
name|Multibinder
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
name|datehistogram
operator|.
name|DateHistogramFacetProcessor
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
name|filter
operator|.
name|FilterFacetProcessor
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
name|geodistance
operator|.
name|GeoDistanceFacetProcessor
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
name|histogram
operator|.
name|HistogramFacetProcessor
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
name|query
operator|.
name|QueryFacetProcessor
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
name|range
operator|.
name|RangeFacetProcessor
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
name|statistical
operator|.
name|StatisticalFacetProcessor
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
name|terms
operator|.
name|TermsFacetProcessor
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
name|termsstats
operator|.
name|TermsStatsFacetProcessor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FacetModule
specifier|public
class|class
name|FacetModule
extends|extends
name|AbstractModule
block|{
DECL|field|processors
specifier|private
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|FacetProcessor
argument_list|>
argument_list|>
name|processors
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|FacetModule
specifier|public
name|FacetModule
parameter_list|()
block|{
name|processors
operator|.
name|add
argument_list|(
name|FilterFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|QueryFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|GeoDistanceFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|HistogramFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|DateHistogramFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|RangeFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|StatisticalFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|TermsFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|TermsStatsFacetProcessor
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|addFacetProcessor
specifier|public
name|void
name|addFacetProcessor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|FacetProcessor
argument_list|>
name|facetProcessor
parameter_list|)
block|{
name|processors
operator|.
name|add
argument_list|(
name|facetProcessor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|Multibinder
argument_list|<
name|FacetProcessor
argument_list|>
name|multibinder
init|=
name|Multibinder
operator|.
name|newSetBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|FacetProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|FacetProcessor
argument_list|>
name|processor
range|:
name|processors
control|)
block|{
name|multibinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|processor
argument_list|)
expr_stmt|;
block|}
name|bind
argument_list|(
name|FacetProcessors
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|FacetParseElement
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|FacetPhase
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

