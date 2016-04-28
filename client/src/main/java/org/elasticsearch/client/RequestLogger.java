begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|RequestLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|StatusLine
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Helper class that exposes static method to unify the way requests are logged  */
end_comment

begin_class
DECL|class|RequestLogger
specifier|public
specifier|final
class|class
name|RequestLogger
block|{
DECL|method|RequestLogger
specifier|private
name|RequestLogger
parameter_list|()
block|{     }
comment|/**      * Logs a request that yielded a response      */
DECL|method|log
specifier|public
specifier|static
name|void
name|log
parameter_list|(
name|Log
name|logger
parameter_list|,
name|String
name|message
parameter_list|,
name|RequestLine
name|requestLine
parameter_list|,
name|Node
name|node
parameter_list|,
name|StatusLine
name|statusLine
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|message
operator|+
literal|" ["
operator|+
name|requestLine
operator|.
name|getMethod
argument_list|()
operator|+
literal|" "
operator|+
name|node
operator|.
name|getHttpHost
argument_list|()
operator|+
name|requestLine
operator|.
name|getUri
argument_list|()
operator|+
literal|"] ["
operator|+
name|statusLine
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Logs a request that failed      */
DECL|method|log
specifier|public
specifier|static
name|void
name|log
parameter_list|(
name|Log
name|logger
parameter_list|,
name|String
name|message
parameter_list|,
name|RequestLine
name|requestLine
parameter_list|,
name|Node
name|node
parameter_list|,
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|message
operator|+
literal|" ["
operator|+
name|requestLine
operator|.
name|getMethod
argument_list|()
operator|+
literal|" "
operator|+
name|node
operator|.
name|getHttpHost
argument_list|()
operator|+
name|requestLine
operator|.
name|getUri
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

