begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_comment
comment|/* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this  * file except in compliance with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under  * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS  * OF ANY KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress.lzf
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|lzf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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

begin_comment
comment|/**  * Simple command-line utility that can be used for testing LZF  * compression, or as rudimentary command-line tool.  * Arguments are the same as used by the "standard" lzf command line tool  *  * @author tatu@ning.com  */
end_comment

begin_class
DECL|class|LZF
specifier|public
class|class
name|LZF
block|{
DECL|field|SUFFIX
specifier|public
specifier|final
specifier|static
name|String
name|SUFFIX
init|=
literal|".lzf"
decl_stmt|;
DECL|method|process
name|void
name|process
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|String
name|oper
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|boolean
name|compress
init|=
literal|"-c"
operator|.
name|equals
argument_list|(
name|oper
argument_list|)
decl_stmt|;
if|if
condition|(
name|compress
operator|||
literal|"-d"
operator|.
name|equals
argument_list|(
name|oper
argument_list|)
condition|)
block|{
name|String
name|filename
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|File
name|src
init|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|src
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"File '"
operator|+
name|filename
operator|+
literal|"' does not exist."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|compress
operator|&&
operator|!
name|filename
operator|.
name|endsWith
argument_list|(
name|SUFFIX
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"File '"
operator|+
name|filename
operator|+
literal|"' does end with expected suffix ('"
operator|+
name|SUFFIX
operator|+
literal|"', won't decompress."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
name|readData
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Read "
operator|+
name|data
operator|.
name|length
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
name|byte
index|[]
name|result
init|=
name|compress
condition|?
name|LZFEncoder
operator|.
name|encode
argument_list|(
name|data
argument_list|)
else|:
name|LZFDecoder
operator|.
name|decode
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Processed into "
operator|+
name|result
operator|.
name|length
operator|+
literal|" bytes."
argument_list|)
expr_stmt|;
name|File
name|resultFile
init|=
name|compress
condition|?
operator|new
name|File
argument_list|(
name|filename
operator|+
name|SUFFIX
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|filename
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|filename
operator|.
name|length
argument_list|()
operator|-
name|SUFFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|resultFile
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wrote in file '"
operator|+
name|resultFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" -c/-d file"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|readData
specifier|private
name|byte
index|[]
name|readData
parameter_list|(
name|File
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|in
operator|.
name|length
argument_list|()
decl_stmt|;
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|in
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|count
init|=
name|fis
operator|.
name|read
argument_list|(
name|result
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
break|break;
name|len
operator|-=
name|count
expr_stmt|;
name|offset
operator|+=
name|count
expr_stmt|;
block|}
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
comment|// should never occur...
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not read the whole file -- received EOF when there was "
operator|+
name|len
operator|+
literal|" bytes left to read"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|LZF
argument_list|()
operator|.
name|process
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

