begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_class
DECL|class|RateLimitedFSDirectory
specifier|public
specifier|final
class|class
name|RateLimitedFSDirectory
extends|extends
name|FilterDirectory
block|{
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
name|super
argument_list|(
name|wrapped
argument_list|)
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
specifier|final
name|IndexOutput
name|output
init|=
name|in
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
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|in
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
name|in
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
name|super
argument_list|(
name|delegate
operator|instanceof
name|BufferedIndexOutput
condition|?
operator|(
operator|(
name|BufferedIndexOutput
operator|)
name|delegate
operator|)
operator|.
name|getBufferSize
argument_list|()
else|:
name|BufferedIndexOutput
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
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
DECL|method|setLength
specifier|public
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
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

