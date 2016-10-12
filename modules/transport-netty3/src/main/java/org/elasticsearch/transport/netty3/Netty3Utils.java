begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.netty3
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty3
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
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

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
name|util
operator|.
name|Supplier
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
name|BytesRefIterator
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
name|bytes
operator|.
name|BytesReference
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
name|logging
operator|.
name|Loggers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|logging
operator|.
name|InternalLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|logging
operator|.
name|InternalLoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|util
operator|.
name|ThreadNameDeterminer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|util
operator|.
name|ThreadRenamingRunnable
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
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_class
DECL|class|Netty3Utils
specifier|public
class|class
name|Netty3Utils
block|{
comment|/**      * Here we go....      *<p>      * When using the socket or file channel API to write or read using heap ByteBuffer, the sun.nio      * package will convert it to a direct buffer before doing the actual operation. The direct buffer is      * cached on an array of buffers under the nio.ch.Util$BufferCache on a thread local.      *<p>      * In netty specifically, if we send a single ChannelBuffer that is bigger than      * SocketSendBufferPool#DEFAULT_PREALLOCATION_SIZE (64kb), it will just convert the ChannelBuffer      * to a ByteBuffer and send it. The problem is, that then same size DirectByteBuffer will be      * allocated (or reused) and kept around on a thread local in the sun.nio BufferCache. If very      * large buffer is sent, imagine a 10mb one, then a 10mb direct buffer will be allocated as an      * entry within the thread local buffers.      *<p>      * In ES, we try and page the buffers allocated, all serialized data uses {@link org.elasticsearch.common.bytes.PagedBytesReference}      * typically generated from {@link org.elasticsearch.common.io.stream.BytesStreamOutput}. When sending it over      * to netty, it creates a {@link org.jboss.netty.buffer.CompositeChannelBuffer} that wraps the relevant pages.      *<p>      * The idea with the usage of composite channel buffer is that a single large buffer will not be sent over      * to the sun.nio layer. But, this will only happen if the composite channel buffer is created with a gathering      * flag set to true. In such a case, the GatheringSendBuffer is used in netty, resulting in calling the sun.nio      * layer with a ByteBuffer array.      *<p>      * This, potentially would have been as disastrous if the sun.nio layer would have tried to still copy over      * all of it to a direct buffer. But, the write(ByteBuffer[]) API (see sun.nio.ch.IOUtil), goes one buffer      * at a time, and gets a temporary direct buffer from the BufferCache, up to a limit of IOUtil#IOV_MAX (which      * is 1024 on most OSes). This means that there will be a max of 1024 direct buffer per thread.      *<p>      * This is still less than optimal to be honest, since it means that if not all data was written successfully      * (1024 paged buffers), then the rest of the data will need to be copied over again to the direct buffer      * and re-transmitted, but its much better than trying to send the full large buffer over and over again.      *<p>      * In ES, we use by default, in our paged data structures, a page of 16kb, so this is not so terrible.      *<p>      * Note, on the read size of netty, it uses a single direct buffer that is defined in both the transport      * and http configuration (based on the direct memory available), and the upstream handlers (SizeHeaderFrameDecoder,      * or more specifically the FrameDecoder base class) makes sure to use a cumulation buffer and not copy it      * over all the time.      *<p>      * TODO: potentially, a more complete solution would be to write a netty channel handler that is the last      * in the pipeline, and if the buffer is composite, verifies that its a gathering one with reasonable      * sized pages, and if its a single one, makes sure that it gets sliced and wrapped in a composite      * buffer.      */
DECL|field|DEFAULT_GATHERING
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_GATHERING
init|=
literal|true
decl_stmt|;
DECL|field|ES_THREAD_NAME_DETERMINER
specifier|private
specifier|static
name|EsThreadNameDeterminer
name|ES_THREAD_NAME_DETERMINER
init|=
operator|new
name|EsThreadNameDeterminer
argument_list|()
decl_stmt|;
DECL|class|EsThreadNameDeterminer
specifier|public
specifier|static
class|class
name|EsThreadNameDeterminer
implements|implements
name|ThreadNameDeterminer
block|{
annotation|@
name|Override
DECL|method|determineThreadName
specifier|public
name|String
name|determineThreadName
parameter_list|(
name|String
name|currentThreadName
parameter_list|,
name|String
name|proposedThreadName
parameter_list|)
throws|throws
name|Exception
block|{
comment|// we control the thread name with a context, so use both
return|return
name|currentThreadName
operator|+
literal|"{"
operator|+
name|proposedThreadName
operator|+
literal|"}"
return|;
block|}
block|}
static|static
block|{
name|InternalLoggerFactory
operator|.
name|setDefaultFactory
argument_list|(
operator|new
name|InternalLoggerFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalLogger
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|name
operator|=
name|name
operator|.
name|replace
argument_list|(
literal|"org.jboss.netty."
argument_list|,
literal|"netty3."
argument_list|)
operator|.
name|replace
argument_list|(
literal|"org.jboss.netty."
argument_list|,
literal|"netty3."
argument_list|)
expr_stmt|;
return|return
operator|new
name|Netty3InternalESLogger
argument_list|(
name|Loggers
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ThreadRenamingRunnable
operator|.
name|setThreadNameDeterminer
argument_list|(
name|ES_THREAD_NAME_DETERMINER
argument_list|)
expr_stmt|;
comment|// Netty 3 SelectorUtil wants to set this; however, it does not execute the property write
comment|// in a privileged block so we just do what Netty wants to do here
specifier|final
name|String
name|key
init|=
literal|"sun.nio.ch.bugLevel"
decl_stmt|;
specifier|final
name|String
name|buglevel
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|buglevel
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"to use System#setProperty to set sun.nio.ch.bugLevel"
argument_list|)
specifier|public
name|Void
name|run
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SecurityException
name|e
parameter_list|)
block|{
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Netty3Utils
operator|.
name|class
argument_list|)
operator|.
name|debug
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"Unable to get/set System Property: {}"
argument_list|,
name|key
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setup
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{     }
comment|/**      * Turns the given BytesReference into a ChannelBuffer. Note: the returned ChannelBuffer will reference the internal      * pages of the BytesReference. Don't free the bytes of reference before the ChannelBuffer goes out of scope.      */
DECL|method|toChannelBuffer
specifier|public
specifier|static
name|ChannelBuffer
name|toChannelBuffer
parameter_list|(
name|BytesReference
name|reference
parameter_list|)
block|{
if|if
condition|(
name|reference
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|ChannelBuffers
operator|.
name|EMPTY_BUFFER
return|;
block|}
if|if
condition|(
name|reference
operator|instanceof
name|ChannelBufferBytesReference
condition|)
block|{
return|return
operator|(
operator|(
name|ChannelBufferBytesReference
operator|)
name|reference
operator|)
operator|.
name|toChannelBuffer
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|BytesRefIterator
name|iterator
init|=
name|reference
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|slice
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ChannelBuffer
argument_list|>
name|buffers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|slice
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|buffers
operator|.
name|add
argument_list|(
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|slice
operator|.
name|bytes
argument_list|,
name|slice
operator|.
name|offset
argument_list|,
name|slice
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|DEFAULT_GATHERING
argument_list|,
name|buffers
operator|.
name|toArray
argument_list|(
operator|new
name|ChannelBuffer
index|[
name|buffers
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"no IO happens here"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Wraps the given ChannelBuffer with a BytesReference      */
DECL|method|toBytesReference
specifier|public
specifier|static
name|BytesReference
name|toBytesReference
parameter_list|(
name|ChannelBuffer
name|channelBuffer
parameter_list|)
block|{
return|return
name|toBytesReference
argument_list|(
name|channelBuffer
argument_list|,
name|channelBuffer
operator|.
name|readableBytes
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Wraps the given ChannelBuffer with a BytesReference of a given size      */
DECL|method|toBytesReference
specifier|public
specifier|static
name|BytesReference
name|toBytesReference
parameter_list|(
name|ChannelBuffer
name|channelBuffer
parameter_list|,
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|ChannelBufferBytesReference
argument_list|(
name|channelBuffer
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
end_class

end_unit

