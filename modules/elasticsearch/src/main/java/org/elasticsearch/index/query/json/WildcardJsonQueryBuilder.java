begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|json
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
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
comment|/**  * Implements the wildcard search query. Supported wildcards are<tt>*</tt>, which  * matches any character sequence (including the empty one), and<tt>?</tt>,  * which matches any single character. Note this query can be slow, as it  * needs to iterate over many terms. In order to prevent extremely slow WildcardQueries,  * a Wildcard term should not start with one of the wildcards<tt>*</tt> or  *<tt>?</tt>.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|WildcardJsonQueryBuilder
specifier|public
class|class
name|WildcardJsonQueryBuilder
extends|extends
name|BaseJsonQueryBuilder
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|wildcard
specifier|private
specifier|final
name|String
name|wildcard
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Implements the wildcard search query. Supported wildcards are<tt>*</tt>, which      * matches any character sequence (including the empty one), and<tt>?</tt>,      * which matches any single character. Note this query can be slow, as it      * needs to iterate over many terms. In order to prevent extremely slow WildcardQueries,      * a Wildcard term should not start with one of the wildcards<tt>*</tt> or      *<tt>?</tt>.      *      * @param name     The field name      * @param wildcard The wildcard query string      */
DECL|method|WildcardJsonQueryBuilder
specifier|public
name|WildcardJsonQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|wildcard
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|wildcard
operator|=
name|wildcard
expr_stmt|;
block|}
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
specifier|public
name|WildcardJsonQueryBuilder
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|doJson
annotation|@
name|Override
specifier|public
name|void
name|doJson
parameter_list|(
name|JsonBuilder
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
name|WildcardJsonQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|boost
operator|==
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|wildcard
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"wildcard"
argument_list|,
name|wildcard
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

