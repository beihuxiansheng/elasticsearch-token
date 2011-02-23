begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.termsstats.strings
package|package
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
name|strings
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
name|ElasticSearchIllegalArgumentException
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
name|ImmutableList
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
name|thread
operator|.
name|ThreadLocals
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
name|ExtTHashMap
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
name|field
operator|.
name|data
operator|.
name|FieldDataCache
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
name|field
operator|.
name|data
operator|.
name|FieldData
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|field
operator|.
name|data
operator|.
name|NumericFieldData
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
name|mapper
operator|.
name|FieldMapper
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
name|mapper
operator|.
name|MapperService
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
name|facet
operator|.
name|termsstats
operator|.
name|TermsStatsFacet
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
name|*
import|;
end_import

begin_class
DECL|class|TermsStatsStringFacetCollector
specifier|public
class|class
name|TermsStatsStringFacetCollector
extends|extends
name|AbstractFacetCollector
block|{
DECL|field|comparatorType
specifier|private
specifier|final
name|TermsStatsFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|fieldDataCache
specifier|private
specifier|final
name|FieldDataCache
name|fieldDataCache
decl_stmt|;
DECL|field|keyFieldName
specifier|private
specifier|final
name|String
name|keyFieldName
decl_stmt|;
DECL|field|valueFieldName
specifier|private
specifier|final
name|String
name|valueFieldName
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|numberOfShards
specifier|private
specifier|final
name|int
name|numberOfShards
decl_stmt|;
DECL|field|keyFieldDataType
specifier|private
specifier|final
name|FieldDataType
name|keyFieldDataType
decl_stmt|;
DECL|field|keyFieldData
specifier|private
name|FieldData
name|keyFieldData
decl_stmt|;
DECL|field|valueFieldDataType
specifier|private
specifier|final
name|FieldDataType
name|valueFieldDataType
decl_stmt|;
DECL|field|valueFieldData
specifier|private
name|NumericFieldData
name|valueFieldData
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|SearchScript
name|script
decl_stmt|;
DECL|field|missing
specifier|private
name|int
name|missing
init|=
literal|0
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
name|entries
decl_stmt|;
DECL|method|TermsStatsStringFacetCollector
specifier|public
name|TermsStatsStringFacetCollector
parameter_list|(
name|String
name|facetName
parameter_list|,
name|String
name|keyFieldName
parameter_list|,
name|String
name|valueFieldName
parameter_list|,
name|int
name|size
parameter_list|,
name|TermsStatsFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|String
name|scriptLang
parameter_list|,
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|facetName
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldDataCache
operator|=
name|context
operator|.
name|fieldDataCache
argument_list|()
expr_stmt|;
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
name|this
operator|.
name|numberOfShards
operator|=
name|context
operator|.
name|numberOfShards
argument_list|()
expr_stmt|;
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartMappers
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|smartName
argument_list|(
name|keyFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartMappers
operator|==
literal|null
operator|||
operator|!
name|smartMappers
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
name|this
operator|.
name|keyFieldName
operator|=
name|keyFieldName
expr_stmt|;
name|this
operator|.
name|keyFieldDataType
operator|=
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|STRING
expr_stmt|;
block|}
else|else
block|{
comment|// add type filter if there is exact doc mapper associated with it
if|if
condition|(
name|smartMappers
operator|.
name|hasDocMapper
argument_list|()
condition|)
block|{
name|setFilter
argument_list|(
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|smartMappers
operator|.
name|docMapper
argument_list|()
operator|.
name|typeFilter
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|keyFieldName
operator|=
name|smartMappers
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyFieldDataType
operator|=
name|smartMappers
operator|.
name|mapper
argument_list|()
operator|.
name|fieldDataType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|script
operator|==
literal|null
condition|)
block|{
name|FieldMapper
name|fieldMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|valueFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"failed to find mappings for ["
operator|+
name|valueFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|valueFieldName
operator|=
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
name|this
operator|.
name|valueFieldDataType
operator|=
name|fieldMapper
operator|.
name|fieldDataType
argument_list|()
expr_stmt|;
name|this
operator|.
name|script
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|valueFieldName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|valueFieldDataType
operator|=
literal|null
expr_stmt|;
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
name|params
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|entries
operator|=
name|popFacets
argument_list|()
expr_stmt|;
block|}
DECL|method|setScorer
annotation|@
name|Override
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
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|script
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
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
name|keyFieldData
operator|=
name|fieldDataCache
operator|.
name|cache
argument_list|(
name|keyFieldDataType
argument_list|,
name|reader
argument_list|,
name|keyFieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|valueFieldName
operator|!=
literal|null
condition|)
block|{
name|valueFieldData
operator|=
operator|(
name|NumericFieldData
operator|)
name|fieldDataCache
operator|.
name|cache
argument_list|(
name|valueFieldDataType
argument_list|,
name|reader
argument_list|,
name|valueFieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|script
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|keyFieldData
operator|.
name|hasValue
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|missing
operator|++
expr_stmt|;
return|return;
block|}
name|String
name|key
init|=
name|keyFieldData
operator|.
name|stringValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
name|stringEntry
init|=
name|entries
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|stringEntry
operator|==
literal|null
condition|)
block|{
name|stringEntry
operator|=
operator|new
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|(
name|key
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|stringEntry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stringEntry
operator|.
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|script
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|valueFieldData
operator|.
name|multiValued
argument_list|()
condition|)
block|{
for|for
control|(
name|double
name|value
range|:
name|valueFieldData
operator|.
name|doubleValues
argument_list|(
name|doc
argument_list|)
control|)
block|{
name|stringEntry
operator|.
name|total
operator|+=
name|value
expr_stmt|;
block|}
block|}
else|else
block|{
name|double
name|value
init|=
name|valueFieldData
operator|.
name|doubleValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|stringEntry
operator|.
name|total
operator|+=
name|value
expr_stmt|;
block|}
block|}
else|else
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
name|stringEntry
operator|.
name|total
operator|+=
name|value
expr_stmt|;
block|}
block|}
DECL|method|facet
annotation|@
name|Override
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|InternalTermsStatsStringFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|ImmutableList
operator|.
expr|<
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
operator|>
name|of
argument_list|()
argument_list|,
name|missing
argument_list|)
return|;
block|}
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
comment|// all terms
comment|// all terms, just return the collection, we will sort it on the way back
return|return
operator|new
name|InternalTermsStatsStringFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
literal|0
comment|/* indicates all terms*/
argument_list|,
name|entries
operator|.
name|values
argument_list|()
argument_list|,
name|missing
argument_list|)
return|;
block|}
comment|// we need to fetch facets of "size * numberOfShards" because of problems in how they are distributed across shards
name|Object
index|[]
name|values
init|=
name|entries
operator|.
name|internalValues
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|,
operator|(
name|Comparator
operator|)
name|comparatorType
operator|.
name|comparator
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
name|ordered
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|size
operator|*
name|numberOfShards
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
name|value
init|=
operator|(
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|ordered
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|// that's fine to push here, this thread will be released AFTER the entries have either been serialized
comment|// or processed
name|pushFacets
argument_list|(
name|entries
argument_list|)
expr_stmt|;
return|return
operator|new
name|InternalTermsStatsStringFacet
argument_list|(
name|facetName
argument_list|,
name|comparatorType
argument_list|,
name|size
argument_list|,
name|ordered
argument_list|,
name|missing
argument_list|)
return|;
block|}
DECL|method|popFacets
specifier|static
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
name|popFacets
parameter_list|()
block|{
name|Deque
argument_list|<
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|>
name|deque
init|=
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|deque
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deque
operator|.
name|add
argument_list|(
operator|new
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
name|facets
init|=
name|deque
operator|.
name|pollFirst
argument_list|()
decl_stmt|;
name|facets
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|facets
return|;
block|}
DECL|method|pushFacets
specifier|static
name|void
name|pushFacets
parameter_list|(
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
name|facets
parameter_list|)
block|{
name|facets
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Deque
argument_list|<
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|>
name|deque
init|=
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|deque
operator|!=
literal|null
condition|)
block|{
name|deque
operator|.
name|add
argument_list|(
name|facets
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|cache
specifier|static
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Deque
argument_list|<
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Deque
argument_list|<
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Deque
argument_list|<
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|>
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|Deque
argument_list|<
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|>
argument_list|>
argument_list|(
operator|new
name|ArrayDeque
argument_list|<
name|ExtTHashMap
argument_list|<
name|String
argument_list|,
name|InternalTermsStatsStringFacet
operator|.
name|StringEntry
argument_list|>
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

