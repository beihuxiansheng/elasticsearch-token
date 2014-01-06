begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

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
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchInterruptedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchTimeoutException
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
name|util
operator|.
name|concurrent
operator|.
name|BaseFuture
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PlainTransportFuture
specifier|public
class|class
name|PlainTransportFuture
parameter_list|<
name|V
extends|extends
name|TransportResponse
parameter_list|>
extends|extends
name|BaseFuture
argument_list|<
name|V
argument_list|>
implements|implements
name|TransportFuture
argument_list|<
name|V
argument_list|>
implements|,
name|TransportResponseHandler
argument_list|<
name|V
argument_list|>
block|{
DECL|field|handler
specifier|private
specifier|final
name|TransportResponseHandler
argument_list|<
name|V
argument_list|>
name|handler
decl_stmt|;
DECL|method|PlainTransportFuture
specifier|public
name|PlainTransportFuture
parameter_list|(
name|TransportResponseHandler
argument_list|<
name|V
argument_list|>
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|txGet
specifier|public
name|V
name|txGet
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
try|try
block|{
return|return
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchInterruptedException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ElasticsearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticsearchException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|TransportException
argument_list|(
literal|"Failed execution"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|txGet
specifier|public
name|V
name|txGet
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
try|try
block|{
return|return
name|get
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchTimeoutException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchInterruptedException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ElasticsearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticsearchException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|TransportException
argument_list|(
literal|"Failed execution"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|V
name|newInstance
parameter_list|()
block|{
return|return
name|handler
operator|.
name|newInstance
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|handler
operator|.
name|executor
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|handleResponse
specifier|public
name|void
name|handleResponse
parameter_list|(
name|V
name|response
parameter_list|)
block|{
try|try
block|{
name|handler
operator|.
name|handleResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|handleException
argument_list|(
operator|new
name|ResponseHandlerFailureTransportException
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|handleException
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
try|try
block|{
name|handler
operator|.
name|handleException
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|setException
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"future("
operator|+
name|handler
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

