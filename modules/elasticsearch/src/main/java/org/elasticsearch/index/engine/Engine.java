begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|document
operator|.
name|Document
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
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|component
operator|.
name|CloseableComponent
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadSafe
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
name|shard
operator|.
name|IndexShardComponent
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
annotation|@
name|ThreadSafe
DECL|interface|Engine
specifier|public
interface|interface
name|Engine
extends|extends
name|IndexShardComponent
extends|,
name|CloseableComponent
block|{
comment|/**      * Starts the Engine.      *      *<p>Note, after the creation and before the call to start, the store might      * be changed.      */
DECL|method|start
name|void
name|start
parameter_list|()
throws|throws
name|EngineException
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
DECL|method|searcher
name|Searcher
name|searcher
parameter_list|()
throws|throws
name|EngineException
function_decl|;
comment|/**      * Refreshes the engine for new search operations to reflect the latest      * changes. Pass<tt>true</tt> if the refresh operation should include      * all the operations performed up to this call.      */
DECL|method|refresh
name|void
name|refresh
parameter_list|(
name|Refresh
name|refresh
parameter_list|)
throws|throws
name|EngineException
function_decl|;
comment|/**      * Flushes the state of the engine, clearing memory.      */
DECL|method|flush
name|void
name|flush
parameter_list|(
name|Flush
name|flush
parameter_list|)
throws|throws
name|EngineException
throws|,
name|FlushNotAllowedEngineException
function_decl|;
DECL|method|optimize
name|void
name|optimize
parameter_list|(
name|Optimize
name|optimize
parameter_list|)
throws|throws
name|EngineException
function_decl|;
DECL|method|snapshot
parameter_list|<
name|T
parameter_list|>
name|T
name|snapshot
parameter_list|(
name|SnapshotHandler
argument_list|<
name|T
argument_list|>
name|snapshotHandler
parameter_list|)
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
comment|/**      * Returns the estimated flushable memory size. Returns<tt>null</tt> if not available.      */
DECL|method|estimateFlushableMemorySize
name|ByteSizeValue
name|estimateFlushableMemorySize
parameter_list|()
function_decl|;
comment|/**      * Recovery allow to start the recovery process. It is built of three phases.      *      *<p>The first phase allows to take a snapshot of the master index. Once this      * is taken, no commit operations are effectively allowed on the index until the recovery      * phases are through.      *      *<p>The seconds phase takes a snapshot of the current transaction log.      *      *<p>The last phase returns the remaining transaction log. During this phase, no dirty      * operations are allowed on the index.      */
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
name|ElasticSearchException
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
name|ElasticSearchException
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
name|ElasticSearchException
function_decl|;
block|}
comment|/**      */
DECL|interface|SnapshotHandler
specifier|static
interface|interface
name|SnapshotHandler
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|snapshot
name|T
name|snapshot
parameter_list|(
name|SnapshotIndexCommit
name|snapshotIndexCommit
parameter_list|,
name|Translog
operator|.
name|Snapshot
name|translogSnapshot
parameter_list|)
throws|throws
name|EngineException
function_decl|;
block|}
DECL|interface|Searcher
specifier|static
interface|interface
name|Searcher
extends|extends
name|Releasable
block|{
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
DECL|class|Refresh
specifier|static
class|class
name|Refresh
block|{
DECL|field|waitForOperations
specifier|private
specifier|final
name|boolean
name|waitForOperations
decl_stmt|;
DECL|method|Refresh
specifier|public
name|Refresh
parameter_list|(
name|boolean
name|waitForOperations
parameter_list|)
block|{
name|this
operator|.
name|waitForOperations
operator|=
name|waitForOperations
expr_stmt|;
block|}
DECL|method|waitForOperations
specifier|public
name|boolean
name|waitForOperations
parameter_list|()
block|{
return|return
name|waitForOperations
return|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"waitForOperations["
operator|+
name|waitForOperations
operator|+
literal|"]"
return|;
block|}
block|}
DECL|class|Flush
specifier|static
class|class
name|Flush
block|{
DECL|field|full
specifier|private
name|boolean
name|full
init|=
literal|false
decl_stmt|;
DECL|field|refresh
specifier|private
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
comment|/**          * Should a refresh be performed after flushing. Defaults to<tt>false</tt>.          */
DECL|method|refresh
specifier|public
name|boolean
name|refresh
parameter_list|()
block|{
return|return
name|this
operator|.
name|refresh
return|;
block|}
comment|/**          * Should a refresh be performed after flushing. Defaults to<tt>false</tt>.          */
DECL|method|refresh
specifier|public
name|Flush
name|refresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|this
operator|.
name|refresh
operator|=
name|refresh
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Should a "full" flush be issued, basically cleaning as much memory as possible.          */
DECL|method|full
specifier|public
name|boolean
name|full
parameter_list|()
block|{
return|return
name|this
operator|.
name|full
return|;
block|}
comment|/**          * Should a "full" flush be issued, basically cleaning as much memory as possible.          */
DECL|method|full
specifier|public
name|Flush
name|full
parameter_list|(
name|boolean
name|full
parameter_list|)
block|{
name|this
operator|.
name|full
operator|=
name|full
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"full["
operator|+
name|full
operator|+
literal|"], refresh["
operator|+
name|refresh
operator|+
literal|"]"
return|;
block|}
block|}
DECL|class|Optimize
specifier|static
class|class
name|Optimize
block|{
DECL|field|waitForMerge
specifier|private
name|boolean
name|waitForMerge
init|=
literal|true
decl_stmt|;
DECL|field|maxNumSegments
specifier|private
name|int
name|maxNumSegments
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|onlyExpungeDeletes
specifier|private
name|boolean
name|onlyExpungeDeletes
init|=
literal|false
decl_stmt|;
DECL|field|flush
specifier|private
name|boolean
name|flush
init|=
literal|false
decl_stmt|;
DECL|field|refresh
specifier|private
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
DECL|method|Optimize
specifier|public
name|Optimize
parameter_list|()
block|{         }
DECL|method|waitForMerge
specifier|public
name|boolean
name|waitForMerge
parameter_list|()
block|{
return|return
name|waitForMerge
return|;
block|}
DECL|method|waitForMerge
specifier|public
name|Optimize
name|waitForMerge
parameter_list|(
name|boolean
name|waitForMerge
parameter_list|)
block|{
name|this
operator|.
name|waitForMerge
operator|=
name|waitForMerge
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxNumSegments
specifier|public
name|int
name|maxNumSegments
parameter_list|()
block|{
return|return
name|maxNumSegments
return|;
block|}
DECL|method|maxNumSegments
specifier|public
name|Optimize
name|maxNumSegments
parameter_list|(
name|int
name|maxNumSegments
parameter_list|)
block|{
name|this
operator|.
name|maxNumSegments
operator|=
name|maxNumSegments
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|onlyExpungeDeletes
specifier|public
name|boolean
name|onlyExpungeDeletes
parameter_list|()
block|{
return|return
name|onlyExpungeDeletes
return|;
block|}
DECL|method|onlyExpungeDeletes
specifier|public
name|Optimize
name|onlyExpungeDeletes
parameter_list|(
name|boolean
name|onlyExpungeDeletes
parameter_list|)
block|{
name|this
operator|.
name|onlyExpungeDeletes
operator|=
name|onlyExpungeDeletes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|flush
specifier|public
name|boolean
name|flush
parameter_list|()
block|{
return|return
name|flush
return|;
block|}
DECL|method|flush
specifier|public
name|Optimize
name|flush
parameter_list|(
name|boolean
name|flush
parameter_list|)
block|{
name|this
operator|.
name|flush
operator|=
name|flush
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|refresh
specifier|public
name|boolean
name|refresh
parameter_list|()
block|{
return|return
name|refresh
return|;
block|}
DECL|method|refresh
specifier|public
name|Optimize
name|refresh
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|this
operator|.
name|refresh
operator|=
name|refresh
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"waitForMerge["
operator|+
name|waitForMerge
operator|+
literal|"], maxNumSegments["
operator|+
name|maxNumSegments
operator|+
literal|"], onlyExpungeDeletes["
operator|+
name|onlyExpungeDeletes
operator|+
literal|"], flush["
operator|+
name|flush
operator|+
literal|"], refresh["
operator|+
name|refresh
operator|+
literal|"]"
return|;
block|}
block|}
DECL|class|Create
specifier|static
class|class
name|Create
block|{
DECL|field|document
specifier|private
specifier|final
name|Document
name|document
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
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
DECL|field|source
specifier|private
specifier|final
name|byte
index|[]
name|source
decl_stmt|;
DECL|method|Create
specifier|public
name|Create
parameter_list|(
name|Document
name|document
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|byte
index|[]
name|source
parameter_list|)
block|{
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
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
name|source
operator|=
name|source
expr_stmt|;
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
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|()
block|{
return|return
name|this
operator|.
name|document
return|;
block|}
DECL|method|analyzer
specifier|public
name|Analyzer
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
DECL|method|source
specifier|public
name|byte
index|[]
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
return|;
block|}
block|}
DECL|class|Index
specifier|static
class|class
name|Index
block|{
DECL|field|uid
specifier|private
specifier|final
name|Term
name|uid
decl_stmt|;
DECL|field|document
specifier|private
specifier|final
name|Document
name|document
decl_stmt|;
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
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
DECL|field|source
specifier|private
specifier|final
name|byte
index|[]
name|source
decl_stmt|;
DECL|method|Index
specifier|public
name|Index
parameter_list|(
name|Term
name|uid
parameter_list|,
name|Document
name|document
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|byte
index|[]
name|source
parameter_list|)
block|{
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
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
name|source
operator|=
name|source
expr_stmt|;
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
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|()
block|{
return|return
name|this
operator|.
name|document
return|;
block|}
DECL|method|analyzer
specifier|public
name|Analyzer
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
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
DECL|method|source
specifier|public
name|byte
index|[]
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
return|;
block|}
block|}
DECL|class|Delete
specifier|static
class|class
name|Delete
block|{
DECL|field|uid
specifier|private
specifier|final
name|Term
name|uid
decl_stmt|;
DECL|method|Delete
specifier|public
name|Delete
parameter_list|(
name|Term
name|uid
parameter_list|)
block|{
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
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
DECL|field|queryParserName
specifier|private
specifier|final
name|String
name|queryParserName
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|byte
index|[]
name|source
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|String
index|[]
name|types
decl_stmt|;
DECL|method|DeleteByQuery
specifier|public
name|DeleteByQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|byte
index|[]
name|source
parameter_list|,
annotation|@
name|Nullable
name|String
name|queryParserName
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
name|queryParserName
operator|=
name|queryParserName
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
block|}
DECL|method|queryParserName
specifier|public
name|String
name|queryParserName
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryParserName
return|;
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
name|byte
index|[]
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
block|}
block|}
end_interface

end_unit

