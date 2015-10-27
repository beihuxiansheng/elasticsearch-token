begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.gce.network
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|gce
operator|.
name|network
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|gce
operator|.
name|GceComputeService
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
name|component
operator|.
name|AbstractComponent
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
name|network
operator|.
name|NetworkService
operator|.
name|CustomNameResolver
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_comment
comment|/**  *<p>Resolves certain GCE related 'meta' hostnames into an actual hostname  * obtained from gce meta-data.</p>  * Valid config values for {@link GceAddressResolverType}s are -  *<ul>  *<li>_gce_ - maps to privateIp</li>  *<li>_gce:privateIp_</li>  *<li>_gce:hostname_</li>  *</ul>  */
end_comment

begin_class
DECL|class|GceNameResolver
specifier|public
class|class
name|GceNameResolver
extends|extends
name|AbstractComponent
implements|implements
name|CustomNameResolver
block|{
DECL|field|gceComputeService
specifier|private
specifier|final
name|GceComputeService
name|gceComputeService
decl_stmt|;
comment|/**      * enum that can be added to over time with more meta-data types      */
DECL|enum|GceAddressResolverType
specifier|private
enum|enum
name|GceAddressResolverType
block|{
comment|/**          * Using the hostname          */
DECL|enum constant|PRIVATE_DNS
name|PRIVATE_DNS
argument_list|(
literal|"gce:hostname"
argument_list|,
literal|"hostname"
argument_list|)
block|,
comment|/**          * Can be gce:privateIp, gce:privateIp:X where X is the network interface          */
DECL|enum constant|PRIVATE_IP
name|PRIVATE_IP
argument_list|(
literal|"gce:privateIp"
argument_list|,
literal|"network-interfaces/{{network}}/ip"
argument_list|)
block|,
comment|/**          * same as "gce:privateIp" or "gce:privateIp:0"          */
DECL|enum constant|GCE
name|GCE
argument_list|(
literal|"gce"
argument_list|,
name|PRIVATE_IP
operator|.
name|gceName
argument_list|)
block|;
DECL|field|configName
specifier|final
name|String
name|configName
decl_stmt|;
DECL|field|gceName
specifier|final
name|String
name|gceName
decl_stmt|;
DECL|method|GceAddressResolverType
name|GceAddressResolverType
parameter_list|(
name|String
name|configName
parameter_list|,
name|String
name|gceName
parameter_list|)
block|{
name|this
operator|.
name|configName
operator|=
name|configName
expr_stmt|;
name|this
operator|.
name|gceName
operator|=
name|gceName
expr_stmt|;
block|}
block|}
comment|/**      * Construct a {@link CustomNameResolver}.      */
DECL|method|GceNameResolver
specifier|public
name|GceNameResolver
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|GceComputeService
name|gceComputeService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|gceComputeService
operator|=
name|gceComputeService
expr_stmt|;
block|}
comment|/**      * @param value the gce hostname type to discover.      * @return the appropriate host resolved from gce meta-data.      * @see CustomNameResolver#resolveIfPossible(String)      */
DECL|method|resolve
specifier|private
name|InetAddress
index|[]
name|resolve
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|gceMetadataPath
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|GceAddressResolverType
operator|.
name|GCE
operator|.
name|configName
argument_list|)
condition|)
block|{
comment|// We replace network placeholder with default network interface value: 0
name|gceMetadataPath
operator|=
name|Strings
operator|.
name|replace
argument_list|(
name|GceAddressResolverType
operator|.
name|GCE
operator|.
name|gceName
argument_list|,
literal|"{{network}}"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|GceAddressResolverType
operator|.
name|PRIVATE_DNS
operator|.
name|configName
argument_list|)
condition|)
block|{
name|gceMetadataPath
operator|=
name|GceAddressResolverType
operator|.
name|PRIVATE_DNS
operator|.
name|gceName
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
name|GceAddressResolverType
operator|.
name|PRIVATE_IP
operator|.
name|configName
argument_list|)
condition|)
block|{
comment|// We extract the network interface from gce:privateIp:XX
name|String
name|network
init|=
literal|"0"
decl_stmt|;
name|String
index|[]
name|privateIpConfig
init|=
name|Strings
operator|.
name|splitStringToArray
argument_list|(
name|value
argument_list|,
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|privateIpConfig
operator|!=
literal|null
operator|&&
name|privateIpConfig
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|network
operator|=
name|privateIpConfig
index|[
literal|2
index|]
expr_stmt|;
block|}
comment|// We replace network placeholder with network interface value
name|gceMetadataPath
operator|=
name|Strings
operator|.
name|replace
argument_list|(
name|GceAddressResolverType
operator|.
name|PRIVATE_IP
operator|.
name|gceName
argument_list|,
literal|"{{network}}"
argument_list|,
name|network
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|value
operator|+
literal|"] is not one of the supported GCE network.host setting. "
operator|+
literal|"Expecting _gce_, _gce:privateIp:X_, _gce:hostname_"
argument_list|)
throw|;
block|}
try|try
block|{
name|String
name|metadataResult
init|=
name|gceComputeService
operator|.
name|metadata
argument_list|(
name|gceMetadataPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadataResult
operator|==
literal|null
operator|||
name|metadataResult
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"no gce metadata returned from ["
operator|+
name|gceMetadataPath
operator|+
literal|"] for ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// only one address: because we explicitly ask for only one via the GceHostnameType
return|return
operator|new
name|InetAddress
index|[]
block|{
name|InetAddress
operator|.
name|getByName
argument_list|(
name|metadataResult
argument_list|)
block|}
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"IOException caught when fetching InetAddress from ["
operator|+
name|gceMetadataPath
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|resolveDefault
specifier|public
name|InetAddress
index|[]
name|resolveDefault
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// using this, one has to explicitly specify _gce_ in network setting
block|}
annotation|@
name|Override
DECL|method|resolveIfPossible
specifier|public
name|InetAddress
index|[]
name|resolveIfPossible
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We only try to resolve network.host setting when it starts with _gce
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"gce"
argument_list|)
condition|)
block|{
return|return
name|resolve
argument_list|(
name|value
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

