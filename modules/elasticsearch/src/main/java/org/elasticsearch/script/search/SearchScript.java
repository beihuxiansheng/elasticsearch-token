begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|search
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|FieldDataCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ExecutableScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SearchScript
specifier|public
class|class
name|SearchScript
block|{
DECL|field|searchLookup
specifier|private
specifier|final
name|ScriptSearchLookup
name|searchLookup
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|ExecutableScript
name|script
decl_stmt|;
DECL|method|SearchScript
specifier|public
name|SearchScript
parameter_list|(
name|ScriptSearchLookup
name|searchLookup
parameter_list|,
name|ExecutableScript
name|script
parameter_list|)
block|{
name|this
operator|.
name|searchLookup
operator|=
name|searchLookup
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
DECL|method|SearchScript
specifier|public
name|SearchScript
parameter_list|(
name|ScriptSearchLookup
name|searchLookup
parameter_list|,
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|)
block|{
name|this
operator|.
name|searchLookup
operator|=
name|searchLookup
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|scriptService
operator|.
name|executable
argument_list|(
name|lang
argument_list|,
name|script
argument_list|,
name|searchLookup
operator|.
name|processScriptParams
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|SearchScript
specifier|public
name|SearchScript
parameter_list|(
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|FieldDataCache
name|fieldDataCache
parameter_list|)
block|{
name|this
operator|.
name|searchLookup
operator|=
operator|new
name|ScriptSearchLookup
argument_list|(
name|mapperService
argument_list|,
name|fieldDataCache
argument_list|)
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|scriptService
operator|.
name|executable
argument_list|(
name|lang
argument_list|,
name|script
argument_list|,
name|searchLookup
operator|.
name|processScriptParams
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|searchLookup
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|execute
specifier|public
name|Object
name|execute
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|searchLookup
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|script
operator|.
name|run
argument_list|()
return|;
block|}
DECL|method|execute
specifier|public
name|Object
name|execute
parameter_list|(
name|int
name|docId
parameter_list|,
name|Map
name|params
parameter_list|)
block|{
name|searchLookup
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|script
operator|.
name|run
argument_list|(
name|params
argument_list|)
return|;
block|}
block|}
end_class

end_unit

