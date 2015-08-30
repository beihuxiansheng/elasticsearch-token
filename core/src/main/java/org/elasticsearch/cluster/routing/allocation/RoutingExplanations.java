begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|ToXContent
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Class used to encapsulate a number of {@link RerouteExplanation}  * explanations.  */
end_comment

begin_class
DECL|class|RoutingExplanations
specifier|public
class|class
name|RoutingExplanations
implements|implements
name|ToXContent
block|{
DECL|field|explanations
specifier|private
specifier|final
name|List
argument_list|<
name|RerouteExplanation
argument_list|>
name|explanations
decl_stmt|;
DECL|method|RoutingExplanations
specifier|public
name|RoutingExplanations
parameter_list|()
block|{
name|this
operator|.
name|explanations
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|RoutingExplanations
name|add
parameter_list|(
name|RerouteExplanation
name|explanation
parameter_list|)
block|{
name|this
operator|.
name|explanations
operator|.
name|add
argument_list|(
name|explanation
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|explanations
specifier|public
name|List
argument_list|<
name|RerouteExplanation
argument_list|>
name|explanations
parameter_list|()
block|{
return|return
name|this
operator|.
name|explanations
return|;
block|}
comment|/**      * Read in a RoutingExplanations object      */
DECL|method|readFrom
specifier|public
specifier|static
name|RoutingExplanations
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|exCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|RoutingExplanations
name|exp
init|=
operator|new
name|RoutingExplanations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exCount
condition|;
name|i
operator|++
control|)
block|{
name|RerouteExplanation
name|explanation
init|=
name|RerouteExplanation
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|exp
operator|.
name|add
argument_list|(
name|explanation
argument_list|)
expr_stmt|;
block|}
return|return
name|exp
return|;
block|}
comment|/**      * Write the RoutingExplanations object      */
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|RoutingExplanations
name|explanations
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|explanations
operator|.
name|explanations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RerouteExplanation
name|explanation
range|:
name|explanations
operator|.
name|explanations
control|)
block|{
name|RerouteExplanation
operator|.
name|writeTo
argument_list|(
name|explanation
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
name|startArray
argument_list|(
literal|"explanations"
argument_list|)
expr_stmt|;
for|for
control|(
name|RerouteExplanation
name|explanation
range|:
name|explanations
control|)
block|{
name|explanation
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

