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
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
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
name|OutputStream
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

begin_comment
comment|/**  * Process ID file abstraction that writes the current pid into a file and optionally  * removes it on system exit.  */
end_comment

begin_class
DECL|class|PidFile
specifier|public
specifier|final
class|class
name|PidFile
block|{
DECL|field|pid
specifier|private
specifier|final
name|long
name|pid
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|deleteOnExit
specifier|private
specifier|final
name|boolean
name|deleteOnExit
decl_stmt|;
DECL|method|PidFile
specifier|private
name|PidFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|deleteOnExit
parameter_list|,
name|long
name|pid
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|deleteOnExit
operator|=
name|deleteOnExit
expr_stmt|;
name|this
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
block|}
comment|/**      * Creates a new PidFile and writes the current process ID into the provided path      *      * @param path the path to the pid file. The file is newly created or truncated if it already exists      * @param deleteOnExit if<code>true</code> the pid file is deleted with best effort on system exit      * @throws IOException if an IOException occurs      */
DECL|method|create
specifier|public
specifier|static
name|PidFile
name|create
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|deleteOnExit
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|create
argument_list|(
name|path
argument_list|,
name|deleteOnExit
argument_list|,
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|pid
argument_list|()
argument_list|)
return|;
block|}
DECL|method|create
specifier|static
name|PidFile
name|create
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|deleteOnExit
parameter_list|,
name|long
name|pid
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|parent
init|=
name|path
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|parent
argument_list|)
operator|&&
name|Files
operator|.
name|isDirectory
argument_list|(
name|parent
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|parent
operator|+
literal|" exists but is not a directory"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|parent
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// only do this if it doesn't exists we get a better exception further down
comment|// if there are security issues etc. this also doesn't work if the parent exists
comment|// and is a soft-link like on many linux systems /var/run can be a link and that should
comment|// not prevent us from writing the PID
name|Files
operator|.
name|createDirectories
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
operator|&&
name|Files
operator|.
name|isRegularFile
argument_list|(
name|path
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|path
operator|+
literal|" exists but is not a regular file"
argument_list|)
throw|;
block|}
try|try
init|(
name|OutputStream
name|stream
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|path
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|,
name|StandardOpenOption
operator|.
name|TRUNCATE_EXISTING
argument_list|)
init|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|pid
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteOnExit
condition|)
block|{
name|addShutdownHook
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PidFile
argument_list|(
name|path
argument_list|,
name|deleteOnExit
argument_list|,
name|pid
argument_list|)
return|;
block|}
comment|/**      * Returns the current process id      */
DECL|method|getPid
specifier|public
name|long
name|getPid
parameter_list|()
block|{
return|return
name|pid
return|;
block|}
comment|/**      * Returns the process id file path      */
DECL|method|getPath
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * Returns<code>true</code> iff the process id file is deleted on system exit. Otherwise<code>false</code>.      */
DECL|method|isDeleteOnExit
specifier|public
name|boolean
name|isDeleteOnExit
parameter_list|()
block|{
return|return
name|deleteOnExit
return|;
block|}
DECL|method|addShutdownHook
specifier|private
specifier|static
name|void
name|addShutdownHook
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|path
argument_list|)
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
literal|"Failed to delete pid file "
operator|+
name|path
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

