begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.jna
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|jna
package|;
end_package

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jna
operator|.
name|Native
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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Natives
specifier|public
class|class
name|Natives
block|{
DECL|field|logger
specifier|private
specifier|static
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Natives
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Set to true, in case native mlockall call was successful
DECL|field|LOCAL_MLOCKALL
specifier|public
specifier|static
name|boolean
name|LOCAL_MLOCKALL
init|=
literal|false
decl_stmt|;
DECL|method|tryMlockall
specifier|public
specifier|static
name|void
name|tryMlockall
parameter_list|()
block|{
name|int
name|errno
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|CLibrary
operator|.
name|mlockall
argument_list|(
name|CLibrary
operator|.
name|MCL_CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
name|errno
operator|=
name|Native
operator|.
name|getLastError
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOCAL_MLOCKALL
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsatisfiedLinkError
name|e
parameter_list|)
block|{
comment|// this will have already been logged by CLibrary, no need to repeat it
return|return;
block|}
if|if
condition|(
name|errno
operator|!=
name|Integer
operator|.
name|MIN_VALUE
condition|)
block|{
if|if
condition|(
name|errno
operator|==
name|CLibrary
operator|.
name|ENOMEM
operator|&&
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|contains
argument_list|(
literal|"linux"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Unable to lock JVM memory (ENOMEM)."
operator|+
literal|" This can result in part of the JVM being swapped out."
operator|+
literal|" Increase RLIMIT_MEMLOCK or run elasticsearch as root."
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|contains
argument_list|(
literal|"mac"
argument_list|)
condition|)
block|{
comment|// OS X allows mlockall to be called, but always returns an error
name|logger
operator|.
name|warn
argument_list|(
literal|"Unknown mlockall error "
operator|+
name|errno
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

