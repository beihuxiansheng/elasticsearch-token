begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|discovery
operator|.
name|zen
operator|.
name|UnicastHostsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
import|;
end_import

begin_comment
comment|/**  * An additional extension point for {@link Plugin}s that extends Elasticsearch's discovery functionality. To add an additional  * {@link NetworkService.CustomNameResolver} just implement the interface and implement the {@link #getCustomNameResolver(Settings)} method:  *  *<pre>{@code  * public class MyDiscoveryPlugin extends Plugin implements DiscoveryPlugin {  *&#64;Override  *     public NetworkService.CustomNameResolver getCustomNameResolver(Settings settings) {  *         return new YourCustomNameResolverInstance(settings);  *     }  * }  * }</pre>  */
end_comment

begin_interface
DECL|interface|DiscoveryPlugin
specifier|public
interface|interface
name|DiscoveryPlugin
block|{
comment|/**      * Override to add additional {@link NetworkService.CustomNameResolver}s.      * This can be handy if you want to provide your own Network interface name like _mycard_      * and implement by yourself the logic to get an actual IP address/hostname based on this      * name.      *      * For example: you could call a third party service (an API) to resolve _mycard_.      * Then you could define in elasticsearch.yml settings like:      *      *<pre>{@code      * network.host: _mycard_      * }</pre>      */
DECL|method|getCustomNameResolver
specifier|default
name|NetworkService
operator|.
name|CustomNameResolver
name|getCustomNameResolver
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Returns providers of unicast host lists for zen discovery.      *      * The key of the returned map is the name of the host provider      * (see {@link org.elasticsearch.discovery.DiscoveryModule#DISCOVERY_HOSTS_PROVIDER_SETTING}), and      * the value is a supplier to construct the host provider when it is selected for use.      *      * @param transportService Use to form the {@link org.elasticsearch.common.transport.TransportAddress} portion      *                         of a {@link org.elasticsearch.cluster.node.DiscoveryNode}      * @param networkService Use to find the publish host address of the current node      */
DECL|method|getZenHostsProviders
specifier|default
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|UnicastHostsProvider
argument_list|>
argument_list|>
name|getZenHostsProviders
parameter_list|(
name|TransportService
name|transportService
parameter_list|,
name|NetworkService
name|networkService
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
block|}
end_interface

end_unit

