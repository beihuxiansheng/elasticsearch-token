begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|util
operator|.
name|IOUtils
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
name|common
operator|.
name|settings
operator|.
name|Settings
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
name|bitset
operator|.
name|BitsetFilterCache
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
name|query
operator|.
name|QueryCache
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndexCache
specifier|public
class|class
name|IndexCache
extends|extends
name|AbstractIndexComponent
implements|implements
name|Closeable
block|{
DECL|field|queryCache
specifier|private
specifier|final
name|QueryCache
name|queryCache
decl_stmt|;
DECL|field|bitsetFilterCache
specifier|private
specifier|final
name|BitsetFilterCache
name|bitsetFilterCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndexCache
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
name|QueryCache
name|queryCache
parameter_list|,
name|BitsetFilterCache
name|bitsetFilterCache
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
name|queryCache
operator|=
name|queryCache
expr_stmt|;
name|this
operator|.
name|bitsetFilterCache
operator|=
name|bitsetFilterCache
expr_stmt|;
block|}
DECL|method|query
specifier|public
name|QueryCache
name|query
parameter_list|()
block|{
return|return
name|queryCache
return|;
block|}
comment|/**      * Return the {@link BitsetFilterCache} for this index.      */
DECL|method|bitsetFilterCache
specifier|public
name|BitsetFilterCache
name|bitsetFilterCache
parameter_list|()
block|{
return|return
name|bitsetFilterCache
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|queryCache
argument_list|,
name|bitsetFilterCache
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|queryCache
operator|.
name|clear
argument_list|(
name|reason
argument_list|)
expr_stmt|;
name|bitsetFilterCache
operator|.
name|clear
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

