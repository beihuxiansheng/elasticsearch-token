begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|StreamInput
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
comment|/**  * Value Serializer for named diffables  */
end_comment

begin_class
DECL|class|NamedDiffableValueSerializer
specifier|public
class|class
name|NamedDiffableValueSerializer
parameter_list|<
name|T
extends|extends
name|NamedDiffable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|DiffableUtils
operator|.
name|DiffableValueSerializer
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
block|{
DECL|field|tClass
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|tClass
decl_stmt|;
DECL|method|NamedDiffableValueSerializer
specifier|public
name|NamedDiffableValueSerializer
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|tClass
parameter_list|)
block|{
name|this
operator|.
name|tClass
operator|=
name|tClass
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|T
name|read
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|tClass
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|supportsVersion
specifier|public
name|boolean
name|supportsVersion
parameter_list|(
name|Diff
argument_list|<
name|T
argument_list|>
name|value
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
return|return
name|version
operator|.
name|onOrAfter
argument_list|(
operator|(
operator|(
name|NamedDiff
argument_list|<
name|?
argument_list|>
operator|)
name|value
operator|)
operator|.
name|getMinimalSupportedVersion
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|supportsVersion
specifier|public
name|boolean
name|supportsVersion
parameter_list|(
name|T
name|value
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
return|return
name|version
operator|.
name|onOrAfter
argument_list|(
name|value
operator|.
name|getMinimalSupportedVersion
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|readDiff
specifier|public
name|Diff
argument_list|<
name|T
argument_list|>
name|readDiff
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|NamedDiff
operator|.
name|class
argument_list|,
name|key
argument_list|)
return|;
block|}
block|}
end_class

end_unit

