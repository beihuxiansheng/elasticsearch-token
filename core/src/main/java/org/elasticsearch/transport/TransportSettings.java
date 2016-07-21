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
name|common
operator|.
name|settings
operator|.
name|Setting
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
name|Setting
operator|.
name|Property
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
name|util
operator|.
name|List
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
name|Function
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|settings
operator|.
name|Setting
operator|.
name|groupSetting
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
name|settings
operator|.
name|Setting
operator|.
name|intSetting
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
name|settings
operator|.
name|Setting
operator|.
name|listSetting
import|;
end_import

begin_comment
comment|/**  * a collection of settings related to transport components, which are also needed in org.elasticsearch.bootstrap.Security  * This class should only contain static code which is *safe* to load before the security manager is enforced.  */
end_comment

begin_class
DECL|class|TransportSettings
specifier|public
specifier|final
class|class
name|TransportSettings
block|{
DECL|field|HOST
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|HOST
init|=
name|listSetting
argument_list|(
literal|"transport.host"
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|PUBLISH_HOST
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|PUBLISH_HOST
init|=
name|listSetting
argument_list|(
literal|"transport.publish_host"
argument_list|,
name|HOST
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|BIND_HOST
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|BIND_HOST
init|=
name|listSetting
argument_list|(
literal|"transport.bind_host"
argument_list|,
name|HOST
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|PORT
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|PORT
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"transport.tcp.port"
argument_list|,
literal|"9300-9400"
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|PUBLISH_PORT
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|PUBLISH_PORT
init|=
name|intSetting
argument_list|(
literal|"transport.publish_port"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PROFILE
init|=
literal|"default"
decl_stmt|;
DECL|field|TRANSPORT_PROFILES_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Settings
argument_list|>
name|TRANSPORT_PROFILES_SETTING
init|=
name|groupSetting
argument_list|(
literal|"transport.profiles."
argument_list|,
name|Property
operator|.
name|Dynamic
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|method|TransportSettings
specifier|private
name|TransportSettings
parameter_list|()
block|{      }
block|}
end_class

end_unit

