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

begin_comment
comment|/**  * A filter for a field based on several terms matching on any of them.  * @deprecated use {@link TermsQueryBuilder#TermsQueryBuilder(name, lookupIndex, lookupType, lookupId)} instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|TermsLookupQueryBuilder
specifier|public
class|class
name|TermsLookupQueryBuilder
extends|extends
name|TermsQueryBuilder
block|{
DECL|method|TermsLookupQueryBuilder
specifier|public
name|TermsLookupQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|queryId
specifier|public
name|String
name|queryId
parameter_list|()
block|{
return|return
name|TermsQueryBuilder
operator|.
name|NAME
return|;
block|}
block|}
end_class

end_unit

