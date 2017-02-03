begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.sampler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|sampler
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
name|LeafReaderContext
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
name|NumericDocValues
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
name|DiversifiedTopDocsCollector
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
name|DiversifiedTopDocsCollector
operator|.
name|ScoreDocKey
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
name|TopDocsCollector
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
name|util
operator|.
name|BytesRef
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
name|common
operator|.
name|lease
operator|.
name|Releasables
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
name|util
operator|.
name|BytesRefHash
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
name|fielddata
operator|.
name|SortedBinaryDocValues
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
name|aggregations
operator|.
name|Aggregator
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
name|aggregations
operator|.
name|AggregatorFactories
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
name|aggregations
operator|.
name|bucket
operator|.
name|BestDocsDeferringCollector
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
name|aggregations
operator|.
name|bucket
operator|.
name|DeferringBucketCollector
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
name|aggregations
operator|.
name|pipeline
operator|.
name|PipelineAggregator
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
name|aggregations
operator|.
name|support
operator|.
name|ValuesSource
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
name|List
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

begin_class
DECL|class|DiversifiedMapSamplerAggregator
specifier|public
class|class
name|DiversifiedMapSamplerAggregator
extends|extends
name|SamplerAggregator
block|{
DECL|field|valuesSource
specifier|private
name|ValuesSource
name|valuesSource
decl_stmt|;
DECL|field|maxDocsPerValue
specifier|private
name|int
name|maxDocsPerValue
decl_stmt|;
DECL|field|bucketOrds
specifier|private
name|BytesRefHash
name|bucketOrds
decl_stmt|;
DECL|method|DiversifiedMapSamplerAggregator
specifier|public
name|DiversifiedMapSamplerAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|shardSize
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|,
name|ValuesSource
name|valuesSource
parameter_list|,
name|int
name|maxDocsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|shardSize
argument_list|,
name|factories
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|valuesSource
operator|=
name|valuesSource
expr_stmt|;
name|this
operator|.
name|maxDocsPerValue
operator|=
name|maxDocsPerValue
expr_stmt|;
name|bucketOrds
operator|=
operator|new
name|BytesRefHash
argument_list|(
name|shardSize
argument_list|,
name|context
operator|.
name|bigArrays
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|bucketOrds
argument_list|)
expr_stmt|;
name|super
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDeferringCollector
specifier|public
name|DeferringBucketCollector
name|getDeferringCollector
parameter_list|()
block|{
name|bdd
operator|=
operator|new
name|DiverseDocsDeferringCollector
argument_list|()
expr_stmt|;
return|return
name|bdd
return|;
block|}
comment|/**      * A {@link DeferringBucketCollector} that identifies top scoring documents      * but de-duped by a key then passes only these on to nested collectors.      * This implementation is only for use with a single bucket aggregation.      */
DECL|class|DiverseDocsDeferringCollector
class|class
name|DiverseDocsDeferringCollector
extends|extends
name|BestDocsDeferringCollector
block|{
DECL|method|DiverseDocsDeferringCollector
name|DiverseDocsDeferringCollector
parameter_list|()
block|{
name|super
argument_list|(
name|shardSize
argument_list|,
name|context
operator|.
name|bigArrays
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createTopDocsCollector
specifier|protected
name|TopDocsCollector
argument_list|<
name|ScoreDocKey
argument_list|>
name|createTopDocsCollector
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|ValuesDiversifiedTopDocsCollector
argument_list|(
name|size
argument_list|,
name|maxDocsPerValue
argument_list|)
return|;
block|}
comment|// This class extends the DiversifiedTopDocsCollector and provides
comment|// a lookup from elasticsearch's ValuesSource
DECL|class|ValuesDiversifiedTopDocsCollector
class|class
name|ValuesDiversifiedTopDocsCollector
extends|extends
name|DiversifiedTopDocsCollector
block|{
DECL|field|values
specifier|private
name|SortedBinaryDocValues
name|values
decl_stmt|;
DECL|method|ValuesDiversifiedTopDocsCollector
name|ValuesDiversifiedTopDocsCollector
parameter_list|(
name|int
name|numHits
parameter_list|,
name|int
name|maxHitsPerKey
parameter_list|)
block|{
name|super
argument_list|(
name|numHits
argument_list|,
name|maxHitsPerKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeys
specifier|protected
name|NumericDocValues
name|getKeys
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
try|try
block|{
name|values
operator|=
name|valuesSource
operator|.
name|bytesValues
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Error reading values"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|valuesCount
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
if|if
condition|(
name|valuesCount
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Sample diversifying key must be a single valued-field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|valuesCount
operator|==
literal|1
condition|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|values
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|bucketOrdinal
init|=
name|bucketOrds
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketOrdinal
operator|<
literal|0
condition|)
block|{
comment|// already seen
name|bucketOrdinal
operator|=
operator|-
literal|1
operator|-
name|bucketOrdinal
expr_stmt|;
block|}
return|return
name|bucketOrdinal
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

