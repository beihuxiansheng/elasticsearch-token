begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms.unmapped
package|package
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
name|unmapped
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
name|ImmutableList
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
name|AtomicReaderContext
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
name|facet
operator|.
name|terms
operator|.
name|TermsFacet
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
name|strings
operator|.
name|InternalStringTermsFacet
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
name|Collection
import|;
end_import

begin_comment
comment|/**  * A facet executor that only aggregates the missing count for unmapped fields and builds a terms facet over it  */
end_comment

begin_class
DECL|class|UnmappedFieldExecutor
specifier|public
class|class
name|UnmappedFieldExecutor
extends|extends
name|FacetExecutor
block|{
DECL|field|size
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|comparatorType
specifier|final
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|missing
name|long
name|missing
decl_stmt|;
DECL|method|UnmappedFieldExecutor
specifier|public
name|UnmappedFieldExecutor
parameter_list|(
name|int
name|size
parameter_list|,
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
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
name|Collection
argument_list|<
name|InternalStringTermsFacet
operator|.
name|TermEntry
argument_list|>
name|entries
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
return|return
operator|new
name|InternalStringTermsFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|entries
argument_list|,
name|missing
argument_list|,
literal|0
argument_list|)
return|;
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
DECL|class|Collector
specifier|final
class|class
name|Collector
extends|extends
name|FacetExecutor
operator|.
name|Collector
block|{
DECL|field|missing
specifier|private
name|long
name|missing
decl_stmt|;
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
block|{
name|UnmappedFieldExecutor
operator|.
name|this
operator|.
name|missing
operator|=
name|missing
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
name|missing
operator|++
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
block|{         }
block|}
block|}
end_class

end_unit

