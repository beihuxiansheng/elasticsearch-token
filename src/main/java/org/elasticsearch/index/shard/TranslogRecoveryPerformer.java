begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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
name|search
operator|.
name|Query
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
name|join
operator|.
name|BitDocIdSetFilter
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|aliases
operator|.
name|IndexAliasesService
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
name|IndexCache
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
name|engine
operator|.
name|IgnoreOnRecoveryEngineException
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
name|DocumentMapper
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
name|MapperAnalyzer
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
name|index
operator|.
name|mapper
operator|.
name|MapperUtils
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
name|Mapping
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
name|Uid
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
name|query
operator|.
name|IndexQueryParserService
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
name|translog
operator|.
name|Translog
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
comment|/**  * The TranslogRecoveryPerformer encapsulates all the logic needed to transform a translog entry into an  * indexing operation including source parsing and field creation from the source.  */
end_comment

begin_class
DECL|class|TranslogRecoveryPerformer
specifier|public
class|class
name|TranslogRecoveryPerformer
block|{
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|queryParserService
specifier|private
specifier|final
name|IndexQueryParserService
name|queryParserService
decl_stmt|;
DECL|field|indexAliasesService
specifier|private
specifier|final
name|IndexAliasesService
name|indexAliasesService
decl_stmt|;
DECL|field|indexCache
specifier|private
specifier|final
name|IndexCache
name|indexCache
decl_stmt|;
DECL|field|mapperAnalyzer
specifier|private
specifier|final
name|MapperAnalyzer
name|mapperAnalyzer
decl_stmt|;
DECL|field|recoveredTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Mapping
argument_list|>
name|recoveredTypes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|TranslogRecoveryPerformer
specifier|protected
name|TranslogRecoveryPerformer
parameter_list|(
name|MapperService
name|mapperService
parameter_list|,
name|MapperAnalyzer
name|mapperAnalyzer
parameter_list|,
name|IndexQueryParserService
name|queryParserService
parameter_list|,
name|IndexAliasesService
name|indexAliasesService
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|)
block|{
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|queryParserService
operator|=
name|queryParserService
expr_stmt|;
name|this
operator|.
name|indexAliasesService
operator|=
name|indexAliasesService
expr_stmt|;
name|this
operator|.
name|indexCache
operator|=
name|indexCache
expr_stmt|;
name|this
operator|.
name|mapperAnalyzer
operator|=
name|mapperAnalyzer
expr_stmt|;
block|}
DECL|method|docMapper
specifier|protected
name|Tuple
argument_list|<
name|DocumentMapper
argument_list|,
name|Mapping
argument_list|>
name|docMapper
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|mapperService
operator|.
name|documentMapperWithAutoCreate
argument_list|(
name|type
argument_list|)
return|;
comment|// protected for testing
block|}
comment|/*      * Applies all operations in the iterable to the current engine and returns the number of operations applied.      * This operation will stop applying operations once an opertion failed to apply.      */
DECL|method|performBatchRecovery
name|int
name|performBatchRecovery
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Iterable
argument_list|<
name|Translog
operator|.
name|Operation
argument_list|>
name|operations
parameter_list|)
block|{
name|int
name|numOps
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Translog
operator|.
name|Operation
name|operation
range|:
name|operations
control|)
block|{
name|performRecoveryOperation
argument_list|(
name|engine
argument_list|,
name|operation
argument_list|)
expr_stmt|;
name|numOps
operator|++
expr_stmt|;
block|}
return|return
name|numOps
return|;
block|}
DECL|method|addMappingUpdate
specifier|private
name|void
name|addMappingUpdate
parameter_list|(
name|String
name|type
parameter_list|,
name|Mapping
name|update
parameter_list|)
block|{
name|Mapping
name|currentUpdate
init|=
name|recoveredTypes
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentUpdate
operator|==
literal|null
condition|)
block|{
name|recoveredTypes
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MapperUtils
operator|.
name|merge
argument_list|(
name|currentUpdate
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Performs a single recovery operation, and returns the indexing operation (or null if its not an indexing operation)      * that can then be used for mapping updates (for example) if needed.      */
DECL|method|performRecoveryOperation
specifier|public
name|void
name|performRecoveryOperation
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Translog
operator|.
name|Operation
name|operation
parameter_list|)
block|{
try|try
block|{
switch|switch
condition|(
name|operation
operator|.
name|opType
argument_list|()
condition|)
block|{
case|case
name|CREATE
case|:
name|Translog
operator|.
name|Create
name|create
init|=
operator|(
name|Translog
operator|.
name|Create
operator|)
name|operation
decl_stmt|;
name|Engine
operator|.
name|Create
name|engineCreate
init|=
name|IndexShard
operator|.
name|prepareCreate
argument_list|(
name|docMapper
argument_list|(
name|create
operator|.
name|type
argument_list|()
argument_list|)
argument_list|,
name|source
argument_list|(
name|create
operator|.
name|source
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|create
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|create
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|routing
argument_list|(
name|create
operator|.
name|routing
argument_list|()
argument_list|)
operator|.
name|parent
argument_list|(
name|create
operator|.
name|parent
argument_list|()
argument_list|)
operator|.
name|timestamp
argument_list|(
name|create
operator|.
name|timestamp
argument_list|()
argument_list|)
operator|.
name|ttl
argument_list|(
name|create
operator|.
name|ttl
argument_list|()
argument_list|)
argument_list|,
name|create
operator|.
name|version
argument_list|()
argument_list|,
name|create
operator|.
name|versionType
argument_list|()
operator|.
name|versionTypeForReplicationAndRecovery
argument_list|()
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|RECOVERY
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|mapperAnalyzer
operator|.
name|setType
argument_list|(
name|create
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
comment|// this is a PITA - once mappings are per index not per type this can go away an we can just simply move this to the engine eventually :)
name|engine
operator|.
name|create
argument_list|(
name|engineCreate
argument_list|)
expr_stmt|;
if|if
condition|(
name|engineCreate
operator|.
name|parsedDoc
argument_list|()
operator|.
name|dynamicMappingsUpdate
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addMappingUpdate
argument_list|(
name|engineCreate
operator|.
name|type
argument_list|()
argument_list|,
name|engineCreate
operator|.
name|parsedDoc
argument_list|()
operator|.
name|dynamicMappingsUpdate
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|SAVE
case|:
name|Translog
operator|.
name|Index
name|index
init|=
operator|(
name|Translog
operator|.
name|Index
operator|)
name|operation
decl_stmt|;
name|Engine
operator|.
name|Index
name|engineIndex
init|=
name|IndexShard
operator|.
name|prepareIndex
argument_list|(
name|docMapper
argument_list|(
name|index
operator|.
name|type
argument_list|()
argument_list|)
argument_list|,
name|source
argument_list|(
name|index
operator|.
name|source
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|index
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|index
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|routing
argument_list|(
name|index
operator|.
name|routing
argument_list|()
argument_list|)
operator|.
name|parent
argument_list|(
name|index
operator|.
name|parent
argument_list|()
argument_list|)
operator|.
name|timestamp
argument_list|(
name|index
operator|.
name|timestamp
argument_list|()
argument_list|)
operator|.
name|ttl
argument_list|(
name|index
operator|.
name|ttl
argument_list|()
argument_list|)
argument_list|,
name|index
operator|.
name|version
argument_list|()
argument_list|,
name|index
operator|.
name|versionType
argument_list|()
operator|.
name|versionTypeForReplicationAndRecovery
argument_list|()
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|RECOVERY
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mapperAnalyzer
operator|.
name|setType
argument_list|(
name|index
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|engine
operator|.
name|index
argument_list|(
name|engineIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|engineIndex
operator|.
name|parsedDoc
argument_list|()
operator|.
name|dynamicMappingsUpdate
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addMappingUpdate
argument_list|(
name|engineIndex
operator|.
name|type
argument_list|()
argument_list|,
name|engineIndex
operator|.
name|parsedDoc
argument_list|()
operator|.
name|dynamicMappingsUpdate
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|DELETE
case|:
name|Translog
operator|.
name|Delete
name|delete
init|=
operator|(
name|Translog
operator|.
name|Delete
operator|)
name|operation
decl_stmt|;
name|Uid
name|uid
init|=
name|Uid
operator|.
name|createUid
argument_list|(
name|delete
operator|.
name|uid
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|engine
operator|.
name|delete
argument_list|(
operator|new
name|Engine
operator|.
name|Delete
argument_list|(
name|uid
operator|.
name|type
argument_list|()
argument_list|,
name|uid
operator|.
name|id
argument_list|()
argument_list|,
name|delete
operator|.
name|uid
argument_list|()
argument_list|,
name|delete
operator|.
name|version
argument_list|()
argument_list|,
name|delete
operator|.
name|versionType
argument_list|()
operator|.
name|versionTypeForReplicationAndRecovery
argument_list|()
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|RECOVERY
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE_BY_QUERY
case|:
name|Translog
operator|.
name|DeleteByQuery
name|deleteByQuery
init|=
operator|(
name|Translog
operator|.
name|DeleteByQuery
operator|)
name|operation
decl_stmt|;
name|engine
operator|.
name|delete
argument_list|(
name|prepareDeleteByQuery
argument_list|(
name|queryParserService
argument_list|,
name|mapperService
argument_list|,
name|indexAliasesService
argument_list|,
name|indexCache
argument_list|,
name|deleteByQuery
operator|.
name|source
argument_list|()
argument_list|,
name|deleteByQuery
operator|.
name|filteringAliases
argument_list|()
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|RECOVERY
argument_list|,
name|deleteByQuery
operator|.
name|types
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No operation defined for ["
operator|+
name|operation
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ElasticsearchException
name|e
parameter_list|)
block|{
name|boolean
name|hasIgnoreOnRecoveryException
init|=
literal|false
decl_stmt|;
name|ElasticsearchException
name|current
init|=
name|e
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|instanceof
name|IgnoreOnRecoveryEngineException
condition|)
block|{
name|hasIgnoreOnRecoveryException
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|current
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ElasticsearchException
condition|)
block|{
name|current
operator|=
operator|(
name|ElasticsearchException
operator|)
name|current
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|hasIgnoreOnRecoveryException
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|operationProcessed
argument_list|()
expr_stmt|;
block|}
DECL|method|prepareDeleteByQuery
specifier|private
specifier|static
name|Engine
operator|.
name|DeleteByQuery
name|prepareDeleteByQuery
parameter_list|(
name|IndexQueryParserService
name|queryParserService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|IndexAliasesService
name|indexAliasesService
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|,
name|BytesReference
name|source
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|filteringAliases
parameter_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
name|origin
parameter_list|,
name|String
modifier|...
name|types
parameter_list|)
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
name|types
operator|=
name|Strings
operator|.
name|EMPTY_ARRAY
expr_stmt|;
block|}
name|Query
name|query
init|=
name|queryParserService
operator|.
name|parseQuery
argument_list|(
name|source
argument_list|)
operator|.
name|query
argument_list|()
decl_stmt|;
name|Query
name|searchFilter
init|=
name|mapperService
operator|.
name|searchFilter
argument_list|(
name|types
argument_list|)
decl_stmt|;
if|if
condition|(
name|searchFilter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
name|Queries
operator|.
name|filtered
argument_list|(
name|query
argument_list|,
name|searchFilter
argument_list|)
expr_stmt|;
block|}
name|Query
name|aliasFilter
init|=
name|indexAliasesService
operator|.
name|aliasFilter
argument_list|(
name|filteringAliases
argument_list|)
decl_stmt|;
name|BitDocIdSetFilter
name|parentFilter
init|=
name|mapperService
operator|.
name|hasNested
argument_list|()
condition|?
name|indexCache
operator|.
name|bitsetFilterCache
argument_list|()
operator|.
name|getBitDocIdSetFilter
argument_list|(
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|Engine
operator|.
name|DeleteByQuery
argument_list|(
name|query
argument_list|,
name|source
argument_list|,
name|filteringAliases
argument_list|,
name|aliasFilter
argument_list|,
name|parentFilter
argument_list|,
name|origin
argument_list|,
name|startTime
argument_list|,
name|types
argument_list|)
return|;
block|}
comment|/**      * Called once for every processed operation by this recovery performer.      * This can be used to get progress information on the translog execution.      */
DECL|method|operationProcessed
specifier|protected
name|void
name|operationProcessed
parameter_list|()
block|{
comment|// noop
block|}
comment|/**      * Returns the recovered types modifying the mapping during the recovery      */
DECL|method|getRecoveredTypes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Mapping
argument_list|>
name|getRecoveredTypes
parameter_list|()
block|{
return|return
name|recoveredTypes
return|;
block|}
block|}
end_class

end_unit

