begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
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
name|util
operator|.
name|Accountable
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
name|lease
operator|.
name|Releasable
import|;
end_import

begin_comment
comment|/**  * The thread safe {@link org.apache.lucene.index.LeafReader} level cache of the data.  */
end_comment

begin_interface
DECL|interface|AtomicFieldData
specifier|public
interface|interface
name|AtomicFieldData
extends|extends
name|Accountable
extends|,
name|Releasable
block|{
comment|/**      * Returns a "scripting" based values.      */
DECL|method|getScriptValues
name|ScriptDocValues
argument_list|<
name|?
argument_list|>
name|getScriptValues
parameter_list|()
function_decl|;
comment|/**      * Return a String representation of the values.      */
DECL|method|getBytesValues
name|SortedBinaryDocValues
name|getBytesValues
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

