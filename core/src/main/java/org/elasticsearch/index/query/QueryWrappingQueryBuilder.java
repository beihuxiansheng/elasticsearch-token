begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
comment|/**  * QueryBuilder implementation that  holds a lucene query, which can be returned by {@link QueryBuilder#toQuery(QueryShardContext)}.  * Doesn't support conversion to {@link org.elasticsearch.common.xcontent.XContent} via {@link #doXContent(XContentBuilder, Params)}.  */
end_comment

begin_comment
comment|//norelease to be removed once all queries support separate fromXContent and toQuery methods. Make AbstractQueryBuilder#toQuery final as well then.
end_comment

begin_class
DECL|class|QueryWrappingQueryBuilder
specifier|public
class|class
name|QueryWrappingQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|QueryWrappingQueryBuilder
argument_list|>
implements|implements
name|SpanQueryBuilder
argument_list|<
name|QueryWrappingQueryBuilder
argument_list|>
implements|,
name|MultiTermQueryBuilder
argument_list|<
name|QueryWrappingQueryBuilder
argument_list|>
block|{
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|method|QueryWrappingQueryBuilder
specifier|public
name|QueryWrappingQueryBuilder
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
comment|//hack to make sure that the boost from the wrapped query is used, otherwise it gets overwritten.
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|boost
operator|=
name|query
operator|.
name|getBoost
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
comment|// this should not be called since we overwrite BaseQueryBuilder#toQuery() in this class
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

