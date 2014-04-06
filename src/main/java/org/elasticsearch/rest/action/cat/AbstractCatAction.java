begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|Table
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
name|io
operator|.
name|UTF8StreamWriter
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|rest
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestTable
operator|.
name|buildHelpWidths
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestTable
operator|.
name|pad
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractCatAction
specifier|public
specifier|abstract
class|class
name|AbstractCatAction
extends|extends
name|BaseRestHandler
block|{
DECL|method|AbstractCatAction
specifier|public
name|AbstractCatAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|doRequest
specifier|abstract
name|void
name|doRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|)
function_decl|;
DECL|method|documentation
specifier|abstract
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
function_decl|;
DECL|method|getTableWithHeader
specifier|abstract
name|Table
name|getTableWithHeader
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|helpWanted
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"help"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|helpWanted
condition|)
block|{
name|Table
name|table
init|=
name|getTableWithHeader
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|int
index|[]
name|width
init|=
name|buildHelpWidths
argument_list|(
name|table
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|bytesOutput
init|=
name|channel
operator|.
name|bytesOutput
argument_list|()
decl_stmt|;
name|UTF8StreamWriter
name|out
init|=
operator|new
name|UTF8StreamWriter
argument_list|()
operator|.
name|setOutput
argument_list|(
name|bytesOutput
argument_list|)
decl_stmt|;
for|for
control|(
name|Table
operator|.
name|Cell
name|cell
range|:
name|table
operator|.
name|getHeaders
argument_list|()
control|)
block|{
comment|// need to do left-align always, so create new cells
name|pad
argument_list|(
operator|new
name|Table
operator|.
name|Cell
argument_list|(
name|cell
operator|.
name|value
argument_list|)
argument_list|,
name|width
index|[
literal|0
index|]
argument_list|,
name|request
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|" | "
argument_list|)
expr_stmt|;
name|pad
argument_list|(
operator|new
name|Table
operator|.
name|Cell
argument_list|(
name|cell
operator|.
name|attr
operator|.
name|containsKey
argument_list|(
literal|"alias"
argument_list|)
condition|?
name|cell
operator|.
name|attr
operator|.
name|get
argument_list|(
literal|"alias"
argument_list|)
else|:
literal|""
argument_list|)
argument_list|,
name|width
index|[
literal|1
index|]
argument_list|,
name|request
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|" | "
argument_list|)
expr_stmt|;
name|pad
argument_list|(
operator|new
name|Table
operator|.
name|Cell
argument_list|(
name|cell
operator|.
name|attr
operator|.
name|containsKey
argument_list|(
literal|"desc"
argument_list|)
condition|?
name|cell
operator|.
name|attr
operator|.
name|get
argument_list|(
literal|"desc"
argument_list|)
else|:
literal|"not available"
argument_list|)
argument_list|,
name|width
index|[
literal|2
index|]
argument_list|,
name|request
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
name|BytesRestResponse
operator|.
name|TEXT_CONTENT_TYPE
argument_list|,
name|bytesOutput
operator|.
name|bytes
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

