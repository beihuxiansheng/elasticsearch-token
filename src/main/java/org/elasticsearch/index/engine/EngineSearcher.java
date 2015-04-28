begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|SearcherManager
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
name|store
operator|.
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|IllegalStateException
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
name|logging
operator|.
name|ESLogger
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
name|store
operator|.
name|Store
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Searcher for an Engine  */
end_comment

begin_class
DECL|class|EngineSearcher
specifier|public
class|class
name|EngineSearcher
extends|extends
name|Engine
operator|.
name|Searcher
block|{
DECL|field|manager
specifier|private
specifier|final
name|SearcherManager
name|manager
decl_stmt|;
DECL|field|released
specifier|private
specifier|final
name|AtomicBoolean
name|released
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|method|EngineSearcher
specifier|public
name|EngineSearcher
parameter_list|(
name|String
name|source
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|SearcherManager
name|manager
parameter_list|,
name|Store
name|store
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
if|if
condition|(
operator|!
name|released
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|/* In general, searchers should never be released twice or this would break reference counting. There is one rare case                  * when it might happen though: when the request and the Reaper thread would both try to release it in a very short amount                  * of time, this is why we only log a warning instead of throwing an exception.                  */
name|logger
operator|.
name|warn
argument_list|(
literal|"Searcher was released twice"
argument_list|,
operator|new
name|IllegalStateException
argument_list|(
literal|"Double release"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|manager
operator|.
name|release
argument_list|(
name|this
operator|.
name|searcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot close"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|/* this one can happen if we already closed the                  * underlying store / directory and we call into the                  * IndexWriter to free up pending files. */
block|}
finally|finally
block|{
name|store
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

