begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
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
name|AbstractIndexComponent
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
name|Index
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
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|none
operator|.
name|NoneFieldDataCache
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
name|filter
operator|.
name|FilterCache
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
name|filter
operator|.
name|none
operator|.
name|NoneFilterCache
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
name|settings
operator|.
name|IndexSettings
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
name|inject
operator|.
name|Inject
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexCache
specifier|public
class|class
name|IndexCache
extends|extends
name|AbstractIndexComponent
block|{
DECL|field|filterCache
specifier|private
specifier|final
name|FilterCache
name|filterCache
decl_stmt|;
DECL|field|fieldDataCache
specifier|private
specifier|final
name|FieldDataCache
name|fieldDataCache
decl_stmt|;
DECL|method|IndexCache
specifier|public
name|IndexCache
parameter_list|(
name|Index
name|index
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|EMPTY_SETTINGS
argument_list|,
operator|new
name|NoneFilterCache
argument_list|(
name|index
argument_list|,
name|EMPTY_SETTINGS
argument_list|)
argument_list|,
operator|new
name|NoneFieldDataCache
argument_list|(
name|index
argument_list|,
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexCache
annotation|@
name|Inject
specifier|public
name|IndexCache
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FilterCache
name|filterCache
parameter_list|,
name|FieldDataCache
name|fieldDataCache
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|filterCache
operator|=
name|filterCache
expr_stmt|;
name|this
operator|.
name|fieldDataCache
operator|=
name|fieldDataCache
expr_stmt|;
block|}
DECL|method|filter
specifier|public
name|FilterCache
name|filter
parameter_list|()
block|{
return|return
name|filterCache
return|;
block|}
DECL|method|fieldData
specifier|public
name|FieldDataCache
name|fieldData
parameter_list|()
block|{
return|return
name|fieldDataCache
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|filterCache
operator|.
name|clear
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|fieldDataCache
operator|.
name|clear
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|filterCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fieldDataCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|clearUnreferenced
specifier|public
name|void
name|clearUnreferenced
parameter_list|()
block|{
name|filterCache
operator|.
name|clearUnreferenced
argument_list|()
expr_stmt|;
name|fieldDataCache
operator|.
name|clearUnreferenced
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

