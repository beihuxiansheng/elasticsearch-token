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
comment|/**  *  */
end_comment

begin_class
DECL|class|SpanMultiTermQueryBuilder
specifier|public
class|class
name|SpanMultiTermQueryBuilder
extends|extends
name|BaseQueryBuilder
implements|implements
name|SpanQueryBuilder
block|{
DECL|field|multiTermQueryBuilder
specifier|private
name|MultiTermQueryBuilder
name|multiTermQueryBuilder
decl_stmt|;
DECL|method|SpanMultiTermQueryBuilder
specifier|public
name|SpanMultiTermQueryBuilder
parameter_list|(
name|MultiTermQueryBuilder
name|multiTermQueryBuilder
parameter_list|)
block|{
name|this
operator|.
name|multiTermQueryBuilder
operator|=
name|multiTermQueryBuilder
expr_stmt|;
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
name|builder
operator|.
name|startObject
argument_list|(
name|SpanMultiTermQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|SpanMultiTermQueryParser
operator|.
name|MATCH_NAME
argument_list|)
expr_stmt|;
name|multiTermQueryBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parserName
specifier|protected
name|String
name|parserName
parameter_list|()
block|{
return|return
name|SpanMultiTermQueryParser
operator|.
name|NAME
return|;
block|}
block|}
end_class

end_unit

