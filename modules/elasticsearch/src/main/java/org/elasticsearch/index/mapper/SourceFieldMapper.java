begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|FieldSelector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|StringHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadSafe
import|;
end_import

begin_comment
comment|/**  * A mapper that maps the actual source of a generated document.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
annotation|@
name|ThreadSafe
DECL|interface|SourceFieldMapper
specifier|public
interface|interface
name|SourceFieldMapper
extends|extends
name|FieldMapper
argument_list|<
name|String
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|final
name|String
name|NAME
init|=
name|StringHelper
operator|.
name|intern
argument_list|(
literal|"_source"
argument_list|)
decl_stmt|;
comment|/**      * Returns<tt>true</tt> if the source field mapper is enabled or not.      */
DECL|method|enabled
name|boolean
name|enabled
parameter_list|()
function_decl|;
DECL|method|value
name|String
name|value
parameter_list|(
name|Document
name|document
parameter_list|)
function_decl|;
comment|/**      * A field selector that loads just the source field.      */
DECL|method|fieldSelector
name|FieldSelector
name|fieldSelector
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

