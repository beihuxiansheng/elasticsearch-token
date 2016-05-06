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
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * Pool of connections to the different hosts that belong to an elasticsearch cluster.  * It keeps track of the different hosts to communicate with and allows to retrieve a stream of connections to be used  * for each request. Exposes the needed hooks to be able to eventually mark connections dead or alive and execute  * arbitrary operations before each single request attempt.  */
end_comment

begin_interface
DECL|interface|ConnectionPool
specifier|public
interface|interface
name|ConnectionPool
extends|extends
name|Closeable
block|{
comment|/**      * Returns a stream of connections that should be used for a request call.      * Ideally, the first connection is retrieved from the stream and used successfully for the request.      * Otherwise, after each failure the next connection should be retrieved from the stream so that the request can be retried.      * The maximum total of attempts is equal to the number of connections that are available in the stream.      * It may happen that the stream is empty, in which case it means that there aren't healthy connections to use.      * Then {@link #lastResortConnection()} should be called to retrieve a non healthy connection and try it.      */
DECL|method|nextConnection
name|Stream
argument_list|<
name|Connection
argument_list|>
name|nextConnection
parameter_list|()
function_decl|;
comment|/**      * Returns a connection that is not necessarily healthy, but can be used for a request attempt. To be called as last resort      * only in case {@link #nextConnection()} returns an empty stream      */
DECL|method|lastResortConnection
name|Connection
name|lastResortConnection
parameter_list|()
function_decl|;
comment|/**      * Called before each single request attempt. Allows to execute operations (e.g. ping) before each request.      * Receives as an argument the connection that is going to be used for the request.      */
DECL|method|beforeAttempt
name|void
name|beforeAttempt
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Called after each successful request call.      * Receives as an argument the connection that was used for the successful request.      */
DECL|method|onSuccess
name|void
name|onSuccess
parameter_list|(
name|Connection
name|connection
parameter_list|)
function_decl|;
comment|/**      * Called after each failed attempt.      * Receives as an argument the connection that was used for the failed attempt.      */
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

