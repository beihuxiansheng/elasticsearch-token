begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.dump
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|dump
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
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|DumpGenerator
specifier|public
interface|interface
name|DumpGenerator
block|{
DECL|method|generateDump
name|Result
name|generateDump
parameter_list|(
name|String
name|cause
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
parameter_list|)
throws|throws
name|DumpGenerationFailedException
function_decl|;
DECL|method|generateDump
name|Result
name|generateDump
parameter_list|(
name|String
name|cause
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
parameter_list|,
name|String
modifier|...
name|contributors
parameter_list|)
throws|throws
name|DumpGenerationFailedException
function_decl|;
DECL|class|Result
specifier|static
class|class
name|Result
block|{
DECL|field|location
specifier|private
specifier|final
name|File
name|location
decl_stmt|;
DECL|field|failedContributors
specifier|private
name|Iterable
argument_list|<
name|DumpContributionFailedException
argument_list|>
name|failedContributors
decl_stmt|;
DECL|method|Result
specifier|public
name|Result
parameter_list|(
name|File
name|location
parameter_list|,
name|Iterable
argument_list|<
name|DumpContributionFailedException
argument_list|>
name|failedContributors
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|failedContributors
operator|=
name|failedContributors
expr_stmt|;
block|}
DECL|method|location
specifier|public
name|String
name|location
parameter_list|()
block|{
return|return
name|location
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|failedContributors
specifier|public
name|Iterable
argument_list|<
name|DumpContributionFailedException
argument_list|>
name|failedContributors
parameter_list|()
block|{
return|return
name|failedContributors
return|;
block|}
block|}
block|}
end_interface

end_unit

