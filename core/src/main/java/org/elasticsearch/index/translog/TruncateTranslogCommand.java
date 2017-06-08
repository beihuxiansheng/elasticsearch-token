begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
package|;
end_package

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
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
name|DirectoryReader
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
name|IndexCommit
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
name|IndexWriter
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|store
operator|.
name|Lock
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
name|store
operator|.
name|LockObtainFailedException
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
name|store
operator|.
name|NativeFSLockFactory
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
name|store
operator|.
name|OutputStreamDataOutput
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|cli
operator|.
name|EnvironmentAwareCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cli
operator|.
name|Terminal
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
name|SuppressForbidden
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
name|io
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|IndexNotFoundException
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
name|seqno
operator|.
name|SequenceNumbersService
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
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|TruncateTranslogCommand
specifier|public
class|class
name|TruncateTranslogCommand
extends|extends
name|EnvironmentAwareCommand
block|{
DECL|field|translogFolder
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|translogFolder
decl_stmt|;
DECL|field|batchMode
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|batchMode
decl_stmt|;
DECL|method|TruncateTranslogCommand
specifier|public
name|TruncateTranslogCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"Truncates a translog to create a new, empty translog"
argument_list|)
expr_stmt|;
name|this
operator|.
name|translogFolder
operator|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"d"
argument_list|,
literal|"dir"
argument_list|)
argument_list|,
literal|"Translog Directory location on disk"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|required
argument_list|()
expr_stmt|;
name|this
operator|.
name|batchMode
operator|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"b"
argument_list|,
literal|"batch"
argument_list|)
argument_list|,
literal|"Enable batch mode explicitly, automatic confirmation of warnings"
argument_list|)
expr_stmt|;
block|}
comment|// Visible for testing
DECL|method|getParser
specifier|public
name|OptionParser
name|getParser
parameter_list|()
block|{
return|return
name|this
operator|.
name|parser
return|;
block|}
annotation|@
name|Override
DECL|method|printAdditionalHelp
specifier|protected
name|void
name|printAdditionalHelp
parameter_list|(
name|Terminal
name|terminal
parameter_list|)
block|{
name|terminal
operator|.
name|println
argument_list|(
literal|"This tool truncates the translog and translog"
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"checkpoint files to create a new translog"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Necessary to use the path passed in"
argument_list|)
DECL|method|getTranslogPath
specifier|private
name|Path
name|getTranslogPath
parameter_list|(
name|OptionSet
name|options
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|get
argument_list|(
name|translogFolder
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|protected
name|void
name|execute
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|OptionSet
name|options
parameter_list|,
name|Environment
name|env
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|batch
init|=
name|options
operator|.
name|has
argument_list|(
name|batchMode
argument_list|)
decl_stmt|;
name|Path
name|translogPath
init|=
name|getTranslogPath
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|Path
name|idxLocation
init|=
name|translogPath
operator|.
name|getParent
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|translogPath
argument_list|)
operator|==
literal|false
operator|||
name|Files
operator|.
name|isDirectory
argument_list|(
name|translogPath
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"translog directory ["
operator|+
name|translogPath
operator|+
literal|"], must exist and be a directory"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|idxLocation
argument_list|)
operator|==
literal|false
operator|||
name|Files
operator|.
name|isDirectory
argument_list|(
name|idxLocation
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"unable to find a shard at ["
operator|+
name|idxLocation
operator|+
literal|"], which must exist and be a directory"
argument_list|)
throw|;
block|}
comment|// Hold the lock open for the duration of the tool running
try|try
init|(
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|idxLocation
argument_list|,
name|NativeFSLockFactory
operator|.
name|INSTANCE
argument_list|)
init|;
name|Lock
name|writeLock
operator|=
name|dir
operator|.
name|obtainLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
init|)
block|{
name|Set
argument_list|<
name|Path
argument_list|>
name|translogFiles
decl_stmt|;
try|try
block|{
name|terminal
operator|.
name|println
argument_list|(
literal|"Checking existing translog files"
argument_list|)
expr_stmt|;
name|translogFiles
operator|=
name|filesInDirectory
argument_list|(
name|translogPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|terminal
operator|.
name|println
argument_list|(
literal|"encountered IOException while listing directory, aborting..."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failed to find existing translog files"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Warn about ES being stopped and files being deleted
name|warnAboutDeletingFiles
argument_list|(
name|terminal
argument_list|,
name|translogFiles
argument_list|,
name|batch
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|commits
decl_stmt|;
try|try
block|{
name|terminal
operator|.
name|println
argument_list|(
literal|"Reading translog UUID information from Lucene commit from shard at ["
operator|+
name|idxLocation
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|commits
operator|=
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexNotFoundException
name|infe
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"unable to find a valid shard at ["
operator|+
name|idxLocation
operator|+
literal|"]"
argument_list|,
name|infe
argument_list|)
throw|;
block|}
comment|// Retrieve the generation and UUID from the existing data
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
init|=
name|commits
operator|.
name|get
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getUserData
argument_list|()
decl_stmt|;
name|String
name|translogGeneration
init|=
name|commitData
operator|.
name|get
argument_list|(
name|Translog
operator|.
name|TRANSLOG_GENERATION_KEY
argument_list|)
decl_stmt|;
name|String
name|translogUUID
init|=
name|commitData
operator|.
name|get
argument_list|(
name|Translog
operator|.
name|TRANSLOG_UUID_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|translogGeneration
operator|==
literal|null
operator|||
name|translogUUID
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"shard must have a valid translog generation and UUID but got: [{}] and: [{}]"
argument_list|,
name|translogGeneration
argument_list|,
name|translogUUID
argument_list|)
throw|;
block|}
name|terminal
operator|.
name|println
argument_list|(
literal|"Translog Generation: "
operator|+
name|translogGeneration
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"Translog UUID      : "
operator|+
name|translogUUID
argument_list|)
expr_stmt|;
name|Path
name|tempEmptyCheckpoint
init|=
name|translogPath
operator|.
name|resolve
argument_list|(
literal|"temp-"
operator|+
name|Translog
operator|.
name|CHECKPOINT_FILE_NAME
argument_list|)
decl_stmt|;
name|Path
name|realEmptyCheckpoint
init|=
name|translogPath
operator|.
name|resolve
argument_list|(
name|Translog
operator|.
name|CHECKPOINT_FILE_NAME
argument_list|)
decl_stmt|;
name|Path
name|tempEmptyTranslog
init|=
name|translogPath
operator|.
name|resolve
argument_list|(
literal|"temp-"
operator|+
name|Translog
operator|.
name|TRANSLOG_FILE_PREFIX
operator|+
name|translogGeneration
operator|+
name|Translog
operator|.
name|TRANSLOG_FILE_SUFFIX
argument_list|)
decl_stmt|;
name|Path
name|realEmptyTranslog
init|=
name|translogPath
operator|.
name|resolve
argument_list|(
name|Translog
operator|.
name|TRANSLOG_FILE_PREFIX
operator|+
name|translogGeneration
operator|+
name|Translog
operator|.
name|TRANSLOG_FILE_SUFFIX
argument_list|)
decl_stmt|;
comment|// Write empty checkpoint and translog to empty files
name|long
name|gen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|translogGeneration
argument_list|)
decl_stmt|;
name|int
name|translogLen
init|=
name|writeEmptyTranslog
argument_list|(
name|tempEmptyTranslog
argument_list|,
name|translogUUID
argument_list|)
decl_stmt|;
name|writeEmptyCheckpoint
argument_list|(
name|tempEmptyCheckpoint
argument_list|,
name|translogLen
argument_list|,
name|gen
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"Removing existing translog files"
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|rm
argument_list|(
name|translogFiles
operator|.
name|toArray
argument_list|(
operator|new
name|Path
index|[]
block|{}
argument_list|)
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"Creating new empty checkpoint at ["
operator|+
name|realEmptyCheckpoint
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|move
argument_list|(
name|tempEmptyCheckpoint
argument_list|,
name|realEmptyCheckpoint
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"Creating new empty translog at ["
operator|+
name|realEmptyTranslog
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|move
argument_list|(
name|tempEmptyTranslog
argument_list|,
name|realEmptyTranslog
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
comment|// Fsync the translog directory after rename
name|IOUtils
operator|.
name|fsync
argument_list|(
name|translogPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|lofe
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Failed to lock shard's directory at ["
operator|+
name|idxLocation
operator|+
literal|"], is Elasticsearch still running?"
argument_list|)
throw|;
block|}
name|terminal
operator|.
name|println
argument_list|(
literal|"Done."
argument_list|)
expr_stmt|;
block|}
comment|/** Write a checkpoint file to the given location with the given generation */
DECL|method|writeEmptyCheckpoint
specifier|public
specifier|static
name|void
name|writeEmptyCheckpoint
parameter_list|(
name|Path
name|filename
parameter_list|,
name|int
name|translogLength
parameter_list|,
name|long
name|translogGeneration
parameter_list|)
throws|throws
name|IOException
block|{
name|Checkpoint
name|emptyCheckpoint
init|=
name|Checkpoint
operator|.
name|emptyTranslogCheckpoint
argument_list|(
name|translogLength
argument_list|,
name|translogGeneration
argument_list|,
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|,
name|translogGeneration
argument_list|)
decl_stmt|;
name|Checkpoint
operator|.
name|write
argument_list|(
name|FileChannel
operator|::
name|open
argument_list|,
name|filename
argument_list|,
name|emptyCheckpoint
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|)
expr_stmt|;
comment|// fsync with metadata here to make sure.
name|IOUtils
operator|.
name|fsync
argument_list|(
name|filename
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Write a translog containing the given translog UUID to the given location. Returns the number of bytes written.      */
DECL|method|writeEmptyTranslog
specifier|public
specifier|static
name|int
name|writeEmptyTranslog
parameter_list|(
name|Path
name|filename
parameter_list|,
name|String
name|translogUUID
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|translogRef
init|=
operator|new
name|BytesRef
argument_list|(
name|translogUUID
argument_list|)
decl_stmt|;
try|try
init|(
name|FileChannel
name|fc
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|filename
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|)
init|;
name|OutputStreamDataOutput
name|out
operator|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|Channels
operator|.
name|newOutputStream
argument_list|(
name|fc
argument_list|)
argument_list|)
init|)
block|{
name|TranslogWriter
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|translogRef
argument_list|)
expr_stmt|;
name|fc
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|TranslogWriter
operator|.
name|getHeaderLength
argument_list|(
name|translogRef
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Show a warning about deleting files, asking for a confirmation if {@code batchMode} is false */
DECL|method|warnAboutDeletingFiles
specifier|public
specifier|static
name|void
name|warnAboutDeletingFiles
parameter_list|(
name|Terminal
name|terminal
parameter_list|,
name|Set
argument_list|<
name|Path
argument_list|>
name|files
parameter_list|,
name|boolean
name|batchMode
parameter_list|)
block|{
name|terminal
operator|.
name|println
argument_list|(
literal|"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"!   WARNING: Elasticsearch MUST be stopped before running this tool   !"
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"!                                                                     !"
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"!   WARNING:    Documents inside of translog files will be lost       !"
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"!                                                                     !"
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"!   WARNING:          The following files will be DELETED!            !"
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|println
argument_list|(
literal|"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|file
range|:
name|files
control|)
block|{
name|terminal
operator|.
name|println
argument_list|(
literal|"--> "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
name|terminal
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|batchMode
operator|==
literal|false
condition|)
block|{
name|String
name|text
init|=
name|terminal
operator|.
name|readText
argument_list|(
literal|"Continue and DELETE files? [y/N] "
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|text
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"y"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"aborted by user"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Return a Set of all files in a given directory */
DECL|method|filesInDirectory
specifier|public
specifier|static
name|Set
argument_list|<
name|Path
argument_list|>
name|filesInDirectory
parameter_list|(
name|Path
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|Path
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|directory
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|file
range|:
name|stream
control|)
block|{
name|files
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|files
return|;
block|}
block|}
end_class

end_unit

