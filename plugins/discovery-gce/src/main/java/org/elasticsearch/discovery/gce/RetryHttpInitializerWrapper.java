begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.gce
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|gce
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|auth
operator|.
name|oauth2
operator|.
name|Credential
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|http
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|util
operator|.
name|ExponentialBackOff
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|util
operator|.
name|Sleeper
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
name|ESLogger
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
name|ESLoggerFactory
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
name|Objects
import|;
end_import

begin_class
DECL|class|RetryHttpInitializerWrapper
specifier|public
class|class
name|RetryHttpInitializerWrapper
implements|implements
name|HttpRequestInitializer
block|{
DECL|field|maxWait
specifier|private
name|int
name|maxWait
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|RetryHttpInitializerWrapper
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Intercepts the request for filling in the "Authorization"
comment|// header field, as well as recovering from certain unsuccessful
comment|// error codes wherein the Credential must refresh its token for a
comment|// retry.
DECL|field|wrappedCredential
specifier|private
specifier|final
name|Credential
name|wrappedCredential
decl_stmt|;
comment|// A sleeper; you can replace it with a mock in your test.
DECL|field|sleeper
specifier|private
specifier|final
name|Sleeper
name|sleeper
decl_stmt|;
DECL|method|RetryHttpInitializerWrapper
specifier|public
name|RetryHttpInitializerWrapper
parameter_list|(
name|Credential
name|wrappedCredential
parameter_list|)
block|{
name|this
argument_list|(
name|wrappedCredential
argument_list|,
name|Sleeper
operator|.
name|DEFAULT
argument_list|,
name|ExponentialBackOff
operator|.
name|DEFAULT_MAX_ELAPSED_TIME_MILLIS
argument_list|)
expr_stmt|;
block|}
DECL|method|RetryHttpInitializerWrapper
specifier|public
name|RetryHttpInitializerWrapper
parameter_list|(
name|Credential
name|wrappedCredential
parameter_list|,
name|int
name|maxWait
parameter_list|)
block|{
name|this
argument_list|(
name|wrappedCredential
argument_list|,
name|Sleeper
operator|.
name|DEFAULT
argument_list|,
name|maxWait
argument_list|)
expr_stmt|;
block|}
comment|// Use only for testing.
DECL|method|RetryHttpInitializerWrapper
name|RetryHttpInitializerWrapper
parameter_list|(
name|Credential
name|wrappedCredential
parameter_list|,
name|Sleeper
name|sleeper
parameter_list|,
name|int
name|maxWait
parameter_list|)
block|{
name|this
operator|.
name|wrappedCredential
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|wrappedCredential
argument_list|)
expr_stmt|;
name|this
operator|.
name|sleeper
operator|=
name|sleeper
expr_stmt|;
name|this
operator|.
name|maxWait
operator|=
name|maxWait
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|HttpRequest
name|httpRequest
parameter_list|)
block|{
specifier|final
name|HttpUnsuccessfulResponseHandler
name|backoffHandler
init|=
operator|new
name|HttpBackOffUnsuccessfulResponseHandler
argument_list|(
operator|new
name|ExponentialBackOff
operator|.
name|Builder
argument_list|()
operator|.
name|setMaxElapsedTimeMillis
argument_list|(
name|maxWait
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|setSleeper
argument_list|(
name|sleeper
argument_list|)
decl_stmt|;
name|httpRequest
operator|.
name|setInterceptor
argument_list|(
name|wrappedCredential
argument_list|)
expr_stmt|;
name|httpRequest
operator|.
name|setUnsuccessfulResponseHandler
argument_list|(
operator|new
name|HttpUnsuccessfulResponseHandler
argument_list|()
block|{
name|int
name|retry
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|handleResponse
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpResponse
name|response
parameter_list|,
name|boolean
name|supportsRetry
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|wrappedCredential
operator|.
name|handleResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|supportsRetry
argument_list|)
condition|)
block|{
comment|// If credential decides it can handle it,
comment|// the return code or message indicated
comment|// something specific to authentication,
comment|// and no backoff is desired.
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|backoffHandler
operator|.
name|handleResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|supportsRetry
argument_list|)
condition|)
block|{
comment|// Otherwise, we defer to the judgement of
comment|// our internal backoff handler.
name|logger
operator|.
name|debug
argument_list|(
literal|"Retrying [{}] times : [{}]"
argument_list|,
name|retry
argument_list|,
name|request
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|httpRequest
operator|.
name|setIOExceptionHandler
argument_list|(
operator|new
name|HttpBackOffIOExceptionHandler
argument_list|(
operator|new
name|ExponentialBackOff
operator|.
name|Builder
argument_list|()
operator|.
name|setMaxElapsedTimeMillis
argument_list|(
name|maxWait
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|setSleeper
argument_list|(
name|sleeper
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

