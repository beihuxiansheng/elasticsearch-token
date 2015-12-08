begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|java
operator|.
name|net
operator|.
name|NetworkInterface
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_class
DECL|class|MacAddressProvider
specifier|public
class|class
name|MacAddressProvider
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|MacAddressProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getMacAddress
specifier|private
specifier|static
name|byte
index|[]
name|getMacAddress
parameter_list|()
throws|throws
name|SocketException
block|{
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|en
init|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
decl_stmt|;
if|if
condition|(
name|en
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|en
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|NetworkInterface
name|nint
init|=
name|en
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|nint
operator|.
name|isLoopback
argument_list|()
condition|)
block|{
comment|// Pick the first valid non loopback address we find
name|byte
index|[]
name|address
init|=
name|nint
operator|.
name|getHardwareAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|isValidAddress
argument_list|(
name|address
argument_list|)
condition|)
block|{
return|return
name|address
return|;
block|}
block|}
block|}
block|}
comment|// Could not find a mac address
return|return
literal|null
return|;
block|}
DECL|method|isValidAddress
specifier|private
specifier|static
name|boolean
name|isValidAddress
parameter_list|(
name|byte
index|[]
name|address
parameter_list|)
block|{
if|if
condition|(
name|address
operator|==
literal|null
operator|||
name|address
operator|.
name|length
operator|!=
literal|6
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|byte
name|b
range|:
name|address
control|)
block|{
if|if
condition|(
name|b
operator|!=
literal|0x00
condition|)
block|{
return|return
literal|true
return|;
comment|// If any of the bytes are non zero assume a good address
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|getSecureMungedAddress
specifier|public
specifier|static
name|byte
index|[]
name|getSecureMungedAddress
parameter_list|()
block|{
name|byte
index|[]
name|address
init|=
literal|null
decl_stmt|;
try|try
block|{
name|address
operator|=
name|getMacAddress
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Unable to get mac address, will use a dummy address"
argument_list|,
name|t
argument_list|)
expr_stmt|;
comment|// address will be set below
block|}
if|if
condition|(
operator|!
name|isValidAddress
argument_list|(
name|address
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Unable to get a valid mac address, will use a dummy address"
argument_list|)
expr_stmt|;
name|address
operator|=
name|constructDummyMulticastAddress
argument_list|()
expr_stmt|;
block|}
name|byte
index|[]
name|mungedBytes
init|=
operator|new
name|byte
index|[
literal|6
index|]
decl_stmt|;
name|SecureRandomHolder
operator|.
name|INSTANCE
operator|.
name|nextBytes
argument_list|(
name|mungedBytes
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6
condition|;
operator|++
name|i
control|)
block|{
name|mungedBytes
index|[
name|i
index|]
operator|^=
name|address
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|mungedBytes
return|;
block|}
DECL|method|constructDummyMulticastAddress
specifier|private
specifier|static
name|byte
index|[]
name|constructDummyMulticastAddress
parameter_list|()
block|{
name|byte
index|[]
name|dummy
init|=
operator|new
name|byte
index|[
literal|6
index|]
decl_stmt|;
name|SecureRandomHolder
operator|.
name|INSTANCE
operator|.
name|nextBytes
argument_list|(
name|dummy
argument_list|)
expr_stmt|;
comment|/*          * Set the broadcast bit to indicate this is not a _real_ mac address          */
name|dummy
index|[
literal|0
index|]
operator||=
operator|(
name|byte
operator|)
literal|0x01
expr_stmt|;
return|return
name|dummy
return|;
block|}
block|}
end_class

end_unit

