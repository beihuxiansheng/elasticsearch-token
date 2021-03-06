begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
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
name|rest
operator|.
name|BytesRestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestChannel
import|;
end_import

begin_comment
comment|/**  * An action listener that requires {@link #processResponse(Object)} to be implemented  * and will automatically handle failures.  */
end_comment

begin_class
DECL|class|RestActionListener
specifier|public
specifier|abstract
class|class
name|RestActionListener
parameter_list|<
name|Response
parameter_list|>
implements|implements
name|ActionListener
argument_list|<
name|Response
argument_list|>
block|{
comment|// we use static here so we won't have to pass the actual logger each time for a very rare case of logging
comment|// where the settings don't matter that much
DECL|field|logger
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|RestResponseListener
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|channel
specifier|protected
specifier|final
name|RestChannel
name|channel
decl_stmt|;
DECL|method|RestActionListener
specifier|protected
name|RestActionListener
parameter_list|(
name|RestChannel
name|channel
parameter_list|)
block|{
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onResponse
specifier|public
specifier|final
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
try|try
block|{
name|processResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processResponse
specifier|protected
specifier|abstract
name|void
name|processResponse
parameter_list|(
name|Response
name|response
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
DECL|method|onFailure
specifier|public
specifier|final
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|inner
operator|.
name|addSuppressed
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"failed to send failure response"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

