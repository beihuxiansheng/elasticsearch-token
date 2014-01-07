begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
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
comment|/**  */
end_comment

begin_interface
DECL|interface|DirectoryService
specifier|public
interface|interface
name|DirectoryService
block|{
DECL|method|build
name|Directory
index|[]
name|build
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|throttleTimeInNanos
name|long
name|throttleTimeInNanos
parameter_list|()
function_decl|;
DECL|method|renameFile
name|void
name|renameFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|fullDelete
name|void
name|fullDelete
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

