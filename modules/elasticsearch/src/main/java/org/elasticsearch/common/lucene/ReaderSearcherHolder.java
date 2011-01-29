begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
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
name|ElasticSearchException
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|ExtendedIndexSearcher
import|;
end_import

begin_comment
comment|/**  * A very simple holder for a tuple of reader and searcher.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|ReaderSearcherHolder
specifier|public
class|class
name|ReaderSearcherHolder
implements|implements
name|Releasable
block|{
DECL|field|indexSearcher
specifier|private
specifier|final
name|ExtendedIndexSearcher
name|indexSearcher
decl_stmt|;
DECL|method|ReaderSearcherHolder
specifier|public
name|ReaderSearcherHolder
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ExtendedIndexSearcher
argument_list|(
name|indexReader
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ReaderSearcherHolder
specifier|public
name|ReaderSearcherHolder
parameter_list|(
name|ExtendedIndexSearcher
name|indexSearcher
parameter_list|)
block|{
name|this
operator|.
name|indexSearcher
operator|=
name|indexSearcher
expr_stmt|;
block|}
DECL|method|reader
specifier|public
name|IndexReader
name|reader
parameter_list|()
block|{
return|return
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
return|;
block|}
DECL|method|searcher
specifier|public
name|ExtendedIndexSearcher
name|searcher
parameter_list|()
block|{
return|return
name|indexSearcher
return|;
block|}
DECL|method|release
annotation|@
name|Override
specifier|public
name|boolean
name|release
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
try|try
block|{
name|indexSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
try|try
block|{
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

