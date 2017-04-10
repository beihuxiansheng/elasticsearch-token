begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|nio
operator|.
name|protocol
operator|.
name|HttpAsyncResponseConsumer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|HttpAsyncResponseConsumerFactory
operator|.
name|HeapBufferedResponseConsumerFactory
operator|.
name|DEFAULT_BUFFER_LIMIT
import|;
end_import

begin_comment
comment|/**  * Factory used to create instances of {@link HttpAsyncResponseConsumer}. Each request retry needs its own instance of the  * consumer object. Users can implement this interface and pass their own instance to the specialized  * performRequest methods that accept an {@link HttpAsyncResponseConsumerFactory} instance as argument.  */
end_comment

begin_interface
DECL|interface|HttpAsyncResponseConsumerFactory
specifier|public
interface|interface
name|HttpAsyncResponseConsumerFactory
block|{
comment|/**      * Creates the default type of {@link HttpAsyncResponseConsumer}, based on heap buffering with a buffer limit of 100MB.      */
DECL|field|DEFAULT
name|HttpAsyncResponseConsumerFactory
name|DEFAULT
init|=
operator|new
name|HeapBufferedResponseConsumerFactory
argument_list|(
name|DEFAULT_BUFFER_LIMIT
argument_list|)
decl_stmt|;
comment|/**      * Creates the {@link HttpAsyncResponseConsumer}, called once per request attempt.      */
DECL|method|createHttpAsyncResponseConsumer
name|HttpAsyncResponseConsumer
argument_list|<
name|HttpResponse
argument_list|>
name|createHttpAsyncResponseConsumer
parameter_list|()
function_decl|;
comment|/**      * Default factory used to create instances of {@link HttpAsyncResponseConsumer}.      * Creates one instance of {@link HeapBufferedAsyncResponseConsumer} for each request attempt, with a configurable      * buffer limit which defaults to 100MB.      */
DECL|class|HeapBufferedResponseConsumerFactory
class|class
name|HeapBufferedResponseConsumerFactory
implements|implements
name|HttpAsyncResponseConsumerFactory
block|{
comment|//default buffer limit is 100MB
DECL|field|DEFAULT_BUFFER_LIMIT
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_LIMIT
init|=
literal|100
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|bufferLimit
specifier|private
specifier|final
name|int
name|bufferLimit
decl_stmt|;
DECL|method|HeapBufferedResponseConsumerFactory
specifier|public
name|HeapBufferedResponseConsumerFactory
parameter_list|(
name|int
name|bufferLimitBytes
parameter_list|)
block|{
name|this
operator|.
name|bufferLimit
operator|=
name|bufferLimitBytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createHttpAsyncResponseConsumer
specifier|public
name|HttpAsyncResponseConsumer
argument_list|<
name|HttpResponse
argument_list|>
name|createHttpAsyncResponseConsumer
parameter_list|()
block|{
return|return
operator|new
name|HeapBufferedAsyncResponseConsumer
argument_list|(
name|bufferLimit
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

