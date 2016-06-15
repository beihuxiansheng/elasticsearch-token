begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.profile.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
operator|.
name|query
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
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
operator|.
name|AbstractInternalProfileTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|profile
operator|.
name|ProfileResult
import|;
end_import

begin_comment
comment|/**  * This class tracks the dependency tree for queries (scoring and rewriting) and  * generates {@link QueryProfileBreakdown} for each node in the tree.  It also finalizes the tree  * and returns a list of {@link ProfileResult} that can be serialized back to the client  */
end_comment

begin_class
DECL|class|InternalQueryProfileTree
specifier|final
class|class
name|InternalQueryProfileTree
extends|extends
name|AbstractInternalProfileTree
argument_list|<
name|QueryProfileBreakdown
argument_list|,
name|Query
argument_list|>
block|{
comment|/** Rewrite time */
DECL|field|rewriteTime
specifier|private
name|long
name|rewriteTime
decl_stmt|;
DECL|field|rewriteScratch
specifier|private
name|long
name|rewriteScratch
decl_stmt|;
annotation|@
name|Override
DECL|method|createProfileBreakdown
specifier|protected
name|QueryProfileBreakdown
name|createProfileBreakdown
parameter_list|()
block|{
return|return
operator|new
name|QueryProfileBreakdown
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTypeFromElement
specifier|protected
name|String
name|getTypeFromElement
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
return|return
name|query
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDescriptionFromElement
specifier|protected
name|String
name|getDescriptionFromElement
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
return|return
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Begin timing a query for a specific Timing context      */
DECL|method|startRewriteTime
specifier|public
name|void
name|startRewriteTime
parameter_list|()
block|{
assert|assert
name|rewriteScratch
operator|==
literal|0
assert|;
name|rewriteScratch
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
comment|/**      * Halt the timing process and add the elapsed rewriting time.      * startRewriteTime() must be called for a particular context prior to calling      * stopAndAddRewriteTime(), otherwise the elapsed time will be negative and      * nonsensical      *      * @return          The elapsed time      */
DECL|method|stopAndAddRewriteTime
specifier|public
name|long
name|stopAndAddRewriteTime
parameter_list|()
block|{
name|long
name|time
init|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|rewriteScratch
argument_list|)
decl_stmt|;
name|rewriteTime
operator|+=
name|time
expr_stmt|;
name|rewriteScratch
operator|=
literal|0
expr_stmt|;
return|return
name|time
return|;
block|}
DECL|method|getRewriteTime
specifier|public
name|long
name|getRewriteTime
parameter_list|()
block|{
return|return
name|rewriteTime
return|;
block|}
block|}
end_class

end_unit

