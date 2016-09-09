begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteable
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
name|plugins
operator|.
name|SearchPlugin
import|;
end_import

begin_comment
comment|/**  * Intermediate serializable representation of a search ext section. To be subclassed by plugins that support  * a custom section as part of a search request, which will be provided within the ext element.  * Any state needs to be serialized as part of the {@link org.elasticsearch.common.io.stream.Writeable#writeTo(StreamOutput)} method and  * read from the incoming stream, usually done adding a constructor that takes {@link org.elasticsearch.common.io.stream.StreamInput} as  * an argument.  *  * Registration happens through {@link SearchPlugin#getSearchExts()}, which also needs a {@link SearchExtParser} that's able to parse  * the incoming request from the REST layer into the proper {@link SearchExtBuilder} subclass.  *  * {@link #getWriteableName()} must return the same name as the one used for the registration  * of the {@link org.elasticsearch.plugins.SearchPlugin.SearchExtSpec}.  *  * @see SearchExtParser  * @see org.elasticsearch.plugins.SearchPlugin.SearchExtSpec  */
end_comment

begin_class
DECL|class|SearchExtBuilder
specifier|public
specifier|abstract
class|class
name|SearchExtBuilder
implements|implements
name|NamedWriteable
implements|,
name|ToXContent
block|{
DECL|method|hashCode
specifier|public
specifier|abstract
name|int
name|hashCode
parameter_list|()
function_decl|;
DECL|method|equals
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
function_decl|;
block|}
end_class

end_unit

