begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.ingest.attachment
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|attachment
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|LuceneTestCase
operator|.
name|SuppressFileSystems
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
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
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
name|test
operator|.
name|ESTestCase
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

begin_comment
comment|/**  * Evil test-coverage cheat, we parse a bunch of docs from tika  * so that we have a nice grab-bag variety, and assert some content  * comes back and no exception.  */
end_comment

begin_class
annotation|@
name|SuppressFileSystems
argument_list|(
literal|"ExtrasFS"
argument_list|)
comment|// don't try to parse extraN
DECL|class|TikaDocTests
specifier|public
class|class
name|TikaDocTests
extends|extends
name|ESTestCase
block|{
comment|/** some test files from tika test suite, zipped up */
DECL|field|TIKA_FILES
specifier|static
specifier|final
name|String
name|TIKA_FILES
init|=
literal|"/org/elasticsearch/ingest/attachment/test/tika-files/"
decl_stmt|;
DECL|method|testFiles
specifier|public
name|void
name|testFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tmp
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"unzipping all tika sample files"
argument_list|)
expr_stmt|;
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
name|PathUtils
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|TIKA_FILES
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|doc
range|:
name|stream
control|)
block|{
name|String
name|filename
init|=
name|doc
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|unzip
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|TIKA_FILES
operator|+
name|filename
argument_list|)
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
block|}
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
name|tmp
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|doc
range|:
name|stream
control|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"parsing: {}"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|assertParseable
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertParseable
name|void
name|assertParseable
parameter_list|(
name|Path
name|fileName
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|byte
name|bytes
index|[]
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|String
name|parsedContent
init|=
name|TikaImpl
operator|.
name|parse
argument_list|(
name|bytes
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parsedContent
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|parsedContent
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"extracted content: {}"
argument_list|,
name|parsedContent
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"parsing of filename: "
operator|+
name|fileName
operator|.
name|getFileName
argument_list|()
operator|+
literal|" failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

