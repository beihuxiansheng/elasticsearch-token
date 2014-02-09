begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging.jdk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|jdk
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
name|ESLoggerFactory
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|JdkESLoggerFactory
specifier|public
class|class
name|JdkESLoggerFactory
extends|extends
name|ESLoggerFactory
block|{
annotation|@
name|Override
DECL|method|rootLogger
specifier|protected
name|ESLogger
name|rootLogger
parameter_list|()
block|{
return|return
name|getLogger
argument_list|(
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newInstance
specifier|protected
name|ESLogger
name|newInstance
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|name
parameter_list|)
block|{
specifier|final
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
name|logger
init|=
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|JdkESLogger
argument_list|(
name|prefix
argument_list|,
name|logger
argument_list|)
return|;
block|}
block|}
end_class

end_unit

