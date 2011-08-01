begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.http.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|http
operator|.
name|client
package|;
end_package

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
name|FileNotFoundException
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|net
operator|.
name|URLConnection
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HttpDownloadHelper
specifier|public
class|class
name|HttpDownloadHelper
block|{
DECL|field|useTimestamp
specifier|private
name|boolean
name|useTimestamp
init|=
literal|false
decl_stmt|;
DECL|field|skipExisting
specifier|private
name|boolean
name|skipExisting
init|=
literal|false
decl_stmt|;
DECL|field|maxTime
specifier|private
name|long
name|maxTime
init|=
literal|0
decl_stmt|;
DECL|method|download
specifier|public
name|boolean
name|download
parameter_list|(
name|URL
name|source
parameter_list|,
name|File
name|dest
parameter_list|,
annotation|@
name|Nullable
name|DownloadProgress
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dest
operator|.
name|exists
argument_list|()
operator|&&
name|skipExisting
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|//don't do any progress, unless asked
if|if
condition|(
name|progress
operator|==
literal|null
condition|)
block|{
name|progress
operator|=
operator|new
name|NullProgress
argument_list|()
expr_stmt|;
block|}
comment|//set the timestamp to the file date.
name|long
name|timestamp
init|=
literal|0
decl_stmt|;
name|boolean
name|hasTimestamp
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|useTimestamp
operator|&&
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
name|timestamp
operator|=
name|dest
operator|.
name|lastModified
argument_list|()
expr_stmt|;
name|hasTimestamp
operator|=
literal|true
expr_stmt|;
block|}
name|GetThread
name|getThread
init|=
operator|new
name|GetThread
argument_list|(
name|source
argument_list|,
name|dest
argument_list|,
name|hasTimestamp
argument_list|,
name|timestamp
argument_list|,
name|progress
argument_list|)
decl_stmt|;
name|getThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|getThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|getThread
operator|.
name|join
argument_list|(
name|maxTime
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|getThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"The GET operation took longer than "
operator|+
name|maxTime
operator|+
literal|" seconds, stopping it."
decl_stmt|;
name|getThread
operator|.
name|closeStreams
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
return|return
name|getThread
operator|.
name|wasSuccessful
argument_list|()
return|;
block|}
comment|/**      * Interface implemented for reporting      * progress of downloading.      */
DECL|interface|DownloadProgress
specifier|public
interface|interface
name|DownloadProgress
block|{
comment|/**          * begin a download          */
DECL|method|beginDownload
name|void
name|beginDownload
parameter_list|()
function_decl|;
comment|/**          * tick handler          */
DECL|method|onTick
name|void
name|onTick
parameter_list|()
function_decl|;
comment|/**          * end a download          */
DECL|method|endDownload
name|void
name|endDownload
parameter_list|()
function_decl|;
block|}
comment|/**      * do nothing with progress info      */
DECL|class|NullProgress
specifier|public
specifier|static
class|class
name|NullProgress
implements|implements
name|DownloadProgress
block|{
comment|/**          * begin a download          */
DECL|method|beginDownload
specifier|public
name|void
name|beginDownload
parameter_list|()
block|{          }
comment|/**          * tick handler          */
DECL|method|onTick
specifier|public
name|void
name|onTick
parameter_list|()
block|{         }
comment|/**          * end a download          */
DECL|method|endDownload
specifier|public
name|void
name|endDownload
parameter_list|()
block|{          }
block|}
comment|/**      * verbose progress system prints to some output stream      */
DECL|class|VerboseProgress
specifier|public
specifier|static
class|class
name|VerboseProgress
implements|implements
name|DownloadProgress
block|{
DECL|field|dots
specifier|private
name|int
name|dots
init|=
literal|0
decl_stmt|;
comment|// CheckStyle:VisibilityModifier OFF - bc
DECL|field|out
name|PrintStream
name|out
decl_stmt|;
comment|// CheckStyle:VisibilityModifier ON
comment|/**          * Construct a verbose progress reporter.          *          * @param out the output stream.          */
DECL|method|VerboseProgress
specifier|public
name|VerboseProgress
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**          * begin a download          */
DECL|method|beginDownload
specifier|public
name|void
name|beginDownload
parameter_list|()
block|{
name|out
operator|.
name|print
argument_list|(
literal|"Downloading "
argument_list|)
expr_stmt|;
name|dots
operator|=
literal|0
expr_stmt|;
block|}
comment|/**          * tick handler          */
DECL|method|onTick
specifier|public
name|void
name|onTick
parameter_list|()
block|{
name|out
operator|.
name|print
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
if|if
condition|(
name|dots
operator|++
operator|>
literal|50
condition|)
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|dots
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**          * end a download          */
DECL|method|endDownload
specifier|public
name|void
name|endDownload
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|(
literal|"DONE"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|GetThread
specifier|private
class|class
name|GetThread
extends|extends
name|Thread
block|{
DECL|field|source
specifier|private
specifier|final
name|URL
name|source
decl_stmt|;
DECL|field|dest
specifier|private
specifier|final
name|File
name|dest
decl_stmt|;
DECL|field|hasTimestamp
specifier|private
specifier|final
name|boolean
name|hasTimestamp
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|field|progress
specifier|private
specifier|final
name|DownloadProgress
name|progress
decl_stmt|;
DECL|field|success
specifier|private
name|boolean
name|success
init|=
literal|false
decl_stmt|;
DECL|field|ioexception
specifier|private
name|IOException
name|ioexception
init|=
literal|null
decl_stmt|;
DECL|field|is
specifier|private
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
DECL|field|os
specifier|private
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
DECL|field|connection
specifier|private
name|URLConnection
name|connection
decl_stmt|;
DECL|field|redirections
specifier|private
name|int
name|redirections
init|=
literal|0
decl_stmt|;
DECL|method|GetThread
name|GetThread
parameter_list|(
name|URL
name|source
parameter_list|,
name|File
name|dest
parameter_list|,
name|boolean
name|h
parameter_list|,
name|long
name|t
parameter_list|,
name|DownloadProgress
name|p
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
name|dest
operator|=
name|dest
expr_stmt|;
name|hasTimestamp
operator|=
name|h
expr_stmt|;
name|timestamp
operator|=
name|t
expr_stmt|;
name|progress
operator|=
name|p
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|success
operator|=
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioex
parameter_list|)
block|{
name|ioexception
operator|=
name|ioex
expr_stmt|;
block|}
block|}
DECL|method|get
specifier|private
name|boolean
name|get
parameter_list|()
throws|throws
name|IOException
block|{
name|connection
operator|=
name|openConnection
argument_list|(
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|downloadSucceeded
init|=
name|downloadFile
argument_list|()
decl_stmt|;
comment|//if (and only if) the use file time option is set, then
comment|//the saved file now has its timestamp set to that of the
comment|//downloaded file
if|if
condition|(
name|downloadSucceeded
operator|&&
name|useTimestamp
condition|)
block|{
name|updateTimeStamp
argument_list|()
expr_stmt|;
block|}
return|return
name|downloadSucceeded
return|;
block|}
DECL|method|redirectionAllowed
specifier|private
name|boolean
name|redirectionAllowed
parameter_list|(
name|URL
name|aSource
parameter_list|,
name|URL
name|aDest
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Argh, github does this...
comment|//            if (!(aSource.getProtocol().equals(aDest.getProtocol()) || ("http"
comment|//                    .equals(aSource.getProtocol())&& "https".equals(aDest
comment|//                    .getProtocol())))) {
comment|//                String message = "Redirection detected from "
comment|//                        + aSource.getProtocol() + " to " + aDest.getProtocol()
comment|//                        + ". Protocol switch unsafe, not allowed.";
comment|//                throw new IOException(message);
comment|//            }
name|redirections
operator|++
expr_stmt|;
if|if
condition|(
name|redirections
operator|>
literal|5
condition|)
block|{
name|String
name|message
init|=
literal|"More than "
operator|+
literal|5
operator|+
literal|" times redirected, giving up"
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|openConnection
specifier|private
name|URLConnection
name|openConnection
parameter_list|(
name|URL
name|aSource
parameter_list|)
throws|throws
name|IOException
block|{
comment|// set up the URL connection
name|URLConnection
name|connection
init|=
name|aSource
operator|.
name|openConnection
argument_list|()
decl_stmt|;
comment|// modify the headers
comment|// NB: things like user authentication could go in here too.
if|if
condition|(
name|hasTimestamp
condition|)
block|{
name|connection
operator|.
name|setIfModifiedSince
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|instanceof
name|HttpURLConnection
condition|)
block|{
operator|(
operator|(
name|HttpURLConnection
operator|)
name|connection
operator|)
operator|.
name|setInstanceFollowRedirects
argument_list|(
literal|false
argument_list|)
expr_stmt|;
operator|(
operator|(
name|HttpURLConnection
operator|)
name|connection
operator|)
operator|.
name|setUseCaches
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|HttpURLConnection
operator|)
name|connection
operator|)
operator|.
name|setConnectTimeout
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
comment|// connect to the remote site (may take some time)
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|// First check on a 301 / 302 (moved) response (HTTP only)
if|if
condition|(
name|connection
operator|instanceof
name|HttpURLConnection
condition|)
block|{
name|HttpURLConnection
name|httpConnection
init|=
operator|(
name|HttpURLConnection
operator|)
name|connection
decl_stmt|;
name|int
name|responseCode
init|=
name|httpConnection
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_MOVED_PERM
operator|||
name|responseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_MOVED_TEMP
operator|||
name|responseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_SEE_OTHER
condition|)
block|{
name|String
name|newLocation
init|=
name|httpConnection
operator|.
name|getHeaderField
argument_list|(
literal|"Location"
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|aSource
operator|+
operator|(
name|responseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_MOVED_PERM
condition|?
literal|" permanently"
else|:
literal|""
operator|)
operator|+
literal|" moved to "
operator|+
name|newLocation
decl_stmt|;
name|URL
name|newURL
init|=
operator|new
name|URL
argument_list|(
name|newLocation
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|redirectionAllowed
argument_list|(
name|aSource
argument_list|,
name|newURL
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|openConnection
argument_list|(
name|newURL
argument_list|)
return|;
block|}
comment|// next test for a 304 result (HTTP only)
name|long
name|lastModified
init|=
name|httpConnection
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_NOT_MODIFIED
operator|||
operator|(
name|lastModified
operator|!=
literal|0
operator|&&
name|hasTimestamp
operator|&&
name|timestamp
operator|>=
name|lastModified
operator|)
condition|)
block|{
comment|// not modified so no file download. just return
comment|// instead and trace out something so the user
comment|// doesn't think that the download happened when it
comment|// didn't
return|return
literal|null
return|;
block|}
comment|// test for 401 result (HTTP only)
if|if
condition|(
name|responseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
condition|)
block|{
name|String
name|message
init|=
literal|"HTTP Authorization failure"
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
comment|//REVISIT: at this point even non HTTP connections may
comment|//support the if-modified-since behaviour -we just check
comment|//the date of the content and skip the write if it is not
comment|//newer. Some protocols (FTP) don't include dates, of
comment|//course.
return|return
name|connection
return|;
block|}
DECL|method|downloadFile
specifier|private
name|boolean
name|downloadFile
parameter_list|()
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|IOException
name|lastEx
init|=
literal|null
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
comment|// this three attempt trick is to get round quirks in different
comment|// Java implementations. Some of them take a few goes to bind
comment|// property; we ignore the first couple of such failures.
try|try
block|{
name|is
operator|=
name|connection
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|lastEx
operator|=
name|ex
expr_stmt|;
block|}
block|}
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't get "
operator|+
name|source
operator|+
literal|" to "
operator|+
name|dest
argument_list|,
name|lastEx
argument_list|)
throw|;
block|}
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|progress
operator|.
name|beginDownload
argument_list|()
expr_stmt|;
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|100
index|]
decl_stmt|;
name|int
name|length
decl_stmt|;
while|while
condition|(
operator|!
name|isInterrupted
argument_list|()
operator|&&
operator|(
name|length
operator|=
name|is
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|progress
operator|.
name|onTick
argument_list|()
expr_stmt|;
block|}
name|finished
operator|=
operator|!
name|isInterrupted
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|os
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
try|try
block|{
name|is
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
comment|// we have started to (over)write dest, but failed.
comment|// Try to delete the garbage we'd otherwise leave
comment|// behind.
if|if
condition|(
operator|!
name|finished
condition|)
block|{
name|dest
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|progress
operator|.
name|endDownload
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|updateTimeStamp
specifier|private
name|void
name|updateTimeStamp
parameter_list|()
block|{
name|long
name|remoteTimestamp
init|=
name|connection
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteTimestamp
operator|!=
literal|0
condition|)
block|{
name|dest
operator|.
name|setLastModified
argument_list|(
name|remoteTimestamp
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Has the download completed successfully?          *          *<p>Re-throws any exception caught during executaion.</p>          */
DECL|method|wasSuccessful
name|boolean
name|wasSuccessful
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|ioexception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ioexception
throw|;
block|}
return|return
name|success
return|;
block|}
comment|/**          * Closes streams, interrupts the download, may delete the          * output file.          */
DECL|method|closeStreams
name|void
name|closeStreams
parameter_list|()
block|{
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|os
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
try|try
block|{
name|is
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
if|if
condition|(
operator|!
name|success
operator|&&
name|dest
operator|.
name|exists
argument_list|()
condition|)
block|{
name|dest
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

