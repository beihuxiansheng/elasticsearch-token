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
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
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
name|StandardOpenOption
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * Tests for reading old and new translog files  */
end_comment

begin_class
DECL|class|TranslogVersionTests
specifier|public
class|class
name|TranslogVersionTests
extends|extends
name|ESTestCase
block|{
DECL|method|checkFailsToOpen
specifier|private
name|void
name|checkFailsToOpen
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|expectedMessage
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|translogFile
init|=
name|getDataPath
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"test file should exist"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|translogFile
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|openReader
argument_list|(
name|translogFile
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be able to open an old translog"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|expectedMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testV0LegacyTranslogVersion
specifier|public
name|void
name|testV0LegacyTranslogVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|checkFailsToOpen
argument_list|(
literal|"/org/elasticsearch/index/translog/translog-v0.binary"
argument_list|,
literal|"pre-1.4 translog"
argument_list|)
expr_stmt|;
block|}
DECL|method|testV1ChecksummedTranslogVersion
specifier|public
name|void
name|testV1ChecksummedTranslogVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|checkFailsToOpen
argument_list|(
literal|"/org/elasticsearch/index/translog/translog-v1.binary"
argument_list|,
literal|"pre-2.0 translog"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCorruptedTranslogs
specifier|public
name|void
name|testCorruptedTranslogs
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Path
name|translogFile
init|=
name|getDataPath
argument_list|(
literal|"/org/elasticsearch/index/translog/translog-v1-corrupted-magic.binary"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"test file should exist"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|translogFile
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|openReader
argument_list|(
name|translogFile
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception about the header being corrupt"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TranslogCorruptedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"translog corruption from header: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"translog looks like version 1 or later, but has corrupted header"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Path
name|translogFile
init|=
name|getDataPath
argument_list|(
literal|"/org/elasticsearch/index/translog/translog-invalid-first-byte.binary"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"test file should exist"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|translogFile
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|openReader
argument_list|(
name|translogFile
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception about the header being corrupt"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TranslogCorruptedException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"translog corruption from header: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid first byte in translog file, got: 1, expected 0x00 or 0x3f"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|checkFailsToOpen
argument_list|(
literal|"/org/elasticsearch/index/translog/translog-v1-corrupted-body.binary"
argument_list|,
literal|"pre-2.0 translog"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTruncatedTranslog
specifier|public
name|void
name|testTruncatedTranslog
parameter_list|()
throws|throws
name|Exception
block|{
name|checkFailsToOpen
argument_list|(
literal|"/org/elasticsearch/index/translog/translog-v1-truncated.binary"
argument_list|,
literal|"pre-2.0 translog"
argument_list|)
expr_stmt|;
block|}
DECL|method|openReader
specifier|public
name|TranslogReader
name|openReader
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|long
name|id
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FileChannel
name|channel
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|)
init|)
block|{
specifier|final
name|long
name|minSeqNo
init|=
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
decl_stmt|;
specifier|final
name|long
name|maxSeqNo
init|=
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
decl_stmt|;
specifier|final
name|Checkpoint
name|checkpoint
init|=
operator|new
name|Checkpoint
argument_list|(
name|Files
operator|.
name|size
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|,
name|id
argument_list|,
name|minSeqNo
argument_list|,
name|maxSeqNo
argument_list|,
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|,
name|id
argument_list|)
decl_stmt|;
return|return
name|TranslogReader
operator|.
name|open
argument_list|(
name|channel
argument_list|,
name|path
argument_list|,
name|checkpoint
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

