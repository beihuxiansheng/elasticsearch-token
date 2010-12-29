begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.histogram
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|histogram
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
name|elasticsearch
operator|.
name|common
operator|.
name|trove
operator|.
name|TLongDoubleHashMap
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
name|trove
operator|.
name|TLongLongHashMap
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
name|search
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ScriptHistogramFacetCollector
specifier|public
class|class
name|ScriptHistogramFacetCollector
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
DECL|field|interval
specifier|private
specifier|final
name|long
name|interval
decl_stmt|;
DECL|field|comparatorType
specifier|private
specifier|final
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|counts
specifier|private
specifier|final
name|TLongLongHashMap
name|counts
init|=
operator|new
name|TLongLongHashMap
argument_list|()
decl_stmt|;
DECL|field|totals
specifier|private
specifier|final
name|TLongDoubleHashMap
name|totals
init|=
operator|new
name|TLongDoubleHashMap
argument_list|()
decl_stmt|;
DECL|method|ScriptHistogramFacetCollector
specifier|public
name|ScriptHistogramFacetCollector
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
name|long
name|interval
parameter_list|,
name|HistogramFacet
operator|.
name|ComparatorType
name|comparatorType
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
operator|new
name|SearchScript
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
argument_list|,
name|context
operator|.
name|scriptService
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueScript
operator|=
operator|new
name|SearchScript
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
argument_list|,
name|context
operator|.
name|scriptService
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
operator|>
literal|0
condition|?
name|interval
else|:
literal|0
expr_stmt|;
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
block|}
DECL|method|doCollect
annotation|@
name|Override
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
name|Number
name|keyValue
init|=
operator|(
name|Number
operator|)
name|keyScript
operator|.
name|execute
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|long
name|bucket
decl_stmt|;
if|if
condition|(
name|interval
operator|==
literal|0
condition|)
block|{
name|bucket
operator|=
name|keyValue
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bucket
operator|=
name|bucket
argument_list|(
name|keyValue
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
name|double
name|value
init|=
operator|(
operator|(
name|Number
operator|)
name|valueScript
operator|.
name|execute
argument_list|(
name|doc
argument_list|)
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|counts
operator|.
name|adjustOrPutValue
argument_list|(
name|bucket
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|totals
operator|.
name|adjustOrPutValue
argument_list|(
name|bucket
argument_list|,
name|value
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNextReader
annotation|@
name|Override
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
DECL|method|facet
annotation|@
name|Override
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
return|return
operator|new
name|InternalHistogramFacet
argument_list|(
name|facetName
argument_list|,
literal|"_na"
argument_list|,
literal|"_na"
argument_list|,
operator|-
literal|1
argument_list|,
name|comparatorType
argument_list|,
name|counts
argument_list|,
name|totals
argument_list|)
return|;
block|}
DECL|method|bucket
specifier|public
specifier|static
name|long
name|bucket
parameter_list|(
name|double
name|value
parameter_list|,
name|long
name|interval
parameter_list|)
block|{
return|return
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|value
operator|/
name|interval
argument_list|)
operator|)
operator|*
name|interval
operator|)
return|;
block|}
block|}
end_class

end_unit

