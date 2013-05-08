begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|store
operator|.
name|IOContext
operator|.
name|Context
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
name|util
operator|.
name|Collection
import|;
end_import

begin_class
DECL|class|RateLimitedFSDirectory
specifier|public
specifier|final
class|class
name|RateLimitedFSDirectory
extends|extends
name|Directory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|FSDirectory
name|delegate
decl_stmt|;
DECL|field|rateLimitingProvider
specifier|private
specifier|final
name|StoreRateLimiting
operator|.
name|Provider
name|rateLimitingProvider
decl_stmt|;
DECL|field|rateListener
specifier|private
specifier|final
name|StoreRateLimiting
operator|.
name|Listener
name|rateListener
decl_stmt|;
DECL|method|RateLimitedFSDirectory
specifier|public
name|RateLimitedFSDirectory
parameter_list|(
name|FSDirectory
name|wrapped
parameter_list|,
name|StoreRateLimiting
operator|.
name|Provider
name|rateLimitingProvider
parameter_list|,
name|StoreRateLimiting
operator|.
name|Listener
name|rateListener
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|wrapped
expr_stmt|;
name|this
operator|.
name|rateLimitingProvider
operator|=
name|rateLimitingProvider
expr_stmt|;
name|this
operator|.
name|rateListener
operator|=
name|rateListener
expr_stmt|;
block|}
DECL|method|wrappedDirectory
specifier|public
name|FSDirectory
name|wrappedDirectory
parameter_list|()
block|{
return|return
name|this
operator|.
name|delegate
return|;
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|listAll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|IndexOutput
name|output
init|=
name|delegate
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|StoreRateLimiting
name|rateLimiting
init|=
name|rateLimitingProvider
operator|.
name|rateLimiting
argument_list|()
decl_stmt|;
name|StoreRateLimiting
operator|.
name|Type
name|type
init|=
name|rateLimiting
operator|.
name|getType
argument_list|()
decl_stmt|;
name|RateLimiter
name|limiter
init|=
name|rateLimiting
operator|.
name|getRateLimiter
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|NONE
operator|||
name|limiter
operator|==
literal|null
condition|)
block|{
return|return
name|output
return|;
block|}
if|if
condition|(
name|context
operator|.
name|context
operator|==
name|Context
operator|.
name|MERGE
condition|)
block|{
comment|// we are mering, and type is either MERGE or ALL, rate limit...
return|return
operator|new
name|RateLimitedIndexOutput
argument_list|(
name|limiter
argument_list|,
name|rateListener
argument_list|,
name|output
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|==
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|ALL
condition|)
block|{
return|return
operator|new
name|RateLimitedIndexOutput
argument_list|(
name|limiter
argument_list|,
name|rateListener
argument_list|,
name|output
argument_list|)
return|;
block|}
comment|// we shouldn't really get here...
return|return
name|output
return|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
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
name|IOException
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSlicer
specifier|public
name|IndexInputSlicer
name|createSlicer
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|createSlicer
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLockFactory
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLockFactory
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|getLockFactory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|getLockID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StoreRateLimiting
name|rateLimiting
init|=
name|rateLimitingProvider
operator|.
name|rateLimiting
argument_list|()
decl_stmt|;
name|StoreRateLimiting
operator|.
name|Type
name|type
init|=
name|rateLimiting
operator|.
name|getType
argument_list|()
decl_stmt|;
name|RateLimiter
name|limiter
init|=
name|rateLimiting
operator|.
name|getRateLimiter
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|StoreRateLimiting
operator|.
name|Type
operator|.
name|NONE
operator|||
name|limiter
operator|==
literal|null
condition|)
block|{
return|return
name|StoreUtils
operator|.
name|toString
argument_list|(
name|delegate
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|"rate_limited("
operator|+
name|StoreUtils
operator|.
name|toString
argument_list|(
name|delegate
argument_list|)
operator|+
literal|", type="
operator|+
name|type
operator|.
name|name
argument_list|()
operator|+
literal|", rate="
operator|+
name|limiter
operator|.
name|getMbPerSec
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|Directory
name|to
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|copy
argument_list|(
name|to
argument_list|,
name|src
argument_list|,
name|dest
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|class|RateLimitedIndexOutput
specifier|static
specifier|final
class|class
name|RateLimitedIndexOutput
extends|extends
name|BufferedIndexOutput
block|{
DECL|field|delegate
specifier|private
specifier|final
name|IndexOutput
name|delegate
decl_stmt|;
DECL|field|bufferedDelegate
specifier|private
specifier|final
name|BufferedIndexOutput
name|bufferedDelegate
decl_stmt|;
DECL|field|rateLimiter
specifier|private
specifier|final
name|RateLimiter
name|rateLimiter
decl_stmt|;
DECL|field|rateListener
specifier|private
specifier|final
name|StoreRateLimiting
operator|.
name|Listener
name|rateListener
decl_stmt|;
DECL|method|RateLimitedIndexOutput
name|RateLimitedIndexOutput
parameter_list|(
specifier|final
name|RateLimiter
name|rateLimiter
parameter_list|,
specifier|final
name|StoreRateLimiting
operator|.
name|Listener
name|rateListener
parameter_list|,
specifier|final
name|IndexOutput
name|delegate
parameter_list|)
block|{
comment|// TODO if Lucene exposed in BufferedIndexOutput#getBufferSize, we could initialize it if the delegate is buffered
if|if
condition|(
name|delegate
operator|instanceof
name|BufferedIndexOutput
condition|)
block|{
name|bufferedDelegate
operator|=
operator|(
name|BufferedIndexOutput
operator|)
name|delegate
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|bufferedDelegate
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|rateLimiter
operator|=
name|rateLimiter
expr_stmt|;
name|this
operator|.
name|rateListener
operator|=
name|rateListener
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flushBuffer
specifier|protected
name|void
name|flushBuffer
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|rateListener
operator|.
name|onPause
argument_list|(
name|rateLimiter
operator|.
name|pause
argument_list|(
name|len
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferedDelegate
operator|!=
literal|null
condition|)
block|{
name|bufferedDelegate
operator|.
name|flushBuffer
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|delegate
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

