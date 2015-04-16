begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.termvectors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|termvectors
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
name|Analyzer
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
name|*
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
name|memory
operator|.
name|MemoryIndex
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
name|action
operator|.
name|termvectors
operator|.
name|TermVectorsFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
operator|.
name|TermVectorsRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
operator|.
name|TermVectorsResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
operator|.
name|dfs
operator|.
name|DfsOnlyRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
operator|.
name|dfs
operator|.
name|DfsOnlyResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
operator|.
name|dfs
operator|.
name|TransportDfsOnlyAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|action
operator|.
name|index
operator|.
name|MappingUpdatedAction
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
name|Nullable
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
name|Strings
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
name|bytes
operator|.
name|BytesReference
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
name|Tuple
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
name|lucene
operator|.
name|uid
operator|.
name|Versions
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
name|index
operator|.
name|engine
operator|.
name|Engine
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
name|get
operator|.
name|GetField
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
name|get
operator|.
name|GetResult
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
name|*
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
name|core
operator|.
name|StringFieldMapper
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
name|internal
operator|.
name|UidFieldMapper
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
name|IndexService
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|AbstractIndexShardComponent
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
name|shard
operator|.
name|ShardId
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
name|shard
operator|.
name|IndexShard
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
name|dfs
operator|.
name|AggregatedDfs
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|SourceToParse
operator|.
name|source
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardTermVectorsService
specifier|public
class|class
name|ShardTermVectorsService
extends|extends
name|AbstractIndexShardComponent
block|{
DECL|field|indexShard
specifier|private
name|IndexShard
name|indexShard
decl_stmt|;
DECL|field|mappingUpdatedAction
specifier|private
specifier|final
name|MappingUpdatedAction
name|mappingUpdatedAction
decl_stmt|;
DECL|field|dfsAction
specifier|private
specifier|final
name|TransportDfsOnlyAction
name|dfsAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|ShardTermVectorsService
specifier|public
name|ShardTermVectorsService
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|MappingUpdatedAction
name|mappingUpdatedAction
parameter_list|,
name|TransportDfsOnlyAction
name|dfsAction
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|mappingUpdatedAction
operator|=
name|mappingUpdatedAction
expr_stmt|;
name|this
operator|.
name|dfsAction
operator|=
name|dfsAction
expr_stmt|;
block|}
comment|// sadly, to overcome cyclic dep, we need to do this and inject it ourselves...
DECL|method|setIndexShard
specifier|public
name|ShardTermVectorsService
name|setIndexShard
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|this
operator|.
name|indexShard
operator|=
name|indexShard
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getTermVectors
specifier|public
name|TermVectorsResponse
name|getTermVectors
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|,
name|String
name|concreteIndex
parameter_list|)
block|{
specifier|final
name|TermVectorsResponse
name|termVectorsResponse
init|=
operator|new
name|TermVectorsResponse
argument_list|(
name|concreteIndex
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Term
name|uidTerm
init|=
operator|new
name|Term
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUidAsBytes
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|GetResult
name|get
init|=
name|indexShard
operator|.
name|get
argument_list|(
operator|new
name|Engine
operator|.
name|Get
argument_list|(
name|request
operator|.
name|realtime
argument_list|()
argument_list|,
name|uidTerm
argument_list|)
operator|.
name|version
argument_list|(
name|request
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|versionType
argument_list|(
name|request
operator|.
name|versionType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Fields
name|termVectorsByField
init|=
literal|null
decl_stmt|;
name|boolean
name|docFromTranslog
init|=
name|get
operator|.
name|source
argument_list|()
operator|!=
literal|null
decl_stmt|;
name|AggregatedDfs
name|dfs
init|=
literal|null
decl_stmt|;
name|TermVectorsFilter
name|termVectorsFilter
init|=
literal|null
decl_stmt|;
comment|/* fetched from translog is treated as an artificial document */
if|if
condition|(
name|docFromTranslog
condition|)
block|{
name|request
operator|.
name|doc
argument_list|(
name|get
operator|.
name|source
argument_list|()
operator|.
name|source
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|termVectorsResponse
operator|.
name|setDocVersion
argument_list|(
name|get
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* handle potential wildcards in fields */
if|if
condition|(
name|request
operator|.
name|selectedFields
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|handleFieldWildcards
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Engine
operator|.
name|Searcher
name|searcher
init|=
name|indexShard
operator|.
name|acquireSearcher
argument_list|(
literal|"term_vector"
argument_list|)
decl_stmt|;
try|try
block|{
name|Fields
name|topLevelFields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|get
operator|.
name|searcher
argument_list|()
operator|!=
literal|null
condition|?
name|get
operator|.
name|searcher
argument_list|()
operator|.
name|reader
argument_list|()
else|:
name|searcher
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
name|Versions
operator|.
name|DocIdAndVersion
name|docIdAndVersion
init|=
name|get
operator|.
name|docIdAndVersion
argument_list|()
decl_stmt|;
comment|/* from an artificial document */
if|if
condition|(
name|request
operator|.
name|doc
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|termVectorsByField
operator|=
name|generateTermVectorsFromDoc
argument_list|(
name|request
argument_list|,
operator|!
name|docFromTranslog
argument_list|)
expr_stmt|;
comment|// if no document indexed in shard, take the queried document itself for stats
if|if
condition|(
name|topLevelFields
operator|==
literal|null
condition|)
block|{
name|topLevelFields
operator|=
name|termVectorsByField
expr_stmt|;
block|}
name|termVectorsResponse
operator|.
name|setArtificial
argument_list|(
operator|!
name|docFromTranslog
argument_list|)
expr_stmt|;
name|termVectorsResponse
operator|.
name|setExists
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/* or from an existing document */
elseif|else
if|if
condition|(
name|docIdAndVersion
operator|!=
literal|null
condition|)
block|{
comment|// fields with stored term vectors
name|termVectorsByField
operator|=
name|docIdAndVersion
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getTermVectors
argument_list|(
name|docIdAndVersion
operator|.
name|docId
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|selectedFields
init|=
name|request
operator|.
name|selectedFields
argument_list|()
decl_stmt|;
comment|// generate tvs for fields where analyzer is overridden
if|if
condition|(
name|selectedFields
operator|==
literal|null
operator|&&
name|request
operator|.
name|perFieldAnalyzer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|selectedFields
operator|=
name|getFieldsToGenerate
argument_list|(
name|request
operator|.
name|perFieldAnalyzer
argument_list|()
argument_list|,
name|termVectorsByField
argument_list|)
expr_stmt|;
block|}
comment|// fields without term vectors
if|if
condition|(
name|selectedFields
operator|!=
literal|null
condition|)
block|{
name|termVectorsByField
operator|=
name|addGeneratedTermVectors
argument_list|(
name|get
argument_list|,
name|termVectorsByField
argument_list|,
name|request
argument_list|,
name|selectedFields
argument_list|)
expr_stmt|;
block|}
name|termVectorsResponse
operator|.
name|setDocVersion
argument_list|(
name|docIdAndVersion
operator|.
name|version
argument_list|)
expr_stmt|;
name|termVectorsResponse
operator|.
name|setExists
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/* no term vectors generated or found */
else|else
block|{
name|termVectorsResponse
operator|.
name|setExists
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/* if there are term vectors, optional compute dfs and/or terms filtering */
if|if
condition|(
name|termVectorsByField
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|useDfs
argument_list|(
name|request
argument_list|)
condition|)
block|{
name|dfs
operator|=
name|getAggregatedDfs
argument_list|(
name|termVectorsByField
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|filterSettings
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|termVectorsFilter
operator|=
operator|new
name|TermVectorsFilter
argument_list|(
name|termVectorsByField
argument_list|,
name|topLevelFields
argument_list|,
name|request
operator|.
name|selectedFields
argument_list|()
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
name|termVectorsFilter
operator|.
name|setSettings
argument_list|(
name|request
operator|.
name|filterSettings
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|termVectorsFilter
operator|.
name|selectBestTerms
argument_list|()
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
literal|"failed to select best terms"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// write term vectors
name|termVectorsResponse
operator|.
name|setFields
argument_list|(
name|termVectorsByField
argument_list|,
name|request
operator|.
name|selectedFields
argument_list|()
argument_list|,
name|request
operator|.
name|getFlags
argument_list|()
argument_list|,
name|topLevelFields
argument_list|,
name|dfs
argument_list|,
name|termVectorsFilter
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failed to execute term vector request"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|get
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
return|return
name|termVectorsResponse
return|;
block|}
DECL|method|handleFieldWildcards
specifier|private
name|void
name|handleFieldWildcards
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pattern
range|:
name|request
operator|.
name|selectedFields
argument_list|()
control|)
block|{
name|fieldNames
operator|.
name|addAll
argument_list|(
name|indexShard
operator|.
name|mapperService
argument_list|()
operator|.
name|simpleMatchToIndexNames
argument_list|(
name|pattern
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|selectedFields
argument_list|(
name|fieldNames
operator|.
name|toArray
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isValidField
specifier|private
name|boolean
name|isValidField
parameter_list|(
name|FieldMapper
name|field
parameter_list|)
block|{
comment|// must be a string
if|if
condition|(
operator|!
operator|(
name|field
operator|instanceof
name|StringFieldMapper
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// and must be indexed
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|addGeneratedTermVectors
specifier|private
name|Fields
name|addGeneratedTermVectors
parameter_list|(
name|Engine
operator|.
name|GetResult
name|get
parameter_list|,
name|Fields
name|termVectorsByField
parameter_list|,
name|TermVectorsRequest
name|request
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|selectedFields
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* only keep valid fields */
name|Set
argument_list|<
name|String
argument_list|>
name|validFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|selectedFields
control|)
block|{
name|FieldMapper
name|fieldMapper
init|=
name|indexShard
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isValidField
argument_list|(
name|fieldMapper
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// already retrieved, only if the analyzer hasn't been overridden at the field
if|if
condition|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
operator|&&
operator|(
name|request
operator|.
name|perFieldAnalyzer
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|request
operator|.
name|perFieldAnalyzer
argument_list|()
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
operator|)
condition|)
block|{
continue|continue;
block|}
name|validFields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|validFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|termVectorsByField
return|;
block|}
comment|/* generate term vectors from fetched document fields */
name|GetResult
name|getResult
init|=
name|indexShard
operator|.
name|getService
argument_list|()
operator|.
name|get
argument_list|(
name|get
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|validFields
operator|.
name|toArray
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Fields
name|generatedTermVectors
init|=
name|generateTermVectors
argument_list|(
name|getResult
operator|.
name|getFields
argument_list|()
operator|.
name|values
argument_list|()
argument_list|,
name|request
operator|.
name|offsets
argument_list|()
argument_list|,
name|request
operator|.
name|perFieldAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
comment|/* merge with existing Fields */
if|if
condition|(
name|termVectorsByField
operator|==
literal|null
condition|)
block|{
return|return
name|generatedTermVectors
return|;
block|}
else|else
block|{
return|return
name|mergeFields
argument_list|(
name|termVectorsByField
argument_list|,
name|generatedTermVectors
argument_list|)
return|;
block|}
block|}
DECL|method|getAnalyzerAtField
specifier|private
name|Analyzer
name|getAnalyzerAtField
parameter_list|(
name|String
name|field
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|perFieldAnalyzer
parameter_list|)
block|{
name|MapperService
name|mapperService
init|=
name|indexShard
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
decl_stmt|;
if|if
condition|(
name|perFieldAnalyzer
operator|!=
literal|null
operator|&&
name|perFieldAnalyzer
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|analyzer
operator|=
name|mapperService
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|perFieldAnalyzer
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|analyzer
operator|=
name|mapperService
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
argument_list|)
operator|.
name|indexAnalyzer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|mapperService
operator|.
name|analysisService
argument_list|()
operator|.
name|defaultIndexAnalyzer
argument_list|()
expr_stmt|;
block|}
return|return
name|analyzer
return|;
block|}
DECL|method|getFieldsToGenerate
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getFieldsToGenerate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|perAnalyzerField
parameter_list|,
name|Fields
name|fieldsObject
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|selectedFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldsObject
control|)
block|{
if|if
condition|(
name|perAnalyzerField
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|selectedFields
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|selectedFields
return|;
block|}
DECL|method|generateTermVectors
specifier|private
name|Fields
name|generateTermVectors
parameter_list|(
name|Collection
argument_list|<
name|GetField
argument_list|>
name|getFields
parameter_list|,
name|boolean
name|withOffsets
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|perFieldAnalyzer
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* store document in memory index */
name|MemoryIndex
name|index
init|=
operator|new
name|MemoryIndex
argument_list|(
name|withOffsets
argument_list|)
decl_stmt|;
for|for
control|(
name|GetField
name|getField
range|:
name|getFields
control|)
block|{
name|String
name|field
init|=
name|getField
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|getAnalyzerAtField
argument_list|(
name|field
argument_list|,
name|perFieldAnalyzer
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|text
range|:
name|getField
operator|.
name|getValues
argument_list|()
control|)
block|{
name|index
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* and read vectors from it */
return|return
name|MultiFields
operator|.
name|getFields
argument_list|(
name|index
operator|.
name|createSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
return|;
block|}
DECL|method|generateTermVectorsFromDoc
specifier|private
name|Fields
name|generateTermVectorsFromDoc
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|,
name|boolean
name|doAllFields
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// parse the document, at the moment we do update the mapping, just like percolate
name|ParsedDocument
name|parsedDocument
init|=
name|parseDocument
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|getIndex
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|doc
argument_list|()
argument_list|)
decl_stmt|;
comment|// select the right fields and generate term vectors
name|ParseContext
operator|.
name|Document
name|doc
init|=
name|parsedDocument
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|seenFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|GetField
argument_list|>
name|getFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
name|FieldMapper
name|fieldMapper
init|=
name|indexShard
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|seenFields
operator|.
name|contains
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|seenFields
operator|.
name|add
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isValidField
argument_list|(
name|fieldMapper
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|request
operator|.
name|selectedFields
argument_list|()
operator|==
literal|null
operator|&&
operator|!
name|doAllFields
operator|&&
operator|!
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|request
operator|.
name|selectedFields
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|request
operator|.
name|selectedFields
argument_list|()
operator|.
name|contains
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|values
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|getFields
operator|.
name|add
argument_list|(
operator|new
name|GetField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|values
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|generateTermVectors
argument_list|(
name|getFields
argument_list|,
name|request
operator|.
name|offsets
argument_list|()
argument_list|,
name|request
operator|.
name|perFieldAnalyzer
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parseDocument
specifier|private
name|ParsedDocument
name|parseDocument
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|BytesReference
name|doc
parameter_list|)
throws|throws
name|Throwable
block|{
name|MapperService
name|mapperService
init|=
name|indexShard
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|IndexService
name|indexService
init|=
name|indexShard
operator|.
name|indexService
argument_list|()
decl_stmt|;
comment|// TODO: make parsing not dynamically create fields not in the original mapping
name|Tuple
argument_list|<
name|DocumentMapper
argument_list|,
name|Mapping
argument_list|>
name|docMapper
init|=
name|mapperService
operator|.
name|documentMapperWithAutoCreate
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|ParsedDocument
name|parsedDocument
init|=
name|docMapper
operator|.
name|v1
argument_list|()
operator|.
name|parse
argument_list|(
name|source
argument_list|(
name|doc
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
operator|.
name|flyweight
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|docMapper
operator|.
name|v2
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|parsedDocument
operator|.
name|addDynamicMappingsUpdate
argument_list|(
name|docMapper
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parsedDocument
operator|.
name|dynamicMappingsUpdate
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|mappingUpdatedAction
operator|.
name|updateMappingOnMasterSynchronously
argument_list|(
name|index
argument_list|,
name|indexService
operator|.
name|indexUUID
argument_list|()
argument_list|,
name|type
argument_list|,
name|parsedDocument
operator|.
name|dynamicMappingsUpdate
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|parsedDocument
return|;
block|}
DECL|method|mergeFields
specifier|private
name|Fields
name|mergeFields
parameter_list|(
name|Fields
name|fields1
parameter_list|,
name|Fields
name|fields2
parameter_list|)
throws|throws
name|IOException
block|{
name|ParallelFields
name|parallelFields
init|=
operator|new
name|ParallelFields
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fields2
control|)
block|{
name|Terms
name|terms
init|=
name|fields2
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|parallelFields
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|fieldName
range|:
name|fields1
control|)
block|{
if|if
condition|(
name|parallelFields
operator|.
name|fields
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Terms
name|terms
init|=
name|fields1
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|parallelFields
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parallelFields
return|;
block|}
comment|// Poached from Lucene ParallelLeafReader
DECL|class|ParallelFields
specifier|private
specifier|static
specifier|final
class|class
name|ParallelFields
extends|extends
name|Fields
block|{
DECL|field|fields
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ParallelFields
name|ParallelFields
parameter_list|()
block|{         }
DECL|method|addField
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Terms
name|terms
parameter_list|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|fields
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|method|useDfs
specifier|private
name|boolean
name|useDfs
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|)
block|{
return|return
name|request
operator|.
name|dfs
argument_list|()
operator|&&
operator|(
name|request
operator|.
name|fieldStatistics
argument_list|()
operator|||
name|request
operator|.
name|termStatistics
argument_list|()
operator|)
return|;
block|}
DECL|method|getAggregatedDfs
specifier|private
name|AggregatedDfs
name|getAggregatedDfs
parameter_list|(
name|Fields
name|termVectorsFields
parameter_list|,
name|TermVectorsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|DfsOnlyRequest
name|dfsOnlyRequest
init|=
operator|new
name|DfsOnlyRequest
argument_list|(
name|termVectorsFields
argument_list|,
operator|new
name|String
index|[]
block|{
name|request
operator|.
name|index
argument_list|()
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|request
operator|.
name|type
argument_list|()
block|}
argument_list|,
name|request
operator|.
name|selectedFields
argument_list|()
argument_list|)
decl_stmt|;
name|DfsOnlyResponse
name|response
init|=
name|dfsAction
operator|.
name|execute
argument_list|(
name|dfsOnlyRequest
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
return|return
name|response
operator|.
name|getDfs
argument_list|()
return|;
block|}
block|}
end_class

end_unit

