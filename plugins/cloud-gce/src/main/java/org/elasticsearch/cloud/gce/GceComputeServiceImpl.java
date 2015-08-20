begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.gce
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
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
name|googleapis
operator|.
name|compute
operator|.
name|ComputeCredential
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
name|googleapis
operator|.
name|javanet
operator|.
name|GoogleNetHttpTransport
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
name|HttpTransport
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
name|json
operator|.
name|JsonFactory
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
name|json
operator|.
name|jackson2
operator|.
name|JacksonFactory
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
name|services
operator|.
name|compute
operator|.
name|Compute
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
name|services
operator|.
name|compute
operator|.
name|model
operator|.
name|Instance
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
name|services
operator|.
name|compute
operator|.
name|model
operator|.
name|InstanceList
import|;
end_import

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
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|AbstractLifecycleComponent
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
name|inject
operator|.
name|Inject
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
name|settings
operator|.
name|Settings
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
name|unit
operator|.
name|TimeValue
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
name|CollectionUtils
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
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|CollectionUtils
operator|.
name|eagerTransform
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GceComputeServiceImpl
specifier|public
class|class
name|GceComputeServiceImpl
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|GceComputeService
argument_list|>
implements|implements
name|GceComputeService
block|{
DECL|field|project
specifier|private
specifier|final
name|String
name|project
decl_stmt|;
DECL|field|zones
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|zones
decl_stmt|;
comment|// Forcing Google Token API URL as set in GCE SDK to
comment|//      http://metadata/computeMetadata/v1/instance/service-accounts/default/token
comment|// See https://developers.google.com/compute/docs/metadata#metadataserver
DECL|field|TOKEN_SERVER_ENCODED_URL
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_SERVER_ENCODED_URL
init|=
literal|"http://metadata.google.internal/computeMetadata/v1/instance/service-accounts/default/token"
decl_stmt|;
annotation|@
name|Override
DECL|method|instances
specifier|public
name|Collection
argument_list|<
name|Instance
argument_list|>
name|instances
parameter_list|()
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"get instances for project [{}], zones [{}]"
argument_list|,
name|project
argument_list|,
name|zones
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|Instance
argument_list|>
argument_list|>
name|instanceListByZone
init|=
name|eagerTransform
argument_list|(
name|zones
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Instance
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Instance
argument_list|>
name|apply
parameter_list|(
name|String
name|zoneId
parameter_list|)
block|{
try|try
block|{
name|Compute
operator|.
name|Instances
operator|.
name|List
name|list
init|=
name|client
argument_list|()
operator|.
name|instances
argument_list|()
operator|.
name|list
argument_list|(
name|project
argument_list|,
name|zoneId
argument_list|)
decl_stmt|;
name|InstanceList
name|instanceList
init|=
name|list
operator|.
name|execute
argument_list|()
decl_stmt|;
if|if
condition|(
name|instanceList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
return|return
name|instanceList
operator|.
name|getItems
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Problem fetching instance list for zone {}"
argument_list|,
name|zoneId
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Full exception:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Collapse instances from all zones into one neat list
name|List
argument_list|<
name|Instance
argument_list|>
name|instanceList
init|=
name|CollectionUtils
operator|.
name|iterableAsArrayList
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|instanceListByZone
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|instanceList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"disabling GCE discovery. Can not get list of nodes"
argument_list|)
expr_stmt|;
block|}
return|return
name|instanceList
return|;
block|}
DECL|field|client
specifier|private
name|Compute
name|client
decl_stmt|;
DECL|field|refreshInterval
specifier|private
name|TimeValue
name|refreshInterval
init|=
literal|null
decl_stmt|;
DECL|field|lastRefresh
specifier|private
name|long
name|lastRefresh
decl_stmt|;
comment|/** Global instance of the HTTP transport. */
DECL|field|gceHttpTransport
specifier|private
name|HttpTransport
name|gceHttpTransport
decl_stmt|;
comment|/** Global instance of the JSON factory. */
DECL|field|gceJsonFactory
specifier|private
name|JsonFactory
name|gceJsonFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|GceComputeServiceImpl
specifier|public
name|GceComputeServiceImpl
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|settings
operator|.
name|get
argument_list|(
name|Fields
operator|.
name|PROJECT
argument_list|)
expr_stmt|;
name|String
index|[]
name|zoneList
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
name|Fields
operator|.
name|ZONE
argument_list|)
decl_stmt|;
name|this
operator|.
name|zones
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|zoneList
argument_list|)
expr_stmt|;
block|}
DECL|method|getGceHttpTransport
specifier|protected
specifier|synchronized
name|HttpTransport
name|getGceHttpTransport
parameter_list|()
throws|throws
name|GeneralSecurityException
throws|,
name|IOException
block|{
if|if
condition|(
name|gceHttpTransport
operator|==
literal|null
condition|)
block|{
name|gceHttpTransport
operator|=
name|GoogleNetHttpTransport
operator|.
name|newTrustedTransport
argument_list|()
expr_stmt|;
block|}
return|return
name|gceHttpTransport
return|;
block|}
DECL|method|client
specifier|public
specifier|synchronized
name|Compute
name|client
parameter_list|()
block|{
if|if
condition|(
name|refreshInterval
operator|!=
literal|null
operator|&&
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|client
operator|!=
literal|null
operator|&&
operator|(
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|<
literal|0
operator|||
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastRefresh
operator|)
operator|<
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|logger
operator|.
name|trace
argument_list|(
literal|"using cache to retrieve client"
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
name|lastRefresh
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|gceJsonFactory
operator|=
operator|new
name|JacksonFactory
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"starting GCE discovery service"
argument_list|)
expr_stmt|;
name|ComputeCredential
name|credential
init|=
operator|new
name|ComputeCredential
operator|.
name|Builder
argument_list|(
name|getGceHttpTransport
argument_list|()
argument_list|,
name|gceJsonFactory
argument_list|)
operator|.
name|setTokenServerEncodedUrl
argument_list|(
name|TOKEN_SERVER_ENCODED_URL
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|credential
operator|.
name|refreshToken
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"token [{}] will expire in [{}] s"
argument_list|,
name|credential
operator|.
name|getAccessToken
argument_list|()
argument_list|,
name|credential
operator|.
name|getExpiresInSeconds
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|credential
operator|.
name|getExpiresInSeconds
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|refreshInterval
operator|=
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
name|credential
operator|.
name|getExpiresInSeconds
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Once done, let's use this token
name|this
operator|.
name|client
operator|=
operator|new
name|Compute
operator|.
name|Builder
argument_list|(
name|getGceHttpTransport
argument_list|()
argument_list|,
name|gceJsonFactory
argument_list|,
literal|null
argument_list|)
operator|.
name|setApplicationName
argument_list|(
name|Fields
operator|.
name|VERSION
argument_list|)
operator|.
name|setHttpRequestInitializer
argument_list|(
name|credential
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"unable to start GCE discovery service"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to start GCE discovery service"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|client
return|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
if|if
condition|(
name|gceHttpTransport
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|gceHttpTransport
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"unable to shutdown GCE Http Transport"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|gceHttpTransport
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
block|}
end_class

end_unit
