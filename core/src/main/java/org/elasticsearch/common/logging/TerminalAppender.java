begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|AppenderSkeleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cli
operator|.
name|Terminal
import|;
end_import

begin_comment
comment|/**  * TerminalAppender logs event to Terminal.DEFAULT. It is used for example by the PluginCli.  * */
end_comment

begin_class
DECL|class|TerminalAppender
specifier|public
class|class
name|TerminalAppender
extends|extends
name|AppenderSkeleton
block|{
annotation|@
name|Override
DECL|method|append
specifier|protected
name|void
name|append
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
name|Terminal
operator|.
name|DEFAULT
operator|.
name|println
argument_list|(
name|event
operator|.
name|getRenderedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|requiresLayout
specifier|public
name|boolean
name|requiresLayout
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

