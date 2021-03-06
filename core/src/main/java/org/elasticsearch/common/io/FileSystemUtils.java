begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|SuppressForbidden
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
name|net
operator|.
name|URL
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
name|util
operator|.
name|stream
operator|.
name|StreamSupport
import|;
end_import

begin_comment
comment|/**  * Elasticsearch utils to work with {@link java.nio.file.Path}  */
end_comment

begin_class
DECL|class|FileSystemUtils
specifier|public
specifier|final
class|class
name|FileSystemUtils
block|{
DECL|method|FileSystemUtils
specifier|private
name|FileSystemUtils
parameter_list|()
block|{}
comment|// only static methods
comment|/**      * Returns<code>true</code> iff one of the files exists otherwise<code>false</code>      */
DECL|method|exists
specifier|public
specifier|static
name|boolean
name|exists
parameter_list|(
name|Path
modifier|...
name|files
parameter_list|)
block|{
for|for
control|(
name|Path
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Check whether the file denoted by the given path is hidden.      * In practice, this will check if the file name starts with a dot.      * This should be preferred to {@link Files#isHidden(Path)} as this      * does not depend on the operating system.      */
DECL|method|isHidden
specifier|public
specifier|static
name|boolean
name|isHidden
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|Path
name|fileName
init|=
name|path
operator|.
name|getFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileName
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|fileName
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
return|;
block|}
comment|/**      * Appends the path to the given base and strips N elements off the path if strip is&gt; 0.      */
DECL|method|append
specifier|public
specifier|static
name|Path
name|append
parameter_list|(
name|Path
name|base
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|strip
parameter_list|)
block|{
for|for
control|(
name|Path
name|subPath
range|:
name|path
control|)
block|{
if|if
condition|(
name|strip
operator|--
operator|>
literal|0
condition|)
block|{
continue|continue;
block|}
name|base
operator|=
name|base
operator|.
name|resolve
argument_list|(
name|subPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|base
return|;
block|}
comment|/**      * Deletes all subdirectories in the given path recursively      * @throws java.lang.IllegalArgumentException if the given path is not a directory      */
DECL|method|deleteSubDirectories
specifier|public
specifier|static
name|void
name|deleteSubDirectories
parameter_list|(
name|Path
modifier|...
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
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
name|path
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|subPath
range|:
name|stream
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|subPath
argument_list|)
condition|)
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|subPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * Check that a directory exists, is a directory and is readable      * by the current user      */
DECL|method|isAccessibleDirectory
specifier|public
specifier|static
name|boolean
name|isAccessibleDirectory
parameter_list|(
name|Path
name|directory
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
assert|assert
name|directory
operator|!=
literal|null
operator|&&
name|logger
operator|!=
literal|null
assert|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|directory
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] directory does not exist."
argument_list|,
name|directory
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|directory
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] should be a directory but is not."
argument_list|,
name|directory
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|isReadable
argument_list|(
name|directory
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] directory is not readable."
argument_list|,
name|directory
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Returns an InputStream the given url if the url has a protocol of 'file' or 'jar', no host, and no port.      */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Will only open url streams for local files"
argument_list|)
DECL|method|openFileURLStream
specifier|public
specifier|static
name|InputStream
name|openFileURLStream
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|protocol
init|=
name|url
operator|.
name|getProtocol
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"file"
operator|.
name|equals
argument_list|(
name|protocol
argument_list|)
operator|==
literal|false
operator|&&
literal|"jar"
operator|.
name|equals
argument_list|(
name|protocol
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid protocol ["
operator|+
name|protocol
operator|+
literal|"], must be [file] or [jar]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|url
operator|.
name|getHost
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"URL cannot have host. Found: ["
operator|+
name|url
operator|.
name|getHost
argument_list|()
operator|+
literal|']'
argument_list|)
throw|;
block|}
if|if
condition|(
name|url
operator|.
name|getPort
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"URL cannot have port. Found: ["
operator|+
name|url
operator|.
name|getPort
argument_list|()
operator|+
literal|']'
argument_list|)
throw|;
block|}
return|return
name|url
operator|.
name|openStream
argument_list|()
return|;
block|}
comment|/**      * Returns an array of all files in the given directory matching.      */
DECL|method|files
specifier|public
specifier|static
name|Path
index|[]
name|files
parameter_list|(
name|Path
name|from
parameter_list|,
name|DirectoryStream
operator|.
name|Filter
argument_list|<
name|Path
argument_list|>
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
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
name|from
argument_list|,
name|filter
argument_list|)
init|)
block|{
return|return
name|toArray
argument_list|(
name|stream
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns an array of all files in the given directory.      */
DECL|method|files
specifier|public
specifier|static
name|Path
index|[]
name|files
parameter_list|(
name|Path
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
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
return|return
name|toArray
argument_list|(
name|stream
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns an array of all files in the given directory matching the glob.      */
DECL|method|files
specifier|public
specifier|static
name|Path
index|[]
name|files
parameter_list|(
name|Path
name|directory
parameter_list|,
name|String
name|glob
parameter_list|)
throws|throws
name|IOException
block|{
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
argument_list|,
name|glob
argument_list|)
init|)
block|{
return|return
name|toArray
argument_list|(
name|stream
argument_list|)
return|;
block|}
block|}
DECL|method|toArray
specifier|private
specifier|static
name|Path
index|[]
name|toArray
parameter_list|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
parameter_list|)
block|{
return|return
name|StreamSupport
operator|.
name|stream
argument_list|(
name|stream
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|toArray
argument_list|(
name|length
lambda|->
operator|new
name|Path
index|[
name|length
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

