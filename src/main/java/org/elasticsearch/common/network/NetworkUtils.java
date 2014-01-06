begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.network
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|network
package|;
end_package

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
name|Lists
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
name|CollectionUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|Loggers
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
name|os
operator|.
name|OsUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NetworkUtils
specifier|public
specifier|abstract
class|class
name|NetworkUtils
block|{
DECL|field|logger
specifier|private
specifier|final
specifier|static
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|NetworkUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|StackType
specifier|public
specifier|static
enum|enum
name|StackType
block|{
DECL|enum constant|IPv4
DECL|enum constant|IPv6
DECL|enum constant|Unknown
name|IPv4
block|,
name|IPv6
block|,
name|Unknown
block|}
DECL|field|IPv4_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|IPv4_SETTING
init|=
literal|"java.net.preferIPv4Stack"
decl_stmt|;
DECL|field|IPv6_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|IPv6_SETTING
init|=
literal|"java.net.preferIPv6Addresses"
decl_stmt|;
DECL|field|NON_LOOPBACK_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|NON_LOOPBACK_ADDRESS
init|=
literal|"non_loopback_address"
decl_stmt|;
DECL|field|localAddress
specifier|private
specifier|final
specifier|static
name|InetAddress
name|localAddress
decl_stmt|;
static|static
block|{
name|InetAddress
name|localAddressX
init|=
literal|null
decl_stmt|;
try|try
block|{
name|localAddressX
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Failed to find local host"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|localAddress
operator|=
name|localAddressX
expr_stmt|;
block|}
DECL|method|defaultReuseAddress
specifier|public
specifier|static
name|Boolean
name|defaultReuseAddress
parameter_list|()
block|{
return|return
name|OsUtils
operator|.
name|WINDOWS
condition|?
literal|null
else|:
literal|true
return|;
block|}
DECL|method|isIPv4
specifier|public
specifier|static
name|boolean
name|isIPv4
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.net.preferIPv4Stack"
argument_list|)
operator|!=
literal|null
operator|&&
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.net.preferIPv4Stack"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
return|;
block|}
DECL|method|getIPv4Localhost
specifier|public
specifier|static
name|InetAddress
name|getIPv4Localhost
parameter_list|()
throws|throws
name|UnknownHostException
block|{
return|return
name|getLocalhost
argument_list|(
name|StackType
operator|.
name|IPv4
argument_list|)
return|;
block|}
DECL|method|getIPv6Localhost
specifier|public
specifier|static
name|InetAddress
name|getIPv6Localhost
parameter_list|()
throws|throws
name|UnknownHostException
block|{
return|return
name|getLocalhost
argument_list|(
name|StackType
operator|.
name|IPv6
argument_list|)
return|;
block|}
DECL|method|getLocalAddress
specifier|public
specifier|static
name|InetAddress
name|getLocalAddress
parameter_list|()
block|{
return|return
name|localAddress
return|;
block|}
DECL|method|getLocalhost
specifier|public
specifier|static
name|InetAddress
name|getLocalhost
parameter_list|(
name|StackType
name|ip_version
parameter_list|)
throws|throws
name|UnknownHostException
block|{
if|if
condition|(
name|ip_version
operator|==
name|StackType
operator|.
name|IPv4
condition|)
return|return
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"127.0.0.1"
argument_list|)
return|;
else|else
return|return
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"::1"
argument_list|)
return|;
block|}
DECL|method|canBindToMcastAddress
specifier|public
specifier|static
name|boolean
name|canBindToMcastAddress
parameter_list|()
block|{
return|return
name|OsUtils
operator|.
name|LINUX
operator|||
name|OsUtils
operator|.
name|SOLARIS
operator|||
name|OsUtils
operator|.
name|HP
return|;
block|}
comment|/**      * Returns the first non-loopback address on any interface on the current host.      *      * @param ip_version Constraint on IP version of address to be returned, 4 or 6      */
DECL|method|getFirstNonLoopbackAddress
specifier|public
specifier|static
name|InetAddress
name|getFirstNonLoopbackAddress
parameter_list|(
name|StackType
name|ip_version
parameter_list|)
throws|throws
name|SocketException
block|{
name|InetAddress
name|address
init|=
literal|null
decl_stmt|;
name|Enumeration
name|intfs
init|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NetworkInterface
argument_list|>
name|intfsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|intfs
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|intfsList
operator|.
name|add
argument_list|(
operator|(
name|NetworkInterface
operator|)
name|intfs
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// order by index, assuming first ones are more interesting
try|try
block|{
specifier|final
name|Method
name|getIndexMethod
init|=
name|NetworkInterface
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"getIndex"
argument_list|)
decl_stmt|;
name|getIndexMethod
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CollectionUtil
operator|.
name|timSort
argument_list|(
name|intfsList
argument_list|,
operator|new
name|Comparator
argument_list|<
name|NetworkInterface
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|NetworkInterface
name|o1
parameter_list|,
name|NetworkInterface
name|o2
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
operator|(
name|Integer
operator|)
name|getIndexMethod
operator|.
name|invoke
argument_list|(
name|o1
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
operator|-
operator|(
operator|(
name|Integer
operator|)
name|getIndexMethod
operator|.
name|invoke
argument_list|(
name|o2
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"failed to fetch index of network interface"
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
for|for
control|(
name|NetworkInterface
name|intf
range|:
name|intfsList
control|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|intf
operator|.
name|isUp
argument_list|()
operator|||
name|intf
operator|.
name|isLoopback
argument_list|()
condition|)
continue|continue;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// might happen when calling on a network interface that does not exists
continue|continue;
block|}
name|address
operator|=
name|getFirstNonLoopbackAddress
argument_list|(
name|intf
argument_list|,
name|ip_version
argument_list|)
expr_stmt|;
if|if
condition|(
name|address
operator|!=
literal|null
condition|)
block|{
return|return
name|address
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the first non-loopback address on the given interface on the current host.      *      * @param intf      the interface to be checked      * @param ipVersion Constraint on IP version of address to be returned, 4 or 6      */
DECL|method|getFirstNonLoopbackAddress
specifier|public
specifier|static
name|InetAddress
name|getFirstNonLoopbackAddress
parameter_list|(
name|NetworkInterface
name|intf
parameter_list|,
name|StackType
name|ipVersion
parameter_list|)
throws|throws
name|SocketException
block|{
if|if
condition|(
name|intf
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Network interface pointer is null"
argument_list|)
throw|;
for|for
control|(
name|Enumeration
name|addresses
init|=
name|intf
operator|.
name|getInetAddresses
argument_list|()
init|;
name|addresses
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|InetAddress
name|address
init|=
operator|(
name|InetAddress
operator|)
name|addresses
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|address
operator|.
name|isLoopbackAddress
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
name|address
operator|instanceof
name|Inet4Address
operator|&&
name|ipVersion
operator|==
name|StackType
operator|.
name|IPv4
operator|)
operator|||
operator|(
name|address
operator|instanceof
name|Inet6Address
operator|&&
name|ipVersion
operator|==
name|StackType
operator|.
name|IPv6
operator|)
condition|)
return|return
name|address
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the first address with the proper ipVersion on the given interface on the current host.      *      * @param intf      the interface to be checked      * @param ipVersion Constraint on IP version of address to be returned, 4 or 6      */
DECL|method|getFirstAddress
specifier|public
specifier|static
name|InetAddress
name|getFirstAddress
parameter_list|(
name|NetworkInterface
name|intf
parameter_list|,
name|StackType
name|ipVersion
parameter_list|)
throws|throws
name|SocketException
block|{
if|if
condition|(
name|intf
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Network interface pointer is null"
argument_list|)
throw|;
for|for
control|(
name|Enumeration
name|addresses
init|=
name|intf
operator|.
name|getInetAddresses
argument_list|()
init|;
name|addresses
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|InetAddress
name|address
init|=
operator|(
name|InetAddress
operator|)
name|addresses
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|address
operator|instanceof
name|Inet4Address
operator|&&
name|ipVersion
operator|==
name|StackType
operator|.
name|IPv4
operator|)
operator|||
operator|(
name|address
operator|instanceof
name|Inet6Address
operator|&&
name|ipVersion
operator|==
name|StackType
operator|.
name|IPv6
operator|)
condition|)
return|return
name|address
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * A function to check if an interface supports an IP version (i.e has addresses      * defined for that IP version).      *      * @param intf      * @return      */
DECL|method|interfaceHasIPAddresses
specifier|public
specifier|static
name|boolean
name|interfaceHasIPAddresses
parameter_list|(
name|NetworkInterface
name|intf
parameter_list|,
name|StackType
name|ipVersion
parameter_list|)
throws|throws
name|SocketException
throws|,
name|UnknownHostException
block|{
name|boolean
name|supportsVersion
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|intf
operator|!=
literal|null
condition|)
block|{
comment|// get all the InetAddresses defined on the interface
name|Enumeration
name|addresses
init|=
name|intf
operator|.
name|getInetAddresses
argument_list|()
decl_stmt|;
while|while
condition|(
name|addresses
operator|!=
literal|null
operator|&&
name|addresses
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
comment|// get the next InetAddress for the current interface
name|InetAddress
name|address
init|=
operator|(
name|InetAddress
operator|)
name|addresses
operator|.
name|nextElement
argument_list|()
decl_stmt|;
comment|// check if we find an address of correct version
if|if
condition|(
operator|(
name|address
operator|instanceof
name|Inet4Address
operator|&&
operator|(
name|ipVersion
operator|==
name|StackType
operator|.
name|IPv4
operator|)
operator|)
operator|||
operator|(
name|address
operator|instanceof
name|Inet6Address
operator|&&
operator|(
name|ipVersion
operator|==
name|StackType
operator|.
name|IPv6
operator|)
operator|)
condition|)
block|{
name|supportsVersion
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|UnknownHostException
argument_list|(
literal|"network interface not found"
argument_list|)
throw|;
block|}
return|return
name|supportsVersion
return|;
block|}
comment|/**      * Tries to determine the type of IP stack from the available interfaces and their addresses and from the      * system properties (java.net.preferIPv4Stack and java.net.preferIPv6Addresses)      *      * @return StackType.IPv4 for an IPv4 only stack, StackYTypeIPv6 for an IPv6 only stack, and StackType.Unknown      *         if the type cannot be detected      */
DECL|method|getIpStackType
specifier|public
specifier|static
name|StackType
name|getIpStackType
parameter_list|()
block|{
name|boolean
name|isIPv4StackAvailable
init|=
name|isStackAvailable
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|isIPv6StackAvailable
init|=
name|isStackAvailable
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// if only IPv4 stack available
if|if
condition|(
name|isIPv4StackAvailable
operator|&&
operator|!
name|isIPv6StackAvailable
condition|)
block|{
return|return
name|StackType
operator|.
name|IPv4
return|;
block|}
comment|// if only IPv6 stack available
elseif|else
if|if
condition|(
name|isIPv6StackAvailable
operator|&&
operator|!
name|isIPv4StackAvailable
condition|)
block|{
return|return
name|StackType
operator|.
name|IPv6
return|;
block|}
comment|// if dual stack
elseif|else
if|if
condition|(
name|isIPv4StackAvailable
operator|&&
name|isIPv6StackAvailable
condition|)
block|{
comment|// get the System property which records user preference for a stack on a dual stack machine
if|if
condition|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|IPv4_SETTING
argument_list|)
condition|)
comment|// has preference over java.net.preferIPv6Addresses
return|return
name|StackType
operator|.
name|IPv4
return|;
if|if
condition|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|IPv6_SETTING
argument_list|)
condition|)
return|return
name|StackType
operator|.
name|IPv6
return|;
return|return
name|StackType
operator|.
name|IPv6
return|;
block|}
return|return
name|StackType
operator|.
name|Unknown
return|;
block|}
DECL|method|isStackAvailable
specifier|public
specifier|static
name|boolean
name|isStackAvailable
parameter_list|(
name|boolean
name|ipv4
parameter_list|)
block|{
name|Collection
argument_list|<
name|InetAddress
argument_list|>
name|allAddrs
init|=
name|getAllAvailableAddresses
argument_list|()
decl_stmt|;
for|for
control|(
name|InetAddress
name|addr
range|:
name|allAddrs
control|)
if|if
condition|(
name|ipv4
operator|&&
name|addr
operator|instanceof
name|Inet4Address
operator|||
operator|(
operator|!
name|ipv4
operator|&&
name|addr
operator|instanceof
name|Inet6Address
operator|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/**      * Returns all the available interfaces, including first level sub interfaces.      */
DECL|method|getAllAvailableInterfaces
specifier|public
specifier|static
name|List
argument_list|<
name|NetworkInterface
argument_list|>
name|getAllAvailableInterfaces
parameter_list|()
throws|throws
name|SocketException
block|{
name|List
argument_list|<
name|NetworkInterface
argument_list|>
name|allInterfaces
init|=
operator|new
name|ArrayList
argument_list|<
name|NetworkInterface
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|interfaces
init|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
init|;
name|interfaces
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|NetworkInterface
name|intf
init|=
name|interfaces
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|allInterfaces
operator|.
name|add
argument_list|(
name|intf
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|subInterfaces
init|=
name|intf
operator|.
name|getSubInterfaces
argument_list|()
decl_stmt|;
if|if
condition|(
name|subInterfaces
operator|!=
literal|null
operator|&&
name|subInterfaces
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
while|while
condition|(
name|subInterfaces
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|allInterfaces
operator|.
name|add
argument_list|(
name|subInterfaces
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|allInterfaces
return|;
block|}
DECL|method|getAllAvailableAddresses
specifier|public
specifier|static
name|Collection
argument_list|<
name|InetAddress
argument_list|>
name|getAllAvailableAddresses
parameter_list|()
block|{
name|Set
argument_list|<
name|InetAddress
argument_list|>
name|retval
init|=
operator|new
name|HashSet
argument_list|<
name|InetAddress
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
name|en
decl_stmt|;
try|try
block|{
name|en
operator|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
expr_stmt|;
if|if
condition|(
name|en
operator|==
literal|null
condition|)
return|return
name|retval
return|;
while|while
condition|(
name|en
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|NetworkInterface
name|intf
init|=
operator|(
name|NetworkInterface
operator|)
name|en
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|InetAddress
argument_list|>
name|addrs
init|=
name|intf
operator|.
name|getInetAddresses
argument_list|()
decl_stmt|;
while|while
condition|(
name|addrs
operator|.
name|hasMoreElements
argument_list|()
condition|)
name|retval
operator|.
name|add
argument_list|(
name|addrs
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to derive all available interfaces"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
DECL|method|NetworkUtils
specifier|private
name|NetworkUtils
parameter_list|()
block|{      }
block|}
end_class

end_unit

