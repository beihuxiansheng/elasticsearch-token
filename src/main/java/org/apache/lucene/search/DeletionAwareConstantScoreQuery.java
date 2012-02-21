begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|lucene
operator|.
name|search
operator|.
name|NotDeletedFilter
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_comment
comment|// LUCENE MONITOR: Against ConstantScoreQuery, basically added logic in the doc iterator to take deletions into account
end_comment

begin_comment
comment|// So it can basically be cached safely even with a reader that changes deletions but remain with teh same cache key
end_comment

begin_comment
comment|// See more: https://issues.apache.org/jira/browse/LUCENE-2468
end_comment

begin_comment
comment|// TODO Lucene 4.0 won't need this, since live docs are "and'ed" while scoring
end_comment

begin_class
DECL|class|DeletionAwareConstantScoreQuery
specifier|public
class|class
name|DeletionAwareConstantScoreQuery
extends|extends
name|ConstantScoreQuery
block|{
DECL|field|actualFilter
specifier|private
specifier|final
name|Filter
name|actualFilter
decl_stmt|;
DECL|method|DeletionAwareConstantScoreQuery
specifier|public
name|DeletionAwareConstantScoreQuery
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|NotDeletedFilter
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|actualFilter
operator|=
name|filter
expr_stmt|;
block|}
comment|// trick so any external systems still think that its the actual filter we use, and not the
comment|// deleted filter
annotation|@
name|Override
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|this
operator|.
name|actualFilter
return|;
block|}
block|}
end_class

end_unit

