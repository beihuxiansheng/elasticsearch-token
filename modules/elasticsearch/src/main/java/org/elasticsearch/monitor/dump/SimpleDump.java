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
name|io
operator|.
name|FileNotFoundException
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

begin_class
DECL|class|SimpleDump
specifier|public
class|class
name|SimpleDump
extends|extends
name|AbstractDump
block|{
DECL|field|location
specifier|private
specifier|final
name|File
name|location
decl_stmt|;
DECL|method|SimpleDump
specifier|public
name|SimpleDump
parameter_list|(
name|long
name|timestamp
parameter_list|,
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
name|File
name|location
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|super
argument_list|(
name|timestamp
argument_list|,
name|cause
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
DECL|method|doCreateFile
annotation|@
name|Override
specifier|protected
name|File
name|doCreateFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|DumpException
block|{
return|return
operator|new
name|File
argument_list|(
name|location
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|finish
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|DumpException
block|{      }
block|}
end_class

end_unit

