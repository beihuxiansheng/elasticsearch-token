begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.logging
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|jdk
operator|.
name|JdkESLoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Log4jESLoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|slf4j
operator|.
name|Slf4jESLoggerFactory
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ESLoggerFactory
specifier|public
specifier|abstract
class|class
name|ESLoggerFactory
block|{
DECL|field|defaultFactory
specifier|private
specifier|static
specifier|volatile
name|ESLoggerFactory
name|defaultFactory
init|=
operator|new
name|JdkESLoggerFactory
argument_list|()
decl_stmt|;
static|static
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.slf4j.Logger"
argument_list|)
expr_stmt|;
name|defaultFactory
operator|=
operator|new
name|Slf4jESLoggerFactory
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// no slf4j
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.log4j.Logger"
argument_list|)
expr_stmt|;
name|defaultFactory
operator|=
operator|new
name|Log4jESLoggerFactory
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e1
parameter_list|)
block|{
comment|// no log4j
block|}
block|}
block|}
comment|/**      * Changes the default factory.      */
DECL|method|setDefaultFactory
specifier|public
specifier|static
name|void
name|setDefaultFactory
parameter_list|(
name|ESLoggerFactory
name|defaultFactory
parameter_list|)
block|{
if|if
condition|(
name|defaultFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"defaultFactory"
argument_list|)
throw|;
block|}
name|ESLoggerFactory
operator|.
name|defaultFactory
operator|=
name|defaultFactory
expr_stmt|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|ESLogger
name|getLogger
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|defaultFactory
operator|.
name|newInstance
argument_list|(
name|prefix
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|getLogger
specifier|public
specifier|static
name|ESLogger
name|getLogger
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|defaultFactory
operator|.
name|newInstance
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|newInstance
specifier|public
name|ESLogger
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
literal|null
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|newInstance
specifier|public
specifier|abstract
name|ESLogger
name|newInstance
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_class

end_unit

