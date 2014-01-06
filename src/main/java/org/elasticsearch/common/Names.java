begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|jsr166y
operator|.
name|ThreadLocalRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Names
specifier|public
specifier|abstract
class|class
name|Names
block|{
DECL|method|randomNodeName
specifier|public
specifier|static
name|String
name|randomNodeName
parameter_list|(
name|URL
name|nodeNames
parameter_list|)
block|{
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|nodeNames
operator|.
name|openStream
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|numberOfNames
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|readLine
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|numberOfNames
operator|++
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|nodeNames
operator|.
name|openStream
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|number
init|=
operator|(
operator|(
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numberOfNames
argument_list|)
operator|)
operator|%
name|numberOfNames
operator|)
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
return|return
name|reader
operator|.
name|readLine
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore this exception
block|}
block|}
block|}
DECL|method|randomNodeName
specifier|public
specifier|static
name|String
name|randomNodeName
parameter_list|(
name|InputStream
name|nodeNames
parameter_list|)
block|{
if|if
condition|(
name|nodeNames
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|nodeNames
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numberOfNames
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|number
init|=
operator|(
operator|(
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numberOfNames
argument_list|)
operator|)
operator|%
name|numberOfNames
operator|)
operator|-
literal|2
decl_stmt|;
comment|// remove 2 for last line and first line
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|number
condition|;
name|i
operator|++
control|)
block|{
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
return|return
name|reader
operator|.
name|readLine
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
finally|finally
block|{
try|try
block|{
name|nodeNames
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
DECL|method|Names
specifier|private
name|Names
parameter_list|()
block|{      }
block|}
end_class

end_unit

