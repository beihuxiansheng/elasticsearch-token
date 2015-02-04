begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
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
name|index
operator|.
name|Term
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
name|Filter
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
name|IndexSearcher
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
name|lease
operator|.
name|Releasable
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
name|logging
operator|.
name|ESLogger
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
operator|.
name|TimeValue
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
name|VersionType
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
name|deletionpolicy
operator|.
name|SnapshotIndexCommit
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
name|ParseContext
operator|.
name|Document
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
name|ParsedDocument
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
name|translog
operator|.
name|Translog
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_interface
DECL|interface|Engine
specifier|public
interface|interface
name|Engine
extends|extends
name|Closeable
block|{
DECL|method|updateIndexingBufferSize
name|void
name|updateIndexingBufferSize
parameter_list|(
name|ByteSizeValue
name|indexingBufferSize
parameter_list|)
function_decl|;
DECL|method|create
name|void
name|create
parameter_list|(
name|Create
name|create
parameter_list|)
throws|throws
name|EngineException
function_decl|;
DECL|method|index
name|void
name|index
parameter_list|(
name|Index
name|index
parameter_list|)
throws|throws
name|EngineException
function_decl|;
DECL|method|delete
name|void
name|delete
parameter_list|(
name|Delete
name|delete
parameter_list|)
throws|throws
name|EngineException
function_decl|;
DECL|method|delete
name|void
name|delete
parameter_list|(
name|DeleteByQuery
name|delete
parameter_list|)
throws|throws
name|EngineException
function_decl|;
DECL|method|get
name|GetResult
name|get
parameter_list|(
name|Get
name|get
parameter_list|)
throws|throws
name|EngineException
function_decl|;
comment|/**      * Returns a new searcher instance. The consumer of this      * API is responsible for releasing the returned seacher in a      * safe manner, preferably in a try/finally block.      *      * @see Searcher#close()      */
DECL|method|acquireSearcher
name|Searcher
name|acquireSearcher
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|EngineException
function_decl|;
comment|/**      * Global stats on segments.      */
DECL|method|segmentsStats
name|SegmentsStats
name|segmentsStats
parameter_list|()
function_decl|;
comment|/**      * The list of segments in the engine.      */
DECL|method|segments
name|List
argument_list|<
name|Segment
argument_list|>
name|segments
parameter_list|(
name|boolean
name|verbose
parameter_list|)
function_decl|;
comment|/**      * Returns<tt>true</tt> if a refresh is really needed.      */
DECL|method|refreshNeeded
name|boolean
name|refreshNeeded
parameter_list|()
function_decl|;
comment|/**      * Refreshes the engine for new search operations to reflect the latest      * changes.      */
DECL|method|refresh
name|void
name|refresh
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|EngineException
function_decl|;
comment|/**      * Flushes the state of the engine, clearing memory.      */
DECL|method|flush
name|void
name|flush
parameter_list|(
name|FlushType
name|type
parameter_list|,
name|boolean
name|force
parameter_list|,
name|boolean
name|waitIfOngoing
parameter_list|)
throws|throws
name|EngineException
function_decl|;
comment|/**      * Optimizes to 1 segment      */
DECL|method|forceMerge
name|void
name|forceMerge
parameter_list|(
name|boolean
name|flush
parameter_list|,
name|boolean
name|waitForMerge
parameter_list|)
function_decl|;
comment|/**      * Triggers a forced merge on this engine      */
DECL|method|forceMerge
name|void
name|forceMerge
parameter_list|(
name|boolean
name|flush
parameter_list|,
name|boolean
name|waitForMerge
parameter_list|,
name|int
name|maxNumSegments
parameter_list|,
name|boolean
name|onlyExpungeDeletes
parameter_list|,
name|boolean
name|upgrade
parameter_list|)
throws|throws
name|EngineException
function_decl|;
comment|/**      * Snapshots the index and returns a handle to it. Will always try and "commit" the      * lucene index to make sure we have a "fresh" copy of the files to snapshot.      */
DECL|method|snapshotIndex
name|SnapshotIndexCommit
name|snapshotIndex
parameter_list|()
throws|throws
name|EngineException
function_decl|;
DECL|method|recover
name|void
name|recover
parameter_list|(
name|RecoveryHandler
name|recoveryHandler
parameter_list|)
throws|throws
name|EngineException
function_decl|;
comment|/** fail engine due to some error. the engine will also be closed. */
DECL|method|failEngine
name|void
name|failEngine
parameter_list|(
name|String
name|reason
parameter_list|,
name|Throwable
name|failure
parameter_list|)
function_decl|;
DECL|method|indexingBufferSize
name|ByteSizeValue
name|indexingBufferSize
parameter_list|()
function_decl|;
DECL|interface|FailedEngineListener
specifier|static
interface|interface
name|FailedEngineListener
block|{
DECL|method|onFailedEngine
name|void
name|onFailedEngine
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|reason
parameter_list|,
annotation|@
name|Nullable
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
comment|/**      * Recovery allow to start the recovery process. It is built of three phases.      *<p/>      *<p>The first phase allows to take a snapshot of the master index. Once this      * is taken, no commit operations are effectively allowed on the index until the recovery      * phases are through.      *<p/>      *<p>The seconds phase takes a snapshot of the current transaction log.      *<p/>      *<p>The last phase returns the remaining transaction log. During this phase, no dirty      * operations are allowed on the index.      */
DECL|interface|RecoveryHandler
specifier|static
interface|interface
name|RecoveryHandler
block|{
DECL|method|phase1
name|void
name|phase1
parameter_list|(
name|SnapshotIndexCommit
name|snapshot
parameter_list|)
throws|throws
name|ElasticsearchException
function_decl|;
DECL|method|phase2
name|void
name|phase2
parameter_list|(
name|Translog
operator|.
name|Snapshot
name|snapshot
parameter_list|)
throws|throws
name|ElasticsearchException
function_decl|;
DECL|method|phase3
name|void
name|phase3
parameter_list|(
name|Translog
operator|.
name|Snapshot
name|snapshot
parameter_list|)
throws|throws
name|ElasticsearchException
function_decl|;
block|}
DECL|interface|Searcher
specifier|static
interface|interface
name|Searcher
extends|extends
name|Releasable
block|{
comment|/**          * The source that caused this searcher to be acquired.          */
DECL|method|source
name|String
name|source
parameter_list|()
function_decl|;
DECL|method|reader
name|IndexReader
name|reader
parameter_list|()
function_decl|;
DECL|method|searcher
name|IndexSearcher
name|searcher
parameter_list|()
function_decl|;
block|}
DECL|class|SimpleSearcher
specifier|static
class|class
name|SimpleSearcher
implements|implements
name|Searcher
block|{
DECL|field|source
specifier|private
specifier|final
name|String
name|source
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|method|SimpleSearcher
specifier|public
name|SimpleSearcher
parameter_list|(
name|String
name|source
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|source
specifier|public
name|String
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|reader
specifier|public
name|IndexReader
name|reader
parameter_list|()
block|{
return|return
name|searcher
operator|.
name|getIndexReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|searcher
specifier|public
name|IndexSearcher
name|searcher
parameter_list|()
block|{
return|return
name|searcher
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
comment|// nothing to release here...
block|}
block|}
DECL|enum|FlushType
specifier|public
specifier|static
enum|enum
name|FlushType
block|{
comment|/**          * A flush that just commits the writer, without cleaning the translog.          */
DECL|enum constant|COMMIT
name|COMMIT
block|,
comment|/**          * A flush that does a commit, as well as clears the translog.          */
DECL|enum constant|COMMIT_TRANSLOG
name|COMMIT_TRANSLOG
block|}
DECL|interface|Operation
specifier|static
interface|interface
name|Operation
block|{
DECL|enum|Type
specifier|static
enum|enum
name|Type
block|{
DECL|enum constant|CREATE
name|CREATE
block|,
DECL|enum constant|INDEX
name|INDEX
block|,
DECL|enum constant|DELETE
name|DELETE
block|}
DECL|enum|Origin
specifier|static
enum|enum
name|Origin
block|{
DECL|enum constant|PRIMARY
name|PRIMARY
block|,
DECL|enum constant|REPLICA
name|REPLICA
block|,
DECL|enum constant|RECOVERY
name|RECOVERY
block|}
DECL|method|opType
name|Type
name|opType
parameter_list|()
function_decl|;
DECL|method|origin
name|Origin
name|origin
parameter_list|()
function_decl|;
block|}
DECL|class|IndexingOperation
specifier|static
specifier|abstract
class|class
name|IndexingOperation
implements|implements
name|Operation
block|{
DECL|field|docMapper
specifier|private
specifier|final
name|DocumentMapper
name|docMapper
decl_stmt|;
DECL|field|uid
specifier|private
specifier|final
name|Term
name|uid
decl_stmt|;
DECL|field|doc
specifier|private
specifier|final
name|ParsedDocument
name|doc
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
DECL|field|versionType
specifier|private
specifier|final
name|VersionType
name|versionType
decl_stmt|;
DECL|field|origin
specifier|private
specifier|final
name|Origin
name|origin
decl_stmt|;
DECL|field|canHaveDuplicates
specifier|private
specifier|final
name|boolean
name|canHaveDuplicates
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
DECL|method|IndexingOperation
specifier|public
name|IndexingOperation
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|,
name|long
name|version
parameter_list|,
name|VersionType
name|versionType
parameter_list|,
name|Origin
name|origin
parameter_list|,
name|long
name|startTime
parameter_list|,
name|boolean
name|canHaveDuplicates
parameter_list|)
block|{
name|this
operator|.
name|docMapper
operator|=
name|docMapper
expr_stmt|;
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|versionType
operator|=
name|versionType
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|this
operator|.
name|canHaveDuplicates
operator|=
name|canHaveDuplicates
expr_stmt|;
block|}
DECL|method|IndexingOperation
specifier|public
name|IndexingOperation
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|)
block|{
name|this
argument_list|(
name|docMapper
argument_list|,
name|uid
argument_list|,
name|doc
argument_list|,
name|Versions
operator|.
name|MATCH_ANY
argument_list|,
name|VersionType
operator|.
name|INTERNAL
argument_list|,
name|Origin
operator|.
name|PRIMARY
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|docMapper
specifier|public
name|DocumentMapper
name|docMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|docMapper
return|;
block|}
annotation|@
name|Override
DECL|method|origin
specifier|public
name|Origin
name|origin
parameter_list|()
block|{
return|return
name|this
operator|.
name|origin
return|;
block|}
DECL|method|parsedDoc
specifier|public
name|ParsedDocument
name|parsedDoc
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
return|;
block|}
DECL|method|uid
specifier|public
name|Term
name|uid
parameter_list|()
block|{
return|return
name|this
operator|.
name|uid
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|type
argument_list|()
return|;
block|}
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|id
argument_list|()
return|;
block|}
DECL|method|routing
specifier|public
name|String
name|routing
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|routing
argument_list|()
return|;
block|}
DECL|method|timestamp
specifier|public
name|long
name|timestamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|timestamp
argument_list|()
return|;
block|}
DECL|method|ttl
specifier|public
name|long
name|ttl
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|ttl
argument_list|()
return|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|updateVersion
specifier|public
name|void
name|updateVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|doc
operator|.
name|version
argument_list|()
operator|.
name|setLongValue
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
DECL|method|versionType
specifier|public
name|VersionType
name|versionType
parameter_list|()
block|{
return|return
name|this
operator|.
name|versionType
return|;
block|}
DECL|method|canHaveDuplicates
specifier|public
name|boolean
name|canHaveDuplicates
parameter_list|()
block|{
return|return
name|this
operator|.
name|canHaveDuplicates
return|;
block|}
DECL|method|parent
specifier|public
name|String
name|parent
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|parent
argument_list|()
return|;
block|}
DECL|method|docs
specifier|public
name|List
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|docs
argument_list|()
return|;
block|}
DECL|method|source
specifier|public
name|BytesReference
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|doc
operator|.
name|source
argument_list|()
return|;
block|}
comment|/**          * Returns operation start time in nanoseconds.          */
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|endTime
specifier|public
name|void
name|endTime
parameter_list|(
name|long
name|endTime
parameter_list|)
block|{
name|this
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
block|}
comment|/**          * Returns operation end time in nanoseconds.          */
DECL|method|endTime
specifier|public
name|long
name|endTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|endTime
return|;
block|}
block|}
DECL|class|Create
specifier|static
specifier|final
class|class
name|Create
extends|extends
name|IndexingOperation
block|{
DECL|field|autoGeneratedId
specifier|private
specifier|final
name|boolean
name|autoGeneratedId
decl_stmt|;
DECL|method|Create
specifier|public
name|Create
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|,
name|long
name|version
parameter_list|,
name|VersionType
name|versionType
parameter_list|,
name|Origin
name|origin
parameter_list|,
name|long
name|startTime
parameter_list|,
name|boolean
name|canHaveDuplicates
parameter_list|,
name|boolean
name|autoGeneratedId
parameter_list|)
block|{
name|super
argument_list|(
name|docMapper
argument_list|,
name|uid
argument_list|,
name|doc
argument_list|,
name|version
argument_list|,
name|versionType
argument_list|,
name|origin
argument_list|,
name|startTime
argument_list|,
name|canHaveDuplicates
argument_list|)
expr_stmt|;
name|this
operator|.
name|autoGeneratedId
operator|=
name|autoGeneratedId
expr_stmt|;
block|}
DECL|method|Create
specifier|public
name|Create
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|,
name|long
name|version
parameter_list|,
name|VersionType
name|versionType
parameter_list|,
name|Origin
name|origin
parameter_list|,
name|long
name|startTime
parameter_list|)
block|{
name|this
argument_list|(
name|docMapper
argument_list|,
name|uid
argument_list|,
name|doc
argument_list|,
name|version
argument_list|,
name|versionType
argument_list|,
name|origin
argument_list|,
name|startTime
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Create
specifier|public
name|Create
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|)
block|{
name|super
argument_list|(
name|docMapper
argument_list|,
name|uid
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|autoGeneratedId
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|opType
specifier|public
name|Type
name|opType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|CREATE
return|;
block|}
DECL|method|autoGeneratedId
specifier|public
name|boolean
name|autoGeneratedId
parameter_list|()
block|{
return|return
name|this
operator|.
name|autoGeneratedId
return|;
block|}
block|}
DECL|class|Index
specifier|static
specifier|final
class|class
name|Index
extends|extends
name|IndexingOperation
block|{
DECL|field|created
specifier|private
name|boolean
name|created
decl_stmt|;
DECL|method|Index
specifier|public
name|Index
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|,
name|long
name|version
parameter_list|,
name|VersionType
name|versionType
parameter_list|,
name|Origin
name|origin
parameter_list|,
name|long
name|startTime
parameter_list|,
name|boolean
name|canHaveDuplicates
parameter_list|)
block|{
name|super
argument_list|(
name|docMapper
argument_list|,
name|uid
argument_list|,
name|doc
argument_list|,
name|version
argument_list|,
name|versionType
argument_list|,
name|origin
argument_list|,
name|startTime
argument_list|,
name|canHaveDuplicates
argument_list|)
expr_stmt|;
block|}
DECL|method|Index
specifier|public
name|Index
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|,
name|long
name|version
parameter_list|,
name|VersionType
name|versionType
parameter_list|,
name|Origin
name|origin
parameter_list|,
name|long
name|startTime
parameter_list|)
block|{
name|super
argument_list|(
name|docMapper
argument_list|,
name|uid
argument_list|,
name|doc
argument_list|,
name|version
argument_list|,
name|versionType
argument_list|,
name|origin
argument_list|,
name|startTime
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Index
specifier|public
name|Index
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Term
name|uid
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|)
block|{
name|super
argument_list|(
name|docMapper
argument_list|,
name|uid
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|opType
specifier|public
name|Type
name|opType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|INDEX
return|;
block|}
comment|/**          * @return true if object was created          */
DECL|method|created
specifier|public
name|boolean
name|created
parameter_list|()
block|{
return|return
name|created
return|;
block|}
DECL|method|created
specifier|public
name|void
name|created
parameter_list|(
name|boolean
name|created
parameter_list|)
block|{
name|this
operator|.
name|created
operator|=
name|created
expr_stmt|;
block|}
block|}
DECL|class|Delete
specifier|static
class|class
name|Delete
implements|implements
name|Operation
block|{
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|uid
specifier|private
specifier|final
name|Term
name|uid
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
DECL|field|versionType
specifier|private
specifier|final
name|VersionType
name|versionType
decl_stmt|;
DECL|field|origin
specifier|private
specifier|final
name|Origin
name|origin
decl_stmt|;
DECL|field|found
specifier|private
name|boolean
name|found
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
DECL|method|Delete
specifier|public
name|Delete
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|Term
name|uid
parameter_list|,
name|long
name|version
parameter_list|,
name|VersionType
name|versionType
parameter_list|,
name|Origin
name|origin
parameter_list|,
name|long
name|startTime
parameter_list|,
name|boolean
name|found
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|versionType
operator|=
name|versionType
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|this
operator|.
name|found
operator|=
name|found
expr_stmt|;
block|}
DECL|method|Delete
specifier|public
name|Delete
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|Term
name|uid
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|id
argument_list|,
name|uid
argument_list|,
name|Versions
operator|.
name|MATCH_ANY
argument_list|,
name|VersionType
operator|.
name|INTERNAL
argument_list|,
name|Origin
operator|.
name|PRIMARY
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Delete
specifier|public
name|Delete
parameter_list|(
name|Delete
name|template
parameter_list|,
name|VersionType
name|versionType
parameter_list|)
block|{
name|this
argument_list|(
name|template
operator|.
name|type
argument_list|()
argument_list|,
name|template
operator|.
name|id
argument_list|()
argument_list|,
name|template
operator|.
name|uid
argument_list|()
argument_list|,
name|template
operator|.
name|version
argument_list|()
argument_list|,
name|versionType
argument_list|,
name|template
operator|.
name|origin
argument_list|()
argument_list|,
name|template
operator|.
name|startTime
argument_list|()
argument_list|,
name|template
operator|.
name|found
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|opType
specifier|public
name|Type
name|opType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|DELETE
return|;
block|}
annotation|@
name|Override
DECL|method|origin
specifier|public
name|Origin
name|origin
parameter_list|()
block|{
return|return
name|this
operator|.
name|origin
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|uid
specifier|public
name|Term
name|uid
parameter_list|()
block|{
return|return
name|this
operator|.
name|uid
return|;
block|}
DECL|method|updateVersion
specifier|public
name|void
name|updateVersion
parameter_list|(
name|long
name|version
parameter_list|,
name|boolean
name|found
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|found
operator|=
name|found
expr_stmt|;
block|}
comment|/**          * before delete execution this is the version to be deleted. After this is the version of the "delete" transaction record.          */
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|versionType
specifier|public
name|VersionType
name|versionType
parameter_list|()
block|{
return|return
name|this
operator|.
name|versionType
return|;
block|}
DECL|method|found
specifier|public
name|boolean
name|found
parameter_list|()
block|{
return|return
name|this
operator|.
name|found
return|;
block|}
comment|/**          * Returns operation start time in nanoseconds.          */
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|endTime
specifier|public
name|void
name|endTime
parameter_list|(
name|long
name|endTime
parameter_list|)
block|{
name|this
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
block|}
comment|/**          * Returns operation end time in nanoseconds.          */
DECL|method|endTime
specifier|public
name|long
name|endTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|endTime
return|;
block|}
block|}
DECL|class|DeleteByQuery
specifier|static
class|class
name|DeleteByQuery
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|BytesReference
name|source
decl_stmt|;
DECL|field|filteringAliases
specifier|private
specifier|final
name|String
index|[]
name|filteringAliases
decl_stmt|;
DECL|field|aliasFilter
specifier|private
specifier|final
name|Filter
name|aliasFilter
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|String
index|[]
name|types
decl_stmt|;
DECL|field|parentFilter
specifier|private
specifier|final
name|BitDocIdSetFilter
name|parentFilter
decl_stmt|;
DECL|field|origin
specifier|private
specifier|final
name|Operation
operator|.
name|Origin
name|origin
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
DECL|method|DeleteByQuery
specifier|public
name|DeleteByQuery
parameter_list|(
name|Query
name|query
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
annotation|@
name|Nullable
name|Filter
name|aliasFilter
parameter_list|,
name|BitDocIdSetFilter
name|parentFilter
parameter_list|,
name|Operation
operator|.
name|Origin
name|origin
parameter_list|,
name|long
name|startTime
parameter_list|,
name|String
modifier|...
name|types
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
name|this
operator|.
name|filteringAliases
operator|=
name|filteringAliases
expr_stmt|;
name|this
operator|.
name|aliasFilter
operator|=
name|aliasFilter
expr_stmt|;
name|this
operator|.
name|parentFilter
operator|=
name|parentFilter
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
block|}
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|this
operator|.
name|query
return|;
block|}
DECL|method|source
specifier|public
name|BytesReference
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
return|;
block|}
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|this
operator|.
name|types
return|;
block|}
DECL|method|filteringAliases
specifier|public
name|String
index|[]
name|filteringAliases
parameter_list|()
block|{
return|return
name|filteringAliases
return|;
block|}
DECL|method|aliasFilter
specifier|public
name|Filter
name|aliasFilter
parameter_list|()
block|{
return|return
name|aliasFilter
return|;
block|}
DECL|method|nested
specifier|public
name|boolean
name|nested
parameter_list|()
block|{
return|return
name|parentFilter
operator|!=
literal|null
return|;
block|}
DECL|method|parentFilter
specifier|public
name|BitDocIdSetFilter
name|parentFilter
parameter_list|()
block|{
return|return
name|parentFilter
return|;
block|}
DECL|method|origin
specifier|public
name|Operation
operator|.
name|Origin
name|origin
parameter_list|()
block|{
return|return
name|this
operator|.
name|origin
return|;
block|}
comment|/**          * Returns operation start time in nanoseconds.          */
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|endTime
specifier|public
name|DeleteByQuery
name|endTime
parameter_list|(
name|long
name|endTime
parameter_list|)
block|{
name|this
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Returns operation end time in nanoseconds.          */
DECL|method|endTime
specifier|public
name|long
name|endTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|endTime
return|;
block|}
block|}
DECL|class|Get
specifier|static
class|class
name|Get
block|{
DECL|field|realtime
specifier|private
specifier|final
name|boolean
name|realtime
decl_stmt|;
DECL|field|uid
specifier|private
specifier|final
name|Term
name|uid
decl_stmt|;
DECL|field|loadSource
specifier|private
name|boolean
name|loadSource
init|=
literal|true
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
init|=
name|Versions
operator|.
name|MATCH_ANY
decl_stmt|;
DECL|field|versionType
specifier|private
name|VersionType
name|versionType
init|=
name|VersionType
operator|.
name|INTERNAL
decl_stmt|;
DECL|method|Get
specifier|public
name|Get
parameter_list|(
name|boolean
name|realtime
parameter_list|,
name|Term
name|uid
parameter_list|)
block|{
name|this
operator|.
name|realtime
operator|=
name|realtime
expr_stmt|;
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
block|}
DECL|method|realtime
specifier|public
name|boolean
name|realtime
parameter_list|()
block|{
return|return
name|this
operator|.
name|realtime
return|;
block|}
DECL|method|uid
specifier|public
name|Term
name|uid
parameter_list|()
block|{
return|return
name|uid
return|;
block|}
DECL|method|loadSource
specifier|public
name|boolean
name|loadSource
parameter_list|()
block|{
return|return
name|this
operator|.
name|loadSource
return|;
block|}
DECL|method|loadSource
specifier|public
name|Get
name|loadSource
parameter_list|(
name|boolean
name|loadSource
parameter_list|)
block|{
name|this
operator|.
name|loadSource
operator|=
name|loadSource
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|version
specifier|public
name|Get
name|version
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|versionType
specifier|public
name|VersionType
name|versionType
parameter_list|()
block|{
return|return
name|versionType
return|;
block|}
DECL|method|versionType
specifier|public
name|Get
name|versionType
parameter_list|(
name|VersionType
name|versionType
parameter_list|)
block|{
name|this
operator|.
name|versionType
operator|=
name|versionType
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|class|GetResult
specifier|static
class|class
name|GetResult
block|{
DECL|field|exists
specifier|private
specifier|final
name|boolean
name|exists
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Translog
operator|.
name|Source
name|source
decl_stmt|;
DECL|field|docIdAndVersion
specifier|private
specifier|final
name|Versions
operator|.
name|DocIdAndVersion
name|docIdAndVersion
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|Searcher
name|searcher
decl_stmt|;
DECL|field|NOT_EXISTS
specifier|public
specifier|static
specifier|final
name|GetResult
name|NOT_EXISTS
init|=
operator|new
name|GetResult
argument_list|(
literal|false
argument_list|,
name|Versions
operator|.
name|NOT_FOUND
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|method|GetResult
specifier|public
name|GetResult
parameter_list|(
name|boolean
name|exists
parameter_list|,
name|long
name|version
parameter_list|,
annotation|@
name|Nullable
name|Translog
operator|.
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|exists
operator|=
name|exists
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|docIdAndVersion
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|GetResult
specifier|public
name|GetResult
parameter_list|(
name|Searcher
name|searcher
parameter_list|,
name|Versions
operator|.
name|DocIdAndVersion
name|docIdAndVersion
parameter_list|)
block|{
name|this
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|source
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|docIdAndVersion
operator|.
name|version
expr_stmt|;
name|this
operator|.
name|docIdAndVersion
operator|=
name|docIdAndVersion
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|exists
return|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
annotation|@
name|Nullable
DECL|method|source
specifier|public
name|Translog
operator|.
name|Source
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
DECL|method|searcher
specifier|public
name|Searcher
name|searcher
parameter_list|()
block|{
return|return
name|this
operator|.
name|searcher
return|;
block|}
DECL|method|docIdAndVersion
specifier|public
name|Versions
operator|.
name|DocIdAndVersion
name|docIdAndVersion
parameter_list|()
block|{
return|return
name|docIdAndVersion
return|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_interface

end_unit

