begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.thread
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|thread
package|;
end_package

begin_comment
comment|/**  * Overrides the thread name proposed by {@link ThreadRenamingRunnable}.  *  *  */
end_comment

begin_interface
DECL|interface|ThreadNameDeterminer
specifier|public
interface|interface
name|ThreadNameDeterminer
block|{
comment|/**      * {@link ThreadNameDeterminer} that accepts the proposed thread name      * as is.      */
DECL|field|PROPOSED
name|ThreadNameDeterminer
name|PROPOSED
init|=
operator|new
name|ThreadNameDeterminer
argument_list|()
block|{
specifier|public
name|String
name|determineThreadName
parameter_list|(
name|String
name|currentThreadName
parameter_list|,
name|String
name|proposedThreadName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|proposedThreadName
return|;
block|}
block|}
decl_stmt|;
comment|/**      * {@link ThreadNameDeterminer} that rejects the proposed thread name and      * retains the current one.      */
DECL|field|CURRENT
name|ThreadNameDeterminer
name|CURRENT
init|=
operator|new
name|ThreadNameDeterminer
argument_list|()
block|{
specifier|public
name|String
name|determineThreadName
parameter_list|(
name|String
name|currentThreadName
parameter_list|,
name|String
name|proposedThreadName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Overrides the thread name proposed by {@link ThreadRenamingRunnable}.      *      * @param currentThreadName  the current thread name      * @param proposedThreadName the proposed new thread name      * @return the actual new thread name.      *         If {@code null} is returned, the proposed thread name is      *         discarded (i.e. no rename).      */
DECL|method|determineThreadName
name|String
name|determineThreadName
parameter_list|(
name|String
name|currentThreadName
parameter_list|,
name|String
name|proposedThreadName
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

